package com.lukaskraener.ha_analyse

import android.os.Environment
import java.io.File
import java.lang.Exception


class Readfiles {

    fun reader(): ArrayList<File> {
        try {
            val directory = File(Environment.getExternalStorageDirectory().toString() + "/_hybridassistant/TripData")
            var files = directory.listFiles()
            val checkedfiles = ArrayList<File>()
            files.forEach {
                if ("Trip_[a-zA-z0-9_-]*.txt".toRegex().matches(it.name)) {
                    checkedfiles.add(it)
                }
            }
            val returnvalue =  checkedfiles
            return returnvalue

        }catch (e : Exception){
            val fehler= ArrayList<File>()
            return fehler
        }
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