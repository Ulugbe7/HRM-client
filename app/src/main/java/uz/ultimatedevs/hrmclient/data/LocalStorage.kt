package uz.ultimatedevs.hrmclient.data

import android.content.Context
import android.content.SharedPreferences
import uz.ultimatedevs.hrmclient.App
import uz.ultimatedevs.hrmclient.utils.BooleanPreference
import uz.ultimatedevs.hrmclient.utils.StringPreference

class LocalStorage {
    companion object {
        val pref: SharedPreferences =
            App.instance.getSharedPreferences("LocaleStorage", Context.MODE_PRIVATE)

        fun clearData() {
            pref.edit().clear().apply()
        }
    }

    var isLogin by BooleanPreference(pref)
    var userId by StringPreference(pref)
    var name by StringPreference(pref)

    var currentDate by StringPreference(pref)

}