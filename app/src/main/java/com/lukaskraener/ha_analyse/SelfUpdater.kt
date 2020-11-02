package com.lukaskraener.ha_analyse

import android.app.DownloadManager
import android.app.DownloadManager.Request
import android.app.DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment


class SelfUpdater(context: Context) {
    private val context = context

    fun download(url: String) {
        var downloadmanager =
            this.context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager?
        val uriString: String = "https://" + url + "app.apk"
        val uri =
            Uri.parse(uriString)

        val request = Request(uri)
        request.setTitle("hatool.apk")
        request.setNotificationVisibility(VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        request.setMimeType("application/vnd.android.package-archive")
        request.setDescription("Downloading") //request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "hatool.apk")


        downloadmanager!!.enqueue(request)


    }
}