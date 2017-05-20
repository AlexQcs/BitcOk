package com.hc.wallcontrl.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alex on 2017/5/15.
 */

public class MatrixUtils {

    //定义切换矩阵所需要的信息...

    public String Version = "2017.01.16.001";

    int sum = 1024;
    /// <summary>
    /// 矩阵类型
    /// </summary>
    String matrixType;
    /// <summary>
    /// 矩阵名称
    /// </summary>
    String matrixName;
    /// <summary>
    /// 矩阵切换类型 A,V,AV
    /// </summary>
    String matrixSwType;
    /// <summary>
    /// 矩阵地址
    /// </summary>
    int matrixAddr;
    /// <summary>
    /// 矩阵输入列表
    /// </summary>
    int[] matrixInput = new int[sum];
    /// <summary>
    /// 矩阵输出列表
    /// </summary>
    int[] matrixOutput = new int[sum];

    public int matrixInputMax = 0;
    /// <summary>
    /// 矩阵输入总数
    /// </summary>
    public int mxInSum = 0;    //输入与输出的总数
    /// <summary>
    /// 矩阵输出总数
    /// </summary>
    public int mxOutSum = 0;
    /// <summary>
    /// 矩阵指令格式
    /// </summary>
    public boolean isHex = false;  //表示某个矩阵的命令格式
    //经过转换后返回可以控制的数据
    static List<String> outputStrCmd;

    static String savePlanStr;

    static String recallPlanStr;
    //为了校验方便
    byte ucCS, Leng;

