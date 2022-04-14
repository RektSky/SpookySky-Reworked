package ml.rektsky.spookysky.processor.impl.mapper

import ml.rektsky.spookysky.mapping.mappings.lwjgl.Keyboard
import ml.rektsky.spookysky.mapping.mappings.lwjgl.Mouse
import ml.rektsky.spookysky.processor.LoadedClass
import ml.rektsky.spookysky.processor.Processor

class MapperLwjglClasses: Processor() {

    override fun process0(loadedClass: LoadedClass) {
        if (loadedClass.classNode.name == "org/lwjgl/input/Keyboard") {
            Keyboard.mapped = loadedClass
        } else if (loadedClass.classNode.name == "org/lwjgl/input/Mouse") {
            Mouse.mapped = loadedClass
        }
    }

    override fun jobDone(): Boolean {
        return Keyboard.isMapped() && Mouse.isMapped()
    }


}