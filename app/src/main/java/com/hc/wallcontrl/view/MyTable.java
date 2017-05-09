package com.hc.wallcontrl.view;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.hc.wallcontrl.R;
import com.hc.wallcontrl.adapter.MyTableAdapter;
import com.hc.wallcontrl.bean.ScreenInputBean;
import com.hc.wallcontrl.bean.ScreenMatrixBean;

import java.util.ArrayList;

/**
 * Created by John on 2015-07-16.
 */
public class MyTable extends LinearLayout {

    protected GridView myTable;
    protected MyTableAdapter myTableAdapter;//适配器
    protected ArrayList<String> myTableSource;//数据源
    protected int[] myTableColors;//数据源


    protected Context myContext;
    //protected HashMap<String,TextView> map = new HashMap<>();

    protected int TableRows = 3;//总行数
    protected int TableCols = 3;//总列数
    protected int TableHeight = 500;
    protected int TableWidth = 500;
    protected Cursor curTable;//每行的数据
    //Selected Area
    protected boolean bStartSlected = false;
    protected int rs = 1; //行数开始
    protected int cs = 1; //列数开始
    protected int re = 1; //行数结束
    protected int ce = 1; //列数结束


    protected OnTableClickListener clickListener;//整个分页控件被点击时的回调函数
    protected OnTableTouchListener touchListener;//分页触摸回调函数

    public ArrayList<ScreenMatrixBean> getListScreenBeen() {
        return mListScreenBeen;
    }

    public void setListScreenBeen(ArrayList<ScreenMatrixBean> listScreenBeen) {
        mListScreenBeen = listScreenBeen;
    }

    private ArrayList<ScreenMatrixBean> mListScreenBeen;

    public MyTable(Context context) {
        super(context);
        this.myContext = context;
    }

    public MyTable(Context context, final int width, final int height) {
        super(context);
        this.myContext = context;
        this.setOrientation(VERTICAL);
        //-----------------------------------------
        myTable = new GridView(context);
        ViewGroup.LayoutParams lyPram = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        myTable.setLayoutParams(lyPram);
        addView(myTable);
        //addView(myTable, new LinearLayoutCompat.LayoutParams(width, height));
        myTableSource = new ArrayList<>();
        myTableColors = new int[1024];

        TableWidth = width;
        TableHeight = height;
        //LoadTable();
    }

    public int[] myGetSeltArea() {
        int[] rtArea = {0, 0, 0, 0};
        if (bStartSlected) {
            if (rs > re) {
                int tmp = rs;
                rs = re;
                re = tmp;
            }
            if (cs > ce) {
                int tmp = cs;
                cs = ce;
                ce = tmp;
            }
            rtArea[0] = rs;
            rtArea[1] = cs;
            rtArea[2] = re;
            rtArea[3] = ce;
        }

        return rtArea;
    }

    public int myGetTableHeight() {
        return myTable.getHeight();
    }

    //设置被选中的格子颜色
    private void mySetSelectItemColor() {

        ///*
        myGetSeltArea();
        for (int r = 0; r < TableRows; r++)
            for (int c = 0; c < TableCols; c++) {
                int idx = r * TableCols + c;
                if (r >= rs - 1 && r <= re - 1 && c >= cs - 1 && c <= ce - 1)
                    myTableColors[idx] = ContextCompat.getColor(myContext, R.color.LightPrimaryColor);
                else
                    myTableColors[idx] = ContextCompat.getColor(myContext, R.color.PrimaryColor);
            }
        //*/
    }

    //返回被选中的格子
    public ArrayList<ScreenMatrixBean> getScreenMatrixSelectItemIndex() {
        ArrayList<ScreenMatrixBean> mScreenMatrixList = new ArrayList<>();
        myGetSeltArea();
        for (int r = 0; r < TableRows; r++) {
            for (int c = 0; c < TableCols; c++) {
                if (r >= rs - 1 && r <= re - 1 && c >= cs - 1 && c <= ce - 1) {
                    ScreenMatrixBean screenBean=new ScreenMatrixBean();
                    screenBean.setColumn(c+1);
                    screenBean.setRow(r+1);
                    mScreenMatrixList.add(screenBean);
                    screenBean=null;
                }
            }
        }
        return mScreenMatrixList;
    }
    
