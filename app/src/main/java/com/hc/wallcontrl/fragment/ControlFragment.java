package com.hc.wallcontrl.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;

import com.hc.wallcontrl.R;
import com.hc.wallcontrl.bean.ScreenInputBean;
import com.hc.wallcontrl.com.ClsV59Ctrl;
import com.hc.wallcontrl.com.fragment.BaseFragment;
import com.hc.wallcontrl.service.SocketService;
import com.hc.wallcontrl.util.ClsCmds;
import com.hc.wallcontrl.util.ConstUtils;
import com.hc.wallcontrl.util.LogUtil;
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

import at.markushi.ui.CircleButton;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;

public class ControlFragment extends BaseFragment implements View.OnTouchListener, View.OnClickListener {

//    GestureDetector gd;// 手势监听类

    SharedPreferences sp = null;

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

    private Context mContext;

    private Spinner mSignalSourceSpinner, mMatrixInputSpinner, mMatrixSwitchSpinner;

    private LinearLayout mLinPutLable, mlinMatrixInput, mLinMatrixSwitch;

    //界面滚动按钮
    private Button btnOn, btnOff, btnAV, btnVGA, btnDVI, btnHDMI, btnAV2, btnDP, btnPlan1, btnPlan2, btnPlan3, btnPlan4, btnPlan5, btnPlan6, btnPlan7, btnPlan8, btnPlan9, btnPlan10, btnPlan11, btnPlan12, btnPlan13, btnPlan14, btnPlan15, btnPlan16;
    private Button mSetMatrixInputBtn;
    private Switch mSingleScreenSwitch, mUseMatrixSwitch;

    private CircleButton mTurnUpBtn, mTurnDownBtn, mMuteBtn;
    private Button mSignalSourceBtn, mInfoBtn, mExitBtn, mUpBtn, mConfirmBtn, mLeftBtn, mMenuBtn, mRightBtn, mFreezeBtn, mDownBtn, mRotateBtn, mPlayBtn, mStopBtn, mPauseBtn, mTattedCodeBtn, mPositionBtn, mColorBtn;
    private int Rs = 4;
    private int Cs = 4;
    private int[] SeltArea = new int[4];

    MyTable mMyTable;

    Timer timer;

    private Bitmap bitmap;

    private ArrayList<ScreenInputBean> mListScreenBeen;
    private List<ScreenInputBean> mListScreenApp;

