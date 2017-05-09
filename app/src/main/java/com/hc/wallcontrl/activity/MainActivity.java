package com.hc.wallcontrl.activity;

import android.animation.Animator;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hc.wallcontrl.R;
import com.hc.wallcontrl.com.activity.AppActivity;
import com.hc.wallcontrl.com.fragment.BaseFragment;
import com.hc.wallcontrl.fragment.ConnFragment;
import com.hc.wallcontrl.fragment.ControlFragment;
import com.hc.wallcontrl.fragment.InputControlFragment;
import com.hc.wallcontrl.fragment.MatrixFragment;
import com.hc.wallcontrl.fragment.WallSettingFragment;
import com.hc.wallcontrl.service.SocketService;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
        setActionBar();

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
        Intent intent=new Intent();
        intent.setClass(MainActivity.this, SocketService.class);
        startService(intent);
    }

    private void createMenuList() {
        SlideMenuItem menuItemClose = new SlideMenuItem(BaseFragment.CLOSE, R.mipmap.item_close);
        mSlideMenuItems.add(menuItemClose);
        SlideMenuItem menuItemMain = new SlideMenuItem(BaseFragment.MAIN, R.mipmap.item_main);
        mSlideMenuItems.add(menuItemMain);
        SlideMenuItem menuItemConn = new SlideMenuItem(BaseFragment.CONN, R.mipmap.item_conn);
        mSlideMenuItems.add(menuItemConn);
        SlideMenuItem menuItemWall = new SlideMenuItem(BaseFragment.WALL, R.mipmap.item_wall);
        mSlideMenuItems.add(menuItemWall);
        SlideMenuItem menuItemMatrix = new SlideMenuItem(BaseFragment.MATRIX, R.mipmap.item_matrix);
        mSlideMenuItems.add(menuItemMatrix);
        SlideMenuItem menuItemScreen = new SlideMenuItem(BaseFragment.SCREEN, R.mipmap.item_screen);
        mSlideMenuItems.add(menuItemScreen);
    }

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
        switch (slideMenuItem.getName()) {
            case BaseFragment.CLOSE:
                return screenShotable;
            case BaseFragment.CONN:
                mTvToolbarText.setText("连接");
                return replaceFragment(ConnFragment.newInstance(), position);
            case BaseFragment.MAIN:
                mTvToolbarText.setText("控制");
                return replaceFragment(ControlFragment.newInstance(), position);
            case BaseFragment.WALL:
                mTvToolbarText.setText("幕墙设置");
                return replaceFragment(WallSettingFragment.newInstance(), position);
            case BaseFragment.MATRIX:
                mTvToolbarText.setText("矩阵");
                return replaceFragment(MatrixFragment.newInstance(), position);
            case BaseFragment.SCREEN:
                mTvToolbarText.setText("输入");
                return replaceFragment(InputControlFragment.newInstance(),position);
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
}
