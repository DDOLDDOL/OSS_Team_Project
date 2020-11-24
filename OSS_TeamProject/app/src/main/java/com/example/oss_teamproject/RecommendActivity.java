package com.example.oss_teamproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

public class RecommendActivity extends AppCompatActivity {
    Button button1, button2, button3, button4;
    Intent intent;

    ArrayList<Pair<String, String>> recommend_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend);

        setReccomendList();
        initializeElement();
    }

    public void setReccomendList() {
        intent=getIntent();

        recommend_list.add(new Pair(intent.getStringExtra("place1"), intent.getStringExtra("url1")));
        recommend_list.add(new Pair(intent.getStringExtra("place2"), intent.getStringExtra("url2")));
        recommend_list.add(new Pair(intent.getStringExtra("place3"), intent.getStringExtra("url3")));
        recommend_list.add(new Pair(intent.getStringExtra("place4"), intent.getStringExtra("url4")));
    }

    private void initializeElement() {
        recommend_list=new ArrayList<>();

        button1=findViewById(R.id.button1);
        button2=findViewById(R.id.button2);
        button3=findViewById(R.id.button3);
        button4=findViewById(R.id.button4);

        button1.setText(recommend_list.get(0).first);
        button2.setText(recommend_list.get(1).first);
        button3.setText(recommend_list.get(2).first);
        button4.setText(recommend_list.get(3).first);
    }

    public void onClick(View view) {
        Intent new_intent;
        String place_url;

        if(view==button1) {
            place_url=recommend_list.get(0).second;

            new_intent=new Intent(Intent.ACTION_VIEW, Uri.parse(place_url));
            startActivity(new_intent);
        }
        else if(view==button2) {
            place_url=recommend_list.get(1).second;

            new_intent=new Intent(Intent.ACTION_VIEW, Uri.parse(place_url));
            startActivity(new_intent);
        }
        else if(view==button3) {
            place_url=recommend_list.get(2).second;

            new_intent=new Intent(Intent.ACTION_VIEW, Uri.parse(place_url));
            startActivity(new_intent);
        }
        else if(view==button4) {
            place_url=recommend_list.get(3).second;

            new_intent=new Intent(Intent.ACTION_VIEW, Uri.parse(place_url));
            startActivity(new_intent);
        }
    }
}