package com.gy.mywifi.base;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.gy.mywifi.App.MyApplication;
import com.gy.mywifi.R;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by algorithm on 2017/10/24.
 */

public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.top_dc)
    ImageView top_dc;
    @BindView(R.id.top_time)
    TextView top_time;
    @BindView(R.id.top_wifi)
    ImageView top_wifi;
    @BindView(R.id.top_sim_wifi)
    ImageView top_sim_wifi;
    @BindView(R.id.top_sim_type)
    TextView top_sim_type;
    @BindView(R.id.top_yys)
    TextView top_yys;
    @BindView(R.id.top_dc_cd)
    ImageView top_dc_cd;

    public ImageView top_wifi_hot;


    private static final String url = "http://www.baidu.com";
    long mNetTime = 0;
    final int RefreshGap = 5000;
    boolean isWFW=false;

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    refreshTime();
                    sendEmptyMessageDelayed(1, RefreshGap);
                    mNetTime += RefreshGap;
                    break;
            }
        }
    };


    protected static String TAG = "BaseActivity";
    /**
     * 当前应用环境
     */
    public MyApplication mApplication;
    protected Activity mContext;

    protected Unbinder mUnBinder;

    protected ProgressDialog mProgressDialog;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //设置TAG 和 context
        TAG = this.getClass().getSimpleName();
        mApplication = (MyApplication) getApplication();
        mContext = this;
        //设置layout布局之前
        beforeSetView();

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //设置layout布局并绑定布局控件
        setContentView(onSetContentView());
        mUnBinder = ButterKnife.bind(this);

        top_wifi_hot=findViewById(R.id.top_wifi_hot);

        //获取Intent
        onGetIntent();
        //初始化
        init();
        //设置事件监听
        setListener();
        //初始化数据
        onInitDataWithsavedInstanceState(savedInstanceState);
        onInitData();
        // 设置Activity禁止横屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }


    public void getYYS() {
        TelephonyManager telManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String name=telManager.getSimOperatorName();
        if (!name.isEmpty()){
            isWFW=false;
            top_yys.setText(name);
        }else {
            isWFW=true;
            top_yys.setText("无服务");
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getYYS();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    protected void init() {
        mProgressDialog = new ProgressDialog(mContext);
        initReceiver();
    }

    public BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context arg0, Intent arg1) {
            if (arg1.getAction().equals(Intent.ACTION_BATTERY_CHANGED)){
                int level = arg1.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
                int scale = arg1.getIntExtra(BatteryManager.EXTRA_SCALE, 0);
                /*电量百分比*/
                int levelPercent = (int) (((float) level / scale) * 100);
                if (levelPercent<=10){
                    top_dc.setImageResource(R.drawable.top_dc1);
                }else if (levelPercent<=20){
                    top_dc.setImageResource(R.drawable.top_dc2);
                }else if (levelPercent<=40){
                    top_dc.setImageResource(R.drawable.top_dc3);
                }else if (levelPercent<=60){
                    top_dc.setImageResource(R.drawable.top_dc4);
                }else if (levelPercent<=80){
                    top_dc.setImageResource(R.drawable.top_dc5);
                }else if (levelPercent<=100){
                    top_dc.setImageResource(R.drawable.top_dc6);
                }

                int status = arg1.getIntExtra(BatteryManager.EXTRA_STATUS, BatteryManager.BATTERY_STATUS_UNKNOWN);;
                switch (status) {
                    case BatteryManager.BATTERY_STATUS_CHARGING:
                        /*充电中……*/
                        top_dc_cd.setImageResource(R.drawable.top_dc_cd);
                        top_dc_cd.setVisibility(View.VISIBLE);
                        break;
                    case BatteryManager.BATTERY_STATUS_DISCHARGING:
                        /*放电中……*/
                    case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                        /*未充电*/
                    case BatteryManager.BATTERY_STATUS_FULL:
                        /*充电完成*/
                    default:
                        top_dc_cd.setVisibility(View.GONE);
                }
            }else if (arg1.getAction().equals(WifiManager.WIFI_STATE_CHANGED_ACTION)){
                int wifistate = arg1.getIntExtra(
                        WifiManager.EXTRA_WIFI_STATE,
                        WifiManager.WIFI_STATE_DISABLED);
                Log.i("ccccccccc","wifistate=="+wifistate);
                if (wifistate == WifiManager.WIFI_STATE_DISABLED) {
                    top_wifi.setVisibility(View.GONE);
                } else if (wifistate == WifiManager.WIFI_STATE_ENABLED) {
                    top_wifi.setVisibility(View.VISIBLE);
                    updateWifiStrength();
                }
            }else if (arg1.getAction().equals(WifiManager.RSSI_CHANGED_ACTION)){
                updateWifiStrength();
            }
        }
    };


    private void initReceiver(){
        IntentFilter intentFilter = new IntentFilter();
        //蓝牙相关广播
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiManager.RSSI_CHANGED_ACTION);

        registerReceiver(mReceiver, intentFilter);

        new  NetTimeTask().execute(null, null, null);
        phoneSignalStrengthsChanged();
    }

    /**
     * 信号质量的改变
     */
    private void phoneSignalStrengthsChanged(){
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        final int type = telephonyManager.getNetworkType();
        PhoneStateListener phoneStateListener = new PhoneStateListener() {

            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onSignalStrengthsChanged(SignalStrength signalStrength) {
                // TODO Auto-generated method stub
                super.onSignalStrengthsChanged(signalStrength);
                int  strength = signalStrength
                        .getGsmSignalStrength();
                if (isWFW){
                    top_sim_wifi.setVisibility(View.GONE);
                }else if (strength==0){
                    top_sim_wifi.setVisibility(View.GONE);
                }else if (strength>0&&strength<=10){
                    top_sim_wifi.setImageResource(R.drawable.top_xh4);
                }else if (strength>10&&strength<20){
                    top_sim_wifi.setImageResource(R.drawable.top_xh3);
                }else if (strength>20&&strength<=30){
                    top_sim_wifi.setImageResource(R.drawable.top_xh2);
                }else {
                    top_sim_wifi.setImageResource(R.drawable.top_xh1);
                }
                if (isWFW){
                    top_sim_type.setText("");
                }else if (type == TelephonyManager.NETWORK_TYPE_UMTS
                        || type == TelephonyManager.NETWORK_TYPE_HSDPA) {
                    top_sim_type.setText("3G");
                } else if (type == TelephonyManager.NETWORK_TYPE_GPRS
                        || type == TelephonyManager.NETWORK_TYPE_EDGE) {
                    top_sim_type.setText("2G");
                }else if(type==TelephonyManager.NETWORK_TYPE_CDMA){
                    top_sim_type.setText("2G");
                }else if(type==TelephonyManager.NETWORK_TYPE_EVDO_0
                        ||type==TelephonyManager.NETWORK_TYPE_EVDO_A|| type==TelephonyManager.NETWORK_TYPE_EVDO_B){
                    top_sim_type.setText("3G");
                }else if (type==TelephonyManager.NETWORK_TYPE_LTE){
                    top_sim_type.setText("4G");
                }else if (type==TelephonyManager.NETWORK_TYPE_1xRTT){
                    top_sim_type.setText("");
                }else{
                    top_sim_type.setText("");
                }
            }
        };

        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_SERVICE_STATE
                |PhoneStateListener.LISTEN_SIGNAL_STRENGTHS
                |PhoneStateListener.LISTEN_CALL_STATE
                |PhoneStateListener.LISTEN_DATA_CONNECTION_STATE
                |PhoneStateListener.LISTEN_DATA_ACTIVITY);
    }



    /*wifi 变化*/
    private void updateWifiStrength(){
        int strength = getStrength(this);
        Log.i("ccccccccc","strength=="+strength);
        if (strength >= 0 && strength <= 1){
            top_wifi.setImageResource(R.drawable.top_wifi4);
        }else if (strength>1&&strength<=2){
            top_wifi.setImageResource(R.drawable.top_wifi3);
        }else if (strength>2&&strength<=3){
            top_wifi.setImageResource(R.drawable.top_wifi2);
        }else {
            top_wifi.setImageResource(R.drawable.top_wifi1);
        }
    }


    public int getStrength(Context context) {
        WifiManager wifiManager = (WifiManager) context
                .getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifiManager.getConnectionInfo();
        if (info.getBSSID() != null) {
            int strength = WifiManager.calculateSignalLevel(info.getRssi(), 5);
            // 链接速度
            // int speed = info.getLinkSpeed();
            // // 链接速度单位
            // String units = WifiInfo.LINK_SPEED_UNITS;
            // // Wifi源名称
            // String ssid = info.getSSID();
            return strength;

        }
        return 0;
    }

    final DateFormat df = new SimpleDateFormat("HH:mm", Locale.CHINA);
    private void refreshTime(){
        if (0==mNetTime){
            top_time.setText(df.format(new Date(System.currentTimeMillis())));
        }else {
            top_time.setText(df.format(new Date(mNetTime)));
        }
    }




    /**
     * 设置layout之前的操作
     */
    protected void beforeSetView() {
        
    }

    /**
     * 设置layout布局
     * @return
     */
    protected abstract int onSetContentView();
   
    /**
     * 在 onInitData() 处理数据之前 接受Activity间传递的数据
     */
    protected void onGetIntent() {}

    /**
     * 设置监听事件
     */
    protected void setListener() {}
    
    protected abstract void onInitData();

    public  void onInitDataWithsavedInstanceState(Bundle savedInstanceState){

    }
    @Override
    public void onClick(View v) {}




    //==================================常用方法=======================================

    /**
     * 通过Class跳转界面
     **/
    protected void startActivity(Class<?> cls) {
        startActivity(cls, null);
    }

    /**
     * 含有Bundle通过Class跳转界面
     **/
    protected void startActivity(Class<?> cls, Bundle bundle) {
        Intent intent = new Intent();
        intent.setClass(this, cls);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }

    /**
     * 通过Action跳转界面
     **/
    protected void startActivity(String action) {
        startActivity(action, null);
    }

    /**
     * 含有Bundle通过Action跳转界面
     **/
    protected void startActivity(String action, Bundle bundle) {
        Intent intent = new Intent();
        intent.setAction(action);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }

    /**
     * 含有Bundle通过Class 返回跳转界面
     *
     * @param cls
     * @param bundle
     * @param requestCode
     */
    protected void startActivityForResult(Class<?> cls, Bundle bundle, int requestCode) {
        Intent intent = new Intent();
        intent.setClass(this, cls);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivityForResult(intent, requestCode);
    }

    /**
     * 返回跳转界面
     *
     * @param cls
     * @param requestCode
     */
    protected void startActivityForResult(Class<?> cls, int requestCode) {
        startActivityForResult(cls, null, requestCode);
    }

    /**
     * 常见头布局的公共方法，其他的用  mToolBar.inflateMenu(R.menu.menu)，添加菜单
     *
     * @param toolbar
     * @param textView
     * @param canBack  是否显示返回键
     * @param title    标题
     */
    protected void setToolbar(Toolbar toolbar, @NonNull TextView textView, boolean canBack, String title) {
        textView.setText(title);
        if (canBack) {
            if (null != toolbar) {
                setSupportActionBar(toolbar);
            }
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            toolbar.setNavigationIcon(R.drawable.back);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
    }


    class NetTimeTask extends AsyncTask<Object, Object, Long> {

        @Override
        protected Long doInBackground(Object... objects) {
            return updateNetTime();
        }

        @Override
        protected void onPostExecute(Long aLong) {
            mNetTime = aLong;
            mHandler.sendEmptyMessage(1);
            super.onPostExecute(aLong);
        }
    }

    private long updateNetTime() {
        long now = 0;
        try {
            URLConnection uc = new URL(url).openConnection();
            uc.connect();
            now = uc.getDate();
        } catch (IOException e) {
        }
        return now;
    }

}
