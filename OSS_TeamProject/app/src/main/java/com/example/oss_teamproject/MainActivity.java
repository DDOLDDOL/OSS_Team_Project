package com.example.oss_teamproject;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    Button btn_set_time, btn_set_place, btn_ok;

    SQLiteDatabase db;
    Intent intent;

    ArrayList<Pair<String, Pair<String, String>>> place_list;
    String gu="null", dong="null";
    String date="", time="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeElement();
    }

    public void initializeElement() {
        DBHelper helper=new DBHelper(this);
        db=helper.getReadableDatabase();
        place_list=new ArrayList<>();

        btn_set_time=findViewById(R.id.btn_set_time);
        btn_set_place=findViewById(R.id.btn_set_place);
        btn_ok=findViewById(R.id.btn_ok);
    }

    public void onClick(View view) {
        if(view==btn_set_time) {
            DatePickerDialog d_dialog = new DatePickerDialog(this,  new DatePickerDialog.OnDateSetListener()
            {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    date=String.valueOf(year);
                    if(monthOfYear<10)
                        date+="0";
                    date+=String.valueOf(monthOfYear);
                    if(dayOfMonth<10)
                        date+="0";
                    date+=String.valueOf(dayOfMonth);
                }
            }, 2020, 1, 1);

            d_dialog.show();

            TimePickerDialog t_dialog = new TimePickerDialog(
                    this, android.R.style.Theme_Holo_Light_Dialog,  new TimePickerDialog.OnTimeSetListener()
            {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute)
                {
                    if(hourOfDay<10)
                        time="0";
                    time+=String.valueOf(hourOfDay);
                    if(minute<10)
                        time="0";
                    time+=String.valueOf(minute);

                    btn_set_time.setTextSize(40);
                    btn_set_time.setText(hourOfDay+":"+minute);
                }
            }, 8, 10, true);

            t_dialog.show();
            btn_set_time.setText(time);
        }
        else if(view==btn_set_place) {
            intent=new Intent(this, LocActivity.class);
            startActivityForResult(intent, 10);
        }
        else if(view==btn_ok) {
            String weahter, name, url;
            if(time!="" && dong!="null") {
                try {
                    Cursor name_cur = db.rawQuery("select name from \"동네csv_com\" where gu==\""+gu+"\" and dong==\""+dong+"\"", null);
                    name_cur.moveToFirst();
                    Cursor weather_cur = db.rawQuery("select weather from \"동네csv_com\" where gu==\""+gu+"\" and dong==\""+dong+"\"", null);
                    weather_cur.moveToFirst();
                    Cursor url_cur = db.rawQuery("select url from \"동네csv_com\" where gu==\""+gu+"\" and dong==\""+dong+"\"", null);
                    url_cur.moveToFirst();

                    while (weather_cur.moveToNext() && name_cur.moveToNext() && url_cur.moveToNext()) {
                        weahter = weather_cur.getString(weather_cur.getColumnIndex("name"));
                        name = name_cur.getString(name_cur.getColumnIndex("name"));
                        url = url_cur.getString(url_cur.getColumnIndex("name"));

                        place_list.add(new Pair(weahter, new Pair(name, url)));
                    }
                }
                catch(Exception e) { Log.e("dbError", e.getMessage()); }

                intent = new Intent(this, WeatherActivity.class);
                intent.putExtra("gu", gu);
                intent.putExtra("dong", dong);
                intent.putExtra("date", date);
                intent.putExtra("time", time);
                intent.putExtra("place_list", place_list);
                startActivity(intent);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==10 && resultCode==RESULT_OK) {
            gu=data.getStringExtra("gu");
            dong=data.getStringExtra("dong");

            btn_set_place.setTextSize(40);
            btn_set_place.setText(dong);
        }
    }

    class DBHelper extends SQLiteOpenHelper {
        private String DB_PATH = "";
        private final String DB_NAME = "place_db.db";

        public DBHelper(@Nullable Context context) {
            super(context, "place_db.db", null, 1);

            DB_PATH += "/data/data/" + context.getPackageName() + "/databases/";
            this.setDB(context);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        }

        public void setDB(Context ctx) {
            File folder = new File(DB_PATH);
            if (!folder.exists())
                folder.mkdirs();

            AssetManager assetManager = ctx.getResources().getAssets();
            File outfile = new File(DB_PATH+DB_NAME);

            InputStream is = null;
            FileOutputStream fo = null;
            long filesize = 0;

            try {
                is = assetManager.open("place_db.db", AssetManager.ACCESS_BUFFER);
                filesize = is.available();

                if (outfile.length() <= 0) {
                    byte[] tempdata = new byte[(int) filesize];
                    is.read(tempdata);
                    is.close();

                    outfile.createNewFile();
                    fo = new FileOutputStream(outfile);
                    fo.write(tempdata);
                    fo.close();
                }
            }
            catch (IOException e) { }
        }
    }
}
