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
import android.widget.TextView;

import com.hc.wallcontrl.R;
import com.hc.wallcontrl.bean.ScreenMatrixBean;
import com.hc.wallcontrl.com.fragment.BaseFragment;
import com.hc.wallcontrl.util.ConstUtils;
import com.hc.wallcontrl.util.LogUtil;
import com.hc.wallcontrl.util.PrefrenceUtils;
import com.hc.wallcontrl.view.MatrixSettingDialog;
import com.hc.wallcontrl.view.MyTable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
    private TextView mMatrixCategoryTv;
    private TextView mMatrixFactoryTv;
    private TextView mMatrixInputNameTv;
    private TextView mMatrixStreamTv;
    private TextView mInputQuantityTv;
    private TextView mAddrTv;
    private TextView mDelayTimeTv;


    private MyTable mMyTable;
    private int[] SeltArea = new int[4];
    //行开始坐标 列开始坐标 行结束坐标 列结束坐标
    int Irs, Ics, Ire, Ice;
    private int Rs = 4;
    private int Cs = 4;

    private ArrayList<ScreenMatrixBean> mListScreenBeen;
    private List<ScreenMatrixBean> mListScreenApp;

    //新建fragment实例
    public static MatrixFragment newInstance() {
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
        mContext = getActivity();
    }

    //初始化数据
    @Override
    public void initData() {
        mListScreenBeen = new ArrayList<>();
        sp = mContext.getSharedPreferences(ConstUtils.SHAREDPREFERENCES, Context.MODE_PRIVATE);

        if (sp == null) return;
        Rs = sp.getInt(ConstUtils.SP_ROWS, Rs);
        Cs = sp.getInt(ConstUtils.SP_COLUMNS, Cs);
        bConnected = sp.getBoolean(ConstUtils.SP_ISCONN, bConnected);
        String listBeanStr=sp.getString(ConstUtils.SP_SCREEN_MATRIX_LIST,"");
        try {
         mListScreenApp= PrefrenceUtils.String2SceneList(listBeanStr);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    //初始化控件
    @Override
    public void initView(View inflateView, Bundle savedInstanceState) {
        mLinPutLable = (LinearLayout) inflateView.findViewById(R.id.lin_table);
        mSetMatrixBtn = (Button) inflateView.findViewById(R.id.btn_set_matrix);
        mAddrTv = (TextView) inflateView.findViewById(R.id.tv_addr);
        mDelayTimeTv = (TextView) inflateView.findViewById(R.id.tv_delaytime);
        mInputQuantityTv = (TextView) inflateView.findViewById(R.id.tv_input_quantity);
        mMatrixCategoryTv = (TextView) inflateView.findViewById(R.id.tv_matirx_category);
        mMatrixFactoryTv = (TextView) inflateView.findViewById(R.id.tv_matirx_factory);
        mMatrixStreamTv= (TextView) inflateView.findViewById(R.id.tv_matrix_stream);
        mMatrixInputNameTv= (TextView) inflateView.findViewById(R.id.tv_matirx_inputname);
        mSetMatrixBtn.setOnClickListener(this);
        setMatirxSetDialog();
        setMyTable();
    }

    //矩阵设置Dialog
    void setMatirxSetDialog() {

        mMatirxSetDialog = new MatrixSettingDialog(mContext, R.style.CustomDatePickerDialog);
        Window window = mMatirxSetDialog.getWindow();
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(R.style.MatrixDialogStyle);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.x = 0;
        lp.y = -20;
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.alpha = 9f;
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
                Log.e("mMatirxSetDialog:矩阵厂家", mMatirxSetDialog.getMatrixCategory());
                Log.e("mMatirxSetDialog:矩阵类型", mMatirxSetDialog.getMatrixFactory());
                Log.e("mMatirxSetDialog:输出通道", mMatirxSetDialog.getMatrixStream());
                Log.e("mMatirxSetDialog:输入总数", mMatirxSetDialog.getIntputQuan() + "");
                Log.e("mMatirxSetDialog:命令延时", mMatirxSetDialog.getDelayTime() + "");
                Log.e("mMatirxSetDialog:设备地址", mMatirxSetDialog.getAddr() + "");
                setMatrixInfo(mListScreenBeen,mListScreenApp, mMatirxSetDialog);
                mMatirxSetDialog.dismiss();
            }
        });

    }

    //设置屏幕矩阵信息
    void setMatrixInfo(List<ScreenMatrixBean> resList, List<ScreenMatrixBean> targetList, MatrixSettingDialog dialog) {
        for (ScreenMatrixBean screenBean : resList) {
            for (int i=0;i<targetList.size();i++) {
                if (screenBean.getRow()==targetList.get(i).getRow()&&screenBean.getColumn()==targetList.get(i).getColumn()){
                    targetList.get(i).setMatrixCategory(dialog.getMatrixCategory());
                    targetList.get(i).setMatrixFactory(dialog.getMatrixFactory());
                    targetList.get(i).setInputQuan(dialog.getIntputQuan());
                    targetList.get(i).setDelaytime(dialog.getDelayTime());
                    targetList.get(i).setAddr(dialog.getAddr());
                    targetList.get(i).setMatrixInputName(dialog.getMatrixInputName());
                    targetList.get(i).setMatrixStream(dialog.getMatrixStream());
                }
            }
        }
        SharedPreferences.Editor editor = sp.edit();
        try {
            editor.putString(ConstUtils.SP_SCREEN_MATRIX_LIST, PrefrenceUtils.SceneList2String(targetList));
        } catch (IOException e) {
            e.printStackTrace();
        }
        editor.commit();
    }

    //这是屏幕表格
    void setMyTable() {
        mLinPutLable.removeAllViews();
        GetRowColumns();

        mMyTable = new MyTable(mContext, mLinPutLable.getWidth(), mLinPutLable.getHeight());
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
                mListScreenBeen = mMyTable.getScreenMatrixSelectItemIndex();
                if (mListScreenBeen.size() == 1)showScreenInfo();
                for (ScreenMatrixBean screenBean : mListScreenBeen) {
                    Log.e("坐标:::", screenBean.getRow() + "-" + screenBean.getColumn());
                }
            }
        });

    }

    void showScreenInfo() {
        LogUtil.e("进到showScreenInfo方法:","外部");
        if (mListScreenBeen.size() == 1) {
            ScreenMatrixBean screenBean = mListScreenBeen.get(0);
            LogUtil.e("进到showScreenInfo方法:","内部true");
            for (int i=0;i<mListScreenApp.size();i++) {
                if (screenBean.getRow()==mListScreenApp.get(i).getRow()&&screenBean.getColumn()==mListScreenApp.get(i).getColumn()){
                    LogUtil.e("进到showScreenInfo方法:","相等");
                    mAddrTv.setText(mListScreenApp.get(i).getAddr() + "");
                    mDelayTimeTv.setText(mListScreenApp.get(i).getDelaytime() + "");
                    mInputQuantityTv.setText(mListScreenApp.get(i).getInputQuan() + "");
                    mMatrixCategoryTv.setText(mListScreenApp.get(i).getMatrixCategory());
                    mMatrixFactoryTv.setText(mListScreenApp.get(i).getMatrixFactory());
                    mMatrixInputNameTv.setText(mListScreenApp.get(i).getMatrixInputName());
                    mMatrixStreamTv.setText(mListScreenApp.get(i).getMatrixStream());
                    LogUtil.e("进到showScreenInfo方法:",mListScreenApp.get(i).toString());
                    return;
                }else {
                    LogUtil.e("进到showScreenInfo方法:","内部false");
                    mAddrTv.setText("");
                    mDelayTimeTv.setText("");
                    mInputQuantityTv.setText("");
                    mMatrixCategoryTv.setText("");
                    mMatrixFactoryTv.setText("");
                    mMatrixStreamTv.setText("");
                    mMatrixInputNameTv.setText("");
                }
            }
        }
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
        editor.putInt(ConstUtils.SP_ROWS, Rs);
        editor.putInt(ConstUtils.SP_COLUMNS, Cs);
        editor.commit();

        return new byte[]{(byte) Irs, (byte) Ics, (byte) Ire, (byte) Ice};
    }


    @Override
    public void takeScreenShot() {
        View mView = inflateView;

        Observable.just(mView)
                .map(new Func1<View, Canvas>() {
                    @Override
                    public Canvas call(View view) {
                        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
                        Canvas canvas = new Canvas(bitmap);
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
        switch (v.getId()) {
            case R.id.btn_set_matrix:
                if (mMatirxSetDialog != null) {
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
