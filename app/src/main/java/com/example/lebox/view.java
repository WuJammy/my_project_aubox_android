package com.example.lebox;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

import io.agora.rtc.Constants;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoCanvas;
import io.agora.rtc.video.VideoEncoderConfiguration;


public class view extends Activity {
    private static final String TAG = view.class.getSimpleName();

    private static final int PERMISSION_REQ_ID = 22;

       private static final String[] REQUESTED_PERMISSIONS = {
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
               };//申請權限，包含相機,聲音及內部空間的許可權

    //----------------------------------------宣告所有變數-----------------------------------------------//
    private RtcEngine mRtcEngine;
    private boolean mCallEnd = false;
    private FrameLayout mLocalContainer;
    private RelativeLayout mRemoteContainer,connect;
    private SurfaceView mLocalView;
    private SurfaceView mRemoteView;
    private ImageView mCallBtn;
    private ImageView mSwitchCameraBtn;

    private final IRtcEngineEventHandler mRtcEventHandler = new IRtcEngineEventHandler() {
        @Override
        public void onUserJoined(final int uid, int elapsed ) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setupRemoteVideo(uid);
                }
            });
        }
           @Override
        public void onUserOffline(final int uid, int reason) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    onRemoteUserLeft();
                }
            });
        }


    };
    //--------------------------------------------------------------------------------------------------//


    class vi extends Thread {
        OkHttpClient client_video = new OkHttpClient();
        Request request_video= new Request.Builder().url(("http://120.101.8.52/aubox604/WebService1.asmx/Updatevideostatus?videostatus=0&boxnumber=0407")).build();

        @Override
        public void run() {
            client_video.newCall( request_video).enqueue(new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {

                }

                @Override
                public void onResponse(Response response) throws IOException {


                }
            });

        }

    }

    //-------------------------------------------------------------------------------------------------//

    private void setupRemoteVideo(int uid) {
        mRemoteView = RtcEngine.CreateRendererView(getBaseContext());
        mRemoteContainer.addView(mRemoteView);
        mRtcEngine.setupRemoteVideo(new VideoCanvas(mRemoteView, VideoCanvas.RENDER_MODE_HIDDEN, uid));
        mRemoteView.setTag(uid);

    }//設定遠端影像

    private void onRemoteUserLeft() {
        removeRemoteVideo();
    }//遠端使用者離開

    private void removeRemoteVideo() {
        if (mRemoteView != null) {
            mRemoteContainer.removeView(mRemoteView);
        }
        mRemoteView = null;
    }//檢查遠端是否有影像傳送過來

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view2);
        initUI();
        if (checkSelfPermission(REQUESTED_PERMISSIONS[0], PERMISSION_REQ_ID) &&
                checkSelfPermission(REQUESTED_PERMISSIONS[1], PERMISSION_REQ_ID) &&
                checkSelfPermission(REQUESTED_PERMISSIONS[2], PERMISSION_REQ_ID)) {
            initEngineAndJoinChannel();

        }


    }

    private void initUI() {
        mLocalContainer = (FrameLayout) findViewById(R.id.local_video_view_container);
        mRemoteContainer = (RelativeLayout) findViewById(R.id.remote_video_view_container);
        mCallBtn = (ImageView)findViewById(R.id.btn_call);
        mSwitchCameraBtn = (ImageView)findViewById(R.id.btn_switch_camera);
       // connect = (RelativeLayout) findViewById(R.id.connectState);
    }//初始化ui介面

    private boolean checkSelfPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(this, permission) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, REQUESTED_PERMISSIONS, requestCode);
            return false;
        }

        return true;
    }//檢查權限是否已獲得

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == PERMISSION_REQ_ID) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED ||
                    grantResults[1] != PackageManager.PERMISSION_GRANTED ||
                    grantResults[2] != PackageManager.PERMISSION_GRANTED) {
                showLongToast("Need permissions " + Manifest.permission.RECORD_AUDIO +
                        "/" + Manifest.permission.CAMERA + "/" + Manifest.permission.WRITE_EXTERNAL_STORAGE);
                finish();
                return;
            }
            initEngineAndJoinChannel();
        }
    }

    private void showLongToast(final String msg) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void initEngineAndJoinChannel() {
        initializeEngine();
        setupVideoConfig();
        setupLocalVideo();
        joinChannel();

    }//初始化視訊引擎加入的頻道

    private void initializeEngine() {
        try {
            mRtcEngine = RtcEngine.create(getBaseContext(), getString(R.string.agora_app_id), mRtcEventHandler);
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
            throw new RuntimeException("NEED TO check rtc sdk init fatal error\n" + Log.getStackTraceString(e));
        }
    }//初始化視訊引擎

    private void setupVideoConfig() {

        mRtcEngine.enableVideo();
        mRtcEngine.setVideoEncoderConfiguration(new VideoEncoderConfiguration(
                VideoEncoderConfiguration.VD_640x360,
                VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_15,
                VideoEncoderConfiguration.STANDARD_BITRATE,
                VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_FIXED_PORTRAIT));
    }//視訊影像的基本設置

    private void setupLocalVideo() {
        mLocalView = RtcEngine.CreateRendererView(getBaseContext());
        mLocalView.setZOrderMediaOverlay(true);
       // mRtcEngine.muteLocalAudioStream(false);
        mLocalContainer.addView(mLocalView);
        mRtcEngine.setupLocalVideo(new VideoCanvas(mLocalView, VideoCanvas.RENDER_MODE_HIDDEN, 0));
    }//設定本地影像

    private void joinChannel() {
        mRtcEngine.joinChannel(null, "demo", "Extra Optional Data", 0);
    }//加入頻道

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!mCallEnd) {
            leaveChannel();
        }
        RtcEngine.destroy();
    }

    private void leaveChannel() {
        if(mRtcEngine!=null)
        mRtcEngine.leaveChannel();
    }//離開頻道


    @SuppressLint("WrongConstant")
    public void onCallClicked(View view) {
        if (mCallEnd) {
            startCall();
            mCallEnd = false;
            mCallBtn.setImageResource(R.drawable.btn_endcall);

        } else {
            endCall();
            mCallEnd = true;
           // mCallBtn.setImageResource(R.drawable.btn_startcall);
            vi thread_view = new vi();
            thread_view.start();
            finish();
        }
        showButtons(!mCallEnd);

    }//接聽及掛斷視訊


    public void onSwitchCameraClicked(View view) {
        mRtcEngine.switchCamera();
    }//改變視訊的前/後鏡頭

    private void startCall() {
        setupLocalVideo();
        joinChannel();
    }//開始視訊

    private void endCall() {
        removeLocalVideo();
        removeRemoteVideo();
        leaveChannel();
    }//結束視訊

    private void removeLocalVideo() {
        if (mLocalView != null) {
            mLocalContainer.removeView(mLocalView);
        }
        mLocalView = null;

    }//檢查本地端是否有影像傳送過來

    private void showButtons(boolean show) {
        int visibility = show ? View.VISIBLE : View.GONE;
        mSwitchCameraBtn.setVisibility(visibility);
    }//決定切換前後鏡頭的按鈕什麼時候顯現
}