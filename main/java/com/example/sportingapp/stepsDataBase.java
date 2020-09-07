package com.example.sportingapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class stepsDataBase extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "StepsCounter.db"; //数据库名称
    private static final int DATABASE__VERSION = 1;//数据库版本,大于0
    public static final String KEY_ID = "_id";//主键
    public static final String KEY_DATE="date";//日期
    public static final String KEY_STEP="totalSteps";//步数
    public static final String KEY_TIME="totalTime";//时长

    private Context context = null;




    public stepsDataBase(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE__VERSION);
        this.context=context;
    }

    private static final String CREATE_DATABASE = "create table  steps("
            + "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
            + "date TEXT, "
            + "totalSteps INTEGER,"
            + "totalTime INTEGER)";


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_DATABASE);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + "steps");
        onCreate(db);


    }
    public void insert(String date, int totalSteps, long totalTime){
        //判断传入参数是否为空或者没有内容
        if(date == null )return;
        if(date.equals("") )return;
        //先查询是否存在
        Cursor cursor = null;
        try{
            cursor = fetchdata(date);
        }catch(Exception e){
            cursor = null;
        }
        if(cursor!=null && cursor.getCount()>0){//若存在，则更新
            UpdateValue(cursor,date,totalSteps, totalTime);
            cursor.close();
        }else{//不存在，则直接插入数据库
            insertNewValue(date,totalSteps, totalTime);
        }
        showSuccesstoSave();

    }
    public Cursor fetchdata(String date){
        /**************查询并将获取的信息存入到游标内***************/
        Cursor mCursor = getReadableDatabase().query(true, "steps", new String[]{KEY_ID, KEY_DATE, KEY_STEP,KEY_TIME}, KEY_DATE+"='"+date+"'",//比较字符串，需要加''单引号
                null, null, null, null, null);//正则匹配搜索
        if(mCursor!=null && mCursor.getCount()>0) mCursor.moveToFirst();//有信息，指向第一条
        return mCursor;
    }

    //更新数据库内容函数
    public void UpdateValue(Cursor cursor,String date,int steps,long time){
        try{
            int titlestep=cursor.getColumnIndex(KEY_STEP);
            int titletime=cursor.getColumnIndex(KEY_TIME);
            int nowsteps=cursor.getInt(titlestep);
            long nowtime=cursor.getLong(titletime);
            ContentValues values = new ContentValues();
            values.put(KEY_DATE, date);
            values.put(KEY_STEP, steps+nowsteps);
            values.put(KEY_TIME, time+nowtime);
            getWritableDatabase().update("steps", values,
                    KEY_DATE +"='"+date+"'", null);//比较字符串，需要加''单引号
        }catch(Exception e){
        }
    }


    // 插入新的数据函数
    public void insertNewValue(String date,int steps,long time){
        ContentValues values= new ContentValues();
        values.put(KEY_DATE, date);
        values.put(KEY_STEP, steps);
        values.put(KEY_TIME, time);
        try{
            getWritableDatabase().insert("steps", KEY_ID, values);
        }catch(Exception e){
        }
    }

    public boolean SearchSport(String date){
        Cursor mCursor=null;
        try {
             mCursor= getReadableDatabase().query(true, "steps", new String[]{KEY_ID, KEY_DATE, KEY_STEP, KEY_TIME}, KEY_DATE + "='" + date + "'",//比较字符串，需要加''单引号
                    null, null, null, null, null);//正则匹配搜索
        }catch (Exception e){
            mCursor=null;
        }
        boolean havesport=true;
        if(mCursor.getCount() == 0) {
            havesport=false;
            //Toast.makeText(context,"没有找到数据",Toast.LENGTH_SHORT).show();
        }

        return havesport;
    };

    public int SerachforSteps(String date){
        Cursor mCursor = getReadableDatabase().query(true, "steps", new String[]{KEY_ID, KEY_DATE, KEY_STEP,KEY_TIME}, KEY_DATE+"='"+date+"'",//比较字符串，需要加''单引号
                null, null, null, null, null);//正则匹配搜索
        if(mCursor!=null && mCursor.getCount()>0) {
            mCursor.moveToFirst();//有信息，指向第一条
            int titlestep=mCursor.getColumnIndex(KEY_STEP);
            int steps=mCursor.getInt(titlestep);
            return steps;}
        else
            return 0;

    };

    public long SerachforTime(String date){
        Cursor mCursor = getReadableDatabase().query(true, "steps", new String[]{KEY_ID, KEY_DATE, KEY_STEP,KEY_TIME}, KEY_DATE+"='"+date+"'",//比较字符串，需要加''单引号
                null, null, null, null, null);//正则匹配搜索
        if(mCursor!=null && mCursor.getCount()>0) {
            mCursor.moveToFirst();//有信息，指向第一条

            int titletime=mCursor.getColumnIndex(KEY_TIME);
            long time=mCursor.getLong(titletime);
            return time;}
        else
            return 0;
    };


    // 删除数据表函数
    public void DelectAllData(){
        getWritableDatabase().delete("steps", null, null);
    }

    private void showSuccesstoSave(){
        Toast.makeText(context, "保存成功", Toast.LENGTH_SHORT).show();
    }





}
