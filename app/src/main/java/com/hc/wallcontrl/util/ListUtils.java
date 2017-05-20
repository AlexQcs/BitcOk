package com.hc.wallcontrl.util;

import java.util.List;

/**
 * Created by alex on 2017/5/17.
 */

public class ListUtils {
    public static List<String> insertAtLast(List<String> resource, String str, int length){
        int temp=length-resource.size();
        for (int i = 1; i <=temp; i++) {
            resource.add(str+(resource.size()+1));
        }
        return resource;
    }

    public static List<String> popList(List<String> resource,int length){
        for (int i = resource.size()-1; i >length-1; i--) {
            resource.remove(i);
        }
        return resource;
    }
}
