package ml.rektsky.spookysky.mapping.mappings

import ml.rektsky.spookysky.mapping.ClassMapping
import ml.rektsky.spookysky.mapping.MethodMapping

object MapClientBrandRetriever: ClassMapping("ClientBrandRetriever") {

    fun getClientModName(): String {
        return (MapGetClientModName.getReflectiveMethod()?.invoke(null) as String?)?:"Unknown";
    }

}


object MapGetClientModName: MethodMapping(MapClientBrandRetriever, "getClientModName") {

}