package com.lukaskraener.ha_analyse



import android.widget.TextView
import okhttp3.*
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.lang.Exception
import java.lang.reflect.Executable
import kotlin.system.exitProcess


class API {
    fun reader( token: String, url: String, anzeige: TextView) {
    val programm= "reader"
        val formBody: RequestBody = FormBody.Builder()
            .add("APP", programm)
            .add("token", token)
            .build()
        sendtoserver(formBody, url, anzeige, programm)
    }

    fun uploader(token: String, url: String, anzeige: TextView){
        val programm = "filename_reader"
        val formBody: RequestBody = FormBody.Builder()
            .add("APP", programm)
            .add("token", token)
            .build()
        sendtoserver(formBody,url,anzeige,programm)
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
        sendtoserver(formBody,url,anzeige,programm)

    }

    fun uploader_handler(url:String,token: String,files:Array<File>): String {
        var r: Int=0
        var f:Int = 0
        for (i in files.iterator()) {
            if ("Trip_[a-zA-z0-9_-]*.txt".toRegex().matches(i.name)) {
                if (UploaderAPI.uploadFile(url, i, token)) { r++ } else { f++ }}
        }
        return "erfolgreich" + r.toString()+"\nfehler: "+ f.toString()
    }

    private fun sendtoserver(formBody: RequestBody, url: String, anzeige: TextView, programm: String){
        val request = Request.Builder().url(url).post(formBody).build()
        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {

            override fun onResponse(call: Call, response: Response) {
                try {
                    Thread(Runnable {
                        when (programm) {
                            "reader" -> ausgabe_reader(response.body!!.string(), anzeige)
                            "filename_reader" -> datenabgleich(response.body!!.string(), anzeige)
                            "start" -> programm_bearbeiten(response.body!!.string(), anzeige)
                        }
                    }).start()
                }catch (e: Exception){
                e.printStackTrace()
                anzeige.text = "Server Fehler"
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                println(e.printStackTrace())
                anzeige.text = "Netzwerkfehler"
            }
        })
    }


    fun ausgabe_reader (responsestring: String, anzeige: TextView){
        var status = 0
        println(responsestring)
        try {
            val json = JSONObject(responsestring)
            status =1
            val db = json.getString("db").toInt()
            status= 2
            val stg_server = json.getString("stg").toInt()
            status= 3
            val stg = Readfiles().int_from_array(Readfiles().reader())
            status=4
            anzeige.text =
                "Lokal: " + stg.toString() + "\nDatenbank: " + db.toString() + "\nDfferenz: " + (stg - db).toString() + "\nServer Speicher: " + stg_server.toString()
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

    private fun datenabgleich(response: String,anzeige: TextView){
        try {
            //val json = gson.fromJson(response.body!!.string(), JSONUploader::class.java)
            var json = JSONObject(response)
            val files = Readfiles().reader()

            var gleich = ArrayList<File>()
            val serverfilelist = json.getJSONArray("files")

                var i = 0
                if (json.getInt("size") > files.count()) {

                    try {
                        for (i in 1..json.getInt("size")) {

                            files.forEach {
                                println("-----------------------------")
                                println(serverfilelist.get(i).toString())
                                println(it.name.toString())
                                println("-----------------------------")

                                if(serverfilelist.get(i).toString() == it.name.toString()) {
                                    gleich.add(files[i])
                                    println("treffer in " + i)
                                }
                            }
                        }
                        anzeige.text= gleich.size.toString()
                    } catch (es: ArrayIndexOutOfBoundsException){
                        es.printStackTrace()
                        anzeige.text= gleich.size.toString() + "array"
                    }

                } else {
                    files.forEach {
                        try {
                            i = 0
                            for (i in 1..json.getInt("size")) {
                                //println("-----------------------------")
                                //println(serverfilelist.get(i).toString())
                                //println(it.name.toString())
                                //println("-----------------------------")

                                if (serverfilelist.get(i)
                                        .toString() == it.name.toString()
                                ) {
                                    gleich.add(files[i])
                                    println("treffer in " + i)
                                }

                            }
                            anzeige.text = gleich.size.toString()
                        }catch(er: ArrayIndexOutOfBoundsException){
                            anzeige.text = gleich.size.toString()
                        }
                    }
                }

                val uncomman = gleich //.toArray() as Array<File>
                anzeige.text="Gleich = "+ uncomman.size

                /*
                    uncomman.forEach {
                    UploaderAPI.uploadFile(url,it,token)
                    i++
                    anzeige.text="hochgeladen: "+i.toString()
                    }
                */
    }catch (e: Exception){
        e.printStackTrace()}
    }
}
class JSONUploader(val files: List<String>, val size:Int)