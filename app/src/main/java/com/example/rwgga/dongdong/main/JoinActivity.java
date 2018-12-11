package com.example.rwgga.dongdong.main;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rwgga.dongdong.R;
import com.example.rwgga.dongdong.database_table.userTable;
import com.example.rwgga.dongdong.php_server.insertData;

/**
 *
 * 작성자 : Created by (김정환)
 *
 * 마지막 수정 날짜 : 2018. 11. 03
 *
 *
 * 사용자 등록 엑티비티
 */

public class JoinActivity extends AppCompatActivity {
    private static String ip_add = "10.0.2.2";
    private static String TAG = "phptest";
    private ImageView s_image;
    private RadioGroup radioGroup;
    private RadioButton radioButton1, radioButton2;
    private TextView s_num_tv, s_name_tv;
    private Button reg_btn;

    /**
     * 현재 사용자 정보 저장*/
    private userTable userData = new userTable();

    /**
     * 최초 접속하는 회원이라면 등록여부를 물어봐주는 activity
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        /**
         * id 연결, 객체 설정*/
        radioGroup = (RadioGroup) findViewById(R.id.reg_layout_rg);
        radioButton1 = (RadioButton) findViewById(R.id.reg_layout_rbtn1);
        radioButton2 = (RadioButton) findViewById(R.id.reg_layout_rbtn2);
        s_image = (ImageView) findViewById(R.id.reg_layout_student_image);
        s_num_tv = (TextView) findViewById(R.id.reg_layout_student_number_view);
        s_name_tv = (TextView) findViewById(R.id.reg_layout_student_name_view);
        reg_btn = (Button) findViewById(R.id.reg_layout_registr_btn);

        /**
         * login activity에서 넘어온 값 */
        Intent intent = getIntent();
        userData = (userTable) intent.getSerializableExtra("userData");

        /**
         * text view 설정*/
        s_num_tv.setText(userData.getStudentNumber());
        s_name_tv.setText(userData.getStudentName());

        /**
         * 등록하기 버튼 눌렀을때*/
        reg_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (radioButton1.isChecked() == false && radioButton2.isChecked() == false) {
                    Toast.makeText(getApplicationContext(), "분류를 선택하세요.", Toast.LENGTH_SHORT).show();
                } else {
                    String isCaptain = "-1";
                    if (radioButton1.isChecked() == true) {
                        isCaptain = "0";
                    } else {
                        isCaptain = "1";
                    }
                    show(isCaptain);
                }
            }
        });
    }

    public void show(final String isCaptain) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("회원 등록");
        builder.setTitle("등록 하시겠습니까?");
        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                /**
                 * 회원정보 database에 insert*/
                userData.setIsCaptain(Integer.parseInt(isCaptain));
                String insertParameter = "studentNumber=" + userData.getStudentNumber() + "&studentPassword=" + userData.getStudentPassword() + "&studentName=" + userData.getStudentName() + "&isCaptain=" + isCaptain;
                insertData insertData = new insertData();
                insertData.execute("http://" + ip_add + "/userInsert.php", insertParameter);
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("userData",userData);
                startActivity(intent);
                finish();
            }
        });
        builder.setNegativeButton("아니요", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
        builder.show();
    }
}

