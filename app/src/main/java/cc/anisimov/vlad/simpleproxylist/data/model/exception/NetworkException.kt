package cc.anisimov.vlad.simpleproxylist.data.model.exception

data class NetworkException(val responseCode: Int): Exception(responseCode.toString())