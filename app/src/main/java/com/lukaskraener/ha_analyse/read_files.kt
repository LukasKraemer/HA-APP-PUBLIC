package com.lukaskraener.ha_analyse

import android.os.Environment
import java.io.File


object read_files {
    fun reader(): String? {
        var ausgabe: String
        var anzahl = 0
        return try {

            var path = Environment.getDataDirectory().toString() + "/_hybridassistant/TripData"
            val directory = File(path)
            val files = directory.listFiles()
            if (files != null) {
                ausgabe = files[0].name
                if (files.size > 0) {
                    for (i in 1 until files.size) {
                        ausgabe = ausgabe + files[i].name
                        anzahl = anzahl + 1
                    }
                }
            } else {
                ausgabe = "keine Dateien"
            }
            "Anzahl $anzahl $ausgabe"
        } catch (e: Exception) {
            e.message
        }
    }

}