package com.sdp.utils;

import com.backblaze.erasure.ReedSolomon;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

/**
 * Created by Guoqing on 2016/12/15.
 * <p>
 * Implement a String version of ReedSolomon <a>https://github.com/Backblaze/JavaReedSolomon</a>
 */
public class ReedSolomonUtil {

    private int dataShards = 4;
    private int parityShards = 2;
    private int totalShards = 6;

    private final String charset = "ISO-8859-1";

    private ReedSolomon reedSolomon;

    /**
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
     * @param str the string to be encoded
     * @return the encoded string array
     * @throws UnsupportedEncodingException
     */
    public String[] encode(String str) throws UnsupportedEncodingException {
        int storedSize = str.getBytes().length;
        int bufferSize = storedSize + dataShards - 1; // make bufferSize can be dived by dataShards
        int shardSize = bufferSize / dataShards;
        byte[] allBytes = new byte[bufferSize];
        byte[][] shards = new byte[totalShards][shardSize];

        ByteBuffer.wrap(allBytes).put(str.getBytes());
        for (int i = 0; i < dataShards; i++) {
            System.arraycopy(allBytes, i * shardSize, shards[i], 0, shardSize);
        }
        reedSolomon.encodeParity(shards, 0, shardSize);

        String[] arrStr = new String[totalShards];
        for (int i = 0; i < totalShards; i++) {
            arrStr[i] = i + ":" + new String(shards[i], charset);
        }

        return arrStr;
    }

    /**
     * @param values the encoded string array
     * @return the original string
     * @throws UnsupportedEncodingException
     */
    public String decode(String[] values) throws UnsupportedEncodingException {
        if (values.length != totalShards) {
            return null;
        }

        int shardSize = 0;
        String[] arrStr = new String[totalShards];
        boolean[] shardPresent = new boolean[totalShards];
        for (int i = 0; i < totalShards; i++) {
            if (values[i] != null) {
                int index = values[i].indexOf(":");
                int id = Integer.parseInt(values[i].substring(0, index));
                arrStr[id] = values[i].substring(index + 1);
                shardPresent[id] = true;
                shardSize = arrStr[id].length();
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

        byte[] out = new byte[bufferSize - dataShards + 1];
        System.arraycopy(allBytes, 0, out, 0, out.length);
        return new String(out);
    }
}
