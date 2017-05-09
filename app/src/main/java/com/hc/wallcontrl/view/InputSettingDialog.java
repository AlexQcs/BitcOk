package com.hc.wallcontrl.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;

import com.hc.wallcontrl.R;

import java.util.Arrays;
import java.util.List;

/**
 * Created by alex on 2017/5/3.
 */

public class InputSettingDialog extends Dialog {

    private Context mContext;

    private Spinner mInputSourceSpinner;//信号源
    private Spinner mMatrixInputSpinner;
    private Spinner mMatrixSwitchSpinner;
    private CheckBox mSingleScreenBox;
    private CheckBox mUseMatrixBox;

    private Button mCancelBtn;//取消按钮
    private Button mConfirmBtn;//确定按钮

    private String mInputSource;//信号源
    private String mMatrixSwitch;//矩阵切换类型
    private String mMatrixInput;//矩阵输入
    private boolean mIsSingleScreen;//单屏模式
    private boolean mIsUseMatrix;//使用矩阵


    private List<String> mInputSourceList;//信号源
    private List<String> mMatrixSwitchList;//矩阵切换类型
    private List<String> mMatrixInputList;//矩阵输入

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

    public List<String> getMatrixInputList() {
        return mMatrixInputList;
    }

    public void setMatrixInputList(List<String> matrixInputList) {
        mMatrixInputList = matrixInputList;
    }


    public boolean isSingleScreen() {
        return mIsSingleScreen;
    }

    public void setSingleScreen(boolean singleScreen) {
        mIsSingleScreen = singleScreen;
    }

    public boolean isUseMatrix() {
        return mIsUseMatrix;
    }

    public void setUseMatrix(boolean useMatrix) {
        mIsUseMatrix = useMatrix;
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
        mCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnCancelClickListener != null) {
                    mOnCancelClickListener.onClick();
                }
            }
        });
        mConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**String mInputSource;//信号源
                 String mMatrixSwitch;//矩阵切换类型
                 String mMatrixInput;//矩阵输入
                 boolean mIsSingleScreen;//单屏模式
                 boolean mIsUseMatrix;//使用矩阵
                 */
                mInputSource=mInputSourceList.get(mInputSourceSpinner.getSelectedItemPosition());
                mMatrixSwitch=mMatrixSwitchList.get(mMatrixSwitchSpinner.getSelectedItemPosition());
                mMatrixInput=mMatrixInputList.get(mMatrixInputSpinner.getSelectedItemPosition());
                mIsSingleScreen=mSingleScreenBox.isChecked();
                mIsUseMatrix=mUseMatrixBox.isChecked();
                if (mOnConfirmClickListener!=null){
                    mOnConfirmClickListener.onClick();
                }

            }
        });
        mUseMatrixBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    mMatrixInputSpinner.setVisibility(View.VISIBLE);
                }else {
                    mMatrixInputSpinner.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    private void initData() {
//        String[] inputSource=mContext.getResources().getStringArray(R.array.)
        String[] inputSrcArray = mContext.getResources().getStringArray(R.array.matrix_category);
        String[] matrixInputArray = mContext.getResources().getStringArray(R.array.matrix_input);
        String[] matrixSwitchArray = mContext.getResources().getStringArray(R.array.matrix_switch);

        mInputSourceList = Arrays.asList(inputSrcArray);
        mMatrixInputList = Arrays.asList(matrixInputArray);
        mMatrixSwitchList = Arrays.asList(matrixSwitchArray);

        ArrayAdapter inputSrcAdapter = new ArrayAdapter(mContext, R.layout.spinner_list_item, inputSrcArray);
        ArrayAdapter matrixInputAdapter = new ArrayAdapter(mContext, R.layout.spinner_list_item, matrixInputArray);
        ArrayAdapter matrixSwitchAdapter = new ArrayAdapter(mContext, R.layout.spinner_list_item, matrixSwitchArray);

        mInputSourceSpinner.setAdapter(inputSrcAdapter);
        mMatrixSwitchSpinner.setAdapter(matrixSwitchAdapter);
        mMatrixInputSpinner.setAdapter(matrixInputAdapter);


    }

    private void initView() {
        mInputSourceSpinner = (Spinner) findViewById(R.id.spinner_input_source);
        mMatrixInputSpinner = (Spinner) findViewById(R.id.spinner_matrix_input);
        mMatrixSwitchSpinner = (Spinner) findViewById(R.id.spinner_matrix_switch);
        mUseMatrixBox = (CheckBox) findViewById(R.id.checkbox_usematrix);
        mSingleScreenBox = (CheckBox) findViewById(R.id.checkbox_singlescreen);
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
