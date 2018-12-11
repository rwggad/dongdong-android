package com.example.rwgga.dongdong.main;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.rwgga.dongdong.R;
import com.example.rwgga.dongdong.database_table.userTable;
import com.example.rwgga.dongdong.main.JoinActivity;
import com.example.rwgga.dongdong.main.MainActivity;
import com.example.rwgga.dongdong.php_server.getData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * 작성자 : Created by (rwggad@gmail.com - 김정환(KimJeongHwan)
 *
 * 로그인 액티비티
 */
public class LoginActivity extends AppCompatActivity {
    private static String ip_add = "10.0.2.2";
    private static String TAG = "phptest";
    private ProgressBar l_pro;
    private EditText s_num;
    private EditText s_pass;
    private LinearLayout e_btn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        /**
         * id 연결부 */
        e_btn = (LinearLayout) findViewById(R.id.layout_login_Enter_Btn);
        s_num = (EditText) findViewById(R.id.login_layout_student_number_edit);
        s_pass = (EditText) findViewById(R.id.login_layout_student_password_edit);
        l_pro = (ProgressBar) findViewById(R.id.login_layout_progress);

        /**
         * edit Text Enter Key set (s_num -> s_pass)*/
        s_num.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    s_pass.requestFocus();
                    return true;
                }
                return false;
            }
        });
        /**
         * edit Text Enter Key set (s_pass -> e_btn) */
        s_pass.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(s_pass.getWindowToken(), 0);
                    e_btn.performClick();
                    return true;
                }
                return false;
            }
        });

        l_pro.setVisibility(View.GONE);
        /**
         * 로기은 버튼을 클릭시 */
        e_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                s_num.getHint().toString();
                String Student_number = s_num.getText().toString();
                String Password = s_pass.getText().toString();
                if (Student_number.length() == 0 || Password.length() == 0) { // 학번이나 , 비밀번호를 입력하지 않았을 경우
                    String Warning_Message = null;
                    if (Student_number.length() == 0)
                        Warning_Message = (s_num.getHint().toString() + "를 입력하세요.");
                    else
                        Warning_Message = (s_pass.getHint().toString() + "를 입력하세요.");
                    Toast.makeText(getApplicationContext(), Warning_Message, Toast.LENGTH_SHORT).show();
                } else {
                    /**
                     * 아이디 비번을 모두 입력했다면
                     *
                     * 사용자가 입력한 아이디 비번을 가져와서 jsoup을 이용해서 동아대학교학교 학생정보 시스템에 로그인을 시도한다.
                     *
                     * 로그인 시도후 jsoup으로 정상적으로 로그인 성공된 쿠키값이 넘어온다면
                     *
                     * php 서버를 이용해서 해당 사용자가 dongdong DataBase에 등록된 회원인지 확인하고 맞다면 바로 mainActivity로
                     *
                     * 아니면 사용자를 등록해도 되냐는 여부를 묻는 joinActivity로 이동한다.
                     * */
                    Connect_Web DONGA = new Connect_Web();
                    DONGA.execute(Student_number, Password);
                }
            }
        });
    }

    /**
     * AsyncTask<Integer, Integer, Boolean>은 아래와 같은 형식이다
     * AsyncTask<전달받은값의종류, Update값의종류, 결과값의종류>
     * AsyncTask<doInBackground()의 변수 종류, onProgressUpdate()에서 사용할 변수 종류, onPostExecute()에서 사용할 변수종류>
     * ex) AsyncTask<Void, Integer, Void>
     */

    // 학생정보 가져오기
    class Connect_Web extends AsyncTask<String, Void, Boolean> {
        private boolean LOGIN_FLAG = false;
        private Map<String, String> LOGIN_COOKIES;
        private Map<String, String> STUDENT_INFO = new HashMap<>();
        private String User_Agent = "Mozilla/5.0 (iPhone; CPU iPhone OS 11_0 like Mac OS X) AppleWebKit/604.1.38 (KHTML, like Gecko) Version/11.0 Mobile/15A372 Safari/604.1";
        private String LOGIN_URL = "https://student.donga.ac.kr/Login.aspx?ReturnUrl=%2f";
        private String INFO_URL = "https://student.donga.ac.kr/Univ/SUD/SSUD0000.aspx?m=1";

        /**
         * execute() 메소드가 호출되면 doInBackground()가 돌기 전 이 메소드가 호출됨.
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            l_pro.setVisibility(View.VISIBLE); // 프로그레스바 시작
        }

        /**
         * 백그라운드 작업
         * Pre 함수가 실행후에는 이 행수가 실행된다 ( 여기서 네트워크 통신을 시작 )
         */
        @Override
        protected Boolean doInBackground(String... strings) {
            try {
                // - GET _ REQUEST
                Connection.Response REQUEST = Jsoup.connect(LOGIN_URL)
                        .userAgent(User_Agent)
                        .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8")
                        .header("Accept-Encoding", "gzip, deflate, br")
                        .header("Accept-Language", "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7")
                        .method(Connection.Method.GET)
                        .execute();
                // COOKIES ( 로그인 전 쿠키 )
                Map<String, String> COOKIES = REQUEST.cookies();
                // GET INPUT VALUE ( 로그인 하기 위한 input _ data 생성 )
                Document REQUEST_DOC = REQUEST.parse();
                //Elements REQUEST_ELES = REQUEST_DOC.getElementsByTag("input"); // check input tag all value
                Map<String, String> INPUT_DATA = new HashMap<String, String>();
                INPUT_DATA.put("__EVENTTARGET", REQUEST_DOC.select("input[name=__EVENTTARGET]").attr("value"));
                INPUT_DATA.put("__EVENTARGUMENT", REQUEST_DOC.select("input[name=__EVENTARGUMENT]").attr("value"));
                INPUT_DATA.put("__VIEWSTATE", REQUEST_DOC.select("input[name=__VIEWSTATE]").attr("value"));
                INPUT_DATA.put("__VIEWSTATEGENERATOR", REQUEST_DOC.select("input[name=__VIEWSTATEGENERATOR]").attr("value"));
                INPUT_DATA.put("__EVENTVALIDATION", REQUEST_DOC.select("input[name=__EVENTVALIDATION]").attr("value"));
                INPUT_DATA.put("txtStudentCd", strings[0]);
                INPUT_DATA.put("txtPasswd", strings[1]);
                INPUT_DATA.put("ibtnLogin.x", "0");
                INPUT_DATA.put("ibtnLogin.y", "0");
                // - POST _ RESPONSE ( input_data 와 로그인전 쿠키를 이용해서 사용자가 인증된 쿠키를 받기 위해 post )
                Connection.Response RESPONSE = Jsoup.connect(LOGIN_URL)
                        .userAgent(User_Agent)
                        .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8")
                        .header("Accept-Encoding", "gzip, deflate, br")
                        .header("Accept-Language", "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7")
                        .header("Origin", "https://student.donga.ac.kr")
                        .header("Referer", "https://student.donga.ac.kr/Login.aspx")
                        .cookies(COOKIES)
                        .data(INPUT_DATA)
                        .method(Connection.Method.POST).execute();
                // LOGIN - COOKIES ( 로그인이 정상적으로 이뤄져서 사용자가 인증된 쿠키를 정상적으로 받아온다면 )
                LOGIN_COOKIES = RESPONSE.cookies();  // - LOGIN COOKIES를 이용해서 홈페이지 학생 정보 페이지에 접근 할 수 있다
                if (LOGIN_COOKIES.size() == 0) { // 로그인 쿠키값이 존재 하지 않는다면 로그인에 실패 한 것.
                    LOGIN_FLAG = false; // 로그인 실패
                } else { // 로그인 쿠키값이 있다면 해당 쿠키를 이용해서 다시 홈페이지에 get해서 학생 정보를 가져온다 ( 이름 학년 학번 학과 등 )
                    /**
                     * 학생 정보 파싱
                     * */
                    LOGIN_FLAG = true; // 로그인 성공
                    try {
                        Document student_info = Jsoup.connect(INFO_URL)
                                .userAgent(User_Agent)
                                .header("Referer", "https://student.donga.ac.kr/Main.aspx")
                                .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8")
                                .header("Accept-Encoding", "Accept-Encoding")
                                .header("Accept-Language", "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7")
                                .cookies(LOGIN_COOKIES)
                                .get();
                        // 학생 정보를 elements에 저장
                        //Elements infos = student_info.select("table.Table4 tbody tr");
                        Iterator<Element> info_str;
                        info_str = student_info.select("#lblStudentCd").iterator();
                        STUDENT_INFO.put("학번", info_str.next().text());
                        info_str = student_info.select("#lblKorNm").iterator();
                        STUDENT_INFO.put("이름", info_str.next().text());
                        info_str = student_info.select("#lblCollegeNm").iterator();
                        STUDENT_INFO.put("대학", info_str.next().text());
                        info_str = student_info.select("#lblDeptNm").iterator();
                        STUDENT_INFO.put("학과", info_str.next().text());
                        info_str = student_info.select("#lblStudentYear").iterator();
                        STUDENT_INFO.put("학년", info_str.next().text());
                    } catch (IOException e) {
                        System.out.println(e.toString());
                    }
                }
            } catch (IOException e) {
                System.out.println(e.toString());
                LOGIN_FLAG = false;
            }
            return LOGIN_FLAG;
        }

        /**
         * 백그라운드 작업이 끝난 후 호출되는 메소드
         */
        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            l_pro.setVisibility(View.GONE); // 프로그레스바 끝
            if (aBoolean == true) {
                /**
                 * DB 에서 현재 접속한 회원의 정보가 있는지 확인하고 받아오기
                 * */
                String studentNumber = STUDENT_INFO.get("학번"); // 현재 접속한 회원 학번
                userTable userData = new userTable();
                try{
                    userData = getUserData(new getData().execute("http://" + ip_add + "/userGet.php?studentNumber=" + studentNumber, "").get()); // JSON 결과 저장
                }catch (Exception e){
                    e.printStackTrace();
                }
                /**
                 * 회원정보가 DB내에 등록 되어 있지 않다면 registraion activity로으로
                 * 아니면 로비로 이동
                 * */
                if (userData != null) {
                    /**
                     * 현재 회원이 등록되어 있다면 studentNumber와 isCaptain 정보와 함께 MainActivity로 이동*/
                    Toast.makeText(getApplicationContext(), STUDENT_INFO.get("이름") + "님 환영합니다!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.putExtra("userData", userData);
                    startActivity(intent);
                    finish();
                } else {
                    /**
                     * 최초회원이라면 입력된 정보와 함께 joinActivity로 이동*/
                    Intent intent = new Intent(getApplicationContext(), JoinActivity.class);
                    userData = new userTable();
                    userData.setStudentNumber(STUDENT_INFO.get("학번"));
                    userData.setStudentName(STUDENT_INFO.get("이름"));
                    userData.setStudentPassword(s_pass.getText().toString());
                    intent.putExtra("userData", userData);
                    startActivity(intent);
                }
            } else {
                Toast.makeText(getApplicationContext(), "학번 또는 비밀번호를 확인하세요.", Toast.LENGTH_SHORT).show();
            }
        }
    }
    /**
     * Json -> String
     * user Data 를 저장한다. (비밀번호는 가져오지 않음)*/
    private userTable getUserData(String Json){
        userTable userData = new userTable();
        String TAG_JSON="root";
        String TAG_studentNumber = "studentNumber";
        String TAG_studentName ="studentName";
        String TAG_isCaptain = "isCaptain";
        try {
            JSONObject jsonObject = new JSONObject(Json);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject item = jsonArray.getJSONObject(i);
                // Josn 으로 변환된 값들 저장
                String studentNumber = item.getString(TAG_studentNumber);
                String studentName = item.getString(TAG_studentName);
                String isCaptain = item.getString(TAG_isCaptain);
                // Map에 저장 ( 현재 DB에 있는 학번 저장 )
                userData.setStudentNumber(studentNumber);
                userData.setStudentName(studentName);
                userData.setIsCaptain(Integer.parseInt(isCaptain));
            }
        } catch (JSONException e) {
            Log.d(TAG, "showResult : ", e);
            return null;
        }
        return userData;
    }
}


