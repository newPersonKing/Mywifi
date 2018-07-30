package com.gy.mywifi.bean;

import android.text.TextUtils;

import com.gy.mywifi.manager.WifiConstant;

import java.io.Serializable;

/**
 * Created by huison on 2018/6/3.
 */

public class WifiDetail implements Comparable<WifiDetail>, Serializable {

    public static final int kStateDisconnected = 0;
    public static final int kStateConnecting = 1;
    public static final int kStateConnected = 2;

    public static final int kStrengthFull = 4;
    public static final int kStrengthThreePiece = 3;
    public static final int kStrengthTwoPiece = 2;
    public static final int kStrengthOnePiece = 1;

    /**
     * wifi加密类型
     */
    public enum WifiCipherType {
        kWifiCipherWep, kWifiCipherWpa, kWifiCipherNoPassword, kWifiCipherUnknown
    }

    private String wifiName;
    private String wifiMac;
    private int frequency;
    private int state = kStateDisconnected;
    /**
     * wifi强度，分4格，4=满格，3=三格，2=两格，1=一格
     */
    private int strength;
    /**
     * wifi加密方式
     */
    private String capabilities;

    public WifiDetail() {

    }

    public void setWifiName(String wifiName) {
        this.wifiName = wifiName;
    }

    public String getWifiName() {
        return wifiName;
    }

    public void setWifiMac(String wifiMac) {
        this.wifiMac = wifiMac;
    }

    public String getWifiMac() {
        return wifiMac;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public int getFrequency() {
        return frequency;
    }

    public int getChannel() {
        return WifiConstant.getChannel(frequency);
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getState() {
        return state;
    }

    public void setStrength(int level) {
        if (Math.abs(level) < 50) {
            this.strength = kStrengthFull;
        } else if (Math.abs(level) < 75) {
            this.strength = kStrengthThreePiece;
        } else if (Math.abs(level) < 90) {
            this.strength = kStrengthTwoPiece;
        } else {
            this.strength = kStrengthOnePiece;
        }
    }

    public int getStrength() {
        return strength;
    }

    public void setCapabilities(String capabilities) {
        this.capabilities = capabilities;
    }

    public String getCapabilities() {
        StringBuilder stringBuilder = new StringBuilder();
        WifiCipherType wifiCipherType = getWifiCipherType();
        if (wifiCipherType == WifiCipherType.kWifiCipherWpa) {
            if (capabilities.contains("WPA-")) {
                stringBuilder.append("WPA");
            }
            if (capabilities.contains("WPA2")) {
                if (stringBuilder.toString().contains("WPA")) {
                    stringBuilder.append("/");
                }
                stringBuilder.append("WPA2");
            }
            if (capabilities.contains("WPS")) {
                if (!TextUtils.isEmpty(stringBuilder.toString())) {
                    stringBuilder.append(" ");
                }
                stringBuilder.append("WPS");
            }
        } else if (wifiCipherType == WifiCipherType.kWifiCipherWep) {
            stringBuilder.append("WEP");
        } else {
            stringBuilder.append("未加密");
        }
        return stringBuilder.toString();
    }

    public boolean isEncrypted() {
        WifiCipherType wifiCipherType = getWifiCipherType();
        return wifiCipherType == WifiCipherType.kWifiCipherWep || wifiCipherType == WifiCipherType.kWifiCipherWpa;
    }

    public WifiCipherType getWifiCipherType() {
        if (TextUtils.isEmpty(capabilities)) {
            return WifiCipherType.kWifiCipherUnknown;
        } else if (capabilities.contains("WEP")) {
            return WifiCipherType.kWifiCipherWep;
        } else if (capabilities.contains("WPA") || capabilities.contains("WPA2") || capabilities.contains("WPS")) {
            return WifiCipherType.kWifiCipherWpa;
        } else {
            return WifiCipherType.kWifiCipherNoPassword;
        }
    }

    @Override

    public int compareTo(WifiDetail o) {
        if (o.state != state) {
            return o.state - state;
        } else {
            return o.strength - strength;
        }
    }
}
