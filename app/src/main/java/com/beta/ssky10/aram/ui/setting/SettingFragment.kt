package com.beta.ssky10.aram.ui.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.Uri
import android.util.Log
import android.widget.Switch
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.beta.ssky10.aram.App
import com.beta.ssky10.aram.R
import com.beta.ssky10.aram.customRecyclerViewAdapter.recommendAppsAdapter.RecommendAdapter
import com.beta.ssky10.aram.netWork.NetworkCoroutin
import com.beta.ssky10.aram.ui.home.HomeFragment
import kotlinx.android.synthetic.main.fragment_setting.view.*
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class SettingFragment : Fragment(), CoroutineScope {

    private lateinit var dashboardViewModel: SettingViewModel

    lateinit var adapter: RecommendAdapter
    private lateinit var mJob: Job
    override val coroutineContext: CoroutineContext
        get() = mJob + Dispatchers.Main

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        dashboardViewModel =
                ViewModelProviders.of(this).get(SettingViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_setting, container, false)

        mJob = Job()

        dashboardViewModel.setAppsAdapter(context!!)

        dashboardViewModel.appsAdapter.observe(this, Observer {
            root!!.rv_setting_recommend.adapter = it
        })

        root.rv_setting_recommend.layoutManager = LinearLayoutManager(
                context,
                LinearLayoutManager.VERTICAL, false
        )

        setAppData()

        root.findViewById<Switch>(R.id.switch_setting_night).setOnClickListener {
            App.prefs.ableNightMode = (it as Switch).isChecked
            if(it.isChecked){
                root.layout_setting_fixed_nigth_mode.visibility = View.VISIBLE
            }else{
                root.layout_setting_fixed_nigth_mode.visibility = View.GONE
            }
            Toast.makeText(context, "다음번 실행시 설정이 적용됩니다.", Toast.LENGTH_LONG).show()
        }

        root.switch_setting_night.isChecked = App.prefs.ableNightMode

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

        root.iv_setting_apps_toggle.setOnClickListener {
            when(root.rv_setting_recommend.visibility){
                View.GONE -> root.rv_setting_recommend.visibility = View.VISIBLE
                View.VISIBLE -> root.rv_setting_recommend.visibility = View.GONE
            }
        }

        return root
    }

    override fun onDestroy() {
        super.onDestroy()
        mJob.cancel()
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

        return packageInfo.versionName
    }

    fun setAppData(){
        launch{
            try{
                val deferred = async(Dispatchers.Default) {
                    //백그라운드 스레드 에서 동작합니다
                    NetworkCoroutin.getAppsList("com.beta.ssky10.aram")
                }
                val result = deferred.await()
                Log.e("setAppData", result.toString())
                for (item in result){
                    val title = item["title"]?: ""
                    val context = item["context"]?: ""
                    val developer = item["developer"]?: ""
                    val type = (item["type"]?: "1").toInt()
                    val url = item["url"]?: ""
                    val thumbnail = item["thumbnail"]?: ""
                    dashboardViewModel.addAppsData(title, context, developer, type, url, thumbnail)
                }
            }finally {

            }
        }
    }
}