    //记录以供调用.....
    /// <summary>
    /// 包含的协议
    /// </summary>
    public String[] matrixNAME =
            {
                    "_NotUsed",
                    "ST001", //0XC5	0XB1 0X00-0XFF 0xA1/0xA2 LONG CMD DATA(1) DATA(2) DATA(N) CHECKSUM
                    //"ST002", //96H 01H 01H 06H (A0H 00H 02H 02H 01H 43H)
                    "ST004",//7B 7B 01 02 01 01 F5 7D 7D
                    "CZ001",//96H 01H 01H 06H (A0H 00H 02H 02H 01H 43H)
                    "MAINVAN",//F4 CAM  21  MON  NET 00  CH
                    "MAINVAN5",//F4 CAML  21  MON  NET 00  CH  CAMH
                    "SISO",  //F2+RIDH+RIDL+22+Data1+Data2+TID+Check
                    "SISO2",  //F3   VIDH   VIDL  22  DATA  00  TID  CHECK //F3 00 CAM 22 MON NET 00 CH
                    "ITRON", //C1<3!
                    "RL1", //C1<3!
                    "DB",//基本格式：<IN,OUT,ADDR,D,B>
                    "DBS",//基本格式：IN,OUT,ADDR,D,B
                    "CREATEK_ID",//@+ID+IN+B+OUT+!
                    "CREATEK",//[x1]B[x2].
                    "RL3",//[x1]B[x2].
                    "CREATEK_ONE",//[x1]V[x2],[x3],[x4].
                    "CREATEK2",//[x1]B[x2]
                    "CREATEK2_ONE",//[x1]V[x2],[x3],[x4]
                    "CREATEK_X",//3V8&9..
                    "CREATEK_X2",//3V8&9.. 调用预案命令不同 其他一样
                    "CREATEK_X2Enter",//3V8&9..(0x2E,0x0A) 多一个回车键 其他一样
                    "CREATEK_M",//[x1]M[x2].
                    "CREATEK_NoPlan",//CREATEK无自带预案
                    "ODT",//CREATEK
                    "Mixed",//[SWCH,devaddr,inchanel,subchanel,outchanel]
                    "CKDZ",//FFH	00H-FFH	01H-FFH	OPT1	OPT2	AAH
                    "CKDZ8D",//F0 0F 03 Cmd Addr Num Out1 Out2 Out3 Out4 Out5 Out6 Out7 Out8 (cmd:09 数字混合矩阵，03: AV， 04 Vga）
                    "Dview",//[7E] [0400] [0100]  [inBoardId] [inPortId] [outBoardId] [outPortId] [7D]
                    "PEARMAIN ",//0xf6+主機地址+ 用戶號+ MON + CAMh +CAMl +Function
                    "ZC001",//FFH	00H-FFH	01H-FFH	OPT1	OPT2	AAH
                    "JDL",//BBH，数据1，数据2，校验，结束码。例：BBH，07H，0CH，4EH，99H
                    "CHANGE1",//基本格式：<IN,OUT,ADDR,D,B>
                    "CHANGE2",//[x1] B[x2].
                    "CHANGE3",//[SWCH,devaddr,inchanel,subchanel,outchanel]
                    "AC",//！  255  ：V  1   *   5  ；3 * 12 Enter
                    "SMTH4",//	ID号+V+输入通道号+M+输出通道号
                    "KEDI",//*02V08# 输入2切换到输出8，只切换视频。当大于96路时，占位为3
                    "LANMA",//BB 01 MON  CAML CAMH  00  NET  CH//（；BB 02  01  MON  00  00  NET  CH）；从0开始
                    "Extron",//兼容性指令 2*15！ 2*15%  2*15$
                    "Extron1",//相比Extron，自带预案调/存功能，多了 G / V /R 矩阵类型识别
                    "HDMXs",//长度+起始码(0x55)+指令码+参数(可无)+结束码(0x77)
                    "HDMXsFan",//长度+起始码(0x55)+指令码+参数(可无)+结束码(0x77) ==结构同HDMx，只是输入输出反了；
                    "MX0404_01",//cir 10\r\n   \\ 63 69 72 20 31 30 0D 0A \\output 3 select input 1
                    "HVSM",//启始字节＋命令内容长度+ 命令内容＋结束字节//BB（起始位）04 01 01 03 04< 00 >55（结束位）//=================多了一个零
                    "HVSM2",//启始字节＋命令内容长度+ 命令内容＋结束字节//BB（起始位）04 01 01 03 04 55（结束位）
                    "ZT",//启始字节＋命令内容长度+ 命令内容＋结束字节//BB（起始位）04 01 01 03 04 55（结束位）
                    "XU",//PL	00－99	CMD	DATA1	DATA2	DATA3
                    "RL2",//PL	00－99	CMD	DATA1	DATA2	DATA3
                    "XU2",//PH	00－99	CMD	DATA1	DATA2	NT
                    "PROV",//BAH(1) + 地址(2) + 命令(3) + 长度(4) + 切换模式(5) + 数据1…数据n(6) + 校验(7)
                    "LANMA5",//A5 CAM_L CAM_H 81 MON TYPE ID CHK
                    "SWI",//切换	SW(ITCH)	输入号	输出号1	输出号2	…
                    "TC8800",// FA FA 0C 81 C0 00 02 00 01 01 FD FD  用户 1 监视器 2 切换 摄像机 1
                    "SOHEOT",//<SOH>	1	WN	N	8	2	<EOT>
                    "JKJS",//23 41 00 03 04 01 01 FF
                    "V_TECH",//0101C 先输出,再输入,再控制指令
                    "NMA",//nMa,n#a
                    "MOV",//<move,1,3,0,0,0,1920,1080> out  in
                    "ODT2",//0xAA 0x00 0x03 Input Output 0xFF  将输入通道" Input "切至输出通道" Output "
                    "A5"//0XA5 源地址(00H-FFH)	目标地址(00H-FFH) 命令 数据1(OUT) 数据2(IN) 数据3 校验(字节1到字节7之和,0X99为万能校验码,调试用)

            };

    //定义矩阵切换控制框架:

