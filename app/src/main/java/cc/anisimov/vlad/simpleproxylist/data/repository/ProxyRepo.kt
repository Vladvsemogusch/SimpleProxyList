package cc.anisimov.vlad.simpleproxylist.data.repository

import android.content.Context
import cc.anisimov.vlad.simpleproxylist.R
import cc.anisimov.vlad.simpleproxylist.data.model.ProxyInfo
import cc.anisimov.vlad.simpleproxylist.data.model.RawDefaultProxies
import cc.anisimov.vlad.simpleproxylist.data.model.RequestResult
import cc.anisimov.vlad.simpleproxylist.data.model.exception.NetworkException
import cc.anisimov.vlad.simpleproxylist.data.repository.LocaleRepo.Companion.REGION_FR
import cc.anisimov.vlad.simpleproxylist.data.repository.LocaleRepo.Companion.REGION_RU
import cc.anisimov.vlad.simpleproxylist.data.repository.LocaleRepo.Companion.REGION_UA
import cc.anisimov.vlad.simpleproxylist.data.repository.LocaleRepo.Companion.REGION_US
import cc.anisimov.vlad.simpleproxylist.data.source.remote.ProxyApi
import cc.anisimov.vlad.simpleproxylist.domain.model.ProxyResponseInfo
import cc.anisimov.vlad.simpleproxylist.ui.model.ProxyInfoUI
import cc.anisimov.vlad.simpleproxylist.utils.Utils
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProxyRepo @Inject constructor(
    private val remoteApi: ProxyApi,
    @ApplicationContext private val appContext: Context,
    private val gson: Gson
) {

    private suspend fun getProxyInfoResponse(
        region: String,
        id: Int
    ): RequestResult<Response<ProxyInfo>> =
        withContext(
            IO
        ) {
            val response: Response<ProxyInfo>
            try {
                response = remoteApi.getProxyInfo(region, id)
            } catch (e: Exception) {
                return@withContext RequestResult.Error(e)
            }
            return@withContext when (response.code()) {
                in 200..299 -> RequestResult.Success(response)
                else -> RequestResult.Error(NetworkException(response.code()))
            }
        }

    fun getDefaultProxyList(region: String): List<ProxyInfoUI> {
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

    suspend fun getProxyResponseInfo(region: String, id: Int): ProxyResponseInfo {
        val requestResponse = getProxyInfoResponse(region, id)
        return when (requestResponse) {
            is RequestResult.Error -> {
                //  Response code doesn't matter in this case
                ProxyResponseInfo(false, -1)
            }
            is RequestResult.Success -> {
                ProxyResponseInfo(true, requestResponse.data.code())
            }
        }
    }

}