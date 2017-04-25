package com.hc.wallcontrl.com;

import com.hc.wallcontrl.util.StringUtils;

/**
 * Created by alex on 2017/3/25.
 */

public class MatrixUtils {

    //矩阵切换类型
    private String matrixSwType;
    //矩阵类型
    private String matrixType;
    //矩阵总输出
    private int mxOutSum;
    //矩阵总输入
    private int mxInSum;
    //矩阵地址
    private String matrixAddr;
    private int[] matrixOutput;
    private int[] matrixInput;
    private String[] outputStrCmd;
    private boolean isHex;
    private boolean bRepeat;

    public MatrixUtils() {

    }

    public String[] switchMatrix(String matrixName) {
        switch (matrixName) {
            case "r12":
                int outPutCount;
                isHex = false;
                switch (matrixSwType) {
                    case "A":
                        matrixSwType = "A";
                        break;
                    case "V":
                        matrixSwType = "V";
                        break;
                    case "A+V":
                    case "AV":
                        matrixSwType = "W";
                        break;
                }
                String mxType = "V";
                switch (matrixType) {
                    case "AUDIO":
                        mxType = "A";
                        break;
                    case "VIDEO":
                    case "AV":
                        mxType = "A";
                        break;
                    case "VGA":
                        mxType = "V";
                        break;
                    case "RGB":
                        mxType = "R";
                        break;
                    case "DVI":
                        mxType = "V";
                        break;
                    case "YPBYR":
                        mxType = "A";
                        break;
                    case "ALL":
                        mxType = "V";
                        break;
                }
                for (outPutCount = 0; outPutCount < mxOutSum; outPutCount++) {
                    if (mxInSum == 1)    //Control
                    {
                        if (matrixOutput[outPutCount] > 0) {
                            bRepeat = false;
                            for (int x = 0; x < outPutCount; x++) {
                                if (matrixOutput[x] == matrixOutput[outPutCount]) {
                                    bRepeat = true;
                                    break;
                                }
                            }
                            if (!bRepeat)//Data: !.254:L.1*.2.
                                outputStrCmd[outPutCount] = "P"
                                        + mxType + StringUtils.padLeft(matrixAddr, 2, '0')
                                        + "S" + matrixSwType + StringUtils.padLeft(matrixInput[0] + "", 2, '0')
                                        + StringUtils.padLeft(matrixOutput[outPutCount] + "", 2, '0') + "NT";
                        }

                    }
                }
                break;
        }
        return outputStrCmd;
    }

    public String getMatrixSwType() {
        return matrixSwType;
    }

    public void setMatrixSwType(String matrixSwType) {
        this.matrixSwType = matrixSwType;
    }

    public String getMatrixType() {
        return matrixType;
    }

    public void setMatrixType(String matrixType) {
        this.matrixType = matrixType;
    }

    public String getMatrixAddr() {
        return matrixAddr;
    }

    public void setMatrixAddr(String matrixAddr) {
        this.matrixAddr = matrixAddr;
    }

    public int[] getMatrixOutput() {
        return matrixOutput;
    }

    public void setMatrixOutput(int[] matrixOutput) {
        this.mxOutSum = matrixOutput.length;
        this.matrixOutput = matrixOutput;
    }

    public int[] getMatrixInput() {
        return matrixInput;
    }

    public void setMatrixInput(int[] matrixInput) {
        this.mxInSum = matrixInput.length;
        this.matrixInput = matrixInput;
    }

    public String[] getOutputStrCmd() {
        return outputStrCmd;
    }

    public void setOutputStrCmd(String[] outputStrCmd) {
        this.outputStrCmd = outputStrCmd;
    }

    public boolean isHex() {
        return isHex;
    }

    public void setHex(boolean hex) {
        isHex = hex;
    }

    public boolean isbRepeat() {
        return bRepeat;
    }

    public void setbRepeat(boolean bRepeat) {
        this.bRepeat = bRepeat;
    }

}
