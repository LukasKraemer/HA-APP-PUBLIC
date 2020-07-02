package com.lukaskraener.ha_analyse

import com.hierynomus.smbj.SMBClient
import com.hierynomus.smbj.auth.AuthenticationContext
import com.hierynomus.smbj.session.Session
import com.hierynomus.smbj.share.DiskShare
import java.io.IOException

object smb {
    var session: Session? = null
    fun init(
        username: String?,
        passwort: String,
        ip: String?
    ): Session? {
        val client = SMBClient()
        try {
            client.connect(ip).use { connection ->
                val ac =
                    AuthenticationContext(username, passwort.toCharArray(), ip)
                session = connection.authenticate(ac)
                return session
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    @Throws(IOException::class)
    fun reader(username: String?, passwort: String, ip: String?): Int {
        var value: Int = 0
        try{
            value = 0
        } catch (e: IOException) {
            e.message
            value = -1
        }
        finally {
            return value
        }
    }

    fun delete(username: String?, passwort: String, ip: String?): Int {
        session = init(username, passwort, ip)
        val share = session!!.connectShare("SHARENAME") as DiskShare
        run { share.rm("FILE") }
        return 0
    }
}