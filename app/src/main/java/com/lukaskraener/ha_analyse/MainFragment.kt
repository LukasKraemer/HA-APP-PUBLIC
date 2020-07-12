package com.lukaskraener.ha_analyse


import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.File


class MainFragment : Fragment() {
    private lateinit var rootview:View
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


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
        btn_uploader.setOnClickListener {

            uploaderfertiganzeige.text = "gestartet"
            try {
                var ftp= ftp_uplaoder(this.ftpip, this.ftpuser, this.ftppwd, port = this.ftpport.toInt())
                uploaderfertiganzeige.text =ftp
            } catch (e: Exception) {
                uploaderfertiganzeige.text = e.message
            }
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

    private fun ftp_uplaoder(
        adress: String, user: String, pwd: String, port: Int = 21): String {
        return try {
            val uploader = FTPUploader(adress,user, pwd, port)
            if (uploader.connect()){
                //uploader.uploadFile("test", "geg", "/")
                //uploader.disconnect()
                "connect" }else{ "nicht connectet"}
        } catch (e: Exception) {
            e.message.toString()
        }
    }
    private fun filereader(anzeige:TextView ): Array<File>? {
        var ausgabe =""
        var anzahl = 0
        val files = read_files.reader()
        if (files != null) {if (files.size > 0) {for (i in 1 until files.size) {ausgabe += files[i].name
                    anzahl += 1 } }
            anzeige.text= "Anzahl $anzahl\n $ausgabe"} else {anzeige.text="keine Dateien gefunden" }
        return files
    }
}

