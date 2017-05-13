package com.hc.wallcontrl.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
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
import com.hc.wallcontrl.bean.ScreenInputBean;
import com.hc.wallcontrl.com.fragment.BaseFragment;
import com.hc.wallcontrl.util.ConstUtils;
import com.hc.wallcontrl.util.LogUtil;
import com.hc.wallcontrl.util.PrefrenceUtils;
import com.hc.wallcontrl.view.InputSettingDialog;
import com.hc.wallcontrl.view.MyTable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
    private TextView mMatrixCateTv, mSingleScreenTv, mUseMatrixTv, mInputNameTv, mMatrixSwitchTv;

    private InputSettingDialog mInputSerttingDialog;

    private Bitmap bitmap;

    private MyTable mMyTable;
    private int[] SeltArea = new int[4];
    //行开始坐标 列开始坐标 行结束坐标 列结束坐标
    int Irs, Ics, Ire, Ice;
    private int Rs = 4;
    private int Cs = 4;

    private ArrayList<ScreenInputBean> mListScreenBeen;
    private List<ScreenInputBean> mListScreenApp;

    public static InputControlFragment newInstance() {
        return new InputControlFragment();
    }

    @Override
    public int getCreateViewLayoutId() {
        return R.layout.fragmrnt_inputcontrol;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void initData() {
        mListScreenBeen = new ArrayList<>();
        sp = mContext.getSharedPreferences(ConstUtils.SHAREDPREFERENCES, Context.MODE_PRIVATE);
        if (sp == null) return;
        Rs = sp.getInt(ConstUtils.SP_ROWS, Rs);
        Cs = sp.getInt(ConstUtils.SP_COLUMNS, Cs);
        bConnected = sp.getBoolean(ConstUtils.SP_ISCONN, bConnected);
        String listBeanStr = sp.getString(ConstUtils.SP_SCREEN_INPUT_LIST, "");
        try {
            mListScreenApp = PrefrenceUtils.String2SceneList(listBeanStr);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initView(View inflateView, Bundle savedInstanceState) {
        mLinPutLable = (LinearLayout) inflateView.findViewById(R.id.lin_table);

        mSetMatrixBtn = (Button) inflateView.findViewById(R.id.btn_set_input);
        mMatrixCateTv = (TextView) inflateView.findViewById(R.id.tv_matirx_category);
        mMatrixSwitchTv = (TextView) inflateView.findViewById(R.id.tv_matirx_switch);
        mInputNameTv = (TextView) inflateView.findViewById(R.id.tv_inputname);
        mUseMatrixTv = (TextView) inflateView.findViewById(R.id.tv_usematirx);
        mSingleScreenTv = (TextView) inflateView.findViewById(R.id.tv_singlescreen);



        mSetMatrixBtn.setOnClickListener(this);
        setMatirxSetDialog();
        setMyTable();
    }

    void setMatirxSetDialog() {
        mInputSerttingDialog = new InputSettingDialog(mContext, R.style.CustomDatePickerDialog);
        Window window = mInputSerttingDialog.getWindow();
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(R.style.MatrixDialogStyle);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.x = 0;
        lp.y = -20;
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.alpha = 9f;
        window.setAttributes(lp);
        mInputSerttingDialog.setOnCancelClickListener(new InputSettingDialog.onCancelClickListener() {
            @Override
            public void onClick() {
                mInputSerttingDialog.dismiss();
            }
        });

        mInputSerttingDialog.setOnConfirmClickListener(new InputSettingDialog.onConfirmClickListener() {
            @Override
            public void onClick() {
                Log.e("mInputDialog:信号源", mInputSerttingDialog.getInputSource());
                Log.e("mInputDialog:矩阵输入", mInputSerttingDialog.getMatrixInput());
                Log.e("mInputDialog:矩阵切换", mInputSerttingDialog.getMatrixSwitch());
                Log.e("mInputDialog:单屏模式", mInputSerttingDialog.isSingleScreen() + "");
                Log.e("mInputDialog:使用矩阵", mInputSerttingDialog.isUseMatrix() + "");
                setMatrixInfo(mListScreenBeen, mListScreenApp, mInputSerttingDialog);
                mInputSerttingDialog.dismiss();
            }


        });
    }

    void setMatrixInfo(ArrayList<ScreenInputBean> resList, List<ScreenInputBean> targetList, InputSettingDialog dialog) {
        for (ScreenInputBean screenInputBean : resList) {
            for (int i = 0; i < targetList.size(); i++) {
                if (screenInputBean.getRow() == targetList.get(i).getRow() && screenInputBean.getColumn() == targetList.get(i).getColumn()) {
                    targetList.get(i).setInputName(dialog.getMatrixInput());
                    targetList.get(i).setSignalSource(dialog.getInputSource());
                    targetList.get(i).setSwitchCate(dialog.getMatrixSwitch());
                    targetList.get(i).setUseMatrix(dialog.isUseMatrix());
                }
            }
        }
        SharedPreferences.Editor editor = sp.edit();
        try {
            editor.putString(ConstUtils.SP_SCREEN_INPUT_LIST, PrefrenceUtils.SceneList2String(targetList));
        } catch (IOException e) {
            e.printStackTrace();
        }
        editor.commit();
    }


    private void setMyTable() {
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
                mListScreenBeen = mMyTable.getScreenInputSelectItemIndex();
                if (mListScreenBeen.size() == 1)showScreenInfo();
                for (ScreenInputBean screenBean : mListScreenBeen) {
                    Log.e("坐标:::", screenBean.getRow() + "-" + screenBean.getColumn());
                }
            }
        });

    }

    byte[] GetRowColumns() {
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

    void showScreenInfo() {
        LogUtil.e("进到showScreenInfo方法:"+"外部");
        if (mListScreenBeen.size() == 1) {
            ScreenInputBean screenBean = mListScreenBeen.get(0);
            LogUtil.e("进到showScreenInfo方法:"+"内部true");
            for (int i=0;i<mListScreenApp.size();i++) {
                if (screenBean.getRow()==mListScreenApp.get(i).getRow()&&screenBean.getColumn()==mListScreenApp.get(i).getColumn()){
                    LogUtil.e("进到showScreenInfo方法:"+"相等");
                    /**
                     *  mMatrixCateTv = (TextView) inflateView.findViewById(R.id.tv_matirx_category);
                     mMatrixSwitchTv = (TextView) inflateView.findViewById(R.id.tv_matirx_switch);
                     mInputNameTv = (TextView) inflateView.findViewById(R.id.tv_inputname);
                     mUseMatrixTv = (TextView) inflateView.findViewById(R.id.tv_usematirx);
                     mSingleScreenTv = (TextView) inflateView.findViewById(R.id.tv_singlescreen);
                     */
                    mMatrixCateTv.setText(mListScreenApp.get(i).getSignalSource() + "");
                    mMatrixSwitchTv.setText(mListScreenApp.get(i).getSwitchCate() + "");
                    mInputNameTv.setText(mListScreenApp.get(i).getInputName() + "");
                    mUseMatrixTv.setText(mListScreenApp.get(i).isUseMatrix()+"");
                    LogUtil.e("进到showScreenInfo方法:"+mListScreenApp.get(i).toString());
                    return;
                }else {
                    LogUtil.e("进到showScreenInfo方法:"+"内部false");
                    mMatrixCateTv.setText("");
                    mMatrixSwitchTv.setText("");
                    mInputNameTv.setText("");
                    mUseMatrixTv.setText("");
                    mSingleScreenTv.setText("");
                }
            }
        }
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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_set_input:
                if (mInputSerttingDialog != null) {
                    mInputSerttingDialog.show();
                }
                break;
        }

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }
}
