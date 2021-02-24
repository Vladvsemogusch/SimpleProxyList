package cc.anisimov.vlad.simpleproxylist.utils

import android.content.res.Resources
import java.io.ByteArrayOutputStream
import java.io.InputStream

object Utils {

    fun readRawTextFile(resources: Resources, rawResId: Int): String {
        val fileInputStream = resources.openRawResource(rawResId)
        return readTextStream(fileInputStream)
    }

    fun readTextStream(inputStream: InputStream): String {
        val outputStream = ByteArrayOutputStream()
        val buf = ByteArray(1024)
        var len: Int
            while (inputStream.read(buf).also { len = it } != -1) {
                outputStream.write(buf, 0, len)
            }
            outputStream.close()
            inputStream.close()
        return outputStream.toString()
    }

}