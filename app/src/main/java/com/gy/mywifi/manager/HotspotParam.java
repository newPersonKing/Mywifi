package com.gy.mywifi.manager;

public class HotspotParam {

    public String hotspotSSID;

    // 密码最少8个字节最多32个字节
    public String hotspotPwd;

    // 加密方式
    public int encryptionType;

    // 最大连接数(1-8之间)
    public int maxClientsNum;

    // 已连接的数量
    public int connectedClientsNum;

    // 已连接的设备名称
    public String[] connectedClients;

    // 黑名单，最多设置10个
    public String[] blackList;

    // 黑名单数量
    public int blackListNum;

}
