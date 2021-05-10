package com.lukaskraener.ha_analyse



import android.content.Context
import android.widget.TextView
import androidx.preference.PreferenceManager
import kotlinx.coroutines.runBlocking
import okhttp3.*
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.net.SocketTimeoutException
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList


class API(
    val anzeiges: TextView?,
    val context: Context
)
{
    private val token = String.format("Bearer %s", PreferenceManager.getDefaultSharedPreferences(context).getString(
        "key_api_token",
        ""
    )!!)

    private var url :String
    private val anzeige = anzeiges
    private lateinit var responsestring: JSONObject
    private var error: Boolean = true

    init {
        val apiprotokoll: String
        apiprotokoll = if(PreferenceManager.getDefaultSharedPreferences(context).getBoolean(
                "key_api_protokoll",
                true
            )) {
            "https://"
        }else{
            "http://"
        }
        this.url= apiprotokoll+PreferenceManager.getDefaultSharedPreferences(context).getString(
            "key_api_ip",
            ""
        )!!+"/app/"
    }

    fun initrespone(response: String){
       try {
           this.responsestring = JSONObject(response)
           error = false

       }catch (e: java.lang.Exception){
           this.responsestring = JSONObject("{'error': 'reponse'}")
           error = true
       }

    }

    fun reader( ) {
        sendit("reader")
    }


    fun uploader(){
        sendit("filename")
    }


    fun programmstart() {
        sendit("start")
    }


    private fun uploaderHandler(files: Set<File>) {
        runBlocking {
            files.forEach{
                UploaderAPI.uploadFile(url, it, token)
            }
        }

    }



    private fun sendit(program: String) {
        try {

            val request = Request.Builder()
                .url(this.url + program)
                .header("User-Agent", "HA-Tool Android")
                .addHeader("Authorization", token)
                .build()

            val time: Long = if (program=="start") {300}else{15}

            val client = OkHttpClient().newBuilder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(time, TimeUnit.SECONDS)
                .build()

            client.newCall(request).enqueue(object : Callback {

                override fun onResponse(call: Call, response: Response) {
                    initrespone(response.body!!.string())
                    if (!error) {
                        try {
                            Thread {
                                when (program) {
                                    "reader" -> ausgabeReader()
                                    "filename" -> datenabgleich()
                                    "start" -> programmBearbeiten()
                                }
                            }.start()
                        } catch (e: Exception) {
                            e.printStackTrace()
                            anzeige?.text = context.getString(R.string.internal_error)
                        }
                    } else {
                        anzeige?.text =
                            this@API.context.getString(R.string.server_error) + responsestring.getString(
                                "error"
                            )
                    }
                }

                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()

                    anzeige?.text = this@API.context.getString(R.string.network_error)
                }
            })
        }catch (e: SocketTimeoutException){
            anzeige?.text = "time out"
        }catch (e: Exception){
            anzeige?.text = this@API.context.getString(R.string.network_error)
        }
    }


    private fun ausgabeReader (){
        try {
            val json = responsestring
            val db = json.getInt("databaseLast")
            val stgServer = json.getInt("filesStorage")
            val stg = Readfiles().reader().size
            anzeige?.text = this.context.getString(
                R.string.reader_output,
                stg,
                db,
                stg - stgServer,
                stgServer
            )
               // "Lokal: " + stg.toString() + "\nDatenbank: " + db.toString() + "\nDfferenz: " + (stg - stg_server).toString() + "\nServer Speicher: " + stg_server.toString()
        }catch (e: Exception){
            e.printStackTrace()
        }
    }


    private fun programmBearbeiten(){
        try{anzeige?.text = this.responsestring.getString("shell").toString()}catch (e: Exception){anzeige?.text="Fehler bei Ausgabe"}
    }

    private fun datenabgleich(){
        try {
            val files = Readfiles().reader()
            val gleich = ArrayList<File>()
            val serverfilelist = responsestring.getJSONArray("filename")
            try {
                files.forEach {
                    for (i in 0 until serverfilelist.length()) {
                        if (it.name == serverfilelist[i]) {
                            gleich.add(it)
                        }
                    }
                }
                anzeige?.text=this.context.getString(R.string.founded_output, gleich.size)
            }catch (e: Exception){
                e.printStackTrace()
                anzeige?.text=this.context.getString(R.string.diff_error)
            }
            val difference = files.toSet().minus(gleich.toSet())
            if( gleich.size + difference.size == files.size) {
                anzeige?.text = this.context.getString(
                    R.string.diff_output,
                    gleich.size,
                    difference.size
                )
            }else{
                anzeige?.text = this.context.getString(R.string.diff_error)+"\n"+this.context.getString(
                    R.string.diff_output,
                    gleich.size,
                    difference.size
                )
            }
            uploaderHandler(difference)
        }catch (e: Exception){
            e.printStackTrace()}
    }


}