package com.lukaskraener.ha_analyse

import android.os.Environment
import android.widget.TextView
import okhttp3.internal.EMPTY_BYTE_ARRAY
import java.io.File
import java.lang.Exception


class Readfiles {

    fun reader(): Int {
        var foundedFilesfromst: ArrayList<File> = ArrayList()
        var ausgabe = ""
        var anzahl = 0
        val regex = "Trip_[a-zA-z0-9_-]*.txt".toRegex()
        val directory = File(Environment.getExternalStorageDirectory().toString() + "/_hybridassistant/TripData")
        var files = directory.listFiles()

        if (files != null) {
            if (files.size >= 0) {
                for (i in 1 until files.size) {
                    if (regex.matches(files[i].name)) {
                        foundedFilesfromst.add(files[i])
                        ausgabe = ausgabe + files[i].name.toString() + "\n"
                        anzahl += 1
                    }
                }
                //foundedFilesfromst.toArray()
            }
        }
        return anzahl
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

}