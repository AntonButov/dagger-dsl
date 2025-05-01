package processor.testutils

import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtNamedFunction
import readers.PsiFileCreatorImpl

/**
 * Represents a method/function found in Kotlin code
 */
data class Method(
    val name: String,
    val genericTypes: List<String> = emptyList(),
    val lambdaMethods: List<Method> = emptyList(),
)

/**
 * Finds all functions in the Kotlin code and returns them as Method objects
 *
 * @return List of Method objects representing the functions in the code
 */
fun String.findFunctions(): List<Method> {
    return this.declarations()
        .filterIsInstance<KtNamedFunction>()
        .map { it.toMethod() }
}

/**
 * Parses Kotlin code and returns the list of declarations
 */
private fun String.declarations(): List<KtDeclaration> {
    val psiFileCreator = PsiFileCreatorImpl()
    val psiFile = psiFileCreator.createKtFile(this)
    return psiFile.declarations
}

/**
 * Converts a KtNamedFunction to a Method object
 */
private fun KtNamedFunction.toMethod(): Method {
    val bodyExpression =
        bodyExpression as? KtBlockExpression ?: return Method(
            name = nameIdentifier?.text ?: "",
        )

    val lambdaMethods = bodyExpression.findAllCallExpressions().map { it.toMethod() }

    return Method(
        name = nameIdentifier?.text ?: "",
        lambdaMethods = lambdaMethods,
    )
}

/**
 * Converts a KtCallExpression to a Method object
 */
private fun KtCallExpression.toMethod(): Method {
    val typeArgList = typeArgumentList
    val genericTypes =
        typeArgList?.arguments?.mapNotNull {
            it.typeReference?.text
        } ?: emptyList()

    val lambdaExpr = lambdaArguments.firstOrNull()?.getLambdaExpression()
    val lambdaBody = lambdaExpr?.bodyExpression

    val nestedMethods =
        lambdaBody?.findAllCallExpressions()?.map {
            it.toMethod()
        } ?: emptyList()

    return Method(
        name = calleeExpression?.text ?: error("No name method."),
        genericTypes = genericTypes,
        lambdaMethods = nestedMethods,
    )
}

/**
 * Recursively finds all call expressions within a block expression
 */
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
