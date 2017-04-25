package com.hc.wallcontrl.com;

import com.hc.wallcontrl.util.ClsCmds;

/**
 * Created by John on 2015-04-08.
 */
public class ClsV59Ctrl {

    public static byte[] GetCmd(byte[] addr,byte fun,byte valueH,byte valueL,byte cmdType){
        byte[] Cmd = new byte[15];
        Cmd[0] = ClsCmds.Head;
        Cmd[1] = cmdType;
        Cmd[2] = addr[0];
        Cmd[3] = addr[1];
        Cmd[4] = addr[2];
        Cmd[5] = addr[3];
        Cmd[6] = fun;
        Cmd[7] = valueH;
        Cmd[8] = valueL;
        Cmd[9] = Cmd[10] = Cmd[11] = Cmd[12] = Cmd[13] = Cmd[14] = 0x00;
        for(int i=0;i<14;i++)
        {
            Cmd[14] += Cmd[i];
        }
        return Cmd;
    }
}
