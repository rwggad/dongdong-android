package com.example.rwgga.dongdong.Adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rwgga.dongdong.R;
import com.example.rwgga.dongdong.club.clubActivity;
import com.example.rwgga.dongdong.database_table.clubListTable;
import com.example.rwgga.dongdong.database_table.clubusersTable;
import com.example.rwgga.dongdong.database_table.userTable;
import com.example.rwgga.dongdong.main.clubJoinActivity;
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
 * 현재 개설된 동아리 목록
 */
public class clubListTable_RecyclerAdapter extends RecyclerView.Adapter<clubListTable_RecyclerAdapter.ViewHolder> {
    /**
     * 변수*/
    private static String ip_add = "10.0.2.2";
    private static String TAG = "phptest";
    private int itemLayout;
    private final String[] kind = {"전체", "문예", "봉사", "종교", "체육", "학술", "기타"};

    /**
     * 안드로이드 객체*/
    private userTable userData;
    private ArrayList<clubListTable> clubList;

    /**
     * 생성자 */
    public clubListTable_RecyclerAdapter(userTable userData, ArrayList<clubListTable> clubList , int itemLayout){
        this.userData = userData;
        this.clubList = clubList;
        this.itemLayout = itemLayout;
    }

    /**
     * 뷰 홀더를 어떻게 생성 할 것인지. 새로운 뷰 생성*/
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(itemLayout,viewGroup,false);
        return new ViewHolder(view);
    }

    /**
     * 뷰 홀더를 데이터와 바인딩 시킬때 어떻게 할 것인가 ? */
    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int position) {
        /**
         * 현재 index (postion)에 맞는 데이터로 동아리 목록등을 설정한다. */
        final clubListTable club = clubList.get(position);
        viewHolder.textName.setText(club.getClubName());
        viewHolder.textContent.setText(club.getClubContent());
        viewHolder.textSize.setText(String.valueOf(club.getClubUserCnt()));
        viewHolder.textCaptain.setText(club.getClubCaptain());
        viewHolder.textKind.setText(kind[club.getClubKind()]);

        /**
         * 사용자가 원하는 동아리를 누르면 AlertDialog 가 뜬다. */
        viewHolder.builder
                .setTitle(club.getClubName())
                .setMessage(club.getClubContent() + "\n" + "문의 : " + club.getClubPhoneNumber() + "\n" + "위치 : " + club.getClubLocation())
                .setPositiveButton("가입하기", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        /**
                         * 이미 가입한 회원인지 확인하고 입하지 않은 회원이라면 회원 가입 창으로 이동*/
                        try{
                            // 회원이 아니라면
                            if(getStudentNumber(new getData().execute("http://" + ip_add + "/isClubuser.php?studentNumber=" + userData.getStudentNumber() + "&boardKind=" + club.getBoardKind(), "").get()) == false){
                                if(getStudentNumber(new getData().execute("http://" + ip_add + "/isWait.php?studentNumber=" + userData.getStudentNumber() + "&boardKind=" + club.getBoardKind(), "").get()) == true){
                                    // 신청 한 동아리라면
                                    Toast.makeText(viewHolder.context, "이미 " + club.getClubName() + " 가입 신청이 진행중입니다." ,Toast.LENGTH_SHORT).show();
                                }else{
                                    viewHolder.intent_club_join.putExtra("userData", userData);
                                    viewHolder.intent_club_join.putExtra("club",club);
                                    viewHolder.context.startActivity(viewHolder.intent_club_join);
                                    ((Activity)viewHolder.context).finish();
                                }
                            }
                            // 이미 회원이라면
                            else{
                                Toast.makeText(viewHolder.context, "이미 " + club.getClubName() + " 동아리 회원입니다." ,Toast.LENGTH_SHORT).show();
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                })
                .setNegativeButton("닫기", null);

        viewHolder.cl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog alertDialog = viewHolder.builder.create();
                alertDialog.show();
            }
        });
    }

    /**
     * 뷰에 나타낼 데이터의 갯수 */
    @Override
    public int getItemCount() {
        return clubList.size();
    }

    /**
     * 반복되는 리스트에서 매번 findViewById는 리소스를 잡아먹으므로
     * 뷰들을 홀딩하고있다마 계속 해서 recycle하기 위해서 만든 홀더이다. */
    public static class ViewHolder extends RecyclerView.ViewHolder{
        public AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext());
        public TextView textName, textContent, textSize, textCaptain, textKind;
        public ConstraintLayout cl;
        public Context context;
        public Intent intent_club, intent_club_join;

        public ViewHolder(final View itemView){
            /**
             * id 연결부 */
            super(itemView);
            context = itemView.getContext();
            intent_club = new Intent(itemView.getContext(), clubActivity.class);
            intent_club_join = new Intent(itemView.getContext(), clubJoinActivity.class);
            textName = (TextView) itemView.findViewById(R.id.club_item_layout_clubName);
            textContent = (TextView) itemView.findViewById(R.id.club_item_layout_clubContent);
            textSize = (TextView) itemView.findViewById(R.id.club_item_layout_clubSize);
            textCaptain = (TextView) itemView.findViewById(R.id.club_item_layout_studentNumber);
            textKind = (TextView) itemView.findViewById(R.id.club_item_layout_clubKind);
            cl = (ConstraintLayout) itemView.findViewById(R.id.club_item_layout);
        }
    }
    /**
     * check isIn (studentNumber)*/
    private Boolean getStudentNumber(String Json){
        String isInData = "";
        String TAG_JSON="root";
        String TAG_studentNumber = "studentNumber";
        try {
            JSONObject jsonObject = new JSONObject(Json);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject item = jsonArray.getJSONObject(i);
                // Josn 으로 변환된 값들 저장
                isInData = item.getString(TAG_studentNumber);
            }
        } catch (JSONException e) {
            Log.d(TAG, "showResult : ", e);
        }
        if(isInData.equals(""))
            // 회원이 아니라면
            return false;
        else
            // 이미 회원이라면 true
            return true;
    }
}