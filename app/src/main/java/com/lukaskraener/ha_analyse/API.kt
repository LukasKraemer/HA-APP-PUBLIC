package com.lukaskraener.ha_analyse



import android.widget.TextView
import okhttp3.*
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.lang.Exception

class API {
    fun reader( token: String, url: String, anzeige: TextView) {
    val programm= "reader"
        val formBody: RequestBody = FormBody.Builder()
            .add("APP", programm)
            .add("token", token)
            .build()
        sendtoserver(url = url, token = token,programm = programm,anzeige = anzeige,formBody = formBody)
    }

    fun uploader(token: String, url: String, anzeige: TextView){
        val programm = "filename_reader"
        val formBody: RequestBody = FormBody.Builder()
            .add("APP", programm)
            .add("token", token)
            .build()
        sendtoserver(url = url, token = token,programm = programm,anzeige = anzeige,formBody = formBody)
    }

    fun programmstart(anzeige: TextView,token: String, url: String, dbuser: String, dbpwd: String, dbip: String, dbschema: String, dbport: String, programm:String, prozessanzahl: String) {
        val programm= "start"
        val formBody: RequestBody = FormBody.Builder()
            .add("APP", programm)
            .add("token", token)
            .add("APP_user", dbuser)
            .add("APP_password", dbpwd)
            .add("APP_adress", dbip)
            .add("APP_schema", dbschema)
            .add("APP_port", dbport)
            .add("APP_program", programm)
            .add("APP_threads", prozessanzahl)
            .build()
        sendtoserver(url = url, token = token,programm = programm,anzeige = anzeige,formBody = formBody)

    }

    fun uploader_handler(url:String,token: String,files:Set<File>, anzeige: TextView) {
        var r: Int=0
        var f:Int = 0
        files.forEach{
                if (UploaderAPI.uploadFile(url, it, token)) { r++ } else { f++ }
            anzeige.text = "erfolgreich: "+r.toString()+"\nfehler: "+f.toString()
        }
    }

    private fun sendtoserver(formBody: RequestBody, url: String, anzeige: TextView, programm: String, token: String){
        val request = Request.Builder().url(url).post(formBody).build()
        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {

            override fun onResponse(call: Call, response: Response) {
                try {
                    Thread(Runnable {
                        when (programm) {
                            "reader" -> ausgabe_reader(response.body!!.string(), anzeige)
                            "filename_reader" -> datenabgleich(response.body!!.string(), anzeige,url,token)
                            "start" -> programm_bearbeiten(response.body!!.string(), anzeige)
                        }
                    }).start()
                }catch (e: Exception){
                e.printStackTrace()
                anzeige.text = "Server Fehler"
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                anzeige.text = "Netzwerkfehler"
            }
        })
    }


    fun ausgabe_reader (responsestring: String, anzeige: TextView){
        var status = 0
        try {
            val json = JSONObject(responsestring)
            status =1
            val db = json.getString("db").toInt()
            status= 2
            val stg_server = json.getInt("stg")
            status= 3
            val stg = Readfiles().reader().size
            status=4
            anzeige.text =
                "Lokal: " + stg.toString() + "\nDatenbank: " + db.toString() + "\nDfferenz: " + (stg - stg_server).toString() + "\nServer Speicher: " + stg_server.toString()
        }catch (e: Exception){
            e.printStackTrace()
            var fehler :String = ""
            when(status){
                0 -> fehler="unerwarte Antwort vom Server"
                1 -> fehler= "Datenbank konnnte nicht ausgelesen werden"
                2 -> fehler ="Fehler auf dem Serverspeicher"
                3 -> fehler ="Fehler beim Einladen des lokalen Speicher"
                4 -> fehler ="Werte können nicht ausgegben werden"
            }
            anzeige.text = status.toString() + " - "+ fehler
        }
    }


    private fun programm_bearbeiten(response: String,anzeige: TextView){
        try{anzeige.text = JSONObject(response).getString("shell")}catch (e : Exception){anzeige.text="Fehler bei Ausgabe"}
    }

    private fun datenabgleich(response: String,anzeige: TextView, url: String,token: String){
        try {
            val json = JSONObject(response)
            val files = Readfiles().reader()
            val gleich = ArrayList<File>()
            val serverfilelist = json.getJSONArray("files")
            try {
                files.forEach {
                    for (i in 0..json.get("size").toString().toInt()-1) {
                        if (it.name == serverfilelist[i]) {
                                gleich.add(it)
                                anzeige.text = "gefunden: " + gleich.size.toString()
                        }
                    }
                }
                anzeige.text="Treffer: "+gleich.size.toString()
            }catch (e: Exception){
                e.printStackTrace()
                anzeige.text="Fehler beim Abgeleich"
            }
            val difference = files.toSet().minus(gleich.toSet())
            if( gleich.size + difference.size == files.size) {
                anzeige.text =
                    "gleich: " + gleich.size.toString() + "\nungleich: " + difference.size.toString()
            }else{
                anzeige.text =
                    "\nDifferenzfehler\ngleich: " + gleich.size.toString() + "\nungleich: " + difference.size.toString()
            }
            uploader_handler(url=url,token = token,files = difference,anzeige=anzeige)
    }catch (e: Exception){
        e.printStackTrace()}
    }
}