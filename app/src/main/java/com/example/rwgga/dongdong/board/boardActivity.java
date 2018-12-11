package com.example.rwgga.dongdong.board;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.rwgga.dongdong.R;
import com.example.rwgga.dongdong.club.clubActivity;
import com.example.rwgga.dongdong.Adapters.commentTable_RecyclerAdapter;
import com.example.rwgga.dongdong.database_table.boardTable;
import com.example.rwgga.dongdong.database_table.clubusersTable;
import com.example.rwgga.dongdong.database_table.commentTable;
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
 * 작성자 : Created by (김정환)
 *
 * 마지막 수정 날짜 : 2018. 11. 10~
 *
 * 게시물을 자세히 보는 activity 이다.
 *
 * boardTable_RecyclerAdapter 에서 layout 선택시 현재 엔티비티가 실행
 *
 * boardTable_RecyclerAdapter 에서 넘긴 데이터 가져옴 ( 게시물 내용 )
 * */
public class boardActivity extends AppCompatActivity {
    private static String ip_add = "10.0.2.2";
    private static String TAG = "phptest";

    /**
     * 변수*/
    private boolean isManagement = false;
    private boolean writeComment = false;
    private int commentSize = 0;

    /**
     * 안드로이드 객체*/
    private ConstraintLayout comment_layout; // 댓글 레이아웃
    private Button commentWriteBtn; // 작성버튼
    private ImageView writeCancel; // 취소버튼
    private EditText commentContentEdit; // 댓글 EditText
    private RecyclerView board_rec; // 댓글 RecycleView
    private InputMethodManager imm; // 키보드 셋팅

