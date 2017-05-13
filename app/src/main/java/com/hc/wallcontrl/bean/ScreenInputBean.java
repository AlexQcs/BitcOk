package com.hc.wallcontrl.bean;

import java.io.Serializable;

/**
 * Created by alex on 2017/5/8.
 */

public class ScreenInputBean implements Serializable {
    private int column;//列
    private int row;//排
    private String signalSource;//信号源
    private String inputName;//矩阵输入
    private String switchCate;//切换类型
    private boolean isUseMatrix;//是否使用矩阵

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public String getSignalSource() {
        return signalSource;
    }

    public void setSignalSource(String signalSource) {
        this.signalSource = signalSource;
    }

    public String getInputName() {
        return inputName;
    }

    public void setInputName(String inputName) {
        this.inputName = inputName;
    }

    public String getSwitchCate() {
        return switchCate;
    }

    public void setSwitchCate(String switchCate) {
        this.switchCate = switchCate;
    }

    public boolean isUseMatrix() {
        return isUseMatrix;
    }

    public void setUseMatrix(boolean useMatrix) {
        isUseMatrix = useMatrix;
    }


    @Override
    public String toString() {
        return "ScreenInputBean{" +
                "column=" + column +
                ", row=" + row +
                ", inputSource='" + signalSource + '\'' +
                ", inputName='" + inputName + '\'' +
                ", switchCate='" + switchCate + '\'' +
                ", isUseMatrix=" + isUseMatrix +
                '}';
    }
}
