package com.example.rwgga.dongdong.club_more;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.example.rwgga.dongdong.php_server.getData;
import com.example.rwgga.dongdong.php_server.insertData;
import com.example.rwgga.dongdong.database_table.userTable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
/**
 *
 * 작성자 : Created by (손민성)
 *
 * 마지막 수정 날짜 : 2018. 11. 25~
 *
 * 동아리 가입한 유저
 */
public class moreClubUsersActivity extends AppCompatActivity {
    private static String ip_add = "10.0.2.2";
    private static String TAG = "phptest";
    /**
     * 안드로이드 객체*/
    private RecyclerView rec;
    private TextView empty, userName;

    /**
     * 현재 사용자 정보 저장*/
    private userTable userData = new userTable();
    /**
     * 현재 동아리 정보 */
    private clubusersTable clubInfo = new clubusersTable();
    private clubListTable club = new clubListTable();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_club_users);
        setTitle("동아리 멤버 보기");

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
        userName = (TextView) findViewById(R.id.clubuser_item_studentName);

        userName.setText(userData.getStudentName() + "(" + userData.getStudentNumber() + ")");
        /**
         * recycler*/
        initData();

        /**
         * 툴바 뒤로가기 */
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    /**
     * 데이터 초기화
     */
    private void initData(){
        ArrayList<clubusersTable> userList = new ArrayList<>();
        try{
            /**
             * 자기 자신 제외하고 검색 */
            userList = getClubusers(new getData().execute("http://" + ip_add + "/clubusersUserGet.php?boardKind=" + club.getBoardKind() + "&studentNumber=" + userData.getStudentNumber()+ "", "").get());
        }catch (Exception e){
            e.printStackTrace();
        }
        /**
         * 자기가 작성한 게시물이 없다면 */
        if(userList.size() > 0){
            empty.setText("");
        }else{
            empty.setText("회원이 없습니다..");
        }
        rec.setAdapter(new RecyclerAdapter(userList, R.layout.clubuser_item));
        rec.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        rec.setItemAnimator(new DefaultItemAnimator());
    }
    /**
     * 클럽 사용자 가져오기 json->string*/
    private ArrayList<clubusersTable> getClubusers(String Json){
        ArrayList<clubusersTable> List = new ArrayList<>();

        String TAG_JSON="root";
        String TAG_infoID = "infoID";
        String TAG_boardKind = "boardKind";
        String TAG_clubName ="clubName";
        String TAG_studentNumber = "studentNumber";
        String TAG_isStaff = "isStaff";

        try {
            JSONObject jsonObject = new JSONObject(Json);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject item = jsonArray.getJSONObject(i);
                // Josn 으로 변환된 값들 저장
                int infoID = item.getInt(TAG_infoID);
                int boardKind = item.getInt(TAG_boardKind);
                String clubName = item.getString(TAG_clubName);
                String studentNumber = item.getString(TAG_studentNumber);
                int isStaff = item.getInt(TAG_isStaff);


                // Map에 저장
                clubusersTable clubusersTables = new clubusersTable();
                clubusersTables.setInfoID(infoID);
                clubusersTables.setBoardKind(boardKind);
                clubusersTables.setClubName(clubName);
                clubusersTables.setStudentNumber(studentNumber);
                clubusersTables.setIsStaff(isStaff);
                List.add(clubusersTables);
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
        private ArrayList<clubusersTable> userList;
        private int layout1;

        /**
         * 생성자*/
        public RecyclerAdapter(ArrayList<clubusersTable> userList, int layout1){
            this.userList = userList;
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
                final clubusersTable userData =  userList.get(i);
                ((ListHolder)viewHolder).textName.setText(getName(userData.getStudentNumber()) +"(" +userData.getStudentNumber() +")");
                /**
                 * 삭제 버튼 눌렀을 때*/
                // 만약 isStaff가 아니면 삭제 불가
                if(clubInfo.getIsStaff() == 0){
                    ((ListHolder)viewHolder).delBtn.setVisibility(View.GONE);
                }
                ((ListHolder)viewHolder).delBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        ((ListHolder) viewHolder).builder.setTitle("정말로 추발 시키겠습니까?");
                        ((ListHolder) viewHolder).builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                /**
                                * clubUsers에서 해당 회원 삭제 */
                                new insertData().execute("http://" + ip_add + "/clubusersDelete.php", "studentNumber=" + userData.getStudentNumber() + "&boardKind=" + club.getBoardKind());
                                /**
                                 * 현재 동아리에서 회원수 감소*/
                                new insertData().execute("http://" + ip_add + "/clublistUpdate.php", "option=" + 0 + "&boardKind=" + club.getBoardKind());
                                Toast.makeText(((ListHolder)viewHolder).context, "추방 시켰습니다", Toast.LENGTH_SHORT).show();
                                /**
                                 * 새로고침*/
                                re();
                            }
                        });
                        ((ListHolder) viewHolder).builder.setNegativeButton("아니요", null);
                        ((ListHolder) viewHolder).builder.show();
                    }
                });

            }
        }

        /**
         * 뷰에 나타낼 데이터의 갯수*/
        @Override
        public int getItemCount() {
            return userList.size();
        }

        /**
         * Holder 생성*/
        public class ListHolder extends RecyclerView.ViewHolder{
            public AlertDialog.Builder builder;
            public Context context;
            public TextView textName;
            public LinearLayout delBtn;
            /**
             * 생성자 id 지정*/
            public ListHolder(@NonNull View itemView) {
                super(itemView);
                context = itemView.getContext();
                builder =  new AlertDialog.Builder(itemView.getContext());
                textName = itemView.findViewById(R.id.clubuser_item_studentName);
                delBtn = itemView.findViewById(R.id.clubuser_delete);
            }
        }
    }
    /**
     * 새로고침*/
    public void re(){
        club.setClubUserCnt(club.getClubUserCnt()-1);
        Intent intent_more = new Intent(getApplicationContext(), moreClubUsersActivity.class);
        intent_more.putExtra("club",club);
        intent_more.putExtra("clubInfo",clubInfo);
        intent_more.putExtra("userData",userData);
        startActivity(intent_more);
        finish();
        overridePendingTransition(0,0);
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
        intent.putExtra("club",club);
        intent.putExtra("clubInfo",clubInfo);
        intent.putExtra("userData",userData);
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
