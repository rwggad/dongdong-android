package com.example.rwgga.dongdong.board;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rwgga.dongdong.R;
import com.example.rwgga.dongdong.club.clubActivity;
import com.example.rwgga.dongdong.database_table.boardTable;
import com.example.rwgga.dongdong.database_table.clubusersTable;
import com.example.rwgga.dongdong.database_table.userTable;
import com.example.rwgga.dongdong.main.MainActivity;
import com.example.rwgga.dongdong.php_server.getData;
import com.example.rwgga.dongdong.php_server.insertData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
/**
 * 작성자 : Created by (rwggad@gmail.com - 김정환(KimJeongHwan)
 * 게시물 작성, 또는 수정하는 엑티비티
 *
 * intent할때 move 값으로 Detail이 나오면 boardTable_RecyclerAdapter 수정버튼을 눌러서 들어온것
 *
 * List가 나오면 게시물 목록에서 수정버튼을 눌러서 들어온것
 * */
public class boardWriteActivity extends AppCompatActivity {
    /**
     * 변수 선언*/
    private static String ip_add = "10.0.2.2"; // php 통신을 위한 안드로이드 아이피 정보
    private String option; // 게시물 작성할 것인지 수정할 것인지
    private String move = "reset"; // 게시물 작성후 이동할 엑티비티

    /**
     * 안드로이드 객체 선언*/
    private LinearLayout cancel, write;
    private TextView title;
    private EditText Content_edit, Title_edit;

    /**
     * 현재 사용자 정보 저장*/
    private userTable userData = new userTable();
    /**
     * 현재 동아리 정보 */
    private clubusersTable clubInfo = new clubusersTable();
    /**
     * 게시물 수정이라면 게시물 정보*/
    private boardTable boardData = new boardTable();

