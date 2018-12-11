package com.example.rwgga.dongdong.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rwgga.dongdong.R;
import com.example.rwgga.dongdong.board.boardActivity;
import com.example.rwgga.dongdong.board.boardWriteActivity;
import com.example.rwgga.dongdong.club.clubActivity;
import com.example.rwgga.dongdong.database_table.boardTable;
import com.example.rwgga.dongdong.database_table.clubusersTable;
import com.example.rwgga.dongdong.database_table.commentTable;
import com.example.rwgga.dongdong.database_table.userTable;
import com.example.rwgga.dongdong.main.MainActivity;
import com.example.rwgga.dongdong.main_more.moreMyboardActivity;
import com.example.rwgga.dongdong.php_server.getData;
import com.example.rwgga.dongdong.php_server.insertData;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 *
 * 작성자 : Created by (rwggad@gmail.com - 김정환(KimJeongHwan)
 * 특정 게시물 자세한 내용과 댓글 리스트 불러오기
 */
public class commentTable_RecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static String ip_add = "10.0.2.2"; // php 통신을 위한 안드로이드 아이피 정보
    private static final int TYPE_ROOT = 1, TYPE_LIST = 2;
    private userTable userData;
    private ArrayList<commentTable> commentData;
    private clubusersTable clubInfo;
    private boardTable boardData;
    private int board_layout, comment_layout;
    private boolean isManagement;

    /**
     * 생성자 */
    public commentTable_RecyclerAdapter(clubusersTable clubInfo, userTable userData, ArrayList<commentTable> comment_item , boardTable board_item, int comment_layout, int board_layout, boolean isManagement){
        this.clubInfo = clubInfo;
        this.userData = userData;
        this.commentData = comment_item;
        this.boardData = board_item;
        this.comment_layout = comment_layout;
        this.board_layout = board_layout;
        this.isManagement = isManagement;
    }

    /**
     * 뷰 타입 지정*/
    @Override
    public int getItemViewType(int position) {
        if(position == 0){
            return TYPE_ROOT;
        }else{
            return TYPE_LIST;
        }
    }

    /**
     * 뷰 홀더를 어떻게 생성 할 것인지. 새로운 뷰 생성*/
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if(viewType == TYPE_ROOT){
            View itemView = LayoutInflater.from(viewGroup.getContext())
                    .inflate(board_layout, viewGroup, false);
            return new RootHolder(itemView);
        }else{
            View itemView = LayoutInflater.from(viewGroup.getContext())
                    .inflate(comment_layout, viewGroup, false);
            return new ListHolder(itemView);
        }
    }

    /**
     * 뷰 홀더를 데이터와 바인딩 시킬때 어떻게 할 것인가 ? */
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int i) {
        if(viewHolder instanceof RootHolder){
            /**
             * 게시물을 볼때 마다 조회수를 count 해준다. */
            String insertParameter = "boardID=" + boardData.getBoardID();
            insertData insertData = new insertData();
            insertData.execute("http://" + ip_add + "/boardCountUpdate.php", insertParameter);
            /**
             * 게시물 정보 값 셋팅*/
            ((RootHolder)viewHolder).textCnt.setText(String.valueOf(boardData.getBoardCount()));
            ((RootHolder)viewHolder).textContent.setText(boardData.getBoardContent());
            ((RootHolder)viewHolder).textDate.setText(boardData.getBoardDate().substring(0,4) + "년 " + boardData.getBoardDate().substring(6,7) + "월 " + boardData.getBoardDate().substring(9,10) + "일 " + boardData.getBoardDate().substring(12,16));
            ((RootHolder)viewHolder).textWriter.setText(getName(boardData.getBoardWriter()) + "(" + boardData.getBoardWriter() + ")");
            /**
             * 게시물 수정 값 셋팅*/
            ((RootHolder)viewHolder).modify_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popupMenu = new PopupMenu(((RootHolder) viewHolder).context, v);
                    ((Activity)((RootHolder) viewHolder).context).getMenuInflater().inflate(R.menu.menu_modiify,popupMenu.getMenu());
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()){
                                case R.id.m1:
                                    /**
                                     * 게시물 작성자가 아니라면*/
                                    if(!userData.getStudentNumber().equals(boardData.getBoardWriter())){
                                        Toast.makeText(((RootHolder) viewHolder).context, "게시물 작성자가 아닙니다.", Toast.LENGTH_SHORT).show();
                                    }else{
                                        /**
                                         * 게시물 수정 페이지로 이동 ( boardWrite )*/
                                        ((RootHolder) viewHolder).intent_modify.putExtra("option", "modify");
                                        ((RootHolder) viewHolder).intent_modify.putExtra("userData", userData);
                                        ((RootHolder) viewHolder).intent_modify.putExtra("clubInfo", clubInfo);
                                        ((RootHolder) viewHolder).intent_modify.putExtra("boardData", boardData);
                                        ((RootHolder) viewHolder).intent_modify.putExtra("move", "detailView");
                                        ((RootHolder) viewHolder).context.startActivity(((RootHolder) viewHolder).intent_modify);
                                        ((Activity) ((RootHolder) viewHolder).context).finish();
                                    }
                                    break;
                                case R.id.m2:
                                    if(!userData.getStudentNumber().equals(boardData.getBoardWriter())){
                                        Toast.makeText(((RootHolder) viewHolder).context, "게시물 작성자가 아닙니다.", Toast.LENGTH_SHORT).show();
                                    }else{
                                        /**
                                         * 게시물 삭제*/
                                        AlertDialog.Builder builder = new AlertDialog.Builder(((RootHolder) viewHolder).context);
                                        builder.setTitle("정말로 삭제하시겠습니까?");
                                        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                String insertParameter = "boardID=" + boardData.getBoardID();
                                                insertData insertData = new insertData();
                                                insertData.execute("http://" + ip_add + "/boardDelete.php", insertParameter);
                                                Toast.makeText(((RootHolder) viewHolder).context, "게시물을 삭제 했습니다.", Toast.LENGTH_SHORT).show();
                                                /**
                                                 * (새로고침)*/
                                                if(!isManagement){
                                                    if (boardData.getBoardKind() == -1) {
                                                        /**
                                                         * 홍보 게시판 */
                                                        ((RootHolder) viewHolder).intent_main.putExtra("userData", userData);
                                                        ((RootHolder) viewHolder).intent_main.putExtra("tab", 1);
                                                        ((RootHolder) viewHolder).context.startActivity(((RootHolder) viewHolder).intent_main);
                                                    } else {
                                                        /**
                                                         * 동아리 게시판 */
                                                        ((RootHolder) viewHolder).intent_club.putExtra("userData", userData);
                                                        ((RootHolder) viewHolder).intent_club.putExtra("clubInfo", clubInfo);
                                                        ((RootHolder) viewHolder).context.startActivity(((RootHolder) viewHolder).intent_club);
                                                    }
                                                }else{
                                                    ((RootHolder) viewHolder).intent_manage.putExtra("userData", userData);
                                                    ((RootHolder) viewHolder).intent_manage.putExtra("clubInfo", clubInfo);
                                                    ((RootHolder) viewHolder).context.startActivity(((RootHolder) viewHolder).intent_manage);
                                                }
                                                ((Activity) ((RootHolder) viewHolder).context).finish();
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

        }else {
            /**
             * 댓글 리스트 값 셋팅*/
            final commentTable comment = commentData.get(i - 1);
            ((ListHolder)viewHolder).textWriter.setText(getName(comment.getCommentWriter()) + "(" + comment.getCommentWriter()  + ")");
            ((ListHolder)viewHolder).textDate.setText(comment.getCommentDate());
            ((ListHolder)viewHolder).textContent.setText(comment.getCommentContent());
            /**
             * 댓글 */
            ((ListHolder)viewHolder).delBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!comment.getCommentWriter().equals(userData.getStudentNumber())){
                        Toast.makeText(((ListHolder)viewHolder).context, "덧글 작성자가 아닙니다.",Toast.LENGTH_SHORT).show();
                    }else{
                        /**
                         * 댓글 삭제*/
                        AlertDialog.Builder builder = new AlertDialog.Builder(((ListHolder) viewHolder).context);
                        builder.setTitle("정말로 삭제하시겠습니까?");
                        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String insertParameter = "commentID=" + comment.getCommentID();
                                insertData insertData = new insertData();
                                insertData.execute("http://" + ip_add + "/commentDelete.php", insertParameter);
                                Toast.makeText(((ListHolder)viewHolder).context, "댓글을 삭제 했습니다.",Toast.LENGTH_SHORT).show();
                                /**
                                 * 삭제 후 메인 엑티비티로 이동 (새로고침)*/
                                ((ListHolder)viewHolder).intent_board.putExtra("clubInfo", clubInfo);
                                ((ListHolder)viewHolder).intent_board.putExtra("userData", userData);
                                ((ListHolder)viewHolder).intent_board.putExtra("boardData", boardData);
                                ((ListHolder)viewHolder).intent_board.putExtra("writeComment", false);
                                ((ListHolder)viewHolder).intent_board.putExtra("isManagement", isManagement);
                                ((ListHolder)viewHolder).context.startActivity(((ListHolder) viewHolder).intent_board);
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
     * 뷰에 나타낼 데이터의 갯수 */
    @Override
    public int getItemCount() {
        return commentData.size() + 1;
    }

    /**
     * 반복되는 리스트에서 매번 findViewById는 리소스를 잡아먹으므로
     * 뷰들을 홀딩하고있다마 계속 해서 recycle하기 위해서 만든 홀더이다. */
    private class RootHolder extends RecyclerView.ViewHolder {
        /**
         * 게시물 내용 홀더 */
        Context context;
        ConstraintLayout modify_btn;
        TextView textWriter, textDate, textCnt, textContent;
        Intent intent_modify, intent_main, intent_club, intent_manage;
        public RootHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();
            intent_manage = new Intent(itemView.getContext(), moreMyboardActivity.class);
            intent_modify = new Intent(itemView.getContext(), boardWriteActivity.class);
            intent_main = new Intent(itemView.getContext(), MainActivity.class);
            intent_club = new Intent(itemView.getContext(), clubActivity.class);
            modify_btn = (ConstraintLayout) itemView.findViewById(R.id.board_modify);
            textWriter = (TextView) itemView.findViewById(R.id.board_studentNumber_writer);
            textDate = (TextView) itemView.findViewById(R.id.board_boardDate);
            textCnt = (TextView) itemView.findViewById(R.id.board_boardCount);
            textContent = (TextView) itemView.findViewById(R.id.board_boardContent);
        }
    }
    private class ListHolder extends RecyclerView.ViewHolder {
        /**
         * 댓글 리스트 홀더 */
        Intent intent_board;
        ConstraintLayout delBtn;
        TextView textWriter, textDate, textContent;
        Context context;
        public ListHolder(final View itemView) {
            super(itemView);
            context = itemView.getContext();
            intent_board = new Intent(itemView.getContext(), boardActivity.class);
            delBtn = (ConstraintLayout) itemView.findViewById(R.id.comment_item_layout_modify);
            textWriter = (TextView) itemView.findViewById(R.id.comment_item_layout_studentNumber);
            textDate = (TextView) itemView.findViewById(R.id.comment_item_layout_boardDate);
            textContent = (TextView) itemView.findViewById(R.id.comment_item_layout_Content);
        }
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