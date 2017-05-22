package com.hc.wallcontrl.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.hc.wallcontrl.R;
import com.hc.wallcontrl.bean.ScreenInputBean;
import com.hc.wallcontrl.bean.ScreenMatrixBean;
import com.hc.wallcontrl.bean.ScreenOutputBean;
import com.hc.wallcontrl.com.ClsV59Ctrl;
import com.hc.wallcontrl.com.fragment.BaseFragment;
import com.hc.wallcontrl.service.SocketService;
import com.hc.wallcontrl.util.ClsCmds;
import com.hc.wallcontrl.util.ConstUtils;
import com.hc.wallcontrl.util.HexUtils;
import com.hc.wallcontrl.util.LogUtil;
import com.hc.wallcontrl.util.MatrixUtils;
import com.hc.wallcontrl.util.PrefrenceUtils;
import com.hc.wallcontrl.util.ToastUtil;
import com.hc.wallcontrl.util.ViewUtil;
import com.hc.wallcontrl.view.MyTable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;

public class ControlFragment extends BaseFragment implements View.OnTouchListener, View.OnClickListener {

//    GestureDetector gd;// 手势监听类

    SharedPreferences sp = null;

    //矩阵控制类
    private MatrixUtils mMatrixUtils;

    private InetSocketAddress is = null;
    //private OutputStream writer;
    Socket MySocket = null;
    int Port = 8899;
    //socket地址
    private String mIPString = "192.168.1.11";
    //socket端口
    private String mPortString = "8899";
    //是否已经连接socket
    boolean bConnected = false;
    int Irs, Ics, Ire, Ice;
    //输入源spinner的位置
    int mInputSoureIndex = 0;

    private Context mContext;

    private Spinner mSignalSourceSpinner, mMatrixInputSpinner, mMatrixSwitchSpinner;
    private String mSignalSource, mMatrixInput, mMatrixSwitch;
    private String mMatrixName;
    private int mMatrixAddr;
    private ArrayAdapter mMatrixInputAdapter;

    private int mMatrixInputSpinnerPosition;//记录选中位置
    private LinearLayout mLinPutLable, mlinMatrixInput, mLinMatrixSwitch;

    //界面滚动按钮
    private Button btnOn, btnOff, btnAV, btnVGA, btnDVI, btnHDMI, btnAV2, btnDP, btnPlan1, btnPlan2, btnPlan3, btnPlan4, btnPlan5, btnPlan6, btnPlan7, btnPlan8, btnPlan9, btnPlan10, btnPlan11, btnPlan12, btnPlan13, btnPlan14, btnPlan15, btnPlan16;
    private Button mSetMatrixInputBtn;
    private Switch mSingleScreenSwitch, mUseMatrixSwitch, mModeSetSwitch;


    private TextView mTvMode;
    private int Rs = 4;
    private int Cs = 4;
    private int[] SeltArea = new int[4];
    //广播命令的地址
    private byte[] mBroadAddr = new byte[4];


    MyTable mMyTable;

    Timer timer;

    //矩阵发送延时
    private int mDelayTime = 0;

    private Bitmap bitmap;
    private List<String> mInputNameList;
    private ArrayList<ScreenInputBean> mListScreenBeen;
    private List<ScreenInputBean> mListScreenApp;
    private List<ScreenMatrixBean> mListMatrixApp;
    private List<ScreenOutputBean> mListOutputApp;

    private boolean mIsMatrixHasSet = false;
    //Socket
    private Thread mThread = null;
    private Socket mSocket = null;
    private BufferedReader mBufferedReader = null;
    OutputStream mWriter = null;
    //private PrintWriter mPrintWriter = null;
    private String RevStr = "";
    //Connect to TcpServer

    //选择模式是调用还是保存
    private boolean mIsSave;

    public static ControlFragment newInstance() {
        ControlFragment controlFragment = new ControlFragment();
        return controlFragment;

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = getActivity();
    }

