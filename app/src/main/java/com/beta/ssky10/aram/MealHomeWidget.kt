package com.beta.ssky10.aram

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.os.Build
import android.text.Html
import android.util.Log
import android.view.View
import android.widget.RemoteViews
import com.beta.ssky10.aram.netWork.NetworkCoroutin
import com.beta.ssky10.aram.ui.home.HomeFragment
import kotlinx.coroutines.*
import java.util.*
import kotlin.coroutines.CoroutineContext

class MealHomeWidget : AppWidgetProvider(), CoroutineScope {

    private lateinit var views:RemoteViews
    private lateinit var aWM:AppWidgetManager
    private lateinit var ct:Context
    private var AId:Int = -1
    private lateinit var cal: Calendar

    private lateinit var mJob: Job
    override val coroutineContext: CoroutineContext
        get() = mJob + Dispatchers.Main

    fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager,
                        appWidgetId: Int) {
        try {
            //CharSequence widgetText = context.getString(R.string.appwidget_text);
            // Construct the RemoteViews object
            Log.e("MealHomeWidget", "updateAppWidget: start")
            views = RemoteViews(context.packageName, R.layout.new_app_widget)
            aWM = appWidgetManager
            AId = appWidgetId
            ct = context

            mJob = Job()

            //RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.new_app_widget);
            Log.e("MealHomeWidget", "setMeal: start")
            views.setViewVisibility(R.id.progressBarLL, View.VISIBLE)
            updateDay()
            setMealData()
            // Instruct the widget manager to update the widget
//appWidgetManager.updateAppWidget(appWidgetId, views);
        } catch (e: Exception) {
            views.setTextViewText(R.id.wid_break, "오류가")
            views.setTextViewText(R.id.wid_lunch, "발생하였")
            views.setTextViewText(R.id.wid_dinner, "습니다.")
            appWidgetManager.updateAppWidget(appWidgetId, views)
            e.printStackTrace()
        } finally {
            var intent = Intent(context, MainActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(context, 0, intent, if(Build.VERSION.SDK_INT < Build.VERSION_CODES.S) 0 else PendingIntent.FLAG_MUTABLE)
            views.setOnClickPendingIntent(R.id.goApp, pendingIntent)
            intent = Intent(context, MealHomeWidget::class.java)
            intent.action = "andriod.action.BUTTON_CLICK"
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            val re_btn = PendingIntent.getBroadcast(context,
                    appWidgetId, intent, if(Build.VERSION.SDK_INT < Build.VERSION_CODES.S) PendingIntent.FLAG_UPDATE_CURRENT else PendingIntent.FLAG_MUTABLE)
            views.setOnClickPendingIntent(R.id.recycle, re_btn)
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }

    override fun onUpdate(context: Context?, appWidgetManager: AppWidgetManager?, appWidgetIds: IntArray?) {
        for (appWidgetId in appWidgetIds!!) {
            updateAppWidget(context!!, appWidgetManager!!, appWidgetId)
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)

        val action = intent!!.action
        // 위젯 업데이트 인텐트를 수신했을 때
        if (action == "andriod.action.BUTTON_CLICK") {
            //int _Day = day;
            //updateDay();
            val id = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID)
            updateAppWidget(context!!, AppWidgetManager.getInstance(context), id) // 버튼이 클릭되면 새로고침 수행
            //Log.d("ExampleWidget", "onReceive: BUTTON_CLICK / id:"+id);
        } else if (action == "android.appwidget.action.APPWIDGET_DISABLED") {
        }
    }

    fun updateDay() {
        cal = Calendar.getInstance()
        views.setTextViewText(R.id.textview_day, "${cal.get(Calendar.MONTH)+1}월 ${cal.get(Calendar.DATE)}일 아람관 식단")
    }

    fun setMealData(){
        launch{
            try{
                val deferred = async(Dispatchers.Default) {
                    //백그라운드 스레드 에서 동작합니다
                    val loadDate = Calendar.getInstance()
                    loadDate.timeInMillis = cal.timeInMillis
                    loadDate.add(Calendar.DATE, -1)
                    NetworkCoroutin.getMeal(loadDate.get(Calendar.YEAR), loadDate.get(Calendar.MONTH)+1, loadDate.get(Calendar.DATE))
                }
                val result = deferred.await()

                val today = (cal.get(Calendar.DAY_OF_WEEK)+5)%7

                if (result.getValue("breakfast")[today].isEmpty()) views.setTextViewText(R.id.wid_break, "정보가 없습니다") else views.setTextViewText(R.id.wid_break, Html.fromHtml(result.getValue("breakfast")[today]))
                if (result.getValue("lunch")[today].isEmpty()) views.setTextViewText(R.id.wid_lunch, "정보가 없습니다") else views.setTextViewText(R.id.wid_lunch, Html.fromHtml(result.getValue("lunch")[today]))
                if (result.getValue("dinner")[today].isEmpty()) views.setTextViewText(R.id.wid_dinner, "정보가 없습니다") else views.setTextViewText(R.id.wid_dinner, Html.fromHtml(result.getValue("dinner")[today]))
            }finally {
                views.setViewVisibility(R.id.progressBarLL, View.GONE)
                aWM.updateAppWidget(AId, views)
            }
        }
    }
}