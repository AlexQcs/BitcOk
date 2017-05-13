package com.hc.wallcontrl.util;

import android.widget.Spinner;
import android.widget.SpinnerAdapter;

/**
 * Created by alex on 2017/5/11.
 */

public class ViewUtil {
    public static void setSpinnerItemSelectedByValue(Spinner spinner, String value){
        if (value.equals("null")||value.equals("")){
            spinner.setSelection(0,true);
            return;
        }
        SpinnerAdapter apsAdapter= spinner.getAdapter(); //得到SpinnerAdapter对象
        int k= apsAdapter.getCount();
        for(int i=0;i<k;i++){
            if(value.equals(apsAdapter.getItem(i).toString())){
                spinner.setSelection(i,true);// 默认选中项
                break;
            }
        }
    }
}