    public ArrayList<ScreenInputBean> getScreenInputSelectItemIndex(){
        ArrayList<ScreenInputBean> mScreenInputList = new ArrayList<>();
        myGetSeltArea();
        for (int r = 0; r < TableRows; r++) {
            for (int c = 0; c < TableCols; c++) {
                if (r >= rs - 1 && r <= re - 1 && c >= cs - 1 && c <= ce - 1) {
                    ScreenInputBean screenBean=new ScreenInputBean();
                    screenBean.setColumn(c+1);
                    screenBean.setRow(r+1);
                    mScreenInputList.add(screenBean);
                    screenBean=null;
                }
            }
        }
        return mScreenInputList;
    }

    public void mySetRows(int rows) {
        this.TableRows = rows;
    }

    public void mySetCols(int cols) {
        this.TableCols = cols;
    }

    public void mySetTableHeight(int height) {
        this.TableHeight = height;
    }

    /**
     * 清除所有数据
     */
    public void myRemoveAll() {
        if (this.curTable != null)
            curTable.close();
        myTableSource.clear();
        myTable.deferNotifyDataSetChanged();
    }

    /**
     * 导入数据
     */
    public void LoadTable() {
        if (curTable != null)
            curTable.close();

        //map.clear();
        myTable.setNumColumns(TableCols);//表现为表格的关键
        for (int r = 0; r < TableRows; r++) {
            for (int c = 0; c < TableCols; c++) {
                int idx = r * TableCols + c;
                String strValue = String.valueOf(r + 1) + "-" + String.valueOf(c + 1);
                myTableSource.add(strValue);
                myTableColors[idx] = ContextCompat.getColor(myContext, R.color.PrimaryColor);
            }
        }

        //添加并显示
        myTableAdapter = new MyTableAdapter(myContext, myTableSource, myTableColors);
        ;
        myTable.setAdapter(myTableAdapter);

        TableHeight = myTable.getHeight();
        Log.e("Table.Height1234:", String.valueOf(TableHeight));

        myTable.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int y = position / TableCols;
                int x = position % TableCols;

                view.setBackgroundColor(Color.RED);

                if (clickListener != null) {//数据被点中
                    clickListener.onTableClickListener(x, y, curTable);
                }
            }
        });
        myTable.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                GridView gv = (GridView) v;
                //Log.e("InnerClassClick", "touch" );
                String str = String.valueOf(event.getX()) + "-" + String.valueOf(event.getY());
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        bStartSlected = false;
                        rs = (int) (event.getY() / (myTable.getHeight() / TableRows)) + 1;
                        cs = (int) (event.getX() / (myTable.getWidth() / TableCols)) + 1;
                        //str = String.valueOf(rs) +"-"+String.valueOf(cs);
                        //Log.e("InnerClassTouch", "Down:" + str);
                    }
                    break;
                    case MotionEvent.ACTION_MOVE: {
                        //Log.e("InnerClassTouch", "Move" );
                    }
                    break;
                    case MotionEvent.ACTION_UP: {
                        bStartSlected = true;
                        float upX = event.getX();
                        float upY = event.getY();
                        if (upX > myTable.getWidth())
                            upX = myTable.getWidth() - 1;
                        else if (upX <= 0)
                            upX = 1;
                        if (upY > myTable.getHeight())
                            upY = myTable.getHeight() - 1;
                        else if (upY <= 0)
                            upY = 1;


                        re = (int) (upY / (myTable.getHeight() / TableRows)) + 1;
                        ce = (int) (upX / (myTable.getWidth() / TableCols)) + 1;
                        //str =  String.valueOf(re) +"-"+String.valueOf(ce);
                        //Log.e("InnerClassTouch", "Up:" + str);
                        mySetSelectItemColor();
//                        mListScreenBeen=getSelectItemIndex();
                        //添加并显示
                        myTableAdapter = new MyTableAdapter(myContext, myTableSource, myTableColors);
                        myTable.setAdapter(myTableAdapter);
                    }
                    break;
                }
                if (touchListener != null)
                    touchListener.onTableTouchListener(v, event);
                return false;
            }
        });
        //========================
        myTable.deferNotifyDataSetChanged();
    }

    /**
     * 表格被点时的回调函数
     */
    public void setTableOnClickListener(OnTableClickListener click) {
        this.clickListener = click;
    }

    public interface OnTableClickListener {
        void onTableClickListener(int x, int y, Cursor c);
    }


    /**
     * 表格被摸时的回调函数
     */
    public void setTableOnTouchListener(OnTableTouchListener click) {
        this.touchListener = click;
    }

    public interface OnTableTouchListener {
        void onTableTouchListener(View v, MotionEvent event);
    }

}