    /**
     * insert할때 필요한 객체들 */
    private int boardKind, boardID;
    private String boardTitle, boardContent, boardDate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_write);

        /**
         * id 연결*/
        cancel = (LinearLayout) findViewById(R.id.board_write_layout_cancel);
        write = (LinearLayout) findViewById(R.id.board_write_layout_write);
        title = (TextView) findViewById(R.id.board_write_layout_title2);
        Content_edit = (EditText) findViewById(R.id.board_write_layout_content_edit);
        Title_edit = (EditText) findViewById(R.id.board_write_layout_title_edit);

        /**
         * intent에서 전달되는 데이터 가져오기 */
        Intent intent = getIntent();

        option = intent.getStringExtra("option"); // 현재 하려는 모드 가져오기
        userData = (userTable) intent.getSerializableExtra("userData");
        clubInfo = (clubusersTable) intent.getSerializableExtra("clubInfo");
        boardKind = intent.getIntExtra("boardKind", -9999);
        move = intent.getStringExtra("move");

        /**
         * 만약 수정하기로 들어왔다면 제목이랑 내용을 DB에 있던 내용으로 바꾸오줌*/
        if(option.equals("modify")){
            boardData = (boardTable) intent.getSerializableExtra("boardData");
            Content_edit.setText(boardData.getBoardContent());
            Title_edit.setText(boardData.getBoardTitle());
            boardKind = boardData.getBoardKind();
        }

        /**
         * 작성하기 layout title 설정*/
        if(boardKind == -1){ // boardKind == -1 홍보 게시판
            title.setText("홍보 게시판");
        }else{ // 아닐땐 동아리 게시판 정보를 가져오고, 동아리 이름을 title로 설정
            title.setText(clubInfo.getClubName());
            boardKind = clubInfo.getBoardKind();
        }

        /**
         * 작성 취소 버튼*/
        cancel.setOnClickListener(new View.OnClickListener() { // 취소
            @Override
            public void onClick(View v) {
                modal_show("작성을 취소 하시겠습니까?", "cancel");
            }
        });
        /**
         * 작성하기 버튼*/
        write.setOnClickListener(new View.OnClickListener() { // 작성
            @Override
            public void onClick(View v) {
                boardTitle = Title_edit.getText().toString();
                boardContent = Content_edit.getText().toString();
                if(boardContent.equals("") ){
                    Toast.makeText(getApplicationContext(),"내용을 작성하세요!",Toast.LENGTH_SHORT).show();
                }
                else if(boardTitle.equals("")){
                    Toast.makeText(getApplicationContext(),"제목을 작성하세요!",Toast.LENGTH_SHORT).show();
                }
                else{
                    modal_show("작성 하시겠습니까?", option);
                }
            }
        });
    }

    /**
     * 현재 시간 가져오기 */
    public String getDate(){
        Date today = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        return format.format(today);
    }

    /**
     * 엑티비티 이동 */
    public void move_activity(){
        if(move.equals("listView")){
            Intent intent;
            if(boardKind == -1) { // 홍보 게시판일 경우 Main으로 이동
                intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("userData",userData);
                intent.putExtra("tab", 1);

            }else { // 아니면 동아리 페이지로
                intent = new Intent(getApplicationContext(), clubActivity.class);
                intent.putExtra("userData",userData);
                intent.putExtra("clubInfo", clubInfo);
            }
            startActivity(intent);
        }
        else if(move.equals("detailView")){
            /**
             * 게시판 자세히 보기에서 편집할때 편집후 자세히 보는 페이지로 다시 이동*/
            Intent intent;
            intent = new Intent(getApplicationContext(), boardActivity.class);
            intent.putExtra("userData",userData);
            intent.putExtra("boardData",boardData);
            intent.putExtra("clubInfo", clubInfo);
            startActivity(intent);
        }
        finish();
    }

    /**
     * 알림 창*/
    public void modal_show(String modal_title, final String option){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(modal_title);
        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(option.equals("write")){ // 글 작성
                    Insert();

                }else if(option.equals("modify")){ // 글 수정
                    Update();
                }
                /**
                 * 게시물 작성, 수정후 */
                move_activity();
            }
        });
        builder.setNegativeButton("아니요", null);
        builder.show();
    }

    /**
     * 글 작성*/
    public void Insert() {
        boardDate = getDate();
        /**
         * 다음 boardID 가져오기*/
        try{
            boardID = getBoardID(new getData().execute("http://" + ip_add + "/boardIDGet.php","").get());
        }catch (Exception e){
            e.printStackTrace();
        }
        /**
         * 게시물 내용을 database에 insert*/
        String insertParameter = "boardID=" + boardID + "&boardKind=" + boardKind + "&boardTitle=" + boardTitle + "&boardContent=" + boardContent + "&boardDate=" + boardDate+ "&boardCount=" + 0 + "&boardWriter=" + userData.getStudentNumber() + "&isDelete=" + 1;
        insertData insertData = new insertData();
        insertData.execute("http://" + ip_add + "/boardInsert.php", insertParameter);
    }

    /**
     * 글수정 확인 창*/
    public void Update() {
        /**
         * 수정된 내용으로 database에 update*/
        String insertParameter = "boardID=" + boardData.getBoardID() + "&boardTitle=" + boardTitle + "&boardContent=" + boardContent;
        insertData insertData = new insertData();
        insertData.execute("http://" + ip_add + "/boardUpdate.php", insertParameter);
        boardData.setBoardTitle(boardTitle);
        boardData.setBoardContent(boardContent);
    }

    /**
     * 게시물 다음 ID 가져오기*/
    private Integer getBoardID(String Json){
        ArrayList<Integer> List = new ArrayList<>();
        String TAG_JSON="root";
        String TAG_boardID = "boardID";
        try {
            JSONObject jsonObject = new JSONObject(Json);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject item = jsonArray.getJSONObject(i);
                // Josn 으로 변환된 값들 저장
                int boardID = item.getInt(TAG_boardID);

                // Map에 저장
                List.add(boardID);
            }
        } catch (JSONException e) {
            e.printStackTrace();
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
    }
}
