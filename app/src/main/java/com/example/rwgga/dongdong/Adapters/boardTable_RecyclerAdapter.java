package com.example.rwgga.dongdong.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;


import com.example.rwgga.dongdong.R;
import com.example.rwgga.dongdong.board.boardActivity;
import com.example.rwgga.dongdong.board.boardWriteActivity;
import com.example.rwgga.dongdong.club.clubActivity;
import com.example.rwgga.dongdong.database_table.boardTable;
import com.example.rwgga.dongdong.database_table.clubListTable;
import com.example.rwgga.dongdong.database_table.clubusersTable;
import com.example.rwgga.dongdong.database_table.userTable;
import com.example.rwgga.dongdong.main.MainActivity;
import com.example.rwgga.dongdong.php_server.getData;
import com.example.rwgga.dongdong.php_server.insertData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 *
 * 작성자 : Created by (김정환, 이현원)
 *
 * 마지막 수정 날짜 : 2018. 11. 26
 *
 * 게시물 목록 출력
 */
public class boardTable_RecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static String ip_add = "10.0.2.2"; // php 통신을 위한 안드로이드 아이피 정보
    private static String TAG = "phptest";
    private static final int TYPE_ROOT = 1, TYPE_LIST = 2;
    private userTable userData;
    private clubListTable club;
    private ArrayList<boardTable> boardList;
    private clubusersTable clubInfo;
    private int board_Layout, club_layout;

    /**
     * 생성자
     */
    public boardTable_RecyclerAdapter(userTable userData, ArrayList<boardTable> boardList, clubusersTable clubInfo, clubListTable club, int board_Layout, int club_layout) {
        this.userData = userData;
        this.boardList = boardList;
        this.clubInfo = clubInfo;
        this.club = club;
        this.board_Layout = board_Layout;
        this.club_layout = club_layout;
    }

    /**
     * 뷰 타입 지정
     */
    @Override
    public int getItemViewType(int position) {
        if (clubInfo != null)
            return position == 0 ? TYPE_ROOT : TYPE_LIST;
        else {
            return TYPE_LIST;
        }
    }

    /**
     * 뷰 홀더를 어떻게 생성 할 것인지. 새로운 뷰 생성
     */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if (viewType == TYPE_ROOT) {
            View itemView = LayoutInflater.from(viewGroup.getContext())
                    .inflate(club_layout, viewGroup, false);
            return new RootHolder(itemView);
        } else {
            View itemView = LayoutInflater.from(viewGroup.getContext())
                    .inflate(board_Layout, viewGroup, false);
            return new ListHolder(itemView);
        }
    }

    /**
     * 뷰 홀더를 데이터와 바인딩 시킬때 어떻게 할 것인가 ?
     */
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, int i) {
        if (viewHolder instanceof RootHolder) {
            /**
             * ------------------ 클럽 상단 정보 값 셋팅*/
            if (clubInfo == null) { // 동아리 정보가 없다면 .. 클럽 정보는 나타낼 필요가 없음
                ((RootHolder) viewHolder).layout.setVisibility(View.GONE);
            } else {
                if(boardList.size() == 0){
                    ((RootHolder)viewHolder).textEmpty.setVisibility(View.VISIBLE);
                }else{
                    ((RootHolder)viewHolder).textEmpty.setVisibility(View.GONE);
                }
                ((RootHolder) viewHolder).layout.setVisibility(View.VISIBLE);
                ((RootHolder) viewHolder).textTitle.setText(club.getClubName());
                ((RootHolder) viewHolder).textCnt.setText(String.valueOf(club.getClubUserCnt()));
                /**
                 * 게시물 작성 ietent로*/
                ((RootHolder) viewHolder).textWriteBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        /**
                         * 게시물 작성 */
                        ((RootHolder)viewHolder).intent_write.putExtra("option","write");
                        ((RootHolder)viewHolder).intent_write.putExtra("clubInfo", clubInfo);
                        ((RootHolder)viewHolder).intent_write.putExtra("userData",userData);
                        ((RootHolder)viewHolder).intent_write.putExtra("move", "listView");
                        ((RootHolder)viewHolder).context.startActivity(((RootHolder) viewHolder).intent_write);
                        ((Activity) ((RootHolder) viewHolder).context).finish();
                    }
                });
            }
        } else {
            /**
             * ------------------ 게시물 리스트 값 셋팅 하는 부분*/
            int index = clubInfo == null ? i : i - 1; // 동아리 정보가 없다면 index를 처음부터 있다면 index - 1 부터
            final boardTable boardData = boardList.get(index); // 현재 index에 맞는 게시물 정보 를 boardTable에 저장

            ((ListHolder) viewHolder).textTitle.setText(boardData.getBoardTitle());
            ((ListHolder) viewHolder).textWriter.setText(getName(boardData.getBoardWriter()) + "(" + boardData.getBoardWriter() + ")");
            ((ListHolder) viewHolder).textDate.setText(boardData.getBoardDate().substring(0, 4) + "년 " + boardData.getBoardDate().substring(6, 7) + "월 " + boardData.getBoardDate().substring(9, 10) + "일 " + boardData.getBoardDate().substring(12, 16));
            ((ListHolder) viewHolder).textContent.setText(boardData.getBoardContent());
            try {
                /**
                 *  게시물에 달린 댓글 갯수 불러오기.... */
                ((ListHolder) viewHolder).textCommentCnt.setText(getCommentCount(new getData().execute("http://" + ip_add + "/commentGet.php?boardKind=" + boardData.getBoardKind() + "&boardID=" + boardData.getBoardID() + "", "").get()));
            } catch (Exception e) {
                e.printStackTrace();
            }

            /**
             * 홍보게시판은 댓글작성하기 버튼이 안보인다*/
            if (boardData.getBoardKind() == -1) {
                ((ListHolder) viewHolder).writeBtn.setVisibility(View.GONE);
            }

            /**
             * 게시물 클릭시 게시물 보기 액티비티로 이동*/
            ((ListHolder) viewHolder).board_View.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /**
                     * 만약 해당 게시물을 자세히 보려고 클릭한다면 게시물을 자세히 볼 수 있는 activity(boardActivity) 로 게시물의 값을 넘겨준다 */
                    ((ListHolder) viewHolder).intent_view.putExtra("writeComment", false);
                    ((ListHolder) viewHolder).intent_view.putExtra("boardData", boardData);
                    ((ListHolder) viewHolder).intent_view.putExtra("userData", userData);
                    ((ListHolder) viewHolder).intent_view.putExtra("clubInfo", clubInfo);
                    ((ListHolder) viewHolder).context.startActivity(((ListHolder) viewHolder).intent_view);
                    ((Activity) ((ListHolder) viewHolder).context).finish();
                }
            });

            /**
             * 게시물 목록에서 댓글작성버튼 클릭시 */
            ((ListHolder) viewHolder).writeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((ListHolder) viewHolder).intent_view.putExtra("writeComment", true);
                    ((ListHolder) viewHolder).intent_view.putExtra("boardData", boardData);
                    ((ListHolder) viewHolder).intent_view.putExtra("userData", userData);
                    ((ListHolder) viewHolder).intent_view.putExtra("clubInfo", clubInfo);
                    ((ListHolder) viewHolder).context.startActivity(((ListHolder) viewHolder).intent_view);
                    ((Activity) ((ListHolder) viewHolder).context).finish();
                }
            });

            /**
             * 삭제 수정 버튼*/
            ((ListHolder) viewHolder).modify_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    /**
                     * 팝업 메뉴 생성*/
                    PopupMenu popupMenu = new PopupMenu(((ListHolder) viewHolder).context, v);
                    ((Activity) ((ListHolder) viewHolder).context).getMenuInflater().inflate(R.menu.menu_modiify, popupMenu.getMenu());
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.m1:
                                    /**
                                     * 만약 게시물 작성자가 아니라면 */
                                    if (!userData.getStudentNumber().equals(boardData.getBoardWriter())) {
                                        Toast.makeText(((ListHolder) viewHolder).context, "게시물 작성자가 아닙니다.", Toast.LENGTH_SHORT).show();
                                    } else {
                                        /**
                                         * 게시물 수정 페이지로 이동 ( boardWrite )*/
                                        ((ListHolder) viewHolder).intent_modify.putExtra("option", "modify");
                                        ((ListHolder) viewHolder).intent_modify.putExtra("userData", userData);
                                        ((ListHolder) viewHolder).intent_modify.putExtra("clubInfo", clubInfo);
                                        ((ListHolder) viewHolder).intent_modify.putExtra("boardData", boardData);
                                        ((ListHolder) viewHolder).intent_modify.putExtra("move", "listView");
                                        ((ListHolder) viewHolder).context.startActivity(((ListHolder) viewHolder).intent_modify);
                                        ((Activity) ((ListHolder) viewHolder).context).finish();
                                    }
                                    break;
                                case R.id.m2:
                                    if (!userData.getStudentNumber().equals(boardData.getBoardWriter())) {
                                        Toast.makeText(((ListHolder) viewHolder).context, "게시물 작성자가 아닙니다.", Toast.LENGTH_SHORT).show();
                                    } else {
                                        /**
                                         * 게시물 삭제*/
                                        AlertDialog.Builder builder = new AlertDialog.Builder(((ListHolder) viewHolder).context);
                                        builder.setTitle("정말로 삭제하시겠습니까?");
                                        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                String insertParameter = "boardID=" + boardData.getBoardID();
                                                insertData insertData = new insertData();
                                                insertData.execute("http://" + ip_add + "/boardDelete.php", insertParameter);
                                                Toast.makeText(((ListHolder) viewHolder).context, "게시물을 삭제 했습니다.", Toast.LENGTH_SHORT).show();
                                                /**
                                                 * (새로고침)*/
                                                if (boardData.getBoardKind() == -1) {
                                                    /**
                                                     * 홍보 게시판 */
                                                    ((ListHolder) viewHolder).intent_main.putExtra("userData", userData);
                                                    ((ListHolder) viewHolder).intent_main.putExtra("tab", 1);
                                                    ((ListHolder) viewHolder).context.startActivity(((ListHolder) viewHolder).intent_main);
                                                } else {
                                                    /**
                                                     * 동아리 게시판 */
                                                    ((ListHolder) viewHolder).intent_club.putExtra("userData", userData);
                                                    ((ListHolder) viewHolder).intent_club.putExtra("clubInfo", clubInfo);
                                                    ((ListHolder) viewHolder).context.startActivity(((ListHolder) viewHolder).intent_club);
                                                }
                                                ((Activity) ((ListHolder) viewHolder).context).overridePendingTransition(0, 0); // intent를 불러올때 애니메이션 삭제
                                                ((Activity) ((ListHolder) viewHolder).context).finish();
                                            }
                                        });
                                        builder.setNegativeButton("아니요", null);
                                        builder.show();
                                    }
                                    break;
                            }
                            return false;
                        }
                    });
                    popupMenu.show();
                }
            });
        }
    }

    /**
     * 뷰에 나타낼 데이터의 갯수
     */
    @Override
    public int getItemCount() {
        return clubInfo == null ? boardList.size() : boardList.size() + 1;
    }

    /**
     * 반복되는 리스트에서 매번 findViewById는 리소스를 잡아먹으므로
     * 뷰들을 홀딩하고있다마 계속 해서 recycle하기 위해서 만든 홀더이다.
     */
    public class RootHolder extends RecyclerView.ViewHolder {
        /**
         * ------------------ 클럽 상단 정보 홀더
         */
        public Intent intent_write;
        public ConstraintLayout layout;
        public TextView textTitle, textCnt, textEmpty;
        public LinearLayout textWriteBtn;
        public Context context;

        public RootHolder(@NonNull final View itemView) {
            super(itemView);
            context = itemView.getContext();
            intent_write = new Intent(itemView.getContext(), boardWriteActivity.class);
            layout = (ConstraintLayout) itemView.findViewById(R.id.club_main_item_layout);
            textTitle = (TextView) itemView.findViewById(R.id.club_main_item_Title);
            textCnt = (TextView) itemView.findViewById(R.id.club_main_item_userCnt);
            textEmpty = (TextView) itemView.findViewById(R.id.club_main_item_empty);
            textWriteBtn = (LinearLayout) itemView.findViewById(R.id.club_main_item_writeBtn);
        }
    }

    public class ListHolder extends RecyclerView.ViewHolder {
        /**
         * ------------------ 게시물 리스트 홀더
         */
        public Intent intent_view, intent_modify, intent_main, intent_club;
        public TextView textTitle, textWriter, textDate, textContent, textCommentCnt;
        public Button writeBtn;
        public ConstraintLayout board_View, modify_btn;
        public Context context;

        public ListHolder(@NonNull final View itemView) {
            super(itemView);
            context = itemView.getContext();
            intent_main = new Intent(itemView.getContext(), MainActivity.class);
            intent_view = new Intent(itemView.getContext(), boardActivity.class);
            intent_club = new Intent(itemView.getContext(), clubActivity.class);
            intent_modify = new Intent(itemView.getContext(), boardWriteActivity.class);
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