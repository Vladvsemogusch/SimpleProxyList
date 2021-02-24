package cc.anisimov.vlad.simpleproxylist.data.source.remote

import cc.anisimov.vlad.simpleproxylist.data.model.ProxyInfo
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ProxyApi {

    @GET("{region}/{id}")
    suspend fun getProxyInfo(
        @Query("region") region: String,
        @Query("id") id: Int
    ): Response<ProxyInfo>

}