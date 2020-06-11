package com.example.lebox;

import androidx.annotation.NonNull;
import androidx.camera.camera2.internal.PreviewConfigProvider;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.CameraX;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.core.impl.ImageAnalysisConfig;
import androidx.camera.core.impl.ImageCaptureConfig;
import androidx.camera.core.impl.ImageOutputConfig;
import androidx.camera.core.impl.PreviewConfig;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.media.ImageReader;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.AlteredCharSequence;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.content.Intent;
import android.widget.ImageView;
import android.widget.Toast;

import com.camerakit.CameraKitView;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

import static android.os.Environment.DIRECTORY_DCIM;


public class MainActivity extends Activity implements LifecycleOwner {
    private  String correct ="\"TRUE\"";
    private LifecycleRegistry lifecycleRegistry;
    private Button ViewPageButton, ScanPageButton,lock;
    private ImageCapture imageCapture;
    private  Executor cameraExecutor = Executors.newSingleThreadExecutor();
    public String str_ifView="some",str_qrCode,str_coorr="some",str;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lifecycleRegistry = new LifecycleRegistry(this);
        lifecycleRegistry.markState(Lifecycle.State.CREATED);


        ifview thread = new ifview();
        thread.start();


        intialUi();
        changePage();

      // startcamera();


    }

    @Override
    protected void onStart() {
        super.onStart();
        lifecycleRegistry.markState(Lifecycle.State.STARTED);
    }

    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        return lifecycleRegistry;
    }

    class ifview extends Thread {
        OkHttpClient client = new OkHttpClient();
        Request request_ifview = new Request.Builder().url("http://120.101.8.52/aubox604/WebService1.asmx/ifvideo?boxnumber=0407").build();
        @Override
        public void run() {
            client.newCall(request_ifview).enqueue(new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {

                }

                @Override
                public void onResponse(Response response) throws IOException {
                    str_ifView = response.body().string();

                }
            });

        }

    }
    class qr extends Thread {
        OkHttpClient client_qrcode = new OkHttpClient();
        Request request_qrcode= new Request.Builder().url(("http://120.101.8.52/aubox604/WebService1.asmx/ifopen?QRcode="+str_qrCode+"&boxnumber=0407")).build();

        @Override
        public void run() {
            client_qrcode.newCall(request_qrcode).enqueue(new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {

                }

                @Override
                public void onResponse(Response response) throws IOException {
                    str_coorr = response.body().string();

                }
            });

        }

    }
    class vi extends Thread {
        OkHttpClient client_video = new OkHttpClient();
        Request request_video= new Request.Builder().url(("http://120.101.8.52/aubox604/WebService1.asmx/Updatevideostatus?videostatus=1&boxnumber=0407")).build();

        @Override
        public void run() {
            client_video.newCall( request_video).enqueue(new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {

                }

                @Override
                public void onResponse(Response response) throws IOException {
                    str = response.body().toString();

                }
            });

        }

    }
    class lock extends Thread {
        OkHttpClient client_lock = new OkHttpClient();
        Request request_lock= new Request.Builder().url(("http://120.101.8.52/aubox604/WebService1.asmx/Updatearduinolock?arduinolock=1&boxnumber=0407")).build();

        @Override
        public void run() {
            client_lock.newCall( request_lock).enqueue(new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {

                }

                @Override
                public void onResponse(Response response) throws IOException {


                }
            });

        }

    }
    class ulock extends Thread {
        OkHttpClient client_lock = new OkHttpClient();
        Request request_lock= new Request.Builder().url(("http://120.101.8.52/aubox604/WebService1.asmx/Updatearduinolock?arduinolock=0&boxnumber=0407")).build();

        @Override
        public void run() {
            client_lock.newCall( request_lock).enqueue(new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {

                }

                @Override
                public void onResponse(Response response) throws IOException {


                }
            });

        }

    }
    private void intialUi() {
        ViewPageButton = findViewById(R.id.btn_view);
        ScanPageButton = findViewById(R.id.btn_scan);
        lock =(Button)findViewById(R.id.close);
    }//初始化ui

    private void scanFrameSet() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);//選擇掃描qrcode
        integrator.setCameraId(1);//相機前後鏡頭調整，1為前鏡頭，0為後鏡頭
        integrator.setCaptureActivity(Scan.class);//抓取自己定義的頁面
        integrator.setBeepEnabled(false);//掃描後有沒有提示音
        integrator.setBarcodeImageEnabled(false);
        integrator.setPrompt(" ");//最下方的提示字
        integrator.initiateScan();//初始化掃描設定
    } //掃描條碼之設定

    private void setDiaolgString(String scanResult) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);//設定對話框內容
        builder.setTitle("掃描結果");//對話框標題


        builder.setMessage("掃描結果:" + scanResult + ":正確");//對話框內容
        builder.setPositiveButton("確定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {//點擊確定後跳轉至視訊頁


                if( correct.equals(str_ifView) && correct.equals(str_coorr))
                {

                    vi thread2 = new vi();
                    thread2.start();
                    Intent intents=new Intent();
                    intents.setClass(MainActivity.this,view.class);
                    startActivity(intents);


                }
                else if(correct.equals(str_coorr))
                {
                    lock threadLock = new lock();
                    threadLock.setPriority(1);
                    threadLock.start();
                }
            }
        });//對話框按鈕

        AlertDialog dialog = builder.create();
        dialog.show();//顯示對話框


    }//傳送文字至對話框內容中

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "You cancelled the scanning", Toast.LENGTH_SHORT).show();
            } else {
                str_qrCode = result.getContents();
                qr thread1 = new qr();
                thread1.start();
                System.out.println( str_coorr);

                setDiaolgString(result.getContents());//顯示掃描結果於對話框

            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }//掃描後執行之動作

    private void changePage() {
        ViewPageButton.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentv = new Intent();
                intentv.setClass(MainActivity.this, input.class);
                startActivity(intentv);
            }
        });//換到視訊頁


        ScanPageButton.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intentv = new Intent();
                //intentv.setClass(MainActivity.this, camera.class);
                //startActivity(intentv);
                scanFrameSet();

            }
        });//換到掃描頁

        lock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ulock thread_lock = new ulock();
                thread_lock .start();
            }
        });

    }

    private void takepic(){
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");//抓取當下日期
        Date curDate = new Date(System.currentTimeMillis());//抓取當下時間
        String str = sDateFormat.format(curDate);//當下日期+時間的字串
        File savedPhoto = new File(Environment.getExternalStorageDirectory(),  str+".jpg");//儲存空間的路徑

        ImageCapture.OutputFileOptions outputFileOptions = new ImageCapture.OutputFileOptions.Builder(savedPhoto).build();
        imageCapture.takePicture(outputFileOptions, (Executor) cameraExecutor, new ImageCapture.OnImageSavedCallback() {
            @Override
            public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {

                    System.out.println(outputFileResults);

            }

            @Override
            public void onError(@NonNull ImageCaptureException exception) {
                System.out.println("error");
            }
        });

    }
    @SuppressLint("RestrictedApi")
    private void startcamera() {


        ListenableFuture cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {

            CameraSelector cameraSelector = new CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_FRONT).build();

            imageCapture = new ImageCapture.Builder().setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY).setCameraSelector(cameraSelector).build();

            ImageAnalysis imageAnalysis = new ImageAnalysis.Builder().setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST).build();

            CameraX.bindToLifecycle((LifecycleOwner) this, cameraSelector,imageAnalysis, imageCapture);


        }, ContextCompat.getMainExecutor(this));




    }
}









