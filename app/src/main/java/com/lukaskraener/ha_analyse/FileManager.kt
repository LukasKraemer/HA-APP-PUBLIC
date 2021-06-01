package com.lukaskraener.ha_analyse

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Environment
import android.widget.Toast
import java.io.File
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class FileManager(private val context: Context) {

    private val mypath = Environment.getExternalStorageDirectory().toString() + "/HA-Analyse"
    private lateinit var db: SQLiteDatabase
    private var databaseConnection: Boolean= false
    private val format: DateFormat = SimpleDateFormat("yyyy-MM-dd_HH-mm-SS", Locale.ROOT)

    init {
        val f = File(mypath)
        val f2 = File("$mypath/txtFiles")
        if (!f.exists()) {
            f.mkdirs()
            f2.mkdirs()
        }
        if (!f2.exists()) {
            f2.mkdir()
        }
    }

    private fun getData(start: String, end:String): Triple<ArrayList<ArrayList<String>>, ArrayList<String>, Long> {
        val arr: ArrayList<ArrayList<String>> = ArrayList()
        val date = SimpleDateFormat("yyyy/MM/dd", Locale.ROOT)
        val time = SimpleDateFormat("HH:mm:ss", Locale.ROOT)
        val columnNames: ArrayList<String> = ArrayList()


        val ti: Cursor = db.rawQuery("PRAGMA table_info(FASTLOG)", null)
        if (ti.moveToFirst()) {
            do {
                columnNames.add(ti.getString(1))
            } while (ti.moveToNext())
        }
        ti.close()

        val dataraw = this.db.rawQuery("SELECT * from FASTLOG where TIMESTAMP > $start-1 AND TIMESTAMP < $end+1;", null)

        var timestamp: Long = 0
        if (dataraw.moveToFirst()) {
            timestamp = dataraw.getString(dataraw.getColumnIndex("TIMESTAMP")).toLong()
            do {
                val row: ArrayList<String> = ArrayList()
                columnNames.forEachIndexed { _, it ->
                    if(it != "TIMESTAMP"){
                        row.add(dataraw.getString(dataraw.getColumnIndex(it)))
                    }else{
                        val timestandRow = dataraw.getString(dataraw.getColumnIndex("TIMESTAMP")).toLong()
                        val daterow = Date(timestandRow)
                        row.add(date.format(daterow))
                        row.add(time.format(daterow))
                    }
                }
                arr.add(row)
            } while (dataraw.moveToNext())
            dataraw.close()
        }
        columnNames.apply {
            add(0, "Time")
            add(0, "Date")
            remove("TIMESTAMP")
        }
        return Triple(arr, columnNames, timestamp)
    }


    private fun getLastGenerate(): Long {
        val dirListing: Array<String?>?
        val dir = File("$mypath/txtFiles")
        dirListing = dir.list()
        Arrays.sort(dirListing)
        return if(dirListing.isNotEmpty()){
            val filename = dirListing[dirListing.size-1]
            val string = filename!!.replace("Trip_", "").replace(".txt", "")
            val date: Date = format.parse(string)
            date.time
        }else{
            0
        }
    }

    fun generateTXTFiles() {
        val f3 = File("$mypath/hybridassistant.db")
        if(f3.exists()){
            this.db = SQLiteDatabase.openDatabase(
                "$mypath/hybridassistant.db",
                null,
                SQLiteDatabase.OPEN_READONLY
            )
            databaseConnection = true
        }else{
            val mess = Toast.makeText(this.context,"KEINE DATENBANK GEFUNDEN unter\n$mypath/hybridassistant.db", Toast.LENGTH_LONG)
            mess.show()
        }
        if (databaseConnection){
            val lastexport = getLastGenerate()
            val trips = this.db.rawQuery("SELECT TSDEB, TSFIN from TRIPS where TSDEB > $lastexport;", null)
            if (trips.moveToFirst()) {
                do {
                    val start = trips.getString(trips.getColumnIndex("TSDEB"))
                    val end = trips.getString(trips.getColumnIndex("TSFIN"))
                    val (data, collumns, timestamp) = getData(start, end)
                    val filename = format.format(Date(timestamp))
                    File("$mypath/txtFiles/Trip_$filename.txt").printWriter().use { out ->
                        collumns.forEachIndexed { index, it ->
                            if (collumns.size  == index+1){
                                out.print(it.toLowerCase(Locale.getDefault()))
                            } else if(it != "Date" && it != "Time"){
                                out.print( it.toLowerCase(Locale.getDefault()) + "\t" )
                            } else{
                                out.print(it+ "\t" )
                            }
                        }
                        out.print( "\n" )
                        data.forEach {
                            it.forEachIndexed { index, element ->
                                if( it.size  == index+1){
                                    out.print(element.replace("", ""))
                                }else{
                                    out.print(element.replace("", "")+"\t")
                                }
                            }
                            out.print("\n")
                        }
                        out.close()
                    }
                } while (trips.moveToNext())
                trips.close()
            }
        }else{
            Toast.makeText(context,"KEINE DATENBANKVERBINDUNG", Toast.LENGTH_LONG).show()
        }
    }
    fun reader(): ArrayList<File> {
        return try {
            val directory = File(
                "$mypath/txtFiles/"
            )
            val files = directory.listFiles()
            val checkedfiles = ArrayList<File>()
            files?.forEach {
                if (DataValidiator.isVAlidFilename(it.name)) {
                    checkedfiles.add(it)
                }
            }
            println(checkedfiles)
            checkedfiles
        }catch (e: Exception){
            ArrayList()
        }
    }
}