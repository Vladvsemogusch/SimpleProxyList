package cc.anisimov.vlad.simpleproxylist.data.model



data class ProxyInfo(
    val crash: Boolean,
    val hello: String,
    val id: Int,
    val ping: Int,
    val refs: List<String>,
    val region: String,
    val timeOut: Boolean
)