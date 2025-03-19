package usescases

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
        classFile: File,
        className: String,
        methodName: String,
    ): Any = runMethod(classFile, className, methodName, false)

    override fun runStaticMethod(
        clazzFile: File,
        className: String,
        methodName: String,
    ): Any = runMethod(clazzFile, className, methodName, true)

    private fun runMethod(
        classFile: File,
        className: String,
        methodName: String,
        isStatic: Boolean,
    ): Any {
        val stdLib = File(System.getProperty("user.dir"), "build/libs/kotlin-stdlib-1.9.25.jar")
        val core = File(System.getProperty("user.dir"), "dagger-dsl-core.jar")
        val classLoader =
            URLClassLoader(
                arrayOf(
                    classFile.toURI().toURL(),
                    stdLib.toURI().toURL(),
                    core.toURI().toURL(),
                ),
                java.lang.ClassLoader.getSystemClassLoader(),
            )

        val clazz = classLoader.loadClass(className)
        val instance = isStatic.takeIf { it.not() }?.let { clazz.getDeclaredConstructor().newInstance() }

        val method = clazz.getMethod(methodName)
        val result = method.invoke(instance)

        classLoader.close()

        return result
    }
}
