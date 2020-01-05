package com.beta.ssky10.aram.models

import android.content.Context
import android.content.SharedPreferences

class MySharedPreferences(context: Context) {
    val PREFS_FILENAME = "prefs"
    val PREF_KEY_NIGHT_MODE = "nightMode"
    val PREF_KEY_FIXED_NIGHT_MODE = "fixedNightMode"
    val prefs: SharedPreferences = context.getSharedPreferences(PREFS_FILENAME, 0)
    /* 파일 이름과 EditText를 저장할 Key 값을 만들고 prefs 인스턴스 초기화 */

    var ableNightMode: Boolean
        get() = prefs.getBoolean(PREF_KEY_NIGHT_MODE, true)
        set(value) = prefs.edit().putBoolean(PREF_KEY_NIGHT_MODE, value).apply()

    var isFixedNightMode: Boolean
        get() = prefs.getBoolean(PREF_KEY_FIXED_NIGHT_MODE, false)
        set(value) = prefs.edit().putBoolean(PREF_KEY_FIXED_NIGHT_MODE, value).apply()
}