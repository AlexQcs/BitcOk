package com.hc.wallcontrl.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hc.wallcontrl.R;

import java.util.List;

/**
 * Created by alex on 2017/5/17.
 */

public class EditorAdapter extends BaseAdapter {

    private Context mContext;
    private List<String> mData;
    private LayoutInflater mInflater;

    public EditorAdapter(Context context, List<String> data) {
        mContext = context;
        mData = data;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_droptext, null);
            holder = new ViewHolder();
            holder.mTextView = (TextView) convertView.findViewById(R.id.ItemText);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.mTextView.setText(mData.get(position));
        return convertView;
    }




    static class ViewHolder {
        TextView mTextView;
    }
}
