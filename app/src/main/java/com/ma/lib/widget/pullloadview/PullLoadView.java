package com.ma.lib.widget.pullloadview;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import java.util.ArrayList;
import java.util.List;

public class PullLoadView extends SwipeRefreshLayout{
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

    public PullLoadView setPullLoadListener(PullLoadListener pullLoadListener) {
        this.pullLoadListener = pullLoadListener;
        return this;
    }

    public PullLoadView(Context context) {
        this(context, null);
    }

    public PullLoadView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        recycleView = new RecyclerView(context);
//        final LinearLayoutManager layoutManager = new LinearLayoutManager(context);
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
        recycleView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        addView(recycleView);
    }

    public void initView(PullLoadBaseAdapter adapter){
        this.adapter = adapter;
        this.adapter.setFooterClickListener(new PullLoadBaseAdapter.FooterClickListener() {
            @Override
            public void clickToLoad() {
                onEndReached(true);
            }
        });
        recycleView.setAdapter(adapter);
        setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
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
            setRefreshing(false);
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