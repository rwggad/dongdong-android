package com.example.rwgga.dongdong.main_more;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rwgga.dongdong.R;
import com.example.rwgga.dongdong.database_table.clubusersTable;
import com.example.rwgga.dongdong.database_table.commentTable;
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
 * 마지막 수정 날짜 : 2018. 11. 24
 *
 *
 * 더보기 페이지 내가 쓴 글 - 댓글 목록
 */
public class moreMyboardTab2Activity extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
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
        final View view = inflater.inflate(R.layout.activity_more_myboard_tab2, container, false);
        /**
         * intent 값 받아오기 */
        userData = (userTable) getArguments().getSerializable("userData");
        userClubs = (ArrayList<clubusersTable>) getArguments().getSerializable("userClubs");

        /**
         * id 연결*/
        empty = (TextView) view.findViewById(R.id.more_tab2_layout_empty);
        rec = (RecyclerView)view.findViewById(R.id.more_tab2_layout_rev);
        refresh = (SwipeRefreshLayout) view.findViewById(R.id.refresh);

        /**
         * 세로고침 */
        refresh.setOnRefreshListener(this);

        /**
         * recycler*/
        initData();

        return view;
    }@Override
    public void onRefresh() {
        Intent intent = new Intent(getContext(), moreMyboardActivity.class);
        intent.putExtra("tab", 1);
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
        ArrayList<commentTable> commentList = new ArrayList<>();
        try{
            commentList = getCommentList(new getData().execute("http://" + ip_add + "/commentMyGet.php?studentNumber=" + userData.getStudentNumber() + "", "").get());
        }catch (Exception e){
            e.printStackTrace();
        }
        /**
         * 자기가 작성한 게시물이 없다면 */
        if(commentList.size() > 0){
            refresh.setVisibility(View.VISIBLE);
            empty.setText("");
        }else{
            refresh.setVisibility(View.GONE);
            empty.setText("작성한 댓글이 없습니다.");
        }
        rec.setAdapter(new RecyclerAdapter(userData, commentList, R.layout.comment_item));
        rec.setLayoutManager(new LinearLayoutManager(getContext()));
        rec.setItemAnimator(new DefaultItemAnimator());
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

    /**
     * Adapter -------------------------------------------------------------------------------------------- */
    class RecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
        private static final int TYPE_ROOT = 1, TYPE_LIST = 2;
        private userTable userData;
        private ArrayList<commentTable> commentList;
        private int layout1;

        /**
         * 생성자*/
        public RecyclerAdapter(userTable userData, ArrayList<commentTable> commentList, int layout1){
            this.userData = userData;
            this.commentList = commentList;
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
                return new RecyclerAdapter.ListHolder(itemView);
            }
            return null;
        }

        /**
         * 데이터 매칭*/
        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, int i) {
            if (viewHolder instanceof RecyclerAdapter.ListHolder) {
                final commentTable commentData = commentList.get(i);
                ((ListHolder)viewHolder).textWriter.setText(commentData.getCommentWriter());
                ((ListHolder)viewHolder).textDate.setText(commentData.getCommentDate());
                ((ListHolder)viewHolder).textContent.setText(commentData.getCommentContent());
                /**
                 * 댓글 */
                ((ListHolder)viewHolder).delBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!commentData.getCommentWriter().equals(userData.getStudentNumber())){
                            Toast.makeText(((ListHolder)viewHolder).context, "덧글 작성자가 아닙니다.",Toast.LENGTH_SHORT).show();
                        }else{
                            /**
                             * 댓글 삭제*/
                            AlertDialog.Builder builder = new AlertDialog.Builder(((ListHolder) viewHolder).context);
                            builder.setTitle("정말로 삭제하시겠습니까?");
                            builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String insertParameter = "commentID=" + commentData.getCommentID();
                                    insertData insertData = new insertData();
                                    insertData.execute("http://" + ip_add + "/commentDelete.php", insertParameter);
                                    Toast.makeText(((ListHolder)viewHolder).context, "댓글을 삭제 했습니다.",Toast.LENGTH_SHORT).show();
                                    /**
                                     * 삭제 후 메인 엑티비티로 이동 (새로고침)*/
                                    ((ListHolder)viewHolder).intent_manage.putExtra("userData",userData);
                                    ((ListHolder)viewHolder).intent_manage.putExtra("userClubs",userClubs);
                                    ((ListHolder)viewHolder).intent_manage.putExtra("tab",1);
                                    ((Activity)((ListHolder)viewHolder).context).startActivity(((ListHolder)viewHolder).intent_manage);
                                    ((Activity)((ListHolder)viewHolder).context).overridePendingTransition(0, 0); // intent를 불러올때 애니메이션 삭제
                                    ((Activity)((ListHolder)viewHolder).context).finish();
                                }
                            });
                            builder.setNegativeButton("아니요", null);
                            builder.show();
                        }
                    }
                });

            }
        }

        /**
         * 뷰에 나타낼 데이터의 갯수*/
        @Override
        public int getItemCount() {
            return commentList.size();
        }

        /**
         * Holder 생성*/
        public class ListHolder extends RecyclerView.ViewHolder{
            /**
             * 댓글 리스트 홀더 */
            ConstraintLayout delBtn;
            TextView textWriter, textDate, textContent;
            Context context;
            Intent intent_manage;
            public ListHolder(final View itemView) {
                super(itemView);
                context = itemView.getContext();
                intent_manage = new Intent(itemView.getContext(), moreMyboardActivity.class);
                delBtn = (ConstraintLayout) itemView.findViewById(R.id.comment_item_layout_modify);
                textWriter = (TextView) itemView.findViewById(R.id.comment_item_layout_studentNumber);
                textDate = (TextView) itemView.findViewById(R.id.comment_item_layout_boardDate);
                textContent = (TextView) itemView.findViewById(R.id.comment_item_layout_Content);
            }
        }
    }
}