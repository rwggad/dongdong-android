package com.example.rwgga.dongdong.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;

import com.example.rwgga.dongdong.R;
import com.example.rwgga.dongdong.database_table.userTable;

/**
 *
 * 작성자 : Created by (rwggad@gmail.com - 김정환(KimJeongHwan)
 * 클럽 생성 액티비티 - 1
 */
public class createClubKindActivity extends AppCompatActivity implements View.OnClickListener {
    /**
     * 안드로이드 객체*/
    private ConstraintLayout cancel_btn;
    private LinearLayout btn1, btn2, btn3, btn4, btn5, btn6;

    /**
     * 현재 사용자 정보 저장*/
    private userTable userData = new userTable();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_club_kind);

        /**
         * MainActivity 넘어온 현재 사용자 학번을 저장한다. */
        Intent intent = getIntent();
        userData = (userTable) intent.getSerializableExtra("userData");

        /**
         * id 연결 */
        btn1 = (LinearLayout) findViewById( R.id.create_club_kind_layout_btn1);
        btn2 = (LinearLayout) findViewById( R.id.create_club_kind_layout_btn2);
        btn3 = (LinearLayout) findViewById( R.id.create_club_kind_layout_btn3);
        btn4 = (LinearLayout) findViewById( R.id.create_club_kind_layout_btn4);
        btn5 = (LinearLayout) findViewById( R.id.create_club_kind_layout_btn5);
        btn6 = (LinearLayout) findViewById( R.id.create_club_kind_layout_btn6);
        cancel_btn = (ConstraintLayout) findViewById(R.id.create_club_kind_layout_cancel);

        /**
         * 분류 버튼 선택*/
        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
        btn3.setOnClickListener(this);
        btn4.setOnClickListener(this);
        btn5.setOnClickListener(this);
        btn6.setOnClickListener(this);
        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    /**
     * 버튼 onclick*/
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.create_club_kind_layout_btn1:
                next_intent(1);
                break;
            case R.id.create_club_kind_layout_btn2:
                next_intent(2);
                break;
            case R.id.create_club_kind_layout_btn3:
                next_intent(3);
                break;
            case R.id.create_club_kind_layout_btn4:
                next_intent(4);
                break;
            case R.id.create_club_kind_layout_btn5:
                next_intent(5);
                break;
            case R.id.create_club_kind_layout_btn6:
                next_intent(6);
                break;
        }
    }
    /**
     * 분류 선택 하고 나음 intent*/
    public void next_intent(int kind_value){
        Intent intent = new Intent(getApplicationContext(), createClubActivity.class);
        intent.putExtra("userData",userData);
        intent.putExtra("clubKind", kind_value);
        intent.putExtra("option","new");
        startActivity(intent);
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
