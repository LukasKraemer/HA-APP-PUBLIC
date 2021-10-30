package de.lukaskraener.ha_analyse

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.widget.Toast
import java.io.File
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class FileManager(private val context: Context) {

    private lateinit var db: SQLiteDatabase
    private var databaseConnection: Boolean= false
    private val format: DateFormat = SimpleDateFormat("yyyy-MM-dd_HH-mm-SS", Locale.ROOT)


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

        val dataRaw = this.db.rawQuery("SELECT * from FASTLOG where TIMESTAMP > $start-1 AND TIMESTAMP < $end+1;", null)

        var timestamp: Long = 0
        if (dataRaw.moveToFirst()) {
            timestamp = dataRaw.getString(dataRaw.getColumnIndex("TIMESTAMP")).toLong()
            do {
                val row: ArrayList<String> = ArrayList()
                columnNames.forEachIndexed { _, it ->
                    if(it != "TIMESTAMP"){
                        row.add(dataRaw.getString(dataRaw.getColumnIndex(it)))
                    }else{
                        val timestampRow = dataRaw.getString(dataRaw.getColumnIndex("TIMESTAMP")).toLong()
                        val dataRow = Date(timestampRow)
                        row.add(date.format(dataRow))
                        row.add(time.format(dataRow))
                    }
                }
                arr.add(row)
            } while (dataRaw.moveToNext())
            dataRaw.close()
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
        val dir = File(context.filesDir, "text")
        dir.mkdir()
        dirListing = dir.list()
        Arrays.sort(dirListing!!)
        return if(dirListing.isNotEmpty()){
            val filename = dirListing[dirListing.size-1]
            val string = filename!!.replace("Trip_", "").replace(".txt", "")
            val date: Date = format.parse(string)!!
            date.time
        }else{
            0
        }
    }

    fun generateTXTFiles() {
        val dir = File(context.filesDir, "app_directory")
        val f3 = File(dir, "hybridassistant.db")
        if(f3.exists()){
            this.db = SQLiteDatabase.openDatabase(
                f3.absolutePath,
                null,
                SQLiteDatabase.OPEN_READONLY
            )
            databaseConnection = true
        }else{
            val mess = Toast.makeText(context,context.getString(R.string.noDatabaseFounded), Toast.LENGTH_LONG)
            mess.show()
            databaseConnection=false
        }

        if (databaseConnection){
            val lastExport = getLastGenerate()
            val trips = this.db.rawQuery("SELECT TSDEB, TSFIN from TRIPS where TSDEB > $lastExport;", null)
            if (trips.moveToFirst()) {
                do {
                    val start = trips.getString(trips.getColumnIndex("TSDEB"))
                    val end = trips.getString(trips.getColumnIndex("TSFIN"))
                    val (data, columns, timestamp) = getData(start, end)
                    val filename = format.format(Date(timestamp))
                    val directory = File(context.filesDir, "text")
                    File(directory, "Trip_$filename.txt").printWriter().use{ out ->
                        columns.forEachIndexed { index, it ->
                            val value: String =
                            if (columns.size  == index+1){
                                it.toLowerCase(Locale.getDefault())
                            } else if(it != "Date" && it != "Time"){
                                it.toLowerCase(Locale.getDefault()) + "\t"
                            } else{
                                it+ "\t"
                            }
                            out.print(value)
                        }
                        out.print( "\n")
                        data.forEach {
                            it.forEachIndexed { index, element ->
                                val value: String = if( it.size  == index+1){
                                    element.replace("", "")
                                }else{
                                    element.replace("", "")+"\t"
                                }
                                out.print(value)
                            }
                            out.print("\n")
                        }
                        out.close()
                    }
                } while (trips.moveToNext())
                trips.close()
            }
        }else{
            Toast.makeText(context,context.getString(R.string.noDatabaseFounded), Toast.LENGTH_LONG).show()
        }
    }
    fun reader(): ArrayList<File> {
        return try {
            val directory = File(context.filesDir, "text")
            val files = directory.listFiles()
            val checkedFiles = ArrayList<File>()
            files?.forEach {
                if (DataValidiator.isVAlidFilename(it.name)) {
                    checkedFiles.add(it)
                }
            }
            checkedFiles
        }catch (e: Exception){
            ArrayList()
        }
    }
}