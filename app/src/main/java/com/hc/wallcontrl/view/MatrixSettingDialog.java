package com.hc.wallcontrl.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.hc.wallcontrl.R;

import java.util.Arrays;
import java.util.List;

/**
 * Created by alex on 2017/4/24.
 */

public class MatrixSettingDialog extends Dialog {

    private Spinner mMatrixCateSpinner;
    private Spinner mMatrixFacSpinner;
    private DropEditText mDropEditStream;
    private EditText mInputQuanEtv;
    private EditText mDelayTimeEtv;
    private EditText mAddrEtv;
    private Button mCancelBtn;
    private Button mConfirmBtn;

    private Context mContext;

    private int mIntputQuan;
    private int mDelayTime;
    private int mAddr;
    private String mMatrixFactory;
    private String mMatrixCategory;
    private List<String> mCateList;
    private List<String> mFacList;

    private onCancelClickListener mOnCancelClickListener;
    private onConfirmClickListener mOnConfirmClickListener;

    private OnCateSpinnerItemSelectedListener mOnCateSpinnerItemSelectedListener;
    private OnFacSpinnerItemSelectedListener mOnFacSpinnerItemSelectedListener;


    public int getIntputQuan() {
        return mIntputQuan;
    }

    public void setIntputQuan(int intputQuan) {
        mIntputQuan = intputQuan;
    }

    public int getDelayTime() {
        return mDelayTime;
    }

    public void setDelayTime(int delayTime) {
        mDelayTime = delayTime;
    }

    public int getAddr() {
        return mAddr;
    }

    public void setAddr(int addr) {
        mAddr = addr;
    }

    public String getMatrixFactory() {
        return mMatrixFactory;
    }

    public void setMatrixFactory(String matrixFactory) {
        mMatrixFactory = matrixFactory;
    }

    public String getMatrixCategory() {
        return mMatrixCategory;
    }

    public void setMatrixCategory(String matrixCategory) {
        mMatrixCategory = matrixCategory;
    }

    public MatrixSettingDialog(@NonNull Context context) {
        super(context);
        mContext = context;
    }

    public MatrixSettingDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
        mContext = context;
    }

    public Spinner getMatrixCateSpinner() {
        return mMatrixCateSpinner;
    }

    public void setMatrixCateSpinner(Spinner matrixCateSpinner) {
        mMatrixCateSpinner = matrixCateSpinner;
    }

    public Spinner getMatrixFacSpinner() {
        return mMatrixFacSpinner;
    }

    public void setMatrixFacSpinner(Spinner matrixFacSpinner) {
        mMatrixFacSpinner = matrixFacSpinner;
    }

    public EditText getInputQuanEtv() {
        return mInputQuanEtv;
    }

    public void setInputQuanEtv(EditText inputQuanEtv) {
        mInputQuanEtv = inputQuanEtv;
    }

    public EditText getDelayTimeEtv() {
        return mDelayTimeEtv;
    }

    public EditText getAddrEtv() {
        return mAddrEtv;
    }

    public void setAddrEtv(EditText addrEtv) {
        mAddrEtv = addrEtv;
    }

    public void setDelayTimeEtv(EditText delayTimeEtv) {
        mDelayTimeEtv = delayTimeEtv;
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
                if (mOnCancelClickListener != null) {
                    mOnCancelClickListener.onClick();
                }
            }
        });

        mConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIntputQuan = Integer.parseInt(mInputQuanEtv.getText().toString());
                mDelayTime = Integer.parseInt(mDelayTimeEtv.getText().toString());
                mAddr = Integer.parseInt(mAddrEtv.getText().toString());
                mMatrixCategory = mCateList.get(mMatrixCateSpinner.getSelectedItemPosition());
                mMatrixFactory = mFacList.get(mMatrixFacSpinner.getSelectedItemPosition());
                if (mOnConfirmClickListener != null) {
                    mOnConfirmClickListener.onClick();
                }
            }
        });

        mMatrixCateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mMatrixCategory = mCateList.get(position);
//               mOnCateSpinnerItemSelectedListener.onItemSelected(parent,view,position,id);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mMatrixCategory = mCateList.get(0);
//                mOnCateSpinnerItemSelectedListener.onNothingSelected(parent);
            }
        });

        mMatrixFacSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mMatrixFactory = mFacList.get(position);
//                mOnFacSpinnerItemSelectedListener.onItemSelected(parent,view,position,id);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mMatrixCategory = mCateList.get(0);
//                mOnFacSpinnerItemSelectedListener.onNothingSelected(parent);
            }
        });
    }

    private void initData() {
        String[] cateArray = mContext.getResources().getStringArray(R.array.matrix_category);
        String[] facArray = mContext.getResources().getStringArray(R.array.matrix_fac);
        mCateList = Arrays.asList(cateArray);
        mFacList = Arrays.asList(facArray);

        ArrayAdapter mCateAdapter = new ArrayAdapter(mContext, R.layout.spinner_list_item, cateArray);
        ArrayAdapter mFacAdapter = new ArrayAdapter(mContext, R.layout.spinner_list_item, facArray);
        mMatrixCateSpinner.setAdapter(mCateAdapter);
        mMatrixFacSpinner.setAdapter(mFacAdapter);

        ArrayAdapter adapter = new ArrayAdapter<String>(mContext, R.layout.spinner_list_item, cateArray);
        mDropEditStream.setAdapter(adapter);

    }

    private void initView() {
        mMatrixCateSpinner = (Spinner) findViewById(R.id.spinner_matrix_category);
        mMatrixFacSpinner = (Spinner) findViewById(R.id.spinner_matrix_factory);
        mDropEditStream = (DropEditText) findViewById(R.id.dropetv_matrix_stream);
        mInputQuanEtv = (EditText) findViewById(R.id.etv_input_quantity);
        mDelayTimeEtv = (EditText) findViewById(R.id.etv_delay_time);
        mAddrEtv = (EditText) findViewById(R.id.etv_addr);
        mCancelBtn = (Button) findViewById(R.id.btn_cancel);
        mConfirmBtn = (Button) findViewById(R.id.btn_confirm);
    }

    public void setOnCancelClickListener(onCancelClickListener onCancelClickListener) {
        this.mOnCancelClickListener = onCancelClickListener;
    }

    public void setOnConfirmClickListener(onConfirmClickListener onConfirmClickListener) {
        this.mOnConfirmClickListener = onConfirmClickListener;
    }

    public void setOnCateSpinnerItemSelectedListener(OnCateSpinnerItemSelectedListener onCateSpinnerItemSelectedListener) {
        this.mOnCateSpinnerItemSelectedListener = onCateSpinnerItemSelectedListener;
    }

    public void setOnFacSpinnerItemSelectedListener(OnFacSpinnerItemSelectedListener onFacSpinnerItemSelectedListener) {
        this.mOnFacSpinnerItemSelectedListener = onFacSpinnerItemSelectedListener;
    }

    public interface onCancelClickListener {
        public void onClick();
    }

    public interface onConfirmClickListener {
        public void onClick();
    }

    public interface OnFacSpinnerItemSelectedListener {
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id);

        public void onNothingSelected(AdapterView<?> parent);
    }

    public interface OnCateSpinnerItemSelectedListener {
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id);

        public void onNothingSelected(AdapterView<?> parent);
    }
}
