package com.beta.ssky10.aram.ui.setting

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.beta.ssky10.aram.customRecyclerViewAdapter.recommendAppsAdapter.RecommendAdapter

class SettingViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is dashboard Fragment"
    }

    private val _appsAdapter = MutableLiveData<RecommendAdapter>().apply {
        value = null
    }

    val text: LiveData<String> = _text

    val appsAdapter: LiveData<RecommendAdapter> = _appsAdapter

    fun setAppsAdapter(context: Context){
        _appsAdapter.value = RecommendAdapter(context)
    }

    fun setAppsAdapter(adapter: RecommendAdapter){
        _appsAdapter.value = adapter
        adapter.notifyDataSetChanged()
    }

    fun addAppsData(title:String, context: String, developer:String, type:Int, url:String, thumbnail:String){
        if(_appsAdapter.value != null){
            _appsAdapter.value!!.addItem(title, context, developer, type, url, thumbnail)
        }
    }

}