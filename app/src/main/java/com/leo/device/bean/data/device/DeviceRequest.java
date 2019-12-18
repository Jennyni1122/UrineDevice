package com.leo.device.bean.data.device;

import com.blankj.utilcode.util.LogUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Leo
 * @date 2019-05-07
 */
public class DeviceRequest {

    public static final byte GET_DATA = 1;
    public static final byte CHECK = 2;
    public static final byte CHECK_AND_GET_DATA = 3;
    public static final byte CLEAR = 4;

    public static final byte SET_BLUERAY = (byte) 128;
    public static final byte SET_WIFI = (byte) 129;
    public static final byte SET = (byte) 130;
    public static final byte SET_INTERVAL = (byte) 131;

    private byte type;
    private int param = 0;
    private byte[] ext = new byte[3];

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public int getParam() {
        return param;
    }

    public void setParam(int param) {
        this.param = param;
    }

    public byte[] getExt() {
        return ext;
    }

    public void setExt(byte[] ext) {
        this.ext = ext;
    }

    public static List<Byte> toByteList(int i) {
        List<Byte> list = new ArrayList<>();
        list.add((byte) ((i >> 24) & 0xFF));
        list.add((byte) ((i >> 16) & 0xFF));
        list.add((byte) ((i >> 8) & 0xFF));
        list.add((byte) (i & 0xFF));
        return list;
    }

    public static List<Byte> toByteList(short s) {
        List<Byte> list = new ArrayList<>();
        list.add((byte) ((s >> 8) & 0xFF));
        list.add((byte) (s & 0xFF));
        return list;
    }

    public byte[] toBytes() {
        List<Byte> byteList = new ArrayList<>();
        byteList.add(type);
        byteList.addAll(toByteList(param));
        for (byte aByte : ext) {
            byteList.add(aByte);
        }
        byte[] bytes = new byte[byteList.size()];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = byteList.get(i);
        }
        LogUtils.i("请求：", byteList);
        return bytes;
    }
}
