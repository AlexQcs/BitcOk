package com.hc.wallcontrl.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by alex on 2017/3/27.
 */

public class StringUtils {
    /**
     * 左补位，右对齐
     * @param oriStr  原字符串
     * @param len  目标字符串长度
     * @param alexin  补位字符
     * @return  目标字符串
     */
    public static String padLeft(String oriStr,int len,char alexin){
        String str="";
        int strlen = oriStr.length();
        if(strlen < len){
            for(int i=0;i<len-strlen;i++){
                str = str+alexin;
            }
        }
        str = oriStr + str;
        return str;
    }

    public static boolean checkIp(String strIP){
        Pattern pattern=Pattern.compile(ConstUtils.MACHES_IP);
        Matcher matcher=pattern.matcher(strIP);
        return matcher.matches();
    }

    public static boolean checkPort(String strPort){
        Pattern pattern=Pattern.compile(ConstUtils.MACHES_PORT);
        Matcher matcher=pattern.matcher(strPort);
        return matcher.matches();
    }
}
