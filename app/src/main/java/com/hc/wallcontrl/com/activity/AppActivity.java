package com.hc.wallcontrl.com.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MotionEvent;

import com.hc.wallcontrl.R;
import com.hc.wallcontrl.com.fragment.BaseFragment;

import java.util.ArrayList;

/**
 * Created by alex on 2017/4/8.
 */

public  abstract class AppActivity extends BaseActivity {
    //获取第一个fragment
    protected abstract BaseFragment getFirstFragment();
    //获取intent
    protected void handleIntent(Intent intent){

    }

    private ArrayList<MyOnTouchListener> mOnTouchListeners=new ArrayList<>(10);


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentViewId());
        if (null!=getIntent()){
            handleIntent(getIntent());
        }

        //避免重复添加fragment
        if(null==getSupportFragmentManager().getFragments()){
            BaseFragment firstFragment= getFirstFragment();
            if (null!=firstFragment){
                addFragment(firstFragment);
            }
        }
    }



    @Override
    protected int getContentViewId() {
        return R.layout.activity_main;
    }

    @Override
    protected int getFragmentContentId() {
        return R.id.content_frame;
    }

    public interface MyOnTouchListener{
         boolean onTouch(MotionEvent event);
    }

    public void registerMyOnTouchListener(MyOnTouchListener myOnTouchListener){
        mOnTouchListeners.add(myOnTouchListener);
    }

    public void unregisterMyOnTouchListener(MyOnTouchListener myOnTouchListener){
        mOnTouchListeners.remove(myOnTouchListener);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        for (MyOnTouchListener listener:mOnTouchListeners){
            listener.onTouch(ev);
        }
        return super.dispatchTouchEvent(ev);
    }
}
