package com.lukaskraener.ha_analyse


import android.annotation.TargetApi
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
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
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.GROUP_ALERT_SUMMARY
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
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
    private  var pyip = ""
    private  var pyuser = ""
    private  var pypwd = ""
    private  var pyport = ""
    private var pyschema= ""
    private  var pyprozess = ""
    private var pyprogram = ""
    private lateinit var thiscontext: Context
    private val notficationsid= 223
    private val CHANEL_ID = "TEst_1234"


    //UI
    private  lateinit var btn_switch: FloatingActionButton
    private  lateinit var btn_uploader: ImageView
    private  lateinit var btn_search: ImageView
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
        pyuser = sharedPreference.getString("key_py_user", "")!!
        pypwd = sharedPreference.getString("key_py_pwd", "")!!
        pyip = sharedPreference.getString("key_py_ip", "")!!
        pyport = sharedPreference.getString("key_py_port", "")!!
        pyschema = sharedPreference.getString("key_py_bau", "")!!
        pyprogram = sharedPreference.getString("key_py_program", "")!!
        pyprozess = sharedPreference.getString("key_py_prozess", "")!!
    }

    private fun loadUI(){
        btn_switch= rootview.findViewById(R.id.btn_switch)
        btn_uploader = rootview.findViewById(R.id.btn_upload)
        btn_search = rootview.findViewById(R.id.btn_search)
        auswertung = rootview.findViewById(R.id.btn_auswertung)
        anzeige_oben = rootview.findViewById(R.id.tv_anzeige_oben)
        uploaderfertiganzeige = rootview.findViewById(R.id.tv_uploader_fertig)
        btn_calc = rootview.findViewById(R.id.btn_calc)
        tv_calc_fertig = rootview.findViewById(R.id.tv_calc_fertig)
        progressbarcircle = rootview.findViewById(R.id.progressBar)
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
        thiscontext = container!!.getContext();
        loadUI()

        btn_calc.setOnClickListener {
            API(anzeiges = tv_calc_fertig,tokens = apitoken,urls =apiwebadresse,context = thiscontext).programmstart(dbip = pyip, dbport = pyport,dbpwd = pypwd,dbschema = pyschema,dbuser = pyuser, py_programm = pyprogram,prozessanzahl = pyprozess)
        }

        btn_uploader.setOnClickListener {
            uploader(uploaderfertiganzeige,token = apitoken,ip = apiwebadresse, progressbarcircle =  progressbarcircle)
        }
        rootview.findViewById<TextView>(R.id.tv_hateam).setOnClickListener {
            val openURL = Intent(Intent.ACTION_VIEW)
            openURL.data = Uri.parse("http://hybridassistant.blogspot.com/p/about.html")
            startActivity(openURL)

        }
    println(pyprogram)
        auswertung.setOnClickListener {
            val openURL = Intent(Intent.ACTION_VIEW)
            openURL.data = Uri.parse(auswertungip)
            startActivity(openURL)

        }
        btn_search.setOnClickListener {
            val tst = API(apitoken,apiwebadresse, anzeige_oben, context = thiscontext)
                tst.reader()
        }

        btn_switch.setOnClickListener {
            findNavController().navigate(R.id.settingsFragment)
        }
        return rootview
    }//onview Close

    private fun uploader(anzeige: TextView, token:String, ip:String, progressbarcircle: ProgressBar){
        progressbarcircle.visibility= View.VISIBLE
        Thread(Runnable {
            try {
                API(tokens = token,urls = ip,anzeiges = anzeige, context = thiscontext).uploader( processbar = progressbarcircle)
            }catch(e: Exception) {
                println(e.printStackTrace())
            }
        }).start()
    }



    fun testbenahritigung(texts:String, wert : Int, max: Int){
        val intent = Intent(thiscontext,MainActivity::class.java).apply {
            flags= Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent: PendingIntent = PendingIntent.getActivity(thiscontext,0,intent,0)
        var text= texts
        var ongoing= true
        var pro= NotificationCompat.PRIORITY_DEFAULT
        if(wert == max){
            ongoing=false
            text= "Fertig"

            text= this.thiscontext.getString(R.string.finished)
            var pro= NotificationCompat.PRIORITY_HIGH
        }

        val builder = NotificationCompat.Builder(this.thiscontext,CHANEL_ID)
            .setOngoing(ongoing)
            .setSmallIcon(R.drawable.ic_baseline_cloud_upload_24)
            .setColor(ContextCompat.getColor(thiscontext, R.color.vektor))
            .setColorized(true)
            .setContentTitle(getString(R.string.app_name))
            .setContentText(text)
            .setProgress(max,wert,false)
            .setPriority(pro)
            .setGroupAlertBehavior(GROUP_ALERT_SUMMARY)
            .setGroup(getString(R.string.app_name))
            .setGroupSummary(false)
            .setStyle(NotificationCompat.BigTextStyle().bigText(text))
            .setContentIntent(pendingIntent)
            .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_logo))

        with(NotificationManagerCompat.from(this.thiscontext)){
            notify(notficationsid, builder.build())
        }
    }

}

