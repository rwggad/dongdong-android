package com.example.rwgga.dongdong.main;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.rwgga.dongdong.R;
import com.example.rwgga.dongdong.board.boardWriteActivity;
import com.example.rwgga.dongdong.main_more.moreActivity;
import com.example.rwgga.dongdong.database_table.userTable;
import com.example.rwgga.dongdong.php_server.getData;

import org.json.JSONArray;
import org.json.JSONObject;


/**
 *
 * 작성자 : Created by (rwggad@gmail.com - 김정환(KimJeongHwan)
 *
 * 메인 엑티비티이다. 탭뷰어로 이루어져있으며,
 *
 * 각 탭은 Fragment 로 이루어져있고 3개의 탭으로 이루어져있다. Tab1, Tab2, Tab3
 *
 * 현재 소스코드에서는 float button 을 설정하고, 탭부분을 설정해준다.
 */

public class MainActivity extends AppCompatActivity {
    /**
     * 변수*/
    private int tab;
    /**
     * 안드로이드 객체 */
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    // Float Btn 설정 변수
    private Boolean isFabOpen = false;
    private FloatingActionButton fab, fabSub1, fabSub2, fabSub3; // fab 설정
    private LinearLayout fabSub1_layout, fabSub2_layout, fabSub3_layout, fabFade;
    Animation showBtn, hideBtn, showLay, hideLay, fadeinLay, fadeoutLay;

    /**
     * 현재 사용자 정보 저장*/
    private userTable userData = new userTable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /**
         * loginActivity에서 넘어온 현재 사용자 학번을 저장한다. */
        Intent intent = getIntent();
        userData = (userTable)intent.getSerializableExtra("userData");
        tab = intent.getIntExtra("tab", 0);

        //Toast.makeText(getApplicationContext(),userData.getStudentName() + "(" + userData.getIsCaptain() + ")",Toast.LENGTH_SHORT).show();

        /**
         * id 연결*/
        mViewPager = (ViewPager) findViewById(R.id.container);
        mTabLayout = (TabLayout) findViewById(R.id.tabs);

        /**
         * 탭뷰어 설정부 .. */
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        // viewPage 설정
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setCurrentItem(tab);
        // Tab 설정
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
        mTabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        /**
         * FloatingActionButton 설정 */
        fab = (FloatingActionButton) findViewById(R.id.main_layout_fab);
        fabSub1 = (FloatingActionButton) findViewById(R.id.main_layout_fabSub1);
        fabSub2 = (FloatingActionButton) findViewById(R.id.main_layout_fabSub2);
        fabSub3 = (FloatingActionButton) findViewById(R.id.main_layout_fabSub3);
        fabSub1_layout = (LinearLayout) findViewById(R.id.main_layout_fabSub1_layout);
        fabSub2_layout = (LinearLayout) findViewById(R.id.main_layout_fabSub2_layout);
        fabSub3_layout = (LinearLayout) findViewById(R.id.main_layout_fabSub3_layout);
        fabFade = (LinearLayout) findViewById(R.id.main_layout_fading);

