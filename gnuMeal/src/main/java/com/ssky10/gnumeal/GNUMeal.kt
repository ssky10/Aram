package com.ssky10.gnumeal

import com.ssky10.gnumeal.enums.CafeteriaType
import org.jsoup.Jsoup
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class GNUMeal(private var type: CafeteriaType = CafeteriaType.GAJWA_DORMITORY) {
    private fun getURL(): String {
        return when (type) {
            CafeteriaType.GAJWA_DORMITORY -> "https://www.gnu.ac.kr/dorm/ad/fm/foodmenu/selectFoodMenuView.do?mi=7278"
        }
    }

    suspend fun getMeal(year:Int, month:Int, day:Int) = suspendCoroutine<Map<String,List<String>>> {
        val breakfast = mutableListOf("", "", "", "", "", "", "")
        val lunch = mutableListOf("", "", "", "", "", "", "")
        val dinner = mutableListOf("", "", "", "", "", "", "")
        val table: Array<MutableList<String>> = arrayOf(breakfast, lunch, dinner)

        try {
            val doc = Jsoup.connect(getURL())
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
}