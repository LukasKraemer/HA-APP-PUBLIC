package com.lukaskraener.ha_analyse

import android.os.Environment
import java.io.File
import java.nio.file.Files

class Read_files {
    private var founded: Array<Any>? = null
    private var foundedFilesfromst:ArrayList<File> = ArrayList()
    fun reader(): String? {
        var path = ""
        var ausgabe: String =""
        var anzahl = 0
        val regex ="Trip_[a-zA-z0-9_-]*.txt".toRegex()
        return try {
            path = Environment.getExternalStorageDirectory()
                .toString() + "/_hybridassistant/TripData"
            val directory = File(path)
            val files = directory.listFiles()
            if (files != null) {
                if (files.size >= 0) {
                    for (i in 1 until files.size) {
                        if(regex.matches(files[i].name)) {
                            this.foundedFilesfromst.add(files[i])
                            ausgabe = ausgabe + files[i].name.toString() + "\n"
                            anzahl += 1
                        }
                    }
                    this.founded= foundedFilesfromst.toArray()
                }
            } else {
                ausgabe = "keine Dateien"
            }

            "Anzahl $anzahl \n $ausgabe"
        } catch (e: Exception) {
            e.message
        }
    }
}