package comalpha4.chatappwithlocation

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences

import comalpha4.chatappwithlocation.Models.UserModel


open class SessionManagement {
    var context: Context? = null
    var sharedPreferences: SharedPreferences? = null
    var editor: SharedPreferences.Editor? = null
    val loginResponse: UserModel? = null
    private var sessionManagement: SessionManagement? = null

    constructor(context: Context?) {
        this.context = context
        try {

            sharedPreferences = context?.getSharedPreferences("chatapp", MODE_PRIVATE)

            editor = sharedPreferences!!.edit()
        } catch (e: Exception) {
//            MyHelperClass.getInstance().log()
            LoggerGenratter.getInstance().printLog("PREF", e.message)

        }
    }

    fun setUserInfo(loginresponse: UserModel) {
        if (loginresponse.name != null) {
            editor?.putString("name", loginresponse.name)
        }
         if (loginresponse.email != null) {
            editor?.putString("email", loginresponse.email)
        }
         if (loginresponse.phone != null) {
            editor?.putString("phone", loginresponse.phone)
        }
         if (loginresponse.fcm != null) {
            editor?.putString("fcm", loginresponse.fcm)
        }
         if (loginresponse.userid != null) {
            editor?.putString("userid", loginresponse.userid)
        }

        if (loginresponse != null) {
            editor?.putBoolean("islogin", true)
        }



        editor?.commit()
        editor?.apply()


    }


    fun  getUserId():String{
        return sharedPreferences?.getString("userid","userid") ?: ""
    }

}