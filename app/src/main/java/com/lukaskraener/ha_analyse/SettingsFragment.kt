package com.lukaskraener.ha_analyse

import android.content.SharedPreferences
import android.os.Bundle
import android.text.InputType
import android.text.TextUtils
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager


class SettingsFragment : PreferenceFragmentCompat() {

    //userdata
    private  var preftpuser:EditTextPreference? = null
    private  var preftppwd:EditTextPreference? = null
    private  var preftpip:EditTextPreference? = null
    private  var preftpport:EditTextPreference? = null
    private  var preauswertungip:EditTextPreference? = null
    private  var prepyip:EditTextPreference? = null
    private  var prepyuser:EditTextPreference? = null
    private  var prepypwd:EditTextPreference? = null
    private  var prepyport:EditTextPreference? = null
    private  var prepyprozess:EditTextPreference? = null
    private  var prepyprogram:EditTextPreference? = null

    //values:
    private  lateinit var sharedPreference: SharedPreferences
    private var ftpuser = ""
    private var ftppwd = ""
    private var ftpip = ""
    private var ftpport = ""
    private var auswertungip = ""
    private  var pyip = ""
    private  var pyuser = ""
    private  var pypwd = ""
    private  var pyport = ""
    private  var pyprozess = ""
    private var pyprogram = ""



    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings, rootKey)

        preftpuser= findPreference("key_ftp_nutzername")
        preftppwd= findPreference("key_ftp_passwort")
        preftpip= findPreference("key_ftp_ip")
        preftpport= findPreference("key_ftp_port")
        preauswertungip = findPreference("key_auswertung_url")

        prepyuser =findPreference("key_py_user")
        prepyip = findPreference("key_py_ip")
        prepypwd = findPreference("key_py_pwd")
        prepyport = findPreference("key_py_port")
        prepyprozess = findPreference("key_py_prozess")
        prepyprogram = findPreference("key_py_program")

        loadDatafromPreferences()

        preftpuser?.setOnBindEditTextListener { editText ->
        editText.inputType = InputType.TYPE_CLASS_TEXT
        }
        preftppwd?.setOnBindEditTextListener { editText ->
        editText.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD or InputType.TYPE_CLASS_TEXT
        }
        preftpport?.setOnBindEditTextListener { editText ->
        editText.inputType = InputType.TYPE_CLASS_NUMBER
        }
        preftpip?.setOnBindEditTextListener { editText ->
        editText.inputType = InputType.TYPE_CLASS_TEXT
        }
        preauswertungip?.setOnBindEditTextListener { editText ->
        editText.inputType = InputType.TYPE_CLASS_TEXT
        }

        prepyuser?.setOnBindEditTextListener { editText ->
        editText.inputType = InputType.TYPE_CLASS_TEXT
        }
        prepypwd?.setOnBindEditTextListener { editText ->
        editText.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD or InputType.TYPE_CLASS_TEXT
        }
        prepyip?.setOnBindEditTextListener { editText ->
        editText.inputType = InputType.TYPE_CLASS_TEXT
        }
        prepyport?.setOnBindEditTextListener { editText ->
        editText.inputType = InputType.TYPE_CLASS_NUMBER
        }
        prepyprogram?.setOnBindEditTextListener { editText ->
        editText.inputType = InputType.TYPE_CLASS_TEXT
        }
        prepyprozess?.setOnBindEditTextListener { editText ->
        editText.inputType = InputType.TYPE_CLASS_NUMBER
        }

        preftpuser?.summaryProvider = EditTextPreference.SimpleSummaryProvider.getInstance()
        preftpport?.summaryProvider = EditTextPreference.SimpleSummaryProvider.getInstance()
        preftpip?.summaryProvider = EditTextPreference.SimpleSummaryProvider.getInstance()
        preauswertungip?.summaryProvider = EditTextPreference.SimpleSummaryProvider.getInstance()
        preftppwd?.summaryProvider= Preference.SummaryProvider<EditTextPreference> {preference ->
        val text = preference.text
        if(TextUtils.isEmpty(text)){
          "Kein Passwoort wurde gesetzt"
        }else{
          val value = "*".repeat(text.length)
          value
        }

        }

        prepyuser?.summaryProvider = EditTextPreference.SimpleSummaryProvider.getInstance()
        prepypwd?.summaryProvider = Preference.SummaryProvider<EditTextPreference> {preference ->
            val text = preference.text
            if(TextUtils.isEmpty(text)){
                "Kein Passwoort wurde gesetzt"
            }else{
                val value = "*".repeat(text.length)
                value
            }

        }
        prepyip?.summaryProvider = EditTextPreference.SimpleSummaryProvider.getInstance()
        prepyport?.summaryProvider = EditTextPreference.SimpleSummaryProvider.getInstance()
        prepyprogram?.summaryProvider = EditTextPreference.SimpleSummaryProvider.getInstance()
        prepyprozess?.summaryProvider = EditTextPreference.SimpleSummaryProvider.getInstance()

    }

        private fun loadDatafromPreferences(){
        sharedPreference = PreferenceManager.getDefaultSharedPreferences(requireContext())
        ftpuser = sharedPreference.getString("key_ftp_nutzername", "")!!
        ftppwd = sharedPreference.getString("key_ftp_passwort", "")!!
        ftpip = sharedPreference.getString("key_ftp_ip", "")!!
        ftpport = sharedPreference.getString("key_ftp_port", "")!!
        auswertungip= sharedPreference.getString("key_auswertung_url", "")!!

        pyuser = sharedPreference.getString("key_py_user", "")!!
        pypwd = sharedPreference.getString("key_py_pwd", "")!!
        pyip = sharedPreference.getString("key_py_ip", "")!!
        pyport = sharedPreference.getString("key_py_port", "")!!

        pyprogram = sharedPreference.getString("key_py_program", "")!!
        pyprozess = sharedPreference.getString("key_py_prozess", "")!!


}
}