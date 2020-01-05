package com.beta.ssky10.aram.ui.setting

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.beta.ssky10.aram.App

class SettingViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is dashboard Fragment"
    }

    val text: LiveData<String> = _text

}