package com.example.rwgga.dongdong.main_more;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.rwgga.dongdong.main.LoginActivity;
import com.example.rwgga.dongdong.R;
import com.example.rwgga.dongdong.database_table.clubusersTable;
import com.example.rwgga.dongdong.php_server.insertData;
import com.example.rwgga.dongdong.database_table.userTable;

import java.util.ArrayList;


/**
 *
 * 작성자 : Created by (rwggad@gmail.com - 김정환(KimJeongHwan)
 *
 * 더보기 페이지 - 탈퇴
 */
public class moreOutActivity extends AppCompatActivity {
    private static String ip_add = "10.0.2.2"; // php 통신을 위한 안드로이드 아이피 정보
    private static String TAG = "phptest";

    /**
     * 안드로이드 객체 */
    private AlertDialog.Builder builder;
    private CheckBox cBox;
    private LinearLayout submit;
    private Button btn;

    /**
     * 사용자 정보*/
    private userTable userData = new userTable();
    /**
     * 현재 사용자가 운영 (가입된) 동아리 목록*/
    private ArrayList<clubusersTable> userClubs = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_out);

        /**
         * 데이터 받아오기 */
        Intent intent = getIntent();
        userData = (userTable)intent.getSerializableExtra("userData");
        userClubs = (ArrayList<clubusersTable>) intent.getSerializableExtra("userClubs");

        /**
         * id 연결*/
        cBox = (CheckBox) findViewById(R.id.out_dialog_ck);
        submit = (LinearLayout) findViewById(R.id.out_dialog_submit);
        btn = (Button)findViewById(R.id.out_dialog_btn) ;
        builder = new AlertDialog.Builder(this);

        /**
         * 툴바 뒤로가기 */
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        /**
         * 체크박스 선택 체크*/
        cBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cBox.isChecked() == true){
                    // 버튼 활성화
                    submit.setClickable(true);
                    btn.setClickable(true);
                    btn.setBackgroundColor(Color.parseColor("#107dac"));
                    btn.setTextColor(Color.parseColor("#ffffff"));
                    btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            builder.setTitle("탈퇴 합니다.");
                            builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String insertParameter;
                                    /**
                                     * 탈퇴 하기
                                     *
                                     * 현재 탈퇴하려는 사용자의 동아리 List중에 현재 사용자가 운영중인 동아리 (isStaff == 1)인 동아리가 하나라도 있다면 탈퇴를 할 수 없다.
                                     *
                                     * 현재 탈퇴하려는 사용자가 자신의 운영중인 동아리를 모두 폐쇠한 상태라면 서비스 탈퇴를 진행한다.
                                     *
                                     * 1. userTable 에서 삭제
                                     * 2. clubUsers에서 현재 탛퇴하려는 사용자의 정보를 모두 삭제 (이때 사용자가 가입된 동아리 목록 boardKInd) 값을 저장
                                     * 3. clubList 에서 2에서 저장된 boardKind에 있는 동아리에서 clubUserCnt 값을 -1 해줌
                                     * 4. 탈퇴 완료
                                     * */

                                    /**
                                     * myclubs에서 isStaff가 1 인것이 있는지 ? */
                                    boolean isPossible = true; // 탈퇴 가능?
                                    for(int i =0 ;i < userClubs.size(); i++){
                                        if(userClubs.get(i).getIsStaff() == 1){
                                            /**
                                             * 만약 사용자가 운영중인 동아리가 있다면.. 탈퇴 불가..!*/
                                            isPossible = false;
                                        }
                                    }
                                    /**
                                     * */
                                    if(isPossible == false){
                                        /**
                                         * 사용자가 운영중인 동아리가 있기 때문에 탈퇴를 할 수 없다. */
                                        Toast.makeText(getApplicationContext(), "현재 운영중인 동아리가 있습니다..! 탈퇴를 하실 수 없습니다.", Toast.LENGTH_SHORT).show();
                                    }else{
                                        /**
                                         * 탈퇴 진행 1
                                         * user Tables 삭제 */
                                        insertParameter = "studentNumber=" + userData.getStudentNumber();
                                        new insertData().execute("http://" + ip_add + "/userDelete.php", insertParameter);
                                        /**
                                         * 탈퇴 진행 2
                                         * clubusers Tables 에서 사용자 정보 삭제 */
                                        insertParameter = "studentNumber=" + userData.getStudentNumber();
                                        new insertData().execute("http://" + ip_add + "/clubusersDelete.php", insertParameter);
                                        /**
                                         * 탈퇴 진행 3
                                         * 탈퇴를 진행하는 사용자가 가입한 동아리의 멤버수를 -1 해줌*/
                                        for(int i = 0;i < userClubs.size(); i++){
                                            int boardKind = userClubs.get(i).getBoardKind();
                                            new insertData().execute("http://" + ip_add + "/clublistUpdate.php", "option=" + 0 + "&boardKind=" + boardKind);
                                        }
                                        /**
                                         * 탈퇴 진행 4
                                         * 탈퇴 완료.. login activity로 이동*/
                                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                        startActivity(intent);
                                        finish();
                                        Toast.makeText(getApplicationContext(),"탈퇴 하셨습니다.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                            builder.setNegativeButton("아니요", null);
                            builder.show();
                        }
                    });
                }else if(cBox.isChecked() == false){
                    // 버튼 비활성화
                    submit.setClickable(false);
                    btn.setClickable(false);
                    btn.setBackgroundColor(Color.parseColor("#d2d0d0"));
                    btn.setTextColor(Color.parseColor("#000000"));
                }
            }
        });
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
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), moreActivity.class);
        intent.putExtra("userData", userData);
        intent.putExtra("userClubs",userClubs);
        startActivity(intent);
        finish();
    }
}
