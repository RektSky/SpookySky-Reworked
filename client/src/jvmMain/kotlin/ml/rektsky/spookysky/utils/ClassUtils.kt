package ml.rektsky.spookysky.utils

import org.apache.logging.log4j.core.config.plugins.util.ResolverUtil
import java.lang.reflect.Modifier
import java.net.URI

object ClassUtils {

    fun <T : Any> resolvePackage(packagePath: String, superClass: Class<T>): MutableSet<Class<T>> {
        // use resolver in log4j to scan classes in target package
        val resolver = ResolverUtil()

        // set class loader
        resolver.classLoader = superClass.classLoader

        // set package to scan
        resolver.findInPackage(object : ResolverUtil.Test {
            override fun matches(type: Class<*>): Boolean {
                return superClass.isAssignableFrom(type) && !type.isInterface && !Modifier.isAbstract(type.modifiers)
            }

            override fun matches(resource: URI?): Boolean {
                return false
            }

            override fun doesMatchClass(): Boolean {
                return true
            }

            override fun doesMatchResource(): Boolean {
                return false
            }
        }, packagePath)

        return resolver.classes as MutableSet<Class<T>>
    }

}