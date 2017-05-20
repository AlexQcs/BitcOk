package com.hc.wallcontrl.util;

/**
 * Created by John on 2015-04-06.
 */
public class ClsCmds {

    public static final byte Power = 0x10;
    public static final byte PowerOn = 0x01;
    public static final byte PowerOff = 0x00;

    public static final byte EmptyValue = 0x00;

    public static final byte Source = 0x30;
    public static final byte AV = 0x00;
    public static final byte AV2 = 0x01;
    public static final byte AV3 = 0x02;
    public static final byte AV4 = 0x03;
    public static final byte QUAD = 0x04;
    public static final byte Svideo = 0x09;
    public static final byte YPBPR=0x0c;
    public static final byte VGA = 0x0d;
    public static final byte DVI = 0x0f;
    public static final byte HDMI = 0x10;
    public static final byte HDMI2 = 0x11;
    public static final byte HDMI3 = 0x12;
    public static final byte DMP1 = 0x13;
    public static final byte DMP2 = 0x14;
    public static final byte USB = 0x00;
    public static final byte DP = 0x13;

    public static final byte SavaPlan= (byte) 0x82;

    public static final byte RecallPlan = (byte) 0x83;

    public static final byte Head = (byte) 0xf5;
    public static final byte ModeW = (byte) 0xb1;

    public static final byte IrMode = 0x20;
    public static final byte IrMute = 0x00;
    public static final byte IrFWall = 0x01;
    public static final byte IrSWall = 0x02;
    public static final byte IrSource = 0x03;
    public static final byte IrMenu = 0x04;
    public static final byte IrLeft = 0x05;
    public static final byte IrRight = 0x06;
    public static final byte IrTop = 0x07;
    public static final byte IrBottom = 0x08;
    public static final byte IrEnter = 0x09;
    public static final byte IrExit = 0x0a;
    public static final byte IrPlay = 0x0b;
    public static final byte IrPause = 0x0c;
    public static final byte IrStop = 0x0d;
    public static final byte IrInfo = 0x0e;
    public static final byte Ir0 = 0x0f;
    public static final byte Ir1 = 0x10;
    public static final byte Ir2 = 0x11;
    public static final byte Ir3 = 0x12;
    public static final byte Ir4 = 0x13;
    public static final byte Ir5 = 0x14;
    public static final byte Ir6 = 0x15;
    public static final byte Ir7 = 0x16;
    public static final byte Ir8 = 0x17;
    public static final byte Ir9 = 0x18;
    public static final byte IrFreeze = 0x19;
    public static final byte IrTurn = 0x1a;
    public static final byte IrID = 0x1b;
    public static final byte IrPos = 0x1c;
    public static final byte IrColor = 0x1d;

}
