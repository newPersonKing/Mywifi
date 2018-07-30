package com.gy.mywifi.activity;

import android.content.Intent;
import android.content.IntentFilter;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.Editable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.gy.mywifi.R;
import com.gy.mywifi.base.BaseTitleActivity;
import com.gy.mywifi.bean.WifiApClient;
import com.gy.mywifi.manager.IWifiApListener;
import com.gy.mywifi.manager.WifiAPUtil;
import com.gy.mywifi.manager.WifiApServer;
import com.gy.mywifi.manager.WifiController;
import com.gy.mywifi.untils.Constant;

import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.OnClick;

public class MakeWifiHotPasswordActivity extends BaseTitleActivity {

    public static final String WIFI_AP_STATE_CHANGED_ACTION = "android.net.wifi.WIFI_AP_STATE_CHANGED";
    public static final String EXTRA_WIFI_AP_STATE = "wifi_state";
    public static int WIFI_AP_STATE_DISABLING = 10;
    public static int WIFI_AP_STATE_DISABLED = 11;
    public static int WIFI_AP_STATE_ENABLING = 12;
    public static int WIFI_AP_STATE_ENABLED = 13;
    public static int WIFI_AP_STATE_FAILED = 14;

    Keyboard fuhao,fuhaoA,fuhaoB,fuhaoC ;
    Keyboard zhu ;
    Keyboard zhu_small ;

    boolean isBig=false;


    @BindView(R.id.et_wifi_password)
    EditText et_wifi_password;
    @BindView(R.id.btn_sure)
    Button btn_sure;
    @BindView(R.id.activity_main_keyboard)
    KeyboardView keyboardView;
    @BindView(R.id.img_first_center)
    ImageView img1;
    @BindView(R.id.img_two_left)
    ImageView img2;
    @BindView(R.id.img_two_center)
    ImageView img3;
    @BindView(R.id.img_two_right)
    ImageView img4;
    @BindView(R.id.img_three_center)
    ImageView img5;
    @BindView(R.id.img_alpha)
    ImageView img_alpha;
    @BindView(R.id.ll_over)
    LinearLayout ll_over;

