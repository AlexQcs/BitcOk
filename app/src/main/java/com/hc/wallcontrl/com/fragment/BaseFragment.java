package com.hc.wallcontrl.com.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.hc.wallcontrl.com.activity.BaseActivity;
import com.hc.wallcontrl.util.ToastUtil;

import yalantis.com.sidemenu.interfaces.ScreenShotable;


/**
 * 基础的Fragment，该类简化了原始类的生命周期，并增加了一些常用方法，强烈建议Fragment继承该类
 * Created by alex on 2017/4/7.
 */

public abstract class BaseFragment extends BaseLogFragment implements IBaseFragment,ScreenShotable {

    public static final String CLOSE="Close";
    public static final String MAIN = "Main";
    public static final String MATRIX = "Matrix";
    public static final String CONN = "Conn";
    public static final String SCREEN = "Screen";
    public static final String WALL = "Wall";
    public static final String INPUT="Input";
    /**
     * Fragment Content view
     */
    public View inflateView;

    /**
     * 所属Activity
     */
    private BaseActivity activity;

    /**
     * 记录是否已经创建了,防止重复创建
     */
    private boolean viewCreated;


    /**
     * 显示Toast消息
     *
     * @param msg 消息文本
     */
    public final void showToast(@NonNull String msg) {
        ToastUtil.showShortMessage(msg);
    }

    /**
     * 通过ID查找控件
     *
     * @param viewId 控件资源ID
     * @param <VIEW> 泛型参数，查找得到的View
     * @return 找到了返回控件对象，否则返回null
     */
    final public <VIEW extends View> VIEW findViewById(@IdRes int viewId) {
        return (VIEW) inflateView.findViewById(viewId);
    }

//    public final <EVENT extends BaseE>

    @CallSuper
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        d("onAttach... activity = " + activity.toString());

    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (BaseActivity) activity;
    }

    @Override
    final public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 防止重复调用onCreate方法，造成在initData方法中adapter重复初始化问题
        if (!viewCreated) {
            viewCreated = true;
            initData();
        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        d("onCreateView...");
        if (null == inflateView) {
            //强制竖屏提示
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            int layoutResId = getCreateViewLayoutId();
            if (layoutResId > 0)
                inflateView = inflater.inflate(getCreateViewLayoutId(), container, false);
            // 解决点击穿透问题
            inflateView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return true;
                }
            });
        }

        return inflateView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        d("onViewCreated.....");
        if (viewCreated) {
            findView(view, savedInstanceState);
            initView(view, savedInstanceState);
            initDialog();
            initListener();
            viewCreated = false;
        }
    }

    @CallSuper
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        d("onCreateDialog...");
        return super.onCreateDialog(savedInstanceState);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        d("onDismiss...");
    }

    //添加fragment
    protected void addFragment(BaseFragment fragment) {
        if (null != fragment) {
            activity.addFragment(fragment);
        }
    }

    //移除fragment
    protected void removeFragment() {
        activity.removeFragment();
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        d("onCancel...");
    }

    @Override
    public void dismiss() {
        // 捕获异常不至于造成崩溃
        try {
            super.dismiss();
        } catch (Exception e) {
            d("---" + e.getMessage());
        }
    }


    @CallSuper
    @Override
    public void dismissAllowingStateLoss() {
        // 捕获异常不至于造成崩溃
        try {
            super.dismissAllowingStateLoss();
        } catch (Exception e) {
            d("---" + e.getMessage());
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        d("onStart...");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        d("onDestroyView...");
        // 解决ViewPager中的问题
        if (null != inflateView) {
            ((ViewGroup) inflateView.getParent()).removeView(inflateView);
        }
    }

    @CallSuper
    @Override
    public void onPause() {
        super.onPause();
        d("onPause...");
    }

    @CallSuper
    @Override
    public void onStop() {
        super.onStop();
        d("onStop...");
    }

    @CallSuper
    @Override
    public void onDetach() {
        super.onDetach();
        d("onDetach...");
    }

    @CallSuper
    @Override
    public void onResume() {
        super.onResume();
        d("onResume...");
    }

    @CallSuper
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        d("onLowMemory...");
    }


    @Override
    public void initData() {
        d("initData...");
    }

    @Override
    public void findView(View inflateView, Bundle savedInstanceState) {
        d("findView...");
    }

    @Override
    public void initView(View inflateView, Bundle savedInstanceState) {
        d("initView...");
    }

    @Override
    public void initListener() {
        d("initListener...");
    }

    @Override
    public void initDialog() {
        d("initDialog...");
    }

    @Override
    protected String getLogTagName() {
        return null;
    }
}
