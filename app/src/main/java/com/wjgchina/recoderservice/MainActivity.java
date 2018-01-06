package com.wjgchina.recoderservice;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        showAudio();
        showReadWrite();
        setContentView(R.layout.activity_main);
    }

    public void showAudio(){
        if(ActivityCompat.checkSelfPermission
                (this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED){
            requestAudioPermission();
        }
    }

    public void requestAudioPermission(){
        if(ActivityCompat.shouldShowRequestPermissionRationale
                (this,Manifest.permission.RECORD_AUDIO)){
            explainAudioDialog();
        }else{
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.RECORD_AUDIO}, 1);
        }
    }

    private void explainAudioDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("应用需要获取您的录音权限,是否授权？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //请求权限
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.RECORD_AUDIO}, 1);
                    }
                }).setNegativeButton("取消", null)
                .create().show();
    }


    public void showReadWrite(){
        if(ActivityCompat.checkSelfPermission
                (this, Manifest.permission.READ_EXTERNAL_STORAGE)
                !=PackageManager.PERMISSION_GRANTED||
                ActivityCompat.checkSelfPermission
                        (this,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED){
            requestReadWritePermission();
        }
    }

    public void requestReadWritePermission(){
        if(ActivityCompat.shouldShowRequestPermissionRationale
                (this,Manifest.permission.READ_EXTERNAL_STORAGE)||
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            explainReadWriteDialog();
        }else{
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
    }

    private void explainReadWriteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("应用需要获取您的读写存储器权限,是否授权？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //请求权限
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.RECORD_AUDIO}, 1);
                    }
                }).setNegativeButton("取消", null)
                .create().show();
    }


}
