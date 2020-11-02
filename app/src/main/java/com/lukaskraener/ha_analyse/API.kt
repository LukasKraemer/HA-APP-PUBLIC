package com.lukaskraener.ha_analyse



import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.LocaleList
import android.widget.TextView
import okhttp3.*
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList


class API(
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

    fun initrespone(response: String){
        this.responsestring = JSONObject(response)
    }

    fun reader( ) {
        sendit("reader")
    }


    fun uploader(){
        sendit("filename_reader")
    }


    fun programmstart() {
        sendit("start")
    }

    @SuppressLint("StringFormatInvalid")
    private fun uploaderHandler(files: Set<File>, common: Int) {
        var r=0
        var f = 0
        print(files)
        files.forEach{
            if (UploaderAPI.uploadFile(url, it, token)) { r++ } else { f++ }
        }
        anzeige.text = this.context.getString(R.string.uploader_output, r, f, common)
    }



    private fun sendit(programm: String) {
        try {

            val request = Request.Builder()
                .url(this.url + "?APP=" + programm)
                .header("User-Agent", "HA-Tool Android")
                .addHeader("Accept", "application/json")
                .addHeader("Accept-Language", getLanguage())
                .addHeader("Authorization", token)
                .addHeader("Connection","keep-alive")
                .addHeader("Accept-Charset","utf-8")
                .build()

            val time: Long = if (programm=="Start") {300}else{10}

            val client = OkHttpClient().newBuilder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(time, TimeUnit.SECONDS)
                .build()

            client.newCall(request).enqueue(object : Callback {

                override fun onResponse(call: Call, response: Response) {
                    initrespone(response.body!!.string())

                    if (responsestring.getString("error") == "none") {
                        try {
                            Thread {
                                when (programm) {
                                    "reader" -> ausgabeReader()
                                    "filename_reader" -> datenabgleich()
                                    "start" -> programmBearbeiten()
                                }
                            }.start()
                        } catch (e: Exception) {
                            e.printStackTrace()
                            anzeige.text = context.getString(R.string.internal_error)
                        }
                    } else {
                        anzeige.text = this@API.context.getString(R.string.server_error)+responsestring.getString("error")
                    }
                }

                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()

                    anzeige.text = this@API.context.getString(R.string.network_error)
                }
            })

        }catch (e: Exception){
            anzeige.text = this@API.context.getString(R.string.network_error)
        }
    }


    private fun ausgabeReader (){
        var status = 0
        try {
            val json = responsestring
            status =1
            val db = json.getInt("db")
            status= 2
            val stgServer = json.getInt("stg")
            status= 3
            val stg = Readfiles().reader().size
            status=4
            anzeige.text = this.context.getString(
                R.string.reader_output,
                stg,
                db,
                stg - stgServer,
                stgServer
            )
               // "Lokal: " + stg.toString() + "\nDatenbank: " + db.toString() + "\nDfferenz: " + (stg - stg_server).toString() + "\nServer Speicher: " + stg_server.toString()
        }catch (e: Exception){
            e.printStackTrace()
            var fehler = ""
            when(status){
                0 -> fehler = "unerwarte Antwort vom Server"
                1 -> fehler = "Datenbank konnnte nicht ausgelesen werden"
                2 -> fehler = "Fehler auf dem Serverspeicher"
                3 -> fehler = "Fehler beim Einladen des lokalen Speicher"
                4 -> fehler = "Werte können nicht ausgegben werden"
            }
            anzeige.text = status.toString() + " - "+ fehler
        }
    }


    private fun programmBearbeiten(){
        try{anzeige.text = this.responsestring.getString("shell").toString()}catch (e: Exception){anzeige.text="Fehler bei Ausgabe"}
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
                            print(it)
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
                anzeige.text = this.context.getString(
                    R.string.diff_output,
                    gleich.size,
                    difference.size
                )
            }else{
                anzeige.text = this.context.getString(R.string.diff_error)+"\n"+this.context.getString(
                    R.string.diff_output,
                    gleich.size,
                    difference.size
                )
            }
            uploaderHandler(files = difference,common = gleich.size)
        }catch (e: Exception){
            e.printStackTrace()}
    }
    private fun getLanguage(): String {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return LocaleList.getDefault().toLanguageTags()
        } else {
            return Locale.getDefault().language
        }
    }

}