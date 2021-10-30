package de.lukaskraener.ha_analyse


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

    //UI
    private lateinit var btnSwitch: FloatingActionButton
    private lateinit var btnUploader: ImageView
    private lateinit var btnFileCompare: ImageView
    private lateinit var btnWebapp: Button
    private lateinit var tvFileCompare: TextView
    private lateinit var btnCalc: ImageView
    private lateinit var tvCalc: TextView
    private lateinit var tvUploader: TextView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        rootview = inflater.inflate(R.layout.fragment_main, container, false)
        loadUI()
        initializeButtonListener(container!!.context)
        return rootview
    }

    private fun loadUI(){
        btnSwitch= rootview.findViewById(R.id.btn_switch)
        btnUploader = rootview.findViewById(R.id.btn_upload)
        btnFileCompare = rootview.findViewById(R.id.btn_search)
        btnWebapp = rootview.findViewById(R.id.btn_overview)
        tvFileCompare = rootview.findViewById(R.id.tv_anzeige_oben)
        tvUploader = rootview.findViewById(R.id.tv_uploader_fertig)
        btnCalc = rootview.findViewById(R.id.btn_calc)
        tvCalc = rootview.findViewById(R.id.tv_calc_fertig)
    }

    private fun initializeButtonListener(context: Context){
        btnCalc.setOnClickListener {
            tvCalc.setText(R.string.started)
            API(tvCalc, context).startProgram()
        }

        btnUploader.setOnClickListener {
            Thread {API(tvUploader,context).uploader() }.start()
        }
        rootview.findViewById<TextView>(R.id.tv_hateam).setOnClickListener {
            val openURL = Intent(Intent.ACTION_VIEW)
            openURL.data = Uri.parse("http://hybridassistant.blogspot.com/p/about.html")
            startActivity(openURL)
        }
        btnWebapp.setOnClickListener {
            val apiIp= PreferenceManager.getDefaultSharedPreferences(context).getString("key_overview_url", "")!!
            val url: String = if (DataValidiator.isValidURL(apiIp)){ apiIp }else{ "https://$apiIp" }

            val openURL = Intent(Intent.ACTION_VIEW)
            openURL.data = Uri.parse(url)
            startActivity(openURL)
        }
        btnFileCompare.setOnClickListener {
            val data = FileManager(context)
            data.generateTXTFiles()
            API( tvFileCompare, context).reader()
        }

        btnSwitch.setOnClickListener {
            findNavController().navigate(R.id.settingsFragment)
        }
    }
}

