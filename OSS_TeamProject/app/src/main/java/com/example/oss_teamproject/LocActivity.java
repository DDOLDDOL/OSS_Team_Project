package com.example.oss_teamproject;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class LocActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    ListView loc_listview;
    Intent intent;
    String[] loc_array;
    String gu, dong;
    int clicked=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loc);

        loc_listview=findViewById(R.id.loc_listview);
        loc_listview.setOnItemClickListener(this);

        loc_array=getResources().getStringArray(R.array.gu_array);
        ArrayAdapter<String> adapter=new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, loc_array);
        loc_listview.setAdapter(adapter);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(clicked==0) {
            gu=loc_array[position];
            loc_array=getResources().getStringArray(R.array.dong_array);
            ArrayAdapter<String> adapter=new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, loc_array);
            loc_listview.setAdapter(adapter);

            clicked++;
        }
        else {
            dong=loc_array[position];
            clicked=0;

            intent = getIntent();
            intent.putExtra("gu", gu);
            intent.putExtra("dong", dong);
            setResult(RESULT_OK, intent);
            finish();
        }
    }
}