    /// <summary>
    /// 矩阵控制协议
    /// </summary>
    /// <param name="matrixName">矩阵名称</param>
    /// <param name="matrixType">矩阵类型</param>
    /// <param name="matrixSwType">矩阵切换类型</param>
    /// <param name="matrixAddr">矩阵地址</param>
    /// <param name="matrixInput">矩阵输入列表</param>
    /// <param name="matrixOutput">矩阵输出列表</param>
    /// <returns></returns>
    public List<String> matrixControl(String matrixName, String matrixType, String matrixSwType, int matrixAddr, int[] matrixInput, int[] matrixOutput) {
        byte[] Buf = new byte[7];
        //控制前清空字符串OR每次初始化 array = null;/array.Clear(array,0,leng);
        outputStrCmd = new ArrayList<>();
        ucCS = 0x00;
        Leng = 0x00;
        int outPutCount = 0;
        boolean bRepeat = false;
        int point = 0;//断点
        //这里写矩阵切换协议
        //MessageBox.Show(matrixName.toLowerCase());
        switch (matrixName.toLowerCase().trim()) {
            case "notused":
                break;

            case "vgamt":
                isHex = true;
                byte mxSwType = 0x51;//矩阵切换
                switch (matrixSwType) {
                    case "A":
                        mxSwType = 0x03;
                        break;
                    case "V":
                        mxSwType = 0x02;
                        break;
                    case "AV":
                        mxSwType = 0x01;
                        break;
                }
                Buf[0] = (byte) 0xbb;
                Buf[1] = 0x04;
                Buf[2] = 0x00;
                Buf[3] = mxSwType;
                Buf[6] = 0x55;
                for (outPutCount = 0; outPutCount < mxOutSum; outPutCount++) {
                    if (mxInSum == 1) {
                        if (matrixOutput[outPutCount] > 0) {
                            bRepeat = false;
                            for (int x = 0; x < outPutCount; x++) {
                                if (matrixOutput[x] == matrixOutput[outPutCount]) {
                                    bRepeat = true;
                                    break;
                                }
                            }
                            if (!bRepeat) {
                                Buf[4] = (byte) matrixInput[0];
                                Buf[5] = (byte) matrixOutput[outPutCount];
                                String data = HexUtils.bytesToHexString(Buf, 6);
                                outputStrCmd.add(data);
                            }
                        }
                    } else if (mxInSum == mxOutSum) {
                        Buf[4]=(byte) matrixInput[outPutCount];
                        Buf[5]=(byte) matrixOutput[outPutCount];
                        String data=HexUtils.bytesToHexString(Buf, 6);
                        outputStrCmd.add(data);
                    }
                }
//                outputStrCmd=ArrayUtils.popAtLast(outputStrCmd,mxOutSum);
                break;
        }

        return outputStrCmd;
    }

    //定义矩阵预案保存方法
    /// <summary>
    /// 保存预设
    /// </summary>
    /// <param name="matrixName">矩阵名</param>
    /// <param name="matrixAddr">矩阵类型</param>
    /// <param name="saveMode">预设编号</param>
    /// <returns></returns>
//    /**
    public String matrixSavePlan(String matrixName, int matrixAddr, int saveMode, String matrixSwType) {
        try {
            String tpS = matrixName.split(":")[1];
            matrixType = tpS;
        } catch (Exception e) {
        }
        try {
            String tpS = matrixName.split(":")[0];
            matrixName = tpS;
        } catch (Exception e) {
        }
        byte[] Buf = new byte[6];
//        Buf = new byte[1024];
        ucCS = 0x00;
        savePlanStr = "";
        switch (matrixName.toLowerCase().trim()) {
            case "vgamt":
                Buf[0] = (byte) 0xBB;
                Buf[1] = 0x03;
                Buf[2] = 0x00;
                Buf[3] = 0x06;
                Buf[4] = (byte) (saveMode - 1);
                Buf[5] = 0x55;
                isHex = true;
                savePlanStr = HexUtils.bytesToHexString(Buf, 5);
                break;
        }


        return savePlanStr;
    }


    public String matrixSavePlan(String matrixName, int matrixAddr, int saveMode) {
        return matrixSavePlan(matrixName, matrixAddr, saveMode, matrixSwType = "AV");
    }

    public String matrixSavePlan(String matrixName,int matrixAddr){
        return matrixSavePlan(matrixName,0,matrixAddr,"");
    }

    //定义矩阵预案调用方法
    /// <summary>
    /// 调用预设
    /// </summary>
    /// <param name="matrixName">矩阵名</param>
    /// <param name="matrixAddr">矩阵地址</param>
    /// <param name="recallMode">预设编号</param>
    /// <param name="matrixType">切换类型</param>
    /// <returns></returns>
    public String matrixRecallPlan(String matrixName, int matrixAddr, int recallMode, String matrixSwType) {
        try {
            String tpS = matrixName.split(":")[1];
            matrixType = tpS;
        } catch (Exception e) {
        }
        try {
            String tpS = matrixName.split(":")[0];
            matrixName = tpS;
        } catch (Exception e) {
        }

        byte[] Buf = new byte[6];
        ucCS = 0x00;
        recallPlanStr = "";
        switch (matrixName.toLowerCase().trim()) {
            case "vgamt":
                Buf[0] = (byte) 0xBB;
                Buf[1] = 0x03;
                Buf[2] = 0x00;
                Buf[3] = 0x04;
                Buf[4] = (byte) (recallMode - 1);
                Buf[5] = 0x55;
                isHex = true;
                recallPlanStr = HexUtils.bytesToHexString(Buf, 5);
                break;
        }

        return recallPlanStr;
    }

