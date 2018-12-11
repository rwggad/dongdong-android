package com.example.rwgga.dongdong.club;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.rwgga.dongdong.R;
import com.example.rwgga.dongdong.Adapters.boardTable_RecyclerAdapter;
import com.example.rwgga.dongdong.board.boardWriteActivity;
import com.example.rwgga.dongdong.database_table.boardTable;
import com.example.rwgga.dongdong.database_table.clubListTable;
import com.example.rwgga.dongdong.database_table.clubusersTable;
import com.example.rwgga.dongdong.main.MainActivity;
import com.example.rwgga.dongdong.club_more.moreClubActivity;
import com.example.rwgga.dongdong.php_server.getData;
import com.example.rwgga.dongdong.database_table.userTable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 *
 * 작성자 : Created by (손민성)
 *
 * 마지막 수정 날짜 : 2018. 11. 12~

 * 사용자가 특정 동아리를 선택해서 들어와지는 동아리 메인 페이지이다.
 *
 * Recycler로 현재 동아리에 개설된 게시물들을 들고오고 a
 *
 * 메인 페이지 처럼 flaot 버튼이 있음
 */
public class clubActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{
    private static String ip_add = "10.0.2.2";
    private static String TAG = "phptest";
    /**
     * 안드로이드 객체*/
    private SwipeRefreshLayout refresh;
    private RecyclerView club_board_rec;
    private TextView empty;
    //float 버튼
    private Boolean isFabOpen = false;
    private FloatingActionButton fab, fabSub1, fabSub2, fabSub3; // fab 설정
    private LinearLayout fabSub1_layout, fabSub2_layout, fabSub3_layout, fabFade;
    Animation showBtn, hideBtn, showLay, hideLay, fadeinLay, fadeoutLay;
    /**
     * 현재 사용자 정보 저장*/
    private userTable userData = new userTable();
    /**
     * 현재 동아리 정보 */
    private clubusersTable clubInfo = new clubusersTable();
    private clubListTable club = new clubListTable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club);
        /**
         * 툴바 연결*/
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /**
         * 툴바 뒤로가기 */
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        /**
         * id 연결 */
        club_board_rec = (RecyclerView) findViewById(R.id.club_layout_rev);
        refresh = (SwipeRefreshLayout) findViewById(R.id.refresh);

        /**
         * 세로고침 */
        refresh.setOnRefreshListener(this);
        /**

        /**
         * clubusersTable_RecyclerAdapter 에서 넘어온 데이터 받아오기 */
        Intent intent = getIntent();
        clubInfo = (clubusersTable) intent.getSerializableExtra("clubInfo");
        userData = (userTable) intent.getSerializableExtra("userData");

        /**
         * 현재 동아리 정보 가져오기*/
        try{
            club = getClub(new getData().execute("http://" + ip_add + "/clubGet.php?boardKind=" + clubInfo.getBoardKind() + "", "").get());
        }catch (Exception e){
            e.printStackTrace();
        }

        /**
         * 액티비티 이름 설정*/
        setTitle(club.getClubName());

        /**
         * 리사이클러 큐 연결 (해당 boardKind에 맞는 게시물 리스트 받아오기) */
        initData();

        /**
         * FloatingActionButton 설정 */
        fab = (FloatingActionButton) findViewById(R.id.club_layout_fab);
        fabSub1 = (FloatingActionButton) findViewById(R.id.club_layout_fabSub1);
        fabSub2 = (FloatingActionButton) findViewById(R.id.club_layout_fabSub2);
        fabSub3 = (FloatingActionButton) findViewById(R.id.club_layout_fabSub3);
        fabSub1_layout = (LinearLayout) findViewById(R.id.club_layout_fabSub1_layout);
        fabSub2_layout = (LinearLayout) findViewById(R.id.club_layout_fabSub2_layout);
        fabSub3_layout = (LinearLayout) findViewById(R.id.club_layout_fabSub3_layout);
        fabFade = (LinearLayout) findViewById(R.id.club_layout_fading);

        // 애니메이션 설정
        showBtn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.show_button);
        hideBtn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.hide_button);
        showLay = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.show_layout);
        hideLay = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.hide_layout);
        fadeinLay = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fadein_layout);
        fadeoutLay = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fadeout_layout);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                anim();
            }
        });

        fabSub1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                anim();
                /**
                 * 게시물 작성 */
                Intent intent = new Intent(getApplicationContext(), boardWriteActivity.class);
                intent.putExtra("option","write");
                intent.putExtra("move","listView");
                intent.putExtra("clubInfo", clubInfo);
                intent.putExtra("userData",userData);
                startActivity(intent);
                finish();
            }
        });

        fabSub2.setOnClickListener(new View.OnClickListener() { // 일정보기
            @Override
            public void onClick(View v) {
                anim();
                Intent intent = new Intent(getApplicationContext(), clubCalendarActivity.class);
                intent.putExtra("clubInfo", clubInfo);
                intent.putExtra("userData",userData);
                intent.putExtra("date", "");
                startActivity(intent);
                finish();
            }
        });

        fabSub3.setOnClickListener(new View.OnClickListener() { // 더보기
            @Override
            public void onClick(View v) {
                anim();
                Intent intent = new Intent(getApplicationContext(), moreClubActivity.class);
                intent.putExtra("club", club);
                intent.putExtra("clubInfo", clubInfo);
                intent.putExtra("userData",userData);
                startActivity(intent);
                finish();
            }
        });
    }
    @Override
    public void onRefresh() {
        Intent intent = new Intent(getApplicationContext(), clubActivity.class);
        intent.putExtra("clubInfo", clubInfo);
        intent.putExtra("userData", userData);
        startActivity(intent);
        overridePendingTransition(0,0);
        refresh.setRefreshing(false); // 새로고침 완료
    }
    /**
     * tool Bar 연동*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_club, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()){
            /**
             * 툴바에서 뒤로가기 버튼 눌렀을 때*/
            case android.R.id.home :
                /**
                 * 메인 페이지로 */
                onBackPressed();
                break;
            case R.id.action_settings:
                intent = new Intent(getApplicationContext(), boardWriteActivity.class);
                /**
                 * 동아리에서 게시물 작성 으로 */
                intent.putExtra("option","write");
                intent.putExtra("move","listView");
                intent.putExtra("userData",userData);
                intent.putExtra("clubInfo", clubInfo);
                startActivity(intent);
                finish();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * fab 애니메이션 설정
     * startAnimation : 버튼 애니메이션 실행
     * setClickable : 버튼 활성화 비활성화
     * */
    public void anim(){
        if(isFabOpen){ // fab이 열려있을 때
            /**
             * 만약 fab 버튼이 열려있는 상태로 anim함수가 호출될때 ( 어떤 버튼이 클릭 될 때 )
             * sub1 sbu2 버튼을 닫고 버튼이 닫혀있다는 상태로 바꾸어줌 (isFabOpen = false)*/
            fabSub1_layout.setVisibility(View.GONE);
            fabSub2_layout.setVisibility(View.GONE);
            fabSub3_layout.setVisibility(View.GONE);
            fabFade.setVisibility(View.GONE);
            fabSub1_layout.startAnimation(hideLay);
            fabSub2_layout.startAnimation(hideLay);
            fabSub3_layout.startAnimation(hideLay);
            fabFade.startAnimation(fadeoutLay);
            fab.startAnimation(hideBtn);
            fabSub1.setClickable(false);
            fabSub2.setClickable(false);
            fabSub3.setClickable(false);
            isFabOpen = false;
        }else{ // fab이 닫혀있을 때
            fabSub1_layout.setVisibility(View.VISIBLE);
            fabSub2_layout.setVisibility(View.VISIBLE);
            fabSub3_layout.setVisibility(View.VISIBLE);
            fabFade.setVisibility(View.VISIBLE);
            fabSub1_layout.startAnimation(showLay);
            fabSub2_layout.startAnimation(showLay);
            fabSub3_layout.startAnimation(showLay);
            fabFade.startAnimation(fadeinLay);
            fab.startAnimation(showBtn);
            fabSub1.setClickable(true);
            fabSub2.setClickable(true);
            fabSub3.setClickable(true);
            isFabOpen = true;
        }
    }
    /**
     * 데이터 초기화
     */
    private void initData(){
        ArrayList<boardTable> boardList = new ArrayList<>();
        /**
         * 현재 kind에 맞는 동아리의 게시물들의 정보를 모두 불러와서 boardList에 저장한다*/
        try{
            boardList = getBoardList(new getData().execute("http://" + ip_add + "/boardGet.php?boardKind=" + clubInfo.getBoardKind() + "", "").get());
        }catch (Exception e){
            e.printStackTrace();
        }

        /**
         * 게시물 리스트 값 저장*/
        club_board_rec.setAdapter(new boardTable_RecyclerAdapter(userData, boardList, clubInfo, club, R.layout.board_item, R.layout.club_main_item));
        club_board_rec.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        club_board_rec.setItemAnimator(new DefaultItemAnimator());

    }
    /**
     * json to List */
    private ArrayList<boardTable> getBoardList(String Json){
        ArrayList<boardTable> List = new ArrayList<>();

        String TAG_JSON="root";
        String TAG_boardID = "boardID";
        String TAG_boardKind = "boardKind";
        String TAG_boardWriter ="boardWriter";
        String TAG_boardTitle ="boardTitle";
        String TAG_boardContent = "boardContent";
        String TAG_boardDate = "boardDate";
        String TAG_boardCount = "boardCount";
        String TAG_isDelete = "isDelete";

        try {
            JSONObject jsonObject = new JSONObject(Json);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject item = jsonArray.getJSONObject(i);
                // Josn 으로 변환된 값들 저장
                int boardID = item.getInt(TAG_boardID);
                int boardKind = item.getInt(TAG_boardKind);
                String boardTitle = item.getString(TAG_boardTitle);
                String boardWriter = item.getString(TAG_boardWriter);
                String boardContent = item.getString(TAG_boardContent);
                String boardDate = item.getString(TAG_boardDate);
                int boardCount = item.getInt(TAG_boardCount);
                int isDelete = item.getInt(TAG_isDelete);

                // Map에 저장 ( 현재 DB에 있는 학번 저장 )
                boardTable boardTables = new boardTable();
                boardTables.setBoardID(boardID);
                boardTables.setBoardKind(boardKind);
                boardTables.setBoardTitle(boardTitle);
                boardTables.setBoardContent(boardContent);
                boardTables.setBoardDate(boardDate);
                boardTables.setBoardCount(boardCount);
                boardTables.setBoardWriter(boardWriter);
                boardTables.setIsDelete(isDelete);
                List.add(boardTables);

            }
        } catch (JSONException e) {
            Log.d(TAG, "showResult : ", e);
        }
        return List;
    }
    /**
     * 해당 클럽의 정보 받아오기*/
    private clubListTable getClub(String Json){
        clubListTable club = new clubListTable();

        String TAG_JSON="root";
        String TAG_boardKind = "boardKind";
        String TAG_clubCaptain = "clubCaptain";
        String TAG_clubName ="clubName";
        String TAG_clubContent = "clubContent";
        String TAG_clubLocation = "clubLocation";
        String TAG_clubPhoneNumber = "clubPhoneNumber";
        String TAG_clubUserCnt = "clubUserCnt";
        String TAG_clubKind = "clubKind";

        try {
            JSONObject jsonObject = new JSONObject(Json);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject item = jsonArray.getJSONObject(i);
                // Josn 으로 변환된 값들 저장
                int boardKind = item.getInt(TAG_boardKind);
                String clubCaptain = item.getString(TAG_clubCaptain);
                String clubName = item.getString(TAG_clubName);
                String clubContent = item.getString(TAG_clubContent);
                String clubLocation = item.getString(TAG_clubLocation);
                String clubPhoneNumber = item.getString(TAG_clubPhoneNumber);
                int clubUserCnt = item.getInt(TAG_clubUserCnt);
                int clubKind = item.getInt(TAG_clubKind);

                // Map에 저장
                club.setBoardKind(boardKind);
                club.setClubCaptain(clubCaptain);
                club.setClubName(clubName);
                club.setClubContent(clubContent);
                club.setClubLocation(clubLocation);
                club.setClubPhoneNumber(clubPhoneNumber);
                club.setClubUserCnt(clubUserCnt);
                club.setClubKind(clubKind);
            }
        } catch (JSONException e) {
            Log.d(TAG, "showResult : ", e);
        }
        return club;
    }
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra("userData",userData);
        startActivity(intent);
        finish();
    }
}
