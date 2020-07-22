package com.lukaskraener.ha_analyse

import android.os.Environment
import android.widget.TextView
import okhttp3.internal.EMPTY_BYTE_ARRAY
import java.io.File
import java.lang.Exception

class Read_files() {
    private var founded: Array<Any>? = null
    private var foundedFilesfromst: ArrayList<File> = ArrayList()
    private lateinit var anzeige: TextView
    fun Read_files(ausgabe: TextView){
        anzeige= ausgabe
    }

    fun reader(){
        var path = ""
        var ausgabe: String = ""
        var anzahl = 0
        val regex = "Trip_[a-zA-z0-9_-]*.txt".toRegex()
        path =
            Environment.getExternalStorageDirectory().toString() + "/_hybridassistant/TripData"
        val directory = File(path)
        var files = directory.listFiles()

        if (files != null) {
            if (files.size >= 0) {
                for (i in 1 until files.size) {
                    if (regex.matches(files[i].name)) {
                        this.foundedFilesfromst.add(files[i])
                        ausgabe = ausgabe + files[i].name.toString() + "\n"
                        anzahl += 1
                    }
                }
                this.founded = foundedFilesfromst.toArray()
            }
        }
        var localFiles: Array<Any>? = founded
        val ServerFiles = loaddifffromServer()
        //return creatediff(localFiles, ServerFiles)
    anzeige.text="geht"
    }

    fun loaddifffromServer(): ByteArray {
        return EMPTY_BYTE_ARRAY

    }

    fun creatediff(local: Array<Any>?, server: ByteArray): Array<Any>? {
        var diff = local
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