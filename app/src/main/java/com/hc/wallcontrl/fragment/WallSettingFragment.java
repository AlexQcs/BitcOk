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
import com.hc.wallcontrl.com.fragment.BaseFragment;
import com.hc.wallcontrl.util.ConstUtils;
import com.hc.wallcontrl.view.MyTable;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

/**
 * A simple {@link Fragment} subclass.
 */
public class WallSettingFragment extends BaseFragment {

    private SharedPreferences mPreferences = null;
    private Spinner mSpinnerRows, mSpinnerColumns;
    private Button mBtnSetTable;
    private LinearLayout mLinTable;
    private int mRows = 4, mColumns = 4;

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
        View view = inflateView;
        Observable.just(view)
                .map(inflateView -> Bitmap.createBitmap(inflateView.getWidth(), inflateView.getHeight(), Bitmap.Config.ARGB_8888))
                .map(bitmap -> new Canvas(bitmap))
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(canvas -> inflateView.draw(canvas));
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
                setLinTable();
            }
        });
    }

    void setLinTable() {
        getRowsColumns();
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
        mPreferences = mContext.getSharedPreferences(ConstUtils.SHAREDPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putInt("rows", mRows);
        editor.putInt("columns", mColumns);
        editor.commit();
    }


    @Override
    public void initData() {

        mPreferences = mContext.getSharedPreferences(ConstUtils.SHAREDPREFERENCES, Context.MODE_PRIVATE);
        if (mPreferences != null) {
            mRows = mPreferences.getInt("rows", 4);
            mColumns = mPreferences.getInt("columns", 4);
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