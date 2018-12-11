package com.example.rwgga.dongdong.main_more;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.example.rwgga.dongdong.R;
import com.example.rwgga.dongdong.database_table.clubusersTable;
import com.example.rwgga.dongdong.database_table.userTable;

import java.util.ArrayList;

/**
 *
 * 작성자 : Created by (손민성)
 *
 * 마지막 수정 날짜 : 2018. 11. 24
 *
 *
 * 더보기 페이지 내가 쓴 글
 */
public class moreMyboardActivity extends AppCompatActivity {
    /**
     * 변수*/
    private int tab;
    /**
     * 안드로이드 객체 */
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private Toolbar toolbar;
    /**
     * 현재 사용자 정보 저장*/
    private userTable userData = new userTable();
    /**
     * 현재 사용자가 운영 (가입된) 동아리 목록*/
    private ArrayList<clubusersTable> userClubs = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_myboard);

        /**
         * loginActivity에서 넘어온 현재 사용자 학번을 저장한다. */
        Intent intent = getIntent();
        userData = (userTable)intent.getSerializableExtra("userData");
        userClubs = (ArrayList<clubusersTable>) intent.getSerializableExtra("userClubs");
        tab = intent.getIntExtra("tab", 0);

        /**
         * id 연결*/
        mViewPager = (ViewPager) findViewById(R.id.container);
        mTabLayout = (TabLayout) findViewById(R.id.tabs);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        /**
         * 탭뷰어 설정부 .. */
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        // viewPage 설정
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setCurrentItem(tab);
        // Tab 설정
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
        mTabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    /**
     * Toolbar 설정*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            /**
             * 상단 탭 선택시*/
            case R.id.action_settings :
                return true;
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 탭뷰터 설정*/
    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        /**
         * 생성자*/
        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        /**
         * 각 탭에 나올 Fragment 설정*/
        @Override
        public Fragment getItem(int position) {
            Bundle args = new Bundle();
            /**
             * 엑티비티로 이동할때 사용자의 학번정보도 넘겨준다.*/
            args.putSerializable("userData", userData);
            args.putSerializable("userClubs", userClubs);
            switch (position) {
                case 0:
                    Fragment Tab1 = new moreMyboardTab1Activity();
                    Tab1.setArguments(args);
                    return Tab1;
                case 1:
                    Fragment Tab2 = new moreMyboardTab2Activity();
                    Tab2.setArguments(args);
                    return Tab2;
            }
            return null;
        }

        /**
         * 탭 갯수*/
        @Override
        public int getCount() {
            return 2;
        }
    }
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), moreActivity.class);
        intent.putExtra("userData", userData);
        intent.putExtra("userClubs",userClubs);
        startActivity(intent);
        finish();
    }
}
