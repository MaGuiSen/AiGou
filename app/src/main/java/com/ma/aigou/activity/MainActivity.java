package com.ma.aigou.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.ma.aigou.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity{
    @Bind(R.id.bottom_bar)
    BottomNavigationBar bottom_bar;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initFragment();
        initBottomTab();
    }

    Fragment currFragment = null;
    Fragment homeFragment = null;
    Fragment mineFragment = null;

    private void initFragment() {
        currFragment = new HomeFragment();
        this.getSupportFragmentManager().beginTransaction().add(R.id.frame_content, currFragment).commit();
    }


    public void initBottomTab() {
        bottom_bar.setActiveColor(R.color.design_snackbar_background_color);
        bottom_bar.setMode(BottomNavigationBar.MODE_FIXED);
        bottom_bar
                .addItem(new BottomNavigationItem(R.mipmap.ic_tab_mine_white_24dp, "首页"))
                .addItem(new BottomNavigationItem(R.mipmap.ic_tab_mine_white_24dp, "我的"))
                .initialise();
        bottom_bar.setTabSelectedListener(new BottomNavigationBar.OnTabSelectedListener(){
            @Override
            public void onTabSelected(int position) {
                switchFragment(position);
            }

            public void onTabUnselected(int position) {
            }

            @Override
            public void onTabReselected(int position) {

            }
        });

    }

    private void switchFragment(int position) {
        switch (position){
            case 0:
                switchContent(currFragment, getHomeFrm());
                break;
            case 1:
                switchContent(currFragment, getMineFrm());
                break;

        }
    }

    private void switchContent(Fragment from, Fragment to){
        if(currFragment != to){
            currFragment = to;
            FragmentTransaction transaction = this.getSupportFragmentManager().beginTransaction();
            if(!to.isAdded()){
                transaction.hide(from).add(R.id.frame_content, to).commit();
            }else{
                transaction.hide(from).show(to).commit();
            }
        }
    }

    private Fragment getHomeFrm() {
        if(homeFragment == null){
            homeFragment = new HomeFragment();
        }
        return homeFragment;
    }

    private Fragment getMineFrm(){
        if(mineFragment == null){
            mineFragment = new MineFragment();
        }
        return mineFragment;
    }
}
