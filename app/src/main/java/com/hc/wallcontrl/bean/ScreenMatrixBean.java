package com.hc.wallcontrl.bean;

import java.io.Serializable;

/**
 * Created by alex on 2017/4/21.
 */

public class ScreenMatrixBean implements Serializable{
    private int column;//列
    private int row;//排
    private String matrixCategory;//矩阵类型
    private String matrixFactory;//矩阵厂家
    private int inputQuan;//输入总数
    private int delaytime;//命令延时
    private String matrixStream;//输出通道
    private int addr;//设备地址
    //矩阵输入名称
    private String matrixInputName;

    public String getMatrixInputName() {
        return matrixInputName;
    }

    public void setMatrixInputName(String matrixInputName) {
        this.matrixInputName = matrixInputName;
    }

    public String getMatrixStream() {
        return matrixStream;
    }

    public void setMatrixStream(String matrixStream) {
        this.matrixStream = matrixStream;
    }

    public int getAddr() {
        return addr;
    }

    public void setAddr(int addr) {
        this.addr = addr;
    }

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

    public String getMatrixCategory() {
        return matrixCategory;
    }

    public void setMatrixCategory(String matrixCategory) {
        this.matrixCategory = matrixCategory;
    }

    public String getMatrixFactory() {
        return matrixFactory;
    }

    public void setMatrixFactory(String matrixFactory) {
        this.matrixFactory = matrixFactory;
    }

    public int getInputQuan() {
        return inputQuan;
    }

    public void setInputQuan(int inputQuan) {
        this.inputQuan = inputQuan;
    }

    public int getDelaytime() {
        return delaytime;
    }

    public void setDelaytime(int delaytime) {
        this.delaytime = delaytime;
    }

    @Override
    public String toString() {
        return "ScreenBean{" +
                "column=" + column +
                ", row=" + row +
                ", matrixCategory='" + matrixCategory + '\'' +
                ", matrixFactory='" + matrixFactory + '\'' +
                ", inputQuan=" + inputQuan +
                ", delaytime=" + delaytime +
                ", matrixStream='" + matrixStream + '\'' +
                ", addr=" + addr +
                ", matrixInputName='" + matrixInputName + '\'' +
                '}';
    }
}
