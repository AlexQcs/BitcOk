package com.hc.wallcontrl.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alex on 2017/5/13.
 */

public class ArrayUtils {

    public static String[] insertAtLast(String[] resource, String str, int addQuan) {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < resource.length; i++) {
            list.add(resource[i]);
        }
        for (int i = 0; i < addQuan-resource.length; i++) {
            list.add(str + (i+resource.length));
        }

        String[] result = list.toArray(resource);
        return result;
    }

    public static String[] popAtLast(String[] resource, int length) {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            list.add(resource[i]);
        }
        String[] result = list.toArray(resource);
        return result;
    }

}
