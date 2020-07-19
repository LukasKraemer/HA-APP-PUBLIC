package com.lukaskraener.ha_analyse


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.navigation.fragment.findNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response


class MainFragment : Fragment() {
    private lateinit var rootview:View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        rootview= inflater.inflate(R.layout.fragment_main, container, false)
        val btn_switch: FloatingActionButton= rootview.findViewById(R.id.btn_switch)


        val btn_uploader = rootview.findViewById(R.id.btn_upload) as ImageView
        val btn_search = rootview.findViewById(R.id.btn_search) as ImageView
        val auswertung = rootview.findViewById(R.id.btn_auswertung) as Button
        val anzeige_oben = rootview.findViewById<TextView>(R.id.tv_anzeige_oben)
        val uploaderfertiganzeige: TextView = rootview.findViewById(R.id.tv_uploader_fertig)
        val btn_calc: ImageView = rootview.findViewById(R.id.btn_calc)
        val tv_calc_fertig: TextView = rootview.findViewById(R.id.tv_calc_fertig)

        btn_calc.setOnClickListener{
            tv_calc_fertig.text = "gestartet"
            val client: OkHttpClient = OkHttpClient()
            val url:String = "https://google.com"
            val request: Request  = Request.Builder()
                .url(url)
                .build()

        }

        btn_uploader.setOnClickListener {
            //wenn der Knopf uploader gedrückt wurde
            uploaderfertiganzeige.text = "gestartet"

            val upload_ip= ""
            val upload_numername= ""
            val upload_passwort = """"""

            try {
                uploaderfertiganzeige.text = ftp_uplaoder(upload_ip, upload_numername, upload_passwort)
            } catch (e: Exception) {
                uploaderfertiganzeige.text = "fehler"
            }
        }

        auswertung.setOnClickListener {
            //knoof unten "zur Auswertung"
            val openURL = Intent(Intent.ACTION_VIEW)
            openURL.data = Uri.parse("https://yarix.ddns.net")
            startActivity(openURL)
        }
        btn_search.setOnClickListener {

            anzeige_oben.text = read_files.reader()
        }

        btn_switch.setOnClickListener {
            try {
                findNavController().navigate(R.id.settingsFragment)
            }catch (e: java.lang.Exception){
                Log.d("3","onCreateView: "+ e.message)
            }

        }
        return rootview
    }

    private fun ftp_uplaoder(
        adress: String,
        user: String,
        pass: String,
        port: Int = 21

    ): String {
        return try {
            val uploader = FTPUploader(
                adress,
                user,
                pass,
                port
            )
            uploader.connect()
            //uploader.uploadFile("test", "geg", "/")
            uploader.disconnect()
            "Erfolgreich"
        } catch (e: Exception) {
            "Fehler"
        }

    }

}