package com.hc.wallcontrl.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Switch;

import com.hc.wallcontrl.R;
import com.hc.wallcontrl.com.ClsV59Ctrl;
import com.hc.wallcontrl.com.fragment.BaseFragment;
import com.hc.wallcontrl.util.ClsCmds;
import com.hc.wallcontrl.util.ConstUtils;
import com.hc.wallcontrl.util.ToastUtil;
import com.hc.wallcontrl.view.MyTable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

public class ControlFragment extends BaseFragment implements View.OnTouchListener, View.OnClickListener {

//    GestureDetector gd;// 手势监听类


    SharedPreferences sp = null;

    private InetSocketAddress is = null;
    //private OutputStream writer;
    Socket MySocket = null;
    int Port = 8899;
    //socket地址
    private String strIP = "192.168.1.11";
    //socket端口
    private String strPort = "8899";
    //是否已经连接socket
    boolean bConnected = false;
    int Irs, Ics, Ire, Ice;

    private Context mContext;

    //界面山滚动按钮
    private Button btnOn, btnOff, btnAV, btnVGA, btnDVI, btnHDMI, btnAV2, btnDP, btnPlan1, btnPlan2, btnPlan3, btnPlan4, btnPlan5, btnPlan6, btnPlan7, btnPlan8, btnPlan9, btnPlan10, btnPlan11, btnPlan12, btnPlan13, btnPlan14, btnPlan15, btnPlan16;


    private Switch swSFWall;

    private int Rs = 4;
    private int Cs = 4;
    private int[] SeltArea = new int[4];

    MyTable table;
    LinearLayout llPutLable;
    Timer timer;

    private Bitmap bitmap;


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

    @Override
    public void initData() {
        sp = mContext.getSharedPreferences(ConstUtils.SHAREDPREFERENCES, Context.MODE_PRIVATE);
        if (sp == null) return;
        Rs = sp.getInt("rows", Rs);
        Cs = sp.getInt("columns", Cs);
        bConnected = sp.getBoolean("isConn", bConnected);
        strIP = sp.getString("IP", strIP);
        strPort = sp.getString("Port", "8899");
        if (!strPort.equals("")) {
            Port = Integer.parseInt(strPort);
        } else {
            ToastUtil.showShortMessage("链接地址为空！");
        }
    }

    @Override
    public void initView(View inflateView, Bundle savedInstanceState) {
        llPutLable = (LinearLayout) inflateView.findViewById(com.hc.wallcontrl.R.id.llPutLables);

        swSFWall = (Switch) inflateView.findViewById(com.hc.wallcontrl.R.id.swSFWall);

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
        Log.e("Height3322:", String.valueOf(llPutLable.getHeight()));

        LinearLayout rl = (LinearLayout) inflateView.findViewById(com.hc.wallcontrl.R.id.activity_main);
        rl.setOnTouchListener(this);
        rl.setLongClickable(true);
    }

    @Override
    public int getCreateViewLayoutId() {
        return R.layout.fragment_control;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (bConnected)
            SetConnect();
        SetTable();
        //Toast.makeText(MainActivity.this,"OnStart",Toast.LENGTH_SHORT).show();

        SetMainFrm();
    }

    private void SetMainFrm() {
        //ViewGroup.LayoutParams lyParam = new ActionBar.LayoutParams();
        for (int i = 0; i < 4; i++) SeltArea[i] = 0;
    }


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
        editor.putInt("rows", Rs);
        editor.putInt("columns", Cs);
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
            Send2Server(Buf);
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
                    if (table.myGetTableHeight() != 0) {//llPutLable.getHeight()
                        Log.i("LinearLayoutW", llPutLable.getWidth() + "");
                        Log.i("LinearLayoutH", llPutLable.getHeight() + "");
                        //table.mySetTableHeight(llPutLable.getMeasuredHeight());
                        Log.e("Table.Height:", String.valueOf(table.myGetTableHeight()));

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

    private void SetConnect() {
        if (!strPort.equals("")) Port = Integer.parseInt(strPort);

        byte[] Buf = new byte[]{1, 9, 8, 7, 0, 3, 2, 1};

        mThread = new Thread(mRunnable);
        mThread.start();


        String result = strIP + ":" + strPort;
        ToastUtil.showShortMessage(result);
    }

    //设置表格
    private void SetTable() {
        //GetSize();

        llPutLable.removeAllViews();
        GetRowColumns();
        //table = new MyTable(MainActivity.this, llPutLable.getWidth(),llPutLable.getHeight());
        table = new MyTable(mContext, llPutLable.getWidth(), llPutLable.getHeight());

        Log.e("Height3321:", String.valueOf(llPutLable.getHeight()));
        //table.mySetTableHeight(llPutLable.getMeasuredHeight());

        table.mySetRows(Rs);
        table.mySetCols(Cs);

        table.LoadTable();
        llPutLable.addView(table);

        //Toast.makeText(MainActivity.this,"3",Toast.LENGTH_SHORT).show();

        table.setTableOnClickListener(new MyTable.OnTableClickListener() {
            @Override
            public void onTableClickListener(int x, int y, Cursor c) {
                String str = "Pos:(" + String.valueOf(y + 1) + "," + String.valueOf(x + 1) + ")";
                //Toast.makeText(MainActivity.this, str, Toast.LENGTH_SHORT).show();
                //Log.e("Click", str);

            }
        });

        table.setTableOnTouchListener(new MyTable.OnTableTouchListener() {
            @Override
            public void onTableTouchListener(View v, MotionEvent event) {
                SeltArea = table.myGetSeltArea();
                if (SeltArea[0] != 0) {
                    Log.e("SelectedArea: ", String.valueOf(SeltArea[0]) + ":" + String.valueOf(SeltArea[1]) + "===" + String.valueOf(SeltArea[2]) + ":" + String.valueOf(SeltArea[3]));

                }
            }
        });

        GetSize();

        Log.e("llLayout's Height", String.valueOf(llPutLable.getHeight()));
    }

    @Override
    public void onClick(View v) {
        byte SourceSw = ClsCmds.Source;
        byte bSFWall = 0;
        if (swSFWall.isChecked())
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
            default:
                break;
        }
    }


    private void Connect2TcpServer() {
        try {
            //Socket 实例化
            mSocket = new Socket(strIP, Port);
            //获取Socket输入输出流进行读写操作
            mBufferedReader = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
            mWriter = mSocket.getOutputStream();
            //mPrintWriter = new PrintWriter(mSocket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //return mSocket;
    }

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
}


