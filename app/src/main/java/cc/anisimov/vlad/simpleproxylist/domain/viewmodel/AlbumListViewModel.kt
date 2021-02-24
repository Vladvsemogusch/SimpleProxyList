package cc.anisimov.vlad.simpleproxylist.domain.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cc.anisimov.vlad.simpleproxylist.data.repository.LocaleRepo
import cc.anisimov.vlad.simpleproxylist.data.repository.LocaleRepo.Companion.REGION_UA
import cc.anisimov.vlad.simpleproxylist.data.repository.ProxyRepo
import cc.anisimov.vlad.simpleproxylist.domain.model.ProxyInfoUI
import kotlinx.coroutines.launch


class AlbumListViewModel @ViewModelInject constructor(
    private val proxyRepo: ProxyRepo,
    private val localeRepo: LocaleRepo
) : ViewModel() {
    companion object {
        const val defaultRegion = REGION_UA
    }

    //  o for observable
    val oProxyList: MutableLiveData<List<ProxyInfoUI>> = MutableLiveData(ArrayList())
    val oLoading = MutableLiveData(false)
    val oError: MutableLiveData<String?> = MutableLiveData(null)
    val oCurrentRegion: MutableLiveData<String?> = MutableLiveData(null)


    init {
        setupDefaultRegion()
        oCurrentRegion.observeForever { currentRegion: String? ->
            currentRegion?.also {
                loadDefaultProxies(currentRegion)
            }
        }
    }

    private fun setupDefaultRegion() {
        val deviceRegion = localeRepo.getCurrentRegion()
        oCurrentRegion.value = if (localeRepo.getRegionList().contains(deviceRegion)) {
            deviceRegion
        } else {
            defaultRegion
        }
    }

    private fun loadDefaultProxies(region: String) {
        oLoading.value = true
        viewModelScope.launch {
            val data = proxyRepo.getDefaultProxies(region)
//            when (result) {
//                is RequestResult.Error -> {
//                    oError.value = result.toString()
//                    oLoading.value = false
//                    return@launch
//                }
//                is RequestResult.Success -> {
//                    val proxyList = result.data
//                    oProxyList.value = proxyList
//                    oLoading.value = false
//                }
//            }
            oProxyList.value = data
            oLoading.value = false
        }
    }

    fun onLocaleSelected(selectedItem: String) {

    }
}