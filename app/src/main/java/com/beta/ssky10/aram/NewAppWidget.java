package com.beta.ssky10.aram;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Implementation of App Widget functionality.
 */
public class NewAppWidget extends AppWidgetProvider {


    private static String[][] m_arr = new String[7][3];
    private static String htmlPageUrl = "http://dorm.gnu.ac.kr/sub/04_05.jsp?";

    private static int select;
    RemoteViews views;
    AppWidgetManager aWM;
    Context ct;
    int AId;
    private static int year,month,day;

    public void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        try{
            //CharSequence widgetText = context.getString(R.string.appwidget_text);
            // Construct the RemoteViews object
            updateDay();

            views = new RemoteViews(context.getPackageName(), R.layout.new_app_widget);

            aWM = appWidgetManager;
            AId = appWidgetId;
            ct = context;

            //RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.new_app_widget);

            JsoupAsyncTask task = new JsoupAsyncTask();
            task.execute();

            // Instruct the widget manager to update the widget
            //appWidgetManager.updateAppWidget(appWidgetId, views);
        }catch(Exception e){
            views.setTextViewText(R.id.wid_break,"오류가");
            views.setTextViewText(R.id.wid_lunch,"발생하였");
            views.setTextViewText(R.id.wid_dinner,"습니다.");
            appWidgetManager.updateAppWidget(appWidgetId, views);
            e.printStackTrace();
        }finally {
            Intent intent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            views.setOnClickPendingIntent(R.id.goApp, pendingIntent);

            intent = new Intent(context, NewAppWidget.class);
            intent.setAction("andriod.action.BUTTON_CLICK");
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            PendingIntent re_btn = PendingIntent.getBroadcast(context,
                    appWidgetId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setOnClickPendingIntent(R.id.recycle, re_btn);
        }

    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    @Override
    public void onReceive(Context context, Intent intent){
        super.onReceive(context, intent);

        String action = intent.getAction();
        // 위젯 업데이트 인텐트를 수신했을 때
        if(action.equals("andriod.action.BUTTON_CLICK"))
        {
            //int _Day = day;
            //updateDay();
            int id = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
            updateAppWidget(context, AppWidgetManager.getInstance(context), id);   // 버튼이 클릭되면 새로고침 수행

            //Log.d("ExampleWidget", "onReceive: BUTTON_CLICK / id:"+id);
        }
        // 위젯 제거 인텐트를 수신했을 때
        else if(action.equals("android.appwidget.action.APPWIDGET_DISABLED"))
        {

        }
    }

    public void updateDay(){
        long now = System.currentTimeMillis();
        Date stan = new Date(now);


        Calendar cal = Calendar.getInstance();
        cal.setTime(stan);

        select = cal.get(Calendar.DAY_OF_WEEK)-1;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        year = Integer.parseInt(sdf.format(stan));
        sdf = new SimpleDateFormat("M");
        month = Integer.parseInt(sdf.format(stan));
        sdf = new SimpleDateFormat("d");
        day = Integer.parseInt(sdf.format(stan));
    }

    private class JsoupAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params){
            try {
                Document doc = Jsoup.connect(htmlPageUrl+"year="+year+"&month="+month+"&date="+day).get();
                Elements links = doc.select("tbody tr");

                int day=0,meal=0;

                for (Element link : links) {
                    Elements subLinks = link.select("td");
                    if(meal>2) break;
                    for(Element sub_link : subLinks) {
                        if(day>6) break;
                        //Elements sSubLinks = sub_link.select("p");
                        //m_arr[day][meal] = "";
                        //for(Element sSub_link : sSubLinks) {
                        m_arr[day][meal] = sub_link.html().replace("<p cla","<pre cla").replace("\">","\" style=\"color:blue\"><b>").replace("</p>","</b></pre><br>");
                        //if(m_arr[day][meal].substring(0,3).equals("<br>")) m_arr[day][meal].replaceFirst("<br>","");
                        //}
                        day++;
                    }
                    day=0; meal++;
                }

            } catch (IOException e) {
                m_arr[select][0] = "오류가";
                m_arr[select][1] = "발생하였";
                m_arr[select][2] = "습니다.";
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            setMeal(aWM,AId,views);
        }
    }

    void setMeal(AppWidgetManager appWidgetManager, int appWidgetId, RemoteViews view){
        views.setTextViewText(R.id.wid_break,Html.fromHtml(m_arr[select][0]));
        views.setTextViewText(R.id.wid_lunch,Html.fromHtml(m_arr[select][1]));
        views.setTextViewText(R.id.wid_dinner,Html.fromHtml(m_arr[select][2]));
        appWidgetManager.updateAppWidget(appWidgetId, view);
    }
}

