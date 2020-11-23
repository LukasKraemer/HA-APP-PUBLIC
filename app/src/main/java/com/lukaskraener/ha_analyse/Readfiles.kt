package com.lukaskraener.ha_analyse

import android.os.Environment
import java.io.File
import java.lang.Exception


class Readfiles {

    fun reader(): ArrayList<File> {
        var checker = Data_validiator()
        try {
            val directory = File(Environment.getExternalStorageDirectory().toString() + "/_hybridassistant/TripData")
            var files = directory.listFiles()
            val checkedfiles = ArrayList<File>()
            files.forEach {
                if (checker.isVAlidFilename(it.name)) {
                    checkedfiles.add(it)
                }
            }
            return checkedfiles

        }catch (e : Exception){
            return ArrayList()
        }
    }


}