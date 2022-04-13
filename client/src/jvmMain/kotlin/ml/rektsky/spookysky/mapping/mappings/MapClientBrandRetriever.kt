package ml.rektsky.spookysky.mapping.mappings

import ml.rektsky.spookysky.mapping.ClassMapping
import ml.rektsky.spookysky.mapping.MethodMapping

object MapClientBrandRetriever: ClassMapping("ClientBrandRetriever") {

    val mapGetClientModeName = MethodMapping(MapMinecraft,"getClientModName")

    fun getClientModName(): String {
        return (mapGetClientModeName.getReflectiveMethod()?.invoke(null) as String?)?:"Unknown";
    }

}

