package ml.rektsky.spookysky.mapping.mappings.settings

import ml.rektsky.spookysky.mapping.ClassMapping
import ml.rektsky.spookysky.mapping.FieldMapping

object MapGameSettings: ClassMapping("GameSettings") {

    val mapKeyBindForward: FieldMapping = FieldMapping(this, "key.forward")
    val mapKeyBindLeft: FieldMapping = FieldMapping(this, "key.left")
    val mapKeyBindBack: FieldMapping = FieldMapping(this, "key.back")
    val mapKeyBindRight: FieldMapping = FieldMapping(this, "key.right")
    val mapKeyBindJump: FieldMapping = FieldMapping(this, "key.jump")
    val mapKeyBindSneak: FieldMapping = FieldMapping(this, "key.sneak")
    val mapKeyBindSprint: FieldMapping = FieldMapping(this, "key.sprint")
    val mapKeyBindInventory: FieldMapping = FieldMapping(this, "key.inventory")
    val mapKeyBindUseItem: FieldMapping = FieldMapping(this, "key.use")
    val mapKeyBindDrop: FieldMapping = FieldMapping(this, "key.drop")
    val mapKeyBindAttack: FieldMapping = FieldMapping(this, "key.attack")
    val mapKeyBindPickBlock: FieldMapping = FieldMapping(this, "key.pickItem")
    val mapKeyBindChat: FieldMapping = FieldMapping(this, "key.chat")
    val mapKeyBindPlayerList: FieldMapping = FieldMapping(this, "key.playerlist")
    val mapKeyBindCommand: FieldMapping = FieldMapping(this, "key.command")
    val mapKeyBindScreenshot: FieldMapping = FieldMapping(this, "key.screenshot")
    val mapKeyBindTogglePerspective: FieldMapping = FieldMapping(this, "key.togglePerspective")
    val mapKeyBindSmoothCamera: FieldMapping = FieldMapping(this, "key.smoothCamera")
    val mapKeyBindFullscreen: FieldMapping = FieldMapping(this, "key.fullscreen")
    val mapKeyBindSpectatorOutlines: FieldMapping = FieldMapping(this, "key.spectatorOutlines")
    val mapKeyBindStreamStartStop: FieldMapping = FieldMapping(this, "key.streamStartStop")
    val mapKeyBindStreamPauseUnpause: FieldMapping = FieldMapping(this, "key.streamPauseUnpause")
    val mapKeyBindStreamCommercials: FieldMapping = FieldMapping(this, "key.streamCommercial")
    val mapKeyBindStreamToggleMic: FieldMapping = FieldMapping(this, "key.streamToggleMic")
    
}

class GameSettings(val original: Any) {
    val keyBindForward: KeyBinding? = getKeyBindingWrapper("key.forward")
    val keyBindLeft: KeyBinding? = getKeyBindingWrapper("key.left")
    val keyBindBack: KeyBinding? = getKeyBindingWrapper("key.back")
    val keyBindRight: KeyBinding? = getKeyBindingWrapper("key.right")
    val keyBindJump: KeyBinding? = getKeyBindingWrapper("key.jump")
    val keyBindSneak: KeyBinding? = getKeyBindingWrapper("key.sneak")
    val keyBindSprint: KeyBinding? = getKeyBindingWrapper("key.sprint")
    val keyBindInventory: KeyBinding? = getKeyBindingWrapper("key.inventory")
    val keyBindUseItem: KeyBinding? = getKeyBindingWrapper("key.use")
    val keyBindDrop: KeyBinding? = getKeyBindingWrapper("key.drop")
    val keyBindAttack: KeyBinding? = getKeyBindingWrapper("key.attack")
    val keyBindPickBlock: KeyBinding? = getKeyBindingWrapper("key.pickItem")
    val keyBindChat: KeyBinding? = getKeyBindingWrapper("key.chat")
    val keyBindPlayerList: KeyBinding? = getKeyBindingWrapper("key.playerlist")
    val keyBindCommand: KeyBinding? = getKeyBindingWrapper("key.command")
    val keyBindScreenshot: KeyBinding? = getKeyBindingWrapper("key.screenshot")
    val keyBindTogglePerspective: KeyBinding? = getKeyBindingWrapper("key.togglePerspective")
    val keyBindSmoothCamera: KeyBinding? = getKeyBindingWrapper("key.smoothCamera")
    val keyBindFullscreen: KeyBinding? = getKeyBindingWrapper("key.fullscreen")
    val keyBindSpectatorOutlines: KeyBinding? = getKeyBindingWrapper("key.spectatorOutlines")
    val keyBindStreamStartStop: KeyBinding? = getKeyBindingWrapper("key.streamStartStop")
    val keyBindStreamPauseUnpause: KeyBinding? = getKeyBindingWrapper("key.streamPauseUnpause")
    val keyBindStreamCommercials: KeyBinding? = getKeyBindingWrapper("key.streamCommercial")
    val keyBindStreamToggleMic: KeyBinding? = getKeyBindingWrapper("key.streamToggleMic")


    private fun getKeyBindingWrapper(name: String): KeyBinding? {
        for (child in MapGameSettings.children) {
            if (child is FieldMapping) {
                if (child.name == name) {
                    if (child.getReflectiveField()?.get(original) == null) return null
                    return KeyBinding(child.getReflectiveField()?.get(original)!!)
                }
            }
        }
        return null
    }
}