    /**
     * 현재 사용자 정보 저장*/
    private userTable userData = new userTable();
    /**
     * 현재 게시물 정보 저장*/
    private boardTable boardData = new boardTable();
    /**
     * 동아리에서 불러왔다면 동아리 정보 저장 */
    private clubusersTable clubInfo = new clubusersTable();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board);

        /**
         * 값 가져오기 */
        Intent intent = getIntent();
        isManagement = intent.getBooleanExtra("isManagement", false);
        writeComment = intent.getBooleanExtra("writeComment", false);
        boardData = (boardTable) intent.getSerializableExtra("boardData");
        userData = (userTable) intent.getSerializableExtra("userData");
        clubInfo = (clubusersTable) intent.getSerializableExtra("clubInfo");

        /**
         *  id 연결 */
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        board_rec = (RecyclerView) findViewById(R.id.board_layout_rev);
        comment_layout = (ConstraintLayout) findViewById(R.id.board_layout_commenLayout);
        commentWriteBtn = (Button) findViewById(R.id.board_layout_commentWrite);
        commentContentEdit = (EditText)findViewById(R.id.board_layout_commentContent);
        writeCancel = (ImageView) findViewById(R.id.board_layout_writeCancel);

        /**
         * 툴바 뒤로 가기*/
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        /**
         * 키보드 내리는 아이콘 클릭시 */
        writeCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
            }
        });

        /**
         * 게시물 내용 설정*/
        setTitle(boardData.getBoardTitle()); // 액티비티 제목목을 게시물 제목으로
        if(boardData.getBoardKind() == -1){ // 홍보 게시판은 댓글 작성 불가
            comment_layout.setVisibility(View.GONE);
        }else{
            comment_layout.setVisibility(View.VISIBLE);
        }
        /**
         * 해당 게시물에 달린 댓글을 읽어오고 있다면 리사이클뷰로 보여준다.*/
        initData();

        /**
         * 댓글 작성 버튼 클릭시 */
        commentWriteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String commentContent = "";
                commentContent = commentContentEdit.getText().toString();
                if(commentContent.equals("")){
                    Toast.makeText(getApplicationContext(),"댓글 내용을 입력하세요.",Toast.LENGTH_SHORT).show();
                } else {
                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                    /**
                     * 다음 commentID 가져오기 */
                    int commentID = -9999;
                    try{
                        commentID = getCommentID(new getData().execute("http://" + ip_add + "/commentIDGet.php","").get());
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    /**
                     * comment DataBase 에 insert*/
                    new insertData().execute("http://" + ip_add + "/commentInsert.php", "commentID=" + commentID + "&boardID=" + boardData.getBoardID() + "&boardKind=" + boardData.getBoardKind() + "&commentContent=" + commentContent  + "&commentDate=" + getDate() + "&commentWriter=" + userData.getStudentNumber() + "&isDelete=" + 1 );

                    /**
                     * insert후 다시 boardActivity 호출 ( 새로고침 )*/
                    Intent intent = new Intent(getApplicationContext(), boardActivity.class);
                    intent.putExtra("writeComment", false);
                    intent.putExtra("boardData", boardData);
                    intent.putExtra("userData",userData);
                    intent.putExtra("clubInfo", clubInfo);
                    startActivity(intent);
                    overridePendingTransition(0, 0); // intent를 불러올때 애니메이션 삭제
                    finish();
                }
            }
        });
    }
    /**
     * 툴바 설정*/
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
     * 데이터 초기화
     */
    private void initData(){
        /**
         * 댓글 작성으로 들어왔을 경우 키보드를 올려줌 */
        if(writeComment == true && boardData.getBoardKind() != -1){
            board_rec.scrollToPosition(commentSize);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        }
        /**
         * 현재  게시물에 해당되는 댓글들을 가져와서 commentList에 추가*/
        ArrayList<commentTable> commentList = new ArrayList<>();
        String URL = ("http://" + ip_add + "/commentGet.php?boardID=" + boardData.getBoardID() + "");
        try{
            commentList = getCommentList(new getData().execute(URL, "").get());
        }catch (Exception e){
            e.printStackTrace();
        }
        commentSize = commentList.size();
        board_rec.setAdapter(new commentTable_RecyclerAdapter(clubInfo, userData, commentList, boardData, R.layout.comment_item, R.layout.board_main_item, isManagement));
        board_rec.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        board_rec.setItemAnimator(new DefaultItemAnimator());

    }
    /**
     * 현재 시간 가져오기 */
    public String getDate(){
        Date today = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        return format.format(today);
    }
    /**
     * json to List */
    private ArrayList<commentTable> getCommentList(String Json){
        ArrayList<commentTable> List = new ArrayList<>();

        String TAG_JSON="root";
        String TAG_commentID = "commentID";
        String TAG_boardID = "boardID";
        String TAG_commentContent = "commentContent";
        String TAG_commentDate = "commentDate";
        String TAG_commentWriter = "commentWriter";
        String TAG_isDelete = "isDelete";

        try {
            JSONObject jsonObject = new JSONObject(Json);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject item = jsonArray.getJSONObject(i);
                // Josn 으로 변환된 값들 저장

                int commentID = item.getInt(TAG_commentID);
                int boardID = item.getInt(TAG_boardID);
                String commentContent = item.getString(TAG_commentContent);
                String commentDate = item.getString(TAG_commentDate);
                String commentWriter = item.getString(TAG_commentWriter);
                int isDelete = item.getInt(TAG_isDelete);

                // Map에 저장
                commentTable commentTables = new commentTable();
                commentTables.setCommentID(commentID);
                commentTables.setBoardID(boardID);
                commentTables.setCommentContent(commentContent);
                commentTables.setCommentDate(commentDate);
                commentTables.setCommentWriter(commentWriter);
                commentTables.setIsDelete(isDelete);

                List.add(commentTables);
            }
        } catch (JSONException e) {
            Log.d(TAG, "showResult : ", e);
        }
        return List;
    }
    private Integer getCommentID(String Json){
        ArrayList<Integer> List = new ArrayList<>();
        String TAG_JSON="root";
        String TAG_commentID = "commentID";
        try {
            JSONObject jsonObject = new JSONObject(Json);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject item = jsonArray.getJSONObject(i);
                // Josn 으로 변환된 값들 저장
                int commentID = item.getInt(TAG_commentID);
                // Map에 저장
                List.add(commentID);
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
     * 뒤로가기 */
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        imm.hideSoftInputFromWindow(commentContentEdit.getWindowToken(),0);
        if(!isManagement){
            /**
             * 관리 모드가 아니라면 */
            if(boardData.getBoardKind() == -1){
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("userData", userData);
                intent.putExtra("tab", 1);
                startActivity(intent);
            }else{
                Intent intent = new Intent(getApplicationContext(), clubActivity.class);
                intent.putExtra("userData", userData);
                intent.putExtra("clubInfo", clubInfo);
                startActivity(intent);
            }
            finish();
        }else{
            super.onBackPressed();
        }
    }
}
