package com.example.rwgga.dongdong.club;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.example.rwgga.dongdong.R;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 *
 * 작성자 : Created by (rwggad@gmail.com - 김정환(KimJeongHwan)
 * 마지막 수정 날짜 : 2018. 11. 24~
 *
 * 캘린더 팝업
 */
public class clubCalendarPopup extends Activity {
    /**
     * 안드로이드 객체 */
    TextView txtText;
    EditText calDate, calContent;
    int year,month,day;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); // 타이틀바 제거
        setContentView(R.layout.calendar_popup);

        /**
         * id 연결*/
        calDate = (EditText)findViewById(R.id.Insert_calDate);
        calContent = (EditText)findViewById(R.id.Insert_calContent);

        /**
         * 데이터 가져와서 data setting */
        Intent intent = getIntent();
        String date = intent.getStringExtra("date");
        calDate.setText(date); // 가져온데이터로 일자 설정

        /**
         * 날짜 계산*/
        GregorianCalendar calendar = new GregorianCalendar();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day= calendar.get(Calendar.DAY_OF_MONTH);


        /**
         * 날짜 계산 하는 부분..*/
        calDate.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                /**
                 * ?? */
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    new DatePickerDialog(clubCalendarPopup.this,android.R.style.Theme_DeviceDefault_Light_Dialog,dateSetListener, year,month,day).show();
                }
                return false;
            }
        });

        overridePendingTransition(0, 0); // intent를 불러올때 애니메이션 삭제
    }
    private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            String msg = String.format("%d-%d-%d", year,monthOfYear+1, dayOfMonth);
            EditText date = (EditText)findViewById(R.id.Insert_calDate);
            date.setText(msg);
        }
    };

    /**
     * 확인 버튼 클릭 ( insert )*/
    public void mOnClose(View v){
        /**
         * 입력된 날짜와 데이터를 넘겨준다.*/
        Intent intent = new Intent();
        /**
         * 만약 일정을 입력하지 않았다면*/
        if(calContent.getText().toString().equals("")) {
            Toast.makeText(clubCalendarPopup.this, "일정 내용을 입력하시오.", Toast.LENGTH_SHORT).show();
            return;
        }
        intent.putExtra("param_calDate", calDate.getText().toString());
        intent.putExtra("param_calContent",calContent.getText().toString());
        setResult(RESULT_OK, intent);
        finish();
    }
}
