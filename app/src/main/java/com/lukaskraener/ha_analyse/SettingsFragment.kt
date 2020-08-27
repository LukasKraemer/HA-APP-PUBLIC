package com.lukaskraener.ha_analyse

import android.content.SharedPreferences
import android.os.Bundle
import android.text.InputType
import android.text.TextUtils
import androidx.preference.*


class SettingsFragment : PreferenceFragmentCompat() {

    //userdata
    private  var preapiip:EditTextPreference? = null
    private  var preapiprotokoll:SwitchPreference? = null
    private  var preapitoken:EditTextPreference? = null
    private  var preauswertungip:EditTextPreference? = null


    //values:
    private  lateinit var sharedPreference: SharedPreferences
    private var apipip = ""
    private var apiprotokoll = ""
    private var apitoken = ""
    private var auswertungip = ""




    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings, rootKey)

        preapiip= findPreference("key_api_ip")
        preapiprotokoll= findPreference("key_api_protokoll")
        preapitoken= findPreference("key_api_token")
        preauswertungip = findPreference("key_auswertung_url")

        loadDatafromPreferences()

        preapiip?.setOnBindEditTextListener { editText ->
        editText.inputType = InputType.TYPE_CLASS_TEXT
        }
        preapitoken?.setOnBindEditTextListener { editText ->
            editText.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD or InputType.TYPE_CLASS_TEXT
        }
        preauswertungip?.setOnBindEditTextListener { editText ->
        editText.inputType = InputType.TYPE_CLASS_TEXT
        }

        //preapiip?.summaryProvider = EditTextPreference.SimpleSummaryProvider.getInstance()
        preauswertungip?.summaryProvider = EditTextPreference.SimpleSummaryProvider.getInstance()


        preapiip?.summaryProvider = Preference.SummaryProvider<EditTextPreference>{ preference->
            val text = preference.text.toString()
            val regexip = "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)(\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)){3}/[a-zA-Z0-9/_+-.]*".toRegex()
            val regexhostname = "^(([a-zA-Z0-9]|[a-zA-Z0-9][a-zA-Z0-9\\-]*[a-zA-Z0-9])\\.)*([A-Za-z0-9]|[A-Za-z0-9][A-Za-z0-9\\-]*[A-Za-z0-9])/[a-zA-Z0-9/_+-.]*".toRegex()
            if(regexip.matches(text)|| regexhostname.matches(text)){
                text
            } else{
                "Synaktisch falsche Eingabe"
            }
        }

        preapitoken?.summaryProvider= Preference.SummaryProvider<EditTextPreference> {preference ->
            val text = preference.text
            if(TextUtils.isEmpty(text)){
                "Kein Token wurde gesetzt"
            }else{
                 "*".repeat(text.length)
            }

        }

    }
        private fun loadDatafromPreferences(){
        sharedPreference = PreferenceManager.getDefaultSharedPreferences(requireContext())
        apipip = sharedPreference.getString("key_api_ip", "")!!
        apitoken = sharedPreference.getString("key_api_token", "")!!
        apiprotokoll = sharedPreference.getBoolean("key_api_protokoll", true).toString()
        auswertungip= sharedPreference.getString("key_auswertung_url", "")!!
}
}