    public String matrixRecallPlan(String matrixName, int matrixAddr, int recallMode) {
        return matrixRecallPlan(matrixName, matrixAddr, recallMode, matrixSwType = "AV");
    }

    public String matrixRecallPlan(String matrixName,int recallMode) {
        return matrixRecallPlan(matrixName, 0, recallMode, "");
    }

    //public String matrixSavePlan(String matrixName, int matrixAddr, int saveMode)
    //{
    // public String matrixSavePlan(String matrixName, int matrixAddr, int saveMode);
    //}


    //public bool bUseMatrix = false;
    public final int MATRIXSUM = 7; //可挂载矩阵数量
    public int matrixIndex;
    public String[] matrixAddIndex = new String[8];
    public int[] matrixDelIndex = new int[8];
    public int MatrixInput; //矩阵输入端口
    public String swSourceType; //当前控制的矩阵类型

    //下面两个函数可以合并(String cpStr=String.empty);
    public int returnEqualIdx(String cpStr) {
        int tpInt = MATRIXSUM;
        for (int i = 0; i < MATRIXSUM; i++) {
            if (matrixAddIndex[i] == cpStr) {
                tpInt = i;
                break;
            }
        }
        return tpInt;
    }

    public int returnEmptyIdx() {
        int tpInt = MATRIXSUM;
        for (int i = 0; i < MATRIXSUM; i++) {
            if (matrixAddIndex[i] == null || matrixAddIndex[i] == "") {
                tpInt = i;
                break;
            }
        }
        return tpInt;
    }

    public enum MxType {
        AV,
        VGA,
        DVI,
        HDMI,
        RGB,
        YPBPR,
        ELSE,
        sum  //MatrixSum
    }

    /// <summary>
    /// 矩阵种类
    /// </summary>
    public String[] MatrixTypeS = {"AV", "VGA", "DVI", "RGB", "HDMI", "YPbPr", "ELSE"};

    public MxType returnMatrixType(String sourStr) {
        MxType tpType = MxType.sum;
        switch (sourStr.trim().toLowerCase()) {
            case "av":
            case "av1":
            case "av2":
            case "av3":
            case "av4":
                tpType = MxType.AV;
                break;
            case "vga":
            case "vga1":
                tpType = MxType.VGA;
                break;
            case "dvi":
                tpType = MxType.DVI;
                break;
            case "hdmi":
                tpType = MxType.HDMI;
                break;
            case "rgb":
            case "rgbhv":
                tpType = MxType.RGB;
                break;
            case "ypbpr":
            case "yuv":
                tpType = MxType.YPBPR;
                break;
            case "else":
                tpType = MxType.ELSE;
                break;
            default:
                break;
        }
        return tpType;
    }


    public class stuMxInfo {
        public String mxName;
        public int mxAddress;
        public int mxInputSum;
        public int mxOutputSum;
        public int[] mxInputAry;
        public String[] mxInputStrAry;
        public String[] mxOutputStrAry;
        public int[] mxInputPlanAry;
        public int[] mxOutputAry;
        public int[] mxOutputMap;
        public int mxDelayTime;
        public MxType mxType;
        public String mxSwitchSourType;
        public boolean mxEnable;
        //        public SerialPort mxComPort;
        public String mxPortName;
        public String mxPortBaud;

        public stuMxInfo(int SUM) {
            mxName = "";
            mxAddress = 0;
            mxInputSum = SUM;
            mxOutputSum = SUM;
            mxDelayTime = 10;
            mxType = MxType.sum;
            mxSwitchSourType = "";
            mxEnable = false;
//            mxComPort = null;
            mxInputAry = new int[SUM];
            mxOutputAry = new int[SUM];
            mxOutputMap = new int[SUM];
            mxInputStrAry = new String[SUM];
            mxOutputStrAry = new String[SUM];
//            mxInputPlanAry = new int[SUM, SUM];
            mxPortName = "";
            mxPortBaud = "";
        }
    }


    public stuMxInfo[] mxInfoList = new stuMxInfo[MATRIXSUM];


    //matrixProtocolCls()
    //{
    //    for (int i = 0; i < MATRIXSUM; i++)
    //    {
    //        mxInfoList[i] = new stuMxInfo();
    //    }
    //}


}



