package com.beta.ssky10.aram.netWork

import android.util.Log
import org.json.JSONObject
import org.jsoup.Jsoup
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class NetworkCoroutin() {

    companion object{
        private val severUrl = "https://ssky10.dev/publish/rest_api/android/"

        suspend fun getAppsList(packageName:String) = suspendCoroutine<List<Map<String,String>>> {
            val result = mutableListOf<Map<String,String>>()
            try {
                val doc = Jsoup.connect(severUrl+"getRecommendApps.php?packageName="+packageName)
                        .ignoreContentType(true).execute().body()

                Log.e("getAppsList", doc)
                val json = JSONObject(doc)

                if(json.getInt("result") == 0){
                    val dataList = json.getJSONArray("items")
                    for (i in 0 until dataList.length()){
                        val data = dataList.getJSONObject(i)
                        val item = mutableMapOf<String,String>()
                        item["title"] = data.getString("title")
                        item["context"] = data.getString("context")
                        item["developer"] = data.getString("developer")
                        item["type"] = data.getString("type")
                        item["url"] = data.getString("url")
                        item["thumbnail"] = data.getString("thumbnail")
                        item["isOfficial"] = data.getString("isOfficial")
                        result.add(item)
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                it.resume(result)
            }
        }
    }
}