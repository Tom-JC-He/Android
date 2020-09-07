package com.example.sportingapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.nio.DoubleBuffer;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    Chronometer chronometer;
    Button button_start;
    Button button_end;
    boolean isstart=true;
    boolean isstop=true;
    long sporttime;

    MediaPlayer mediaPlayer;
    Button music_play;
    Button music_stop;
    boolean isplaying=false;
    boolean issinging=false;

    SensorManager sensorManager;
    Sensor sensor;
    boolean issport=false;
    TextView tv_sportstep;
    TextView tv_sportdata;
    int step=0;
    Button button_save;
    Button button_show;
    Calendar calendar = Calendar.getInstance();
    String date;
    ImageButton imageButton;
    ImageButton imageButton_music;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        chronometer=(Chronometer)findViewById(R.id.chronometer1);

        button_start=(Button)findViewById(R.id.button1);
        button_end=(Button)findViewById(R.id.button2);

        imageButton=(ImageButton)findViewById(R.id.imageButton1);
        imageButton_music=(ImageButton)findViewById(R.id.imageButton2);



        button_start.setOnClickListener(button_click);
        button_end.setOnClickListener(button_click);

        imageButton.setOnClickListener(button_click);
        imageButton_music.setOnClickListener(button_click);

        tv_sportstep=(TextView)findViewById(R.id.testView1);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            Toast.makeText(MainActivity.this, "[权限]" + "ACTIVITY_RECOGNITION 未获得", Toast.LENGTH_SHORT).show();
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED) {
                // 检查权限状态
                if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.ACTIVITY_RECOGNITION)) {
                    //  用户彻底拒绝授予权限，一般会提示用户进入设置权限界面
                    Toast.makeText(MainActivity.this, "[权限]" + "ACTIVITY_RECOGNITION 以拒绝，需要进入设置权限界面打开", Toast.LENGTH_SHORT).show();
                } else {
                    //  用户未彻底拒绝授予权限
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, 1);
                    Toast.makeText(MainActivity.this, "[权限]" + "ACTIVITY_RECOGNITION 未彻底拒绝拒绝，请求用户同意", Toast.LENGTH_SHORT).show();
                }
//                return;
            } else {

                Toast.makeText(MainActivity.this, "[权限]" + "ACTIVITY_RECOGNITION ready", Toast.LENGTH_SHORT).show();
                sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
                sensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
                sensorManager.registerListener(sensorEventListener1, sensor, SensorManager.SENSOR_DELAY_FASTEST);
            }
        } else {
            sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
            sensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
            sensorManager.registerListener(sensorEventListener1, sensor, SensorManager.SENSOR_DELAY_FASTEST);

        }
        //获取系统的日期
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH)+1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        date= year+"-"+month+"-"+day;
        //Toast.makeText(this,date,Toast.LENGTH_SHORT).show();


    }
    private SensorEventListener sensorEventListener1=new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            if (issport) {
                if(event.values[0]==1.0f) {
                    step++;
                }


            }

            tv_sportstep.setText("本次运动步数为" + step+"步");


        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    private View.OnClickListener button_click = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.button1:

                    if (isstart) {
                        //设置开始计时时间
                        chronometer.setBase(SystemClock.elapsedRealtime());
                        isstart = false;
                        chronometer.start();
                        button_end.setEnabled(false);
                        issport=true;
                        isstop=true;
                        button_start.setText("暂停");



                    } else if (isstop) {
                        chronometer.stop();
                        isstop = false;
                        button_end.setEnabled(true);
                        sporttime = SystemClock.elapsedRealtime() - chronometer.getBase();
                        issport = false;
                        button_start.setText("继续运动");

                    } else {
                        chronometer.setBase(-sporttime + SystemClock.elapsedRealtime());
                        chronometer.start();
                        button_end.setEnabled(false);
                        isstop = true;
                        issport = true;
                        button_start.setText("暂停");

                    }
                    break;
                case R.id.button2:

                    chronometer.setBase(SystemClock.elapsedRealtime());
                    isstart = true;
                    issport = false;
                    isstop = false;

                    button_start.setText("开始运动");
                    AlertDialog.Builder dialog =new AlertDialog.Builder(MainActivity.this);
                    dialog.setTitle("是否保存本次运动记录");
                    dialog.setMessage("点击保存将保存本次运动记录，点击取消将取消保存");
                    dialog.setPositiveButton("保存", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            stepsDataBase stepsDB=new stepsDataBase(getApplicationContext());
                            stepsDB.insert(date,step,sporttime);
                            stepsDB.close();
                            step=0;
                            tv_sportstep.setText("");
                            dialog.dismiss();
                        }
                    });
                    dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            step=0;
                            tv_sportstep.setText("");
                            dialog.cancel();
                        }
                    });
                    dialog.show();



                    break;



                case R.id.imageButton1:
                    //String date2= Calendar.YEAR+"-"+(Calendar.MONTH+1)+"-"+Calendar.DAY_OF_MONTH;
                    Intent intent=new Intent(MainActivity.this,DataShowActivity.class);
                    startActivity(intent);
                    break;
                case R.id.imageButton2:
                    if (isplaying == false) {
                        mediaPlayer = MediaPlayer.create(MainActivity.this, R.raw.victory);
                        mediaPlayer.setLooping(true);
                        isplaying = true;

                    }
                    if(issinging==false) {

                        mediaPlayer.start();
                        imageButton_music.setImageResource(R.drawable.stop);
                        issinging=true;
                    }
                    else{
                        mediaPlayer.pause();
                        imageButton_music.setImageResource(R.drawable.play);
                        issinging=false;
                    }


            }
        }
    };




        @Override
        public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            if (requestCode == 1) {
                for (int i = 0; i < permissions.length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                                // 申请成功
                       Toast.makeText(MainActivity.this, "[权限]" + "ACTIVITY_RECOGNITION 申请成功",Toast.LENGTH_SHORT).show();
                        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
                        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
                        sensorManager.registerListener(sensorEventListener1, sensor, SensorManager.SENSOR_DELAY_FASTEST);

                    } else {
                                // 申请失败
                        Toast.makeText(MainActivity.this, "[权限]" + "ACTIVITY_RECOGNITION 申请失败",Toast.LENGTH_SHORT).show();
                    }
                }
            }

        }



            ;

}



