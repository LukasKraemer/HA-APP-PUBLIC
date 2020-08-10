package com.lukaskraener.ha_analyse



import android.widget.TextView
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.security.MessageDigest


class API {
    /**baut die Anfrage für den Reader und start die Post funktion*/
    fun reader( token: String, url: String, anzeige: TextView) {

        val formBody: RequestBody = FormBody.Builder()
            .add("APP", "reader")
            .add("token", token)
            .build()
        val request = Request.Builder().url(url).post(formBody).build()
        val client = OkHttpClient()

        client.newCall(request).enqueue(object : Callback {

            override fun onResponse(call: Call, response: Response) {
                println(response.body!!.string())
                ausgabe_reader(response,anzeige)

            }

            override fun onFailure(call: Call, e: IOException) {
                println(e.printStackTrace())
            }
        })



    }
    /**übergibt das Datenarray einzeln an die Post Medthode*/
    fun uploader_handler(url:String,token: String,files:Array<File>): String {
        var r: Int=0
        var f:Int = 0



        for (i in files.iterator()) {
            if ("Trip_[a-zA-z0-9_-]*.txt".toRegex().matches(i.name)) {
                if (UploaderAPI.uploadFile(url, i, token)) { r++ } else { f++ }}
        }
        return "erfolgreich" + r.toString()+"\nfehler: "+ f.toString()
    }

    fun uploader(token: String, url: String, anzeige: TextView){
        val formBody: RequestBody = FormBody.Builder()
            .add("APP", "filename_reader")
            .add("token", token)
            .build()
        Thread(Runnable {
            val request = Request.Builder().url(url).post(formBody).build()
            val client = OkHttpClient()

            client.newCall(request).enqueue(object : Callback {

                override fun onResponse(call: Call, response: Response) {
                    var files = emptyArray<File>()
                    try {
                        val json = JSONArray(response.body!!.string())
                        try {
                            files = Readfiles().reader()
                        }catch (e: java.lang.Exception){
                            files = emptyArray<File>()
                        }


                        if (files.isNotEmpty()) {/*
                    files.forEach {
                        for (i in 1..json.length()) {
                            if (json.getString(i) == it.name.toString()) {
                                neue_data.add(it)
                            }
                        }
                    }
*/
                            val jsonlist = json.get(0).toString().toList() as List<String>
                            var diff = jsonlist
                            files.forEach {
                                val data= it.name.toList() as List<String>
                                diff.minus(it.name.toList() as List<String>)
                            }
                            println(diff.toTypedArray().size)





                            anzeige.text = "Differenz = " + diff.size
                            //datenarray.forEach {
                            //    UploaderAPI.uploadFile(url,it,token)
                            //      i++
                            // anzeige.text="hochgeladen: "+i.toString()
                            // }anzeige.text= Fertig
                        } else {
                            anzeige.text = "keine Daten loakel gefunden"
                        }

                    }catch (e:Exception){
                        e.printStackTrace()
                        anzeige.text= e.message.toString()
                    }
                }

                override fun onFailure(call: Call, e: IOException) {
                    println(e.printStackTrace())
                }
            })
        }).start()

    }

    /**baut die Anfrage für den Programmstart und start die Post funktion*/
    fun programmstart(token: String, url: String, dbuser: String, dbpwd: String, dbip: String, dbschema: String, dbport: String, programm:String, prozessanzahl: String, anzeige :TextView) {
        val formBody: RequestBody = FormBody.Builder()
            .add("APP", "start")
            .add("token", token)
            .add("APP_user", dbuser)
            .add("APP_password", dbpwd)
            .add("APP_adress", dbip)
            .add("APP_schema", dbschema)
            .add("APP_port", dbport)
            .add("APP_programm", programm)
            .add("APP_prozess", prozessanzahl)
            .build()

        val request = Request.Builder().url(url).post(formBody).build()
        val client = OkHttpClient()

        client.newCall(request).enqueue(object : Callback {

            override fun onResponse(call: Call, response: Response) {
                println(response.body!!.string())
                anzeige.text= "Programm gestartet"

            }

            override fun onFailure(call: Call, e: IOException) {
                println(e.printStackTrace())
            }
        })

    }
    /**Sendet mit Post alles zum Server*/
    fun posttoServer(url: String, formBody: RequestBody){
        val request = Request.Builder().url(url).post(formBody).build()
        val client = OkHttpClient()

        client.newCall(request).enqueue(object : Callback {

            override fun onResponse(call: Call, response: Response) {

                Thread(Runnable {

                }).start()
                val body = response.body!!.string()

                val json = JSONObject(body)
            }

            override fun onFailure(call: Call, e: IOException) {
                println(e.printStackTrace())
            }
        })

    }
    fun ausgabe_reader (response: Response, anzeige: TextView){
        var stg =0
        try {
            stg = Readfiles().reader().count()
        }catch (e: Exception){
            stg = 0
        }
        try {
            val json = JSONObject(response.body!!.string())
            val db = json.getString("db").toInt()
            val stg_server = json.getString("stg").toInt()

            anzeige.text =
                "Lokal: " + stg.toString() + "\nDatenbank: " + db.toString() + "\nDfferenz: " + (stg - db).toString() + "\nServer Speicher: " + stg_server.toString()
        }catch (es: Exception){
            es.printStackTrace()
            anzeige.text= "Fehler Server"
        }

    }
    fun hash(zeichenkette : String): String {
        val bytes = zeichenkette.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.fold("", { str, it -> str + "%02x".format(it) })
    }
}
