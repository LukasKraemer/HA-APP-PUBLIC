package com.lukaskraener.ha_analyse

import android.os.Environment
import java.io.File
import java.nio.file.Files


object read_files {
    fun reader(): Array<File>? {
        var path = Environment.getDataDirectory().toString() + "/_hybridassistant/TripData"
        val directory = File(path)
        val files = directory.listFiles()
        return files
    }
}