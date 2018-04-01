package com.beta.ssky10.aram;

import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import co.ceryle.segmentedbutton.SegmentedButtonGroup;
import dmax.dialog.SpotsDialog;

public class MainActivity extends AppCompatActivity {

    private String htmlPageUrl = "http://dorm.gnu.ac.kr/sub/04_05.jsp?";
    public String[][] m_arr = new String[7][3];

    TextView breakfast,lunch,dinner,now_week;
    int select;
    int year,month,day;
    long now = System.currentTimeMillis();
    Date stan = new Date(now);
    SpotsDialog dialog;
    String[] weekStr = new String[]{"일","월","화","수","목","금","토"};
    SegmentedButtonGroup segmentedButtonGroup;

    public static final String WIFE_STATE = "WIFE";
    public static final String MOBILE_STATE = "MOBILE";
    public static final String NONE_STATE = "NONE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String getNetwork =  getWhatKindOfNetwork(getApplication());
        if(getNetwork.equals(NONE_STATE)){
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle(new String("네트워크 문제"))
                    .setMessage(new String("네트워크 연결이 필요합니다."))
                    .setPositiveButton(new String("확인"), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub
                            finish();
                        }
                    })
                    // 뒤로가기 버튼이 작동하지 못하게 함
                    .setCancelable(false)
                    .show();
        }else{
            if(getNetwork.equals(MOBILE_STATE)) {
                Toast.makeText(this, "모바일 데이터로 연결되었습니다.\n데이터통화료가 부과 될 수 있습니다.", Toast.LENGTH_LONG).show();
            }

            segmentedButtonGroup = (SegmentedButtonGroup) findViewById(R.id.segmentedButtonGroup);
            segmentedButtonGroup.setOnClickedButtonListener(new SegmentedButtonGroup.OnClickedButtonListener() {
                @Override
                public void onClickedButton(int position) {
                    Calendar cal = Calendar.getInstance();
                    cal.set(year,month-1,day-(select-position));
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
                    year = Integer.parseInt(sdf.format(cal.getTime()));
                    sdf = new SimpleDateFormat("M");
                    month = Integer.parseInt(sdf.format(cal.getTime()));
                    sdf = new SimpleDateFormat("d");
                    day = Integer.parseInt(sdf.format(cal.getTime()));
                    //day -= select-position;
                    select = position;
                    now_week.setText(month+"월"+day+"일("+weekStr[select]+")");
                    setMeal(select);
                }
            });

            dialog = new SpotsDialog(this);

            breakfast = (TextView)findViewById(R.id.breakfast);
            lunch = (TextView)findViewById(R.id.lunch);
            dinner = (TextView)findViewById(R.id.dinner);
            now_week = (TextView)findViewById(R.id.now_week);
            TextView today = (TextView)findViewById(R.id.toDay);

            Calendar cal = Calendar.getInstance();
            cal.setTime(stan);

            select = cal.get(Calendar.DAY_OF_WEEK)-1;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
            year = Integer.parseInt(sdf.format(stan));
            sdf = new SimpleDateFormat("M");
            month = Integer.parseInt(sdf.format(stan));
            sdf = new SimpleDateFormat("d");
            day = Integer.parseInt(sdf.format(stan));

            today.setText("오늘은 "+month+"월"+day+"일("+weekStr[select]+")");
            now_week.setText(month+"월"+day+"일("+weekStr[select]+")");
            now_week.setSelected(true);

            JsoupAsyncTask task = new JsoupAsyncTask();
            task.execute();
        }

    }
/*
    protected String getWeekString(int year, int month,int day, int dayOfWeek){
        String result;

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf;

        cal.set(year,month-1,day-dayOfWeek);
        sdf = new SimpleDateFormat("M월d일(일) ");
        result = sdf.format(cal.getTime())+"~";
        cal.set(year,month-1,day-dayOfWeek+6);
        sdf = new SimpleDateFormat(" M월d일(토)");
        result += sdf.format(cal.getTime());

        return result;
    }
*/
    protected void setMeal(int dayOfWeek){
        try{
            if(m_arr[dayOfWeek][0].isEmpty()) breakfast.setText("정보가 없습니다.");
            else breakfast.setText(Html.fromHtml(m_arr[dayOfWeek][0]));
            if(m_arr[dayOfWeek][1].isEmpty()) lunch.setText("정보가 없습니다.");
            else lunch.setText(Html.fromHtml(m_arr[dayOfWeek][1]));
            if(m_arr[dayOfWeek][2].isEmpty()) dinner.setText("정보가 없습니다.");
            else dinner.setText(Html.fromHtml(m_arr[dayOfWeek][2]));
        }catch (Exception e){
            breakfast.setText("실행중 오류가 발생했습니다.\n종료 후 다시실행해 주세요.");
            lunch.setText("실행중 오류가 발생했습니다.\n종료 후 다시실행해 주세요.");
            dinner.setText("실행중 오류가 발생했습니다.\n종료 후 다시실행해 주세요.");
        }

    }

    public void weekBtnClick(View view) {
        Calendar cal = Calendar.getInstance();
        if(view.getId()==R.id.last_week){
            cal.set(year,month-1,day-7);
        }else if(view.getId()==R.id.next_week){
            cal.set(year,month-1,day+7);
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        year = Integer.parseInt(sdf.format(cal.getTime()));
        sdf = new SimpleDateFormat("M");
        month = Integer.parseInt(sdf.format(cal.getTime()));
        sdf = new SimpleDateFormat("d");
        day = Integer.parseInt(sdf.format(cal.getTime()));
        now_week.setText(month+"월"+day+"일("+weekStr[select]+")");
        JsoupAsyncTask task = new JsoupAsyncTask();
        task.execute();
    }

    private class JsoupAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
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
                        m_arr[day][meal] = sub_link.html().replace("<p class=\"special_menu_a\"","<pre class=\"special_menu_a\"")
                                .replace("<p cla","<br><pre cla")
                                .replace("\">","\" style=\"color:blue\"><b>")
                                .replace("</p>","</b></pre><br>");
                        //if(m_arr[day][meal].substring(0,3).equals("<br>")) m_arr[day][meal].replaceFirst("<br>","");
                        //}
                        day++;
                    }
                    day=0; meal++;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            setMeal(select);
            segmentedButtonGroup.setPosition(select);
            dialog.dismiss();
        }
    }

    // 인터넷
    public static String getWhatKindOfNetwork(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                return WIFE_STATE;
            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                return MOBILE_STATE;
            }
        }
        return NONE_STATE;
    }
}
