package com.hc.wallcontrl.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;

import com.hc.wallcontrl.R;
import com.hc.wallcontrl.view.nicespinner.NiceSpinner;

/**
 * Created by alex on 2017/4/24.
 */

public class MatrixSettingDialog extends Dialog {

    private NiceSpinner mMatrixCateSpinner;
    private NiceSpinner mMatrixFacSpinner;
    private EditText mInputQuanEtv;
    private EditText mDelayTimeEtv;
    private Button mCancelBtn;
    private Button mConfirmBtn;

    private int mIntputQuan;
    private int mDelayTime;
    private String mMatrixFactory;
    private String mMatrixCategory;


    private onCancelClickListener mOnCancelClickListener;
    private onConfirmClickListener mOnConfirmClickListener;

    private OnCateSpinnerItemSelectedListener mOnCateSpinnerItemSelectedListener;
    private OnFacSpinnerItemSelectedListener mOnFacSpinnerItemSelectedListener;


    public MatrixSettingDialog(@NonNull Context context) {
        super(context);
    }

    public MatrixSettingDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_setting_matrix);
        setCanceledOnTouchOutside(false);
        
        initView();
        initData();
        initEvent();
        
    }

    private void initEvent() {
        mCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnCancelClickListener!=null){
                    mOnCancelClickListener.onClick();
                }
            }
        });

        mConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnConfirmClickListener!=null){
                    mOnConfirmClickListener.onClick();
                }
            }
        });

        mMatrixCateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mOnCateSpinnerItemSelectedListener.onItemSelected(parent,view,position,id);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mOnCateSpinnerItemSelectedListener.onNothingSelected(parent);
            }
        });

        mMatrixFacSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mOnFacSpinnerItemSelectedListener.onItemSelected(parent,view,position,id);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mOnFacSpinnerItemSelectedListener.onNothingSelected(parent);
            }
        });
    }

    private void initData() {
    }

    private void initView() {
        mMatrixCateSpinner= (NiceSpinner)findViewById(R.id.spinner_matrix_category);
        mMatrixFacSpinner= (NiceSpinner) findViewById(R.id.spinner_matrix_factory);
        mInputQuanEtv= (EditText) findViewById(R.id.etv_input_quantity);
        mDelayTimeEtv= (EditText) findViewById(R.id.etv_delay_time);
        mCancelBtn= (Button) findViewById(R.id.btn_cancel);
        mConfirmBtn= (Button) findViewById(R.id.btn_confirm);
    }

    public void setOnCancelClickListener(onCancelClickListener onCancelClickListener){
        this.mOnCancelClickListener=onCancelClickListener;
    }

    public void setOnConfirmClickListener(onConfirmClickListener onConfirmClickListener){
        this.mOnConfirmClickListener=onConfirmClickListener;
    }

    public void setOnCateSpinnerItemSelectedListener(OnCateSpinnerItemSelectedListener onCateSpinnerItemSelectedListener){
        this.mOnCateSpinnerItemSelectedListener=onCateSpinnerItemSelectedListener;
    }

    public void setOnFacSpinnerItemSelectedListener(OnFacSpinnerItemSelectedListener onFacSpinnerItemSelectedListener){
        this.mOnFacSpinnerItemSelectedListener=onFacSpinnerItemSelectedListener;
    }

    public interface onCancelClickListener{
        public void onClick();
    }

    public interface onConfirmClickListener{
        public void onClick();
    }

    public interface OnFacSpinnerItemSelectedListener{
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id);
        public void onNothingSelected(AdapterView<?> parent);
    }

    public interface OnCateSpinnerItemSelectedListener{
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id);
        public void onNothingSelected(AdapterView<?> parent);
    }
}
