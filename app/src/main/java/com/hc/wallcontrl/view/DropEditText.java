package com.hc.wallcontrl.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.hc.wallcontrl.R;
import com.hc.wallcontrl.adapter.DropRecyclerAdapter;

/**
 * Created by alex on 2017/4/28.
 */

public class DropEditText extends android.support.v7.widget.AppCompatEditText implements AdapterView.OnItemClickListener, PopupWindow.OnDismissListener {

    private Drawable mDrawable;//显示的图
    private PopupWindow mPopupWindow;//点击图片弹出的window对象
    private RecyclerView mPopListView;//下拉列表
    private int mDropDrawableResId;//下拉图标
    private int mRiseDrawableResId;//收起图标
    private Context mContext;
    private int mCurrentItemIndex;
    private DropRecyclerAdapter mAdapter;
    private OnListViewItemClickListener mOnListViewItemClickListener;

    public DropEditText(Context context) {
        this(context, null);
    }

    public DropEditText(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DropEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        mPopListView = new RecyclerView(context);
        mDropDrawableResId = R.drawable.ic_arrow_drop_down_black_24dp;
        mRiseDrawableResId = R.drawable.ic_arrow_drop_down_black_24dp;
        showDropDrawable();//默认显示下拉图标
        mPopListView.setLayoutManager(new LinearLayoutManager(context));

    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        if (mOnListViewItemClickListener!=null){
//            mOnListViewItemClickListener.onClick(position);
//        }
//        this.setText( mPopListView.getAdapter().getItemViewType(position).toString());
//        mAdapter.setOnListViewItemClickListener(new DropRecyclerAdapter.OnRecyclerViewItemClickListenner() {
//            @Override
//            public void onItemClick(View v, int p) {
//                v=view;
//                p=position;
//
//            }
//        });

    }

    public void dismiss(){
        mPopupWindow.dismiss();
    }

    /**
     * 我们无法直接给EditText设置点击事件，只能通过按下的位置来模拟点击事件
     * 当我们按下的位置在图标包括图标到控件右边的间距范围内均算有效
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (getCompoundDrawables()[2] != null) {
                int start = getWidth() - getTotalPaddingRight() + getPaddingRight();//起始位置
//                int start=getWidth()-20;
                int end = getWidth();//结束位置
                boolean available = (event.getX() > start) && (event.getX() < end);
                if (available) {
                    closeSoftInput();
                    showPopWindow();
                    return true;
                } else {
                    showSoftInput();
                }
            }
        }
        return super.onTouchEvent(event);
    }

    private void showPopWindow() {
        //在当前view的左下方弹出，坐标偏移量为向右移5个单位
        mPopupWindow.showAsDropDown(this, 0, 5);
        showRiseDrawable();
    }

    private void showDropDrawable() {
        mDrawable = ContextCompat.getDrawable(mContext, R.drawable.ic_arrow_drop_down_black_24dp);
        mDrawable.setBounds(0, 0, mDrawable.getIntrinsicWidth(), mDrawable.getIntrinsicHeight());
        setCompoundDrawables(getCompoundDrawables()[0], getCompoundDrawables()[1], mDrawable, getCompoundDrawables()[3]);
    }

    private void showRiseDrawable() {
        mDrawable = ContextCompat.getDrawable(mContext, R.drawable.ic_arrow_drop_down_black_24dp);
        //setBounds表示将drawable用draw方法画到Canvas时指定drawable的边界，就是要保留的部分
        //它是指定一个矩形区域，然后通过draw(Canvas)画的时候，就只在这个矩形区域内画图。
        mDrawable.setBounds(0, 0, mDrawable.getIntrinsicWidth(), mDrawable.getIntrinsicHeight());
        setCompoundDrawables(getCompoundDrawables()[0], getCompoundDrawables()[1], mDrawable, getCompoundDrawables()[3]);
    }

    private void closeSoftInput() {
        //InputMethodManager是一个用于控制显示或隐藏输入法面板的类（当然还有其他作用）。
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(this.getWindowToken(), 0);
    }

    private void showSoftInput() {
        setFocusable(true);
        setFocusableInTouchMode(true);
        requestFocus();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {
            mPopupWindow = new PopupWindow(mPopListView, getWidth(), LinearLayout.LayoutParams.WRAP_CONTENT);
            mPopupWindow.setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.color.deepgray));
            mPopupWindow.setFocusable(true);//允许让popwindow获取焦点
            mPopupWindow.setOnDismissListener(this);
        }
    }

    public void setAdapter(DropRecyclerAdapter adapter) {
        mAdapter=adapter;
        mPopListView.setAdapter(mAdapter);
    }


    @Override
    public void onDismiss() {
        showDropDrawable();//当popwindow消失时显示下拉图标
    }

    public int getCurrentItemIndex() {
        return mCurrentItemIndex;
    }

    public void setCurrentItemIndex(int currentItemIndex) {
        mCurrentItemIndex = currentItemIndex;
    }

    public void setItemClickListener(OnListViewItemClickListener onListViewItemClickListener) {
        mOnListViewItemClickListener = onListViewItemClickListener;
    }


    public interface OnListViewItemClickListener {
        void onClick(int position);
    }
}
