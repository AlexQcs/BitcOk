package com.hc.wallcontrl.activity;

import android.animation.Animator;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hc.wallcontrl.R;
import com.hc.wallcontrl.com.activity.AppActivity;
import com.hc.wallcontrl.com.fragment.BaseFragment;
import com.hc.wallcontrl.fragment.ConnFragment;
import com.hc.wallcontrl.fragment.ControlFragment;
import com.hc.wallcontrl.fragment.InputControlFragment;
import com.hc.wallcontrl.fragment.MatrixFragment;
import com.hc.wallcontrl.fragment.WallSettingFragment;
import com.hc.wallcontrl.service.SocketService;
import com.hc.wallcontrl.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import io.codetail.animation.ViewAnimationUtils;
import yalantis.com.sidemenu.interfaces.Resourceble;
import yalantis.com.sidemenu.interfaces.ScreenShotable;
import yalantis.com.sidemenu.model.SlideMenuItem;
import yalantis.com.sidemenu.util.ViewAnimator;


public class MainActivity extends AppActivity implements ViewAnimator.ViewAnimatorListener {


    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private List<SlideMenuItem> mSlideMenuItems = new ArrayList<>();
    private ControlFragment mControlFragment;
    private ViewAnimator mViewAnimator;
    private LinearLayout mLinearLayout;
    private TextView mTvToolbarText;

    private int mPosition = 0;

    private int index_menuiterm = 1;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
        initEvent();
        setActionBar();
    }

    private void initEvent() {

    }

    void initView() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.setScrimColor(Color.TRANSPARENT);
        mLinearLayout = (LinearLayout) findViewById(R.id.left_drawer);
        mLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.closeDrawers();
            }
        });
    }

    void initData() {
        createMenuList();
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, SocketService.class);
        startService(intent);
    }

    private void createMenuList() {
        SlideMenuItem menuItemClose = new SlideMenuItem(BaseFragment.CLOSE, R.mipmap.item_close);
        mSlideMenuItems.add(menuItemClose);
        //主页
        SlideMenuItem menuItemMain = new SlideMenuItem(BaseFragment.MAIN, R.mipmap.item_main);
        mSlideMenuItems.add(menuItemMain);
        //遥控器
        SlideMenuItem menuItemWall = new SlideMenuItem(BaseFragment.SCREEN, R.mipmap.item_contoller);
        mSlideMenuItems.add(menuItemWall);
        //矩阵设置
        SlideMenuItem menuItemMatrix = new SlideMenuItem(BaseFragment.MATRIX, R.mipmap.item_matrix);
        mSlideMenuItems.add(menuItemMatrix);
        //屏幕设置
        SlideMenuItem menuItemScreen = new SlideMenuItem(BaseFragment.WALL, R.mipmap.item_screen);
        mSlideMenuItems.add(menuItemScreen);
        //连接
        SlideMenuItem menuItemConn = new SlideMenuItem(BaseFragment.CONN, R.mipmap.item_conn);
        mSlideMenuItems.add(menuItemConn);
    }


    private Handler mToastHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0x11:
                    String ip = msg.getData().getString("ip");
                    ToastUtil.showShortMessage(ip);
                    break;
            }
        }
    };

    //连接操作
