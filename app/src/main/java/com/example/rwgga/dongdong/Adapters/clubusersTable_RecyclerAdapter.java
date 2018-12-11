package com.example.rwgga.dongdong.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.rwgga.dongdong.R;
import com.example.rwgga.dongdong.club.clubActivity;
import com.example.rwgga.dongdong.database_table.clubusersTable;
import com.example.rwgga.dongdong.database_table.userTable;

import java.util.ArrayList;

/**
 *
 * 작성자 : Created by (rwggad@gmail.com - 김정환(KimJeongHwan)
 * 내 동아리 목록 보여주는
 */
public class clubusersTable_RecyclerAdapter extends RecyclerView.Adapter<clubusersTable_RecyclerAdapter.ViewHolder> {
    private static String ip_add = "10.0.2.2";
    private static String TAG = "phptest";

    private userTable userData;
    private ArrayList<clubusersTable> clubList;
    private int itemLayout;

    /**
     * 생성자 */
    public clubusersTable_RecyclerAdapter(userTable userData, ArrayList<clubusersTable> clubList, int itemLayout){
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
        final clubusersTable clubInfo = clubList.get(position);
        /**
         * 값셋팅*/
        viewHolder.textName.setText(clubInfo.getClubName());

        /**
         * item 선택시 클럽 메인화면으로 이동 */
        viewHolder.cl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * 자기가 가입한 동아리를 누르게 되면 그 동아리의 main Activity를 띄우는데 해당 동아리 정보와
                 * 현재 사용자의 정보, staff 정보를 넘겨줌 (clubActiivity) */
                viewHolder.intent.putExtra("clubInfo", clubInfo);
                viewHolder.intent.putExtra("userData", userData);
                viewHolder.context.startActivity(viewHolder.intent);
                ((Activity)(viewHolder).context).finish();
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
     * 뷰들을 홀딩하고있다마 계속 해서 recycle하기 위해서 만든 홀더이다.
     * */
    public static class ViewHolder extends RecyclerView.ViewHolder{
        public Intent intent;
        public TextView textName;
        public LinearLayout cl;
        public Context context;

        public ViewHolder(final View itemView){
            super(itemView);
            /**
             * id 연결*/
            context = itemView.getContext();
            intent = new Intent(itemView.getContext(), clubActivity.class);
            textName = (TextView) itemView.findViewById(R.id.myclub_item_layout_clubName);
            cl = (LinearLayout) itemView.findViewById(R.id.myclub_item_layout);
        }
    }
}