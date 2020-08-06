package com.lukaskraener.ha_analyse



import android.widget.TextView
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.IOException


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

                val json = JSONArray(response.body!!.string())
                val neue_data: ArrayList<File> = ArrayList()
                var files = Readfiles().reader()
                if(files.isNotEmpty()) {
                    files.forEach {
                        for (i in 1..json.length()) {
                            if (json.getString(i) == it.name.toString()) {
                                neue_data.add(it)
                            }
                        }
                    }
                }
                val datenarray: Array<File> = neue_data.toArray() as Array<File>
                anzeige.text="Differenz = "+ datenarray.size

                //datenarray.forEach {
                //    UploaderAPI.uploadFile(url,it,token)
                //      i++
                // anzeige.text="hochgeladen: "+i.toString()
                // }anzeige.text= Fertig

            }

            override fun onFailure(call: Call, e: IOException) {
                println(e.printStackTrace())
            }
        })
        }).start()

    }
    /**baut die Anfrage für den Programmstart und start die Post funktion*/
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
    /**Sendet mit Post alles zum Server*/
     fun posttoServer(url: String, formBody: RequestBody){
         val request = Request.Builder().url(url).post(formBody).build()
         val client = OkHttpClient()

        client.newCall(request).enqueue(object : Callback {

            override fun onResponse(call: Call, response: Response) {

                Thread(Runnable {

                }).start()
                val body = response.body!!.string()
                println(response.body)
                println("response = "+body)
                val json = JSONObject(body)
                println(json.getString("db"))
            }

            override fun onFailure(call: Call, e: IOException) {
                println(e.printStackTrace())
            }
        })

    }
    fun ausgabe_reader (response: Response, anzeige: TextView){
        val json = JSONObject(response.body!!.string())
        val db = json.getString("db").toInt()
        val stg_server = json.getString("stg").toInt()
        val stg = Readfiles().int_from_array(Readfiles().reader())
        anzeige.text= "Lokal: "+ stg.toString() + "\nDatenbank: "+db.toString()+"\nDfferenz: "+ (stg-db).toString() +"\nServer Speicher: "+ stg_server.toString()
    }
}
