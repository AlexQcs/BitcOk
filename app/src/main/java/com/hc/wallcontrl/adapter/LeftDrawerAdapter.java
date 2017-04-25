package com.hc.wallcontrl.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

/**
 * Created by alex on 2017/3/28.
 */

public class LeftDrawerAdapter  extends RecyclerView.Adapter{

    public static interface OnRecyclerViewListener{
        void onItemClick(int position);
    }

    private OnRecyclerViewListener mOnRecyclerViewListener;

    public void setOnRecyclerViewListener(OnRecyclerViewListener onRecyclerViewListener){
        this.mOnRecyclerViewListener=onRecyclerViewListener;
    }

    private static final String TAG=LeftDrawerAdapter.class.getSimpleName();

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
