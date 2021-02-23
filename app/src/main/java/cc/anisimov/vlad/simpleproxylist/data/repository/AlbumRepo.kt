package cc.anisimov.vlad.simpleproxylist.data.repository

import cc.anisimov.vlad.simpleproxylist.data.model.RequestResult
import cc.anisimov.vlad.simpleproxylist.data.source.remote.TypicodeApi
import cc.anisimov.vlad.simpleproxylist.domain.model.AlbumUI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlbumRepo @Inject constructor(
    private val remoteApi: TypicodeApi,
    private val photoRepo: PhotoRepo
) {

    suspend fun getAlbumsByUserId(userId: Int): RequestResult<List<AlbumUI>> = withContext(
        Dispatchers.IO
    ) {
        val result = kotlin.runCatching { remoteApi.getAlbumsByUserId(userId) }
        if (result.isFailure) {
            return@withContext RequestResult.Error(result.exceptionOrNull())
        }
        val albumList = result.getOrNull()!!.body()!!
        val albumUIList: MutableList<AlbumUI> = ArrayList()
        for (album in albumList) {
            val thumbnailResult = photoRepo.getAlbumThumbnailUrls(album.id)
            if (thumbnailResult is RequestResult.Error) {
                return@withContext thumbnailResult
            }
            val thumbnailURLList = (thumbnailResult as RequestResult.Success).data
            albumUIList.add(AlbumUI(album.id, album.title, thumbnailURLList))
        }
        return@withContext RequestResult.Success(albumUIList)
    }
}