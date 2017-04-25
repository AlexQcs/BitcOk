package com.hc.wallcontrl.bean;

import java.io.Serializable;

/**
 * Created by John on 2015-07-28.
 */
public class ClsSerializableSetPram implements Serializable {

    private String strIP,strPort;
    private int Rs,Cs;
    private boolean bConn;
    public final void setIP(String ip)
    {
        strIP = ip;
    }
    public final String getIP()
    {
        return strIP;
    }
    public final void setPort(String port)
    {
        strPort = port;
    }
    public final String getPort()
    {
        return strPort;
    }
    public final void setRs(int rsum)
    {
        Rs = rsum;
    }
    public final int getRs()
    {
        return Rs;
    }
    public final void setCs(int csum)
    {
        Cs = csum;
    }
    public final int getCs()
    {
        return Cs;
    }
    public final void setConn(boolean bconn)
    {
        bConn = bconn;
    }
    public final boolean getConn()
    {
        return bConn;
    }
}
