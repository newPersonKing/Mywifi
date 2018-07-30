package com.gy.mywifi.manager;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;


import com.gy.mywifi.bean.WifiApClient;
import com.gy.mywifi.bean.WifiDetail;
import com.gy.mywifi.untils.HandlerUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by huison on 2018/5/27.
 * Wifi控制器
 */

public class WifiController {

    public static final int kWifiApStateDisabling = 10;
    public static final int kWifiApStateDisabled = 11;
    public static final int kWifiApStateEnabling = 12;
    public static final int kWifiApStateEnabled = 13;
    public static final int kWifiApStateFailed = 14;

    private static final int kReachableTimeOut = 300;

    private static volatile WifiController sInstance;

    private final Object syncSignal = new Object();

    private WifiManager wifiManager;

    private WifiBroadcastReceiver wifiBroadcastReceiver;
    private List<IWifiListener> wifiListeners;
    private List<WifiDetail> wifiDetails;

    private ExecutorService executorService;

    public interface WifiConnectCallback {
        void onWifiConnect(boolean success, String error);
    }

    private WifiController() {
        wifiListeners = new ArrayList<>();
        wifiDetails = new ArrayList<>();
        wifiBroadcastReceiver = new WifiBroadcastReceiver(this);
        executorService = Executors.newSingleThreadExecutor();
    }

    public static WifiController instance() {
        if (sInstance == null) {
            synchronized (WifiController.class) {
                if (sInstance == null) {
                    sInstance = new WifiController();
                }
            }
        }
        return sInstance;
    }

    public static boolean isSettingCanWrite(Context context) {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M ||
                (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Settings.System.canWrite(context));
    }

    public void init(Context context) {
        wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        wifiApSharePref = context.getSharedPreferences("wifi_ap_clients", Context.MODE_APPEND);
    }

    public void addWifiListener(IWifiListener wifiListener) {
        synchronized (syncSignal) {
            if (wifiListener != null) {
                wifiListeners.add(wifiListener);
            }
        }
    }

    public void removeWifiListener(IWifiListener wifiListener) {
        synchronized (syncSignal) {
            wifiListeners.remove(wifiListener);
        }
    }

    public boolean isWifiOpen() {
        return wifiManager.isWifiEnabled();
    }

    public WifiInfo getConnectionWifiInfo() {
        return wifiManager.getConnectionInfo();
    }

    public WifiConfiguration isHistoryConnection(WifiDetail wifiDetail) {
        List<WifiConfiguration> historyConnections = wifiManager.getConfiguredNetworks();
        for (WifiConfiguration configuration : historyConnections) {
            if (configuration.SSID.equals("\"" + wifiDetail.getWifiName() + "\"")) {
                return configuration;
            }
        }
        return null;
    }

    public WifiConfiguration genWifiConfiguration(WifiDetail wifiDetail, String password) {
        WifiConfiguration wifiConfiguration = new WifiConfiguration();
        wifiConfiguration.allowedAuthAlgorithms.clear();
        wifiConfiguration.allowedGroupCiphers.clear();
        wifiConfiguration.allowedKeyManagement.clear();
        wifiConfiguration.allowedPairwiseCiphers.clear();
        wifiConfiguration.allowedProtocols.clear();
        wifiConfiguration.SSID = "\"" + wifiDetail.getWifiName() + "\"";

        switch (wifiDetail.getWifiCipherType()) {
            case kWifiCipherNoPassword:
                wifiConfiguration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                break;
            case kWifiCipherWep:
                wifiConfiguration.preSharedKey = "\"" + password + "\"";
                wifiConfiguration.hiddenSSID = true;
                wifiConfiguration.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
                wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
                wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
                wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
                wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
                wifiConfiguration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                wifiConfiguration.wepTxKeyIndex = 0;
                break;
            case kWifiCipherWpa:
                wifiConfiguration.preSharedKey = "\"" + password + "\"";
                wifiConfiguration.hiddenSSID = true;
                wifiConfiguration.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
                wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
                wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
                wifiConfiguration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
                wifiConfiguration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
                wifiConfiguration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
                wifiConfiguration.status = WifiConfiguration.Status.ENABLED;
                break;
            default:
                break;
        }
        return wifiConfiguration;
    }

