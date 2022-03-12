package com.beta.ssky10.aram.netWork

import android.util.Log
import org.json.JSONObject
import org.jsoup.Jsoup
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class NetworkCoroutin() {

    companion object{
        private val htmlPageUrl = "https://newgh.gnu.ac.kr/dorm/ad/fm/foodmenu/selectFoodMenuView.do?mi=7278"
        private val severUrl = "https://ssky10.dev/publish/rest_api/android/"

        suspend fun getMeal(year:Int, month:Int, day:Int) = suspendCoroutine<Map<String,List<String>>> {
            val breakfast = mutableListOf("", "", "", "", "", "", "")
            val lunch = mutableListOf("", "", "", "", "", "", "")
            val dinner = mutableListOf("", "", "", "", "", "", "")
            val table: Array<MutableList<String>> = arrayOf(breakfast, lunch, dinner)

            try {
                val doc = Jsoup.connect(htmlPageUrl)
                    .timeout(10000)
                    .data("restSeq", "47")
                    .data("schDt", "$year-$month-$day")
                    .post()

                val links = doc.select("div.BD_table tbody tr")

                if (links.size >= 3) {
                    for (index in 0 until 3) {
                        val dayTable = links[index].select("td")
                        when (dayTable.size) {
                            7 -> {
                                table[index].clear()
                                for (meal in dayTable) {
                                    table[index].add(meal.html()
                                            .replace("<p class=\"special_menu_a\"", "<pre class=\"special_menu_a\"")
                                            .replace("<p cla", "<br><pre cla")
                                            .replace("\">", "\" style=\"color:blue\"><b>")
                                            .replace("</p>", "</b></pre><br>"))
                                }
                            }
                        }
                    }
                }
                Log.d("Net", table[0][0])
                Log.d("Net", "date : $year-$month-$day")
            } catch (e: IOException) {
                e.printStackTrace()
                for(i in 0 until 7){
                    table[0][i] = "오류가 발생했습니다.<br/>다시 시도해주세요"
                    table[1][i] = "오류가 발생했습니다.<br/>다시 시도해주세요"
                    table[2][i] = "오류가 발생했습니다.<br/>다시 시도해주세요"
                }
            } finally {
                it.resume(mapOf(Pair("breakfast", breakfast), Pair("lunch", lunch), Pair("dinner", dinner)))
            }
        }

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