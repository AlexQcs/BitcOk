package com.hc.wallcontrl.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.hc.wallcontrl.util.ConstUtils;

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

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mSocket=new Socket();

    }

    public class SocketSerrviceBroadCast extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            String action=intent.getAction();
            switch (action){
                case ConstUtils.ACTION_CONN:
                    mSocketIp=intent.getStringExtra("ip");
                    mSocketPort=intent.getIntExtra("port",0000);

                    break;

                case ConstUtils.ACTION_SEND:
                    break;

                case ConstUtils.ACTION_CLOSE:
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
            mSocket.close();
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

}
