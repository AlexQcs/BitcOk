package com.hc.wallcontrl.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by alex on 2017/4/21.
 */

public class ScreenMatrixBean implements Serializable {
    private String matrixCategory;//矩阵类型
    private MatrixFactory matrixFactory;
    private int delaytime;//命令延时
    private int inputQuan;//输入总数
    private List<String> matrixInputName;
    private boolean hasSet;
    private List<ScreenOutputBean> listOutputScreen;

//    //A:0 v:1 AV:2
//    private int matrixSwitch;
//
//    public int getMatrixSwitch() {
//        return matrixSwitch;
//    }
//
//    public void setMatrixSwitch(int matrixSwitch) {
//        this.matrixSwitch = matrixSwitch;
//    }


    public List<ScreenOutputBean> getListOutputScreen() {
        return listOutputScreen;
    }

    public void setListOutputScreen(List<ScreenOutputBean> listOutputScreen) {
        this.listOutputScreen = listOutputScreen;
    }

    public boolean isHasSet() {
        return hasSet;
    }

    public void setHasSet(boolean hasSet) {
        this.hasSet = hasSet;
    }

    public MatrixFactory getMatrixFactory() {
        return matrixFactory;
    }

    public void setMatrixFactory(MatrixFactory matrixFactory) {
        this.matrixFactory = matrixFactory;
    }


    public String getMatrixCategory() {
        return matrixCategory;
    }

    public void setMatrixCategory(String matrixCategory) {
        this.matrixCategory = matrixCategory;
    }

    public int getDelaytime() {
        return delaytime;
    }

    public void setDelaytime(int delaytime) {
        this.delaytime = delaytime;
    }

    public int getInputQuan() {
        return inputQuan;
    }

    public void setInputQuan(int inputQuan) {
        this.inputQuan = inputQuan;
    }

    public List<String> getMatrixInputName() {
        return matrixInputName;
    }

    public void setMatrixInputName(List<String> matrixInputName) {
        this.matrixInputName = matrixInputName;
    }

    public static class MatrixFactory implements Serializable {
        private String matrixName;
        private int addr;
        private int isHasAddr;

        public int getIsHasAddr() {
            return isHasAddr;
        }

        public void setIsHasAddr(int isHasAddr) {
            this.isHasAddr = isHasAddr;
        }

        public String getMatrixName() {
            return matrixName;
        }

        public void setMatrixName(String matrixName) {
            this.matrixName = matrixName;
        }

        public int getAddr() {
            return addr;
        }

        public void setAddr(int addr) {
            this.addr = addr;
        }
    }


    @Override
    public String toString() {
        return "ScreenMatrixBean{" +
                "matrixCategory='" + matrixCategory + '\'' +
                ", matrixFactory=" + matrixFactory +
                ", delaytime=" + delaytime +
                ", inputQuan=" + inputQuan +
                ", matrixInputName=" + matrixInputName +
                '}';
    }
}
