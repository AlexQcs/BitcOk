package com.hc.wallcontrl.fragment;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;

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


    //新建fragment实例
    public static MatrixFragment newInstance(){
        return new MatrixFragment();
    }

    //绑定fragment布局
    @Override
    public int getCreateViewLayoutId() {
        return R.layout.fragment_matrix;
    }

    //在onAttach阶段获取上下文对象
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext=getActivity();
    }

    //初始化数据
    @Override
    public void initData() {
        mListScreenBeen=new ArrayList<>();
        sp = mContext.getSharedPreferences(ConstUtils.SHAREDPREFERENCES, Context.MODE_PRIVATE);
        if (sp==null)return;
        Rs = sp.getInt("rows", Rs);
        Cs = sp.getInt("columns", Cs);
        bConnected = sp.getBoolean("isConn", bConnected);
    }

    //初始化控件
    @Override
    public void initView(View inflateView, Bundle savedInstanceState) {
        mLinPutLable= (LinearLayout) inflateView.findViewById(R.id.lin_table);
        mSetMatrixBtn= (Button) inflateView.findViewById(R.id.btn_set_matrix);
        mSetMatrixBtn.setOnClickListener(this);
        setMatirxSetDialog();
        setMyTable();
    }

    //矩阵设置Dialog
    void setMatirxSetDialog(){

        mMatirxSetDialog=new MatrixSettingDialog(mContext,R.style.CustomDatePickerDialog);
        Window window=mMatirxSetDialog.getWindow();
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(R.style.MatrixDialogStyle);
        WindowManager.LayoutParams lp=window.getAttributes();
        lp.x=0;
        lp.y=-20;
        lp.width=  WindowManager.LayoutParams.MATCH_PARENT;
        lp.alpha=9f;
        window.setAttributes(lp);
        mMatirxSetDialog.setOnCancelClickListener(new MatrixSettingDialog.onCancelClickListener() {
            @Override
            public void onClick() {
                mMatirxSetDialog.dismiss();
            }
        });
        mMatirxSetDialog.setOnConfirmClickListener(new MatrixSettingDialog.onConfirmClickListener() {
            @Override
            public void onClick() {
                Log.e("mMatirxSetDialog",mMatirxSetDialog.getMatrixCategory());
                Log.e("mMatirxSetDialog",mMatirxSetDialog.getMatrixFactory());
                Log.e("mMatirxSetDialog",mMatirxSetDialog.getIntputQuan()+"");
                Log.e("mMatirxSetDialog",mMatirxSetDialog.getDelayTime()+"");
                setMatrixInfo(mListScreenBeen,mMatirxSetDialog);
                mMatirxSetDialog.dismiss();
            }
        });

    }

    //设置屏幕矩阵信息
    void setMatrixInfo(ArrayList<ScreenBean> list,MatrixSettingDialog dialog){
        for (int i = 0; i < list.size(); i++) {
            list.get(i).setMatrixCategory(dialog.getMatrixCategory());
            list.get(i).setMatrixFactory(dialog.getMatrixFactory());
            list.get(i).setInputQuan(dialog.getIntputQuan());
            list.get(i).setDelaytime(dialog.getDelayTime());
        }
    }

    //这是屏幕表格
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

    //获取行列数
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

    //控件点击事件
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_set_matrix:
                if (mMatirxSetDialog!=null){
                    mMatirxSetDialog.show();
                }
                break;
        }
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }
}
