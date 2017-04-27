package com.hc.wallcontrl.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.badoo.mobile.util.WeakHandler;
import com.hc.wallcontrl.util.ConstUtils;
import com.hc.wallcontrl.util.LogUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by alex on 2017/4/14.
 */

public class SocketService extends Service {

    private Socket mSocket;
    private int mSocketPort;
    private String mSocketIp;
    private Boolean isConn;
    private InetSocketAddress mSocketAddress;
    private InputStream mSocketReader;
    private OutputStream mSocketWriter;
    private WeakHandler mWeakHandler;
    private SocketSerrviceBroadCast mSocketSerrviceBroadCast;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mSocket=new Socket();
        mWeakHandler=new WeakHandler();
        mSocketSerrviceBroadCast=new SocketSerrviceBroadCast();
        registerBroadcast();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return super.onStartCommand(intent, flags, startId);
    }

    public class SocketSerrviceBroadCast extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            LogUtil.e("SocketSerrvice:ip地址","接收到消息");
            String action=intent.getAction();
            switch (action){
                case ConstUtils.ACTION_CONN:
                    mSocketIp=intent.getStringExtra("ip");
                    mSocketPort=intent.getIntExtra("port",0000);
                    LogUtil.e("SocketSerrvice:ip地址","接收到消息");
                    LogUtil.e("SocketSerrvice:ip地址",mSocketIp);
                    LogUtil.e("SocketSerrvice:端口",mSocketPort+"");
                    new  Thread(mConnRunnable).start();
                    break;

                case ConstUtils.ACTION_SEND:
                    byte[] bytes=new byte[2];
                    sendMsgToSocket(bytes);
                    break;

                case ConstUtils.ACTION_CLOSE:
                    closeSocket();
                    break;

            }

        }
    }

    private Runnable mConnRunnable=new Runnable() {
        @Override
        public void run() {
            connSocekt();
        }
    };

    void connSocekt(){
        try {
            mSocket.setSoLinger(true,2);
            mSocket.setSoTimeout(2000);
            mSocketAddress=new InetSocketAddress(mSocketIp,mSocketPort);
            mSocket.connect(mSocketAddress);
            isConn=true;
            mSocketReader=mSocket.getInputStream();
            mSocketWriter=mSocket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void closeSocket(){
        try {
            if (mSocket!=null){
                isConn=false;
                mSocket.shutdownInput();
                mSocket.shutdownOutput();
                mSocketWriter.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void sendMsgToSocket(byte[] bytes){
        try {
            mSocketWriter.write(bytes);
            mSocketWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void registerBroadcast(){
        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(ConstUtils.ACTION_CONN);
        mFilter.addAction(ConstUtils.ACTION_CLOSE);
        mFilter.addAction(ConstUtils.ACTION_SEND);

//        mFilter.addAction("ACTION_SEEKBAR");
//
//        mFilter.addAction("ACTION_STYLE");

        registerReceiver(mSocketSerrviceBroadCast, mFilter);
    }


}
