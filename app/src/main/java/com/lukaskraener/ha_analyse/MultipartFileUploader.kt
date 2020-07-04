package com.lukaskraener.ha_analyse

import java.io.File
import java.io.IOException

/**
 * This program demonstrates a usage of the MultipartUtility class.
 * @author www.codejava.net
 */
object MultipartFileUploader {
    @JvmStatic
    fun uploader(): String {
        var fehler : Int=0
        val charset = "UTF-8"
        fehler=1
        val uploadFile1 = File("e:/Test/PIC1.JPG")
        fehler=2
        val uploadFile2 = File("e:/Test/PIC2.JPG")
        fehler=3
        val requestURL = "http://192.168.178.76:80/test.php"
        fehler=4
        if(trash.isInternetAvailable()) {
            try {
                val multipart = MultipartUtility(requestURL, charset)
                fehler = 5
                multipart.addHeaderField("User-Agent", "CodeJava")
                fehler = 6
                multipart.addHeaderField("Test-Header", "Header-Value")
                fehler = 7
                multipart.addFormField("description", "Cool Pictures")
                fehler = 8
                multipart.addFormField("keywords", "Java,upload,Spring")
                fehler = 9
                multipart.addFilePart("fileUpload", uploadFile1)
                fehler = 10
                multipart.addFilePart("fileUpload", uploadFile2)
                fehler = 11
                val response = multipart.finish()
                fehler = 12
                println("SERVER REPLIED:")
                for (line in response) {
                    println(line)
                }
                fehler = 13
                return "ferig"
            } catch (e: Exception) {
                return "FEhler =" + fehler
            }
        }else{
            return trash.isInternetAvailable().toString()
        }
    }
}