package com.ma.lib.widget.banner;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Scroller;
import android.widget.Toast;

import com.ma.lib.utils.ImageLoad;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class BannerView extends FrameLayout {
    private Context context;
    private  boolean isAutoPlay = true;  //自动轮播启用开关
    private List<View> viewList = new ArrayList<>();
    private MyPagerAdapter myPagerAdapter;
    private ViewPager viewPager;
    private int currentIndex = 0; //当前轮播页
    Timer timer;
    private static int To_Next = 10;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(To_Next == msg.what) {
                viewPager.setCurrentItem(currentIndex);
            }
        }
    };

    public BannerView(Context context) {
        this(context, null);
    }
    public BannerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public BannerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        initUI();
        startPlay();
    }

    public void onPause(){
        stopPlay();
    }

    public  void onDestroy(){
        stopPlay();
    }

    public void onResume(){
        startPlay();
    }

    private void startPlay(){
        if(!isAutoPlay || timer != null){
            return;
        }
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                currentIndex = currentIndex + 1;
                handler.sendEmptyMessage(To_Next);
            }
        }, 3000, 5000);
    }

    private void stopPlay(){
        if(timer != null){
            timer.cancel();
            timer = null;
        }
    }

    private void initUI(){
        viewPager = new ViewPager(context);
        viewPager.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        viewPager.setFocusable(true);
        myPagerAdapter = new MyPagerAdapter();
        viewPager.setAdapter(myPagerAdapter);
        viewPager.addOnPageChangeListener(new MyPageChangeListener());

        try {
            Field field = ViewPager.class.getDeclaredField("mScroller");
            field.setAccessible(true);
            FixedSpeedScroller scroller = new FixedSpeedScroller(viewPager.getContext(), new LinearInterpolator());
            field.set(viewPager, scroller);
            scroller.setmDuration(300);
        } catch (Exception e) {
            e.printStackTrace();
        }

        myPagerAdapter.notifyDataSetChanged();
        currentIndex = 0;
        viewPager.setCurrentItem(currentIndex,false);
        viewPager.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "dddddddddddd", Toast.LENGTH_SHORT).show();
            }
        });
        addView(viewPager);
    }

    public void notifyDataChange(List<BannerData> dataList){
        stopPlay();
        if(dataList == null || dataList.size() ==0){
            return;
        }
        viewList.clear();
        for (int i = 0; i < dataList.size(); i++) {
            final BannerData dataItem = dataList.get(i);
            ImageView imageView =  new ImageView(context);
            LinearLayout.LayoutParams imgParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
            imageView.setLayoutParams(imgParams);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

            final int position = i;
            imageView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                if (onTickerClicklistener != null) {
                    onTickerClicklistener.onTickerClick(position, dataItem);
                }
                }
            });
            ImageLoad.load(dataItem.imgUrl, imageView);
            viewList.add(imageView);
        }
        myPagerAdapter.notifyDataSetChanged();
        currentIndex = viewList.size()*10; //
        viewPager.setCurrentItem(currentIndex,false);
        startPlay();
    }

    private class MyPagerAdapter  extends PagerAdapter {

        @Override
        public int getCount() {
           return viewList.size() <= 2 ? viewList.size() :200;
        }
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            //在数据更新的那一瞬间还会出现空白，所以下想办法在更新数据得时候轮播停止，
            if (viewList.size() != 2){
                //两条会出错，所以专门对不等于两条进行处理
                position %=viewList.size();
                if(position < 0){
                    position = viewList.size()+position;
                }
            }
            View view  = viewList.get(position);
            ViewParent vp = view.getParent();
            if(vp != null){
                ViewGroup parent = (ViewGroup)vp;
                parent.removeView(view);
            }
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
//            (container).removeView(viewList.get(position));这里不加这句,为了避免切换动画不能使用
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }
        @Override
        public void restoreState(Parcelable arg0, ClassLoader arg1) {
        }

        @Override
        public Parcelable saveState() {
            return null;
        }
    }

    /**
     * ViewPager的监听器
     * 当ViewPager中页面的状态发生改变时调用
     *
     */
    private class MyPageChangeListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrollStateChanged(int arg0) {
            switch (arg0) {
                case 1:// 手势滑动，空闲中
                    stopPlay();
                    break;
                case 2:// 界面切换中
                    stopPlay();
                    break;
                case 0:// 滑动结束，即切换完毕或者加载完毕
                    startPlay();
                    if(viewList.size()<=1){
                        return;
                    }
                    // 当前为最后一张，此时从右向左滑，则切换到第一张
                    if (viewPager.getCurrentItem() == viewPager.getAdapter().getCount() - 1) {
                        viewPager.setCurrentItem(0,false);
                    }
                    // 当前为第一张，此时从左向右滑，则切换到最后一张
                    else if (viewPager.getCurrentItem() == 0) {
                        viewPager.setCurrentItem(viewPager.getAdapter().getCount() - 1,false);
                    }
                    break;
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageSelected(int pos) {
            currentIndex = pos;
        }
    }

    //用于Viewpager切换时间控制
    public class FixedSpeedScroller extends Scroller {
        private int mDuration = 1500;

        public FixedSpeedScroller(Context context) {
            super(context);
        }

        public FixedSpeedScroller(Context context, Interpolator interpolator) {
            super(context, interpolator);
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy, int duration) {
            // Ignore received duration, use fixed one instead
            super.startScroll(startX, startY, dx, dy, mDuration);
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy) {
            // Ignore received duration, use fixed one instead
            super.startScroll(startX, startY, dx, dy, mDuration);
        }

        public void setmDuration(int time) {
            mDuration = time;
        }

        public int getmDuration() {
            return mDuration;
        }
    }

    private OnTickerClickListener onTickerClicklistener;

    public void setOnTickerClickListener(OnTickerClickListener onTickerClickListener) {
        this.onTickerClicklistener = onTickerClicklistener;
    }

    public interface OnTickerClickListener {
        void onTickerClick(int index, BannerData data);
    }

    public static class BannerData{
        public String imgUrl = "", action = "", value = "", title="";
    }
}
