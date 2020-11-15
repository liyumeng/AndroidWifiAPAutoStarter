package com.liyumeng.yuml.wifi;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Method;

import static android.widget.Toast.LENGTH_LONG;

public class MainActivity extends AppCompatActivity {
    private WifiManager wifiManager = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestPermission();
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);


        final Button btnStart = (Button) this.findViewById(R.id.buttonStart);
        final TextView textView = (TextView) this.findViewById(R.id.textView);
        final Context currentContext = this;
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            textView.setText("等待开启wifi...");
            Method setWifiApEnabled = null;
            Method getWifiApConfiguration = null;
            Method isWifiApEnabled = null;
            try {
                isWifiApEnabled = wifiManager.getClass().getMethod("isWifiApEnabled");
                isWifiApEnabled.setAccessible(true);
                boolean isEnabled = (Boolean) isWifiApEnabled.invoke(wifiManager);
                getWifiApConfiguration = wifiManager.getClass().getMethod("getWifiApConfiguration");
                getWifiApConfiguration.setAccessible(true);
                WifiConfiguration netConfig = (WifiConfiguration) getWifiApConfiguration.invoke(wifiManager);

                if(!isEnabled) {
                    setWifiApEnabled = wifiManager.getClass().getMethod("setWifiApEnabled",
                            WifiConfiguration.class, boolean.class);
                    setWifiApEnabled.setAccessible(true);

                    isEnabled = (Boolean) setWifiApEnabled.invoke(wifiManager, netConfig, true);

                }

                if(isEnabled)
                {
                    textView.setText("已开启wifi名字："+netConfig.SSID);
                    btnStart.setText(netConfig.SSID+"已开启");
                    Toast.makeText(currentContext, netConfig.SSID+"网络已开启", LENGTH_LONG).show();
                    Intent home=new Intent(Intent.ACTION_MAIN);
                    home.addCategory(Intent.CATEGORY_HOME);
                    startActivity(home);
                }else {
                    textView.setText("开启失败");
                }

            } catch (Exception e) {
                e.printStackTrace();
                textView.setText("开启异常:"+e.getMessage()+":"+e.getCause().getMessage());
            }
            }
        });

        btnStart.callOnClick();
    }
    public int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 2;
    public void requestPermission() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_SETTINGS)
                != PackageManager.PERMISSION_GRANTED) {


            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_PHONE_STATE,
                            Manifest.permission.WRITE_SETTINGS},
                    MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);

            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
            // app-defined int constant. The callback method gets the
            // result of the request.

        }
    }
}
