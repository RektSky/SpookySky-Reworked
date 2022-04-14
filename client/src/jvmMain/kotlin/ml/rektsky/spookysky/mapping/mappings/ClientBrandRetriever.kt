package ml.rektsky.spookysky.mapping.mappings

import ml.rektsky.spookysky.mapping.ClassMapping
import ml.rektsky.spookysky.mapping.MethodMapping

object MapClientBrandRetriever: ClassMapping("ClientBrandRetriever") {

    val mapGetClientModeName = MethodMapping(MapMinecraft,"getClientModName")



}

object ClientBrandRetriever {
    fun getClientModName(): String {
        return (MapClientBrandRetriever.mapGetClientModeName.getReflectiveMethod()?.invoke(null) as String?)?:"Unknown";
    }
}