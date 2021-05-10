package com.lukaskraener.ha_analyse

class AuthRepsonde {
    private var accessToken: String? = null
    private var permission: Int = 0

    fun AuthRepsonde(accessToken: String?, permission: Int) {
        this.accessToken = accessToken
        this.permission = permission
    }
    fun getToken(): String? {
        return if (permission >= 0){
            accessToken
        }else{
            ""
        }
    }
}