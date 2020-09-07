package com.example.sportingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

public class DataShowActivity extends AppCompatActivity {
    CalendarView calendarView1;
    TextView textView1;
    boolean ifsport;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_show);
        textView1=(TextView)findViewById(R.id.textView1);
        calendarView1=(CalendarView)findViewById(R.id.calendarView1);
        calendarView1.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                String date=year+"-"+(month+1)+'-'+dayOfMonth;
                stepsDataBase stepsDB2=new stepsDataBase(getApplicationContext());
                ifsport=stepsDB2.SearchSport(date);
                //Toast.makeText(DataShowActivity.this,date,Toast.LENGTH_SHORT).show();
                if(ifsport) {
                    int steps=stepsDB2.SerachforSteps(date);
                    long time=stepsDB2.SerachforTime(date);
                    int hours=(int)time/(1000*3600);
                    int minutes=0;
                    if(hours==0) {
                        minutes = (int)time/(1000*60);
                    }
                    else {

                        minutes = (int) (time % (1000 * 3600)) / (1000 * 60);
                    }
                    int seconds=0;
                    if(minutes==0){
                        seconds=(int)(time % (1000 * 3600))/1000;
                    }
                    else {
                        seconds = (int) ((time % (1000 * 3600)) % (1000 * 60)) / 1000;
                    }
                    int distance=(int) (0.6*steps);
                    textView1.setText(date + "共运动" + steps + "步\n" + hours+"小时"+minutes+"分钟"+seconds+"秒\n"+"共约运动"+distance+"米");
                }
                else
                    textView1.setText("暂无运动数据");
                stepsDB2.close();
            }

            });


    }
}
