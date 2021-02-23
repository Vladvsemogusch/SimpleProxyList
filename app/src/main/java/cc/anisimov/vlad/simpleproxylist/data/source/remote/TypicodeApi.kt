package cc.anisimov.vlad.simpleproxylist.data.source.remote

import cc.anisimov.vlad.simpleproxylist.data.model.Album
import cc.anisimov.vlad.simpleproxylist.data.model.Photo
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface TypicodeApi {

    @GET("albums")
    suspend fun getAlbumsByUserId(@Query("userId") userId: Int): Response<List<Album>>

    @GET("photos")
    suspend fun getPhotosByAlbumId(
        @Query("albumId") albumId: Int,
        @Query("_limit") limit: Int? = null
    ): Response<List<Photo>>

}