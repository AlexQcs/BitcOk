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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.hc.wallcontrl.R;
import com.hc.wallcontrl.adapter.DropRecyclerAdapter;
import com.hc.wallcontrl.bean.ScreenMatrixBean;
import com.hc.wallcontrl.bean.ScreenOutputBean;
import com.hc.wallcontrl.com.fragment.BaseFragment;
import com.hc.wallcontrl.util.ConstUtils;
import com.hc.wallcontrl.util.ListUtils;
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
public class MatrixFragment extends BaseFragment implements View.OnClickListener {

    private Context mContext;
    private SharedPreferences sp = null;
    boolean bConnected = false;

    private Bitmap bitmap;

    private LinearLayout mLinPutLable;
    private Button mSetMatrixBtn;
//    private MatrixSettingDialog mMatirxSetDialog;

    private int mInputIndex;

    private int mCateIndex;

    private Spinner mMatrixCateSpinner;
    private Spinner mMatrixFacSpinner;
    private EditText mStreamOutDropEdit;
    private DropEditText mInputNameDropEdit;
    private EditText mInputQuanEtv;
    private EditText mDelayTimeEtv;
    private EditText mAddrEtv;
    private LinearLayout mStreamDropLin;

//    private EditorAdapter mEditorAdapter;

    private MyTable mMyTable;
    private int[] SeltArea = new int[4];
    //行开始坐标 列开始坐标 行结束坐标 列结束坐标
    int Irs, Ics, Ire, Ice;
    private int Rs = 4;
    private int Cs = 4;

    private ArrayList<ScreenOutputBean> mListScreenBeen;
    private List<ScreenOutputBean> mListOutput;
    private List<String> mListInputName;
    private List<ScreenMatrixBean> mListMatrixApp;

    private String[] mCateArray, mFacArray, mInputNameArray;
    private ArrayAdapter mCateAdapter, mFacAdapter, mStreamOutAdapter;
    private DropRecyclerAdapter mDropRecyclerAdapter;

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
        mListInputName = new ArrayList<>();
        sp = mContext.getSharedPreferences(ConstUtils.SHAREDPREFERENCES, Context.MODE_PRIVATE);

        if (sp == null) return;
        Rs = sp.getInt(ConstUtils.SP_ROWS, Rs);
        Cs = sp.getInt(ConstUtils.SP_COLUMNS, Cs);
        bConnected = sp.getBoolean(ConstUtils.SP_ISCONN, bConnected);
//        String listBeanStr = sp.getString(ConstUtils.SP_SCREEN_OUTPUT_LIST, "");
        String matrixListStr = sp.getString(ConstUtils.SP_MATRIX_LIST, "");

        try {
//             mListOutput = PrefrenceUtils.String2SceneList(listBeanStr);
            mListMatrixApp = PrefrenceUtils.String2SceneList(matrixListStr);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        mCateArray = mContext.getResources().getStringArray(R.array.matrix_category);
        mFacArray = mContext.getResources().getStringArray(R.array.matrix_fac);

        if (mListMatrixApp == null || mListMatrixApp.size() == 0) {
            mListMatrixApp = new ArrayList<>();
            for (int i = 0; i < mCateArray.length; i++) {
                ScreenMatrixBean temp = new ScreenMatrixBean();
                List<String> listStr=new ArrayList<>();
                List<ScreenOutputBean> listOutput=new ArrayList<>();
                temp.setMatrixCategory(mCateArray[i]);
                int inputQuan = Rs * Cs;
                temp.setInputQuan(inputQuan);
                temp.setDelaytime(20);
                //设置矩阵厂家
                ScreenMatrixBean.MatrixFactory matrixFac = new ScreenMatrixBean.MatrixFactory();
                matrixFac.setAddr(0);
                matrixFac.setMatrixName(mFacArray[0]);
                temp.setMatrixFactory(matrixFac);
                temp.setHasSet(false);

                for (int r = 0; r < Rs; r++) {
                    for (int c = 0; c < Cs; c++) {
                        ScreenOutputBean screenOutputBean=new ScreenOutputBean();
                        screenOutputBean.setColumn(c+1);
                        screenOutputBean.setRow(r+1);
                        screenOutputBean.setMatrixOutputStream(Cs*r+c+1);
                        listOutput.add(screenOutputBean);
                    }
                }

                //设置输入通道
                for (int j = 0; j < inputQuan; j++) {
                    listStr.add("矩阵输入" + (j+1));
                }
                temp.setMatrixInputName(listStr);
                temp.setListOutputScreen(listOutput);
                mListMatrixApp.add(temp);
            }
            mListInputName=mListMatrixApp.get(mCateIndex).getMatrixInputName();
            mListOutput=mListMatrixApp.get(mCateIndex).getListOutputScreen();
        }else {
            mListInputName=mListMatrixApp.get(mCateIndex).getMatrixInputName();
            mListOutput=mListMatrixApp.get(mCateIndex).getListOutputScreen();
        }


    }

