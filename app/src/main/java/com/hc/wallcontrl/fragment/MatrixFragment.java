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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.hc.wallcontrl.R;
import com.hc.wallcontrl.bean.ScreenMatrixBean;
import com.hc.wallcontrl.com.fragment.BaseFragment;
import com.hc.wallcontrl.util.ConstUtils;
import com.hc.wallcontrl.util.LogUtil;
import com.hc.wallcontrl.util.PrefrenceUtils;
import com.hc.wallcontrl.util.ToastUtil;
import com.hc.wallcontrl.util.ViewUtil;
import com.hc.wallcontrl.view.DropEditText;
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
public class MatrixFragment extends BaseFragment implements  View.OnClickListener {

    private Context mContext;
    private SharedPreferences sp = null;
    boolean bConnected = false;

    private Bitmap bitmap;

    private LinearLayout mLinPutLable;
    private Button mSetMatrixBtn;
//    private MatrixSettingDialog mMatirxSetDialog;

    private Spinner mMatrixCateSpinner;
    private Spinner mMatrixFacSpinner;
    private DropEditText mStreamOutDropEdit;
    private DropEditText mInputNameDropEdit;
    private EditText mInputQuanEtv;
    private EditText mDelayTimeEtv;
    private EditText mAddrEtv;
    private LinearLayout mStreamDropLin;

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
        mMatrixCateSpinner = (Spinner) inflateView.findViewById(R.id.spinner_matrix_category);
        mMatrixFacSpinner = (Spinner) inflateView.findViewById(R.id.spinner_matrix_factory);
        mStreamOutDropEdit = (DropEditText) inflateView.findViewById(R.id.dropetv_matrix_stream);
        mStreamDropLin=findViewById(R.id.lin_matrix_stream);
        mInputNameDropEdit= (DropEditText)inflateView.findViewById(R.id.dropetv_matrix_name);
        mInputQuanEtv = (EditText)inflateView.findViewById(R.id.etv_input_quantity);
        mDelayTimeEtv = (EditText)inflateView.findViewById(R.id.etv_delay_time);
        mAddrEtv = (EditText)inflateView.findViewById(R.id.etv_addr);

        String[] cateArray = mContext.getResources().getStringArray(R.array.matrix_category);
        String[] facArray = mContext.getResources().getStringArray(R.array.matrix_fac);
        String[] inputNameArray=mContext.getResources().getStringArray(R.array.matrix_input);
        String[] streamOutArray=mContext.getResources().getStringArray(R.array.matrix_stream);

        ArrayAdapter mCateAdapter = new ArrayAdapter(mContext, R.layout.spinner_list_item, cateArray);
        ArrayAdapter mFacAdapter = new ArrayAdapter(mContext, R.layout.spinner_list_item, facArray);
        ArrayAdapter mStreamOutAdapter = new ArrayAdapter<String>(mContext, R.layout.spinner_list_item, streamOutArray);
        ArrayAdapter mInputNameAdapter=new ArrayAdapter<String> (mContext,R.layout.spinner_list_item,inputNameArray);

        mMatrixCateSpinner.setAdapter(mCateAdapter);
        mMatrixFacSpinner.setAdapter(mFacAdapter);
        mStreamOutDropEdit.setAdapter(mStreamOutAdapter);
        mInputNameDropEdit.setAdapter(mInputNameAdapter);

