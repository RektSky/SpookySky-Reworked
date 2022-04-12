package ml.rektsky.spookysky.modules

import ml.rektsky.spookysky.module.Category


abstract class Module(
    val name: String,
    val description: String,
    val category: Category,
) {

    var toggled = false
        set(value) {
            field = value
            if (value) onEnable() else onDisable()
        }

    protected abstract fun onDisable()
    protected abstract fun onEnable()

}


