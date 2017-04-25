package com.hc.wallcontrl.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

/**
 * Created by John on 2015-04-14.
 */
public class KeyboardActivity extends Activity implements View.OnTouchListener,GestureDetector.OnGestureListener {

    GestureDetector gd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.hc.wallcontrl.R.layout.activity_keyboard);

        gd = new GestureDetector(this,this);
        //为当前布局添加事件
        LinearLayout ll = (LinearLayout)findViewById(com.hc.wallcontrl.R.id.activity_keyboard);
        ll.setOnTouchListener(this);
        ll.setLongClickable(true);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        final int FLING_MIN_DISTANCE = 100, FLING_MIN_VELOCITY = 200;
        if(e1.getX() - e2.getX() > FLING_MIN_DISTANCE&&Math.abs(velocityX) > FLING_MIN_VELOCITY)
        {
            Intent intent = new Intent();
            intent.setClass(KeyboardActivity.this, MainActivity.class);
            startActivity(intent);
            Log.e("Fling","To Left");
            Toast.makeText(this, "向左手势", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }
}
