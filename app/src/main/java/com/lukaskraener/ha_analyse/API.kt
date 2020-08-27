package com.lukaskraener.ha_analyse



import android.content.Context
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import okhttp3.*
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.lang.Exception

class API (
    val tokens: String,
    val urls: String,
    val anzeiges: TextView,
    val context: Context
)
{
    private val token = tokens
    private val url = urls
    private val anzeige = anzeiges
    private lateinit var responsestring: JSONObject

    fun initrespone(response:String){
        this.responsestring = JSONObject(response)
    }

    fun reader( ) {
        val programm= "reader"
        val formBody: RequestBody = FormBody.Builder()
            .add("APP", programm)
            .add("token", token)
            .build()
        sendtoserver(programm = programm, formBody = formBody)
    }

    fun uploader(processbar : ProgressBar){
        val programm = "filename_reader"
        val formBody: RequestBody = FormBody.Builder()
            .add("APP", programm)
            .add("token", token)
            .build()
        sendtoserver(programm = programm,formBody = formBody)
        processbar.visibility= View.GONE
    }

    fun programmstart(dbuser: String, dbpwd: String, dbip: String, dbschema: String, dbport: String, py_programm:String, prozessanzahl: String) {
        val programm= "start"
        val formBody: RequestBody = FormBody.Builder()
            .add("APP", programm)
            .add("token", token)
            .add("APP_user", dbuser)
            .add("APP_password", dbpwd)
            .add("APP_adress", dbip)
            .add("APP_schema", dbschema)
            .add("APP_port", dbport)
            .add("APP_program", py_programm)
            .add("APP_threads", prozessanzahl)
            .build()

        sendtoserver(programm = programm,formBody = formBody)

    }

    private fun uploader_handler(files:Set<File>) {
        var r: Int=0
        var f:Int = 0
        var i : Int = 0
        val notificationtext= this.context.getString(R.string.notification_uploader)
        MainFragment().testbenahritigung (notificationtext, 0, files.size)
        files.forEach{
            if (UploaderAPI.uploadFile(url, it, token)) { r++ } else { f++ }
            MainFragment().testbenahritigung (notificationtext, r+f, files.size)
        }
        anzeige.text = this.context.getString(R.string.uploader_output, r, f)
    }

    private fun sendtoserver(formBody: RequestBody, programm: String){
        val request = Request.Builder().url(this.url).post(formBody).build()
        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {

            override fun onResponse(call: Call, response: Response) {
                initrespone(response.body!!.string())

                if(responsestring.getString("error")!= "none") {
                    try {
                        Thread {
                            when (programm) {
                                "reader" -> ausgabe_reader()
                                "filename_reader" -> datenabgleich()
                                "start" -> programm_bearbeiten()
                            }
                        }.start()
                    } catch (e: Exception) {
                        e.printStackTrace()
                        anzeige.text = context.getString(R.string.internal_error)
                    }
                }else{
                    anzeige.text = this@API.context.getString(R.string.server_error)
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                anzeige.text = this@API.context.getString(R.string.network_error)
            }
        })
    }


    private fun ausgabe_reader (){
        var status = 0
        try {
            val json = responsestring
            status =1
            val db = json.getInt("db")
            status= 2
            val stg_server = json.getInt("stg")
            status= 3
            val stg = Readfiles().reader().size
            status=4
            anzeige.text = this.context.getString(R.string.reader_output, stg,db, stg - stg_server, stg_server)
               // "Lokal: " + stg.toString() + "\nDatenbank: " + db.toString() + "\nDfferenz: " + (stg - stg_server).toString() + "\nServer Speicher: " + stg_server.toString()
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


    private fun programm_bearbeiten(){
        try{anzeige.text = responsestring.getString("shell")}catch (e : Exception){anzeige.text="Fehler bei Ausgabe"}
    }

    private fun datenabgleich(){
        try {
            val json = responsestring
            val files = Readfiles().reader()
            val gleich = ArrayList<File>()
            val serverfilelist = json.getJSONArray("files")
            try {
                files.forEach {
                    for (i in 0..json.get("size").toString().toInt()-1) {
                        if (it.name == serverfilelist[i]) {
                            gleich.add(it)
                        }
                    }
                }
                anzeige.text=this.context.getString(R.string.founded_output, gleich.size)
            }catch (e: Exception){
                e.printStackTrace()
                anzeige.text=this.context.getString(R.string.diff_error)
            }
            val difference = files.toSet().minus(gleich.toSet())
            if( gleich.size + difference.size == files.size) {
                anzeige.text = this.context.getString(R.string.diff_output, gleich.size, difference.size )
            }else{
                anzeige.text = this.context.getString(R.string.diff_error)+"\n"+this.context.getString(R.string.diff_output, gleich.size, difference.size )
            }
            uploader_handler(files = difference)
        }catch (e: Exception){
            e.printStackTrace()}
    }
}