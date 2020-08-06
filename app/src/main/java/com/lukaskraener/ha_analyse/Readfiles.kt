package com.lukaskraener.ha_analyse

import android.os.Environment
import android.widget.TextView
import okhttp3.internal.EMPTY_BYTE_ARRAY
import java.io.File
import java.lang.Exception


class Readfiles {

    fun reader(): Array<File>{
        val directory = File(Environment.getExternalStorageDirectory().toString() + "/_hybridassistant/TripData")
        var files = directory.listFiles()
        return  files

    }

    fun creatediff(local: Array<Any>?, server: Array<Any>): Array<Any>? {
        val diff = local
        if (diff != null) {
            val s = if (diff.isNotEmpty()) {
                "Es wurden " + diff.size + " neue Dateien gefunden"
            } else {
                "keine neunen Dateien"
            }

        }
        return  diff
    }
fun int_from_array(files: Array<File>): Int{
    var anzahl = 0
    if (files != null) {
        if (files.size >= 0) {
            for (i in 1 until files.size) {
                if ("Trip_[a-zA-z0-9_-]*.txt".toRegex().matches(files[i].name)) { anzahl += 1 } } } }
    return anzahl
}
}