package com.lukaskraener.ha_analyse

import android.annotation.TargetApi
import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.net.URL


class MainActivity : AppCompatActivity() {
    protected fun shouldAskPermissions(): Boolean {
        return Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1
    }

    @TargetApi(23)
    protected fun askPermissions() {
        val permissions = arrayOf(
            "android.permission.ACCESS_NETWORK_STATE",
            "android.permission.INTERNET",
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE"


        )
        val requestCode = 200
        requestPermissions(permissions, requestCode)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (shouldAskPermissions()) {
            askPermissions()
        }
        val btn_uploader = findViewById(R.id.btn_upload) as Button
        val btn_search = findViewById(R.id.btn_search) as Button
        val upload_numername = findViewById(R.id.input_nutzername) as EditText
        val upload_ip = findViewById(R.id.input_ip) as EditText
        val upload_passwort = findViewById(R.id.input_passwort) as EditText
        val auswertung = findViewById(R.id.btn_auswertung) as Button
        val uploaderfertiganzeige = findViewById<TextView>(R.id.tv_uplaoder_fertig)
        val anzeige_oben = findViewById<TextView>(R.id.tv_sache_res)

        btn_uploader.setOnClickListener() {
            uploaderfertiganzeige.text = "gestartet"

            try{
                uploaderfertiganzeige.text = MultipartFileUploader.uploader().toString()
                //uploaderfertiganzeige.text= ftp_uplaoder(upload_ip)

            }
            catch (e: Exception){
                uploaderfertiganzeige.text= "fehler"
            }


        }
        auswertung.setOnClickListener {
            //wb1.loadUrl("https://google.com")
        }
        btn_search.setOnClickListener {
            anzeige_oben.text = read_files.reader().toString()

        }
    }

        private fun ftp_uplaoder(
            adress: EditText

        ): String {

            val ip: String = adress.text.toString()


            var fehler: Int =0
            return try {
                var adresse = URL("http://192.168.178.76/test.php")
                fehler =1
                var http = fileuploader(adresse)
                fehler=2
                http.addFormField("name", "Lukas")
                fehler=3
                http.addHeaderField("vornname","krämer")
                fehler=4
                http.upload(null)
                fehler=5
                //smb.test(user, pass,ip)
                "erfolgreich"
            } catch (e: Exception) {
                return fehler.toString()
            }


        }

    }



