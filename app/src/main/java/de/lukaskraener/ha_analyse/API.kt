package de.lukaskraener.ha_analyse



import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import android.widget.Toast
import androidx.preference.PreferenceManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import okhttp3.*
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList


class API(private val oTextview: TextView?, val context: Context)
{
    private val token: String = String.format("Bearer %s", PreferenceManager.getDefaultSharedPreferences(context).getString("key_api_token", "")!!)
    private lateinit var responseString: JSONObject
    private var error: Boolean = true

    private fun sendToast(message: String){
        Handler(Looper.getMainLooper()).post {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * @return an valid hostname or "" if where is any error
     */
    private fun getHostname(): String {
        val apiProtocol: String =  if(PreferenceManager.getDefaultSharedPreferences(context).getBoolean("key_api_protocol", true)) { "https://" }else{ "http://" }
        val url = PreferenceManager.getDefaultSharedPreferences(context).getString("key_api_ip", "")

        return if (url  == "" || url == "0.0.0.0" ){
            sendToast("NO IP SET")
            ""
        }else if(token == ""){
            Handler(Looper.getMainLooper()).post {
                sendToast("NO Token SET")
            }
            ""
        }else{
            apiProtocol+PreferenceManager.getDefaultSharedPreferences(context).getString("key_api_ip", ""+"/app/"
            )!!+"/app/"
        }
    }

    private suspend fun uploaderHandler(files: Set<File>, all: Int) {
        val context = this.context
        val url = getHostname()
        if( url == ""){ return }
        if(token == ""){ return }

        withContext(Dispatchers.IO) {
            files.forEach {
                println(it)
                UploaderAPI.uploadFile(url, it, token)
                delay(300)
                Handler(Looper.getMainLooper()).post {
                    oTextview?.text = context.getString(
                        R.string.uploader_output,
                        SUCCESS,
                        FAIL,
                        all-FAIL
                    )
                }
            }
        }
        oTextview?.text = context.getString(
            R.string.uploader_output,
            SUCCESS,
            FAIL,
            all-FAIL
        )
    }

    private fun send(program: String) {
        val url = getHostname()

        if( url == ""){
            return
        }
        if(token == ""){
            return
        }

        try {
            val request = Request.Builder()
                .url(url + program)
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
                    handleResponse(response.body!!.string())
                    if (!error) {
                        try {
                            Thread {
                                when (program) {
                                    "reader" -> doCompare()
                                    "filename" -> compareData()
                                    "start" -> programRun()
                                }
                            }.start()
                        } catch (e: Exception) {
                            e.printStackTrace()
                            oTextview?.text = context.getString(R.string.internal_error)
                        }
                    } else {
                        oTextview?.text = context.getString(R.string.server_error, responseString.getString("error" ))
                    }
                }

                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                    Handler(Looper.getMainLooper()).post {
                        oTextview?.text = context.getString(R.string.network_error)
                    }

                }
            })
        }catch (e: SocketTimeoutException){
            oTextview?.text = context.getString(R.string.timeout)
        }catch (e: Exception){
            oTextview?.text = context.getString(R.string.network_error)
        }
    }


    private fun doCompare (){
        try {
            val json = responseString
            val db = json.getInt("databaseLast")
            val stgServer = json.getInt("filesStorage")
            val stg = FileManager(context).reader().size
            oTextview?.text = context.getString(
                R.string.reader_output,
                stg,
                db,
                stg - stgServer,
                stgServer
            )
        }catch (e: Exception){
            e.printStackTrace()
        }
    }


    private fun programRun(){
        try{oTextview?.text = responseString.getString("shell").toString()}catch (e: Exception){oTextview?.text=context.getString(R.string.server_error)}
    }

    private fun compareData(){
        try {
            val files = FileManager(context).reader()
            val same = ArrayList<File>()
            val serverFileList = responseString.getJSONArray("filename")
            try {
                files.forEach {
                    for (i in 0 until serverFileList.length()) {
                        if (it.name == serverFileList[i]) {
                            same.add(it)
                        }
                    }
                }

                oTextview?.text=context.getString(R.string.founded_output, same.size)
            }catch (e: Exception){
                e.printStackTrace()
                oTextview?.text=context.getString(R.string.diff_error)
            }
            val difference = files.toSet().minus(same.toSet())
            if( same.size + difference.size == files.size) {
                FAIL=0
                SUCCESS=0
                runBlocking {
                    uploaderHandler(difference, files.size)
                }
            }else{
                oTextview?.text = context.getString(R.string.diff_error)
            }
        }catch (e: Exception){
            e.printStackTrace()}
    }

    fun handleResponse(response: String){

       try {
           responseString = JSONObject(response)
           error = false

       }catch (e: java.lang.Exception){
           responseString = JSONObject("{'error': 'response'}")
           error = true
       }
    }

    fun reader( ) {
        send("reader")
    }

    fun uploader(){
        send("filename")
    }

    fun startProgram() {
        send("start")
    }



    companion object{
        private var SUCCESS: Int = 0
        private var FAIL: Int = 0
        fun success(){
            SUCCESS++
        }
        fun fail(){
            FAIL++
        }

    }

}