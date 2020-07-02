package com.lukaskraener.ha_analyse

import android.annotation.TargetApi
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (shouldAskPermissions()) {
            askPermissions()
        }
        val btn_uploader = findViewById(R.id.btn_upload) as Button
        val btn_search = findViewById(R.id.btn_search) as Button
        val ftp_numername = findViewById(R.id.input_nutzername) as EditText
        val ftp_ip = findViewById(R.id.input_ip) as EditText
        val ftp_passwort = findViewById(R.id.input_passwort) as EditText
        val auswertung = findViewById(R.id.btn_auswertung) as Button
        val uploaderfertiganzeige = findViewById<TextView>(R.id.tv_uplaoder_fertig)
        var anzeige_oben = findViewById<TextView>(R.id.tv_sache_res)

        btn_uploader.setOnClickListener() {
            uploaderfertiganzeige.text = "gestartet"
            val meldung: String = ftp_uplaoder(ftp_ip, ftp_numername, ftp_passwort)
            uploaderfertiganzeige.text = meldung

        }
        auswertung.setOnClickListener {
            //webview.loadUrl("https://yarix.ddns.net")
        }
        btn_search.setOnClickListener {
            anzeige_oben.text = read_files.reader().toString()

        }
    }

        private fun ftp_uplaoder(
            adress: EditText,
            nutzername: EditText,
            passwort: EditText
        ): String {


            val user: String = nutzername.text.toString()
            val pass: String = passwort.text.toString()
            val ip: String = adress.text.toString()


            return try {
                smb.reader(user, pass, ip)
                ("erfolgreich")

            } catch (e: Exception) {
                ("Fehler")
            }


        }

    }



