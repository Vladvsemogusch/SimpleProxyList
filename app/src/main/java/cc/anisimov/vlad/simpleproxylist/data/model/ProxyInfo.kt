package cc.anisimov.vlad.simpleproxylist.data.model


import com.google.gson.annotations.SerializedName

data class ProxyInfo(
    val crash: Boolean,
    val hello: String,
    val id: Int,
    val ping: Int,
    val refs: List<String>,
    val region: String,
    val timeOut: Boolean
)