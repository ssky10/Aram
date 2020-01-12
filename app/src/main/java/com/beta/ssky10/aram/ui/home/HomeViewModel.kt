package com.beta.ssky10.aram.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.beta.ssky10.aram.models.Meal

class HomeViewModel : ViewModel() {

    private val _title = MutableLiveData<String>().apply {
        value = ""
    }

    private val _mealData = MutableLiveData<MutableList<Meal>>().apply {
        value = mutableListOf()
    }

    val title: LiveData<String> = _title


    val mealData: LiveData<MutableList<Meal>> = _mealData

    fun setTitle(title: String){
        _title.postValue(title)
    }

    fun setTitle(index: Int){
        _title.postValue(_mealData.value!![index].title)
    }

    fun addMael(title: String,breakfast:String, lunch:String, dinner:String){
        _mealData.value!!.add(Meal(title,breakfast,lunch,dinner))
    }

    fun updateMealData(){
        _mealData.postValue(_mealData.value)
    }

    fun clearMealData(){
        _mealData.value!!.clear()
    }
}