package com.ma.aigou.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ma.aigou.R;
import com.ma.lib.widget.pullloadview.PullLoadBaseAdapter;

import java.util.List;


public class ListAdapter extends PullLoadBaseAdapter {

    public ListAdapter(Context context) {
        super(context);
    }

    public void bindData(RecyclerView.ViewHolder holder, int position, List datas){
        if (holder instanceof MyViewHolder){
            int data = (int) datas.get(position);
            ((MyViewHolder)holder).textView.setText("order" + data);
        }
    }

//    public int getMyItemViewType(int position){
//
//    }

    public RecyclerView.ViewHolder getViewHolder(ViewGroup parent) {
        View view = inflate(R.layout.layout_list_item, parent, false);
        return new MyViewHolder(view);
    }

    private class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textView = null;

        MyViewHolder(View view) {
            super(view);
            textView = (TextView) view.findViewById(R.id.title_show);
        }

    }
}