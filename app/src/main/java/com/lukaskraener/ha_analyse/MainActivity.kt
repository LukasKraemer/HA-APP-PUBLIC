package com.lukaskraener.ha_analyse

import android.os.Bundle
import android.provider.Telephony.Carriers.PORT
import android.webkit.WebView
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import it.sauronsoftware.ftp4j.FTPClient
import java.io.File


class MainActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btn_uploader = findViewById(R.id.btn_upload) as Button
        val btn_search = findViewById(R.id.btn_search) as Button
        val ftp_numername = findViewById(R.id.input_nutzername) as EditText
        val ftp_ip = findViewById(R.id.input_ip) as EditText
        val ftp_passwort = findViewById(R.id.input_passwort) as EditText
        val auswertung = findViewById(R.id.btn_auswertung) as Button
        val webview =findViewById(R.id.wb1) as WebView
        val uploaderfertiganzeige = findViewById<TextView>(R.id.tv_uplaoder_fertig)

        btn_uploader.setOnClickListener() {
            uploaderfertiganzeige.text = "gestartet"
            val meldung: String= ftp_uplaoder(ftp_ip, ftp_numername, ftp_passwort)
            uploaderfertiganzeige.text = meldung

        }
        auswertung.setOnClickListener{
            webview.loadUrl("https://yarix.ddns.net")
        }
        btn_search.setOnClickListener{

        }

    }

    private fun ftp_uplaoder( adress: EditText, nutzername: EditText, passwort: EditText): String {


        val user: String = nutzername.text.toString()
        val pass: String = passwort.text.toString()
        val ip: String = adress.text.toString()


        try {
            val mFtpClient = FTPClient()
            mFtpClient.connect(ip, 22)
            mFtpClient.login(user, pass)
            mFtpClient.type = FTPClient.TYPE_BINARY
            //mFtpClient.changeDirectory("/directory_path/")

            //mFtpClient.upload(File("file_path"))
            mFtpClient.disconnect(true)
            return ("erfolgreich")

        } catch (e: Exception) {
            return ("Fehler")
        }



    }

}


