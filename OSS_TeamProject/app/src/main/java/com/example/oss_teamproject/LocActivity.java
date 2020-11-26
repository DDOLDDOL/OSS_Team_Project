package com.example.oss_teamproject;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class LocActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    ListView loc_listview;

    Intent intent;

    String[] loc_array;
    String[] format={""};
    String gu, dong;
    int clicked = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loc);

        loc_listview = findViewById(R.id.loc_listview);
        loc_listview.setOnItemClickListener(this);

        loc_array = getResources().getStringArray(R.array.gu_array);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, loc_array);
        loc_listview.setAdapter(adapter);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (clicked == 0) {
            gu = loc_array[position];
            loc_array = getDongArray(gu).toArray(format);

            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, loc_array);
            loc_listview.setAdapter(adapter);

            clicked++;
        }
        else {
            dong = loc_array[position];
            clicked = 0;

            intent = getIntent();
            intent.putExtra("gu", gu);
            intent.putExtra("dong", dong);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    public ArrayList<String> getDongArray(String g) {
        ArrayList<String> dong_list = new ArrayList<>();

        Cursor cur = MainActivity.db.rawQuery("select distinct dong from 동네csv_com where gu==\"" + g + "\"", null);
        cur.moveToFirst();
        dong_list.add(cur.getString(cur.getColumnIndex("dong")));

        while (cur.moveToNext())
            dong_list.add(cur.getString(cur.getColumnIndex("dong")));

        return dong_list;
    }
}