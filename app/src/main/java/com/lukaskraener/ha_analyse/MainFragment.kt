package com.lukaskraener.ha_analyse


import android.annotation.TargetApi
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.File
import java.lang.Exception


class MainFragment : Fragment() {
    private lateinit var rootview:View
    private  lateinit var sharedPreference: SharedPreferences
    private var ftpuser = ""
    private var ftppwd = ""
    private var ftpip = ""
    private var ftpport = ""
    private var ftptoken = ""
    private var auswertungip = ""
    private  var pyip = ""
    private  var pyuser = ""
    private  var pypwd = ""
    private  var pyport = ""
    private  var pyprozess = ""
    private var pyprogram = ""

    private fun loadDatafromPreferences(){
        sharedPreference = PreferenceManager.getDefaultSharedPreferences(requireContext())
        ftpuser = sharedPreference.getString("key_ftp_nutzername", "")!!
        ftppwd = sharedPreference.getString("key_ftp_passwort", "")!!
        ftpip = sharedPreference.getString("key_ftp_ip", "")!!
        ftpport = sharedPreference.getString("key_ftp_port", "")!!
        ftptoken = sharedPreference.getString("key_ftp_token", "")!!
        auswertungip= sharedPreference.getString("key_auswertung_url", "")!!
        pyuser = sharedPreference.getString("key_py_user", "")!!
        pypwd = sharedPreference.getString("key_py_pwd", "")!!
        pyip = sharedPreference.getString("key_py_ip", "")!!
        pyport = sharedPreference.getString("key_py_port", "")!!
        pyprogram = sharedPreference.getString("key_py_program", "")!!
        pyprozess = sharedPreference.getString("key_py_prozess", "")!!
    }
    protected fun shouldAskPermissions(): Boolean {
        return Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1
    }

    @TargetApi(23)
    protected fun askPermissions() {
        val permissions = arrayOf(
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE"
        )
        val requestCode = 200
        requestPermissions(permissions, requestCode)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        if (shouldAskPermissions()) {
            askPermissions()
        }
        rootview = inflater.inflate(R.layout.fragment_main, container, false)
        loadDatafromPreferences()

        val btn_switch: FloatingActionButton = rootview.findViewById(R.id.btn_switch)

        val btn_uploader = rootview.findViewById(R.id.btn_upload) as ImageView
        val btn_search = rootview.findViewById(R.id.btn_search) as ImageView
        val auswertung = rootview.findViewById(R.id.btn_auswertung) as Button
        val anzeige_oben = rootview.findViewById<TextView>(R.id.tv_anzeige_oben)
        val uploaderfertiganzeige: TextView = rootview.findViewById(R.id.tv_uploader_fertig)
        val btn_calc: ImageView = rootview.findViewById(R.id.btn_calc)
        val tv_calc_fertig: TextView = rootview.findViewById(R.id.tv_calc_fertig)


        btn_calc.setOnClickListener {
            tv_calc_fertig.text = "gestartet"
        }

        btn_uploader.setOnClickListener() {
            //wenn der Knopf uploader gedrückt wurde
            uploaderfertiganzeige.text = "gestartet"

            uploaderfertiganzeige.text= uploader(ftptoken,ftpip)

        }

        auswertung.setOnClickListener {
            val openURL = Intent(Intent.ACTION_VIEW)
            openURL.data = Uri.parse("https://yarix.ddns.net")
            startActivity(openURL)
        }
        btn_search.setOnClickListener {
            filereader(anzeige_oben)
        }

        btn_switch.setOnClickListener {
            findNavController().navigate(R.id.settingsFragment)
        }
        return rootview
    }//onview Close

    private fun uploader(token:String, ip:String): String {
        try {
            println(1)
            var path =
                Environment.getExternalStorageDirectory().toString() + "/test/test.txt"
            println(2)
            val directory = File(path)
            println(3)
            var files = directory.isFile
            println(4)
            API().uploader(ftpip, Environment.getExternalStorageDirectory().toString() + "/test/test.txt")
            println(5)
            var anzahl=0
            println(6)
            println(9)
            return files.toString()
        }catch (e: Exception){
            println(10)
            println(e.message)
            return "fehler"
        }




    }
    private fun filereader(anzeige : TextView){
        var speicher = Readfiles().reader()
        //var datenbank = API().reader(ftptoken,ftpip)
        var datenbank=1000
        val diff= datenbank- speicher

        anzeige.text= "Speicher: "+ speicher.toString()+ "\nDB: "+datenbank.toString()+ "\nDifferenz: "+diff.toString()
    }


}

