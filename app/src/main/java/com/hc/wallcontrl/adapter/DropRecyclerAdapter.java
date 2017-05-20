package com.hc.wallcontrl.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hc.wallcontrl.R;

import java.util.List;

/**
 * Created by alex on 2017/5/18.
 */

public class DropRecyclerAdapter extends RecyclerView.Adapter<DropRecyclerAdapter.MyViewHolder> implements View.OnClickListener {

    private Context mContext;
    private List<String> mData;
    private OnRecyclerViewItemClickListenner mOnRecyclerViewItemClickListenner = null;

    public static interface OnRecyclerViewItemClickListenner {
        void onItemClick(View view,int position);
    }

    public void setOnListViewItemClickListener(OnRecyclerViewItemClickListenner onRecyclerViewItemClickListenner) {
        mOnRecyclerViewItemClickListenner = onRecyclerViewItemClickListenner;
    }

    public DropRecyclerAdapter(Context context, List<String> data) {
        mContext = context;
        mData = data;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_droptext, parent,
                false);//这个布局就是一个imageview用来显示图片
        MyViewHolder holder = new MyViewHolder(view);

        //给布局设置点击和长点击监听
        view.setOnClickListener(this);

        return holder;

    }



    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.itemView.setTag(position);
        holder.mTextView.setText(mData.get(position));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public List<String> getData() {
        return mData;
    }

    public void setData(List<String> list){
        mData=list;
    }


    @Override
    public void onClick(View v) {
        if (mOnRecyclerViewItemClickListenner != null) {
            mOnRecyclerViewItemClickListenner.onItemClick(v, (int) v.getTag());
        }
    }

    //自定义ViewHolder，用于加载图片
    class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView mTextView;

        public MyViewHolder(View view) {
            super(view);
            mTextView = (TextView) view.findViewById(R.id.ItemText);
        }
    }
}
