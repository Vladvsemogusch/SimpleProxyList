package cc.anisimov.vlad.simpleproxylist.data.repository

import cc.anisimov.vlad.simpleproxylist.data.model.RequestResult
import cc.anisimov.vlad.simpleproxylist.data.source.remote.TypicodeApi
import cc.anisimov.vlad.simpleproxylist.domain.model.PhotoUI
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PhotoRepo @Inject constructor(
    private val remoteApi: TypicodeApi
) {

    suspend fun getAlbumThumbnailUrls(albumId: Int): RequestResult<List<String>> = withContext(IO) {
        val result =
            kotlin.runCatching { remoteApi.getPhotosByAlbumId(albumId, 4) }
        if (result.isFailure) {
            return@withContext RequestResult.Error(result.exceptionOrNull())
        }
        //  List of first 4 thumbnail urls
        val urlList = result.getOrNull()!!.body()!!.map { it.thumbnailUrl }
        RequestResult.Success(urlList)
    }

    suspend fun getPhotosByAlbumId(albumId: Int): RequestResult<List<PhotoUI>> = withContext(IO) {
        val result =
            kotlin.runCatching { remoteApi.getPhotosByAlbumId(albumId) }
        if (result.isFailure) {
            return@withContext RequestResult.Error(result.exceptionOrNull())
        }
        val photos = result.getOrNull()!!.body()!!
        val photosUI = photos.map { PhotoUI(it.id, it.thumbnailUrl, it.title, it.url) }
        RequestResult.Success(photosUI)
    }
}