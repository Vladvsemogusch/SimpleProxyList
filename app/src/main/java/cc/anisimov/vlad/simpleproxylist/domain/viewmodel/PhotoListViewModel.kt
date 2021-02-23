package cc.anisimov.vlad.simpleproxylist.domain.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cc.anisimov.vlad.simpleproxylist.data.model.RequestResult
import cc.anisimov.vlad.simpleproxylist.data.repository.PhotoRepo
import cc.anisimov.vlad.simpleproxylist.domain.model.PhotoUI
import kotlinx.coroutines.launch

class PhotoListViewModel @ViewModelInject constructor(private val photoRepo: PhotoRepo) :
    ViewModel() {

    private var albumId: Int = -1
    val oPhotoList: MutableLiveData<List<PhotoUI>> = MutableLiveData(ArrayList())
    val oLoading = MutableLiveData(false)
    val oError: MutableLiveData<String?> = MutableLiveData(null)

    fun start(albumId: Int) {
        this.albumId = albumId
        loadPhotos(albumId)
    }

    private fun loadPhotos(albumId: Int = this.albumId) {
        if (albumId == -1) {
            throw IllegalArgumentException("albumId not set")
        }
        oLoading.value = true
        viewModelScope.launch {
            val result = photoRepo.getPhotosByAlbumId(albumId)
            if (result is RequestResult.Error) {
                oError.value = result.toString()
                oLoading.value = false
                return@launch
            }
            val photoList = (result as RequestResult.Success).data
            oPhotoList.value = photoList
            oLoading.value = false
        }
    }
}