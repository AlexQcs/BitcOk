package com.hc.wallcontrl.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.hc.wallcontrl.R;
import com.hc.wallcontrl.com.ClsV59Ctrl;
import com.hc.wallcontrl.com.fragment.BaseFragment;
import com.hc.wallcontrl.util.ClsCmds;
import com.hc.wallcontrl.util.ConstUtils;

import at.markushi.ui.CircleButton;
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
    private byte[] addr=new byte[4];

    private CircleButton mTurnUpBtn, mTurnDownBtn, mMuteBtn;
    private Button mSignalSourceBtn, mInfoBtn, mExitBtn, mUpBtn, mConfirmBtn, mLeftBtn, mMenuBtn, mRightBtn, mFreezeBtn, mDownBtn, mRotateBtn, mPlayBtn, mStopBtn, mPauseBtn, mTattedCodeBtn, mPositionBtn, mColorBtn;
    private Button mNum00Btn, mNum01Btn, mNum02Btn, mNum03Btn, mNum04Btn, mNum05Btn, mNum06Btn, mNum07Btn, mNum08Btn, mNum09Btn;

    private Bitmap bitmap;

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
        sp = mContext.getSharedPreferences(ConstUtils.SHAREDPREFERENCES, Context.MODE_PRIVATE);
        if (sp == null) return;
        bConnected = sp.getBoolean(ConstUtils.SP_ISCONN, bConnected);
        addr[0]=0x00;
        addr[1]=0x00;
        addr[2]= (byte) 0xff;
        addr[3]= (byte) 0xff;
    }

    @Override
    public void initView(View inflateView, Bundle savedInstanceState) {
        mTurnUpBtn = (CircleButton) inflateView.findViewById(R.id.btn_turn_up);
        mTurnDownBtn = (CircleButton) inflateView.findViewById(R.id.btn_turn_down);
        mMuteBtn = (CircleButton) inflateView.findViewById(R.id.btn_mute);
        mNum00Btn = (Button) inflateView.findViewById(R.id.btn_num_00);
        mNum01Btn = (Button) inflateView.findViewById(R.id.btn_num_01);
        mNum02Btn = (Button) inflateView.findViewById(R.id.btn_num_02);
        mNum03Btn = (Button) inflateView.findViewById(R.id.btn_num_03);
        mNum04Btn = (Button) inflateView.findViewById(R.id.btn_num_04);
        mNum05Btn = (Button) inflateView.findViewById(R.id.btn_num_05);
        mNum06Btn = (Button) inflateView.findViewById(R.id.btn_num_06);
        mNum07Btn = (Button) inflateView.findViewById(R.id.btn_num_07);
        mNum08Btn = (Button) inflateView.findViewById(R.id.btn_num_08);
        mNum09Btn = (Button) inflateView.findViewById(R.id.btn_num_09);
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
        mNum00Btn.setOnClickListener(this);
        mNum01Btn.setOnClickListener(this);
        mNum02Btn.setOnClickListener(this);
        mNum03Btn.setOnClickListener(this);
        mNum04Btn.setOnClickListener(this);
        mNum05Btn.setOnClickListener(this);
        mNum06Btn.setOnClickListener(this);
        mNum07Btn.setOnClickListener(this);
        mNum08Btn.setOnClickListener(this);
        mNum09Btn.setOnClickListener(this);
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
        switch (v.getId()) {
            case R.id.btn_turn_up:
                TestCmd(addr, ClsCmds.IrMode, ClsCmds.EmptyValue, ClsCmds.IrFWall);
                break;
            case R.id.btn_mute:
                TestCmd(addr, ClsCmds.IrMode, ClsCmds.EmptyValue, ClsCmds.IrMute);
                break;
            case R.id.btn_turn_down:
                TestCmd(addr, ClsCmds.IrMode, ClsCmds.EmptyValue, ClsCmds.IrSWall);
                break;
            case R.id.btn_play:
                TestCmd(addr, ClsCmds.IrMode, ClsCmds.EmptyValue, ClsCmds.IrPlay);
                break;
            case R.id.btn_pause:
                TestCmd(addr, ClsCmds.IrMode, ClsCmds.EmptyValue, ClsCmds.IrPause);
                break;
            case R.id.btn_stop:
                TestCmd(addr, ClsCmds.IrMode, ClsCmds.EmptyValue, ClsCmds.IrStop);
                break;
            case R.id.btn_exit:
                TestCmd(addr, ClsCmds.IrMode, ClsCmds.EmptyValue, ClsCmds.IrExit);
                break;
            case R.id.btn_up:
                TestCmd(addr, ClsCmds.IrMode, ClsCmds.EmptyValue, ClsCmds.IrTop);
                break;
            case R.id.btn_confirm:
                TestCmd(addr, ClsCmds.IrMode, ClsCmds.EmptyValue, ClsCmds.IrEnter);
                break;
            case R.id.btn_left:
                TestCmd(addr, ClsCmds.IrMode, ClsCmds.EmptyValue, ClsCmds.IrLeft);
                break;
            case R.id.btn_menu:
                TestCmd(addr, ClsCmds.IrMode, ClsCmds.EmptyValue, ClsCmds.IrMenu);
                break;
            case R.id.btn_right:
                TestCmd(addr, ClsCmds.IrMode, ClsCmds.EmptyValue, ClsCmds.IrRight);
                break;
            case R.id.btn_freeze:
                TestCmd(addr, ClsCmds.IrMode, ClsCmds.EmptyValue, ClsCmds.IrFreeze);
                break;
            case R.id.btn_down:
                TestCmd(addr, ClsCmds.IrMode, ClsCmds.EmptyValue, ClsCmds.IrBottom);
                break;
            case R.id.btn_rotate:
                TestCmd(addr, ClsCmds.IrMode, ClsCmds.EmptyValue, ClsCmds.IrTurn);
                break;
            case R.id.btn_tatted_code:
                TestCmd(addr, ClsCmds.IrMode, ClsCmds.EmptyValue, ClsCmds.IrID);
                break;
            case R.id.btn_position:
                TestCmd(addr, ClsCmds.IrMode, ClsCmds.EmptyValue, ClsCmds.IrPos);
                break;
            case R.id.btn_color:
                TestCmd(addr, ClsCmds.IrMode, ClsCmds.EmptyValue, ClsCmds.IrColor);
                break;
            case R.id.btn_signalsource:
                TestCmd(addr, ClsCmds.IrMode, ClsCmds.EmptyValue, ClsCmds.IrSource);
                break;
            case R.id.btn_num_00:
                TestCmd(addr, ClsCmds.IrMode, ClsCmds.EmptyValue, ClsCmds.Ir0);
                break;
            case R.id.btn_info:
                TestCmd(addr, ClsCmds.IrMode, ClsCmds.EmptyValue, ClsCmds.IrInfo);
                break;
            case R.id.btn_num_01:
                TestCmd(addr, ClsCmds.IrMode, ClsCmds.EmptyValue, ClsCmds.Ir1);
                break;
            case R.id.btn_num_02:
                TestCmd(addr, ClsCmds.IrMode, ClsCmds.EmptyValue, ClsCmds.Ir2);
                break;
            case R.id.btn_num_03:
                TestCmd(addr, ClsCmds.IrMode, ClsCmds.EmptyValue, ClsCmds.Ir3);
                break;
            case R.id.btn_num_04:
                TestCmd(addr, ClsCmds.IrMode, ClsCmds.EmptyValue, ClsCmds.Ir4);
                break;
            case R.id.btn_num_05:
                TestCmd(addr, ClsCmds.IrMode, ClsCmds.EmptyValue, ClsCmds.Ir5);
                break;
            case R.id.btn_num_06:
                TestCmd(addr, ClsCmds.IrMode, ClsCmds.EmptyValue, ClsCmds.Ir6);
                break;
            case R.id.btn_num_07:
                TestCmd(addr, ClsCmds.IrMode, ClsCmds.EmptyValue, ClsCmds.Ir7);
                break;
            case R.id.btn_num_08:
                TestCmd(addr, ClsCmds.IrMode, ClsCmds.EmptyValue, ClsCmds.Ir8);
                break;
            case R.id.btn_num_09:
                TestCmd(addr, ClsCmds.IrMode, ClsCmds.EmptyValue, ClsCmds.Ir9);
                break;
            default:
                break;
        }

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }
}
