package com.sdp.utils;

import java.io.UnsupportedEncodingException;
import java.util.Random;

/**
 * Created by Guoqing on 2016/12/14.
 */
public class StringTest {

    public static void main(String [] arguments) throws UnsupportedEncodingException {

        String str = "Reed-Solomon 是个好算法！";

        ReedSolomonUtil reedSolomonUtil = new ReedSolomonUtil(3, 1);

        String[] arrStr = reedSolomonUtil.encode(str);
        arrStr[new Random().nextInt(arrStr.length)] = null;

        System.out.println(reedSolomonUtil.decode(arrStr));
    }
}
