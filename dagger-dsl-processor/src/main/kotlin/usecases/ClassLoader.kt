package usecases

import processor.Processor
import java.io.File
import java.net.URLClassLoader

interface ClassLoader {
    fun runMethod(
        clazzFile: File,
        className: String,
        methodName: String,
    ): Any

    fun runStaticMethod(
        clazzFile: File,
        className: String,
        methodName: String,
    ): Any
}

class ClassLoaderImpl : ClassLoader {
    override fun runMethod(
        jarFile: File,
        className: String,
        methodName: String,
    ): Any = runMethod(jarFile, className, methodName, false)

    override fun runStaticMethod(
        jarFile: File,
        className: String,
        methodName: String,
    ): Any = runMethod(jarFile, className, methodName, true)

    private fun runMethod(
        classFile: File,
        className: String,
        methodName: String,
        isStatic: Boolean,
    ): Any {
        val classLoader =
            URLClassLoader(
                arrayOf(
                    classFile.toURI().toURL(),
                ),
                Processor::class.java.classLoader,
            )

        val clazz = classLoader.loadClass(className)
        val instance = isStatic.takeIf { it.not() }?.let { clazz.getDeclaredConstructor().newInstance() }

        val method = clazz.getMethod(methodName)
        val result = method.invoke(instance)

        //   classLoader.close()

        return result
    }
}
