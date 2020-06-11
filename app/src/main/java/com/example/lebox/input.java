package com.example.lebox;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

public class input extends Activity {
    private EditText input;
    private Button enterCorrect,ccancel;
    public String inputText,str_coorr;
    private  String correct ="\"TRUE\"";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);


        input = (EditText)findViewById(R.id.inputText);
        enterCorrect =(Button)findViewById(R.id.enter);
        ccancel = (Button)findViewById(R.id.cancel);

        enterCorrect.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputText=input.getText().toString();

                qr thread = new qr();
                thread.setPriority(Thread.MIN_PRIORITY);
                thread.start();
                setDiaolgString(inputText);

            }
        });
        ccancel.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });



    }

    class qr extends Thread {
        OkHttpClient client_qrcode = new OkHttpClient();
        Request request_qrcode= new Request.Builder().url(("http://120.101.8.52/aubox604/WebService1.asmx/ifopen?QRcode="+inputText+"&boxnumber=0407")).build();

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

    private void setDiaolgString(String scanResult) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);//設定對話框內容
        builder.setTitle("輸入結果");//對話框標題


        builder.setMessage("輸入結果:" + scanResult + ":正確");//對話框內容
        builder.setPositiveButton("確定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(correct.equals(str_coorr)){
                    lock thread1 = new lock();
                    thread1.start();
                    System.out.println("OPEN");

                }
                finish();


            }
        });//對話框按鈕

        AlertDialog dialog = builder.create();
        dialog.show();//顯示對話框


    }//傳送文字至對話框內容中


}
