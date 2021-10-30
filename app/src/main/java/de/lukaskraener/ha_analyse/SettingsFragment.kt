package de.lukaskraener.ha_analyse

import android.content.SharedPreferences
import android.os.Bundle
import android.text.InputType
import android.text.TextUtils
import androidx.preference.*
import com.google.gson.Gson
import okhttp3.*
import java.io.IOException
import java.lang.Exception


class SettingsFragment : PreferenceFragmentCompat() {

    //userdata
    private var preapiip:EditTextPreference? = null
    private var preapiprotocol:SwitchPreference? = null
    private var preapitoken:EditTextPreference? = null
    private var preoverviewip:EditTextPreference? = null
    private var preusername: EditTextPreference? = null
    private var prepasswd: EditTextPreference? = null

    //values:
    private  lateinit var sharedPreference: SharedPreferences
    private var apipip = ""
    private var apiprotocol = true
    private var apitoken = ""
    private var username = ""
    private var passwd = ""




    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings, rootKey)

        preapiip= findPreference("key_api_ip")
        preapiprotocol= findPreference("key_api_protocol")
        preapitoken= findPreference("key_api_token")
        preoverviewip = findPreference("key_overview_url")
        preusername = findPreference("username")
        prepasswd = findPreference("passwd")

        loadDatafromPreferences()

        preapiip?.setOnBindEditTextListener { editText ->
        editText.inputType = InputType.TYPE_CLASS_TEXT
        }
        preapitoken?.setOnBindEditTextListener { editText ->
            editText.inputType = InputType.TYPE_CLASS_TEXT
        }
        preoverviewip?.setOnBindEditTextListener { editText ->
        editText.inputType = InputType.TYPE_CLASS_TEXT
        }

        preusername?.setOnBindEditTextListener { editText ->
            editText.inputType = InputType.TYPE_CLASS_TEXT
        }
        prepasswd?.setOnBindEditTextListener { editText ->
            editText.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD or InputType.TYPE_CLASS_TEXT
        }

        preoverviewip?.summaryProvider = EditTextPreference.SimpleSummaryProvider.getInstance()


        preapiip?.summaryProvider = Preference.SummaryProvider<EditTextPreference>{ preference->
            val text = preference.text.toString()
            if(DataValidiator.isValidHostname(text)|| DataValidiator.isValidIP(text)){
                text
            } else{
                getString(R.string.wrongvalue)
            }
        }

        preapitoken?.summaryProvider= Preference.SummaryProvider<EditTextPreference> {preference ->
            val text = preference.text
            if(TextUtils.isEmpty(text)){
                getString(R.string.notoken)
                }else{
                text
            }
        }
        preapitoken?.summaryProvider= Preference.SummaryProvider<EditTextPreference> {preference ->
            val text = preference.text
            if(TextUtils.isEmpty(text)){
                getString(R.string.notoken)
            }else{
                var output = ""
                if( text.length > 30){
                    for (i in 0..30){
                        output += text[i]
                    }
                    output
                }else{
                    getString(R.string.notoken)
                }
            }
        }
        preusername?.summaryProvider= Preference.SummaryProvider<EditTextPreference> {preference ->
            val text = preference.text
            if(TextUtils.isEmpty(text)){
                getString(R.string.notoken)
            }else{
                text
            }
        }


        prepasswd?.summaryProvider= Preference.SummaryProvider<EditTextPreference> {preference ->
            val text = preference.text
            if(TextUtils.isEmpty(text)){
                getString(R.string.notoken)
            }else{
                "*".repeat(text.length)
            }
        }

    }

    private fun getToken(){
        try{
            val protocol: String = if(apiprotocol){
                "https"
            }else{
                "http"
            }
            if( apipip == "" || apipip == "0.0.0.0"){
                return
            }
            if( username == "" || apipip == "-"){
                return
            }
            if( passwd == "" || apipip == "-"){
                return
            }

            val url = "$protocol://$apipip/genToken"
            println(url)
            val formBody: RequestBody = FormBody.Builder()
                .add("username", username)
                .add("password", passwd)
                .build()
            val request = Request.Builder()
                .url(url)
                .header("User-Agent", "HA-Tool Android")
                //.addHeader("Authorization", Credentials.basic(username, passwd))
                .post(formBody)
                .build()

            val client = OkHttpClient().newBuilder().build()

            client.newCall(request).enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    val gson = Gson()
                    val entity: AuthRepsonde =
                        gson.fromJson(response.body?.string(), AuthRepsonde::class.java)
                    updateToken(entity.getToken().toString())
                }
                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                }
            })
        }catch (e: Exception){
            e.stackTrace
        }

    }

    fun updateToken(value : String){
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val editor: SharedPreferences.Editor = prefs.edit()
        editor.putString("key_api_token", value)
        editor.apply()
    }
        private fun loadDatafromPreferences(){
        sharedPreference = PreferenceManager.getDefaultSharedPreferences(requireContext())
        apipip = sharedPreference.getString("key_api_ip", "")!!
        apitoken = sharedPreference.getString("key_api_token", "")!!
        passwd = sharedPreference.getString("passwd", "")!!
        username = sharedPreference.getString("username", "")!!
        getToken()
    }
}

