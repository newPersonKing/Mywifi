package com.gy.mywifi.bean;

import android.text.TextUtils;

/**
 * Created by huison on 2018/6/3.
 */

public class WifiApClient {

    private String clientName;
    private String clientIp;
    private String clientMac;
    private boolean isReachable;

    public WifiApClient() {
    }

    public WifiApClient(String clientName, String clientIp, String clientMac, boolean isReachable) {
        this.clientName = clientName;
        this.clientIp = clientIp;
        this.clientMac = clientMac;
        this.isReachable = isReachable;
    }

    public String getClientName() {
        return TextUtils.isEmpty(clientName) ? "未知设备" : clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getClientIp() {
        return clientIp;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }

    public String getClientMac() {
        return clientMac;
    }

    public void setClientMac(String clientMac) {
        this.clientMac = clientMac;
    }

    public boolean isReachable() {
        return isReachable;
    }

    public void setReachable(boolean reachable) {
        isReachable = reachable;
    }
}
