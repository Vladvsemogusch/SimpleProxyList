package cc.anisimov.vlad.simpleproxylist.data.model


import com.google.gson.annotations.SerializedName

data class RawDefaultProxies(
    val fr: List<String>,
    val ru: List<String>,
    val ua: List<String>,
    val us: List<String>
)