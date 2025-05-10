package psiUtils

import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtProperty

data class Method(
    val name: String,
    val genericTypes: List<String> = emptyList(),
    val lambdaMethods: List<Method> = emptyList(),
    val lambdaBody: String = "",
) {
    companion object {
        fun fromKtNamedFunction(function: KtNamedFunction): Method {
            val body = function.bodyExpression
            return when (body) {
                is KtBlockExpression -> {
                    val lambdaMethods = body.findAllCallExpressions().map { fromKtCallExpression(it) }
                    Method(
                        name = function.nameIdentifier?.text ?: "",
                        lambdaMethods = lambdaMethods,
                        lambdaBody = body.text,
                    )
                }
                is KtCallExpression -> {
                    Method(
                        name = function.nameIdentifier?.text ?: "",
                        lambdaMethods = listOf(fromKtCallExpression(body)),
                        lambdaBody = body.text,
                    )
                }
                else ->
                    Method( // todo узнать когда работает
                        name = function.nameIdentifier?.text ?: "",
                    )
            }
        }

        fun fromKtCallExpression(callExpression: KtCallExpression): Method {
            val typeArgList = callExpression.typeArgumentList
            val genericTypes =
                typeArgList?.arguments?.mapNotNull {
                    it.typeReference?.text
                } ?: emptyList()

            val lambdaExpr = callExpression.lambdaArguments.firstOrNull()?.getLambdaExpression()
            val lambdaBody = lambdaExpr?.bodyExpression

            val nestedMethods =
                lambdaBody?.findAllCallExpressions()?.map {
                    fromKtCallExpression(it)
                } ?: emptyList()

            return Method(
                name = callExpression.calleeExpression?.text ?: error("No name method."),
                genericTypes = genericTypes,
                lambdaMethods = nestedMethods,
                lambdaBody = lambdaExpr?.text ?: "",
            )
        }
    }
}

fun String.toMethods(): List<Method> {
    return this.toDeclarations()
        .mapNotNull {
            when (it) {
                is KtProperty -> {
                    val initializerExpr = it.initializer ?: error("Body not initialized")
                    val callExpr =
                        initializerExpr as? KtCallExpression
                            ?: error("Property ${it.name} initializer is not a call expression: ${initializerExpr::class.simpleName}")
                    Method(
                        name = it.name.toString(),
                        lambdaMethods = listOf(Method.fromKtCallExpression(callExpr)),
                    )
                }
                is KtNamedFunction -> {
                    Method.fromKtNamedFunction(it)
                }
                else -> null
            }
        }
}

fun String.toDeclarations(): List<KtDeclaration> {
    val psiFileCreator = PsiFileCreatorImpl()
    val psiFile = psiFileCreator.createKtFile(this)
    return psiFile.declarations
}

private fun KtBlockExpression.findAllCallExpressions(): List<KtCallExpression> {
    val result = mutableListOf<KtCallExpression>()

    this.statements.forEach { statement ->
        if (statement is KtCallExpression) {
            result.add(statement)
        }

        if (statement is KtBlockExpression) {
            result.addAll(statement.findAllCallExpressions())
        }
    }

    return result
}
