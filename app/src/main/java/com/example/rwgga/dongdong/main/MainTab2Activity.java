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
import android.widget.TextView;

import com.example.rwgga.dongdong.R;
import com.example.rwgga.dongdong.database_table.boardTable;
import com.example.rwgga.dongdong.Adapters.boardTable_RecyclerAdapter;
import com.example.rwgga.dongdong.php_server.getData;
import com.example.rwgga.dongdong.database_table.userTable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 작성자 : Created by (rwggad@gmail.com - 김정환(KimJeongHwan)
 *
 * 홍보 게시물 목록
 */
public class MainTab2Activity extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private static String ip_add = "10.0.2.2";
    private static String TAG = "phptest";
    /**
     * 안드로이드 객체 */
    private Context context;
    private SwipeRefreshLayout refresh;
    private RecyclerView rec;
    private TextView empty;
    /**
     * 현재 사용자 정보 저장*/
    private userTable userData = new userTable();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_main_tab2, container, false);
        /**
         * intent 값 받아오기 */
        userData = (userTable) getArguments().getSerializable("userData");

        /**
         * id 연결*/
        empty = (TextView) view.findViewById(R.id.tab2_layout_empty);
        rec = (RecyclerView)view.findViewById(R.id.tab2_layout_rev);
        refresh = (SwipeRefreshLayout) view.findViewById(R.id.refresh);

        /**
         * 세로고침 */
        refresh.setOnRefreshListener(this);
        /**
         * recycler*/
        initData();

        return view;
    }
    @Override
    public void onRefresh() {
        Intent intent = new Intent(getContext(), MainActivity.class);
        intent.putExtra("tab", 1);
        intent.putExtra("userData", userData);
        startActivity(intent);
        (getActivity()).overridePendingTransition(0,0);
        refresh.setRefreshing(false); // 새로고침 완료
    }


    @Nullable
    @Override
    public Context getContext() { return super.getContext(); }
    /**
     * 데이터 초기화 */
    private void initData(){
        ArrayList<boardTable> boardList = new ArrayList<>();
        try{
            boardList = getBaordList(new getData().execute("http://" + ip_add + "/boardGet.php?boardKind=" + -1 + "", "").get());
        }catch (Exception e){
            e.printStackTrace();
        }
        if(boardList.size() > 0){
            refresh.setVisibility(View.VISIBLE);
            empty.setText("");
        }else{
            refresh.setVisibility(View.GONE);
            empty.setText("등록된 게시글이 없습니다.");
        }
        rec.setAdapter(new boardTable_RecyclerAdapter(userData, boardList, null, null,  R.layout.board_item, R.layout.club_main_item));
        rec.setLayoutManager(new LinearLayoutManager(getContext()));
        rec.setItemAnimator(new DefaultItemAnimator());
    }
    /**
     * 게시물 목록 들고와서 json->string으로 바꾸기*/
    private ArrayList<boardTable> getBaordList(String Json){
        ArrayList<boardTable> List = new ArrayList<>();

        String TAG_JSON="root";
        String TAG_boardID = "boardID";
        String TAG_boardKind = "boardKind";
        String TAG_boardWriter="boardWriter";
        String TAG_boardTitle ="boardTitle";
        String TAG_boardContent = "boardContent";
        String TAG_boardDate = "boardDate";
        String TAG_boardCount = "boardCount";
        String TAG_isDelete = "isDelete";

        try {
            JSONObject jsonObject = new JSONObject(Json);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject item = jsonArray.getJSONObject(i);
                // Josn 으로 변환된 값들 저장
                int boardID = item.getInt(TAG_boardID);
                int boardKind = item.getInt(TAG_boardKind);
                String boardTitle = item.getString(TAG_boardTitle);
                String boardWriter = item.getString(TAG_boardWriter);
                String boardContent = item.getString(TAG_boardContent);
                String boardDate = item.getString(TAG_boardDate);
                int boardCount = item.getInt(TAG_boardCount);
                int isDelete = item.getInt(TAG_isDelete);

                // Map에 저장 ( 현재 DB에 있는 학번 저장 )
                boardTable boardTables = new boardTable();
                boardTables.setBoardID(boardID);
                boardTables.setBoardKind(boardKind);
                boardTables.setBoardTitle(boardTitle);
                boardTables.setBoardContent(boardContent);
                boardTables.setBoardDate(boardDate);
                boardTables.setBoardCount(boardCount);
                boardTables.setBoardWriter(boardWriter);
                boardTables.setIsDelete(isDelete);
                List.add(boardTables);

            }
        } catch (JSONException e) {
            Log.d(TAG, "showResult : ", e);
        }
        return List;
    }

}
