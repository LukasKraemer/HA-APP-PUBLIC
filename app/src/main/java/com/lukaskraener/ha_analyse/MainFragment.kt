package com.lukaskraener.ha_analyse


import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
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


class MainFragment : Fragment() {
    private lateinit var rootview:View

    //Settings
    private lateinit var auswertungip :String
    private lateinit var thiscontext: Context

    //UI
    private  lateinit var btnSwitch: FloatingActionButton
    private  lateinit var btnUploader: ImageView
    private  lateinit var btnSearch: ImageView
    private  lateinit var auswertung: Button
    private  lateinit var anzeige_oben: TextView
    private  lateinit var btn_calc: ImageView
    private  lateinit var tv_calc_fertig: TextView
    private lateinit var uploaderfertiganzeige: TextView


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

    private fun askPermissions() {
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
    ): View {
        askPermissions()

        rootview = inflater.inflate(R.layout.fragment_main, container, false)
        thiscontext = container!!.context
        loadUI()
        this.auswertungip= PreferenceManager.getDefaultSharedPreferences(context).getString("key_auswertung_url", "")!!


        btn_calc.setOnClickListener {
            tv_calc_fertig.setText(R.string.started)
            API(
                anzeiges = tv_calc_fertig,
                context = thiscontext
            ).programmstart()
        }

        btnUploader.setOnClickListener {
            uploader(uploaderfertiganzeige)
        }
        rootview.findViewById<TextView>(R.id.tv_hateam).setOnClickListener {
            val openURL = Intent(Intent.ACTION_VIEW)
            openURL.data = Uri.parse("http://hybridassistant.blogspot.com/p/about.html")
            startActivity(openURL)

        }
        auswertung.setOnClickListener {

            val url: String = if (DataValidiator.isValidURL(auswertungip)){
                auswertungip
            }else{
                "https://"+auswertungip
            }
            val openURL = Intent(Intent.ACTION_VIEW)
            openURL.data = Uri.parse(url)
            startActivity(openURL)

        }
        btnSearch.setOnClickListener {
            val data = FileManager(thiscontext)
            data.generateTXTFiles()
            API( anzeige_oben, context = thiscontext).reader()
        }

        btnSwitch.setOnClickListener {
            findNavController().navigate(R.id.settingsFragment)
        }
        return rootview
    }

    private fun uploader(display: TextView){
        Thread {
            try {
                API(anzeiges = display, context = thiscontext).uploader()
            } catch (e: Exception) {
                println(e.printStackTrace())
            }
        }.start()
    }

}

