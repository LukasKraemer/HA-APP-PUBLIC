package com.lukaskraener.ha_analyse


import android.os.Environment
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.IOException
import java.nio.file.Files


class API {

    fun reader( token: String, url: String){
        val formBody: RequestBody = FormBody.Builder()
            .add("APP", "reader")
            .add("token", token)
            .build()

        posttoServer(url, formBody)
    }

    fun uploader(url:String,token: String): String {
        var r: Int=0
        var f:Int = 0
        val directory = File(Environment.getExternalStorageDirectory().toString() + "/_hybridassistant/TripData")
        val files = directory.listFiles()!!
        for (i in files.iterator()) {
            if ("Trip_[a-zA-z0-9_-]*.txt".toRegex().matches(i.name)) {
                if (UploaderAPI.uploadFile(url, i, token)) { r++ } else { f++ }}
        }

    return "Erfolgreich: "+ r.toString()+"\nFehler: "+f.toString()
}
    fun programmstart(token: String, url: String, dbuser: String, dbpwd: String, dbip: String, dbschema: String, dbport: String, programm:String, prozessanzahl: String) {
        val formBody: RequestBody = FormBody.Builder()
            .add("APP", "uploader")
            .add("token", token)
            .add("APP_user", dbuser)
            .add("APP_password", dbpwd)
            .add("APP_adress", dbip)
            .add("APP_schema", dbschema)
            .add("APP_port", dbport)
            .add("APP_programm", programm)
            .add("APP_prozess", prozessanzahl)
            .build()

        posttoServer(formBody = formBody, url = url)

    }

     fun posttoServer(url: String, formBody: RequestBody) {
         val request = Request.Builder().url(url).post(formBody).build()
         val client = OkHttpClient()



         client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                //array = JSONArray(response.body!!.string())
                println(response.body!!.string())
            }

            override fun onFailure(call: Call, e: IOException) {
                println(e.printStackTrace())
            }

        })

    }
}