//    private void SetConnect() {
//        if (!mPortString.equals("")) Port = Integer.parseInt(mPortString);
////        byte[] Buf = new byte[]{1, 9, 8, 7, 0, 3, 2, 1};
//
////        mThread = new Thread(mRunnable);
////        mThread.start();
//        Intent intent = new Intent();
//        intent.setAction(ConstUtils.ACTION_CONN);
//        intent.putExtra(ConstUtils.BROADCAST_IP, mIPString);
//        intent.putExtra(ConstUtils.BROADCAST_ISCONN, bConnected);
//        int socketPort = Integer.parseInt(mPortString);
//        intent.putExtra(ConstUtils.BROADCAST_PORT, socketPort);
//        this.sendBroadcast(intent);
//
//        String result = mIPString + ":" + mPortString;
//        Message msg = new Message();
//        msg.what = 0x11;
//        Bundle bundle = new Bundle();
//        bundle.putString("ip", result);
//        msg.setData(bundle);
//        mToastHandler.sendMessage(msg);
//    }

    @Override
    protected BaseFragment getFirstFragment() {
        mControlFragment = ControlFragment.newInstance();
        return mControlFragment;
    }

    private void setActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        mTvToolbarText = (TextView) findViewById(R.id.toolbar_title);
        mTvToolbarText.setText("主页");
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.mipmap.more);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        mDrawerToggle = new ActionBarDrawerToggle(this,
                mDrawerLayout,
                toolbar,
                R.string.drawer_open, /* "open drawer" description */
                R.string.drawer_close/* "close drawer" description */
        ) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                mLinearLayout.removeAllViews();
                mLinearLayout.invalidate();
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                if (slideOffset > 0.6 && mLinearLayout.getChildCount() == 0) {
                    mViewAnimator.showMenuContent();
                    mLinearLayout.getChildAt(index_menuiterm).setSelected(true);
                }
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mViewAnimator = new ViewAnimator<>(this, mSlideMenuItems, mControlFragment, mDrawerLayout, this);
    }

    @Override
    public void onPostCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onPostCreate(savedInstanceState, persistentState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {

            return true;
        }
        return super.onOptionsItemSelected(item);
//        switch (item.getItemId()) {
//            case R.id.action_settings:
//                return true;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
    }


    @Override
    public ScreenShotable onSwitch(Resourceble slideMenuItem, ScreenShotable screenShotable, int position) {
        mLinearLayout.getChildAt(index_menuiterm).setSelected(false);
        mPosition = position;
        switch (slideMenuItem.getName()) {
            case BaseFragment.CLOSE:
                return screenShotable;
            case BaseFragment.MAIN:
                index_menuiterm = 1;
                mTvToolbarText.setText("控制");
                return replaceFragment(ControlFragment.newInstance(), position);
            case BaseFragment.SCREEN:
                index_menuiterm = 2;
                mTvToolbarText.setText("虚拟按键");
                return replaceFragment(InputControlFragment.newInstance(), position);
            case BaseFragment.MATRIX:
                index_menuiterm = 3;
                mTvToolbarText.setText("矩阵设置");
                return replaceFragment(MatrixFragment.newInstance(), position);
            case BaseFragment.WALL:
                index_menuiterm = 4;
                mTvToolbarText.setText("幕墙设置");
                return replaceFragment(WallSettingFragment.newInstance(), position);
            case BaseFragment.CONN:
                index_menuiterm = 5;
                mTvToolbarText.setText("连接设置");
                return replaceFragment(ConnFragment.newInstance(), position);
            default:
                return replaceFragment(screenShotable, position);
//                return null;
        }
    }

    @Override
    public void disableHomeButton() {
        getSupportActionBar().setHomeButtonEnabled(false);
    }

    @Override
    public void enableHomeButton() {
        getSupportActionBar().setHomeButtonEnabled(true);
        mDrawerLayout.closeDrawers();
    }

    @Override
    public void addViewToContainer(View view) {
        mLinearLayout.addView(view);

    }


    private ScreenShotable replaceFragment(ScreenShotable screenShotable, int topOption) {
        mLinearLayout.getChildAt(index_menuiterm).setSelected(true);
        View view = findViewById(R.id.content_frame);
        int finalRadius = Math.max(view.getWidth(), view.getHeight());
        Animator animator = ViewAnimationUtils.createCircularReveal(view, 0, topOption, 0, finalRadius);
        animator.setInterpolator(new AccelerateInterpolator());
        animator.setDuration(ViewAnimator.CIRCULAR_REVEAL_ANIMATION_DURATION);
        findViewById(R.id.content_overlay).setBackgroundDrawable(new BitmapDrawable(getResources(), screenShotable.getBitmap()));
        animator.start();
//        ControlFragment controlFragment=ControlFragment.newInstance();
        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, (Fragment) screenShotable).commit();
        return screenShotable;
    }


    private long exitTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
//                index_menuiterm =1;
//
                if (index_menuiterm != 1) {
                    index_menuiterm = 1;
                    mTvToolbarText.setText("控制");
                    if (mPosition != 0) {
                        View view = findViewById(R.id.content_frame);
                        int finalRadius = Math.max(view.getWidth(), view.getHeight());
                        Animator animator = ViewAnimationUtils.createCircularReveal(view, 0, mPosition, 0, finalRadius);
                        animator.setInterpolator(new AccelerateInterpolator());
                        animator.setDuration(ViewAnimator.CIRCULAR_REVEAL_ANIMATION_DURATION);
                        findViewById(R.id.content_overlay).setBackgroundDrawable(new BitmapDrawable(getResources(), ControlFragment.newInstance().getBitmap()));
                        animator.start();
                    }

                    getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, ControlFragment.newInstance()).commit();
                } else {
                    Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                }
//                replaceFragment(ControlFragment.newInstance(), 1);
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


}
