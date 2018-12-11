package com.example.rwgga.dongdong.main;

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
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.rwgga.dongdong.R;
import com.example.rwgga.dongdong.database_table.clubListTable;
import com.example.rwgga.dongdong.Adapters.clubListTable_RecyclerAdapter;
import com.example.rwgga.dongdong.php_server.getData;
import com.example.rwgga.dongdong.Adapters.kind_Spinner_Adapter;
import com.example.rwgga.dongdong.database_table.userTable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
/**
 *
 * 작성자 : Created by (rwggad@gmail.com - 김정환(KimJeongHwan)
 *
 * 개설된 동아리 목록
 */
public class MainTab3Activity extends Fragment implements SwipeRefreshLayout.OnRefreshListener{
    private static String ip_add = "10.0.2.2";
    private static String TAG = "phptest";
    /**
     * 안드로이드 객체 */
    private RecyclerView rec;
    private Spinner spinner;
    private TextView empty;
    private SwipeRefreshLayout refresh;
    /**
     * 현재 사용자 정보 저장*/
    private userTable userData = new userTable();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_main_tab3, container, false);

        /**
         * intent값 받아오기*/
        userData = (userTable) getArguments().getSerializable("userData");

        /**
         * id 연결*/
        empty = (TextView) view.findViewById(R.id.tab3_layout_empty);
        rec = (RecyclerView)view.findViewById(R.id.tab3_layout_rev);
        spinner = (Spinner)view.findViewById(R.id.tab3_layout_Spinner);
        refresh = (SwipeRefreshLayout) view.findViewById(R.id.refresh);

        /**
         * 세로고침 */
        refresh.setOnRefreshListener(this);

        /**
         * 스피너와 어댑터 연결*/
        List<String> spinner_data = new ArrayList<>();
        spinner_data.add("전체");
        spinner_data.add("문예");
        spinner_data.add("봉사");
        spinner_data.add("종교");
        spinner_data.add("체육");
        spinner_data.add("학술");
        spinner_data.add("기타");
        kind_Spinner_Adapter adapter = new kind_Spinner_Adapter(view.getContext(), spinner_data);
        spinner.setAdapter(adapter);
        /**
         * 스피너 값 선택시
         * */
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                /**
                 * recycler*/
                initData(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
        return view;
    }
    @Override
    public void onRefresh() {
        Intent intent = new Intent(getContext(), MainActivity.class);
        intent.putExtra("tab", 2);
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
    private void initData(int clubKind){
        ArrayList<clubListTable> clubList = new ArrayList<>();
        try{
            clubList = getClubLists(new getData().execute("http://" + ip_add + "/clubLIstGet.php?clubKind=" + clubKind + "", "").get());
        }catch (Exception e){
            e.printStackTrace();
        }
        /**
         * 현재 분류에 해당되는 동아리가 있다면*/
        if(clubList.size() > 0){
            refresh.setVisibility(View.VISIBLE);
            empty.setText("");
        }else{
            refresh.setVisibility(View.GONE);
            empty.setText("개설된 동아리가 없습니다.");
        }
        rec.setAdapter(new clubListTable_RecyclerAdapter(userData, clubList, R.layout.club_item));
        rec.setLayoutManager(new LinearLayoutManager(getContext()));
        rec.setItemAnimator(new DefaultItemAnimator());
    }
    /**
     * json to List
     */
    private ArrayList<clubListTable> getClubLists(String Json){
        ArrayList<clubListTable> List = new ArrayList<>();

        String TAG_JSON="root";
        String TAG_boardKind = "boardKind";
        String TAG_clubCaptain = "clubCaptain";
        String TAG_clubName ="clubName";
        String TAG_clubContent = "clubContent";
        String TAG_clubLocation = "clubLocation";
        String TAG_clubPhoneNumber = "clubPhoneNumber";
        String TAG_clubUserCnt = "clubUserCnt";
        String TAG_clubKind = "clubKind";

        try {
            JSONObject jsonObject = new JSONObject(Json);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject item = jsonArray.getJSONObject(i);
                // Josn 으로 변환된 값들 저장
                int boardKind = item.getInt(TAG_boardKind);
                String clubCaptain = item.getString(TAG_clubCaptain);
                String clubName = item.getString(TAG_clubName);
                String clubContent = item.getString(TAG_clubContent);
                String clubLocation = item.getString(TAG_clubLocation);
                String clubPhoneNumber = item.getString(TAG_clubPhoneNumber);
                int clubUserCnt = item.getInt(TAG_clubUserCnt);
                int clubKind = item.getInt(TAG_clubKind);

                // Map에 저장
                clubListTable clubListTables = new clubListTable();
                clubListTables.setBoardKind(boardKind);
                clubListTables.setClubCaptain(clubCaptain);
                clubListTables.setClubName(clubName);
                clubListTables.setClubContent(clubContent);
                clubListTables.setClubLocation(clubLocation);
                clubListTables.setClubPhoneNumber(clubPhoneNumber);
                clubListTables.setClubUserCnt(clubUserCnt);
                clubListTables.setClubKind(clubKind);
                List.add(clubListTables);
            }
        } catch (JSONException e) {
            Log.d(TAG, "showResult : ", e);
        }
        return List;
    }
}


