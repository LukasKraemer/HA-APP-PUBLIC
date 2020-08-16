package com.lukaskraener.ha_analyse


import android.annotation.TargetApi
import android.app.PendingIntent
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
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.GROUP_ALERT_SUMMARY
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import com.google.android.material.floatingactionbutton.FloatingActionButton


class MainFragment : Fragment() {
    private lateinit var rootview:View
    private  lateinit var sharedPreference: SharedPreferences

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

        val btn_switch: FloatingActionButton = rootview.findViewById(R.id.btn_switch)

        val btn_uploader = rootview.findViewById(R.id.btn_upload) as ImageView
        val btn_search = rootview.findViewById(R.id.btn_search) as ImageView
        val auswertung = rootview.findViewById(R.id.btn_auswertung) as Button
        val anzeige_oben = rootview.findViewById<TextView>(R.id.tv_anzeige_oben)
        uploaderfertiganzeige = rootview.findViewById(R.id.tv_uploader_fertig)
        val btn_calc: ImageView = rootview.findViewById(R.id.btn_calc)
        val tv_calc_fertig: TextView = rootview.findViewById(R.id.tv_calc_fertig)
        progressbarcircle = rootview.findViewById<ProgressBar>(R.id.progressBar)


        btn_calc.setOnClickListener {
            API().programmstart(anzeige = tv_calc_fertig,token = apitoken,url =apiwebadresse, dbip = pyip, dbport = pyport,dbpwd = pypwd,dbschema = pyschema,dbuser = pyuser, programm = pyprogram,prozessanzahl = pyprozess)
            //tv_calc_fertig.text = "gestartet"
        }

        btn_uploader.setOnClickListener {
            uploader(uploaderfertiganzeige,token = apitoken,ip = apiwebadresse, progressbarcircle =  progressbarcircle)

        }

        auswertung.setOnClickListener {
            val openURL = Intent(Intent.ACTION_VIEW)
            openURL.data = Uri.parse(auswertungip)
            startActivity(openURL)

        }
        btn_search.setOnClickListener {
            filereader(anzeige_oben,apipip,apitoken)
        }

        btn_switch.setOnClickListener {
            findNavController().navigate(R.id.settingsFragment)
        }
        return rootview
    }//onview Close

    private fun uploader(anzeige: TextView, token:String, ip:String, progressbarcircle: ProgressBar){
    //anzeige.text = "gestartet"
        progressbarcircle.visibility= View.VISIBLE
    Thread(Runnable {
        // a potentially time consuming task
        try {

            API().uploader(token = token,url = ip,anzeige = anzeige, processbar = progressbarcircle)


        }catch(e: Exception) {
            println(e.printStackTrace())
        }
    }).start()
    }

    private fun filereader(anzeige : TextView, ip: String, token :String){
        API().reader(token,apiwebadresse, anzeige)
        for (i in 1..100){
            testbenahritigung("Dateiupload auf Server %d",i,100)
            Thread.sleep(100)
        }
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
            var pro= NotificationCompat.PRIORITY_HIGH
        }

        val builder = NotificationCompat.Builder(this.thiscontext,CHANEL_ID)
            .setOngoing(ongoing)
            .setSmallIcon(R.drawable.ic_logo)
            .setContentTitle("HA-Tool")
            .setContentText(text)
            .setProgress(max,wert,false)
            .setPriority(pro)
            .setGroupAlertBehavior(GROUP_ALERT_SUMMARY)
            .setGroup("HA-Tool")
            .setGroupSummary(false)
            .setStyle(NotificationCompat.BigTextStyle().bigText(text))
            .setContentIntent(pendingIntent)


        with(NotificationManagerCompat.from(this.thiscontext)){
            notify(notficationsid, builder.build())
        }
    }

}

