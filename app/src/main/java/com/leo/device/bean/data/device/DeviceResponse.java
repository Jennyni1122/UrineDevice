package com.leo.device.bean.data.device;

import android.text.TextUtils;
import com.blankj.utilcode.util.ConvertUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Leo
 * @date 2019-05-07
 */
public class DeviceResponse {
    private byte[] uid;
    private byte battery;
    private byte version;
    private byte type;
    private byte ext;
    private short sequence;
    private short interval;
    private short dataExt;
    private short[][] data = new short[8][4];
    private byte ext2;
    private byte verify;

    public DeviceResponse() {
    }

    public DeviceResponse(byte[] bytes) {
        if (bytes == null) {
            return;
        }
        List<Byte> byteList = new ArrayList<>(bytes.length);
        for (byte aByte : bytes) {
            byteList.add(aByte);
        }
        initFromByteList(byteList);
    }

    public DeviceResponse(List<Byte> byteList) {
        initFromByteList(byteList);
    }

    public void initFromByteList(List<Byte> byteList) {
        if (byteList == null || byteList.size() == 0) {
            return;
        }
        uid = getByteArrayFromList(byteList, 4);
        battery = getByteFromList(byteList);
        verify = getByteFromList(byteList);
        version = getByteFromList(byteList);
        type = getByteFromList(byteList);
        ext = getByteFromList(byteList);
        sequence = getShortFromList(byteList);
        interval = getShortFromList(byteList);
        dataExt = getShortFromList(byteList);
        for (int i = 0; i < data.length; i++) {
            for (int i2 = 0; i2 < data[i].length; i2++) {
                data[i][i2] = getShortFromList(byteList);
            }
        }
        ext2 = getByteFromList(byteList);
        verify = getByteFromList(byteList);
    }

    public byte[] getUid() {
        return uid;
    }

    public String getDeviceId() {
        if (uid != null) {
            List<String> hexList = new ArrayList<>();
            for (byte aByte : uid) {
                hexList.add(ConvertUtils.bytes2HexString(new byte[]{aByte}));
            }
            return TextUtils.join(":", hexList);
        }
        return "";
    }

    public byte getBattery() {
        return battery;
    }

    public byte getVersion() {
        return version;
    }

    public byte getType() {
        return type;
    }

    public byte getExt() {
        return ext;
    }

    public short getSequence() {
        return sequence;
    }

    public short getInterval() {
        return interval;
    }

    public short getDataExt() {
        return dataExt;
    }

    public short[][] getData() {
        return data;
    }

    public byte getExt2() {
        return ext2;
    }

    public byte getVerify() {
        return verify;
    }

    public boolean isVerify() {
        return verify == (byte) 176;
    }

    private static final int INT_SIZE = 4;
    private static final int SHORT_SIZE = 2;

    public static int getIntFromList(List<Byte> byteList) {
        return getNumFromList(byteList, INT_SIZE);
    }

    public static short getShortFromList(List<Byte> byteList) {
        return (short) getNumFromList(byteList, SHORT_SIZE);
    }

    public static byte getByteFromList(List<Byte> byteList) {
        byte value = 0;
        if (byteList != null && byteList.size() > 0) {
            value = byteList.get(0);
            byteList.remove(0);
        }
        return value;
    }

    private static int getNumFromList(List<Byte> byteList, int size) {
        int value = 0;
        if (byteList != null && byteList.size() >= size) {
            // 由高位到低位
            for (int i = 0; i < size; i++) {
                int shift = (size - 1 - i) * 8;
                // 往高位游
                value += (byteList.get(0) & 0x000000FF) << shift;
                byteList.remove(0);
            }
        }
        return value;
    }

    private static byte[] getByteArrayFromList(List<Byte> byteList, final int size) {
        byte[] bytes = new byte[size];
        for (int i = 0; i < size; i++) {
            if (byteList.size() > 0) {
                bytes[i] = byteList.get(0);
                byteList.remove(0);
            } else {
                return null;
            }
        }
        return bytes;
    }

    public static byte[] getByteArray() {
        return new byte[80];
    }
}
