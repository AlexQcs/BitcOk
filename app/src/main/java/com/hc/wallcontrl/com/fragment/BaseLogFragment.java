package com.hc.wallcontrl.com.fragment;

import com.hc.wallcontrl.util.EmptyUtils;
import com.hc.wallcontrl.util.LogUtil;

/**
 * Created by alex on 2017/4/7.
 */

public abstract class BaseLogFragment extends android.support.v4.app.DialogFragment  {
    /**
     * Log日志标签
     */
    private String tag;

    /**
     * 得到Log日志标签名称，用于打印调试信息
     *
     * @return Log日志标签名称
     */
    protected abstract String getLogTagName();

    /**
     * 构造方法
     */
    public BaseLogFragment() {
        final String tagName = getLogTagName();
        if (EmptyUtils.isEmpty(tagName))
            this.tag = "BaseLogFragment";
        else
            this.tag = tagName;
    }

    final protected void d(String msg) {
        LogUtil.d(msg);
    }

    final protected void c(String msg) {
        LogUtil.c(msg);
    }

    final protected void e(String msg) {
        LogUtil.e( msg);
    }

    final protected void i(String msg) {
        LogUtil.i(msg);
    }

    final protected void v(String msg) {
        LogUtil.v( msg);
    }

    final protected void w(String msg) {
        LogUtil.w(msg);
    }
}
