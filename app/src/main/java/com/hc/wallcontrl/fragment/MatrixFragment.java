package com.hc.wallcontrl.fragment;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.hc.wallcontrl.R;
import com.hc.wallcontrl.bean.ScreenBean;
import com.hc.wallcontrl.com.fragment.BaseFragment;
import com.hc.wallcontrl.util.ConstUtils;
import com.hc.wallcontrl.view.MatrixSettingDialog;
import com.hc.wallcontrl.view.MyTable;

import java.util.ArrayList;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

/**
 * A simple {@link Fragment} subclass.
 */
public class MatrixFragment extends BaseFragment implements View.OnTouchListener, View.OnClickListener {

    private Context mContext;
    private SharedPreferences sp = null;
    boolean bConnected = false;

    private Bitmap bitmap;

    private LinearLayout mLinPutLable;
    private Button mSetMatrixBtn;
    private MatrixSettingDialog mMatirxSetDialog;

    private MyTable mMyTable;
    private int[] SeltArea = new int[4];
    //行开始坐标 列开始坐标 行结束坐标 列结束坐标
    int Irs, Ics, Ire, Ice;
    private int Rs = 4;
    private int Cs = 4;

    private ArrayList<ScreenBean> mListScreenBeen;



    public static MatrixFragment newInstance(){
        return new MatrixFragment();
    }

    @Override
    public int getCreateViewLayoutId() {
        return R.layout.fragment_matrix;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext=getActivity();
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
        mSetMatrixBtn= (Button) inflateView.findViewById(R.id.btn_set_matrix);
        mSetMatrixBtn.setOnClickListener(this);
        setMyTable();
    }

    void setMyTable(){
        mLinPutLable.removeAllViews();
        GetRowColumns();

        mMyTable=new MyTable(mContext,mLinPutLable.getWidth(),mLinPutLable.getHeight());
        mMyTable.mySetRows(Rs);
        mMyTable.mySetCols(Cs);
        mMyTable.LoadTable();
        mLinPutLable.addView(mMyTable);

        mMyTable.setTableOnTouchListener(new MyTable.OnTableTouchListener() {
            @Override
            public void onTableTouchListener(View v, MotionEvent event) {
                SeltArea = mMyTable.myGetSeltArea();
                if (SeltArea[0] != 0) {
                    Log.e("SelectedArea: ", String.valueOf(SeltArea[0]) + ":" + String.valueOf(SeltArea[1]) + "===" + String.valueOf(SeltArea[2]) + ":" + String.valueOf(SeltArea[3]));
                }
                mListScreenBeen=mMyTable.getSelectItemIndex();
                for (ScreenBean screenBean : mListScreenBeen) {
                    Log.e("坐标:::",screenBean.getRow()+"-"+screenBean.getColumn());
                }
            }
        });

    }

    private byte[] GetRowColumns() {
        Irs = SeltArea[0];
        Ics = SeltArea[1];
        Ire = SeltArea[2];
        Ice = SeltArea[3];

        //Rs = spRsum.getSelectedItemPosition() + 1;
        // Cs = spCsum.getSelectedItemPosition() + 1;

        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("rows", Rs);
        editor.putInt("columns", Cs);
        editor.commit();

        return new byte[]{(byte) Irs, (byte) Ics, (byte) Ire, (byte) Ice};
    }



    @Override
    public boolean getShowsDialog() {
        return super.getShowsDialog();
    }


    @Override
    public void takeScreenShot() {
        View view=inflateView;
        Observable.just(view)
                .map(inflateView->Bitmap.createBitmap(inflateView.getWidth(),inflateView.getHeight(),Bitmap.Config.ARGB_8888))
                .map(bitmap -> new Canvas(bitmap))
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(canvas -> inflateView.draw(canvas));
    }

    @Override
    public Bitmap getBitmap() {
        return bitmap;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_set_matrix:

                break;
        }
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }
}
