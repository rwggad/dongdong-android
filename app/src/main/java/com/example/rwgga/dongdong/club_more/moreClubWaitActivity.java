package com.example.rwgga.dongdong.club_more;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rwgga.dongdong.R;
import com.example.rwgga.dongdong.database_table.clubListTable;
import com.example.rwgga.dongdong.database_table.clubusersTable;
import com.example.rwgga.dongdong.database_table.userTable;
import com.example.rwgga.dongdong.database_table.waitTable;
import com.example.rwgga.dongdong.main_more.moreActivity;
import com.example.rwgga.dongdong.main_more.moreMywaitActivity;
import com.example.rwgga.dongdong.php_server.getData;
import com.example.rwgga.dongdong.php_server.insertData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
/**
 *
 * 작성자 : Created by (rwggad@gmail.com - 김정환(KimJeongHwan)
 * 동아리 가입 현황
 */
public class moreClubWaitActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    /**
     * 변수*/
    private static String ip_add = "10.0.2.2"; // php 통신을 위한 안드로이드 아이피 정보
    private static String TAG = "phptest";

    /**
     * 안드로이드 객체*/
    private RecyclerView rec;
    private TextView empty;
    private SwipeRefreshLayout refresh;

    /**
     * 사용자 정보*/
    private userTable userData = new userTable();
    /**
     * 현재 동아리 정보 */
    private clubusersTable clubInfo = new clubusersTable();
    private clubListTable club = new clubListTable();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_club_wait);
        setTitle("신청 현황");
        /**
         * 데이터 받아오기 */
        Intent intent = getIntent();
        club = (clubListTable) intent.getSerializableExtra("club");
        clubInfo = (clubusersTable) intent.getSerializableExtra("clubInfo");
        userData = (userTable) intent.getSerializableExtra("userData");

        /**
         * id 연결*/
        empty = (TextView) findViewById(R.id.layout_empty);
        rec = (RecyclerView) findViewById(R.id.layout_rev);
        refresh = (SwipeRefreshLayout) findViewById(R.id.refresh);


        /**
         * recycler*/
        initData();

        /**
         * 세로고침 */
        refresh.setOnRefreshListener(this);

        /**
         * 툴바 뒤로가기 */
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    /**
     * 데이터 초기화
     */
    private void initData(){
        ArrayList<waitTable> waitList = new ArrayList<>();
        try{
            /**
             * 자기 자신 제외하고 검색 */
            waitList = getWait(new getData().execute("http://" + ip_add + "/waitClubGet.php?boardKind=" + club.getBoardKind(), "").get());
        }catch (Exception e){
            e.printStackTrace();
        }
        /**
         * 자기가 작성한 게시물이 없다면 */
        if(waitList.size() > 0){
            refresh.setVisibility(View.VISIBLE);
            empty.setText("");
        }else{
            refresh.setVisibility(View.GONE);
            empty.setText("신청 현황이 없습니다..");
        }
        rec.setAdapter(new RecyclerAdapter(waitList, R.layout.wait_item));
        rec.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        rec.setItemAnimator(new DefaultItemAnimator());
    }

    /**
     * 현재 동아리에 가입 신청한 목록 가져오기*/
    private  ArrayList<waitTable> getWait(String Json){
        ArrayList<waitTable> List = new ArrayList<>();
        String TAG_JSON="root";
        String TAG_infoID = "infoID";
        String TAG_boardKind = "boardKind";
        String TAG_waitContent = "waitContent";
        String TAG_studentNumber = "studentNumber";
        try {
            JSONObject jsonObject = new JSONObject(Json);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject item = jsonArray.getJSONObject(i);
                // Josn 으로 변환된 값들 저장
                int infoID = item.getInt(TAG_infoID);
                int boardKind = item.getInt(TAG_boardKind);
                String waitContent = item.getString(TAG_waitContent);
                String studentNumber = item.getString(TAG_studentNumber);
                // Map에 저장
                waitTable waits = new waitTable();
                waits.setInfoId(infoID);
                waits.setBoardKind(boardKind);
                waits.setWaitContent(waitContent);
                waits.setStudentNumber(studentNumber);
                List.add(waits);
            }
        } catch (JSONException e) {
            Log.d(TAG, "showResult : ", e);
        }
        return List;
    }
    /**
     * Adapter -------------------------------------------------------------------------------------------- */
    class RecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
        private static final int TYPE_ROOT = 1, TYPE_LIST = 2;
        private  ArrayList<waitTable> waitList;
        private int layout1;

        /**
         * 생성자*/
        public RecyclerAdapter( ArrayList<waitTable> waitList, int layout1){
            this.waitList = waitList;
            this.layout1 = layout1;
        }

        /**
         * 뷰 타입 지정*/
        @Override
        public int getItemViewType(int position) {
            return TYPE_LIST;
        }

        /**
         * 뷰 홀더 생성 지정*/
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
            if(viewType == TYPE_LIST){
                View itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(layout1, viewGroup, false);
                return new ListHolder(itemView);
            }
            return null;
        }

        /**
         * 데이터 매칭*/
        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, int i) {
            if (viewHolder instanceof ListHolder) {
                final waitTable waitData = waitList.get(i);
                ((ListHolder) viewHolder).textName.setText(String.valueOf(getName(waitData.getStudentNumber()) +"(" +waitData.getStudentNumber() +")"));
                ((ListHolder) viewHolder).textContent.setText(waitData.getWaitContent());
                ((ListHolder) viewHolder).deleteBtn.setVisibility(View.GONE);
                ((ListHolder) viewHolder).viewBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        ((ListHolder) viewHolder).builder.setTitle("가입을 승인 하시겠습니까?");
                        ((ListHolder) viewHolder).builder.setPositiveButton("승인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                /**
                                 * 다음 infoID 가져오기 */
                                int infoID = -9999;
                                try{
                                    infoID = getInfoID(new getData().execute("http://" + ip_add + "/infoIDGet.php","").get());
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                                /**
                                 * clubUsers에 insert*/
                                new insertData().execute("http://" + ip_add + "/clubusersInsert.php", "infoID=" + infoID + "&boardKind=" + club.getBoardKind() + "&clubName=" + club.getClubName() + "&studentNumber=" + waitData.getStudentNumber()  + "&isStaff=" + 0);
                                /**
                                 * 해당 동아리에 멤버수 추가 */
                                new insertData().execute("http://" + ip_add + "/clublistUpdate.php", "option=" + 1 + "&boardKind=" + club.getBoardKind());
                                /**
                                 * wait Table에서 삭제*/
                                new insertData().execute("http://" + ip_add + "/waitDelete.php", "studentNumber=" + waitData.getStudentNumber() + "&boardKind=" + club.getBoardKind());
                                Toast.makeText(((ListHolder)viewHolder).context, "완료", Toast.LENGTH_SHORT).show();
                                /**
                                 * 새로고침 */
                                club.setClubUserCnt(club.getClubUserCnt()+1);
                                Intent intent = new Intent(getApplicationContext(), moreClubWaitActivity.class);
                                intent.putExtra("userData",userData);
                                intent.putExtra("clubInfo",clubInfo);
                                intent.putExtra("club",club);
                                startActivity(intent);
                                overridePendingTransition(0,0);

                            }
                        });
                        ((ListHolder) viewHolder).builder.setNegativeButton("거절", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                /**
                                 * wait Table에서 삭제*/
                                new insertData().execute("http://" + ip_add + "/waitDelete.php", "studentNumber=" + waitData.getStudentNumber() + "&boardKind=" + club.getBoardKind());
                                Toast.makeText(((ListHolder)viewHolder).context, "완료", Toast.LENGTH_SHORT).show();
                                /**
                                 * 새로고침 */
                                Intent intent = new Intent(getApplicationContext(), moreClubWaitActivity.class);
                                intent.putExtra("userData",userData);
                                intent.putExtra("clubInfo",clubInfo);
                                intent.putExtra("club",club);
                                startActivity(intent);
                                overridePendingTransition(0,0);
                            }
                        });
                        ((ListHolder) viewHolder).builder.show();
                    }
                });
            }
        }

        /**
         * 뷰에 나타낼 데이터의 갯수*/
        @Override
        public int getItemCount() {
            return waitList.size();
        }

        /**
         * Holder 생성*/
        public class ListHolder extends RecyclerView.ViewHolder{

            public AlertDialog.Builder builder;
            public TextView textName, textContent;
            public LinearLayout deleteBtn;
            public ConstraintLayout viewBtn;
            public Context context;
            /**
             * 생성자 id 지정*/
            public ListHolder(@NonNull View itemView) {
                super(itemView);
                builder = new AlertDialog.Builder(itemView.getContext());
                context = itemView.getContext();
                viewBtn = (ConstraintLayout) itemView.findViewById(R.id.wait_item_parent);
                textName = (TextView) itemView.findViewById(R.id.wait_item_Name);
                textContent = (TextView) itemView.findViewById(R.id.wait_item_content);
                deleteBtn = (LinearLayout) itemView.findViewById(R.id.wait_item_delete);
            }
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
     * 새로고침 */
    @Override
    public void onRefresh() {
        Intent intent = new Intent(getApplicationContext(), moreMywaitActivity.class);
        intent.putExtra("userData",userData);
        startActivity(intent);
        overridePendingTransition(0,0);
        refresh.setRefreshing(false); // 새로고침 완료
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
        //super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), moreClubActivity.class);
        intent.putExtra("userData",userData);
        intent.putExtra("clubInfo",clubInfo);
        intent.putExtra("club",club);
        startActivity(intent);
        finish();
    }
    /**
     * 이름 불러오기 */
    public String getName(String studentNumber){
        String studentName = "";
        try{
            String Json = new getData().execute("http://" + ip_add + "/userNameGet.php?studentNumber=" + studentNumber + "", "").get();
            String TAG_JSON = "root";
            String TAG_studentName = "studentName";
            JSONObject jsonObject = new JSONObject(Json);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);
            JSONObject item = jsonArray.getJSONObject(0);
            studentName = item.getString(TAG_studentName);

        }catch (Exception e){
            System.out.println(e.toString());
        }
        return studentName;
    }
}
