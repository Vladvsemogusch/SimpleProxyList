package cc.anisimov.vlad.simpleproxylist.data.source.remote

import cc.anisimov.vlad.simpleproxylist.data.model.ProxyInfo
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ProxyApi {

    @GET("{region}/{id}")
    suspend fun getProxyInfo(
        @Path("region") region: String,
        @Path("id") id: Int
    ): Response<ProxyInfo>

}