    //初始化控件
    @Override
    public void initView(View inflateView, Bundle savedInstanceState) {
        mLinPutLable = (LinearLayout) inflateView.findViewById(R.id.lin_table);
        mSetMatrixBtn = (Button) inflateView.findViewById(R.id.btn_set_matrix);
        mMatrixCateSpinner = (Spinner) inflateView.findViewById(R.id.spinner_matrix_category);
        mMatrixFacSpinner = (Spinner) inflateView.findViewById(R.id.spinner_matrix_factory);
        mStreamOutDropEdit = (EditText) inflateView.findViewById(R.id.dropetv_matrix_stream);
        mStreamDropLin = findViewById(R.id.lin_matrix_stream);
        mInputNameDropEdit = (DropEditText) inflateView.findViewById(R.id.dropetv_matrix_name);
        mInputQuanEtv = (EditText) inflateView.findViewById(R.id.etv_input_quantity);
        mDelayTimeEtv = (EditText) inflateView.findViewById(R.id.etv_delay_time);
        mAddrEtv = (EditText) inflateView.findViewById(R.id.etv_addr);
        mInputQuanEtv.setText(mListInputName.size() + "");


//        mInputNameList= Arrays.asList(mInputNameArray);
        mInputNameArray=mListInputName.toArray(new String[0]);

        mCateAdapter = new ArrayAdapter(mContext, R.layout.spinner_list_item, mCateArray);
        mFacAdapter = new ArrayAdapter(mContext, R.layout.spinner_list_item, mFacArray);
//        mInputNameAdapter=new ArrayAdapter(mContext,R.layout.spinner_list_item,mInputNameArray );
        mDropRecyclerAdapter=new DropRecyclerAdapter(mContext,mListInputName);
//        mEditorAdapter = new EditorAdapter(mContext, mListInputName);
        mMatrixCateSpinner.setAdapter(mCateAdapter);


        mDropRecyclerAdapter.setOnListViewItemClickListener(new DropRecyclerAdapter.OnRecyclerViewItemClickListenner() {

            @Override
            public void onItemClick(View view, int position) {
                mInputIndex=position;
                mInputNameDropEdit.dismiss();
                mInputNameDropEdit.setText(mListInputName.get(position));
            }
        });

        mMatrixCateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mCateIndex=position;
                showMatrixInfo(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        mMatrixFacSpinner.setAdapter(mFacAdapter);
        mInputNameDropEdit.setAdapter(mDropRecyclerAdapter);

//        mInputNameDropEdit.setItemClickListener(new DropEditText.OnListViewItemClickListener() {
//            @Override
//            public void onClick(int position) {
//                mInputIndex = position;
//
//            }
//        });
        mSetMatrixBtn.setOnClickListener(this);
        setMyTable();
    }

    //设置屏幕输出通道信息
    void setScreenOutPutInfo(List<ScreenOutputBean> resList, List<ScreenOutputBean> targetList) {
        for (ScreenOutputBean screenBean : resList) {
            for (int i = 0; i < targetList.size(); i++) {
                if (screenBean.getRow() == targetList.get(i).getRow() && screenBean.getColumn() == targetList.get(i).getColumn()) {
                    int streamOut = Integer.parseInt(mStreamOutDropEdit.getText().toString());

                    targetList.get(i).setMatrixOutputStream(streamOut);
                }
            }
        }
        SharedPreferences.Editor editor = sp.edit();
        try {
            editor.putString(ConstUtils.SP_SCREEN_OUTPUT_LIST, PrefrenceUtils.SceneList2String(targetList));
        } catch (IOException e) {
            e.printStackTrace();
        }
        editor.commit();
    }

    //设置矩阵输入信息
    void setMatrixInfo(List<ScreenMatrixBean> targetList, int index) {


        ScreenMatrixBean screenBean = targetList.get(index);

        String category = (String) mMatrixCateSpinner.getSelectedItem();

        ScreenMatrixBean.MatrixFactory matrixFac = new ScreenMatrixBean.MatrixFactory();
        String factoryName = (String) mMatrixFacSpinner.getSelectedItem();
        int factoryAddr = Integer.parseInt(mAddrEtv.getText().toString());
        matrixFac.setMatrixName(factoryName);
        matrixFac.setAddr(factoryAddr);

        int quan = Integer.parseInt(mInputQuanEtv.getText().toString());
        int delay = Integer.parseInt(mDelayTimeEtv.getText().toString());

        String name = mInputNameDropEdit.getText().toString();
        mListInputName=screenBean.getMatrixInputName();
        if (mInputIndex < mListInputName.size()) {
            mListInputName.set(mInputIndex, name);
        }

        screenBean.setMatrixCategory(category);
        screenBean.setInputQuan(quan);
        screenBean.setMatrixFactory(matrixFac);
        screenBean.setDelaytime(delay);
        screenBean.setMatrixInputName(mListInputName);
        screenBean.setHasSet(true);

        targetList.set(index, screenBean);

        SharedPreferences.Editor editor = sp.edit();
        try {
            editor.putString(ConstUtils.SP_MATRIX_LIST, PrefrenceUtils.SceneList2String(targetList));
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
                if ( mListOutput == null ||  mListOutput.size() == 0) {
                    ToastUtil.showShortMessage("第一次打开应用，请先设置屏幕数量！");
                    return;
                }
                SeltArea = mMyTable.myGetSeltArea();
                if (SeltArea[0] != 0) {
                    Log.e("SelectedArea: ", String.valueOf(SeltArea[0]) + ":" + String.valueOf(SeltArea[1]) + "===" + String.valueOf(SeltArea[2]) + ":" + String.valueOf(SeltArea[3]));
                }
                mListScreenBeen = mMyTable.getScreenMatrixSelectItemIndex();
                if (mListScreenBeen.size() == 1) {
                    showScreenOutPutInfo();
                }
                for (ScreenOutputBean screenBean : mListScreenBeen) {
                    Log.e("坐标:::", screenBean.getRow() + "-" + screenBean.getColumn());
                }
            }
        });

    }

    void showMatrixInfo(int index) {
        ScreenMatrixBean screenBean = mListMatrixApp.get(index);
        screenBean.toString();
        String input=screenBean.getMatrixInputName().get(0);
        String addr = screenBean.getMatrixFactory().getAddr() + "";
        String delay = screenBean.getDelaytime() + "";
        mListInputName = screenBean.getMatrixInputName();
        String matrixFac = screenBean.getMatrixFactory().getMatrixName() + "";
        int quan=screenBean.getInputQuan();
        mListOutput=screenBean.getListOutputScreen();
        showScreenOutPutInfo();

        mAddrEtv.setText(addr);
        mDelayTimeEtv.setText(delay);
        mInputNameDropEdit.setText(input);
        ViewUtil.setSpinnerItemSelectedByValue(mMatrixFacSpinner, matrixFac);
        mInputQuanEtv.setText(quan+"");
        notifyAdapter();
        LogUtil.e("进到showScreenInfo方法:" + screenBean.toString());

    }

    void showScreenOutPutInfo() {
        LogUtil.e("进到showScreenInfo方法:" + "外部");
        if (mListScreenBeen.size() == 1) {
            ScreenOutputBean screenBean = mListScreenBeen.get(0);
            LogUtil.e("进到showScreenInfo方法:" + "内部true");
            for (int i = 0; i <  mListOutput.size(); i++) {
                if (screenBean.getRow() ==  mListOutput.get(i).getRow() && screenBean.getColumn() ==  mListOutput.get(i).getColumn()) {
                    LogUtil.e("进到showScreenInfo方法:" + "相等");
                    String outputName =  mListOutput.get(i).getMatrixOutputStream() + "";
                    mStreamOutDropEdit.setText(outputName);
                    LogUtil.e("进到showScreenInfo方法:" +  mListOutput.get(i).toString());
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
                if ( mListOutput != null) {
                    if (mListScreenBeen.size() == 1) {
                        setScreenOutPutInfo(mListScreenBeen,  mListOutput);
                    } else if (mListScreenBeen.size() > 1) {
                        ToastUtil.showShortMessage("请先选择一个屏幕且仅能选择一个！");
                    }

                } else {
                    ToastUtil.showShortMessage("请先到设置页面设置幕墙！");
                }
                String inputNameQuanStr = mInputQuanEtv.getText().toString();
                String delayTimeStr = mDelayTimeEtv.getText().toString();
                String outPutStreamStr = mStreamOutDropEdit.getText().toString();
                String matrixNameStr = mInputNameDropEdit.getText().toString();
                String addrStr = mAddrEtv.getText().toString();
                if (inputNameQuanStr.equals("")) {
                    ToastUtil.showShortMessage("设置错误！请设置输入总数！");
                    return;
                } else if (inputNameQuanStr.equals("0")) {
                    ToastUtil.showShortMessage("设置错误！输入总数不能为0！");
                    return;
                } else if (delayTimeStr.equals("")) {
                    ToastUtil.showShortMessage("设置错误！命令延时不能为空！");
                    return;
                } else if (addrStr.equals("")) {
                    ToastUtil.showShortMessage("设置错误！设备地址不能为空！");
                    return;
                } else if (matrixNameStr.equals("")) {
                    ToastUtil.showShortMessage("设置错误！输入名称不能为空！");
                    return;
                } else if (outPutStreamStr.equals("")) {
                    ToastUtil.showShortMessage("设置错误！输出通道不能为空！");
                }


                int inputNameQuan = Integer.parseInt(inputNameQuanStr);
                LogUtil.e(inputNameQuan + "");
                setMatrixInfo(mListMatrixApp, mCateIndex);

                changePopListViewItemQuan(inputNameQuan);
                changePopListViewItem();
                saveData();
                break;
        }
    }

    void changePopListViewItemQuan(int quan) {
        LogUtil.e(mListInputName.size() + "");

        if (quan > mListInputName.size()) {
            mListInputName = ListUtils.insertAtLast(mListInputName, "矩阵输入", quan);
            notifyAdapter();
        }

        if (quan < mListInputName.size()) {
            mListInputName = ListUtils.popList(mListInputName, quan);
            notifyAdapter();
        }
        for (int i = 0; i < mListInputName.size(); i++) {
            Log.e("数据",mListInputName.get(i));
        }
    }

    void changePopListViewItem() {
        int current = mInputIndex;
        Log.e("数据指向",mInputIndex+"");
        String editStr = mInputNameDropEdit.getText().toString();
        if (current < mListInputName.size()) {
            mListInputName.set(mInputIndex,editStr);
        }

        notifyAdapter();

    }

    void notifyAdapter(){
        mDropRecyclerAdapter.setData(mListInputName);
        mDropRecyclerAdapter.notifyDataSetChanged();
        mInputNameDropEdit.setAdapter(mDropRecyclerAdapter);
    }




    void saveData() {

        SharedPreferences.Editor editor = sp.edit();
        try {
            editor.putString(ConstUtils.SP_MATRIX_LIST, PrefrenceUtils.SceneList2String(mListMatrixApp));
            editor.commit();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
