package com.example.rwgga.dongdong.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rwgga.dongdong.R;
import com.example.rwgga.dongdong.board.boardWriteActivity;
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
 * 작성자 : Created by (김정환)
 *
 * 마지막 수정 날짜 : 2018. 11. 22
 *
 * 클럽 가입 액티비티
 */
public class clubJoinActivity extends AppCompatActivity implements View.OnClickListener{
    /**
     * 변수*/
    private static String ip_add = "10.0.2.2";
    private static String TAG = "phptest";

    /**
     * 안드로이드 객체*/
    private TextView cName;
    private EditText content;
    private ConstraintLayout cancel, post;


    /**
     * 현재 사용자 정보 저장*/
    private userTable userData = new userTable();
    /**
     * 현재 동아리 정보 */
    private clubListTable club = new clubListTable();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club_join);

        /**
         * 데이터 받아오기 */
        Intent intent = getIntent();
        userData = (userTable) intent.getSerializableExtra("userData");
        club = (clubListTable) intent.getSerializableExtra("club");

        /**
         * id 연결*/
        cName = (TextView) findViewById(R.id.club_join_clubName);
        content = (EditText) findViewById(R.id.club_join_content);
        cancel = (ConstraintLayout) findViewById(R.id.club_join_cancel);
        post = (ConstraintLayout) findViewById(R.id.club_join_post);
        cName.setText(club.getClubName());

        /**
         * 버튼 이벤트*/
        cancel.setOnClickListener(this);
        post.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.club_join_cancel:
                onBackPressed();
                break;
            case R.id.club_join_post:
                String note ="";
                note = content.getText().toString();
                if(note.equals("")){
                    Toast.makeText(getApplicationContext(), "간단한 소개를 입력해주세요!!!!",Toast.LENGTH_SHORT).show();
                }else{
                    /**
                     * 다음 info ID 가져오기*/
                    int infoID = -9999;
                    try{
                        infoID = getInfoID(new getData().execute("http://" + ip_add + "/waitIDGet.php","").get());
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    /**
                     * 신청 정보 넣기*/
                    new insertData().execute("http://" + ip_add + "/waitInsert.php", "infoID=" + infoID + "&boardKind=" + club.getBoardKind()+ "&waitContent=" + note + "&studentNumber=" + userData.getStudentNumber());
                    Toast.makeText(getApplicationContext(), "신청완료",Toast.LENGTH_SHORT).show();
                    onBackPressed();
                }
                break;
        }
    }
    /**
     * json to Integer (infoID) */
    private Integer getInfoID(String Json){
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
    /**
     * 뒤로가기*/
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra("userData", userData);
        intent.putExtra("tab",2);
        startActivity(intent);
        finish();
    }
}
