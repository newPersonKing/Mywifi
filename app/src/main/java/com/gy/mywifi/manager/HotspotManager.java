package com.gy.mywifi.manager;

import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.gy.mywifi.bean.ConnectHotClient;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class HotspotManager {

    private static final String Tag = "HotspotManager";
    public static WifiManager mWifiManager;

    /**
     * 设置热点状态，会对应的打开或关闭WiFi
     *
     * @param state
     *            要设置的状态
     * @param ssid
     *            热点名称，can be null
     * @param pwd
     *            热点密码，can be null
     * @return 设置成功与否
     */
    public static boolean setHotPotState(boolean state, String ssid, String pwd) {
        boolean isOpened = isHotPotTurnedOn();
        if (state == isOpened) {
            Log.i(Tag, "-> Set state is equal with hotpot state!");
            return false;
        }

        Log.i(Tag, "set hot pot state : " + state);
        try {
            Log.i("cccccccccc","1323");
            WifiConfiguration apConfig = getHotPotConfig();
            // 通过反射调用设置热点
            Method method = WifiManager.class.getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
            mWifiManager.setWifiEnabled(!state);
            if (ssid != null) {
                apConfig.SSID = ssid;
            }
            if (pwd != null) {
                apConfig.preSharedKey = pwd;
            }
            Log.i("cccccccccc","1323");
            return (boolean) method.invoke(mWifiManager,apConfig, state);
        } catch (Exception e) {
            Log.e(Tag, "Set hot pot " + state + " got exception : " + e.getLocalizedMessage());
            return false;
        }

    }



    /**
     * 热点是否打开
     * @return
     */
    public static boolean isHotPotTurnedOn() {
        try {
            Method method = mWifiManager.getClass().getDeclaredMethod("isWifiApEnabled");
            // 调用getWifiApState() ，获取返回值
            return (boolean) method.invoke(mWifiManager);
        } catch (Exception e) {
            Log.i(Tag, "Get hot pot state exception -> " + e.getLocalizedMessage());
            return false;
        }
    }

    /**
     * 获取当前热点的配置参数
     *
     * @return
     */
    public static HotspotParam getHotPotParams() {
        try {
            HotspotParam param = new HotspotParam();

            WifiConfiguration config = getHotPotConfig();
            if (config.SSID != null && !config.SSID.equals("")) {
                param.hotspotSSID = new String(config.SSID);
            } else {
                config.SSID = "";
            }

            if (config.preSharedKey != null && !config.preSharedKey.equals("")) {
                param.hotspotPwd = new String(config.preSharedKey);
            } else {
                param.hotspotPwd = "";
            }
            param.encryptionType = config.allowedKeyManagement.get(4) ? 4 : 0;
            List<?> tmp = getHotspotClients();
            if (tmp != null && tmp.size() > 0) {
                param.connectedClientsNum = 0;
                param.blackListNum = 0;
                for (int i = 0; i < tmp.size(); i++) {
                    HotspotClient client = (HotspotClient) tmp.get(i);
                    Log.d(Tag, "Got hotspot client -> " + client.deviceAddress);
                    if (!client.isBlocked) {
                        if (param.connectedClients == null) {
                            param.connectedClients = new String[tmp.size()];
                        }
                        param.connectedClients[param.connectedClientsNum] = new String(client.deviceAddress);
                        param.connectedClientsNum++;
                    } else {
                        if (param.blackList == null) {
                            param.blackList = new String[tmp.size()];
                        }
                        param.blackList[param.blackListNum] = new String(client.deviceAddress);
                        param.blackListNum++;
                    }
                }
            }

            return param;
        } catch (IllegalAccessException e) {
            Log.e(Tag, "getHotPotParams IllegalAccessException : " + e.getMessage());
            return null;
        } catch (NoSuchMethodException e) {
            Log.e(Tag, "getHotPotParams NoSuchMethodException : " + e.getMessage());
            return null;
        } catch (IllegalArgumentException e) {
            Log.e(Tag, "getHotPotParams IllegalArgumentException : " + e.getMessage());
            return null;
        } catch (InvocationTargetException e) {
            Log.e(Tag, "getHotPotParams InvocationTargetException : " + e.getMessage());
            return null;
        }
    }

    /**
     * 设置热点名称
     *
     *            名称长度 1-32bytes
     * @param ssid
     *            名称
     */
    public static boolean setHotPotSSID(String ssid) {
        try {
            WifiConfiguration config = getHotPotConfig();
//            String former = config.SSID;
//            Log.i(Tag, "former ssid:" + former + " set ssid:" + ssid);
//            config.SSID = ssid;
//            Log.i(Tag, "pwd:" + config.preSharedKey);
            return setWifiApConfiguration(config);
        } catch (NoSuchMethodException e) {
            Log.e(Tag, "setHotPotSSID NoSuchMethodException : " + e.getLocalizedMessage());
            return false;
        } catch (IllegalAccessException e) {
            Log.e(Tag, "setHotPotSSID IllegalAccessException : " + e.getLocalizedMessage());
            return false;
        } catch (IllegalArgumentException e) {
            Log.e(Tag, "setHotPotSSID IllegalArgumentException : " + e.getLocalizedMessage());
            return false;
        } catch (InvocationTargetException e) {
            Log.e(Tag, "setHotPotSSID InvocationTargetException : " + e.getLocalizedMessage());
            return false;
        }
    }

    /**
     * 设置热点密码
     *
     * @param pwd
     * @return
     */
    public static boolean setHotPotPwd(String pwd) {
        try {
            WifiConfiguration config = getHotPotConfig();
            String former = config.preSharedKey;
            Log.d(Tag, "former pwd:" + former + " set pwd:" + pwd);
            config.preSharedKey = pwd;
            return setWifiApConfiguration(config);
        } catch (NoSuchMethodException e) {
            Log.e(Tag, "setHotPotPwd NoSuchMethodException : " + e.getLocalizedMessage());
            return false;
        } catch (IllegalAccessException e) {
            Log.e(Tag, "setHotPotPwd IllegalAccessException : " + e.getLocalizedMessage());
            return false;
        } catch (IllegalArgumentException e) {
            Log.e(Tag, "setHotPotPwd IllegalArgumentException : " + e.getLocalizedMessage());
            return false;
        } catch (InvocationTargetException e) {
            Log.e(Tag, "setHotPotPwd InvocationTargetException : " + e.getLocalizedMessage());
            return false;
        }
    }

    /**
     * 设置加密方式
     *
     * @param type
     *            0代表不加密，4代表WPA2_PSK
     * @return
     */
    public static boolean setHotPotEncryption(int type) {
        int tmp = 0;
        if (type == 0) {
            tmp = 0;
        } else {
            tmp = 4;
        }

        try {
            WifiConfiguration config = getHotPotConfig();
            int oldEnc = config.allowedKeyManagement.get(4) ? 4 : 0;

            Log.d(Tag, "former encryption:" + oldEnc + " set encryption:" + tmp);
            if (oldEnc == -1) {
                Log.e(Tag, "Unexpected here!!!加密方式只有两种选择,检查代码");
                oldEnc = 0;
            }

            if (oldEnc == tmp) {
                return true;
            }

            config.allowedKeyManagement.clear();
            config.allowedKeyManagement.set(tmp);
            if (tmp == 4) {
                config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN); // WPA2_PSK
            } else {
                config.wepTxKeyIndex = 0;
                config.wepKeys[0] = "";
            }
            return setWifiApConfiguration(config);
        } catch (NoSuchMethodException e) {
            Log.e(Tag, "setHotPotEncryption NoSuchMethodException : " + e.getLocalizedMessage());
            return false;
        } catch (IllegalAccessException e) {
            Log.e(Tag, "setHotPotEncryption IllegalAccessException : " + e.getLocalizedMessage());
            return false;
        } catch (IllegalArgumentException e) {
            Log.e(Tag, "setHotPotEncryption IllegalArgumentException : " + e.getLocalizedMessage());
            return false;
        } catch (InvocationTargetException e) {
            Log.e(Tag,
                    "setHotPotEncryption InvocationTargetException : " + e.getLocalizedMessage());
            return false;
        }
    }

    /**
     * 设置黑名单
     * @param deviceAddr
     * @return
     */
    public static boolean blockClient(String deviceAddr) {
        try {
            // 获取当前连接的客户端
            List<?> list = getHotspotClients();
            if (list == null || (list != null && list.size() == 0)) {
                Log.e(Tag, "blockClient current connected clients is null!");
                return false;
            }

            // 通过反射获取设置黑名单方法
            Method method = null;
            Method[] methods = WifiManager.class.getMethods();
            for (Method method2 : methods) {
                if (method2.getName().equals("blockClient")) {
                    method = method2;
                    break;
                }
            }

            if (method == null) {
                Log.e(Tag, "blockClient cannot find blockClient method");
                return false;
            }

            for (int i = 0; i < list.size(); i++) {
                HotspotClient client = (HotspotClient) list.get(i);
                if (client.deviceAddress.equals(deviceAddr) && !client.isBlocked) {
                    return (boolean) method.invoke(mWifiManager, client);
                }
            }

            return false;
        } catch (IllegalAccessException e) {
            Log.e(Tag, "blockClient exception : " + e.getLocalizedMessage());
            return false;
        } catch (IllegalArgumentException e) {
            Log.e(Tag, "blockClient exception : " + e.getLocalizedMessage());
            return false;
        } catch (InvocationTargetException e) {
            Log.e(Tag, "blockClient exception : " + e.getLocalizedMessage());
            return false;
        }
    }

    /**
     * 移出黑名单
     * @param addr
     * @return
     */
    public static boolean unblockClient(String addr) {
        // 通过反射获取设置黑名单方法
        Method method = null;
        Method[] methods = WifiManager.class.getMethods();
        for (Method method2 : methods) {
            if (method2.getName().equals("unblockClient")) {
                method = method2;
                break;
            }
        }

        if (method == null) {
            Log.e(Tag, "unblockClient cannot find unblockClient method");
            return false;
        }

        Log.d(Tag, "unblock client -> " + addr);
        HotspotClient client = new HotspotClient(addr, true);
        try {
            return (boolean) method.invoke(mWifiManager, client);
        } catch (IllegalAccessException e) {
            Log.e(Tag, "unblockClient exception : " + e.getLocalizedMessage());
            return false;
        } catch (IllegalArgumentException e) {
            Log.e(Tag, "unblockClient exception : " + e.getLocalizedMessage());
            return false;
        } catch (InvocationTargetException e) {
            Log.e(Tag, "unblockClient exception : " + e.getLocalizedMessage());
            return false;
        }
    }

    /**
     * 设置热点配置
     * @param config
     * @return
     */
    private static boolean setWifiApConfiguration(WifiConfiguration config)
            throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Method method = null;
        Method[] methods = WifiManager.class.getMethods();
        for (Method method2 : methods) {
            if (method2.getName().equals("setWifiApConfiguration")) {
                method = method2;
                break;
            }
        }
        if (method == null) {
            return false;
        }
        return (boolean) method.invoke(mWifiManager, config);
    }

    @SuppressWarnings("unused")
    private static ArrayList<String> getCurrentConnectedClients() {
        ArrayList<String> connected = null;
        try {
            File file = new File("/proc/net/arp");
            if (!file.exists()) {
                return null;
            }

            connected = new ArrayList<>();
            FileReader fr = new FileReader(file);
            BufferedReader reader = new BufferedReader(fr);
            String line = reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split("[ ]+");
                if (tokens != null && tokens.length >= 4) {
                    String flag = tokens[2];// 连接状态，这个也不准确
                    String HWAddr = tokens[3];// 地址
                    if (!HWAddr.equals("00:00:00:00:00:00")) {
                        connected.add(HWAddr);
                        Log.d(Tag, "地址：" + HWAddr + ",状态：" + flag);
                    }
                }
            }
            reader.close();
            if (connected.size() > 0) {
                return connected;
            } else
                return null;
        } catch (Exception e) {
            Log.e(Tag, "Get connected clients exception : " + e.getLocalizedMessage());
            return null;
        }
    }

    /**
     * 获取当前连接到热点的客户端
     * @return
     */
    private static List<?> getHotspotClients()
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Method method = null;
        Method[] methods = WifiManager.class.getMethods();
        for (Method method2 : methods) {
            if (method2.getName().equals("getHotspotClients")) {
                method = method2;
                break;
            }
        }
        if (method == null) {
            return null;
        }

        return (List<?>) method.invoke(mWifiManager);
    }


    /*获取当前连接到热点的客户端*/
    public static List<ConnectHotClient> getGYHotspotClients(){
        ArrayList<ConnectHotClient> connectHotClients = new ArrayList<ConnectHotClient>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(
                    "/proc/net/arp"));
            String line;
            while ((line = br.readLine()) != null) {
                String[] splitted = line.split(" +");
                if (splitted != null && splitted.length >= 4) {
                    String ip = splitted[0];
                    Log.i("ccccccccc","line==="+line);
                    if (splitted[0].equals("IP")){
                        continue;
                    }else {
                        ConnectHotClient connectHotClient=new ConnectHotClient();
                        connectHotClient.setIp(splitted[0]);
                        connectHotClient.setMac(splitted[3]);
                        connectHotClients.add(connectHotClient);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return connectHotClients;
    }

    /**
     * 获取当前热点配置
     * @return
     */
    private static WifiConfiguration getHotPotConfig()
            throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Log.i("cccccc","12123");
        Method method = WifiManager.class.getDeclaredMethod("getWifiApConfiguration");
        Log.i("cccccc","12123");
        method.setAccessible(true);
        Log.i("cccccc","12123"+mWifiManager);
        WifiConfiguration config = (WifiConfiguration) method.invoke(mWifiManager);
        Log.i("cccccc","12123");
        return config;
    }

}
