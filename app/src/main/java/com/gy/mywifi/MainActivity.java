package com.gy.mywifi;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.net.Uri;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.gy.mywifi.activity.ConnetListActivity;
import com.gy.mywifi.activity.MakeWifiHotActivity;
import com.gy.mywifi.base.BaseTitleActivity;
import com.gy.mywifi.bean.WifiApClient;
import com.gy.mywifi.manager.HotspotManager;
import com.gy.mywifi.manager.IWifiApListener;
import com.gy.mywifi.manager.WifiAPUtil;
import com.gy.mywifi.manager.WifiApServer;
import com.gy.mywifi.manager.WifiController;

import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.OnClick;

public class MainActivity extends BaseTitleActivity {

    @BindView(R.id.wifi_hot_name)
    TextView wifi_hot_name;
    @BindView(R.id.turn_on)
    CheckBox turn_on;
    @BindView(R.id.connect_count)
    TextView connect_count;

    private Random random=new Random();
    boolean isSetting=false;
    @Override
    protected int onSetContentView() {
        return R.layout.activity_main;
    }

    @Override
    protected void onInitData() {
        setTitle(R.drawable.retur,"WLAN热点分享",0);
        WifiApServer wifiApServer=WifiController.instance().getWifiApConfiguration();
        if (wifiApServer!=null){
            wifi_hot_name.setText(wifiApServer.getSSID());
            isSetting=true;
        }else {
            isSetting=false;
            wifi_hot_name.setText("EUPIN"+random.nextInt(1000000));
            WifiAPUtil.getInstance(this).setNameAndPassword(wifi_hot_name.getText().toString(),"12345678", WifiAPUtil.WifiSecurityType.WIFICIPHER_WPA);
        }


        turn_on.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (!WifiController.instance().isMobileNetOpen(MainActivity.this)){
                    Toast.makeText(MainActivity.this,"请插入sim卡",Toast.LENGTH_SHORT).show();
                    turn_on.setChecked(false);
                    return;
                }

                turn_on.setChecked(isChecked);

                if (!WifiController.instance().requestWritePermissionSettings(MainActivity.this,200)){
                    turn_on.setChecked(false);
                    return;
                }

                if (!isChecked){
                    WifiController.instance().closeWifiAp();
                }else {
                    if (isSetting){
                        WifiController.instance().openWifiAp();
                    }else {
                        turn_on.setChecked(false);
                        Toast.makeText(MainActivity.this,"请先设置wifi热点",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_PHONE_STATE},200);
            return;
        }else {
            getYYS();
        }
    }
    IWifiApListener listener=new IWifiApListener() {
        @Override
        public void onScanWifiApClients(List<WifiApClient> wifiApClients) {
            if (WifiController.instance().isWifiApOpen()){
                connect_count.setText("已连接"+wifiApClients.size()+"台");
            }else {
                connect_count.setText("已连接"+0+"台");
            }
        }

        @Override
        public void getState(int state) {
            if (state==10||state==11){
                top_wifi_hot.setVisibility(View.GONE);
            }else {
                top_wifi_hot.setVisibility(View.VISIBLE);
            }
        }
    };
    @Override
    protected void onResume() {
        super.onResume();

        WifiApServer wifiApServer=WifiController.instance().getWifiApConfiguration();
        if (wifiApServer!=null) {
            wifi_hot_name.setText(wifiApServer.getSSID());
            isSetting = true;
        }

        turn_on.setChecked(WifiController.instance().isWifiApOpen());

        WifiController.instance().registerWifiApReceiver(this, listener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        WifiController.instance().unregisterWifiApReceiver(this,listener);

    }

    @OnClick({
            R.id.btn_make_wifi,
            R.id.btn_connect_number
    })
    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){

            case R.id.btn_make_wifi:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (WifiController.instance().requestWritePermissionSettings(MainActivity.this,200)){
                        Intent intent=new Intent(this,MakeWifiHotActivity.class);
                        startActivity(intent);
                    }
                }
                break;
            case R.id.btn_connect_number:
                Intent connect=new Intent(this,ConnetListActivity.class);
                startActivity(connect);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }
}