    //初始化数据
    @Override
    public void initData() {

        mMatrixUtils = new MatrixUtils();

        Intent intent = new Intent();
        intent.setClass(mContext, SocketService.class);
        mContext.startService(intent);

        mListScreenBeen = new ArrayList<>();
        sp = mContext.getSharedPreferences(ConstUtils.SHAREDPREFERENCES, Context.MODE_PRIVATE);
        if (sp == null) return;
        Rs = sp.getInt(ConstUtils.SP_ROWS, Rs);
        Cs = sp.getInt(ConstUtils.SP_COLUMNS, Cs);
        bConnected = sp.getBoolean(ConstUtils.SP_ISCONN, bConnected);
        mIPString = sp.getString(ConstUtils.SP_IP, mIPString);
        mPortString = sp.getString(ConstUtils.SP_PORT, "8899");

        SeltArea[0] = SeltArea[1] = 1;
        SeltArea[2] = Rs;
        SeltArea[3] = Cs;

        mBroadAddr[0] = mBroadAddr[1] = (byte) 0x00;
        mBroadAddr[2] = mBroadAddr[3] = (byte) 0xff;
        for (int i = 0; i < Rs; i++) {
            for (int j = 0; j < Cs; j++) {
                ScreenInputBean screenInputBean = new ScreenInputBean();
                screenInputBean.setColumn(j + 1);
                screenInputBean.setRow(i + 1);
                mListScreenBeen.add(screenInputBean);
            }
        }
        if (!mPortString.equals("")) {
            Port = Integer.parseInt(mPortString);
        } else {
            ToastUtil.showShortMessage("链接地址为空！");
        }
        String listBeanStr = sp.getString(ConstUtils.SP_SCREEN_INPUT_LIST, "");
        String listMatrixStr = sp.getString(ConstUtils.SP_MATRIX_LIST, "");
        try {
            mListScreenApp = PrefrenceUtils.String2SceneList(listBeanStr);
            mListMatrixApp = PrefrenceUtils.String2SceneList(listMatrixStr);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        mInputNameList = new ArrayList<>();


//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                while (bConnected){
//                    SetConnect();
//                    try {
//                        Thread.sleep(1000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//
//                }
//
//            }
//        }).start();

    }

    /**
     *
     *
     */
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
                if (mListScreenApp == null || mListScreenApp.size() == 0) {
                    ToastUtil.showShortMessage("第一次打开应用，请先设置屏幕数量！");
                    return;
                }
                SeltArea = mMyTable.myGetSeltArea();
                if (SeltArea[0] != 0) {
                    Log.e("SelectedArea: ", String.valueOf(SeltArea[0]) + ":" + String.valueOf(SeltArea[1]) + "===" + String.valueOf(SeltArea[2]) + ":" + String.valueOf(SeltArea[3]));
                }
                mListScreenBeen = mMyTable.getScreenInputSelectItemIndex();
                if (mListScreenBeen.size() == 1) {
                    showScreenInfo();
                }
                for (ScreenInputBean screenBean : mListScreenBeen) {
                    Log.e("坐标:::", screenBean.getRow() + "-" + screenBean.getColumn());
                }
            }
        });

    }

    //显示此屏幕的信息

    /**
     * @param
     * @return null
     * @author.alex.on.2017年5月11日15:03:48
     * @throw null
     * 此方法用于:当选择一个屏幕的时候显示所选择屏幕的配置信息，包括:输入源,输入通道,矩阵类型,是否使用矩阵
     */
    void showScreenInfo() {
        if (mListScreenBeen.size() == 1) {
            ScreenInputBean screenBean = mListScreenBeen.get(0);
            for (int i = 0; i < mListScreenApp.size(); i++) {
                if (screenBean.getRow() == mListScreenApp.get(i).getRow() && screenBean.getColumn() == mListScreenApp.get(i).getColumn()) {
                    LogUtil.e("进到showScreenInfo方法:" + "相等");
                    String signalSource = mListScreenApp.get(i).getSignalSource() + "";
                    String matrixInput = mListScreenApp.get(i).getInputName() + "";
                    String matrixSwitch = mListScreenApp.get(i).getSwitchCate() + "";
                    boolean useMatrix = mListScreenApp.get(i).isUseMatrix();

                    mUseMatrixSwitch.setChecked(useMatrix);
                    ViewUtil.setSpinnerItemSelectedByValue(mSignalSourceSpinner, signalSource);
                    ViewUtil.setSpinnerItemSelectedByValue(mMatrixInputSpinner, matrixInput);
                    ViewUtil.setSpinnerItemSelectedByValue(mMatrixSwitchSpinner, matrixSwitch);

                    LogUtil.e("进到showScreenInfo方法:" + mListScreenApp.get(i).toString());
                    return;
                }
            }
        }
    }

    //设置屏幕矩阵信息

    /**
     * @param resList,target 屏幕输入配置对象集合源对象及目标对象
     * @return null
     * @author.alex.on.2017年5月11日15:05:40
     * @throw IOException 当SharedPreferences.Editor书写出错时抛出
     * 此方法用于: 把选择的屏幕对象的屏幕输入配置信息保存到SharedPreferences中
     */
    void setMatrixInfo(List<ScreenInputBean> resList, List<ScreenInputBean> targetList) {
        for (ScreenInputBean screenBean : resList) {
            for (int i = 0; i < targetList.size(); i++) {
                if (screenBean.getRow() == targetList.get(i).getRow() && screenBean.getColumn() == targetList.get(i).getColumn()) {
                    String signalSource = (String) mSignalSourceSpinner.getSelectedItem();
                    String matrixInput = (String) mMatrixInputSpinner.getSelectedItem();
                    String matrixSwitch = (String) mMatrixSwitchSpinner.getSelectedItem();
                    boolean useMatrix = mUseMatrixSwitch.isChecked();

                    targetList.get(i).setSignalSource(signalSource);
                    targetList.get(i).setInputName(matrixInput);
                    targetList.get(i).setSwitchCate(matrixSwitch);
                    targetList.get(i).setUseMatrix(useMatrix);
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

    //初始化控件
    @Override
    public void initView(View inflateView, Bundle savedInstanceState) {
        mLinPutLable = (LinearLayout) inflateView.findViewById(R.id.lin_table);
        mlinMatrixInput = (LinearLayout) inflateView.findViewById(R.id.lin_matrix_input);
        mLinMatrixSwitch = (LinearLayout) inflateView.findViewById(R.id.lin_matrix_switch);

        btnOn = (Button) inflateView.findViewById(com.hc.wallcontrl.R.id.btnOn);
        btnOff = (Button) inflateView.findViewById(com.hc.wallcontrl.R.id.btnOff);
        btnAV = (Button) inflateView.findViewById(com.hc.wallcontrl.R.id.btnAV);
        btnVGA = (Button) inflateView.findViewById(com.hc.wallcontrl.R.id.btnVGA);
        btnDVI = (Button) inflateView.findViewById(com.hc.wallcontrl.R.id.btnDVI);
        btnHDMI = (Button) inflateView.findViewById(com.hc.wallcontrl.R.id.btnHDMI);
        btnAV2 = (Button) inflateView.findViewById(com.hc.wallcontrl.R.id.btnAV2);
        btnDP = (Button) inflateView.findViewById(com.hc.wallcontrl.R.id.btnDP);

        btnPlan1 = (Button) inflateView.findViewById(com.hc.wallcontrl.R.id.btnPlan1);
        btnPlan2 = (Button) inflateView.findViewById(com.hc.wallcontrl.R.id.btnPlan2);
        btnPlan3 = (Button) inflateView.findViewById(com.hc.wallcontrl.R.id.btnPlan3);
        btnPlan4 = (Button) inflateView.findViewById(com.hc.wallcontrl.R.id.btnPlan4);
        btnPlan5 = (Button) inflateView.findViewById(com.hc.wallcontrl.R.id.btnPlan5);
        btnPlan6 = (Button) inflateView.findViewById(com.hc.wallcontrl.R.id.btnPlan6);
        btnPlan7 = (Button) inflateView.findViewById(com.hc.wallcontrl.R.id.btnPlan7);
        btnPlan8 = (Button) inflateView.findViewById(com.hc.wallcontrl.R.id.btnPlan8);
        btnPlan9 = (Button) inflateView.findViewById(com.hc.wallcontrl.R.id.btnPlan9);
        mSetMatrixInputBtn = (Button) inflateView.findViewById(R.id.btn_set_matrixinput);

        mTvMode = (TextView) inflateView.findViewById(R.id.tv_mode);


        mSignalSourceSpinner = (Spinner) inflateView.findViewById(R.id.spinner_input_source);
        mMatrixInputSpinner = (Spinner) inflateView.findViewById(R.id.spinner_matrix_input);
        mMatrixSwitchSpinner = (Spinner) inflateView.findViewById(R.id.spinner_matrix_switch);

        mUseMatrixSwitch = (Switch) inflateView.findViewById(R.id.switch_usematrix);
        mSingleScreenSwitch = (Switch) inflateView.findViewById(R.id.switch_singlescreen);
        mModeSetSwitch = (Switch) inflateView.findViewById(R.id.switch_recall_save);

        //http://www.jb51.net/article/55329.htm
        btnOn.setOnClickListener(this);
        btnOff.setOnClickListener(this);
        btnAV.setOnClickListener(this);
        btnVGA.setOnClickListener(this);
        btnDVI.setOnClickListener(this);
        btnHDMI.setOnClickListener(this);
        btnAV2.setOnClickListener(this);
        btnDP.setClickable(false);
        btnPlan1.setOnClickListener(this);
        btnPlan2.setOnClickListener(this);
        btnPlan3.setOnClickListener(this);
        btnPlan4.setOnClickListener(this);
        btnPlan5.setOnClickListener(this);
        btnPlan6.setOnClickListener(this);
        btnPlan7.setOnClickListener(this);
        btnPlan8.setOnClickListener(this);
        btnPlan9.setOnClickListener(this);
        mSetMatrixInputBtn.setOnClickListener(this);

        Log.e("Height3322:", String.valueOf(mLinPutLable.getHeight()));


        String[] inputMatrixArray = (String[]) mInputNameList.toArray(new String[mInputNameList.size()]);
        mMatrixInputAdapter = new ArrayAdapter(mContext, android.R.layout.simple_spinner_item, inputMatrixArray);
        mMatrixInputSpinner.setAdapter(mMatrixInputAdapter);
        mMatrixInputSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mMatrixInputSpinnerPosition = position + 1;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        mUseMatrixSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mMatrixInputSpinner.setVisibility(View.INVISIBLE);
                    mMatrixSwitchSpinner.setVisibility(View.INVISIBLE);
                } else {
                    if (mIsMatrixHasSet) {
                        mMatrixInputSpinner.setVisibility(View.VISIBLE);
                        mMatrixSwitchSpinner.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
        mMatrixSwitchSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        mSignalSourceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                showMatrixInfo(position);
                mInputSoureIndex = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mModeSetSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mIsSave = true;
                    mTvMode.setText("模式保存");
                } else {
                    mIsSave = false;
                    mTvMode.setText("模式调用");
                }
            }
        });


        if (mListMatrixApp != null && mListMatrixApp.size() != 0) {
            mInputNameList = mListMatrixApp.get(0).getMatrixInputName();
            mListOutputApp = mListMatrixApp.get(0).getListOutputScreen();
        } else {
            mMatrixInputSpinner.setVisibility(View.INVISIBLE);
//            mSetMatrixInputBtn.setClickable(false);
        }

        setMyTable();
        //Toast.makeText(MainActivity.this,"OnStart",Toast.LENGTH_SHORT).show();

//        SetMainFrm();
    }


    //绑定fragment布局
    @Override
    public int getCreateViewLayoutId() {
        return R.layout.fragment_control;
    }


    private void SetMainFrm() {
        //ViewGroup.LayoutParams lyParam = new ActionBar.LayoutParams();
        for (int i = 0; i < 4; i++) SeltArea[i] = 0;
    }


    //触摸事件
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }


    private void showMatrixInfo(int index) {
        if (mListMatrixApp == null || index >= mListMatrixApp.size()) {
            ToastUtil.showShortMessage("请先设置矩阵");
            mMatrixInputSpinner.setVisibility(View.INVISIBLE);
            mMatrixSwitchSpinner.setVisibility(View.INVISIBLE);
            mIsMatrixHasSet = false;
            return;
        }
        ScreenMatrixBean screenMatrixBean = mListMatrixApp.get(index);
        mInputNameList = screenMatrixBean.getMatrixInputName();
        mListOutputApp = screenMatrixBean.getListOutputScreen();
        String[] inputMatrixArray = (String[]) mInputNameList.toArray(new String[mInputNameList.size()]);
        mMatrixInputAdapter = new ArrayAdapter(mContext, android.R.layout.simple_spinner_item, inputMatrixArray);
        mMatrixInputSpinner.setAdapter(mMatrixInputAdapter);
        if (screenMatrixBean.isHasSet()) {
            if (!mUseMatrixSwitch.isChecked()) {
                mMatrixInputSpinner.setVisibility(View.VISIBLE);
                mMatrixSwitchSpinner.setVisibility(View.VISIBLE);
            }
            mIsMatrixHasSet = true;
        } else {
            mMatrixInputSpinner.setVisibility(View.INVISIBLE);
            mMatrixSwitchSpinner.setVisibility(View.INVISIBLE);
            mIsMatrixHasSet = false;
        }
    }

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

    private void TestCmd(byte[] addr, byte Fun, byte ValueH, byte ValueL) {
        if (!bConnected) {
            Message message = new Message();
            message.what = 0x12;
            mToastHandler.sendMessage(message);
            return;
        }

        /*
        byte[] Buf = new byte[7];
        Buf[0] = (byte) 0xf5;
        Buf[1] = (byte) 0xb0;
        Buf[2] = (byte)((addr[0]<<4)+(addr[1]&0x0f));
        Buf[3] = (byte)((addr[2]<<4)+(addr[3]&0x0f));
        Buf[4] = (byte)Fun;
        Buf[5] = (byte)Value;
        Buf[6] = (byte) 0xae;
        */
        byte[] Buf = ClsV59Ctrl.GetCmd(addr, Fun, ValueH, ValueL, ClsCmds.ModeW);

        try {
//            Send2Server(Buf);
            Intent intent = new Intent();
            intent.setAction(ConstUtils.ACTION_SEND);
            intent.putExtra(ConstUtils.BROADCAST_BUFF, Buf);
            getActivity().sendBroadcast(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }


        String BufStr = "";
        for (int i = 0; i < Buf.length; i++) {
            String tpStr = String.format("%2s", Integer.toHexString(Buf[i] & 0xFF));
            tpStr = tpStr.replaceAll("\\s", "0");
            BufStr += tpStr.toUpperCase() + " ";
        }

    }

    private void GetSize() {
        final Handler myHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    if (mMyTable.myGetTableHeight() != 0) {//llPutLable.getHeight()
                        Log.i("LinearLayoutW", mLinPutLable.getWidth() + "");
                        Log.i("LinearLayoutH", mLinPutLable.getHeight() + "");
                        //table.mySetTableHeight(llPutLable.getMeasuredHeight());
                        Log.e("Table.Height:", String.valueOf(mMyTable.myGetTableHeight()));

                        //SetTable();

                        //取消定时器
                        timer.cancel();

                    }
                }
            }
        };

        timer = new Timer();
        TimerTask task = new TimerTask() {
            public void run() {
                Message message = new Message();
                message.what = 1;
                myHandler.sendMessage(message);
            }
        };
        //延迟每次延迟10 毫秒 隔1秒执行一次
        timer.schedule(task, 10, 1000);
    }

    private Handler mToastHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0x11:
                    String ip = msg.getData().getString("ip");
                    ToastUtil.showShortMessage(ip);
                    break;
                case 0x12:
                    ToastUtil.showShortMessage("未连接局域网！");
                    break;
            }
        }
    };

    //连接操作
    private void SetConnect() {
        if (!mPortString.equals("")) Port = Integer.parseInt(mPortString);

//        byte[] Buf = new byte[]{1, 9, 8, 7, 0, 3, 2, 1};

//        mThread = new Thread(mRunnable);
//        mThread.start();
        Intent intent = new Intent();
        intent.setAction(ConstUtils.ACTION_CONN);
        intent.putExtra(ConstUtils.BROADCAST_IP, mIPString);
        intent.putExtra(ConstUtils.BROADCAST_ISCONN, bConnected);
        int socketPort = Integer.parseInt(mPortString);
        intent.putExtra(ConstUtils.BROADCAST_PORT, socketPort);
        mContext.sendBroadcast(intent);

        String result = mIPString + ":" + mPortString;
        Message msg = new Message();
        msg.what = 0x11;
        Bundle bundle = new Bundle();
        bundle.putString("ip", result);
        msg.setData(bundle);
        mToastHandler.sendMessage(msg);
//        ToastUtil.showShortMessage(result);
    }


    //控件点击事件
    @Override
    public void onClick(View v) {
        byte SourceSw = ClsCmds.Source;
        byte bSFWall = 0;
        if (mSingleScreenSwitch.isChecked())
            bSFWall = 0;
        else
            bSFWall = 1;

        switch (v.getId()) {

            case com.hc.wallcontrl.R.id.btnOn:
                TestCmd(GetRowColumns(), ClsCmds.Power, ClsCmds.EmptyValue, ClsCmds.PowerOn);
                break;
            case com.hc.wallcontrl.R.id.btnOff:
                TestCmd(GetRowColumns(), ClsCmds.Power, ClsCmds.EmptyValue, ClsCmds.PowerOff);
                break;
            case com.hc.wallcontrl.R.id.btnAV:
                TestCmd(GetRowColumns(), SourceSw, ClsCmds.AV, bSFWall);
                break;
            case com.hc.wallcontrl.R.id.btnVGA:
                TestCmd(GetRowColumns(), SourceSw, ClsCmds.VGA, bSFWall);
                break;
            case com.hc.wallcontrl.R.id.btnDVI:
                TestCmd(GetRowColumns(), SourceSw, ClsCmds.DVI, bSFWall);
                break;
            case com.hc.wallcontrl.R.id.btnHDMI:
                TestCmd(GetRowColumns(), SourceSw, ClsCmds.HDMI, bSFWall);
                break;
            case com.hc.wallcontrl.R.id.btnAV2:
                TestCmd(GetRowColumns(), SourceSw, ClsCmds.AV2, bSFWall);
                break;
            case com.hc.wallcontrl.R.id.btnDP:
                TestCmd(GetRowColumns(), SourceSw, ClsCmds.DP, bSFWall);
                break;
            case com.hc.wallcontrl.R.id.btnPlan1:
                clientSetPlan(mIsSave, 1, (byte) 0x01);
                break;
            case com.hc.wallcontrl.R.id.btnPlan2:
                clientSetPlan(mIsSave, 2, (byte) 0x02);
                break;
            case com.hc.wallcontrl.R.id.btnPlan3:
                clientSetPlan(mIsSave, 3, (byte) 0x03);
                break;
            case com.hc.wallcontrl.R.id.btnPlan4:
                clientSetPlan(mIsSave, 4, (byte) 0x04);
                break;
            case com.hc.wallcontrl.R.id.btnPlan5:
                clientSetPlan(mIsSave, 5, (byte) 0x05);
                break;
            case com.hc.wallcontrl.R.id.btnPlan6:
                clientSetPlan(mIsSave, 6, (byte) 0x06);
                break;
            case com.hc.wallcontrl.R.id.btnPlan7:
                clientSetPlan(mIsSave, 7, (byte) 0x07);
                break;
            case com.hc.wallcontrl.R.id.btnPlan8:
                clientSetPlan(mIsSave, 8, (byte) 0x08);
                break;
            case com.hc.wallcontrl.R.id.btnPlan9:
                clientSetPlan(mIsSave, 9, (byte) 0x09);
                break;
            case R.id.btn_set_matrixinput:
                clientSetSource(mInputSoureIndex, SourceSw, bSFWall);
                break;
            case R.id.btn_turn_up:
                TestCmd(GetRowColumns(), ClsCmds.IrMode, ClsCmds.EmptyValue, ClsCmds.IrFWall);
                break;
            case R.id.btn_mute:
                TestCmd(GetRowColumns(), ClsCmds.IrMode, ClsCmds.EmptyValue, ClsCmds.IrMute);
                break;
            case R.id.btn_turn_down:
                TestCmd(GetRowColumns(), ClsCmds.IrMode, ClsCmds.EmptyValue, ClsCmds.IrSWall);
                break;
            case R.id.btn_play:
                TestCmd(GetRowColumns(), ClsCmds.IrMode, ClsCmds.EmptyValue, ClsCmds.IrPlay);
                break;
            case R.id.btn_pause:
                TestCmd(GetRowColumns(), ClsCmds.IrMode, ClsCmds.EmptyValue, ClsCmds.IrPause);
                break;
            case R.id.btn_stop:
                TestCmd(GetRowColumns(), ClsCmds.IrMode, ClsCmds.EmptyValue, ClsCmds.IrStop);
                break;
            case R.id.btn_exit:
                TestCmd(GetRowColumns(), ClsCmds.IrMode, ClsCmds.EmptyValue, ClsCmds.IrExit);
                break;
            case R.id.btn_up:
                TestCmd(GetRowColumns(), ClsCmds.IrMode, ClsCmds.EmptyValue, ClsCmds.IrTop);
                break;
            case R.id.btn_confirm:
                TestCmd(GetRowColumns(), ClsCmds.IrMode, ClsCmds.EmptyValue, ClsCmds.IrEnter);
                break;
            case R.id.btn_left:
                TestCmd(GetRowColumns(), ClsCmds.IrMode, ClsCmds.EmptyValue, ClsCmds.IrLeft);
                break;
            case R.id.btn_menu:
                TestCmd(GetRowColumns(), ClsCmds.IrMode, ClsCmds.EmptyValue, ClsCmds.IrMenu);
                break;
            case R.id.btn_right:
                TestCmd(GetRowColumns(), ClsCmds.IrMode, ClsCmds.EmptyValue, ClsCmds.IrRight);
                break;
            case R.id.btn_freeze:
                TestCmd(GetRowColumns(), ClsCmds.IrMode, ClsCmds.EmptyValue, ClsCmds.IrFreeze);
                break;
            case R.id.btn_down:
                TestCmd(GetRowColumns(), ClsCmds.IrMode, ClsCmds.EmptyValue, ClsCmds.IrBottom);
                break;
            case R.id.btn_rotate:
                TestCmd(GetRowColumns(), ClsCmds.IrMode, ClsCmds.EmptyValue, ClsCmds.IrTurn);
                break;
            case R.id.btn_tatted_code:
                TestCmd(GetRowColumns(), ClsCmds.IrMode, ClsCmds.EmptyValue, ClsCmds.IrID);
                break;
            case R.id.btn_position:
                TestCmd(GetRowColumns(), ClsCmds.IrMode, ClsCmds.EmptyValue, ClsCmds.IrPos);
                break;
            case R.id.btn_color:
                TestCmd(GetRowColumns(), ClsCmds.IrMode, ClsCmds.EmptyValue, ClsCmds.IrColor);
                break;
            case R.id.btn_signalsource:
                TestCmd(GetRowColumns(), ClsCmds.IrMode, ClsCmds.EmptyValue, ClsCmds.IrSource);
                break;
            case R.id.btn_num_00:
                TestCmd(GetRowColumns(), ClsCmds.IrMode, ClsCmds.EmptyValue, ClsCmds.Ir0);
                break;
            case R.id.btn_info:
                TestCmd(GetRowColumns(), ClsCmds.IrMode, ClsCmds.EmptyValue, ClsCmds.IrInfo);
                break;
            case R.id.btn_num_01:
                TestCmd(GetRowColumns(), ClsCmds.IrMode, ClsCmds.EmptyValue, ClsCmds.Ir1);
                break;
            case R.id.btn_num_02:
                TestCmd(GetRowColumns(), ClsCmds.IrMode, ClsCmds.EmptyValue, ClsCmds.Ir2);
                break;
            case R.id.btn_num_03:
                TestCmd(GetRowColumns(), ClsCmds.IrMode, ClsCmds.EmptyValue, ClsCmds.Ir3);
                break;
            case R.id.btn_num_04:
                TestCmd(GetRowColumns(), ClsCmds.IrMode, ClsCmds.EmptyValue, ClsCmds.Ir4);
                break;
            case R.id.btn_num_05:
                TestCmd(GetRowColumns(), ClsCmds.IrMode, ClsCmds.EmptyValue, ClsCmds.Ir5);
                break;
            case R.id.btn_num_06:
                TestCmd(GetRowColumns(), ClsCmds.IrMode, ClsCmds.EmptyValue, ClsCmds.Ir6);
                break;
            case R.id.btn_num_07:
                TestCmd(GetRowColumns(), ClsCmds.IrMode, ClsCmds.EmptyValue, ClsCmds.Ir7);
                break;
            case R.id.btn_num_08:
                TestCmd(GetRowColumns(), ClsCmds.IrMode, ClsCmds.EmptyValue, ClsCmds.Ir8);
                break;
            case R.id.btn_num_09:
                TestCmd(GetRowColumns(), ClsCmds.IrMode, ClsCmds.EmptyValue, ClsCmds.Ir9);
                break;
            default:
                break;
        }
    }

    void clientSetSource(int sourceIndex, byte SourceSw, byte bSFWall) {
        if (mListScreenApp != null && mListScreenBeen.size() > 0) {
            setMatrixInfo(mListScreenBeen, mListScreenApp);
            byte sourceByte = 0x00;
            switch (sourceIndex) {
                case 0:
                    sourceByte = ClsCmds.AV;
                    break;
                case 1:
                    sourceByte = ClsCmds.AV2;
                    break;
                case 2:
                    sourceByte = ClsCmds.VGA;
                    break;
                case 3:
                    sourceByte = ClsCmds.DVI;
                    break;
                case 4:
                    sourceByte = ClsCmds.HDMI;
                    break;
//                case 5:
//                    sourceByte = ClsCmds.Svideo;
//                    break;
//                case 6:
//                    sourceByte = ClsCmds.YPBPR;
//                    break;
//                case 7:
//                    sourceByte = ClsCmds.VGA;
//                    break;
//                case 8:
//                    sourceByte = ClsCmds.DVI;
//                    break;
//                case 9:
//                    sourceByte = ClsCmds.HDMI;
//                    break;
//                case 10:
//                    sourceByte = ClsCmds.HDMI2;
//                    break;
//                case 11:
//                    sourceByte = ClsCmds.HDMI3;
//                    break;
//                case 12:
//                    sourceByte = ClsCmds.DMP1;
//                    break;
//                case 13:
//                    sourceByte = ClsCmds.DMP2;
//                    break;
                default:
                    break;
            }
            byte finalSourceByte = sourceByte;
            new Thread() {
                public void run() {
                    try {
                        TestCmd(GetRowColumns(), SourceSw, finalSourceByte, bSFWall);
                        Thread.sleep(500);
                        if (!mUseMatrixSwitch.isChecked()) userMatrixCtrl();
                    } catch (InterruptedException e) {
                    }
                }
            }.start();
        }
    }

    void matrixSetPlan(boolean isSave, int data) {
        if (isSave) {
            userMatrixSaveModeCtrl(ConstUtils.VGAMT, data);
        } else {
            userMatrixRecallModeCtrl(ConstUtils.VGAMT, data);
        }
    }

    void clientSetPlan(boolean isSave, int matrixData, byte data) {

        new Thread() {
            public void run() {
                try {
                    if (isSave) {
                        TestCmd(mBroadAddr, ClsCmds.SavaPlan, ClsCmds.EmptyValue, data);
                    } else {
                        TestCmd(mBroadAddr, ClsCmds.RecallPlan, ClsCmds.EmptyValue, data);
                    }
                    Thread.sleep(500);
                    matrixSetPlan(isSave, matrixData);
                } catch (InterruptedException e) {
                }
            }
        }.start();
    }

    //连接socket服务
    private void Connect2TcpServer() {
        try {
            //Socket 实例化
            mSocket = new Socket(mIPString, Port);
            //获取Socket输入输出流进行读写操作
            mBufferedReader = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
            mWriter = mSocket.getOutputStream();
            //mPrintWriter = new PrintWriter(mSocket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //return mSocket;
    }

    //线程：监听服务器发来的消息
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            Connect2TcpServer();
        }
    };


    //使用矩阵控制
    private void userMatrixCtrl() {
        if (mListMatrixApp == null || mListMatrixApp.size() == 0) {
            ToastUtil.showShortMessage("请先设置矩阵");
            return;
        } else if (mListScreenBeen == null || mListScreenBeen.size() == 0) {
            ToastUtil.showShortMessage("请先选择屏幕");
            return;
        } else if (mListOutputApp == null || mListOutputApp.size() == 0) {
            ToastUtil.showShortMessage("请先设置矩阵");
            return;
        }
        int[] input = new int[1];//矩阵输入
        int[] output = new int[mListScreenBeen.size()];//墙面输出
        int outCount;//输出数量
        mMatrixUtils.mxInSum = input.length;
        mMatrixUtils.mxOutSum = mListScreenBeen.size();
        mMatrixUtils.matrixInputMax = mInputNameList.size();
        outCount = mListScreenBeen.size();

//        for (int i = 0; i < mListMatrixApp.size(); i++) {
        input[0] = mMatrixInputSpinnerPosition;
//        }
        for (int i = 0; i < outCount; i++) {
            ScreenInputBean screenBean = mListScreenBeen.get(i);
            for (int j = 0; j < mListOutputApp.size(); j++) {
                if (screenBean.getRow() == mListOutputApp.get(j).getRow() && screenBean.getColumn() == mListOutputApp.get(j).getColumn()) {
                    output[i] = mListOutputApp.get(j).getMatrixOutputStream();
                }
            }
        }

        mMatrixName = "";
        mSignalSource = mSignalSourceSpinner.getSelectedItem().toString();
        mMatrixInput = mMatrixInputSpinner.getSelectedItem().toString();

        LogUtil.e(mMatrixInput + "");
        LogUtil.e(mSignalSource + "");

        for (ScreenMatrixBean bean : mListMatrixApp) {
            if (mSignalSource.equals(bean.getMatrixCategory())) {
                mMatrixName = bean.getMatrixFactory().getMatrixName();
                mMatrixAddr = bean.getMatrixFactory().getAddr();
                mDelayTime = bean.getDelaytime();
            }
        }

        mMatrixSwitch = mMatrixSwitchSpinner.getSelectedItem().toString();

        List<String> datas = mMatrixUtils.matrixControl(mMatrixName, mSignalSource, mMatrixSwitch, mMatrixAddr, input, output);
        for (String data : datas) {
            Log.e("返回数据", data + "。");
        }

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < datas.size(); i++) {
                    Intent intent = new Intent();
                    intent.setAction(ConstUtils.ACTION_SEND);
                    intent.putExtra(ConstUtils.BROADCAST_BUFF, HexUtils.hexStringToByte(datas.get(i)));

                    getActivity().sendBroadcast(intent);
                    try {
                        Thread.sleep(mDelayTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        new Thread(runnable).start();
    }

    //使用矩阵控制
    private void userMatrixSaveModeCtrl(final String matrixName, final int mode) {

        String data = mMatrixUtils.matrixSavePlan(matrixName, mode);
        Intent intent = new Intent();
        intent.setAction(ConstUtils.ACTION_SEND);
        intent.putExtra(ConstUtils.BROADCAST_BUFF, HexUtils.hexStringToByte(data));
        getActivity().sendBroadcast(intent);

    }

    //使用矩阵控制
    private void userMatrixRecallModeCtrl(final String matrixName, final int mode) {
        String data = mMatrixUtils.matrixRecallPlan(matrixName, mode);
        Intent intent = new Intent();
        intent.setAction(ConstUtils.ACTION_SEND);
        intent.putExtra(ConstUtils.BROADCAST_BUFF, HexUtils.hexStringToByte(data));
        getActivity().sendBroadcast(intent);

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

//                .map(inflateView->Bitmap.createBitmap(inflateView.getWidth(),inflateView.getHeight(),Bitmap.Config.ARGB_4444))
//                .map(bitmap -> new Canvas(bitmap))
//                .subscribeOn(AndroidSchedulers.mainThread())
//                .subscribe(canvas -> inflateView.draw(canvas));
    }


    @Override
    public Bitmap getBitmap() {
        return bitmap;
    }

}


