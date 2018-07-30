package com.gy.mywifi.untils;

import android.os.Handler;
import android.os.Looper;

/**
 * Created by huison on 2018/6/3.
 */

public class HandlerUtils {

    private static Handler kHandler = new Handler(Looper.getMainLooper());

    public static void runOnUIThread(Runnable runnable) {
        kHandler.post(runnable);
    }

    public static void runOnUIThreadDelay(Runnable runnable, int delay) {
        kHandler.postDelayed(runnable, delay);
    }

    public static void removeRunnable(Runnable runnable) {
        kHandler.removeCallbacks(runnable);
    }

    public static void remove() {
        kHandler.removeCallbacksAndMessages(null);
    }
}
