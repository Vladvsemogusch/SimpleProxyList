package cc.anisimov.vlad.simpleproxylist.data.repository

import android.content.res.Resources
import androidx.core.os.ConfigurationCompat
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocaleRepo @Inject constructor() {
    companion object {
        const val REGION_UA = "ua"
        const val REGION_RU = "ru"
        const val REGION_FR = "fr"
        const val REGION_US = "us"
    }

    private val regionList = arrayListOf(REGION_UA, REGION_RU, REGION_FR, REGION_US)

    fun getRegionList(): List<String> {
        return regionList
    }

    fun getCurrentRegion() =
        ConfigurationCompat.getLocales(Resources.getSystem().configuration)[0].language

}