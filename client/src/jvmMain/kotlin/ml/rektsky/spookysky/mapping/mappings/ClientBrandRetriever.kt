package ml.rektsky.spookysky.mapping.mappings

import ml.rektsky.spookysky.mapping.ClassMapping
import ml.rektsky.spookysky.mapping.MethodMapping

object ClientBrandRetriever: ClassMapping("ClientBrandRetriever") {

    val mapGetClientModeName = MethodMapping(Minecraft,"getClientModName")

    fun getClientModName(): String {
        return (mapGetClientModeName.getReflectiveMethod()?.invoke(null) as String?)?:"Unknown";
    }

}

