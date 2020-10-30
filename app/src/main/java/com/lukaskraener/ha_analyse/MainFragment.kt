package com.lukaskraener.ha_analyse


import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import com.google.android.material.floatingactionbutton.FloatingActionButton


class MainFragment : Fragment() {
    private lateinit var rootview:View
    private  lateinit var sharedPreference: SharedPreferences

    //Settings
    private var apipip = ""
    private var apiprotokoll = ""
    private var apiwebadresse = ""
    private var apitoken = ""
    private var auswertungip = ""
    private lateinit var thiscontext: Context


    //UI
    private  lateinit var btnSwitch: FloatingActionButton
    private  lateinit var btnUploader: ImageView
    private  lateinit var btnSearch: ImageView
    private  lateinit var auswertung: Button
    private  lateinit var anzeige_oben: TextView
    private  lateinit var btn_calc: ImageView
    private  lateinit var  tv_calc_fertig: TextView
    private lateinit var uploaderfertiganzeige: TextView
    private  lateinit var progressbarcircle: ProgressBar


    private fun loadDatafromPreferences(){
        sharedPreference = PreferenceManager.getDefaultSharedPreferences(requireContext())
        apipip = sharedPreference.getString("key_api_ip", "")!!
        apitoken = sharedPreference.getString("key_api_token", "")!!
        if(sharedPreference.getBoolean("key_api_protokoll", true)) {
            apiprotokoll = "https://"
        }else{ apiprotokoll="http://"
        }
        apiwebadresse= apiprotokoll+apipip
        auswertungip= sharedPreference.getString("key_auswertung_url", "")!!
    }

    private fun loadUI(){
        btnSwitch= rootview.findViewById(R.id.btn_switch)
        btnUploader = rootview.findViewById(R.id.btn_upload)
        btnSearch = rootview.findViewById(R.id.btn_search)
        auswertung = rootview.findViewById(R.id.btn_auswertung)
        anzeige_oben = rootview.findViewById(R.id.tv_anzeige_oben)
        uploaderfertiganzeige = rootview.findViewById(R.id.tv_uploader_fertig)
        btn_calc = rootview.findViewById(R.id.btn_calc)
        tv_calc_fertig = rootview.findViewById(R.id.tv_calc_fertig)
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
        thiscontext = container!!.context
        loadUI()

        btn_calc.setOnClickListener {
            API(anzeiges = tv_calc_fertig,tokens = apitoken,urls =apiwebadresse,context = thiscontext).programmstart()
        }

        btnUploader.setOnClickListener {
            uploader(uploaderfertiganzeige,token = apitoken,ip = apiwebadresse)
        }
        rootview.findViewById<TextView>(R.id.tv_hateam).setOnClickListener {
            val openURL = Intent(Intent.ACTION_VIEW)
            openURL.data = Uri.parse("http://hybridassistant.blogspot.com/p/about.html")
            startActivity(openURL)

        }
        auswertung.setOnClickListener {

            val url: String
            url = if (Data_validiator().isValidURL(auswertungip)){
                auswertungip
            }else{
                "https://"+auswertungip
            }
            val openURL = Intent(Intent.ACTION_VIEW)
            openURL.data = Uri.parse(url)
            startActivity(openURL)

        }
        btnSearch.setOnClickListener {
            API(apitoken,apiwebadresse, anzeige_oben, context = thiscontext).reader()
        }

        btnSwitch.setOnClickListener {
            findNavController().navigate(R.id.settingsFragment)
        }
        return rootview
    }//onview Close

    private fun uploader(display: TextView, token:String, ip:String){
        Thread(Runnable {
            try {
                API(tokens = token,urls = ip,anzeiges = display, context = thiscontext).uploader()
            }catch(e: Exception) {
                println(e.printStackTrace())
            }
        }).start()
    }




}

