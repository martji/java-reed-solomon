package com.sdp.utils;

import com.backblaze.erasure.ReedSolomon;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

/**
 * Created by Guoqing on 2016/12/15.
 *
 * Implement a String version of ReedSolomon <a>https://github.com/Backblaze/JavaReedSolomon</a>
 */
public class ReedSolomonUtil {

    private int dataShards = 4;
    private int parityShards = 2;
    private int totalShards = 6;

    private final int BYTES_IN_INT = 4;
    private final String charset = "ISO-8859-1";

    private ReedSolomon reedSolomon;

    /**
     *
     * @param dataShards
     * @param parityShards
     */
    public ReedSolomonUtil(int dataShards, int parityShards) {
        this.dataShards = dataShards;
        this.parityShards = parityShards;
        this.totalShards = dataShards + parityShards;

        this.reedSolomon = ReedSolomon.create(dataShards, parityShards);
    }

    /**
     *
     * @param str the string to be encoded
     * @return the encoded string array
     * @throws UnsupportedEncodingException
     */
    public String[] encode(String str) throws UnsupportedEncodingException {
        int len = str.getBytes().length;
        int storedSize = len + BYTES_IN_INT;
        int shardSize = (storedSize + dataShards - 1) / dataShards;
        int bufferSize = shardSize * dataShards;
        byte[] allBytes = new byte[bufferSize];
        ByteBuffer.wrap(allBytes).putInt(len);
        ByteBuffer.wrap(allBytes).put(str.getBytes());

        byte[][] shards = new byte[totalShards][shardSize];
        for (int i = 0; i < dataShards; i++) {
            System.arraycopy(allBytes, i * shardSize, shards[i], 0, shardSize);
        }
        reedSolomon.encodeParity(shards, 0, shardSize);

        String[] arrStr = new String[totalShards];
        for (int i = 0; i < totalShards; i++) {
            arrStr[i] = new String(shards[i], charset);
        }

        return arrStr;
    }

    /**
     *
     * @param arrStr the encoded string array
     * @return the original string
     * @throws UnsupportedEncodingException
     */
    public String decode(String[] arrStr) throws UnsupportedEncodingException {
        if (arrStr.length != totalShards) {
            return null;
        }

        int shardSize = 0;
        boolean[] shardPresent = new boolean [totalShards];
        for (int i = 0; i < totalShards; i++) {
            if (arrStr[i] != null) {
                shardPresent[i] = true;
                shardSize = arrStr[i].length();
            }
        }
        int bufferSize = shardSize * dataShards;
        byte[][] shards = new byte[totalShards][shardSize];
        byte[] allBytes = new byte[bufferSize];
        for (int i = 0; i < totalShards; i++) {
            if (shardPresent[i]) {
                shards[i] = arrStr[i].getBytes(charset);
            }
        }
        reedSolomon.decodeMissing(shards, shardPresent, 0, shardSize);

        for (int i = 0; i < dataShards; i++) {
            System.arraycopy(shards[i], 0, allBytes, shardSize * i, shardSize);
        }
        ByteBuffer.wrap(allBytes).getInt();

        return new String(allBytes);
    }
}
