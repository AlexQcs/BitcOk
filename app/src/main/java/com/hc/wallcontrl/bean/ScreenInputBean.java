package com.hc.wallcontrl.bean;

import java.io.Serializable;

/**
 * Created by alex on 2017/5/8.
 */

public class ScreenInputBean implements Serializable {
    private int column;//列
    private int row;//排
    private String inputSource;
    private String inputName;
    private String switchCate;
    private boolean isUseMatrix;
    private boolean isSingleScreen;

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

    public String getInputSource() {
        return inputSource;
    }

    public void setInputSource(String inputSource) {
        this.inputSource = inputSource;
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

    public boolean isSingleScreen() {
        return isSingleScreen;
    }

    public void setSingleScreen(boolean singleScreen) {
        isSingleScreen = singleScreen;
    }

    @Override
    public String toString() {
        return "ScreenInputBean{" +
                "column=" + column +
                ", row=" + row +
                ", inputSource='" + inputSource + '\'' +
                ", inputName='" + inputName + '\'' +
                ", switchCate='" + switchCate + '\'' +
                ", isUseMatrix=" + isUseMatrix +
                ", isSingleScreen=" + isSingleScreen +
                '}';
    }
}
