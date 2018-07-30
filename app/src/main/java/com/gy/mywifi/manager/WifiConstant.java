package com.gy.mywifi.manager;

import android.util.SparseIntArray;

/**
 * Created by huison on 2018/6/3.
 */

public class WifiConstant {

    private static SparseIntArray kChannelFrequency = new SparseIntArray();

    static {
        kChannelFrequency.put(1, 2412);
        kChannelFrequency.put(2, 2417);
        kChannelFrequency.put(3, 2422);
        kChannelFrequency.put(4, 2427);
        kChannelFrequency.put(5, 2432);
        kChannelFrequency.put(6, 2437);
        kChannelFrequency.put(7, 2442);
        kChannelFrequency.put(8, 2447);
        kChannelFrequency.put(9, 2452);
        kChannelFrequency.put(10, 2457);
        kChannelFrequency.put(11, 2462);
        kChannelFrequency.put(12, 2467);
        kChannelFrequency.put(13, 2472);
        kChannelFrequency.put(14, 2484);

        kChannelFrequency.put(36, 5180);
        kChannelFrequency.put(40, 5200);
        kChannelFrequency.put(44, 5220);
        kChannelFrequency.put(48, 5240);
        kChannelFrequency.put(52, 5260);
        kChannelFrequency.put(56, 5280);
        kChannelFrequency.put(60, 5300);
        kChannelFrequency.put(64, 5320);
        kChannelFrequency.put(100, 5500);
        kChannelFrequency.put(104, 5520);
        kChannelFrequency.put(108, 5540);
        kChannelFrequency.put(112, 5560);
        kChannelFrequency.put(116, 5580);
        kChannelFrequency.put(120, 5600);
        kChannelFrequency.put(124, 5620);
        kChannelFrequency.put(128, 5640);
        kChannelFrequency.put(132, 5660);
        kChannelFrequency.put(136, 5680);
        kChannelFrequency.put(140, 5700);
        kChannelFrequency.put(149, 5745);
        kChannelFrequency.put(153, 5765);
        kChannelFrequency.put(157, 5785);
        kChannelFrequency.put(161, 5805);
    }

    public static int getChannel(int frequency) {
        int index = kChannelFrequency.indexOfValue(frequency);
        if (index > -1) {
            return kChannelFrequency.keyAt(index);
        }
        return -1;
    }
}
