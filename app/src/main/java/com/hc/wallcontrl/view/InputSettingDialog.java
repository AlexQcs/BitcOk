package com.hc.wallcontrl.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;

import com.hc.wallcontrl.R;

import java.util.List;

/**
 * Created by alex on 2017/5/3.
 */

public class InputSettingDialog extends Dialog {

    private Context mContext;

    private Spinner mInputSourceSpinner;//信号源
    private Spinner mMatrixInputSpinner;
    private Spinner mMatrixSwitchSpinner;

    private Button mCancelBtn;
    private Button mConfirmBtn;

    private String mInputSource;
    private String mMatrixSwitch;
    private String mMatrixInput;

    private List<String> mInputSourceList;
    private List<String> mMatrixSwitchList;
    private List<String> mmMatrixInputList;

    private onCancelClickListener mOnCancelClickListener;
    private onConfirmClickListener mOnConfirmClickListener;

    private OnInputSourceSpinnerItemSelectedListener mOnInputSourceSpinnerItemSelectedListener;
    private OnMatrixInputSpinnerItemSelectedListener mOnMatrixInputSpinnerItemSelectedListener;
    private OnMatrixSwitchSpinnerItemSelectedListener mMatrixSwitchSpinnerItemSelectedListener;

    public String getInputSource() {
        return mInputSource;
    }

    public void setInputSource(String inputSource) {
        mInputSource = inputSource;
    }

    public String getMatrixSwitch() {
        return mMatrixSwitch;
    }

    public void setMatrixSwitch(String matrixSwitch) {
        mMatrixSwitch = matrixSwitch;
    }

    public String getMatrixInput() {
        return mMatrixInput;
    }

    public void setMatrixInput(String matrixInput) {
        mMatrixInput = matrixInput;
    }

    public List<String> getInputSourceList() {
        return mInputSourceList;
    }

    public void setInputSourceList(List<String> inputSourceList) {
        mInputSourceList = inputSourceList;
    }

    public List<String> getMatrixSwitchList() {
        return mMatrixSwitchList;
    }

    public void setMatrixSwitchList(List<String> matrixSwitchList) {
        mMatrixSwitchList = matrixSwitchList;
    }

    public List<String> getMmMatrixInputList() {
        return mmMatrixInputList;
    }

    public void setMmMatrixInputList(List<String> mmMatrixInputList) {
        this.mmMatrixInputList = mmMatrixInputList;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_setting_input);
        setCanceledOnTouchOutside(false);

        initView();
        initData();
        initEvent();
    }

    private void initEvent() {

    }

    private void initData() {
//        String[] inputSource=mContext.getResources().getStringArray(R.array.)
    }

    private void initView() {
        mInputSourceSpinner = (Spinner) findViewById(R.id.spinner_input_source);
        mMatrixInputSpinner = (Spinner) findViewById(R.id.spinner_matrix_input);
        mMatrixSwitchSpinner = (Spinner) findViewById(R.id.spinner_matrix_switch);
        mCancelBtn = (Button) findViewById(R.id.btn_cancel);
        mConfirmBtn = (Button) findViewById(R.id.btn_confirm);
    }

    public InputSettingDialog(@NonNull Context context) {
        super(context);
        mContext = context;
    }

    public InputSettingDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
        mContext = context;
    }

    protected InputSettingDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public interface onCancelClickListener {
        public void onClick();
    }

    public interface onConfirmClickListener {
        public void onClick();
    }

    public void setOnCancelClickListener(onCancelClickListener onCancelClickListener) {
        this.mOnCancelClickListener = onCancelClickListener;
    }

    public void setOnConfirmClickListener(onConfirmClickListener onConfirmClickListener) {
        this.mOnConfirmClickListener = onConfirmClickListener;
    }

    public interface OnInputSourceSpinnerItemSelectedListener {
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id);

        public void onNothingSelected(AdapterView<?> parent);
    }

    public interface OnMatrixInputSpinnerItemSelectedListener {
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id);

        public void onNothingSelected(AdapterView<?> parent);
    }

    public interface OnMatrixSwitchSpinnerItemSelectedListener {
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id);

        public void onNothingSelected(AdapterView<?> parent);
    }
}
