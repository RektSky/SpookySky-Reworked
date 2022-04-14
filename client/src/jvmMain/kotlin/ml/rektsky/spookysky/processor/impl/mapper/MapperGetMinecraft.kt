package ml.rektsky.spookysky.processor.impl.mapper

import ml.rektsky.spookysky.mapping.mappings.MapMinecraft
import ml.rektsky.spookysky.processor.LoadedClass
import ml.rektsky.spookysky.processor.Processor
import java.lang.reflect.Modifier

class MapperGetMinecraft: Processor() {

    init {
        dependsOn(MapMinecraft)
    }


    override fun shouldProcess(loadedClass: LoadedClass): Boolean {
        return loadedClass == MapMinecraft.mapped
    }

    override fun process0(loadedClass: LoadedClass) {
        val methodNode = loadedClass.classNode.methods.firstOrNull {
            Modifier.isStatic(it.access) &&
                    Modifier.isPublic(it.access) &&
                    it.desc == "()L${MapMinecraft.mapped!!.classNode.name};"
        } ?: return
        MapMinecraft.mapGetMinecraft.mapped = methodNode
    }

    override fun jobDone(): Boolean {
        return MapMinecraft.mapGetMinecraft.isMapped()
    }
}