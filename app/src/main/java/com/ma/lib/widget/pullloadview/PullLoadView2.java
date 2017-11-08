package com.ma.lib.widget.pullloadview;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ma.aigou.R;
import com.ma.lib.utils.DensityUtil;

import java.util.ArrayList;
import java.util.List;

import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.PtrUIHandler;
import in.srain.cube.views.ptr.indicator.PtrIndicator;

public class PullLoadView2 extends PtrFrameLayout {
    int pageIndex = 1;
    int pageSize = 20;
    String loadMoreStatus = "";//加载更多的状态 loading success failure
    String refreshStatus = "";//刷新的状态 loading success failure
    boolean canLoadMore = false;

    PullLoadListener pullLoadListener = null;
    List dataListAll =  new ArrayList();
    PullLoadBaseAdapter adapter = null;
    Context context = null;
    public RecyclerView recycleView = null;

    public PullLoadView2 setPullLoadListener(PullLoadListener pullLoadListener) {
        this.pullLoadListener = pullLoadListener;
        return this;
    }

    public PullLoadView2(Context context) {
        this(context, null);
    }

    public PullLoadView2(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initSelf();
        initHeadLay();
        initRecycleView();
    }

    private void initSelf() {
        //说明：默认头部是一个屏幕的高度（大概）
        //设置阻力
        setResistance(1.4f);

        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int headStateHeight = layHeadState.getHeight();
                //设置头部停留比例
                setOffsetToKeepHeaderWhileLoading(headStateHeight);
                int refreshOffset = DensityUtil.dip2px(context, 20) + headStateHeight;
                setRatioOfHeaderHeightToRefresh(refreshOffset*1f/getHeaderHeight());
            }
        });
        //设置关闭时间
        setDurationToClose(200);
        setDurationToCloseHeader(800);
        //拖动过程中，当到达一定距离自动刷新（我们不需要，而是放开刷新）
        setPullToRefresh(false);
        // 当刷新过程中，保持头部
        setKeepHeaderWhenRefresh(true);
    }

    private void initRecycleView() {
        recycleView = new RecyclerView(context);
        //final LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        final GridLayoutManager layoutManager = new GridLayoutManager(context, 2);
        recycleView.setLayoutManager(layoutManager);
        recycleView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(newState == RecyclerView.SCROLL_STATE_IDLE){
                    int lastVisiblePosition = layoutManager.findLastVisibleItemPosition();
                    if(lastVisiblePosition >= layoutManager.getItemCount() - 2){
                        onEndReached(false);
                    }
                }
            }
        });
        addView(recycleView);
    }

    private void initHeadLay(){
        //组建头部
        headLay = View.inflate(context, R.layout.layout_header_refresh, null);
        headStateTxt = (TextView) headLay.findViewById(R.id.txt_header_state);
        layHeadState = headLay.findViewById(R.id.lay_state);
        addView(headLay);
    }

    View headLay = null;
    TextView headStateTxt = null;
    View layHeadState = null;
    ImageView imgRefreshBg = null;
    public void initView(PullLoadBaseAdapter adapter){
        this.adapter = adapter;
        this.adapter.setFooterClickListener(new PullLoadBaseAdapter.FooterClickListener() {
            @Override
            public void clickToLoad() {
                onEndReached(true);
            }
        });
        recycleView.setAdapter(adapter);

        //下拉刷新
        addPtrUIHandler(new PtrUIHandler() {
            @Override
            public void onUIReset(PtrFrameLayout frame) {
                headStateTxt.setText("下拉刷新");
            }

            @Override
            public void onUIRefreshPrepare(PtrFrameLayout frame) {
                headStateTxt.setText("下拉刷新");
            }

            @Override
            public void onUIRefreshBegin(PtrFrameLayout frame) {
                headStateTxt.setText("刷新中");
            }

            @Override
            public void onUIRefreshComplete(PtrFrameLayout frame) {
                headStateTxt.setText("刷新完成");
            }

            @Override
            public void onUIPositionChange(PtrFrameLayout frame, boolean isUnderTouch, byte status, PtrIndicator ptrIndicator) {
//                headStateTxt.setText("onUIPositionChange");
                final int mOffsetToRefresh = frame.getOffsetToRefresh();
                final int currentPos = ptrIndicator.getCurrentPosY();
                final int lastPos = ptrIndicator.getLastPosY();

                if (currentPos < mOffsetToRefresh && lastPos >= mOffsetToRefresh) {
                    if (isUnderTouch && status == PtrFrameLayout.PTR_STATUS_PREPARE) {
                        //                 crossRotateLineFromBottomUnderTouch(frame);
                        //                 if (mRotateView != null) {
                        //                 mRotateView.clearAnimation();
                        //                 mRotateView.startAnimation(mReverseFlipAnimation);
                        //                 }
                        headStateTxt.setText("下拉刷新");
                    }
                } else if (currentPos > mOffsetToRefresh && lastPos <= mOffsetToRefresh) {
                    if (isUnderTouch && status == PtrFrameLayout.PTR_STATUS_PREPARE) {
                        //                 crossRotateLineFromTopUnderTouch(frame);
                        //                 if (mRotateView != null) {
                        //                 mRotateView.clearAnimation();
                        //                 mRotateView.startAnimation(mFlipAnimation);
                        //                 }
                        headStateTxt.setText("释放刷新");
                    }
                }
            }
        });
        setPtrHandler(new PtrHandler()
        {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header)
            {
//                return true;
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame)
            {
                onLoadRefresh();
            }
        });
    }

    public void onLoadRefresh(){
        if(pullLoadListener == null)
            return;
        if(!(PullLoadBaseAdapter.STATUS_LOADING.equals(refreshStatus))){
            pageIndex = 1;
            pullLoadListener.loadDataFun(false, pageIndex, pageSize);
            refreshStatus = PullLoadBaseAdapter.STATUS_LOADING;
        }
    }


    public void onEndReached(boolean isFailToReLoad){
        if(pullLoadListener == null)
            return;
        //isFailToReLoad 加载失败之后重新加载
        if(canLoadMore && !PullLoadBaseAdapter.STATUS_LOADING.equals(loadMoreStatus)){
            pageIndex += (isFailToReLoad ? 0 : 1);
            pullLoadListener.loadDataFun(true, pageIndex, pageSize);
            loadMoreStatus = PullLoadBaseAdapter.STATUS_LOADING;
            //TODO..
            adapter.notifyAllChange(dataListAll, loadMoreStatus, canLoadMore);
        }
    }

    public void endLoad(boolean isSuccess, boolean isLoadMore, List dataList){
        if(!isSuccess){
            if(isLoadMore){
                loadMoreStatus = PullLoadBaseAdapter.STATUS_FAILURE;
            }else{
                refreshStatus = PullLoadBaseAdapter.STATUS_FAILURE;
            }
            adapter.notifyAllChange(dataListAll, loadMoreStatus, canLoadMore);
            return;
        }
        canLoadMore = dataList.size() >= pageSize;

        dataListAll = loadData(dataListAll, dataList, isLoadMore);

        if(isLoadMore){
            loadMoreStatus = PullLoadBaseAdapter.STATUS_SUCCESS;
        }else{
            refreshStatus = PullLoadBaseAdapter.STATUS_SUCCESS;
            refreshComplete();
        }
        adapter.notifyAllChange(dataListAll, loadMoreStatus, canLoadMore);
    }

    public List loadData(List sourceList, List newList, boolean isLoadMore){
        if(!isLoadMore){
            sourceList.clear();
        }
        sourceList.addAll(newList);
        return sourceList;
    }

    public interface PullLoadListener {
        void loadDataFun(boolean isLoadMore, int pageIndex, int pageSize);
    }
}