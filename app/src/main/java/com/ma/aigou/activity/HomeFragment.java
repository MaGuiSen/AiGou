package com.ma.aigou.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ma.aigou.R;
import com.ma.aigou.adapter.ListAdapter;
import com.ma.lib.widget.pullloadview.PullLoadView2;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;

public class HomeFragment extends BaseFragment{
    View currView = null;
    @Bind(R.id.refreshView)
    PullLoadView2 pullLoadView;
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        currView = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, currView);
        initView();
        return currView;
    }

    boolean isSuccess = false;

    public void initView(){
        ListAdapter adapter = new ListAdapter(getContext());
        adapter.addHeadView(View.inflate(getActivity(),R.layout.layout_header, null));
        adapter.addHeadView(View.inflate(getActivity(),R.layout.layout_header2, null));
        pullLoadView.initView(adapter);
        pullLoadView.setPullLoadListener(new PullLoadView2.PullLoadListener() {
            @Override
            public void loadDataFun(final boolean isLoadMore, final int pageIndex, int pageSize) {
                final Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        // 更新列表
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                int code = 200;
                                List dataList = new ArrayList();
                                if (pageIndex == 4){
                                    for(int i=0;i<10;i++){
                                        dataList.add(i);
                                    }
                                }else{
                                    for(int i=0;i<20;i++){
                                        dataList.add(i);
                                    }
                                }
                                if(pageIndex == 2){
                                    if(!isSuccess){
                                        code = 100;
                                        isSuccess = true;
                                    }
                                }
                                pullLoadView.endLoad(code == 200, isLoadMore, dataList);
                            }
                        });
                        timer.cancel();
                    }
                }, 3000);
            }
        });
    }
}