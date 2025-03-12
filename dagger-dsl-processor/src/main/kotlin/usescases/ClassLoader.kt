package usescases

import java.io.File
import java.net.URLClassLoader

interface ClassLoader {
    fun runMethod(
        clazzFile: File,
        className: String,
        methodName: String,
    ): Any
}

class ClassLoaderImpl : ClassLoader {
    override fun runMethod(
        classFile: File,
        className: String,
        methodName: String,
    ): Any {
        val classLoader = URLClassLoader(arrayOf(classFile.toURI().toURL()), null)

        val clazz = classLoader.loadClass(className)
        val instance = clazz.getDeclaredConstructor().newInstance()

        val method = clazz.getMethod(methodName)
        val result = method.invoke(instance)

        classLoader.close()

        return result
    }
}
