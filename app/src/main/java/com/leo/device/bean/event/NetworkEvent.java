package com.leo.device.bean.event;

import com.blankj.utilcode.util.NetworkUtils;

/**
 * @author Leo
 * @date 2019/12/9 0009 10:24
 */
public class NetworkEvent {
    private boolean isConnected;
    private NetworkUtils.NetworkType networkType;

    public NetworkEvent(boolean isConnected, NetworkUtils.NetworkType networkType) {
        this.isConnected = isConnected;
        this.networkType = networkType;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public NetworkUtils.NetworkType getNetworkType() {
        return networkType;
    }
}