    public boolean openWifi() {
        if (!wifiManager.isWifiEnabled()) {
            return wifiManager.setWifiEnabled(true);
        }
        return false;
    }

    public boolean closeWifi() {
        if (wifiManager.isWifiEnabled()) {
            return wifiManager.setWifiEnabled(false);
        }
        return false;
    }

    private WifiConnectCallback wifiConnectCallback;

    public void connectToWifi(WifiConfiguration wifiConfiguration, WifiConnectCallback wifiConnectCallback) {
        this.wifiConnectCallback = wifiConnectCallback;
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        if (wifiInfo != null) {
            wifiManager.disableNetwork(wifiInfo.getNetworkId());
        }

        boolean success;
        if (wifiConfiguration.networkId > 0) {
            success = wifiManager.enableNetwork(wifiConfiguration.networkId, true);
            wifiManager.updateNetwork(wifiConfiguration);
            if (!success) {
                if (wifiConnectCallback != null) {
                    wifiConnectCallback.onWifiConnect(false, "网络繁忙");
                }
            }
        } else {
            int networkId = wifiManager.addNetwork(wifiConfiguration);
            if (networkId > 0) {
                wifiManager.saveConfiguration();
                success = wifiManager.enableNetwork(networkId, true);
                if (!success) {
                    if (wifiConnectCallback != null) {
                        wifiConnectCallback.onWifiConnect(false, "网络繁忙");
                    }
                }
            } else {
                if (wifiConnectCallback != null) {
                    wifiConnectCallback.onWifiConnect(false, "密码错误");
                }
            }
        }
    }

    public boolean disconnectWifi() {
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        if (wifiInfo != null) {
            return wifiManager.disableNetwork(wifiInfo.getNetworkId());
        }
        return false;
    }

    public boolean forgetWifi() {
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        if (wifiInfo != null) {
            return forgetWifi(wifiInfo.getNetworkId());
        }
        return false;
    }

    public boolean forgetWifi(int netWorkId) {
        boolean remove = wifiManager.removeNetwork(netWorkId);
        boolean save = wifiManager.saveConfiguration();
        return remove && save;
    }

    public List<WifiDetail> getWifiDetails() {
        List<WifiDetail> wifiDetails = distinctSameScanResult(wifiManager.getScanResults());
        updateWifiConnection(WifiDetail.kStateConnected);
        return wifiDetails;
    }

