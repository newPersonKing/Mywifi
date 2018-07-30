package com.gy.mywifi.manager;

import com.gy.mywifi.bean.WifiApClient;

import java.util.List;

/**
 * Created by huison on 2018/6/3.
 */

public interface IWifiApListener {

    void onScanWifiApClients(List<WifiApClient> wifiApClients);

    void getState(int state);
}
