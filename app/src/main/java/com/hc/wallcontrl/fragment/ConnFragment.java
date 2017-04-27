package com.hc.wallcontrl.fragment;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.hc.wallcontrl.R;
import com.hc.wallcontrl.com.fragment.BaseFragment;
import com.hc.wallcontrl.service.SocketService;
import com.hc.wallcontrl.util.ConstUtils;
import com.hc.wallcontrl.util.LogUtil;
import com.hc.wallcontrl.util.StringUtils;
import com.hc.wallcontrl.util.ToastUtil;
import com.kyleduo.switchbutton.SwitchButton;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;

/**
 * A simple {@link Fragment} subclass.
 */
public class ConnFragment extends BaseFragment implements CompoundButton.OnCheckedChangeListener {

    private Bitmap bitmap;
    private Context mContext;

    private EditText mEditIP;
    private EditText mEditPort;
    private SwitchButton mSwitchIsConn;

    private String mIPStr="10.0.0.1";
    private String mPortStr="8080";
    private boolean mIsConnected;

    private SharedPreferences mPreferences=null;

    @Override
    public int getCreateViewLayoutId() {
        return R.layout.fragment_conn;
    }

    public static ConnFragment newInstance() {
        ConnFragment connFragment = new ConnFragment();
        return connFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = getActivity();
    }

    @Override
    public void onPause() {
        super.onPause();
        mPreferences=mContext.getSharedPreferences(ConstUtils.SHAREDPREFERENCES,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mPreferences.edit();

        mIPStr=mEditIP.getText().toString().trim();
        mPortStr=mEditPort.getText().toString().trim();
        editor.putString("IP",mIPStr);
        editor.putString("Port",mPortStr);
        editor.putBoolean("isConn",mIsConnected);

        editor.commit();
    }

    @Override
    public void initView(View inflateView, Bundle savedInstanceState) {
        mEditIP = (EditText) inflateView.findViewById(R.id.edit_ip);
        mEditPort = (EditText) inflateView.findViewById(R.id.edit_port);
        mSwitchIsConn = (SwitchButton) inflateView.findViewById(R.id.switch_conn);
        mSwitchIsConn.setThumbSize(200, 200);
        mSwitchIsConn.setBackRadius(Math.min(mSwitchIsConn.getBackSizeF().x, mSwitchIsConn.getBackSizeF().y) );
        mSwitchIsConn.setBackMeasureRatio(2.4f);
        mSwitchIsConn.setAnimationDuration(500);

        mSwitchIsConn.setOnCheckedChangeListener(this);
        mSwitchIsConn.setOnCheckedChangeListener(this);


        mEditIP.setText(mIPStr);
        mEditPort.setText(mPortStr);
        mSwitchIsConn.setChecked(mIsConnected);
    }

    @Override
    public void initData() {
        mPreferences=mContext.getSharedPreferences(ConstUtils.SHAREDPREFERENCES,Context.MODE_PRIVATE);
        if (mPreferences!=null){
            mIPStr=mPreferences.getString("IP","0.0.0.0");
            mPortStr=mPreferences.getString("Port","0000");
            mIsConnected=mPreferences.getBoolean("isConn",false);
        }


    }

    @Override
    public void takeScreenShot() {
        View mView=inflateView;

        Observable.just(mView)
                .map(new Func1<View, Canvas>() {
                    @Override
                    public Canvas call(View view) {
                        Bitmap bitmap=Bitmap.createBitmap(view.getWidth(),view.getHeight(),Bitmap.Config.ARGB_8888);
                        Canvas canvas=new Canvas(bitmap);
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
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked){
            mIPStr=mEditIP.getText().toString().trim();
            mPortStr=mEditPort.getText().toString().trim();
            if (!StringUtils.checkIp(mIPStr)){
                ToastUtil.showShortMessage("请输出正确的ip地址");
                LogUtil.e("onCheckedChanged","请输出正确的ip地址");
                mSwitchIsConn.setChecked(false);
            }
            if (!StringUtils.checkPort(mPortStr)){
                ToastUtil.showShortMessage("请输入正确的端口");
                LogUtil.e("onCheckedChanged","请输入正确的端口");
                mSwitchIsConn.setChecked(false);
            }
            mIsConnected=mSwitchIsConn.isChecked();
            if (mIsConnected){
                Log.e("是否成功开启",mIsConnected+"");
                Intent intent=new Intent();
                intent.setAction(ConstUtils.ACTION_CONN);
                intent.putExtra("ip",mIPStr);
                intent.putExtra("port",Integer.parseInt(mPortStr));
                mContext.sendBroadcast(intent);
            }

        }else {
            Intent intent=new Intent(mContext, SocketService.class);
            intent.setAction(ConstUtils.ACTION_CLOSE);
            mContext.sendBroadcast(intent);
        }
    }
}
