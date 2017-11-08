package com.ma.lib.widget.pullloadview;

import android.content.Context;
import android.support.v4.util.SparseArrayCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ma.aigou.R;

import java.util.ArrayList;
import java.util.List;


public abstract class PullLoadBaseAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    static String STATUS_FAILURE = "failure";
    static String STATUS_LOADING = "loading";
    static String STATUS_SUCCESS = "success";

    private int TYPE_NORMAL = 100000;
    private int TYPE_FOOTER = 200000;
    private int TYPE_HEADER = 300000;
    private LayoutInflater layoutInflater = null;
    private View footLay = null;
    private RecyclerView recycleView = null;
    private String loadMoreStatus = "";
    private List<T> datas = new ArrayList<>();
    private SparseArrayCompat<View> headViews = new SparseArrayCompat<>();

    public void addHeadView(View headView){
        headViews.put(headViews.size() + TYPE_HEADER, headView);
    }

    public PullLoadBaseAdapter(Context context) {
        layoutInflater = LayoutInflater.from(context);
    }

    public int getItemCount() {
        int headCount = getHeadersCount();
        int footerCount = hasFooter() ? 1 : 0;
        return headCount + datas.size() + footerCount;
    }

    public int getHeadersCount() {
        return headViews.size();
    }

    private boolean hasFooter(){
        return datas.size()>0;
    }


    final public int getItemViewType(int position) {
        if(isHeadView(position)){
            return headViews.keyAt(position);
        }else if(isFooterView(position)){
            return TYPE_FOOTER;
        }
        return getMyItemViewType(getRealPosition(position));
    }

    private int getRealPosition(int position){
        return position - getHeadersCount();
    }

    public int getMyItemViewType(int position){
        return TYPE_NORMAL;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        // 用来在运行时指出对象是某一个对象
        if (isHeadView(position)){
            return;
        }
        if (isFooterView(position)){
            return;
        }
        bindData(holder, getRealPosition(position), datas);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (headViews.get(viewType) != null){
            return new HeadViewHolder(headViews.get(viewType));
        }else  if (viewType == TYPE_FOOTER) {
            View view = layoutInflater.inflate(R.layout.layout_footer, parent, false);
            footLay = view;
            footLay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                if(STATUS_FAILURE.equals(loadMoreStatus) && footerClickListener != null){
                    // 点击加载更多
                    footerClickListener.clickToLoad();
                }
                }
            });
            return new FooterViewHolder(view);
        } else {
            return getViewHolder(parent);
        }
    }

    private boolean isHeadView(int position) {
        return position < getHeadersCount();
    }

    private boolean isFooterView(int position) {
        return hasFooter() && position +1 == (getItemCount());
    }

    public void onAttachedToRecyclerView(RecyclerView recycleView) {
        try {
            if (this.recycleView == null || this.recycleView != recycleView) {
                this.recycleView = recycleView;
            }
            ifGridLayoutManager();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
        if(lp != null && lp instanceof StaggeredGridLayoutManager.LayoutParams) {
            StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) lp;
            p.setFullSpan(holder.getLayoutPosition() == 0);
        }
    }

    private void ifGridLayoutManager() {
        if (recycleView == null) return;
        final RecyclerView.LayoutManager layoutManager = recycleView.getLayoutManager();
        //在kotlin里，instanceof使用is代替
        if (layoutManager instanceof GridLayoutManager) {
            ((GridLayoutManager) layoutManager).setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                public int getSpanSize(int position) {
                 return isHeadView(position) || isFooterView(position) ? ((GridLayoutManager) layoutManager).getSpanCount() : 1;
                }
            });
        }
    }

    public View inflate(int resource, ViewGroup root, boolean attachToRoot){
        return layoutInflater.inflate(resource, root, attachToRoot);
    }

    public void notifyAllChange(List<T> datas, String loadMoreStatus, boolean canLoadMore) {
        this.datas = datas;
        this.loadMoreStatus = loadMoreStatus;
        notifyDataSetChanged();

        String footTxt = "";
        if (STATUS_LOADING.equals(loadMoreStatus)) {
            footTxt = "加载中";
        } else if (STATUS_FAILURE.equals(loadMoreStatus) && canLoadMore) {
            footTxt = "点击加载更多";
        } else {
            footTxt = canLoadMore ? "上拉加载更多" : "到底啦";
        }
        if (footLay == null)
            return;
        ((TextView) footLay.findViewById(R.id.title_show)).setText(footTxt);
        if (STATUS_LOADING.equals(loadMoreStatus)) {
            footLay.findViewById(R.id.progress).setVisibility(View.VISIBLE);
        } else {
            footLay.findViewById(R.id.progress).setVisibility(View.GONE);
        }
    }

    abstract public RecyclerView.ViewHolder getViewHolder(ViewGroup parent);

    abstract public void bindData(RecyclerView.ViewHolder holder, int position, List datas);

    private class FooterViewHolder<T> extends RecyclerView.ViewHolder {
        FooterViewHolder(View view) {
            super(view);
        }
    }

    private class HeadViewHolder<T> extends RecyclerView.ViewHolder {
        HeadViewHolder(View view) {
            super(view);
        }
    }

    private FooterClickListener footerClickListener;

    public PullLoadBaseAdapter setFooterClickListener(FooterClickListener footerClickListener) {
        this.footerClickListener = footerClickListener;
        return this;
    }

    interface FooterClickListener{
        void clickToLoad();
    }
}