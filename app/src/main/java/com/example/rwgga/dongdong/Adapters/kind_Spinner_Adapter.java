package com.example.rwgga.dongdong.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.rwgga.dongdong.R;

import java.util.List;

/**
 *
 * 작성자 : Created by (이현원)
 *
 * 마지막 수정 날짜 : 2018. 11. 15
 *
 * 분류에 맞는 동아리 가져오기
 */
public class kind_Spinner_Adapter extends BaseAdapter {
    Context context;
    List<String> data;
    LayoutInflater inflater;


    public kind_Spinner_Adapter(Context context, List<String> data){
        this.context = context;
        this.data = data;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {
        if(data!=null) return data.size();
        else return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null) {
            convertView = inflater.inflate(R.layout.kind_spinner_nomarl, parent, false);
        }
        if(data!=null){
            //데이터세팅
            switch (position){
                case 0:
                    ((ImageView)convertView.findViewById(R.id.kind_spinnerImage)).setBackgroundResource(R.drawable.kbtn0);
                    break;
                case 1:
                    ((ImageView)convertView.findViewById(R.id.kind_spinnerImage)).setBackgroundResource(R.drawable.kbtn1);
                    break;
                case 2:
                    ((ImageView)convertView.findViewById(R.id.kind_spinnerImage)).setBackgroundResource(R.drawable.kbtn2);
                    break;
                case 3:
                    ((ImageView)convertView.findViewById(R.id.kind_spinnerImage)).setBackgroundResource(R.drawable.kbtn3);
                    break;
                case 4:
                    ((ImageView)convertView.findViewById(R.id.kind_spinnerImage)).setBackgroundResource(R.drawable.kbtn4);
                    break;
                case 5:
                    ((ImageView)convertView.findViewById(R.id.kind_spinnerImage)).setBackgroundResource(R.drawable.kbtn5);
                    break;
                case 6:
                    ((ImageView)convertView.findViewById(R.id.kind_spinnerImage)).setBackgroundResource(R.drawable.kbtn6);
                    break;
            }
            String text = data.get(position);
            ((TextView)convertView.findViewById(R.id.kind_spinnerText)).setText(text);
        }

        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            convertView = inflater.inflate(R.layout.kind_spinner_dropdown, parent, false);
        }

        //데이터세팅
        switch (position){
            case 0:
                ((ImageView)convertView.findViewById(R.id.kind_spinner_dropdownImage)).setBackgroundResource(R.drawable.kbtn0);
                break;
            case 1:
                ((ImageView)convertView.findViewById(R.id.kind_spinner_dropdownImage)).setBackgroundResource(R.drawable.kbtn1);
                break;
            case 2:
                ((ImageView)convertView.findViewById(R.id.kind_spinner_dropdownImage)).setBackgroundResource(R.drawable.kbtn2);
                break;
            case 3:
                ((ImageView)convertView.findViewById(R.id.kind_spinner_dropdownImage)).setBackgroundResource(R.drawable.kbtn3);
                break;
            case 4:
                ((ImageView)convertView.findViewById(R.id.kind_spinner_dropdownImage)).setBackgroundResource(R.drawable.kbtn4);
                break;
            case 5:
                ((ImageView)convertView.findViewById(R.id.kind_spinner_dropdownImage)).setBackgroundResource(R.drawable.kbtn5);
                break;
            case 6:
                ((ImageView)convertView.findViewById(R.id.kind_spinner_dropdownImage)).setBackgroundResource(R.drawable.kbtn6);
                break;
        }
        String text = data.get(position);
        ((TextView)convertView.findViewById(R.id.kind_spinner_dropdownText)).setText(text);
        return convertView;
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
