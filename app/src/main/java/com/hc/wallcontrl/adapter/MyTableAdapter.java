package com.hc.wallcontrl.adapter;


import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by John on 2015-07-20.
 */
public class MyTableAdapter extends BaseAdapter {

    private Context myContext;
    private LayoutInflater myInflater;
    ArrayList<String> myTableSource;
    int[] myTableColors;
    LinearLayout.LayoutParams params;

    public MyTableAdapter(Context context,ArrayList<String> myTableSrc,int [] myTableClor){
        myContext = context;
        myInflater = LayoutInflater.from(context);
        myTableSource = myTableSrc;
        myTableColors = myTableClor;

        params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
    }

    @Override
    public int getCount() {
        return myTableSource.size();
    }

    @Override
    public Object getItem(int position) {
        return myTableSource.get(position);
    }

    public long getItemId(int position){
        return position;
    }

    public View getView(int position,View convertView,ViewGroup parent){

        ///*
        ItemViewTag viewTag;
        if(convertView == null){
            convertView = myInflater.inflate(com.hc.wallcontrl.R.layout.item,null);
            viewTag = new ItemViewTag((TextView)convertView.findViewById(com.hc.wallcontrl.R.id.ItemText));//,myTableColors.get(position)
            convertView.setTag(viewTag);
        }
        else {
            viewTag = (ItemViewTag)convertView.getTag();
        }
        //Set Value
        viewTag.myTextVeiw.setText(myTableSource.get(position));
        viewTag.myTextVeiw.setBackgroundColor(myTableColors[position]);

        //*/
        return convertView;
    }

    private class ItemViewTag{
        protected TextView myTextVeiw;
        //protected Color myColor;,Color mClor
        public ItemViewTag(TextView mTV)
        {
            this.myTextVeiw = mTV;
            //this.myColor = mClor;
        }
    }

}