    private EditText  edtInput;
    private String content;
    private Random random=new Random();
    int fuhaoIndex=1;
    @Override
    protected int onSetContentView() {
        return R.layout.layout_make_wifi_hot_password;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onInitData() {
        setTitle(R.drawable.retur,"配置WLAN热点密码",0);

        IntentFilter filter = new IntentFilter();
        filter.addAction(WIFI_AP_STATE_CHANGED_ACTION);

        fuhao = new Keyboard(this, R.xml.keybord_fuhao);// 符号键盘
        fuhaoA=new Keyboard(this,R.xml.keybord_fuhao_a);
        fuhaoB=new Keyboard(this,R.xml.keybord_fuhao_b);
        fuhaoC=new Keyboard(this,R.xml.keybord_fuhao_c);
        zhu = new Keyboard(this, R.xml.keybord_zhu); // 主键盘
        zhu_small=new Keyboard(this,R.xml.keybord_zhu_small);//小写的键盘
        getWindow().setSoftInputMode( WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

    /*    String pd="";
        for (int i=0;i<8;i++){

            int a=random.nextInt(100);
            if (a%2==0){
                pd+= Constant.zimu[random.nextInt(15)];
            }else {
                pd+= Constant.shuzi[random.nextInt(10)];
            }
        }*/
        WifiApServer wifiApServer=WifiController.instance().getWifiApConfiguration();
        if (wifiApServer!=null){
            et_wifi_password.setText(wifiApServer.getPassword());
        }

        keyboardView.setKeyboard(zhu_small);
        edtInput=et_wifi_password;


        img1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction()==MotionEvent.ACTION_DOWN){
                    appendContent(0);
                }
                img_alpha.setVisibility(View.GONE);
                ll_over.setVisibility(View.GONE);
                Log.i("cccccccccc","img1==="+img1);
                return false;
            }
        });

        img2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction()==MotionEvent.ACTION_DOWN){
                    appendContent(1);
                }
                img_alpha.setVisibility(View.GONE);
                ll_over.setVisibility(View.GONE);
                return false;
            }
        });

        img3.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction()==MotionEvent.ACTION_DOWN){
                    appendContent(2);
                }
                img_alpha.setVisibility(View.GONE);
                ll_over.setVisibility(View.GONE);
                return false;
            }
        });

        img4.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction()==MotionEvent.ACTION_DOWN){
                    appendContent(3);
                }
                img_alpha.setVisibility(View.GONE);
                ll_over.setVisibility(View.GONE);
                return false;
            }
        });

        img5.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction()==MotionEvent.ACTION_DOWN){
                    appendContent(4);
                }
                img_alpha.setVisibility(View.GONE);
                ll_over.setVisibility(View.GONE);
                return false;
            }
        });

        img_alpha.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                img_alpha.setVisibility(View.GONE);
                ll_over.setVisibility(View.GONE);
                return true;
            }
        });

        et_wifi_password.setShowSoftInputOnFocus(false);
        keyboardView.setOnKeyboardActionListener(new KeyboardView.OnKeyboardActionListener() {
            @Override
            public void onPress(int primaryCode) {

            }

            @Override
            public void onRelease(int primaryCode) {

            }

            @Override
            public void onKey(int primaryCode, int[] keyCodes) {

                Editable editable = edtInput.getText();
                int start = edtInput.getSelectionStart();
                switch (primaryCode) {
                    case Keyboard.KEYCODE_SHIFT:// 设置shift状态然后刷新页面

                        break;
                    case 11:// 点击删除键，长按连续删除
                        if (editable != null && editable.length() > 0 && start > 0) {
                            editable.delete(start - 1, start);
                        }
                        break;
                    case -11:// 自定义code，切换到字母键盘
//                        keyboardView.setKeyboard(numberKB);
                        break;
                    case -12:// 自定义code
                        // 切换到符号键盘，待实现
                        break;
                    case 0:
                        setVisible();
                        img1.setVisibility(View.INVISIBLE);
                        img5.setVisibility(View.INVISIBLE);
                        img2.setImageResource(R.drawable.a_0_click);
                        img3.setImageResource(R.drawable.a_1_click);
                        img4.setImageResource(R.drawable.kg_click);
                        content=" 01 ";
                        break;
                    case 1:
                        setVisible();
                        img1.setImageResource(R.drawable.a_2_click);
                        img5.setVisibility(View.INVISIBLE);
                        img2.setImageResource(R.drawable.a_a0_click);
                        img3.setImageResource(R.drawable.b_a0_click);
                        img4.setImageResource(R.drawable.c_a0_click);
                        img_alpha.setVisibility(View.VISIBLE);
                        content="2ABC";
                        break;
                    case 2:
                        setVisible();
                        img1.setImageResource(R.drawable.a_3_click);
                        img5.setVisibility(View.INVISIBLE);
                        img2.setImageResource(R.drawable.d_a0_click);
                        img3.setImageResource(R.drawable.e_a0_click);
                        img4.setImageResource(R.drawable.f_a0_click);
                        content="3DEF";
                        break;
                    case 3:
                        setVisible();
                        img1.setImageResource(R.drawable.a_4_click);
                        img5.setVisibility(View.INVISIBLE);
                        img2.setImageResource(R.drawable.g_a0_click);
                        img3.setImageResource(R.drawable.h_a0_click);
                        img4.setImageResource(R.drawable.i_a0_click);
                        content="4GHI";
                        break;
                    case 4:
                        setVisible();
                        img1.setImageResource(R.drawable.a_5_click);
                        img5.setVisibility(View.INVISIBLE);
                        img2.setImageResource(R.drawable.j_a0_click);
                        img3.setImageResource(R.drawable.k_a0_click);
                        img4.setImageResource(R.drawable.l_a0_click);
                        content="5JKL";
                        break;
                    case 5:
                        setVisible();
                        img1.setImageResource(R.drawable.a_6_click);
                        img5.setVisibility(View.INVISIBLE);
                        img2.setImageResource(R.drawable.m_a0_click);
                        img3.setImageResource(R.drawable.n_a0_click);
                        img4.setImageResource(R.drawable.o_a0_click);
                        content="6MNO";
                        break;
                    case 6:
                        setVisible();
                        img1.setImageResource(R.drawable.a_7_click);
                        img2.setImageResource(R.drawable.p_a0_click);
                        img3.setImageResource(R.drawable.q_a0_click);
                        img4.setImageResource(R.drawable.r_a0_click);
                        img5.setImageResource(R.drawable.s_a0_click);
                        content="7PQRZ";
                        break;
                    case 7:
                        setVisible();
                        img1.setImageResource(R.drawable.a_8_click);
                        img2.setImageResource(R.drawable.t_a0_click);
                        img3.setImageResource(R.drawable.u_a0_click);
                        img4.setImageResource(R.drawable.v_a0_click);
                        img5.setVisibility(View.INVISIBLE);
                        content="8TUV";
                        break;
                    case 8:
                        setVisible();
                        img1.setImageResource(R.drawable.a_9_click);
                        img2.setImageResource(R.drawable.w_a0_click);
                        img3.setImageResource(R.drawable.x_a0_click);
                        img4.setImageResource(R.drawable.y_a0_click);
                        img5.setImageResource(R.drawable.z_a0_click);
                        content="9WXYZ";
                        break;
                    case 9:
                        if (isBig){
                            keyboardView.setKeyboard(zhu_small);
                        }else {
                            keyboardView.setKeyboard(zhu);
                        }
                        isBig=!isBig;
                        break;
                    case 10:
                        fuhaoIndex=1;
                        setVisible();
                        keyboardView.setKeyboard(fuhao);
                        img_alpha.setVisibility(View.GONE);
                        ll_over.setVisibility(View.GONE);
                        break;
                    case 12:
                        setVisible();
                        img1.setVisibility(View.INVISIBLE);
                        img5.setVisibility(View.INVISIBLE);
                        img2.setImageResource(R.drawable.a_0_click);
                        img3.setImageResource(R.drawable.a_1_click);
                        img4.setImageResource(R.drawable.kg_click);
                        content=" 01 ";
                        break;
                    case 13:
                        setVisible();
                        img1.setImageResource(R.drawable.a_2_click);
                        img5.setVisibility(View.INVISIBLE);
                        img2.setImageResource(R.drawable.a_a_click);
                        img3.setImageResource(R.drawable.b_a);
                        img4.setImageResource(R.drawable.c_a_click);
                        img_alpha.setVisibility(View.VISIBLE);
                        content="2abc";
                        break;
                    case 14:
                        setVisible();
                        img1.setImageResource(R.drawable.a_3_click);
                        img5.setVisibility(View.INVISIBLE);
                        img2.setImageResource(R.drawable.d_a_click);
                        img3.setImageResource(R.drawable.e_a_click);
                        img4.setImageResource(R.drawable.f_a_click);
                        content="3def";
                        break;
                    case 15:
                        setVisible();
                        img1.setImageResource(R.drawable.a_4_click);
                        img5.setVisibility(View.INVISIBLE);
                        img2.setImageResource(R.drawable.g_a_click);
                        img3.setImageResource(R.drawable.h_a_click);
                        img4.setImageResource(R.drawable.i_a_click);
                        content="4ghi";
                        break;
                    case 16:
                        setVisible();
                        img1.setImageResource(R.drawable.a_5_click);
                        img5.setVisibility(View.INVISIBLE);
                        img2.setImageResource(R.drawable.j_a_click);
                        img3.setImageResource(R.drawable.k_a_click);
                        img4.setImageResource(R.drawable.l_a_click);
                        content="5jkl";
                        break;
                    case 17:
                        setVisible();
                        img1.setImageResource(R.drawable.a_6_click);
                        img5.setVisibility(View.INVISIBLE);
                        img2.setImageResource(R.drawable.m_a_click);
                        img3.setImageResource(R.drawable.n_a_click);
                        img4.setImageResource(R.drawable.o_a_click);
                        content="6mno";
                        break;
                    case 18:
                        setVisible();
                        img1.setImageResource(R.drawable.a_7_click);
                        img2.setImageResource(R.drawable.p_a_click);
                        img3.setImageResource(R.drawable.q_a_click);
                        img4.setImageResource(R.drawable.r_a_click);
                        img5.setImageResource(R.drawable.s_a_click);
                        content="7pqrs";
                        break;
                    case 19:
                        setVisible();
                        img1.setImageResource(R.drawable.a_8_click);
                        img2.setImageResource(R.drawable.t_a_click);
                        img3.setImageResource(R.drawable.u_a_click);
                        img4.setImageResource(R.drawable.v_a_click);
                        img5.setVisibility(View.INVISIBLE);
                        content="8tuv";
                        break;
                    case 20:
                        setVisible();
                        img1.setImageResource(R.drawable.a_9_click);
                        img2.setImageResource(R.drawable.w_a_click);
                        img3.setImageResource(R.drawable.x_a_click);
                        img4.setImageResource(R.drawable.y_a_click);
                        img5.setImageResource(R.drawable.z_a_click);
                        content="9wxyz";
                        break;
                    case 21:
                        break;
                    case 22:
                        break;
                    case 30:
                        content="@";
                        appendContent(0);
                        break;
                    case 31:
                        content="-";
                        appendContent(0);
                        break;
                    case 32:
                        content="*";
                        appendContent(0);
                        break;
                    case 33:
                        content="\"";
                        appendContent(0);
                        break;
                    case 34:
                        content=";";
                        appendContent(0);
                        break;
                    case 35:
                        content="\\.";
                        appendContent(0);
                        break;
                    case 36:
                        content="%";
                        appendContent(0);
                        break;
                    case 37:
                        content="+";
                        appendContent(0);
                        break;
                    case 38:
                        content=":";
                        appendContent(0);
                        break;
                    case 39:
                        fuhaoIndex++;
                        if (fuhaoIndex>4){
                            fuhaoIndex=1;
                        }
                        if (fuhaoIndex==1){
                            keyboardView.setKeyboard(fuhao);
                        }else if (fuhaoIndex==2){
                            keyboardView.setKeyboard(fuhaoA);
                        }else if (fuhaoIndex==3){
                            keyboardView.setKeyboard(fuhaoB);
                        }else {
                            keyboardView.setKeyboard(fuhaoC);
                        }
                        break;
                    case 40:
                        fuhaoIndex++;
                        if (fuhaoIndex>4){
                            fuhaoIndex=1;
                        }
                        if (fuhaoIndex==1){
                            keyboardView.setKeyboard(fuhao);
                        }else if (fuhaoIndex==2){
                            keyboardView.setKeyboard(fuhaoA);
                        }else if (fuhaoIndex==3){
                            keyboardView.setKeyboard(fuhaoB);
                        }else {
                            keyboardView.setKeyboard(fuhaoC);
                        }
                        break;
                    case 41:
                        keyboardView.setKeyboard(zhu);
                        break;
                    case 50:
                        content="(";
                        appendContent(0);
                        break;
                    case 51:
                        content=")";
                        appendContent(0);
                        break;
                    case 52:
                        content="&";
                        appendContent(0);
                        break;
                    case 53:
                        content="<";
                        appendContent(0);
                        break;
                    case 54:
                        content=">";
                        appendContent(0);
                        break;
                    case 55:
                        content="\\";
                        appendContent(0);
                        break;
                    case 56:
                        content="[";
                        appendContent(0);
                        break;
                    case 57:
                        content="]";
                        appendContent(0);
                        break;
                    case 58:
                        content="'";
                        appendContent(0);
                        break;
                    case 60:
                        content=",";
                        appendContent(0);
                        break;
                    case 61:
                        content="?";
                        appendContent(0);
                        break;
                    case 62:
                        content="!";
                        appendContent(0);
                        break;
                    case 63:
                        content="/";
                        appendContent(0);
                        break;
                    case 64:
                        content="^";
                        appendContent(0);
                        break;
                    case 65:
                        content="’";
                        appendContent(0);
                        break;
                    case 66:
                        content="·";
                        appendContent(0);
                        break;
                    case 67:
                        content="#";
                        appendContent(0);
                        break;
                    case 68:
                        content="$";
                        appendContent(0);
                        break;
                    case 70:
                        content="...";
                        appendContent(0);
                        break;
                    case 71:
                        content="_";
                        appendContent(0);
                        break;
                    case 72:
                        content="=";
                        appendContent(0);
                        break;
                    case 73:
                        content="{";
                        appendContent(0);
                        break;
                    case 74:
                        content="}";
                        appendContent(0);
                        break;
                    case 75:
                        content="|";
                        appendContent(0);
                        break;
                    default:// 数值code
                        break;
                }

            }

            @Override
            public void onText(CharSequence text) {

            }

            @Override
            public void swipeLeft() {

            }

            @Override
            public void swipeRight() {

            }

            @Override
            public void swipeDown() {

            }

            @Override
            public void swipeUp() {

            }
        });


    }

    IWifiApListener listener=new IWifiApListener() {
        @Override
        public void onScanWifiApClients(List<WifiApClient> wifiApClients) {

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
        WifiController.instance().registerWifiApReceiver(this,listener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        WifiController.instance().unregisterWifiApReceiver(this,listener);
        unregisterReceiver(mReceiver);
    }

    private void appendContent(int index){
        Editable editable = edtInput.getText();
        int start = edtInput.getSelectionStart();
        if (editable != null) {
            editable.insert(start,content.substring(index,index+1));
        }
    }

    private void setVisible(){
        img1.setVisibility(View.VISIBLE);
        img2.setVisibility(View.VISIBLE);
        img3.setVisibility(View.VISIBLE);
        img4.setVisibility(View.VISIBLE);
        img5.setVisibility(View.VISIBLE);
        img_alpha.setVisibility(View.VISIBLE);
        ll_over.setVisibility(View.VISIBLE);
    }

    private void  turnWifiHot(String name,String password){
        WifiAPUtil.getInstance(getApplicationContext()).turnOnWifiAp(name,password, WifiAPUtil.WifiSecurityType.WIFICIPHER_WPA);
    }

    @OnClick({
            R.id.btn_sure
    })
    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.btn_sure:
                Intent intent=new Intent();
                intent.putExtra("password",et_wifi_password.getText().toString());
                setResult(100,intent);
                finish();
                break;
        }
    }
}
