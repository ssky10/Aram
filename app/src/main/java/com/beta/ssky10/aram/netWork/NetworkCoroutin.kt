package com.beta.ssky10.aram.netWork

import android.util.Log
import org.json.JSONObject
import org.jsoup.Jsoup
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class NetworkCoroutin() {

    companion object{
        private val htmlPageUrl = "http://dorm.gnu.ac.kr/sub/04_05.jsp?"
        private val severUrl = "https://kakaoplus.ml"

        suspend fun getMeal(year:Int, month:Int, day:Int) = suspendCoroutine<Map<String,List<String>>> {
            val breakfast = mutableListOf("", "", "", "", "", "", "")
            val lunch = mutableListOf("", "", "", "", "", "", "")
            val dinner = mutableListOf("", "", "", "", "", "", "")
            val table: Array<MutableList<String>> = arrayOf(breakfast, lunch, dinner)

            try {
                val doc = Jsoup.connect(htmlPageUrl + "year=" + year + "&month=" + month + "&date=" + day).get()
                val links = doc.select("tbody tr")

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
                                Log.d("onPostExecute", breakfast.toString())
                            }
                        }
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                it.resume(mapOf(Pair("breakfast", breakfast), Pair("lunch", lunch), Pair("dinner", dinner)))
            }
        }

        suspend fun getAppsList() = suspendCoroutine<List<Map<String,String>>> {
            val result = mutableListOf<Map<String,String>>()
            try {
                val doc = Jsoup.connect(severUrl).get()
                val json = JSONObject(doc.toString())
                
                if(json.getBoolean("result")){
                    val dataList = json.getJSONArray("list")
                    for (i in 0 until dataList.length()){
                        val data = dataList.getJSONObject(i)
                        val item = mutableMapOf<String,String>()
                        item["img"] = data.getString("img")
                        item["title"] = data.getString("title")
                        item["context"] = data.getString("context")
                        item["developer"] = data.getString("developer")
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