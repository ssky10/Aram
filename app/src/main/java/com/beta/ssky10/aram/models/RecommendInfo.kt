package com.beta.ssky10.aram.models

data class RecommendInfo(val title:String, val context:String, val developer:String, val type:Int, val url:String, val thumbnail:String){
    companion object{
        const val TYPE_APP = 0
        const val TYPE_URL = 1

        const val APP_BASE_URL = "market://details?id="
    }
}