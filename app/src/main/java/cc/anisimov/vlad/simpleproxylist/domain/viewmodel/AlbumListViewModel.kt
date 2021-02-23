package cc.anisimov.vlad.simpleproxylist.domain.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cc.anisimov.vlad.simpleproxylist.data.model.RequestResult
import cc.anisimov.vlad.simpleproxylist.data.repository.AlbumRepo
import cc.anisimov.vlad.simpleproxylist.domain.model.AlbumUI
import kotlinx.coroutines.launch

class AlbumListViewModel @ViewModelInject constructor(private val albumRepo: AlbumRepo) :
    ViewModel() {
    companion object {
        const val USER_ID = 1
    }

    //  o for observable
    val oAlbumList: MutableLiveData<List<AlbumUI>> = MutableLiveData(ArrayList())
    val oLoading = MutableLiveData(false)
    val oError: MutableLiveData<String?> = MutableLiveData(null)

    init {
        loadAlbums()
    }

    private fun loadAlbums() {
        oLoading.value = true
        viewModelScope.launch {
            val result = albumRepo.getAlbumsByUserId(USER_ID)
            if (result is RequestResult.Error) {
                oError.value = result.toString()
                oLoading.value = false
                return@launch
            }
            val albumList = (result as RequestResult.Success).data
            oAlbumList.value = albumList
            oLoading.value = false
        }
    }
}