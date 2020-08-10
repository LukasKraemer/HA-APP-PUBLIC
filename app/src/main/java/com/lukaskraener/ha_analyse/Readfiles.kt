package com.lukaskraener.ha_analyse

import android.os.Environment
import android.widget.TextView
import okhttp3.internal.EMPTY_BYTE_ARRAY
import java.io.File
import java.io.IOException
import java.lang.Exception


class Readfiles {

    fun reader(): Array<File>{
        try {
            val directory = File(
                Environment.getExternalStorageDirectory().toString() + "/_hybridassistant/TripData"
            )
            var files = directory.listFiles()
            return files
        }catch (e: IOException){
            val errorfile= emptyArray<File>()
            return errorfile
        }
    }
}