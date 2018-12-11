package com.example.rwgga.dongdong.main_more;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.rwgga.dongdong.main.LoginActivity;
import com.example.rwgga.dongdong.R;
import com.example.rwgga.dongdong.database_table.clubusersTable;
import com.example.rwgga.dongdong.main.MainActivity;
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
 * 마지막 수정 날짜 : 2018. 11. 15
 *
 *
 * 메인 페이지 더보기 페이지
 */
public class moreActivity extends AppCompatActivity implements View.OnClickListener {
    /**
     * 변수*/
    private static String ip_add = "10.0.2.2"; // php 통신을 위한 안드로이드 아이피 정보
    private static String TAG = "phptest";
    /**
     * 안드로이드 객체*/
    private TextView userInfo, userClub;
    private LinearLayout userBtn, noticeBtn, infoBtn, myClubBtn, myWriteBtn, logoutBtn, outBtn;

    /**
     * 사용자 정보*/
    private userTable userData = new userTable();
    /**
     * 현재 사용자가 운영 (가입된) 동아리 목록*/
    private ArrayList<clubusersTable> userClubs = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more);
        setTitle("더보기");
        /**
         * 데이터 받아오기 */
        Intent intent = getIntent();
        userData = (userTable)intent.getSerializableExtra("userData");

        /**
         * id 연결*/
        userBtn = (LinearLayout) findViewById(R.id.more_layout_btn0);
        noticeBtn = (LinearLayout) findViewById(R.id.more_layout_btn1);
        infoBtn = (LinearLayout) findViewById(R.id.more_layout_btn2);
        myClubBtn= (LinearLayout) findViewById(R.id.more_layout_btn3);
        myWriteBtn= (LinearLayout) findViewById(R.id.more_layout_btn4);
        logoutBtn = (LinearLayout) findViewById(R.id.more_layout_btn5);
        outBtn = (LinearLayout) findViewById(R.id.more_layout_btn6);
        userInfo = (TextView) findViewById(R.id.more_layout_Name_Number);
        userClub = (TextView) findViewById(R.id.more_layout_clubCnt);

        /**
         * 현재 사용자가 운영중인(가입된) 동아리 목록을 가져오기 */
        try{
            userClubs = getMyClubList(new getData().execute("http://" + ip_add + "/clubusersGet.php?studentNumber='" + userData.getStudentNumber() + "'", "").get());
        }catch (Exception e){
            e.printStackTrace();
        }

        /**
         * 사용자 정보 업데이트*/
        userInfo.setText(userData.getStudentName() + "(" + userData.getStudentNumber() + ")"); // 이름 학번
        userClub.setText(String.valueOf(userClubs.size()));

        /**
         * 버튼 선택시*/
        userBtn.setOnClickListener(this);
        noticeBtn.setOnClickListener(this);
        myClubBtn.setOnClickListener(this);
        myWriteBtn.setOnClickListener(this);
        infoBtn.setOnClickListener(this);
        logoutBtn.setOnClickListener(this);
        outBtn.setOnClickListener(this);

        /**
         * 툴바 뒤로가기 */
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }
    /**
     * 버튼 클릭시 */
    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()){
            case R.id.more_layout_btn0:
                break;
            case R.id.more_layout_btn1: // 공지사항
                modal_show("준비중입니다.", "notice");
                break;
            case R.id.more_layout_btn2: // 어플리케이션 정보
                modal_show("준비중입니다.", "info");
                break;
            case R.id.more_layout_btn3: // 나의 신청 내역
                intent = new Intent(getApplicationContext(), moreMywaitActivity.class);
                intent.putExtra("userData", userData);
                startActivity(intent);
                finish();
                break;
            case R.id.more_layout_btn4: // 내가 쓴 글
                intent = new Intent(getApplicationContext(), moreMyboardActivity.class);
                intent.putExtra("userData", userData);
                intent.putExtra("userClubs",userClubs);
                startActivity(intent);
                finish();
                break;
            case R.id.more_layout_btn5: // 로그아웃
                modal_show("로그아웃 하시겠습니까?", "logout");
                break;
            case R.id.more_layout_btn6: // 탈퇴
                intent = new Intent(getApplicationContext(), moreOutActivity.class);
                intent.putExtra("userData", userData);
                intent.putExtra("userClubs",userClubs);
                startActivity(intent);
                finish();
                break;
        }
    }

    /**
     * 사용자가 가입한 동아리 정보 가져오기 */
    private ArrayList<clubusersTable> getMyClubList(String Json){
        ArrayList<clubusersTable> List = new ArrayList<>();

        String TAG_JSON="root";
        String TAG_infoID = "infoID";
        String TAG_boardKind = "boardKind";
        String TAG_clubName = "clubName";
        String TAG_studentNumber = "studentNumber";
        String TAG_isStaff = "isStaff";

        try {
            JSONObject jsonObject = new JSONObject(Json);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject item = jsonArray.getJSONObject(i);
                clubusersTable table = new clubusersTable();
                // Josn 으로 변환된 값들 저장
                int infoID = item.getInt(TAG_infoID);
                int boardKind = item.getInt(TAG_boardKind);
                String clubName = item.getString(TAG_clubName);
                String studentNumber = item.getString(TAG_studentNumber);
                int isStaff = item.getInt(TAG_isStaff);
                table.setInfoID(infoID);
                table.setBoardKind(boardKind);
                table.setClubName(clubName);
                table.setStudentNumber(studentNumber);
                table.setIsStaff(isStaff);
                List.add(table);
            }
        } catch (JSONException e) {
            Log.d(TAG, "showResult : ", e);
        }
        return List;
    }

    /**
     * 알림 창*/
    public void modal_show(String modal_title, final String option){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(modal_title);
        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (option.equals("notice")){

                }else if(option.equals("info")){

                } if (option.equals("logout")) {
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
        builder.setNegativeButton("아니요", null);
        builder.show();
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

    /**
     * 뒤로가기 버튼 */
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra("userData",userData);
        startActivity(intent);
        finish();
    }
}
