package com.beta.ssky10.aram.ui.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.support.v4.app.Fragment
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.Uri
import android.widget.Switch
import android.widget.Toast
import com.beta.ssky10.aram.App
import com.beta.ssky10.aram.R
import kotlinx.android.synthetic.main.fragment_setting.view.*

class SettingFragment : Fragment() {

    private lateinit var dashboardViewModel: SettingViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        dashboardViewModel =
                ViewModelProviders.of(this).get(SettingViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_setting, container, false)

        root.findViewById<Switch>(R.id.switch_setting_night).setOnClickListener {
            App.prefs.ableNightMode = (it as Switch).isChecked
            if(it.isChecked){
                root.layout_setting_fixed_nigth_mode.visibility = View.VISIBLE
            }else{
                root.layout_setting_fixed_nigth_mode.visibility = View.GONE
            }
            Toast.makeText(context, "다음번 실행시 설정이 적용됩니다.", Toast.LENGTH_LONG).show()
        }

        root!!.switch_setting_night.isChecked = App.prefs.ableNightMode

        if(App.prefs.ableNightMode){
            root.switch_setting_night.isChecked = true
            root.layout_setting_fixed_nigth_mode.visibility = View.VISIBLE
        }else{
            root.switch_setting_night.isChecked = false
            root.layout_setting_fixed_nigth_mode.visibility = View.GONE
        }

        root.findViewById<Switch>(R.id.switch_setting_night_auto).setOnClickListener {
            App.prefs.isFixedNightMode = (it as Switch).isChecked
            Toast.makeText(context, "다음번 실행시 설정이 적용됩니다.", Toast.LENGTH_LONG).show()
        }

        root.switch_setting_night_auto.isChecked = App.prefs.isFixedNightMode

        root.tv_setting_now_version.text = getAppVersionName()

        root.tv_setting_review.setOnClickListener{
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(
                        "https://play.google.com/store/apps/details?id=com.beta.ssky10.aram")
                setPackage("com.android.vending")
            }
            startActivity(intent)
        }

        return root
    }

    fun getAppVersionName() : String {
        var packageInfo: PackageInfo? = null

        //PackageInfo 초기화
        try{
            packageInfo = context!!.packageManager.getPackageInfo(context!!.packageName, 0);
        }catch (e: PackageManager.NameNotFoundException){
            e.printStackTrace()
            return ""
        }

        return packageInfo.versionName;
    }
}