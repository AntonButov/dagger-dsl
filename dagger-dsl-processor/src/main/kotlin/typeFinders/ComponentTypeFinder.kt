package typeFinders

import com.google.devtools.ksp.getDeclaredFunctions
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.ClassKind
import models.ComponentType
import models.ComponentTypeMethod
import models.Param
import models.Type
import usecases.getClassKind

interface ComponentTypeFinder {
    fun findByName(
        resolver: Resolver,
        name: String,
    ): ComponentType
}

class ComponentTypeFinderImpl : ComponentTypeFinder {
    override fun findByName(
        resolver: Resolver,
        name: String,
    ): ComponentType {
        val symbol = resolver.getClassKind(ClassKind.INTERFACE).first { it.simpleName.asString() == name }
        val functionSymbols = symbol.getDeclaredFunctions()

        val methods =
            functionSymbols.map { functionSymbol ->
                val declaration = functionSymbol.returnType?.resolve()?.declaration ?: error("Return type not found")
                val returnTypeName = declaration.simpleName.asString()
                val packageName = declaration.packageName.asString()
                val parameters =
                    functionSymbol.parameters.map {
                        val declaration = it.type.resolve().declaration
                        val name = declaration.simpleName.asString()
                        val packageName = declaration.packageName.asString()
                        Param(
                            it.name!!.asString(),
                            Type(name, packageName),
                        )
                    }
                ComponentTypeMethod(
                    name = functionSymbol.simpleName.asString(),
                    params = parameters,
                    returnType = Type(returnTypeName, packageName),
                )
            }

        return ComponentType(name = symbol.simpleName.asString(), methods = methods.toList())
    }
}
