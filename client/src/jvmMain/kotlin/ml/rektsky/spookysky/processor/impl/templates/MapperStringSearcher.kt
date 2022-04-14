package ml.rektsky.spookysky.processor.impl.templates

import ml.rektsky.spookysky.mapping.ClassMapping
import ml.rektsky.spookysky.processor.LoadedClass
import ml.rektsky.spookysky.processor.Processor
import ml.rektsky.spookysky.utils.MappingUtils

abstract class MapperStringSearcher(val text: String, val mapping: ClassMapping): Processor() {


    override fun process0(loadedClass: LoadedClass) {
        if (MappingUtils.hasString(text, loadedClass.classNode)) {
            mapping.mapped = loadedClass
            return
        }
    }

    override fun jobDone(): Boolean {
        return mapping.isMapped()
    }
}