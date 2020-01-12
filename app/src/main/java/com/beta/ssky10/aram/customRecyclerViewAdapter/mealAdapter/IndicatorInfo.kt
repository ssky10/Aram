package com.beta.ssky10.aram.customRecyclerViewAdapter.mealAdapter

import android.graphics.Rect

data class IndicatorInfo(val touchArea:MutableList<Rect> = mutableListOf()){
    fun addTouchArea(area:Rect){
        touchArea.add(area)
    }
}