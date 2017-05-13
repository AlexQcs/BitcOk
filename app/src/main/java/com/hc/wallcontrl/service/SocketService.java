package com.hc.wallcontrl.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;

import com.badoo.mobile.util.WeakHandler;
import com.hc.wallcontrl.util.ConstUtils;
import com.hc.wallcontrl.util.LogUtil;
import com.hc.wallcontrl.util.ToastUtil;
import com.orhanobut.logger.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by alex on 2017/4/14.
 */

public class SocketService extends Service {

    private final static int CONN = 0x11;
    private final static int CLOSE = 0x12;
    private final static int ERROR = 0x13;

    private String TAG = "SocketService";
    private Socket mSocket;
    private int mSocketPort;
    private String mSocketIp;
    private boolean isConnApp = false;
    private Boolean isConn = false;
    private InetSocketAddress mSocketAddress;
    private InputStream mSocketReader;
    private OutputStream mSocketWriter;
    private WeakHandler mWeakHandler;
    private SocketSerrviceBroadCast mSocketSerrviceBroadCast;
    //    private Handler mToastHandler;
    private Thread mConnThread;
    private Thread mCloseThread;
    private boolean flag = true;
    private Handler mConnHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case CONN:
                    ToastUtil.showShortMessage("连接服务器成功!");
                    break;
                case CLOSE:
                    ToastUtil.showShortMessage("已关闭连接!");
                    break;
                case ERROR:
                    ToastUtil.showShortMessage("连接服务器失败，请检查ip地址及服务器状态!");
                    break;
            }
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mSocket = new Socket();
        mWeakHandler = new WeakHandler();
        mSocketSerrviceBroadCast = new SocketSerrviceBroadCast();
        registerBroadcast();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregistBroadcast();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return super.onStartCommand(intent, flags, startId);
    }

    public class SocketSerrviceBroadCast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            LogUtil.e("SocketSerrvice:" + "接收到消息");
            String action = intent.getAction();
            switch (action) {
                case ConstUtils.ACTION_CONN:
                    mSocketIp = intent.getStringExtra(ConstUtils.BROADCAST_IP);
                    mSocketPort = intent.getIntExtra(ConstUtils.BROADCAST_PORT, 0000);
                    isConnApp = intent.getBooleanExtra(ConstUtils.BROADCAST_ISCONN, false);
                    LogUtil.e("SocketSerrvice:连接操作" + "接收到消息");
                    LogUtil.e("SocketSerrvice:ip地址" + mSocketIp);
                    LogUtil.e("SocketSerrvice:端口" + mSocketPort + "");
                    LogUtil.e("SocketSerrvice:是否连接" + isConnApp + "");
                    isConn = isConnApp;
                    if (!mSocket.isConnected()) {
                        mConnThread = new Thread(mConnRunnable);
                        mConnThread.start();
                    }
                    break;

                case ConstUtils.ACTION_SEND:
                    byte[] bytes = intent.getByteArrayExtra(ConstUtils.BROADCAST_BUFF);
                    sendMsgToSocket(bytes);
                    break;

                case ConstUtils.ACTION_CLOSE:
                    isConn = false;
                    mCloseThread = new Thread(mCloseRunnable);
                    mCloseThread.start();
                    break;

            }

        }
    }

    private Runnable mConnRunnable = new Runnable() {
        @Override
        public void run() {
            while (isConn) {
                try {
                    connSocekt();
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
    };

    private Runnable mCloseRunnable = new Runnable() {
        @Override
        public void run() {
            closeSocket();
        }
    };

    void connSocekt() {
        if (mSocket.isConnected()) return;
        Logger.e("尝试连接!");
        if (mSocket.isClosed() || !mSocket.isConnected()) mSocket = new Socket();
        try {
            mSocket.setSoLinger(true, 2);
            mSocketAddress = new InetSocketAddress(mSocketIp, mSocketPort);
            mSocket.connect(mSocketAddress, 10000);
            mSocketReader = mSocket.getInputStream();
            mSocketWriter = mSocket.getOutputStream();

            Message connMsg = new Message();
            connMsg.what = CONN;
            mConnHandler.sendMessage(connMsg);
            Logger.e(TAG, "连接服务器成功!");
//            ToastUtil.showShortMessage("连接服务器成功！");
        } catch (IOException e) {
            e.printStackTrace();
            Message errorMsg = new Message();
            errorMsg.what = ERROR;
            mConnHandler.sendMessage(errorMsg);
            Logger.e("连接服务器失败，请检查ip地址及服务器状态！");

        }
    }

    void closeSocket() {
        try {
            if (mSocket != null && mSocket.isConnected()) {
                mSocket.shutdownInput();
                mSocket.shutdownOutput();
                mSocket.close();
                Message closeMsg = new Message();
                closeMsg.what = CLOSE;
                mConnHandler.sendMessage(closeMsg);
                Logger.e("已断开连接!");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void sendMsgToSocket(byte[] bytes) {
        try {
            if (mSocket != null && mSocket.isConnected()) {
                mSocketWriter.write(bytes);
                mSocketWriter.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void registerBroadcast() {
        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(ConstUtils.ACTION_CONN);
        mFilter.addAction(ConstUtils.ACTION_CLOSE);
        mFilter.addAction(ConstUtils.ACTION_SEND);
        registerReceiver(mSocketSerrviceBroadCast, mFilter);
    }


    void unregistBroadcast() {
        if (mSocketSerrviceBroadCast != null) {
            unregisterReceiver(mSocketSerrviceBroadCast);
            mSocketSerrviceBroadCast = null;
        }

    }


}