    public void registerWifiReceiver(Context context) {
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        filter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION);
        context.registerReceiver(wifiBroadcastReceiver, filter);
    }

    public void unregisterWifiReceiver(Context context) {
        context.unregisterReceiver(wifiBroadcastReceiver);
    }

    private void handleWifiStateChanged(int state) {
        for (IWifiListener listener : wifiListeners) {
            if (listener != null) {
                listener.onWifiStateChanged(state);
            }
        }
    }

    private void handleNetworkStateChanged(NetworkInfo.State state) {
        WifiInfo wifiInfo;
        switch (state) {
            case DISCONNECTED:
                updateWifiConnection(WifiDetail.kStateDisconnected);
                notifyWifiScanChanged();
                break;
            case CONNECTING:
                wifiInfo = wifiManager.getConnectionInfo();
                if (wifiInfo != null) {
                    updateWifiConnection(WifiDetail.kStateConnecting);
                    notifyWifiScanChanged();
                }
                break;
            case CONNECTED:
                wifiInfo = wifiManager.getConnectionInfo();
                if (wifiInfo != null) {
                    updateWifiConnection(WifiDetail.kStateConnected);
                    notifyWifiScanChanged();
                }
                break;
            default:
                break;
        }
    }

    private void notifyWifiScanChanged() {
        for (IWifiListener listener : wifiListeners) {
            if (listener != null) {
                listener.onWifiScanChanged(wifiDetails);
            }
        }
    }

    private void handleWifiScanResultsChanged() {
        distinctSameScanResult(wifiManager.getScanResults());
        notifyWifiScanChanged();
    }

    private void handleWifiConnectStateChanged(Intent intent) {
        SupplicantState supplicantState = intent.getParcelableExtra(WifiManager.EXTRA_NEW_STATE);
        switch (supplicantState) {
            case INACTIVE:
            case DISCONNECTED:
                if (wifiConnectCallback != null) {
                    wifiConnectCallback.onWifiConnect(false, "密码错误");
                    wifiConnectCallback = null;
                }
                break;
            case COMPLETED:
                if (wifiConnectCallback != null) {
                    wifiConnectCallback.onWifiConnect(true, "连接成功");
                    wifiConnectCallback = null;
                }
            default:
                break;
        }
    }

    private void updateWifiConnection(int newState) {
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        if (wifiInfo != null && !TextUtils.isEmpty(wifiInfo.getSSID())) {
            // wifiInfo.getSSID()获取到的wifi名称比scanResult头尾多了""
            String wifiName = wifiInfo.getSSID();
            for (WifiDetail wifiDetail : wifiDetails) {
                if (wifiName.equals("\"" + wifiDetail.getWifiName() + "\"")) {
                    wifiDetail.setState(newState);
                } else {
                    wifiDetail.setState(WifiDetail.kStateDisconnected);
                }
            }
        }
    }

    private List<WifiDetail> distinctSameScanResult(List<ScanResult> scanResults) {
        for (ScanResult scanResult : scanResults) {
            if (!TextUtils.isEmpty(scanResult.SSID) && !isContains(scanResult.SSID)) {
                WifiDetail wifiDetail = new WifiDetail();
                wifiDetail.setWifiName(scanResult.SSID);
                wifiDetail.setWifiMac(scanResult.BSSID);
                wifiDetail.setFrequency(scanResult.frequency);
                wifiDetail.setStrength(scanResult.level);
                wifiDetail.setCapabilities(scanResult.capabilities);
                wifiDetail.setState(WifiDetail.kStateDisconnected);

                wifiDetails.add(wifiDetail);
            }
        }
        return wifiDetails;
    }

    private boolean isContains(String wifiName) {
        for (WifiDetail item : wifiDetails) {
            if (!TextUtils.isEmpty(item.getWifiName()) && item.getWifiName().equals(wifiName)) {
                return true;
            }
        }
        return false;
    }

    private static class WifiBroadcastReceiver extends BroadcastReceiver {

        WeakReference<WifiController> weakReference;

        WifiBroadcastReceiver(WifiController wifiController) {
            weakReference = new WeakReference<>(wifiController);
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            WifiController wifiController = weakReference.get();
            if (wifiController == null || intent == null) {
                return;
            }
            String action = intent.getAction();
            switch (action) {
                case WifiManager.WIFI_STATE_CHANGED_ACTION:
                    int state = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
                    wifiController.handleWifiStateChanged(state);
                    break;
                case WifiManager.NETWORK_STATE_CHANGED_ACTION:
                    NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                    wifiController.handleNetworkStateChanged(networkInfo.getState());
                    break;
                case WifiManager.SCAN_RESULTS_AVAILABLE_ACTION:
                    wifiController.handleWifiScanResultsChanged();
                    break;
                case WifiManager.SUPPLICANT_STATE_CHANGED_ACTION:
                    wifiController.handleWifiConnectStateChanged(intent);
                    break;
                default:
                    break;
            }
        }
    }

    /******************************************* 热点相关 **********************************************/


    private SharedPreferences wifiApSharePref;

    public boolean requestWritePermissionSettings(Activity activity, int requestCode) {
        if (!isSettingCanWrite(activity.getApplication())) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
            intent.setData(Uri.parse("package:" + activity.getApplication().getPackageName()));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivityForResult(intent, requestCode);
            return false;
        }
        return true;
    }

    public int getWifiApState() {
        try {
            Method method = wifiManager.getClass().getMethod("getWifiApState");
            return ((Integer) method.invoke(wifiManager));
        } catch (Throwable e) {
            e.printStackTrace();
            return kWifiApStateFailed;
        }
    }

    public boolean isWifiApOpen() {
        return getWifiApState() == kWifiApStateEnabled;
    }

    public boolean openWifiAp() {
        if (!isWifiApOpen()) {
            try {
                wifiManager.setWifiEnabled(false);
                Method method = wifiManager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
                return (Boolean) method.invoke(wifiManager, null, true);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public boolean closeWifiAp() {
        if (isWifiApOpen()) {
            try {
                Method method = wifiManager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
                return (Boolean) method.invoke(wifiManager, null, false);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public void scanWifiApClients() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                BufferedReader bufferedReader = null;
                final ArrayList<WifiApClient> wifiApClients = new ArrayList<>();
                try {
                    bufferedReader = new BufferedReader(new FileReader("/proc/net/arp"));
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        String[] splits = line.split(" +");
                        if (splits.length >= 6) {
                            String deviceMac = splits[3];
                            if (deviceMac.matches("..:..:..:..:..:..")) {
                                String deviceIp = splits[0];
                                String deviceName = /*splits[5]*/wifiApSharePref.getString(deviceIp, null);
                                boolean isReachable = isReachableByPing(deviceIp);
                                wifiApClients.add(new WifiApClient(deviceName, deviceIp, deviceMac, false));
                            }
                        }
                    }
                    HandlerUtils.runOnUIThread(new Runnable() {
                        @Override
                        public void run() {
                            for (IWifiApListener listener : wifiApListeners) {
                                if (listener != null) {
                                    listener.onScanWifiApClients(wifiApClients);
                                }
                            }
                        }
                    });
                } catch (Throwable e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (bufferedReader != null) {
                            bufferedReader.close();
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        executorService.execute(runnable);
    }

    public void saveWifiApClientName(String deviceIp, String deviceName) {
        wifiApSharePref.edit().putString(deviceIp, deviceName).apply();
    }

    public String getWifiApClientName(String deviceIp) {
        return wifiApSharePref.getString(deviceIp, "");
    }

    private static boolean isReachableByPing(String ip) {
        try {
            Process ipAddressProcess = Runtime.getRuntime().exec("/system/bin/ping -c 1 -w 3 " + ip);
            int exitValue = ipAddressProcess.waitFor();
            return (exitValue == 0);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 配置热点
     */
    public boolean createWifiApConfiguration(WifiApServer wifiApServer) {
        if (wifiApServer == null) {
            return false;
        }
        if (TextUtils.isEmpty(wifiApServer.getSSID()) || TextUtils.isEmpty(wifiApServer.getPassword())) {
            return false;
        }
        if (wifiApServer.getPassword().length() < 8) {
            return false;
        }
        if (wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(false);
        }
        try {
            Method method = wifiManager.getClass().getMethod("getWifiApConfiguration");
            WifiConfiguration wifiConfiguration = (WifiConfiguration) method.invoke(wifiManager);
            wifiConfiguration.SSID = wifiApServer.getSSID();
            wifiConfiguration.preSharedKey = wifiApServer.getPassword();
            wifiConfiguration.allowedKeyManagement.set(wifiApServer.getCipherBitIndex());
            method = wifiManager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
            return (Boolean) method.invoke(wifiManager, wifiConfiguration, true);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获取热点数据
     */
    public WifiApServer getWifiApConfiguration() {
        try {
            Method method = wifiManager.getClass().getMethod("getWifiApConfiguration");
            WifiConfiguration wifiConfiguration = (WifiConfiguration) method.invoke(wifiManager);
            if (wifiConfiguration == null) {
                return null;
            }
            String SSID = wifiConfiguration.SSID;
            String password = wifiConfiguration.preSharedKey;
            // Android 4.2.2异常，返回{}
            //使用apConfig.allowedKeyManagement.toString()返回{0}这样的格式，需要截取中间的具体数值
            //下面几种写法都可以
            //int index = Integer.valueOf(apConfig.allowedKeyManagement.toString().substring(1, 2));
            //int index = Integer.valueOf(String.valueOf(apConfig.allowedKeyManagement.toString().charAt(1)));
            //int index = Integer.valueOf(apConfig.allowedKeyManagement.toString().charAt(1)+"");
            int bitIndex = wifiConfiguration.allowedKeyManagement.toString().charAt(1) - '0';
            String cipherTypeDesc = WifiConfiguration.KeyMgmt.strings[bitIndex];
            return new WifiApServer(SSID, password, WifiApServer.getCipherType(cipherTypeDesc));
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * register when Activity.onResume()
     */
    public void registerWifiApReceiver(Context context, IWifiApListener listener) {
        synchronized (syncSignal) {
            if (listener != null && !wifiApListeners.contains(listener)) {
                wifiApListeners.add(listener);
            }
        }
        IntentFilter intentFilter = new IntentFilter("android.net.wifi.WIFI_AP_STATE_CHANGED");
        context.registerReceiver(wifiApReceiver, intentFilter);
    }

    /**
     * register when Activity.onPause()
     */
    public void unregisterWifiApReceiver(Context context, IWifiApListener listener) {
        synchronized (syncSignal) {
            if (listener != null) {
                wifiApListeners.remove(listener);
            }
        }
        context.unregisterReceiver(wifiApReceiver);
    }

    private List<IWifiApListener> wifiApListeners = new ArrayList<>();

    private final BroadcastReceiver wifiApReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            Log.i("ccccccccc","WIFI_AP_STATE_CHANGED");
            if ("android.net.wifi.WIFI_AP_STATE_CHANGED".equals(action)) {
                //便携式热点的状态为：10---正在关闭；11---已关闭；12---正在开启；13---已开启
                int state = intent.getIntExtra("wifi_state",  0);

                for (IWifiApListener listener : wifiApListeners) {
                    if (listener != null) {
                        listener.getState(state);
                    }
                }

                scanWifiApClients();
            }
        }
    };

    /**
     * 设置飞行模式开关
     */
    public void setAirplaneMode(Context context, boolean enabling) {
        try {
            Settings.System.putInt(context.getContentResolver(),
                    Settings.System.AIRPLANE_MODE_ON, enabling ? 1 : 0);
            Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
            intent.putExtra("state", enabling);
            context.sendBroadcast(intent);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * 是否开启飞行模式
     */
    public boolean isAirplaneModeOn(Context context) {
        int isAirplaneMode = Settings.System.getInt(context.getContentResolver(),
                Settings.System.AIRPLANE_MODE_ON, 0);
        return isAirplaneMode == 1;
    }

    /**
     * 设置移动网络开关
     */
    public void setMobileNetMode(Context context, boolean enabling) {
        try {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            Class ownerClass = mConnectivityManager.getClass();
            Class[] argsClass = new Class[1];
            argsClass[0] = boolean.class;
            Method method = ownerClass.getMethod("setMobileDataEnabled", argsClass);
            method.invoke(mConnectivityManager, enabling);
        } catch (Throwable e) {
            e.printStackTrace();
            Log.e("WifiController", "移动数据设置错误: " + e.toString());
        }
    }

    /**
     * 是否开启移动网络
     */
    public boolean isMobileNetOpen(Context context) {
        try {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            Class ownerClass = mConnectivityManager.getClass();
            Method method = ownerClass.getMethod("getMobileDataEnabled", null);
            Boolean isOpen = (Boolean) method.invoke(mConnectivityManager, null);
            return isOpen;
        } catch (Throwable e) {
            return false;
        }
    }
}
