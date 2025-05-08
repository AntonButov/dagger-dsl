package usecases

import psiUtils.Method
import psiUtils.findFunctions
import java.io.File

interface ComponentMethodFinder {
    fun mapComponent(dslFun: DaggerDslFunction): Method
}

class ComponentMethodFinderImpl() : ComponentMethodFinder {
    override fun mapComponent(dslFun: DaggerDslFunction): Method {
        val code = File(dslFun.containingFile.filePath).readText()
        return code
            .findFunctions()
            .first { it.name == dslFun.nameFun }
            .lambdaMethods
            .firstOrNull() ?: error("Component not found")
    }
}