    //Socket
    private Thread mThread = null;
    private Socket mSocket = null;
    private BufferedReader mBufferedReader = null;
    OutputStream mWriter = null;
    //private PrintWriter mPrintWriter = null;
    private String RevStr = "";
    //Connect to TcpServer


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
        if (!mPortString.equals("")) {
            Port = Integer.parseInt(mPortString);
        } else {
            ToastUtil.showShortMessage("链接地址为空！");
        }
        String listBeanStr = sp.getString(ConstUtils.SP_SCREEN_INPUT_LIST, "");
        try {
            mListScreenApp = PrefrenceUtils.String2SceneList(listBeanStr);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        if (bConnected)
            SetConnect();
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
                    boolean useMatrix = mUseMatrixSwitch.isSelected();

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
        mTurnUpBtn = (CircleButton) inflateView.findViewById(R.id.btn_turn_up);
        mTurnDownBtn = (CircleButton) inflateView.findViewById(R.id.btn_turn_down);
        mMuteBtn = (CircleButton) inflateView.findViewById(R.id.btn_mute);
        mSignalSourceBtn = (Button) inflateView.findViewById(R.id.btn_signalsource);
        mInfoBtn = (Button) inflateView.findViewById(R.id.btn_info);
        mExitBtn = (Button) inflateView.findViewById(R.id.btn_exit);
        mUpBtn = (Button) inflateView.findViewById(R.id.btn_up);
        mConfirmBtn = (Button) inflateView.findViewById(R.id.btn_confirm);
        mLeftBtn = (Button) inflateView.findViewById(R.id.btn_left);
        mMenuBtn = (Button) inflateView.findViewById(R.id.btn_menu);
        mRightBtn = (Button) inflateView.findViewById(R.id.btn_right);
        mFreezeBtn = (Button) inflateView.findViewById(R.id.btn_freeze);
        mDownBtn = (Button) inflateView.findViewById(R.id.btn_down);
        mRotateBtn = (Button) inflateView.findViewById(R.id.btn_rotate);
        mPlayBtn = (Button) inflateView.findViewById(R.id.btn_play);
        mPauseBtn = (Button) inflateView.findViewById(R.id.btn_pause);
        mTattedCodeBtn = (Button) inflateView.findViewById(R.id.btn_tatted_code);
        mPositionBtn = (Button) inflateView.findViewById(R.id.btn_position);
        mColorBtn = (Button) inflateView.findViewById(R.id.btn_color);

        mSignalSourceSpinner = (Spinner) inflateView.findViewById(R.id.spinner_input_source);
        mMatrixInputSpinner = (Spinner) inflateView.findViewById(R.id.spinner_matrix_input);
        mMatrixSwitchSpinner = (Spinner) inflateView.findViewById(R.id.spinner_matrix_switch);

        mUseMatrixSwitch = (Switch) inflateView.findViewById(R.id.switch_usematrix);
        mSingleScreenSwitch = (Switch) inflateView.findViewById(R.id.switch_singlescreen);

        //http://www.jb51.net/article/55329.htm
        btnOn.setOnClickListener(this);
        btnOff.setOnClickListener(this);
        btnAV.setOnClickListener(this);
        btnVGA.setOnClickListener(this);
        btnDVI.setOnClickListener(this);
        btnHDMI.setOnClickListener(this);
        btnAV2.setOnClickListener(this);
        btnDP.setOnClickListener(this);
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
        mTurnUpBtn.setOnClickListener(this);
        mTurnDownBtn.setOnClickListener(this);
        mMuteBtn.setOnClickListener(this);
        mSignalSourceBtn.setOnClickListener(this);
        mInfoBtn.setOnClickListener(this);
        mExitBtn.setOnClickListener(this);
        mUpBtn.setOnClickListener(this);
        mConfirmBtn.setOnClickListener(this);
        mLeftBtn.setOnClickListener(this);
        mMenuBtn.setOnClickListener(this);
        mRightBtn.setOnClickListener(this);
        mFreezeBtn.setOnClickListener(this);
        mDownBtn.setOnClickListener(this);
        mRotateBtn.setOnClickListener(this);
        mPlayBtn.setOnClickListener(this);
        mPauseBtn.setOnClickListener(this);
        mTattedCodeBtn.setOnClickListener(this);
        mPositionBtn.setOnClickListener(this);
        mColorBtn.setOnClickListener(this);
        Log.e("Height3322:", String.valueOf(mLinPutLable.getHeight()));

        mUseMatrixSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mlinMatrixInput.setVisibility(View.GONE);
                    mLinMatrixSwitch.setVisibility(View.GONE);
                } else {
                    mlinMatrixInput.setVisibility(View.VISIBLE);
                    mLinMatrixSwitch.setVisibility(View.VISIBLE);
                }
            }
        });
        setMyTable();
        //Toast.makeText(MainActivity.this,"OnStart",Toast.LENGTH_SHORT).show();

        SetMainFrm();
    }

    //绑定fragment布局
    @Override
    public int getCreateViewLayoutId() {
        return R.layout.fragment_control;
    }


    @Override
    public void onStart() {
        super.onStart();
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
        if (!bConnected) return;

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

    //连接操作
    private void SetConnect() {
        if (!mPortString.equals("")) Port = Integer.parseInt(mPortString);

//        byte[] Buf = new byte[]{1, 9, 8, 7, 0, 3, 2, 1};

//        mThread = new Thread(mRunnable);
//        mThread.start();
        Intent intent = new Intent();
        intent.setAction(ConstUtils.ACTION_CONN);
        intent.putExtra(ConstUtils.BROADCAST_IP, mIPString);
        intent.putExtra(ConstUtils.BROADCAST_ISCONN,bConnected);
        int socketPort = Integer.parseInt(mPortString);
        intent.putExtra(ConstUtils.BROADCAST_PORT, socketPort);
        getActivity().sendBroadcast(intent);

        String result = mIPString + ":" + mPortString;
        ToastUtil.showShortMessage(result);
    }

    //设置表格
    private void SetTable() {
        //GetSize();

        mLinPutLable.removeAllViews();
        GetRowColumns();
        //table = new MyTable(MainActivity.this, llPutLable.getWidth(),llPutLable.getHeight());
        mMyTable = new MyTable(mContext, mLinPutLable.getWidth(), mLinPutLable.getHeight());

        Log.e("Height3321:", String.valueOf(mLinPutLable.getHeight()));
        //table.mySetTableHeight(llPutLable.getMeasuredHeight());

        mMyTable.mySetRows(Rs);
        mMyTable.mySetCols(Cs);

        mMyTable.LoadTable();
        mLinPutLable.addView(mMyTable);

        //Toast.makeText(MainActivity.this,"3",Toast.LENGTH_SHORT).show();

//        mMyTable.setTableOnClickListener(new MyTable.OnTableClickListener() {
//            @Override
//            public void onTableClickListener(int x, int y, Cursor c) {
//                String str = "Pos:(" + String.valueOf(y + 1) + "," + String.valueOf(x + 1) + ")";
//                //Toast.makeText(MainActivity.this, str, Toast.LENGTH_SHORT).show();
//                //Log.e("Click", str);
//
//            }
//        });

//        mMyTable.setTableOnTouchListener(new MyTable.OnTableTouchListener() {
//            @Override
//            public void onTableTouchListener(View v, MotionEvent event) {
//                SeltArea = mMyTable.myGetSeltArea();
//                if (SeltArea[0] != 0) {
//                    Log.e("SelectedArea: ", String.valueOf(SeltArea[0]) + ":" + String.valueOf(SeltArea[1]) + "===" + String.valueOf(SeltArea[2]) + ":" + String.valueOf(SeltArea[3]));
//
//                }
//            }
//        });

        GetSize();

        Log.e("llLayout's Height", String.valueOf(mLinPutLable.getHeight()));
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
                TestCmd(GetRowColumns(), ClsCmds.EmptyValue, ClsCmds.RecallPlan, (byte) 0x01);
                break;
            case com.hc.wallcontrl.R.id.btnPlan2:
                TestCmd(GetRowColumns(), ClsCmds.EmptyValue, ClsCmds.RecallPlan, (byte) 0x02);
                break;
            case com.hc.wallcontrl.R.id.btnPlan3:
                TestCmd(GetRowColumns(), ClsCmds.EmptyValue, ClsCmds.RecallPlan, (byte) 0x03);
                break;
            case com.hc.wallcontrl.R.id.btnPlan4:
                TestCmd(GetRowColumns(), ClsCmds.EmptyValue, ClsCmds.RecallPlan, (byte) 0x04);
                break;
            case com.hc.wallcontrl.R.id.btnPlan5:
                TestCmd(GetRowColumns(), ClsCmds.EmptyValue, ClsCmds.RecallPlan, (byte) 0x05);
                break;
            case com.hc.wallcontrl.R.id.btnPlan6:
                TestCmd(GetRowColumns(), ClsCmds.EmptyValue, ClsCmds.RecallPlan, (byte) 0x06);
                break;
            case com.hc.wallcontrl.R.id.btnPlan7:
                TestCmd(GetRowColumns(), ClsCmds.EmptyValue, ClsCmds.RecallPlan, (byte) 0x07);
                break;
            case com.hc.wallcontrl.R.id.btnPlan8:
                TestCmd(GetRowColumns(), ClsCmds.EmptyValue, ClsCmds.RecallPlan, (byte) 0x08);
                break;
            case com.hc.wallcontrl.R.id.btnPlan9:
                TestCmd(GetRowColumns(), ClsCmds.EmptyValue, ClsCmds.RecallPlan, (byte) 0x09);
                break;
            case R.id.btn_set_matrixinput:
                if (mListScreenApp != null && mListScreenBeen.size() > 0) {
                    setMatrixInfo(mListScreenBeen, mListScreenApp);
                }
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

    //发送数据
    private void Send2Server(byte[] BufMsg) {
        try {
            mWriter.write(BufMsg);
            mWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //线程：监听服务器发来的消息
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            Connect2TcpServer();
        }
    };


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


