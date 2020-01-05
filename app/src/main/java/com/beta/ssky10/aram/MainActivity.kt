package com.beta.ssky10.aram

import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.ActionBar
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.app.AppCompatDelegate
import android.util.Log
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.beta.ssky10.aram.models.MySharedPreferences


class MainActivity : AppCompatActivity() {

    private val WIFE_STATE = "WIFE"
    private val MOBILE_STATE = "MOBILE"
    private val NONE_STATE = "NONE"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val getNetwork = getWhatKindOfNetwork(application)

        if(getNetwork == NONE_STATE){
            AlertDialog.Builder(this)
                    .setTitle("네트워크 문제")
                    .setMessage("네트워크 연결이 필요합니다.")
                    .setPositiveButton("확인") { _: DialogInterface, _: Int ->
                        finish()
                    }
                    // 뒤로가기 버튼이 작동하지 못하게 함
                    .setCancelable(false)
                    .show()
        }else {
            if (getNetwork == MOBILE_STATE) {
                Toast.makeText(this, "모바일 데이터로 연결되었습니다.\n데이터통화료가 부과 될 수 있습니다.", Toast.LENGTH_LONG).show();
            }

            val navView: BottomNavigationView = findViewById(R.id.nav_view)

            val navController = findNavController(R.id.nav_host_fragment)
            // Passing each menu ID as a set of Ids because each
            // menu should be considered as top level destinations.
            val appBarConfiguration = AppBarConfiguration(setOf(
                    R.id.navigation_home, R.id.navigation_setting))
            //setupActionBarWithNavController(navController, appBarConfiguration)
            navView.setupWithNavController(navController)
        }
    }

    // 인터넷
    fun getWhatKindOfNetwork(context: Context): String {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        if (activeNetwork != null) {
            if (activeNetwork.type == ConnectivityManager.TYPE_WIFI) {
                return WIFE_STATE
            } else if (activeNetwork.type == ConnectivityManager.TYPE_MOBILE) {
                return MOBILE_STATE
            }
        }
        return NONE_STATE
    }
}
