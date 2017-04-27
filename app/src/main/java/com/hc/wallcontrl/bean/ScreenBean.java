package com.hc.wallcontrl.bean;

/**
 * Created by alex on 2017/4/21.
 */

public class ScreenBean {
    private int column;
    private int row;
    private String matrixCategory;
    private String matrixFactory;
    private int inputQuan;
    private int delaytime;

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
}
