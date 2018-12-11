package com.example.rwgga.dongdong.club_more;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rwgga.dongdong.R;
import com.example.rwgga.dongdong.club.clubCalendarActivity;
import com.example.rwgga.dongdong.database_table.calendarTable;
import com.example.rwgga.dongdong.database_table.clubListTable;
import com.example.rwgga.dongdong.database_table.clubusersTable;
import com.example.rwgga.dongdong.database_table.userTable;
import com.example.rwgga.dongdong.php_server.getData;
import com.example.rwgga.dongdong.php_server.insertData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
/**
 *
 * 작성자 : Created by (손민성)
 *
 * 마지막 수정 날짜 : 2018. 11. 25~
 *
 * 일정 캘린더
 */
public class moreClubCalendarActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{
    private static String ip_add = "10.0.2.2";
    private static String TAG = "phptest";
    /**
     * 안드로이드 객체*/
    private RecyclerView rec;
    private TextView empty;
    private SwipeRefreshLayout refresh;

    /**
     * 현재 사용자 정보 저장*/
    private userTable userData = new userTable();
    /**
     * 현재 동아리 정보 */
    private clubusersTable clubInfo = new clubusersTable();
    private clubListTable club = new clubListTable();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_club_calendar);
        setTitle("일정 모아 보기");

        /**
         * 데이터 받아오기 */
        Intent intent = getIntent();
        club = (clubListTable) intent.getSerializableExtra("club");
        clubInfo = (clubusersTable) intent.getSerializableExtra("clubInfo");
        userData = (userTable) intent.getSerializableExtra("userData");

        /**
         * id 연결*/
        empty = (TextView) findViewById(R.id.layout_empty);
        rec = (RecyclerView) findViewById(R.id.layout_rev);
        refresh = (SwipeRefreshLayout) findViewById(R.id.refresh);

        /**
         * 세로고침 */
        refresh.setOnRefreshListener(this);

        /**
         * recycler*/
        initData();

        /**
         * 툴바 뒤로가기 */
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    /**
     * 데이터 초기화
     */
    private void initData(){
        ArrayList<calendarTable> calendarList = new ArrayList<>();
        try{
            /**
             * 자기 자신 제외하고 검색 */
            calendarList = getCalendar(new getData().execute("http://" + ip_add + "/clubCalendarAllCLubGet.php?boardKind=" + club.getBoardKind(), "").get());
        }catch (Exception e){
            e.printStackTrace();
        }
        /**
         * 자기가 작성한 게시물이 없다면 */
        if(calendarList.size() > 0){
            refresh.setVisibility(View.VISIBLE);
            empty.setText("");
        }else{
            refresh.setVisibility(View.GONE);
            empty.setText("등록된 일정이 없습니다..");
        }
        rec.setAdapter(new RecyclerAdapter(calendarList, R.layout.calendar_item));
        rec.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        rec.setItemAnimator(new DefaultItemAnimator());
    }
    /**
     * 새로고침 */
    @Override
    public void onRefresh() {
        Intent intent = new Intent(getApplicationContext(), moreClubCalendarActivity.class);
        intent.putExtra("club",club);
        intent.putExtra("clubInfo",clubInfo);
        intent.putExtra("userData",userData);
        startActivity(intent);
        overridePendingTransition(0,0);
        refresh.setRefreshing(false); // 새로고침 완료
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
                                Intent intent_more = new Intent(getApplicationContext(), moreClubCalendarActivity.class);
                                intent_more.putExtra("club",club);
                                intent_more.putExtra("clubInfo",clubInfo);
                                intent_more.putExtra("userData",userData);
                                startActivity(intent_more);
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
     * 툴바 설정 */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            /**
             * 툴바에서 뒤로가기 버튼 눌렀을 때*/
            case android.R.id.home :
                onBackPressed();
                break;

        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), moreClubActivity.class);
        intent.putExtra("club",club);
        intent.putExtra("clubInfo",clubInfo);
        intent.putExtra("userData",userData);
        startActivity(intent);
        finish();
    }
}
