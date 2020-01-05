package com.beta.ssky10.aram.customRecyclerViewAdapter

import android.support.v7.widget.RecyclerView
import android.view.MotionEvent

class MealTouchListener(val indicatorInfo: IndicatorInfo) : RecyclerView.OnItemTouchListener {
    companion object{
        const val TAG = "MealTouchListener"
    }
    override fun onTouchEvent(p0: RecyclerView, p1: MotionEvent) {

    }

    override fun onInterceptTouchEvent(p0: RecyclerView, p1: MotionEvent): Boolean {
        for ((index, touchArea) in indicatorInfo.touchArea.withIndex()){
            if(touchArea.contains(p1.x.toInt(), p1.y.toInt())){
                p0.scrollToPosition(index+1)
            }
        }
        return false
    }

    override fun onRequestDisallowInterceptTouchEvent(p0: Boolean) {

    }
}