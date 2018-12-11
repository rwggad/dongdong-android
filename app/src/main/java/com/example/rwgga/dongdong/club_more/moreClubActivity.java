package com.example.rwgga.dongdong.club_more;

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
import android.widget.Toast;

import com.example.rwgga.dongdong.R;
import com.example.rwgga.dongdong.club.clubActivity;
import com.example.rwgga.dongdong.database_table.boardTable;
import com.example.rwgga.dongdong.database_table.clubListTable;
import com.example.rwgga.dongdong.database_table.clubusersTable;
import com.example.rwgga.dongdong.main.MainActivity;
import com.example.rwgga.dongdong.main.createClubActivity;
import com.example.rwgga.dongdong.php_server.getData;
import com.example.rwgga.dongdong.php_server.insertData;
import com.example.rwgga.dongdong.database_table.userTable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 *
 * 작성자 : Created by (rwggad@gmail.com - 김정환(KimJeongHwan)
 * 동아리 더보기1
 */
public class moreClubActivity extends AppCompatActivity implements View.OnClickListener {
    private static String ip_add = "10.0.2.2";
    private static String TAG = "phptest";
    /**
     * 안드로이드 객체*/
    private TextView cName, cCnt;
    private LinearLayout clubInfoBtn, clubUserBtn, memBtn, calBtn, outBtn, DeleteBtn;
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
        setContentView(R.layout.activity_more_club);
        setTitle("더보기");

        /**
         * 데이터 받아오기 */
        Intent intent = getIntent();
        club = (clubListTable) intent.getSerializableExtra("club");
        clubInfo = (clubusersTable) intent.getSerializableExtra("clubInfo");
        userData = (userTable) intent.getSerializableExtra("userData");

        /**
         * id 연결 */
        cName = (TextView) findViewById(R.id.more_layout_clubName);
        cCnt = (TextView) findViewById(R.id.more_layout_clubCnt);
        clubInfoBtn = (LinearLayout) findViewById(R.id.more_layout_btn0);
        clubUserBtn = (LinearLayout) findViewById(R.id.more_layout_btn1);
        memBtn = (LinearLayout) findViewById(R.id.more_layout_btn2);
        calBtn = (LinearLayout) findViewById(R.id.more_layout_btn3);
        outBtn = (LinearLayout) findViewById(R.id.more_layout_btn4);
        DeleteBtn = (LinearLayout) findViewById(R.id.more_layout_btn5);

        /**
         * 만약 해당 동아리의 장이 아닐경우 Delete Btn, memBtn 은 비활성화 */
        if(clubInfo.getIsStaff() != 1){
            DeleteBtn.setVisibility(View.GONE);
            outBtn.setVisibility(View.VISIBLE);
            memBtn.setVisibility(View.GONE);
        }else{
            DeleteBtn.setVisibility(View.VISIBLE);
            outBtn.setVisibility(View.GONE);
            memBtn.setVisibility(View.VISIBLE);
        }

        /**
         * 값 설정*/
        cName.setText(club.getClubName());
        cCnt.setText(String.valueOf(club.getClubUserCnt()));

        /**
         * 버튼 선택시*/
        clubInfoBtn.setOnClickListener(this);
        clubUserBtn.setOnClickListener(this);
        memBtn.setOnClickListener(this);
        calBtn.setOnClickListener(this);
        outBtn.setOnClickListener(this);
        DeleteBtn.setOnClickListener(this);

        /**
         * 툴바 뒤로가기 */
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    /**
     * 버튼 클릭시*/
    /**
     * 버튼 클릭시 */
    @Override
    public void onClick(View v) {
        /**
         * 객체 선언 */
        Intent intent;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        switch (v.getId()){
            case R.id.more_layout_btn0: // 동아리 소개
                intent = new Intent(getApplicationContext(), createClubActivity.class);
                intent.putExtra("club",club);
                intent.putExtra("clubInfo",clubInfo);
                intent.putExtra("userData",userData);
                intent.putExtra("option","modify");
                startActivity(intent);
                finish();
                break;
            case R.id.more_layout_btn1: // 멤버
                intent = new Intent(getApplicationContext(), moreClubUsersActivity.class);
                intent.putExtra("club",club);
                intent.putExtra("clubInfo",clubInfo);
                intent.putExtra("userData",userData);
                startActivity(intent);
                finish();
                break;
            case R.id.more_layout_btn2: // 멤버 보기
                intent = new Intent(getApplicationContext(), moreClubWaitActivity.class);
                intent.putExtra("club",club);
                intent.putExtra("clubInfo",clubInfo);
                intent.putExtra("userData",userData);
                startActivity(intent);
                finish();
                break;
            case R.id.more_layout_btn3: // 일정
                intent = new Intent(getApplicationContext(), moreClubCalendarActivity.class);
                intent.putExtra("club",club);
                intent.putExtra("clubInfo",clubInfo);
                intent.putExtra("userData",userData);
                startActivity(intent);
                finish();
                break;
            case R.id.more_layout_btn4: // 탈퇴
                builder.setTitle("정말로 동아리를 탈퇴하시겠습니까?\n 작성한 게시물을 삭제되지 않습니다!");
                builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        /**
                         * clubUsers에서 해당 회원 삭제 */
                        new insertData().execute("http://" + ip_add + "/clubusersDelete.php", "studentNumber=" + userData.getStudentNumber() + "&boardKind=" + club.getBoardKind());
                        /**
                         * 현재 동아리에서 회원수 감소*/
                        new insertData().execute("http://" + ip_add + "/clublistUpdate.php", "option=" + 0 + "&boardKind=" + club.getBoardKind());
                        /**
                         * 메인으로 이동*/
                        Toast.makeText(getApplicationContext(), "동아리 탈퇴 하셨습니다.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.putExtra("userData",userData);
                        startActivity(intent);
                        finish();
                    }
                });
                builder.setNegativeButton("아니요", null);
                builder.show();
                break;
            case R.id.more_layout_btn5: // 폐쇠
                /**
                 * 만약 자기 자신을 제외하고 회원이 한명이라도 있으면 삭제를 할 수 없다.*/
                if(club.getClubUserCnt() > 1){
                    Toast.makeText(getApplicationContext(), "아직 동아리에 회원이 남아 있습니다.\n동아리를 삭제 할 수 없습니다.", Toast.LENGTH_SHORT).show();
                }else{
                    builder.setTitle("정말로 동아리를 삭제하시겠습니까???\n데이터는 복구되지 않습니다.");
                    builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            /**
                             * 동아리 폐쇠ㅣ가  가능하다면
                             * 1. clubList 삭제*/
                            new insertData().execute("http://" + ip_add + "/clublistDelete.php", "boardKind=" + club.getBoardKind());
                            /**
                             * 2. clubUsers 삭제*/
                            new insertData().execute("http://" + ip_add + "/clubusersDelete.php", "studentNumber=" + userData.getStudentNumber() + "&boardKind=" + club.getBoardKind());
                            /**
                             * 등록된 게시물 id 가져오기 */
                            ArrayList<boardTable> boardList = new ArrayList<>();
                            try{
                                boardList = getBaordList(new getData().execute("http://" + ip_add + "/boardGet.php?boardKind=" + club.getBoardKind() + "", "").get());
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                            /**
                             * 3. 게시물 과 댓글 삭제*/
                            for(int i = 0; i < boardList.size(); i++){
                                new insertData().execute("http://" + ip_add + "/boardDelete.php", "boardID=" + boardList.get(i).getBoardID());
                            }
                            /**
                             * 4. 일정 삭제*/
                            new insertData().execute("http://" + ip_add + "/clubCalendarAllDelete.php", "boardKind=" + club.getBoardKind());

                            /**
                             * 메인으로 이동*/
                            Toast.makeText(getApplicationContext(), "동아리 삭제 하셨습니다.", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            intent.putExtra("userData",userData);
                            startActivity(intent);
                            finish();
                        }
                    });
                    builder.setNegativeButton("아니요", null);
                    builder.show();
                }
                break;
        }
    }
    /**
     * json to List
     */
    private ArrayList<boardTable> getBaordList(String Json){
        ArrayList<boardTable> List = new ArrayList<>();

        String TAG_JSON="root";
        String TAG_boardID = "boardID";
        String TAG_boardKind = "boardKind";
        String TAG_boardWriter="boardWriter";
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
     * 뒤로가기 */
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), clubActivity.class);
        intent.putExtra("clubInfo", clubInfo);
        intent.putExtra("userData", userData);
        startActivity(intent);
        finish();
    }
}
