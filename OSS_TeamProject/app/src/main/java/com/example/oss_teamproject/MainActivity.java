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
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    Button btn_set_time, btn_set_place, btn_ok;

    public static SQLiteDatabase db;
    Intent intent;

    String gu="null", dong="null";
    String date="20201127", time="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeElement();
    }

    public void initializeElement() {
        DBHelper helper=new DBHelper(this);
        db=helper.getReadableDatabase();

        btn_set_time=findViewById(R.id.btn_set_time);
        btn_set_place=findViewById(R.id.btn_set_place);
        btn_ok=findViewById(R.id.btn_ok);
    }

    public void onClick(View view) {
        if(view==btn_set_time) {
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
                        time+="0";
                    time+=String.valueOf(minute);

                    btn_set_time.setTextSize(40);

                    if(minute<10)
                        btn_set_time.setText(hourOfDay+":0"+minute);
                    else
                        btn_set_time.setText(hourOfDay+":"+minute);
                }
            }, 12, 00, true);
            t_dialog.show();

            DatePickerDialog d_dialog = new DatePickerDialog(this,  new DatePickerDialog.OnDateSetListener()
            {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    date=String.valueOf(year);

                    if(monthOfYear<10)
                        date+="0";
                    date+=String.valueOf(monthOfYear+1);

                    if(dayOfMonth<10)
                        date+="0";
                    date+=String.valueOf(dayOfMonth);
                }
            }, 2020, 10, 26);

            d_dialog.show();
        }
        else if(view==btn_set_place) {
            intent=new Intent(this, LocActivity.class);
            startActivityForResult(intent, 10);
        }
        else if(view==btn_ok) {
            if (time != "" && dong != "null") {
                intent = new Intent(this, WeatherActivity.class);

                intent.putExtra("gu", gu);
                intent.putExtra("dong", dong);
                intent.putExtra("date", date);
                intent.putExtra("time", time);

                Cursor n_cur=db.rawQuery(
                        "select distinct nx, ny from 동네csv_com where gu==\""+gu+"\" and dong==\""+dong+"\"", null);
                n_cur.moveToFirst();

                intent.putExtra("nx", n_cur.getString(n_cur.getColumnIndex("nx")));
                intent.putExtra("ny", n_cur.getString(n_cur.getColumnIndex("ny")));

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
