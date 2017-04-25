package com.hc.wallcontrl.bean;

import java.io.Serializable;

/**
 * Created by alex on 2017/3/28.
 */

public class MatrixSetBean implements Serializable {
    private String name;
    private int drawable;

    public MatrixSetBean(String name, int drawable) {
        this.name = name;
        this.drawable = drawable;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDrawable() {
        return drawable;
    }

    public void setDrawable(int drawable) {
        this.drawable = drawable;
    }
}
