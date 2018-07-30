package com.gy.mywifi.manager;

import android.net.wifi.WifiConfiguration;
import android.util.SparseArray;

import java.io.Serializable;

/**
 * Created by huison on 2018/6/5.
 */

public class WifiApServer implements Serializable {

    public static final int kCipherNoPassword = 0;
    public static final int kCipherTypeWep = 1;
    public static final int kCipherTypeWpa = 2;

    private static SparseArray<String> cipherSpareArray = new SparseArray<>();

    static {
        cipherSpareArray.append(kCipherNoPassword, "NONE");
        cipherSpareArray.append(kCipherTypeWep, "WPA_EAP");
        cipherSpareArray.append(kCipherTypeWpa, "WPA2_PSK");
    }

    private String SSID;
    private String password;
    // 加密方式 kCipherNoPassword、kCipherTypeWep、kCipherTypeWpa
    private int cipherType = kCipherNoPassword;

    public WifiApServer(String SSID, String password, int cipherType) {
        this.SSID = SSID;
        this.password = password;
        this.cipherType = cipherType;
    }

    public String getSSID() {
        return SSID == null ? "" : SSID;
    }

    public String getPassword() {
        return password == null ? "" : password;
    }

    public int getCipherType() {
        return cipherType;
    }

    public String getCipherTypeDesc() {
        return cipherSpareArray.get(cipherType);
    }

    /**
     * litIndex 用于wifiConfiguration.allowedKeyManagement.set(bitIndex)设置热点密码
     * 有如下几种 WifiConfiguration.KeyMgmt.strings = { "NONE", "WPA_PSK", "WPA_EAP", "IEEE8021X", "WPA2_PSK", "OSEN" }
     * 以上为默认api顺序，但不同设备也会不一样
     * 指定安全性为WPA_PSK，在不支持WPA_PSK的手机上看不到密码
     *
     * @return litIndex
     */
    public int getCipherBitIndex() {
        String cipherTypeDesc = getCipherTypeDesc();
        for (int bitIndex = 0, len = WifiConfiguration.KeyMgmt.strings.length; bitIndex < len; bitIndex++) {
            if (WifiConfiguration.KeyMgmt.strings[bitIndex].equals(cipherTypeDesc)) {
                return bitIndex;
            }
        }
        return 0;
    }

    public static int getCipherType(String cipherTypeDesc) {
        int index = cipherSpareArray.indexOfValue(cipherTypeDesc);
        if (index < 0) {
            return kCipherNoPassword;
        } else {
            return cipherSpareArray.keyAt(index);
        }
    }
}
