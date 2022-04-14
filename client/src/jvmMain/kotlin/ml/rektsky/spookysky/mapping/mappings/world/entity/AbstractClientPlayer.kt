package ml.rektsky.spookysky.mapping.mappings.world.entity

import ml.rektsky.spookysky.mapping.ClassMapping


object MapAbstractClientPlayer : ClassMapping("AbstractClientPlayer") {


}


open class AbstractClientPlayer(original: Any): EntityPlayer(original) {

}
