package ml.rektsky.spookysky.modules

import org.apache.logging.log4j.core.config.plugins.util.ResolverUtil

object ModulesManager {

    private val modules = ArrayList<Module>()

    init {
        val resolverUtil = ResolverUtil()
        resolverUtil.classLoader = this.javaClass.classLoader
        for (clazz in resolverUtil.classes) {
            if (clazz.superclass == Module::class.java) {
                modules.add((clazz as Class<*>).newInstance() as Module)
            }
        }
    }

    fun getRegisteredModules(): List<Module> = ArrayList(modules)

}