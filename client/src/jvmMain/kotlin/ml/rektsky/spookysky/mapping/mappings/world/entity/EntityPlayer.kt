package ml.rektsky.spookysky.mapping.mappings.world.entity

import ml.rektsky.spookysky.mapping.ClassMapping


object MapEntityPlayer : ClassMapping("EntityPlayer") {


}


open class EntityPlayer(original: Any): EntityLivingBase(original) {

}
