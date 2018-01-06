package com.wjgchina.recoderservice.service;

import android.Manifest;
import android.accessibilityservice.AccessibilityService;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RecorderService extends AccessibilityService {
    private static final String TAG="RecorderService";
    private static final String TAG1="手机通话状态";

    private MediaRecorder recorder;
    private SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private OutCallReceiver outCallReceiver;
    private IntentFilter intentFilter;



    private  String currentCallNum="";

    private int previousStats=0;

    private String currentFile="";

    private boolean isRecording=false;

    private String dirPath="";

    public RecorderService() {
    }

    @Override
    protected void onServiceConnected() {
        Log.i(TAG,"onServiceConneted");

        Toast.makeText(getApplicationContext(),"自动录音服务已启动",Toast.LENGTH_LONG).show();

        int checkRecoderPhonePermission = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO);
        if(checkRecoderPhonePermission != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions((Activity) getApplicationContext(),new String[]{Manifest.permission.RECORD_AUDIO},1);
            return;
        }
        int checkWritePhonePermission = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if(checkWritePhonePermission != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions((Activity) getApplicationContext(),new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
            return;
        }
        int checkReadPhonePermission = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
        if(checkReadPhonePermission != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions((Activity) getApplicationContext(),new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
            return;
        }
    }



    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        Log.i(TAG,"eventType"+ accessibilityEvent.getEventType());
    }

    @Override
    public void onInterrupt() {
        Log.i(TAG,"onInterrupt");
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        TelephonyManager tm= (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        tm.listen(new MyListener(), PhoneStateListener.LISTEN_CALL_STATE);
        outCallReceiver=new OutCallReceiver();
        intentFilter=new IntentFilter();
        intentFilter.addAction("android.intent.action.NEW_OUTGOING_CALL");
        registerReceiver(outCallReceiver,intentFilter);
        dirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/com.ct.phonerecorder/";

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(getApplicationContext(),"进程被关闭，无法继续录音，请打开录音服务！",Toast.LENGTH_LONG).show();
        if(outCallReceiver!=null){
            unregisterReceiver(outCallReceiver);
        }

    }

    class MyListener extends PhoneStateListener{
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            Log.d(TAG1,"空闲状态"+incomingNumber);
            switch(state){
                case TelephonyManager.CALL_STATE_IDLE:
                    Log.d(TAG1,"空闲");
                    if(recorder!=null&& isRecording){
                        recorder.stop();
                        recorder.release();
                        recorder=null;
                        Log.d("电话","通话结束，停止录音");

                    }
                    isRecording=false;
                    break;
                case TelephonyManager.CALL_STATE_RINGING:
                    Log.d(TAG1,"来电响铃"+incomingNumber);
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    Log.d(TAG1,"摘机"+(!incomingNumber.equals("")?incomingNumber:currentCallNum));
                    initRecord(!incomingNumber.equals("")?incomingNumber:currentCallNum);
                    if(recorder!=null){
                        recorder.start();
                        isRecording=true;
                    }
                default:
                    break;
            }
            super.onCallStateChanged(state,incomingNumber);
        }
    }


    private void initRecord(String incomingNumber){
        previousStats=TelephonyManager.CALL_STATE_RINGING;
        recorder=new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        File out=new File(dirPath);
        if(!out.exists()){
            out.mkdirs();
        }
        recorder.setOutputFile(dirPath+
                getFileName(previousStats==TelephonyManager.CALL_STATE_RINGING?incomingNumber:currentCallNum));
        try {
            recorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private String getFileName(String incomingNumber){
        Date date=new Date(System.currentTimeMillis());
        currentFile=incomingNumber+" "+dateFormat.format(date)+".mp3";
        return currentFile;
    }

    public class OutCallReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG1,"当前手机拨打了电话:"+currentCallNum);
            if(intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)){
                currentCallNum=intent.getStringExtra(intent.EXTRA_PHONE_NUMBER);
                Log.d(TAG1,"当前手机拨打了电话:"+currentCallNum);

            }else{
                Log.d(TAG1,"有电话，快接听电话");
            }
        }
    }


}
