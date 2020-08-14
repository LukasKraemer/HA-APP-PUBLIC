package com.lukaskraener.ha_analyse



import android.widget.TextView
import okhttp3.*
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

        val request = Request.Builder().url(url).post(formBody).build()
        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                try {
                    //val json = gson.fromJson(response.body!!.string(), JSONUploader::class.java)
                    var json = JSONObject(response.body!!.string())
                    val files = Readfiles().reader()

                    var gleich = ArrayList<File>()
                   val serverfilelist = json.getJSONArray("files")

                    Thread(Runnable {
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
                    }).start()

                    //anzeige.text= "Fertig"
                }catch (e: Exception){
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                println(e.printStackTrace())
            }
        })


    }
    /**baut die Anfrage für den Programmstart und start die Post funktion*/
    fun programmstart(anzeige: TextView,token: String, url: String, dbuser: String, dbpwd: String, dbip: String, dbschema: String, dbport: String, programm:String, prozessanzahl: String) {
        val formBody: RequestBody = FormBody.Builder()
            .add("APP", "start")
            .add("token", token)
            .add("APP_user", dbuser)
            .add("APP_password", dbpwd)
            .add("APP_adress", dbip)
            .add("APP_schema", dbschema)
            .add("APP_port", dbport)
            .add("APP_program", programm)
            .add("APP_threads", prozessanzahl)
            .build()

        val request = Request.Builder().url(url).post(formBody).build()
        val client = OkHttpClient()

        client.newCall(request).enqueue(object : Callback {

            override fun onResponse(call: Call, response: Response) {
                val json = JSONObject(response.body!!.string())
                val shellausgabe = json.getString("shell")
                println(shellausgabe)
                anzeige.text = shellausgabe
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
class JSONUploader(val files: List<String>, val size:Int)