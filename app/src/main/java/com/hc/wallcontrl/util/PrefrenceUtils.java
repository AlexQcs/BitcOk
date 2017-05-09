package com.hc.wallcontrl.util;

import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.List;

/**
 * Created by alex on 2017/5/4.
 */

public class PrefrenceUtils {
    public static String SceneList2String(List sceneList) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(sceneList);
        String sceneListString = new String(Base64.encode(byteArrayOutputStream.toByteArray(),
                Base64.DEFAULT));
        objectOutputStream.close();
        return sceneListString;
    }

    @SuppressWarnings("unchecked")
    public static List String2SceneList(String SceneListString)
            throws StreamCorruptedException, IOException,
            ClassNotFoundException {
        byte[] mobileBytes = Base64.decode(SceneListString.getBytes(),
                Base64.DEFAULT);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
                mobileBytes);
        ObjectInputStream objectInputStream = new ObjectInputStream(
                byteArrayInputStream);
        List SceneList = (List) objectInputStream
                .readObject();
        objectInputStream.close();
        return SceneList;
    }

}