        // 애니메이션 설정
        showBtn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.show_button);
        hideBtn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.hide_button);
        showLay = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.show_layout);
        hideLay = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.hide_layout);
        fadeinLay = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fadein_layout);
        fadeoutLay = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fadeout_layout);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                anim();
            }
        });

        fabSub1.setOnClickListener(new View.OnClickListener() { // 게시물 개설
            @Override
            public void onClick(View v) {
                anim();
                if(userData.getIsCaptain() == 0){
                    /**
                     * 일반 학부생일 경우 */
                    Toast.makeText(getApplicationContext(), "동아리장만 사용할 수 있습니다!", Toast.LENGTH_SHORT).show();
                }else{
                    /**
                     * 홍보 게시물 작성*/
                    Intent intent = new Intent(getApplicationContext(), boardWriteActivity.class);
                    intent.putExtra("option","write");
                    intent.putExtra("userData",userData);
                    intent.putExtra("move","listView");
                    intent.putExtra("boardKind", -1);
                    startActivity(intent);
                    finish();
                }
            }
        });

        fabSub2.setOnClickListener(new View.OnClickListener() { // 동아리 개설
            @Override
            public void onClick(View v) {
                anim();
                if(userData.getIsCaptain() == 0){
                    /**
                     * 일반 학부생일 경우 */
                    Toast.makeText(getApplicationContext(), "동아리장만 사용할 수 있습니다!", Toast.LENGTH_SHORT).show();
                }else{
                    /**
                     * 동아리 개설 페이지 */
                    Intent intent = new Intent(getApplicationContext(), createClubKindActivity.class);
                    intent.putExtra("userData",userData);
                    startActivity(intent);
                    finish();
                }
            }
        });

        fabSub3.setOnClickListener(new View.OnClickListener() { // 더보기
            @Override
            public void onClick(View v) {
                anim();
                Intent intent = new Intent(getApplicationContext(), moreActivity.class);
                intent.putExtra("userData",userData);
                startActivity(intent);
                finish();
            }
        });
    }
    /**
     * fab 애니메이션 설정
     * startAnimation : 버튼 애니메이션 실행
     * setClickable : 버튼 활성화 비활성화
     * */
    public void anim(){
        if(isFabOpen){ // fab이 열려있을 때
            /**
             * 만약 fab 버튼이 열려있는 상태로 anim함수가 호출될때 ( 어떤 버튼이 클릭 될 때 )
             * sub1 sbu2 버튼을 닫고 버튼이 닫혀있다는 상태로 바꾸어줌 (isFabOpen = false)*/
            fabSub1_layout.setVisibility(View.GONE);
            fabSub2_layout.setVisibility(View.GONE);
            fabSub3_layout.setVisibility(View.GONE);
            fabFade.setVisibility(View.GONE);
            fabSub1_layout.startAnimation(hideLay);
            fabSub2_layout.startAnimation(hideLay);
            fabSub3_layout.startAnimation(hideLay);
            fabFade.startAnimation(fadeoutLay);
            fab.startAnimation(hideBtn);
            fabSub1.setClickable(false);
            fabSub2.setClickable(false);
            fabSub3.setClickable(false);
            isFabOpen = false;
        }else{ // fab이 닫혀있을 때
            fabSub1_layout.setVisibility(View.VISIBLE);
            fabSub2_layout.setVisibility(View.VISIBLE);
            fabSub3_layout.setVisibility(View.VISIBLE);
            fabFade.setVisibility(View.VISIBLE);
            fabSub1_layout.startAnimation(showLay);
            fabSub2_layout.startAnimation(showLay);
            fabSub3_layout.startAnimation(showLay);
            fabFade.startAnimation(fadeinLay);
            fab.startAnimation(showBtn);
            fabSub1.setClickable(true);
            fabSub2.setClickable(true);
            fabSub3.setClickable(true);
            isFabOpen = true;
        }
    }

    /**
     * 더보기 탭 설정 */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_club, menu);
        return true;
    }

    /**
     * Toolbar 설정*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /**
         * 상단 탭 선택시*/
        switch (item.getItemId()){
            case R.id.action_settings :
                return true;
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
            switch (position) {
                case 0:
                    Fragment Tab1 = new MainTab1Activity();
                    Tab1.setArguments(args);
                    return Tab1;
                case 1:
                    Fragment Tab2 = new MainTab2Activity();
                    Tab2.setArguments(args);
                    return Tab2;
                case 2:
                    Fragment Tab3 = new MainTab3Activity();
                    Tab3.setArguments(args);
                    return Tab3;
            }
            return null;
        }

        /**
         * 탭 갯수*/
        @Override
        public int getCount() {
            return 3;
        }
    }
    private final long FINISH_INTERVAL_TIME = 2000;
    private long backPressedTime = 0;
    /**
     * back키 두번으로 앱 종료 */
    @Override
    public void onBackPressed() {
        long tempTime = System.currentTimeMillis();
        long intervalTime = tempTime - backPressedTime;

        if (0 <= intervalTime && FINISH_INTERVAL_TIME >= intervalTime) {
            super.onBackPressed();
        } else {
            backPressedTime = tempTime;
            Toast.makeText(getApplicationContext(), "한번더 뒤로가기 버튼을 누르면 종료됩니다.", Toast.LENGTH_SHORT).show();
        }
    }
}
