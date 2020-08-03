package com.lukaskraener.ha_analyse

import okhttp3.*
import org.json.JSONArray
import java.io.IOException


class API {

    private lateinit var dbjson: JSONArray
    fun reader( token: String, url: String){
        val formBody: RequestBody = FormBody.Builder()
            .add("APP", "reader")
            .add("token", token)
            .build()

        posttoServer(url, formBody)
        //return this.dbjson.length().toString()
    }

    fun uploader(daten: Array<Any>, url:String){

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
        var array:JSONArray
        val request = Request.Builder().url(url).post(formBody).build()
        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                //array = JSONArray(response.body!!.string())
                println(response.body!!.string())
                println("${response.body!!.toString()::class.qualifiedName}")
            }

            override fun onFailure(call: Call, e: IOException) {
                println("Fehler beim laden")
            }
        })

    }
}