package com.hc.wallcontrl.bean;

import java.io.Serializable;

/**
 * Created by alex on 2017/5/17.
 */

public class ScreenOutputBean implements Serializable {
    private int column;//列
    private int row;//排
    private int matrixOutputStream;//输出通道

    public int getMatrixOutputStream() {
        return matrixOutputStream;
    }

    public void setMatrixOutputStream(int matrixOutputStream) {
        this.matrixOutputStream = matrixOutputStream;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    @Override
    public String toString() {
        return "ScreenOutputBean{" +
                "column=" + column +
                ", row=" + row +
                ", matrixOutputStream=" + matrixOutputStream +
                '}';
    }
}

