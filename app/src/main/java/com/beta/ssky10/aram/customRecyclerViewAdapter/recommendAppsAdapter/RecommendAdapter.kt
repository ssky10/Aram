package com.beta.ssky10.aram.customRecyclerViewAdapter.recommendAppsAdapter

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.beta.ssky10.aram.R
import com.beta.ssky10.aram.models.RecommendInfo
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_app.view.*


class RecommendAdapter(var Act_context: Context) : RecyclerView.Adapter<RecommendAdapter.ItemHolder>()  {

    var mealsList = mutableListOf<RecommendInfo>()
        set(values){
            field = values
            notifyDataSetChanged()
        }

    var isLoading = false //현재 로딩중인지 확인
        set(value) {
            notifyDataSetChanged()
            field = value
        }

    var isLastPage = false //마지막페이지까지 모두 로드되었는지 확인

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder = ItemHolder(parent)

    //일반 식단 리스트 아이템
    inner class ItemHolder(parent: ViewGroup)
        : RecyclerView.ViewHolder(
            LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_app, parent, false)){
        val tvTitle: TextView = itemView.tv_item_app_title
        val tvContext: TextView = itemView.tv_item_app_context
        val tvDeveloper: TextView = itemView.tv_item_app_developer
        val ivThumbnail: ImageView = itemView.iv_item_app_thumbnail
        val clLayout:ConstraintLayout = itemView.cl_item_app_layout
    }

    override fun getItemCount(): Int = mealsList.size

    fun addItem(title:String, context: String, developer:String, type:Int, url:String, thumbnail:String){
        mealsList.add(RecommendInfo(title, context, developer, type, url, thumbnail))
        notifyDataSetChanged()
    }

    fun clear(){
        mealsList = mutableListOf()
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        mealsList[position].let { repo ->
            with(holder){
                Log.e("onBindViewHolder", repo.title)
                tvTitle.text = repo.title
                tvContext.text = repo.context
                tvDeveloper.text = repo.developer
                Glide.with(Act_context).load(repo.thumbnail).into(ivThumbnail)
                clLayout.setOnClickListener {
                    when(repo.type){
                        RecommendInfo.TYPE_APP -> {
                            if(getPackageList(repo.url)){
                                val intent: Intent = Act_context.packageManager.getLaunchIntentForPackage(repo.url)!!
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                Act_context.startActivity(intent)
                            }else{
                                val url = "market://details?id=${repo.url}"
                                val i = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                Act_context.startActivity(i)
                            }
                        }
                        RecommendInfo.TYPE_URL -> {
                            val url = repo.url
                            val i = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                            Act_context.startActivity(i)
                        }
                    }
                }
            }
        }
    }

    fun getPackageList(packageName:String): Boolean {
        var isExist = false
        val pkgMgr: PackageManager = Act_context.packageManager
        val mApps: List<ResolveInfo>
        val mainIntent = Intent(Intent.ACTION_MAIN, null)
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER)
        mApps = pkgMgr.queryIntentActivities(mainIntent, 0)
        try {
            for (i in mApps.indices) {
                if (mApps[i].activityInfo.packageName.startsWith(packageName)) {
                    isExist = true
                    break
                }
            }
        } catch (e: Exception) {
            isExist = false
        }
        return isExist
    }
}