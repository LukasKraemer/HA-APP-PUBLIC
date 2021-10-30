package de.lukaskraener.ha_analyse


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import java.io.*





class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme)
        setContentView(R.layout.activity_main)
        when (intent?.action) {Intent.ACTION_SEND -> { handleIndent(intent) }}
    }

    private fun handleIndent(indent: Intent){
        val fileUri = indent.getParcelableExtra(Intent.EXTRA_STREAM) as Uri?
        val initialStream: InputStream? = contentResolver.openInputStream(fileUri!!)
        val path = File(this.filesDir, "app_directory")
        path.mkdir()
        val targetFile = File(path, "hybridassistant.db")
        val outStream: OutputStream = FileOutputStream(targetFile)
        val buffer = ByteArray(8 * 1024)
        var bytesRead: Int
        while (initialStream!!.read(buffer).also { bytesRead = it } != -1) {
            outStream.write(buffer, 0, bytesRead)
        }
    }
}



