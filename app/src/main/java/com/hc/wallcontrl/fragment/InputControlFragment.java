package com.hc.wallcontrl.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.hc.wallcontrl.R;
import com.hc.wallcontrl.bean.ScreenBean;
import com.hc.wallcontrl.com.fragment.BaseFragment;
import com.hc.wallcontrl.util.ConstUtils;
import com.hc.wallcontrl.view.MyTable;

import java.util.ArrayList;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;

/**
 * Created by alex on 2017/5/3.
 */

public class InputControlFragment extends BaseFragment implements View.OnTouchListener, View.OnClickListener {

    private Context mContext;
    private SharedPreferences sp = null;
    boolean bConnected = false;

    private LinearLayout mLinPutLable;
    private Button mSetMatrixBtn;

    private Bitmap bitmap;

    private MyTable mMyTable;
    private int[] SeltArea = new int[4];
    //行开始坐标 列开始坐标 行结束坐标 列结束坐标
    int Irs, Ics, Ire, Ice;
    private int Rs = 4;
    private int Cs = 4;

    private ArrayList<ScreenBean> mListScreenBeen;

    @Override
    public int getCreateViewLayoutId() {
        return R.layout.fragmrnt_inputcontrol;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext=context;
    }

    @Override
    public void initData() {
        mListScreenBeen=new ArrayList<>();
        sp = mContext.getSharedPreferences(ConstUtils.SHAREDPREFERENCES, Context.MODE_PRIVATE);
        if (sp==null)return;
        Rs = sp.getInt("rows", Rs);
        Cs = sp.getInt("columns", Cs);
        bConnected = sp.getBoolean("isConn", bConnected);
    }

    @Override
    public void initView(View inflateView, Bundle savedInstanceState) {
        mLinPutLable= (LinearLayout) inflateView.findViewById(R.id.lin_table);

    }

    @Override
    public void takeScreenShot() {
        View mView=inflateView;

        Observable.just(mView)
                .map(new Func1<View, Canvas>() {
                    @Override
                    public Canvas call(View view) {
                        Bitmap bitmap=Bitmap.createBitmap(view.getWidth(),view.getHeight(),Bitmap.Config.ARGB_8888);
                        Canvas canvas=new Canvas(bitmap);
                        return canvas;
                    }
                }).subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Canvas>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Canvas canvas) {
                        mView.draw(canvas);
                    }
                });
    }

    @Override
    public Bitmap getBitmap() {
        return bitmap;
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }
}
