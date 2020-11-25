package com.example.oss_teamproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class RecommendActivity extends AppCompatActivity {
    Button button1, button2, button3, button4;
    TextView textView;

    Intent intent;

    String gu, dong, weather;
    String[] place_url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend);

        getDateFromIntent();
        initializeElement();
        setButton();
    }

    public void getDateFromIntent() {
        intent=getIntent();

        gu=intent.getStringExtra("gu");
        dong=intent.getStringExtra("dong");
        weather=intent.getStringExtra("weather");
    }

    public void initializeElement() {
        place_url=new String[4];

        button1=findViewById(R.id.button1);
        button2=findViewById(R.id.button2);
        button3=findViewById(R.id.button3);
        button4=findViewById(R.id.button4);

        textView=findViewById(R.id.textView);
        textView.setTextSize(30);
        textView.setText(gu+" "+dong+" 추천 장소");
    }

    public void setButton() {
        Cursor r_cur = MainActivity.db.rawQuery(
                "select name, url from 동네csv_com where gu==\"" + gu + "\" and dong==\"" + dong + "\" and weather==\"" + weather + "\"",
                null);

        r_cur.moveToFirst();
        button1.setText(r_cur.getString(r_cur.getColumnIndex("name")));
        place_url[0] = r_cur.getString(r_cur.getColumnIndex("url"));

        r_cur.moveToNext();
        button2.setText(r_cur.getString(r_cur.getColumnIndex("name")));
        place_url[1] = r_cur.getString(r_cur.getColumnIndex("url"));

        r_cur.moveToNext();
        button3.setText(r_cur.getString(r_cur.getColumnIndex("name")));
        place_url[2] = r_cur.getString(r_cur.getColumnIndex("url"));

        r_cur.moveToNext();
        button4.setText(r_cur.getString(r_cur.getColumnIndex("name")));
        place_url[3] = r_cur.getString(r_cur.getColumnIndex("url"));
    }

    public void onClick(View view) {
        Intent new_intent;

        if(view==button1) {
            new_intent=new Intent(Intent.ACTION_VIEW, Uri.parse(place_url[0]));
            startActivity(new_intent);
        }
        else if(view==button2) {
            new_intent=new Intent(Intent.ACTION_VIEW, Uri.parse(place_url[1]));
            startActivity(new_intent);
        }
        else if(view==button3) {
            new_intent=new Intent(Intent.ACTION_VIEW, Uri.parse(place_url[2]));
            startActivity(new_intent);
        }
        else if(view==button4) {
            new_intent=new Intent(Intent.ACTION_VIEW, Uri.parse(place_url[3]));
            startActivity(new_intent);
        }
    }
}