package com.hc.wallcontrl.util;

/**
 * Created by alex on 2017/4/12.
 */

public class ConstUtils {
    public final static String SHAREDPREFERENCES = "SharedPreferences";

    //SocketService的动作指令
    public final static String ACTION_CONN = "ACTION_CONN";
    public final static String ACTION_CLOSE = "ACTION_CLOSE";
    public final static String ACTION_SEND = "ACTION_SEND";
    //正则验证输入的服务器地址是否正确
    public final static String MACHES_IP ="^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\."
            +"(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
            +"(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
            +"(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$";
    public final static String MACHES_PORT="^([0-9]|[1-9]\\d{1,3}|[1-5]\\d{4}|6[0-5]{2}[0-3][0-5])$";

    //shareprefrence对应key
    public final static String SP_ROWS="rows";
    public final static String SP_COLUMNS="clumns";
    public final static String SP_ISCONN="isConn";
    public final static String SP_SCREEN_MATRIX_LIST="creen_matrix_list";
    public final static String SP_SCREEN_INPUT_LIST="screen_input_list";
    public final static String SP_IP="IP";
    public final static String SP_PORT="Port";


    //广播接收器对应key
    public final static String BROADCAST_IP="ip";
    public final static String BROADCAST_PORT="port";
    public final static String BROADCAST_BUFF="send_buff";
    public final static String BROADCAST_ISCONN="isconn";
}
