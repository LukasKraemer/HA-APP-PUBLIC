package de.lukaskraener.ha_analyse

data class AuthRepsonde(
    private var accessToken: String? = null,
    private var permission: Int = 0)
{
    fun getToken(): String? {
        return if (permission >= 0){
            accessToken
        }else{
            ""
        }
    }
}