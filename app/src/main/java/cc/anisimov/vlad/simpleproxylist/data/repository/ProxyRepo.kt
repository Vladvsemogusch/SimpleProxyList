package cc.anisimov.vlad.simpleproxylist.data.repository

import android.content.Context
import cc.anisimov.vlad.simpleproxylist.R
import cc.anisimov.vlad.simpleproxylist.data.model.RawDefaultProxies
import cc.anisimov.vlad.simpleproxylist.data.model.RequestResult
import cc.anisimov.vlad.simpleproxylist.data.repository.LocaleRepo.Companion.REGION_FR
import cc.anisimov.vlad.simpleproxylist.data.repository.LocaleRepo.Companion.REGION_RU
import cc.anisimov.vlad.simpleproxylist.data.repository.LocaleRepo.Companion.REGION_UA
import cc.anisimov.vlad.simpleproxylist.data.repository.LocaleRepo.Companion.REGION_US
import cc.anisimov.vlad.simpleproxylist.data.source.remote.ProxyApi
import cc.anisimov.vlad.simpleproxylist.domain.model.ProxyInfoUI
import cc.anisimov.vlad.simpleproxylist.utils.Utils
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProxyRepo @Inject constructor(
    private val remoteApi: ProxyApi,
    @ApplicationContext private val appContext: Context,
    private val gson: Gson
) {

    suspend fun getProxyInfo(region: String, id: Int): RequestResult<ProxyInfoUI> =
        withContext(
            Dispatchers.IO
        ) {
            val result = kotlin.runCatching { remoteApi.getProxyInfo(region, id) }
            if (result.isFailure) {
                return@withContext RequestResult.Error(result.exceptionOrNull())
            }
            val proxyInfo = result.getOrNull()!!.body()!!
            val proxyInfoUI = ProxyInfoUI(proxyInfo.region,proxyInfo.id)
            return@withContext RequestResult.Success(proxyInfoUI)
        }

    fun getDefaultProxies(region: String): List<ProxyInfoUI> {
        val defaultProxiesJson = Utils.readRawTextFile(appContext.resources, R.raw.default_proxies)
        val defaultProxies = gson.fromJson(defaultProxiesJson, RawDefaultProxies::class.java)
        //  Assuming json comes from remote source and we can't change it's structure
        val localeSpecificProxies = when (region) {
            REGION_UA -> defaultProxies.ua
            REGION_RU -> defaultProxies.ru
            REGION_FR -> defaultProxies.fr
            REGION_US -> defaultProxies.us
            else -> throw Exception("No such region: $region")
        }
        return localeSpecificProxies.map {
            val splitProxyUrl = it.split("/")
            val proxyRegion = splitProxyUrl[3]
            val proxyId = splitProxyUrl[4].toInt()
            ProxyInfoUI(proxyRegion, proxyId)
        }
    }

}