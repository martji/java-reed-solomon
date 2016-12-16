package com.sdp.utils;

import java.io.UnsupportedEncodingException;

/**
 * Created by Guoqing on 2016/12/14.
 */
public class StringTest {

    public static void main(String [] arguments) throws UnsupportedEncodingException {

        String str = "Reed-Solomon 是个好算法！";

        ReedSolomonUtil reedSolomonUtil = new ReedSolomonUtil(2, 2);

        String[] arrStr = reedSolomonUtil.encode(str);
        arrStr[0] = arrStr[2];
        arrStr[1] = arrStr[3];
        arrStr[2] = null;
        arrStr[3] = null;

        String out = reedSolomonUtil.decode(arrStr);
        System.out.println(out + " : " + out.equals(str));
    }
}
