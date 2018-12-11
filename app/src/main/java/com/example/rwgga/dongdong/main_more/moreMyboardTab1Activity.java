package com.example.rwgga.dongdong.main_more;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.rwgga.dongdong.R;
import com.example.rwgga.dongdong.board.boardActivity;
import com.example.rwgga.dongdong.database_table.boardTable;
import com.example.rwgga.dongdong.database_table.clubusersTable;
import com.example.rwgga.dongdong.main.MainActivity;
import com.example.rwgga.dongdong.php_server.getData;
import com.example.rwgga.dongdong.database_table.userTable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 *
 * 작성자 : Created by (rwggad@gmail.com - 김정환(KimJeongHwan)
 *
 * 더보기 페이지 내가 쓴 글 - 게시물 목록
 */
public class moreMyboardTab1Activity extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private static String ip_add = "10.0.2.2";
    private static String TAG = "phptest";
    /**
     * 안드로이드 객체*/
    private SwipeRefreshLayout refresh;
    private RecyclerView rec;
    private TextView empty;
    /**
     * 현재 사용자 정보 저장*/
    private userTable userData = new userTable();
    /**
     * 현재 사용자가 운영 (가입된) 동아리 목록*/
    private ArrayList<clubusersTable> userClubs = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.activity_more_myboard_tab1, container, false);
        /**
         * intent 값 받아오기 */
        userData = (userTable) getArguments().getSerializable("userData");
        userClubs = (ArrayList<clubusersTable>) getArguments().getSerializable("userClubs");

        /**
         * id 연결*/
        empty = (TextView) view.findViewById(R.id.more_tab1_layout_empty);
        rec = (RecyclerView)view.findViewById(R.id.more_tab1_layout_rev);
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
        Intent intent = new Intent(getContext(), moreMyboardActivity.class);
        intent.putExtra("tab", 0);
        intent.putExtra("userData", userData);
        intent.putExtra("userClubs", userClubs);
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
        ArrayList<boardTable> boardList = new ArrayList<>();
        try{
            boardList = getBaordList(new getData().execute("http://" + ip_add + "/boardMyGet.php?studentNumber=" + userData.getStudentNumber() + "", "").get());
        }catch (Exception e){
            e.printStackTrace();
        }
        if(boardList.size() > 0){
            refresh.setVisibility(View.VISIBLE);
            empty.setText("");
        }else{
            refresh.setVisibility(View.GONE);
            empty.setText("작성한 게시글이 없습니다..");
        }
        rec.setAdapter(new RecyclerAdapter(userData, boardList,  R.layout.board_item));
        rec.setLayoutManager(new LinearLayoutManager(getContext()));
        rec.setItemAnimator(new DefaultItemAnimator());
    }
    /**
     * json to List */
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

    /**
     * Adapter -------------------------------------------------------------------------------------------- */
    class RecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
        private static final int TYPE_ROOT = 1, TYPE_LIST = 2;
        private userTable userData;
        private ArrayList<boardTable> boardList;
        private int layout1;

        /**
         * 생성자*/
        public RecyclerAdapter(userTable userData, ArrayList<boardTable> boardList, int layout1){
            this.userData = userData;
            this.boardList = boardList;
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
                final boardTable boardData = boardList.get(i);

                ((ListHolder) viewHolder).textTitle.setText(boardData.getBoardTitle());
                ((ListHolder) viewHolder).textWriter.setText(boardData.getBoardWriter());
                ((ListHolder) viewHolder).textDate.setText(boardData.getBoardDate().substring(0, 4) + "년 " + boardData.getBoardDate().substring(6, 7) + "월 " + boardData.getBoardDate().substring(9, 10) + "일 " + boardData.getBoardDate().substring(12, 16));
                ((ListHolder) viewHolder).textContent.setText(boardData.getBoardContent());
                ((ListHolder) viewHolder).writeBtn.setVisibility(View.GONE);
                ((ListHolder) viewHolder).modify_btn.setVisibility(View.GONE);

                try {
                    /**
                     *  게시물에 달린 댓글 갯수 불러오기.... */
                    ((ListHolder) viewHolder).textCommentCnt.setText(getCommentCount(new getData().execute("http://" + ip_add + "/commentGet.php?boardKind=" + boardData.getBoardKind() + "&boardID=" + boardData.getBoardID() + "", "").get()));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                ((ListHolder) viewHolder).board_View.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((ListHolder) viewHolder).intent_view.putExtra("isManagement", true);
                        ((ListHolder) viewHolder).intent_view.putExtra("boardData", boardData);
                        ((ListHolder) viewHolder).intent_view.putExtra("userData", userData);
                        ((ListHolder) viewHolder).context.startActivity(((ListHolder) viewHolder).intent_view);
                    }
                });
            }
        }

        /**
         * 뷰에 나타낼 데이터의 갯수*/
        @Override
        public int getItemCount() {
            return boardList.size();
        }

        /**
         * Holder 생성*/
        public class ListHolder extends RecyclerView.ViewHolder{

            public Intent intent_view, intent_modify;
            public TextView textTitle, textWriter, textDate, textContent, textCommentCnt;
            public Button writeBtn;
            public ConstraintLayout board_View, modify_btn;
            public Context context;
            /**
             * 생성자 id 지정*/
            public ListHolder(@NonNull View itemView) {
                super(itemView);
                context = itemView.getContext();
                intent_view = new Intent(itemView.getContext(), boardActivity.class);
                textTitle = (TextView) itemView.findViewById(R.id.re_item_layout_boardTitle);
                textWriter = (TextView) itemView.findViewById(R.id.re_item_layout_studentNumber);
                textDate = (TextView) itemView.findViewById(R.id.re_item_layout_boardDate);
                textContent = (TextView) itemView.findViewById(R.id.re_item_layout_boardContent);
                textCommentCnt = (TextView) itemView.findViewById(R.id.re_item_layout_boardCount);
                writeBtn = (Button) itemView.findViewById(R.id.re_item_layout_CommentWriteBtn);
                board_View = (ConstraintLayout) itemView.findViewById(R.id.re_item_layout);
                modify_btn = (ConstraintLayout) itemView.findViewById(R.id.re_item_layout_modify);
            }
        }
    }
    /**
     * 댓글 갯수, 동아리 회원수 들고오기
     */
    private String getCommentCount(String Json) {
        String TAG_JSON = "root";
        int cnt = 0;
        try {
            JSONObject jsonObject = new JSONObject(Json);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);
            cnt = jsonArray.length();

        } catch (JSONException e) {
            Log.d(TAG, "showResult : ", e);
            return "0";
        }
        return String.valueOf(cnt);
    }
}