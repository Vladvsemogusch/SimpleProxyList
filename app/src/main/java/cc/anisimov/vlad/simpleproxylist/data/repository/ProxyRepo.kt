package cc.anisimov.vlad.simpleproxylist.data.repository

import android.content.Context
import android.util.Log
import cc.anisimov.vlad.simpleproxylist.R
import cc.anisimov.vlad.simpleproxylist.data.model.ProxyInfo
import cc.anisimov.vlad.simpleproxylist.data.model.RawDefaultProxies
import cc.anisimov.vlad.simpleproxylist.data.model.RequestResult
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
            Dispatchers.IO
        ) {
            var requestResult: RequestResult<Response<ProxyInfo>> =
                RequestResult.Error(Exception("Unexpected scenario"))
            val handler = CoroutineExceptionHandler { _, exception ->
                requestResult = RequestResult.Error(exception)
            }
            launch(handler) {
                try {
                    val response = remoteApi.getProxyInfo(region, id)
                    requestResult = RequestResult.Success(response)
                }catch (e:Exception){
                    Log.d("asd",e.toString())
                }

            }
            return@withContext requestResult
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
                ProxyResponseInfo(false, -1)
            }
            is RequestResult.Success -> {
                ProxyResponseInfo(true, requestResponse.data.code())
            }
        }
    }

}