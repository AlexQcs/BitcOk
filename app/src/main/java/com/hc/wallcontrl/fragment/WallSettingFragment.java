package com.hc.wallcontrl.fragment;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.hc.wallcontrl.R;
import com.hc.wallcontrl.bean.ScreenInputBean;
import com.hc.wallcontrl.bean.ScreenMatrixBean;
import com.hc.wallcontrl.bean.ScreenOutputBean;
import com.hc.wallcontrl.com.fragment.BaseFragment;
import com.hc.wallcontrl.util.ConstUtils;
import com.hc.wallcontrl.util.PrefrenceUtils;
import com.hc.wallcontrl.view.MyTable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;

/**
 * A simple {@link Fragment} subclass.
 */
public class WallSettingFragment extends BaseFragment {

    private SharedPreferences mPreferences = null;
    private Spinner mSpinnerRows, mSpinnerColumns;
    private Button mBtnSetTable;
    private LinearLayout mLinTable;
    private int mRows = 4, mColumns = 4;
    private ArrayList<ScreenOutputBean> mListScreenOutput;
    private ArrayList<ScreenInputBean> mListScreenInput;
    private List<ScreenMatrixBean> mListMatrixBean;
    private MyTable mMyTable;

    private Context mContext;
    

    private Bitmap bitmap;

    public static WallSettingFragment newInstance() {
        return new WallSettingFragment();
    }


    @Override
    public int getCreateViewLayoutId() {
        return R.layout.fragment_wall_setting;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = getActivity();
    }

    @Override
    public void onPause() {
        super.onPause();

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
    public void initView(View inflateView, Bundle savedInstanceState) {
        mSpinnerColumns = (Spinner) inflateView.findViewById(R.id.spinner_columns);
        mSpinnerRows = (Spinner) inflateView.findViewById(R.id.spinner_rows);
        mBtnSetTable = (Button) inflateView.findViewById(R.id.btn_settable);
        mLinTable = (LinearLayout) inflateView.findViewById(R.id.lin_table);

        ArrayAdapter adapterRsum = ArrayAdapter.createFromResource(mContext, com.hc.wallcontrl.R.array.ListArray, android.R.layout.simple_spinner_item);
        mSpinnerColumns.setAdapter(adapterRsum);
        mSpinnerColumns.setOnItemSelectedListener(new MySpinnerSeltListener());
        mSpinnerColumns.setVisibility(View.VISIBLE);
        mSpinnerColumns.setSelection(mColumns - 1);

        ArrayAdapter adapterCsum = ArrayAdapter.createFromResource(mContext, com.hc.wallcontrl.R.array.ListArray, android.R.layout.simple_spinner_item);
        mSpinnerRows.setAdapter(adapterCsum);
        mSpinnerRows.setOnItemSelectedListener(new MySpinnerSeltListener());
        mSpinnerRows.setVisibility(View.VISIBLE);
        mSpinnerRows.setSelection(mRows - 1);
        setLinTable();
        mBtnSetTable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getRowsColumns();
                setLinTable();
            }
        });
    }

    void setLinTable() {
        mLinTable.removeAllViews();
        mMyTable = new MyTable(mContext, mLinTable.getWidth(), mLinTable.getHeight());
        Log.e("TableHeight:", mLinTable.getHeight() + "");

        mMyTable.mySetRows(mRows);
        mMyTable.mySetCols(mColumns);

        mMyTable.LoadTable();
        mLinTable.addView(mMyTable);

    }

    void getRowsColumns() {
        mRows = mSpinnerRows.getSelectedItemPosition() + 1;
        mColumns = mSpinnerColumns.getSelectedItemPosition() + 1;
        mListScreenOutput=new ArrayList<>();
        mListScreenInput=new ArrayList<>();
        for (int i = 0; i < mRows; i++) {
            for (int j = 0; j < mColumns; j++) {
                ScreenOutputBean screenMatrixBean=new ScreenOutputBean();
                ScreenInputBean screenInputBean=new ScreenInputBean();
                screenMatrixBean.setColumn(j+1);
                screenMatrixBean.setRow(i+1);
                screenMatrixBean.setMatrixOutputStream(mColumns*i+j+1);

                screenInputBean.setColumn(j+1);
                screenInputBean.setRow(i+1);

                mListScreenOutput.add(screenMatrixBean);
                mListScreenInput.add(screenInputBean);
                screenMatrixBean=null;
                screenInputBean=null;
            }
        }

        if (mListMatrixBean!=null&&mListMatrixBean.size()!=0){
            for (int i = 0; i < mListMatrixBean.size(); i++) {
                mListMatrixBean.get(i).setListOutputScreen(mListScreenOutput);
            }
        }

        mPreferences = mContext.getSharedPreferences(ConstUtils.SHAREDPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mPreferences.edit();
        try {
            String listOutputString= PrefrenceUtils.SceneList2String(mListScreenOutput);
            editor.putString(ConstUtils.SP_SCREEN_OUTPUT_LIST, listOutputString);
            String listInputString=PrefrenceUtils.SceneList2String(mListScreenInput);
            editor.putString(ConstUtils.SP_SCREEN_INPUT_LIST,listInputString);
            if (mListMatrixBean!=null&&mListMatrixBean.size()!=0){
                String listMatrixString=PrefrenceUtils.SceneList2String(mListMatrixBean);
                editor.putString(ConstUtils.SP_MATRIX_LIST, listMatrixString);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        editor.putInt(ConstUtils.SP_ROWS, mRows);
        editor.putInt(ConstUtils.SP_COLUMNS, mColumns);

        editor.commit();
    }


    @Override
    public void initData() {

        mPreferences = mContext.getSharedPreferences(ConstUtils.SHAREDPREFERENCES, Context.MODE_PRIVATE);
        if (mPreferences != null) {
            mRows = mPreferences.getInt(ConstUtils.SP_ROWS, 4);
            mColumns = mPreferences.getInt(ConstUtils.SP_COLUMNS, 4);
            try {
                String listMatrixStr=mPreferences.getString(ConstUtils.SP_MATRIX_LIST,"");
                mListMatrixBean=PrefrenceUtils.String2SceneList(listMatrixStr);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public Bitmap getBitmap() {
        return bitmap;
    }

    private class MySpinnerSeltListener implements AdapterView.OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> arg0, View v, int arg2, long arg3) {
            TextView tv = (TextView) v;
            tv.setTextColor(getResources().getColor(com.hc.wallcontrl.R.color.white));
            tv.setGravity(Gravity.CENTER);
        }

        public void onNothingSelected(AdapterView<?> arg0) {

        }
    }


}
