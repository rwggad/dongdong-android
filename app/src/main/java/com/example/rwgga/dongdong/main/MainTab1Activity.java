package com.example.rwgga.dongdong.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rwgga.dongdong.R;
import com.example.rwgga.dongdong.database_table.clubusersTable;
import com.example.rwgga.dongdong.Adapters.clubusersTable_RecyclerAdapter;
import com.example.rwgga.dongdong.php_server.getData;
import com.example.rwgga.dongdong.database_table.userTable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
/**
 *
 * 작성자 : Created by (김정환)
 *
 * 마지막 수정 날짜 : 2018. 11. 25
 *
 *
 * 탭뷰어에서 내동아리들의 목록이랑 동아리 추천 리스트를 부여주는 탭이다
 */
public class MainTab1Activity extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private static String ip_add = "10.0.2.2";
    private static String TAG = "phptest";
    /**
     * 안드로이드 객체*/
    private SwipeRefreshLayout refresh;
    private LinearLayout cBtn;
    private RecyclerView rec;
    private TextView empty;
    /**
     * 현재 사용자 정보 저장*/
    private userTable userData = new userTable();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.activity_main_tab1, container, false);
        /**
         * intent 값 받아오기 */
        userData = (userTable) getArguments().getSerializable("userData");
        /**
         * id 연결*/
        empty = (TextView) view.findViewById(R.id.tab1_layout_empty);
        rec = (RecyclerView)view.findViewById(R.id.tab1_layout_rev);
        cBtn = (LinearLayout)view.findViewById(R.id.tab1_layout_createClub);
        refresh = (SwipeRefreshLayout) view.findViewById(R.id.refresh);

        /**
         * recycler*/
        initData();

        /**
         * 새로고침*/
        refresh.setOnRefreshListener(this);

        /**
         * 동아리 만들기 탭 버튼 액션*/
        cBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(userData.getIsCaptain() == 0){
                    /**
                     * 일반 학부생일 경우 */
                    Toast.makeText(view.getContext(), "동아리장만 사용할 수 있습니다!", Toast.LENGTH_SHORT).show();
                }else{
                    /**
                     * 동아리 개설 페이지 */
                    Intent intent = new Intent(view.getContext(), createClubKindActivity.class);
                    intent.putExtra("userData",userData);
                    startActivity(intent);
                    ((Activity)view.getContext()).finish();
                }
            }
        });
        return view;
    }
    @Override
    public void onRefresh() {
        Intent intent = new Intent(getContext(), MainActivity.class);
        intent.putExtra("tab", 0);
        intent.putExtra("userData", userData);
        startActivity(intent);
        (getActivity()).overridePendingTransition(0,0);
        refresh.setRefreshing(false); // 새로고침 완료
    }

    @Nullable
    @Override
    public Context getContext() {
        return super.getContext();
    }
    /**
     * 데이터 초기화
     */
    private void initData(){
        ArrayList<clubusersTable> myClubList = new ArrayList<>();
        /**
         * 현재 접속한 회원이 가입한 동아리 리스트 들고오기 */
        try{
            myClubList = getMyclubList(new getData().execute("http://" + ip_add + "/clubusersGet.php?studentNumber='" + userData.getStudentNumber() + "'", "").get());
        }catch (Exception e){
            e.printStackTrace();
        }
        /**
         * 만약 현재 가입한 동아리가 있다면*/

        if(myClubList.size() > 0){
            refresh.setVisibility(View.VISIBLE);
            empty.setText("");
        }else{
            refresh.setVisibility(View.GONE);
            empty.setText("가입한 동아리가 없습니다..");
        }
        rec.setAdapter(new clubusersTable_RecyclerAdapter(userData, myClubList,  R.layout.myclub_item));
        rec.setLayoutManager(new LinearLayoutManager(getContext()));
        rec.setItemAnimator(new DefaultItemAnimator());
    }
    /**
     * json to List
     */
    private ArrayList<clubusersTable> getMyclubList(String Json){
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
}