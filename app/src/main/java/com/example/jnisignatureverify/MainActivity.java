package com.example.jnisignatureverify;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.security.MessageDigest;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    protected TextView appSignaturesTv;
    protected TextView jniSignaturesTv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_main);

        initView();

        appSignaturesTv.setText(getSha1Value(MainActivity.this));
        jniSignaturesTv.setText(getSignaturesSha1(MainActivity.this));
    }
    private void initView() {
        appSignaturesTv = (TextView) findViewById(R.id.app_signatures_tv);
        jniSignaturesTv = (TextView) findViewById(R.id.jni_signatures_tv);
        boolean result = checkSha1(MainActivity.this);
        if(result){
            new AlertDialog.Builder(this)
                    .setTitle ("警告！！")
                    .setMessage ("检测到你正在使用非正版软件，请下载正版软件")
                    .setNegativeButton ("关闭", new DialogInterface.OnClickListener () {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    }).show ();
        }
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String getSignaturesSha1(Context context);
    public native boolean checkSha1(Context context);
    public native String getToken(Context context,String userId);

//获取apk当前的签名
    public String getSha1Value(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), PackageManager.GET_SIGNATURES);
            byte[] cert = info.signatures[0].toByteArray();
            MessageDigest md = MessageDigest.getInstance("SHA1");
            byte[] publicKey = md.digest(cert);
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < publicKey.length; i++) {
                String appendString = Integer.toHexString(0xFF & publicKey[i])
                        .toUpperCase(Locale.US);
                if (appendString.length() == 1)
                    hexString.append("0");
                hexString.append(appendString);
            }
            String result = hexString.toString();
            return result.substring(0, result.length());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
