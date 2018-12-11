package com.example.rwgga.dongdong.club;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rwgga.dongdong.R;
import com.example.rwgga.dongdong.database_table.calendarTable;
import com.example.rwgga.dongdong.database_table.clubusersTable;
import com.example.rwgga.dongdong.decorators.EventDecorator;
import com.example.rwgga.dongdong.decorators.OneDayDecorator;
import com.example.rwgga.dongdong.decorators.SaturdayDecorator;
import com.example.rwgga.dongdong.decorators.SundayDecorator;
import com.example.rwgga.dongdong.php_server.getData;
import com.example.rwgga.dongdong.php_server.insertData;
import com.example.rwgga.dongdong.database_table.userTable;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
/**
 *
 * 작성자 : Created by (rwggad@gmail.com - 김정환(KimJeongHwan)
 * 마지막 수정 날짜 : 2018. 11. 24~
 *
 * 캘린더 액티비티
 */
public class clubCalendarActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    /**
     * 변수 */
    private static String ip_add = "10.0.2.2";
    private static String TAG = "phptest";
    private String date, select_date;

    /**
     * 안드로이드 객체 */
    MaterialCalendarView materialCalendarView;
    private RecyclerView rec;
    private TextView empty;
    private SwipeRefreshLayout refresh;
    /**
     * 현재 사용자 정보 저장*/
    private userTable userData = new userTable();
    /**
     * 현재 동아리 정보 */
    private clubusersTable clubInfo = new clubusersTable();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club_calendar);
        setTitle("일정 보기");

        /**
         *  데이터 받아오기 */
        Intent intent = getIntent();
        clubInfo = (clubusersTable) intent.getSerializableExtra("clubInfo");
        userData = (userTable) intent.getSerializableExtra("userData");
        date = intent.getStringExtra("date");

        /**
         * id 연결 */
        materialCalendarView = (MaterialCalendarView)findViewById(R.id.calendarView);
        rec = (RecyclerView) findViewById(R.id.cal_rev);
        empty = (TextView) findViewById(R.id.cal_empty);
        refresh = (SwipeRefreshLayout) findViewById(R.id.refresh);


        /**
         * 만약 intent에서 date 값이 넘어오면 그 날짜에 맞는 일정 보여주고 안넘어오면 오늘 날짜 일정을 보여줌 */
        if(date.equals("")){
            select_date = getDate();
            GetCalendarList(select_date);
        }else{
            select_date = date;
            GetCalendarList(select_date);
        }

        /**
         * 세로고침 */
        refresh.setOnRefreshListener(this);

        /**
         * DB에 일정들 전부 가져오기*/
        ArrayList<String> DateLists = new ArrayList<>();
        try{
            DateLists = getDateList(new getData().execute("http://" + ip_add + "/clubCalendarAllGet.php?boardKind=" + clubInfo.getBoardKind(), "").get());
        }catch (Exception e){
            System.out.println(e.toString());
        }

        /**
         * 달력 셋팅*/
        materialCalendarView.state().edit()
                .setFirstDayOfWeek(Calendar.SUNDAY)
                .setMinimumDate(CalendarDay.from(2017, 0, 1)) // 달력의 시작
                .setMaximumDate(CalendarDay.from(2030, 11, 31)) // 달력의 끝
                .setCalendarDisplayMode(CalendarMode.MONTHS)
                .commit();

        /**
         * 달력에 버튼 색 지정해줌 */
        materialCalendarView.addDecorators(
                new SundayDecorator(),
                new SaturdayDecorator(),
                new OneDayDecorator());

        /**
         * 현재 있는 일정들 화면에 나타내기*/
        if(DateLists.size() != 0){
            final String[] result = new String[DateLists.size()];
            DateLists.toArray(result);
            new ApiSimulator(result).executeOnExecutor(Executors.newSingleThreadExecutor());
        }

        /**EventDecorator
         * 해당 날짜 클릭시 일정 들고오기 */
        materialCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                int Year = date.getYear();
                int Month = date.getMonth() + 1;
                int Day = date.getDay();
                select_date = Year + "-" + Month + "-" + Day; // 날짜 형식 지정
                materialCalendarView.clearSelection();
                GetCalendarList(select_date);
            }
        });

        /**
         * 툴바 뒤로가기 */
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    public void GetCalendarList(String select_day){
        /**
         * 선택한 날짜 의 일정 가져오기*/
        ArrayList<calendarTable> calendarList = new ArrayList<>();
        try{
            calendarList = getCalendar(new getData().execute("http://" + ip_add + "/clubCalendarGet.php?calDate='" + select_day + "'&boardKind=" + clubInfo.getBoardKind(),"").get());
        }catch (Exception e){
            e.printStackTrace();
        }
        /**
         * recycler로 나타내기*/
        if(calendarList.size() > 0){
            refresh.setVisibility(View.VISIBLE);
            empty.setText("");
        }else{
            refresh.setVisibility(View.GONE);
            empty.setText("이벤트 없음");
        }
        rec.setAdapter(new RecyclerAdapter(calendarList, R.layout.calendar_item));
        rec.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        rec.setItemAnimator(new DefaultItemAnimator());
    }
    /**
     * 현재 시간 가져오기 */
    public String getDate(){
        Date today = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(today);
    }

    /**
     * 팝업창이 꺼지면 result값 받아오기*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==1){
            if(resultCode==RESULT_OK){
                Toast.makeText(getApplicationContext(),"데이터 삽입",Toast.LENGTH_SHORT).show();
                /**
                 * 데이터 받아오고*/
                int calID = 0;
                String calDate = data.getStringExtra("param_calDate");
                String calContent = data.getStringExtra("param_calContent");
                /**
                 * 다음 calID 가져오기 */
                try{
                    calID = getCalendarID(new getData().execute("http://" + ip_add + "/clubCalendarIDGet.php","").get());
                }catch (Exception e){
                    e.printStackTrace();
                }
                /**
                 * DB에 삽입 */
                new insertData().execute("http://" + ip_add + "/clubCalendarInsert.php", "calID=" + calID + "&boardKind=" + clubInfo.getBoardKind() + "&calDate=" + calDate + "&calContent=" + calContent);
                /**
                 * 액티비티 새로고침*/

                Intent intent = new Intent(getApplicationContext(), clubCalendarActivity.class);
                intent.putExtra("clubInfo", clubInfo);
                intent.putExtra("userData",userData);
                intent.putExtra("date",calDate);
                startActivity(intent);
                finish();
            }
        }
    }

    /**
     * 현재 DB에 입력되어있는 일정들을 가져와서
     * 달력에 보여준다. */
    private class ApiSimulator extends AsyncTask<Void, Void, List<CalendarDay>> {
        String[] Time_Result;

        ApiSimulator(String[] Time_Result){
            this.Time_Result = Time_Result;
        }

        @Override
        protected List<CalendarDay> doInBackground(@NonNull Void... voids) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            String[] temp = Time_Result[Time_Result.length-1].split("-");
            Calendar calendar = Calendar.getInstance();
            calendar.set(Integer.parseInt(temp[0]),Integer.parseInt(temp[1])-1,Integer.parseInt(temp[2]));
            ArrayList<CalendarDay> dates = new ArrayList<>();
            /*특정날짜 달력에 점표시해주는곳*/
            /*월은 0이 1월 년,일은 그대로*/
            //string 문자열인 Time_Result 을 받아와서 ,를 기준으로짜르고 string을 int 로 변환
            //System.out.println(calendar.);
            for(int i = 0 ; i < Time_Result.length ; i ++){
                CalendarDay day = CalendarDay.from(calendar);
                String[] time = Time_Result[i].split("-");
                int year = Integer.parseInt(time[0]);
                int month = Integer.parseInt(time[1]);
                int dayy = Integer.parseInt(time[2]);
                dates.add(day);
                calendar.set(year,month-1,dayy);
            }
            return dates;
        }

        @Override
        protected void onPostExecute(@NonNull List<CalendarDay> calendarDays) {
            super.onPostExecute(calendarDays);

            if (isFinishing()) {
                return;
            }

            materialCalendarView.addDecorator(new EventDecorator(Color.BLACK, calendarDays,clubCalendarActivity.this));
        }
    }

    /**
     * 해당 날짜의 일정 가져오기*/
    private  ArrayList<calendarTable> getCalendar(String Json){
        ArrayList<calendarTable> List = new ArrayList<>();
        String TAG_JSON="root";
        String TAG_calID = "calID";
        String TAG_boardKind = "boardKind";
        String TAG_calDate = "calDate";
        String TAG_calContent = "calContent";
        try {
            JSONObject jsonObject = new JSONObject(Json);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject item = jsonArray.getJSONObject(i);
                // Josn 으로 변환된 값들 저장
                int calID = item.getInt(TAG_calID);
                int boardKind = item.getInt(TAG_boardKind);
                String calDate = item.getString(TAG_calDate);
                String calContent = item.getString(TAG_calContent);
                // Map에 저장
                calendarTable calendar = new calendarTable();
                calendar.setCalID(calID);
                calendar.setBoardKind(boardKind);
                calendar.setCalDate(calDate);
                calendar.setCalContent(calContent);
                List.add(calendar);
            }
        } catch (JSONException e) {
            Log.d(TAG, "showResult : ", e);
        }
        return List;
    }

    /**
     * 다음 캘린더 ID 가져오기*/
    private Integer getCalendarID(String Json){
        ArrayList<Integer> List = new ArrayList<>();
        String TAG_JSON="root";
        String TAG_calID = "calID";
        try {
            JSONObject jsonObject = new JSONObject(Json);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject item = jsonArray.getJSONObject(i);
                // Josn 으로 변환된 값들 저장
                int calID = item.getInt(TAG_calID);
                // Map에 저장
                List.add(calID);
            }
        } catch (JSONException e) {
            Log.d(TAG, "showResult : ", e);
        }
        if(List.size() == 0)
            return 1;
        else{
            return List.get(0) + 1;
        }
    }

    /**
     * 현재 DB에 있는 일정 정보 모두 가져오기 */
    private ArrayList<String> getDateList(String Json){
        ArrayList<String> List = new ArrayList<>();
        String TAG_JSON="root";
        String TAG_calDate = "calDate";
        try {
            JSONObject jsonObject = new JSONObject(Json);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject item = jsonArray.getJSONObject(i);
                String calDate = item.getString(TAG_calDate); // json에서 calDate에 해당하는 태그들 들고와서 저장
                List.add(calDate);
            }
        } catch (JSONException e) {
            Log.d(TAG, "showResult : ", e);
        }
        return List;
    }

    /**
     * Adapter -------------------------------------------------------------------------------------------- */
    class RecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
        private static final int TYPE_ROOT = 1, TYPE_LIST = 2;
        private ArrayList<calendarTable> calendarList;
        private int layout1;

        /**
         * 생성자*/
        public RecyclerAdapter(ArrayList<calendarTable> calendarList, int layout1){
            this.calendarList = calendarList;
            this.layout1 = layout1;
        }

        /**
         * 뷰 타입 지정*/
        @Override
        public int getItemViewType(int position) {
            return TYPE_LIST;
        }

        /**
         * 뷰 홀더 생성 지정*/
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
            if(viewType == TYPE_LIST){
                View itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(layout1, viewGroup, false);
                return new ListHolder(itemView);
            }
            return null;
        }

        /**
         * 데이터 매칭*/
        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, int i) {
            if (viewHolder instanceof ListHolder) {
                final calendarTable calendarData = calendarList.get(i);
                ((ListHolder) viewHolder).textContent.setText(calendarData.getCalContent() + " (" + calendarData.getCalDate() +")");

                /**
                 * 동아리 장만 일정 삭제 가능*/
                if(clubInfo.getIsStaff() != 1) {
                    ((ListHolder)viewHolder).deleteBtn.setVisibility(View.GONE);
                }else{
                    ((ListHolder)viewHolder).deleteBtn.setVisibility(View.VISIBLE);
                }

                /**
                 * 일정 삭제*/
                ((ListHolder) viewHolder).deleteBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((ListHolder) viewHolder).builder.setTitle("정말로 삭제하시겠습니까?");
                        ((ListHolder) viewHolder).builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                /**
                                 * 일정 삭제 */
                                new insertData().execute("http://" + ip_add + "/clubCalendarDelete.php", "calID=" + calendarData.getCalID());
                                Toast.makeText(((ListHolder)viewHolder).context, "일정을 삭제 했습니다.", Toast.LENGTH_SHORT).show();
                                /**
                                 * 새로고침 */
                                Intent intent = new Intent(getApplicationContext(), clubCalendarActivity.class);
                                intent.putExtra("userData", userData);
                                intent.putExtra("clubInfo", clubInfo);
                                intent.putExtra("date", calendarData.getCalDate());
                                startActivity(intent);
                                overridePendingTransition(0,0);
                            }
                        });
                        ((ListHolder) viewHolder).builder.setNegativeButton("아니요", null);
                        ((ListHolder) viewHolder).builder.show();
                    }
                });
            }
        }

        /**
         * 뷰에 나타낼 데이터의 갯수*/
        @Override
        public int getItemCount() {
            return calendarList.size();
        }

        /**
         * Holder 생성*/
        public class ListHolder extends RecyclerView.ViewHolder{

            public AlertDialog.Builder builder;
            public Intent intent_view;
            public TextView textContent;
            public LinearLayout deleteBtn;
            public Context context;
            /**
             * 생성자 id 지정*/
            public ListHolder(@NonNull View itemView) {
                super(itemView);
                builder = new AlertDialog.Builder(itemView.getContext());
                context = itemView.getContext();
                intent_view = new Intent(itemView.getContext(), clubCalendarActivity.class);
                textContent = (TextView) itemView.findViewById(R.id.cal_item_content);
                deleteBtn = (LinearLayout) itemView.findViewById(R.id.cal_item_delete);
            }
        }
    }

    /**
     * tool Bar 연동*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(clubInfo.getIsStaff() == 1){
            getMenuInflater().inflate(R.menu.menu_club, menu);
        }
        return true;
    }

    /**
     * 툴바 설정*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            /**
             * 툴바에서 뒤로가기 버튼 눌렀을 때*/
            case android.R.id.home :
                /**
                 * 클럽 페이지로 */
                onBackPressed();
                break;
            case R.id.action_settings:
                if(clubInfo.getIsStaff() == 1) {
                    /**
                     * 일정 작성하기 */
                    Intent intent = new Intent(getApplicationContext(), clubCalendarPopup.class);
                    intent.putExtra("date", getDate());
                    startActivityForResult(intent, 1);
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 새로고침 */
    @Override
    public void onRefresh() {
        Intent intent = new Intent(getApplicationContext(), clubCalendarActivity.class);
        intent.putExtra("userData", userData);
        intent.putExtra("clubInfo", clubInfo);
        intent.putExtra("date", select_date);
        startActivity(intent);
        overridePendingTransition(0,0);
        refresh.setRefreshing(false); // 새로고침 완료
    }

    /**
     * 뒤로가기 */
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), clubActivity.class);
        intent.putExtra("clubInfo", clubInfo);
        intent.putExtra("userData",userData);
        startActivity(intent);
        finish();
    }
}
