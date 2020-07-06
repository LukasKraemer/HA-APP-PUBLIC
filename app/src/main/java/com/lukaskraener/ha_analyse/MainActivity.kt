package com.lukaskraener.ha_analyse

import android.Manifest
import android.annotation.TargetApi
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

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
        var internet: String = ""
        if(ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            internet = "true"
        }else{
            internet = "false"
    }



        btn_uploader.setOnClickListener() {
            uploaderfertiganzeige.text = "gestartet"

            try{
                //uploaderfertiganzeige.text = MultipartFileUploader.uploader().toString()
                uploaderfertiganzeige.text= ftp_uplaoder(upload_ip,upload_numername,upload_passwort)
            }
            catch (e: Exception){
                uploaderfertiganzeige.text= "fehler"
            }
        }
        auswertung.setOnClickListener {
            anzeige_oben.text = "internet: "+internet
            val openURL = Intent(Intent.ACTION_VIEW)
            openURL.data = Uri.parse("https://yarix.ddns.net")
            startActivity(openURL)
        }
        btn_search.setOnClickListener {
            anzeige_oben.text = read_files.reader().toString()
        }
    }

        private fun ftp_uplaoder(
            adress: EditText,
            user: EditText,
            pass: EditText

        ): String {
            try {
                val uploader = FTPUploader(
                    adress.toString(),
                    user.toString(),
                    pass.toString())
                //uploader.connect()
                //uploader.uploadFile("test", "geg", "/")
                //uploader.disconnect()
                return "Erfolgreich"
            }catch (e: Exception){
                return "Fehler"
            }
        }

    }



