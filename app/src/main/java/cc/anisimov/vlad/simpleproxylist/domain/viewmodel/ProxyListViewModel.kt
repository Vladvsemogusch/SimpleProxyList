package cc.anisimov.vlad.simpleproxylist.domain.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cc.anisimov.vlad.simpleproxylist.data.repository.LocaleRepo
import cc.anisimov.vlad.simpleproxylist.data.repository.LocaleRepo.Companion.REGION_UA
import cc.anisimov.vlad.simpleproxylist.data.repository.ProxyRepo
import cc.anisimov.vlad.simpleproxylist.ui.model.ProxyInfoUI
import kotlinx.coroutines.launch


class ProxyListViewModel @ViewModelInject constructor(
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
            val proxyList = proxyRepo.getDefaultProxyList(region)
            var filteredProxyList = filterProxies(proxyList)
            filteredProxyList = filteredProxyList.sortedBy { it.id }
            oProxyList.value = filteredProxyList
            oLoading.value = false
        }
    }

    private suspend fun filterProxies(proxyList: List<ProxyInfoUI>): List<ProxyInfoUI> {
        val filteredProxies = ArrayList<ProxyInfoUI>()
        for (proxyInfo in proxyList) {
            if (checkAlive(proxyInfo)){
                filteredProxies.add(proxyInfo)
            }
        }
        return filteredProxies
    }

    private suspend fun checkAlive(proxyInfo: ProxyInfoUI): Boolean {
        val proxyResponseInfo = proxyRepo.getProxyResponseInfo(proxyInfo.region, proxyInfo.id)
        return proxyResponseInfo.serverResponded && proxyResponseInfo.responseCode in 200..299
    }

    fun onRegionSelected(region: String) {
        oCurrentRegion.value = region
    }
}