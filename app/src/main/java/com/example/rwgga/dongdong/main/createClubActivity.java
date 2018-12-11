package com.example.rwgga.dongdong.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.rwgga.dongdong.R;
import com.example.rwgga.dongdong.club.clubActivity;
import com.example.rwgga.dongdong.club_more.moreClubActivity;
import com.example.rwgga.dongdong.database_table.clubListTable;
import com.example.rwgga.dongdong.database_table.clubusersTable;
import com.example.rwgga.dongdong.database_table.userTable;
import com.example.rwgga.dongdong.php_server.getData;
import com.example.rwgga.dongdong.php_server.insertData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * 작성자 : Created by (rwggad@gmail.com - 김정환(KimJeongHwan)
 * 클럽 생성 액티비티 - 2
 */
public class createClubActivity extends AppCompatActivity {
    private static String ip_add = "10.0.2.2";
    private static String TAG = "phptest";
    private String option;
    /**
     * 안드로이드 객체*/
    private ConstraintLayout back_btn, create_btn;
    private EditText cName, cContent, cLocation, cPNumber;
    /**
     * 현재 사용자 정보 저장*/
    private userTable userData = new userTable();
    /**
     * 현재 생성하는 동아리 정보 저장*/
    private clubusersTable clubInfo = new clubusersTable();
    private clubListTable club = new clubListTable();
    /**
     * 사용자가 설정한 동아리 분류*/
    private int clubKind;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_club);

        /**
         * MainActivity 넘어온 현재 사용자 학번을 저장한다. */
        Intent intent = getIntent();
        option = intent.getStringExtra("option");
        userData = (userTable) intent.getSerializableExtra("userData");
        clubKind = intent.getIntExtra("clubKind", -9999);
        if(option.equals("modify")){
            clubInfo = (clubusersTable) intent.getSerializableExtra("clubInfo");
            club = (clubListTable) intent.getSerializableExtra("club");
        }

        /**
         * id 연결 */
        back_btn = (ConstraintLayout) findViewById(R.id.create_club_layout_cancel);
        create_btn = (ConstraintLayout) findViewById(R.id.create_club_layout_create);
        cName = (EditText) findViewById(R.id.create_club_layout_clubName);
        cContent = (EditText) findViewById(R.id.create_club_layout_clubContent);
        cLocation = (EditText) findViewById(R.id.create_club_layout_clubLocation);
        cPNumber = (EditText) findViewById(R.id.create_club_layout_clubPhoneNumber);


        /**
         * 만약 동아리 정보 수정이면 edittext에 원래 정보를 가져온다*/
        if(option.equals("modify")){
            cName.setText(club.getClubName());
            cContent.setText(club.getClubContent());
            cLocation.setText(club.getClubLocation());
            cPNumber.setText(club.getClubPhoneNumber());
        }


        /**
         * 버튼 선택*/
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        create_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String clubName = "";
                clubName = cName.getText().toString();
                String clubContent = "";
                clubContent = cContent.getText().toString();
                String clubLocation = "";
                clubLocation = cLocation.getText().toString();
                String clubPhoneNumber = "";
                clubPhoneNumber = cPNumber.getText().toString();

                if(clubName.equals("")){
                    Toast.makeText(getApplicationContext(), "동아리 이름을 입력하세요!", Toast.LENGTH_SHORT).show();
                }else if(clubContent.equals("")){
                    Toast.makeText(getApplicationContext(), "동아리 소개를 입력하세요!", Toast.LENGTH_SHORT).show();
                }else if(clubLocation.equals("")){
                    Toast.makeText(getApplicationContext(), "동아리 위치를 입력하세요!", Toast.LENGTH_SHORT).show();
                }else if(clubPhoneNumber.equals("")) {
                    Toast.makeText(getApplicationContext(), "연락가능 번호를 입력하세요!", Toast.LENGTH_SHORT).show();
                } else{
                    if(option.equals("new")){

                        /**
                         * 동아리 이름, 소개 둘다 기입했을 경우 */
                        int boardKind = -9999, infoID = -9999;
                        /**
                         * DB에 kindset에 탐색해서 boardKind, 안겹치게 가져오기  */
                        try{
                            HashMap<Integer,Boolean> Cur_KindSet = JsonToArray_boardKind(new getData().execute("http://" + ip_add + "/clubListGet.php?clubKind="+ 0,"").get());
                            while(true){
                                double randomValue = Math.random();
                                int intValue = (int)(randomValue * 1000) + 1;
                                if(Cur_KindSet.get(intValue) == null){
                                    boardKind = intValue;
                                    break;
                                }
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        /**
                         * 다음 infoID 가져오기 */
                        try{
                            infoID = JsonToArray_infoID(new getData().execute("http://" + ip_add + "/infoIDGet.php","").get());
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        /**
                         * clubList insert*/
                        new insertData().execute("http://" + ip_add + "/clublistInsert.php",
                                "boardKind=" + boardKind + "&clubCaptain=" + userData.getStudentNumber() + "&clubName=" + clubName + "&clubContent=" + clubContent + "&clubLocation=" + clubLocation + "&clubPhoneNumber=" + clubPhoneNumber + "&clubUserCnt=" + 1 +  "&clubKind=" + clubKind);
                        /**
                         * clubUsers에 insert*/
                        new insertData().execute("http://" + ip_add + "/clubusersInsert.php",
                                "infoID=" + infoID + "&boardKind=" + boardKind + "&clubName=" + clubName + "&studentNumber=" + userData.getStudentNumber()  + "&isStaff=" + 1);

                        /**
                         * 동아리 정보 저장*/
                        clubInfo.setInfoID(infoID);
                        clubInfo.setBoardKind(boardKind);
                        clubInfo.setClubName(clubName);
                        clubInfo.setStudentNumber(userData.getStudentNumber());
                        clubInfo.setIsStaff(1);

                        club.setClubKind(clubKind);
                        club.setClubName(clubName);
                        club.setClubCaptain(userData.getStudentNumber());
                        club.setClubLocation(clubLocation);
                        club.setClubPhoneNumber(clubPhoneNumber);
                        club.setClubContent(clubContent);
                        club.setClubUserCnt(1);
                        /**
                         * MainActivity 이동*/
                        Intent intent = new Intent(getApplicationContext(), clubActivity.class);
                        intent.putExtra("club",club);
                        intent.putExtra("userData",userData);
                        intent.putExtra("clubInfo",clubInfo);
                        startActivity(intent);
                        finish();
                    }else{
                        /**
                         * 새로운 정보로 업데이트 */
                        new insertData().execute("http://" + ip_add + "/clublistInfoUpdate.php",
                                "clubName=" + clubName + "&clubContent=" + clubContent + "&clubLocation=" + clubLocation + "&clubPhoneNumber=" + clubPhoneNumber + "&boardKind=" + club.getBoardKind());
                        club.setClubName(clubName);
                        club.setClubContent(clubContent);
                        club.setClubLocation(clubLocation);
                        club.setClubPhoneNumber(clubPhoneNumber);
                        onBackPressed();
                    }
                }
            }
        });
    }

    private  HashMap<Integer,Boolean> JsonToArray_boardKind(String Json){
        HashMap<Integer,Boolean> List = new HashMap<>();
        String TAG_JSON="root";
        String TAG_boardKind = "boardKind";
        try {
            JSONObject jsonObject = new JSONObject(Json);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject item = jsonArray.getJSONObject(i);
                // Josn 으로 변환된 값들 저장
                int boardKind = item.getInt(TAG_boardKind);
                // Map에 저장
                List.put(boardKind, true);
            }
        } catch (JSONException e) {
            Log.d(TAG, "showResult : ", e);
        }
        return List;
    }
    private Integer JsonToArray_infoID(String Json){
        ArrayList<Integer> List = new ArrayList<>();
        String TAG_JSON="root";
        String TAG_infoID = "infoID";
        try {
            JSONObject jsonObject = new JSONObject(Json);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject item = jsonArray.getJSONObject(i);
                // Josn 으로 변환된 값들 저장
                int infoID = item.getInt(TAG_infoID);
                // Map에 저장
                List.add(infoID);
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
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        if(option.equals("new")){
            Intent intent = new Intent(getApplicationContext(), createClubKindActivity.class);
            intent.putExtra("userData",userData);
            startActivity(intent);
            finish();
        }else{
            Intent intent = new Intent(getApplicationContext(), moreClubActivity.class);
            intent.putExtra("userData",userData);
            intent.putExtra("club",club);
            intent.putExtra("clubInfo",clubInfo);
            startActivity(intent);
            finish();

        }
    }
}