        mSetMatrixBtn.setOnClickListener(this);
        setMyTable();
    }


    //设置屏幕矩阵信息
    void setMatrixInfo(List<ScreenMatrixBean> resList, List<ScreenMatrixBean> targetList) {
        for (ScreenMatrixBean screenBean : resList) {
            for (int i=0;i<targetList.size();i++) {
                if (screenBean.getRow()==targetList.get(i).getRow()&&screenBean.getColumn()==targetList.get(i).getColumn()){
                    String category=(String) mMatrixCateSpinner.getSelectedItem();
                    String factory=(String) mMatrixFacSpinner.getSelectedItem();
                    int quan=Integer.parseInt(mInputQuanEtv.getText().toString());
                    int delay=Integer.parseInt(mDelayTimeEtv.getText().toString());
                    int addr=Integer.parseInt(mAddrEtv.getText().toString());
                    String inputName=mInputNameDropEdit.getText().toString();
                    String streamOut=mStreamOutDropEdit.getText().toString();

                    targetList.get(i).setMatrixCategory(category);
                    targetList.get(i).setMatrixFactory(factory);
                    targetList.get(i).setInputQuan(quan);
                    targetList.get(i).setDelaytime(delay);
                    targetList.get(i).setAddr(addr);
                    targetList.get(i).setMatrixInputName(inputName);
                    targetList.get(i).setMatrixStream(streamOut);
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
                if (mListScreenApp==null||mListScreenApp.size()==0){
                    ToastUtil.showShortMessage("第一次打开应用，请先设置屏幕数量！");
                    return;
                }
                SeltArea = mMyTable.myGetSeltArea();
                if (SeltArea[0] != 0) {
                    Log.e("SelectedArea: ", String.valueOf(SeltArea[0]) + ":" + String.valueOf(SeltArea[1]) + "===" + String.valueOf(SeltArea[2]) + ":" + String.valueOf(SeltArea[3]));
                }
                mListScreenBeen = mMyTable.getScreenMatrixSelectItemIndex();
                if (mListScreenBeen.size() == 1){
                    showScreenInfo();
                }
                for (ScreenMatrixBean screenBean : mListScreenBeen) {
                    Log.e("坐标:::", screenBean.getRow() + "-" + screenBean.getColumn());
                }
            }
        });

    }

    void showScreenInfo() {
        LogUtil.e("进到showScreenInfo方法:"+"外部");
        if (mListScreenBeen.size() == 1) {
            ScreenMatrixBean screenBean = mListScreenBeen.get(0);
            LogUtil.e("进到showScreenInfo方法:"+"内部true");
            for (int i=0;i<mListScreenApp.size();i++) {
                if (screenBean.getRow()==mListScreenApp.get(i).getRow()&&screenBean.getColumn()==mListScreenApp.get(i).getColumn()){
                    LogUtil.e("进到showScreenInfo方法:"+"相等");
                    String addr=mListScreenApp.get(i).getAddr() + "";
                    String delay=mListScreenApp.get(i).getDelaytime() + "";
                    String quan=mListScreenApp.get(i).getInputQuan() + "";
                    String inputName=mListScreenApp.get(i).getMatrixInputName()+"";
                    String streamOut=mListScreenApp.get(i).getMatrixStream()+"";
                    String matrixCate=mListScreenApp.get(i).getMatrixCategory()+"";
                    String matrixFac=mListScreenApp.get(i).getMatrixFactory()+"";

                    mAddrEtv.setText(addr);
                    mDelayTimeEtv.setText(delay);
                    mInputQuanEtv.setText(quan);
                    ViewUtil.setSpinnerItemSelectedByValue(mMatrixCateSpinner,matrixCate);
                    ViewUtil.setSpinnerItemSelectedByValue(mMatrixFacSpinner,matrixFac);
                    mInputNameDropEdit.setText(inputName);
                    mStreamOutDropEdit.setText(streamOut);
                    LogUtil.e("进到showScreenInfo方法:"+mListScreenApp.get(i).toString());
                    return;
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
                if (mListScreenApp!=null){
                    if (mListScreenBeen.size()==1){
                        setMatrixInfo(mListScreenBeen,mListScreenApp);
                    }else if (mListScreenBeen.size()>1){
                        ToastUtil.showShortMessage("请先选择一个屏幕且仅能选择一个！");
                    }

                }else {
                    ToastUtil.showShortMessage("请先到设置页面设置幕墙！");
                }
                break;
        }
    }

}
