package com.example.oss_teamproject;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

// 윤철
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    TextView timepicker, locpicker;
    Button btn_ok;

    Intent intent;
    String gu, dong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setAllView();
    }

    public void setAllView() {
        timepicker=findViewById(R.id.timepicker);
        locpicker=findViewById(R.id.locpicker);

        btn_ok=findViewById(R.id.btn_ok);
        btn_ok.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v==timepicker) {

        }
        else if(v==locpicker) {
            intent=new Intent(getApplicationContext(), LocActivity.class);
            startActivityForResult(intent, 10);
        }
        else if(v==btn_ok) {
            intent=new Intent(this, WeatherActivity.class);
            intent.putExtra("gu", gu);
            intent.putExtra("dong", dong);
            startActivity(intent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==10 && resultCode==RESULT_OK) {
            gu=data.getStringExtra("gu");
            dong=data.getStringExtra("dong");
            locpicker.setText(gu+" "+dong);
        }
    }
}
