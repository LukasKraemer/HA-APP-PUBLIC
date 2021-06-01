package com.lukaskraener.ha_analyse

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
    private var preapiprotokoll:SwitchPreference? = null
    private var preapitoken:EditTextPreference? = null
    private var preapischuled:SwitchPreference? = null
    private var preauswertungip:EditTextPreference? = null
    private var preusername: EditTextPreference? = null
    private var prepasswd: EditTextPreference? = null

    //values:
    private  lateinit var sharedPreference: SharedPreferences
    private var apipip = ""
    private var apiprotokoll = true
    private var apitoken = ""
    private var username = ""
    private var passwd = ""




    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings, rootKey)

        preapiip= findPreference("key_api_ip")
        preapiprotokoll= findPreference("key_api_protokoll")
        preapitoken= findPreference("key_api_token")
        preauswertungip = findPreference("key_auswertung_url")
        preapischuled= findPreference(" api_scheduled")
        preusername = findPreference("username")
        prepasswd = findPreference("passwd")

        loadDatafromPreferences()
        getToken()
        preapiip?.setOnBindEditTextListener { editText ->
        editText.inputType = InputType.TYPE_CLASS_TEXT
        }
        preapitoken?.setOnBindEditTextListener { editText ->
            editText.inputType = InputType.TYPE_CLASS_TEXT
        }
        preauswertungip?.setOnBindEditTextListener { editText ->
        editText.inputType = InputType.TYPE_CLASS_TEXT
        }

        preusername?.setOnBindEditTextListener { editText ->
            editText.inputType = InputType.TYPE_CLASS_TEXT
        }
        prepasswd?.setOnBindEditTextListener { editText ->
            editText.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD or InputType.TYPE_CLASS_TEXT
        }

        //preapiip?.summaryProvider = EditTextPreference.SimpleSummaryProvider.getInstance()
        preauswertungip?.summaryProvider = EditTextPreference.SimpleSummaryProvider.getInstance()


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
            val protocol: String
            if(apiprotokoll){
                protocol = "https"
            }else{
                protocol = "http"
            }

            val formBody: RequestBody = FormBody.Builder()
                .add("username", username)
                .add("password", passwd)
                .build()
            val request = Request.Builder()
                .url("$protocol://$apipip/genToken")
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
    }
}

