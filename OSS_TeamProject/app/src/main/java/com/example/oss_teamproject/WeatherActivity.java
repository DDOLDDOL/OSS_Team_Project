package com.example.oss_teamproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class WeatherActivity extends AppCompatActivity {
    TextView temperature, rain_probability, rain_amount, wind, txt_weather_main;
    ImageView status_weather;
    Button recommend_place;

    Handler handler;
    Intent intent;

    final String key="u8A%2B5H78lLJAQF4izW49VG32bMUGmjryumhVXumYzQrSKRHAaAraWH%2BiHa9TbwCgWZvq9zv%2FfqS2IoPAFQ57HQ%3D%3D";
    String url="http://apis.data.go.kr/1360000/VilageFcstInfoService/getVilageFcst";
    String gu, dong;
    String target_date, target_time;

    String date_today;
    String nx, ny;
    String weather;
    int page;
    int del;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        getInfoFromIntent();
        initializeElement();

        url += "?serviceKey=" + key;
        url += "&numOfRows=82";
        url += "&pageNo=1";
        url += "&base_date=20201126";
        url += "&base_time=2030";
        url += "&nx=" + nx;
        url += "&ny=" + ny;
        
        try {
            Thread http_thread = new Thread(new MyThread());
            http_thread.start();
            http_thread.join();
        }
        catch(Exception e) {
            Log.e("thr_err", e.toString());
        }
    }

    public void getInfoFromIntent() {
        intent = getIntent();

        gu = intent.getStringExtra("gu");
        dong = intent.getStringExtra("dong");
        target_date = intent.getStringExtra("date");
        target_time = intent.getStringExtra("time");
        nx = intent.getStringExtra("nx");
        ny = intent.getStringExtra("ny");

        setDate();
        setPage(target_date, target_time);
    }

    public void initializeElement() {
        handler=new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 0)
                    weather = "s";
                else
                    weather = "r";
            }
        };

        temperature=findViewById(R.id.temperature);
        rain_probability=findViewById(R.id.rain_probability);
        rain_amount=findViewById(R.id.rain_amount);
        wind=findViewById(R.id.wind);
        txt_weather_main=findViewById(R.id.txt_weather_main);

        status_weather=findViewById(R.id.status_weather);
        recommend_place=findViewById(R.id.recommend_place);

        int hour=Integer.parseInt(target_time)/100;

        String minute="";
        if(Integer.parseInt(target_time)%100<10)
            minute+="0";
        minute+=Integer.parseInt(target_time)%100;

        txt_weather_main.setTextSize(30);
        txt_weather_main.setText(hour + ":" + minute + " " + gu + " " + dong + " 날씨");
    }

    public void setDate() {
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdfNow = new SimpleDateFormat("yyyyMMdd");
        date_today = String.valueOf(Integer.parseInt(sdfNow.format(date)) - 1);
    }

    public void setPage(String d, String t) {
        page = Integer.parseInt(d) - Integer.parseInt(date_today);
        del = Integer.parseInt(t) / 300;
    }

    public String getDataFromHTTP(String targetURL) {
        String document = "go";

        try {
            URL url_address = new URL(targetURL);
            HttpURLConnection httpConnect = (HttpURLConnection) url_address.openConnection();

            httpConnect.setRequestMethod("GET");
            httpConnect.setRequestProperty("Content-type", "application/xml");

            int responseCode = httpConnect.getResponseCode();

            if (responseCode == 200) {
                BufferedReader buffRead = new BufferedReader(new InputStreamReader(httpConnect.getInputStream()));

                //결과를 읽어 result에 저장
                StringBuffer result = new StringBuffer();
                String line;

                while ((line = buffRead.readLine()) != null) {
                    result.append(line);
                }

                buffRead.close();
                httpConnect.disconnect();
                document = result.toString();
            } else
                Log.e("Fatal", "Wrong HTTP Connection");
        } catch (Exception e) {
            Log.e("eeeee", e.toString());
        }

        return document;
    }

    public void recomClick(View view) {
        if(view.getId()==R.id.recommend_place) {
            intent=new Intent(this, RecommendActivity.class);
            intent.putExtra("gu", gu);
            intent.putExtra("dong", dong);
            intent.putExtra("weather", weather);

            startActivity(intent);
        }
    }

    class UIUpdate implements Runnable {
        String tem, r_pro, r_amo, win, sky;
        Message msg;

        public UIUpdate(String result) {
            try {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                factory.setIgnoringElementContentWhitespace(true);
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document doc = builder.parse(new InputSource(new StringReader(result)));

                NodeList nodeList = doc.getElementsByTagName("item");
                for (int idx = 0; idx < nodeList.getLength(); idx++) {
                    String str = nodeList.item(idx).getChildNodes().item(2).getTextContent();
                    String time_part = nodeList.item(idx).getChildNodes().item(4).getTextContent();

                    if (Integer.parseInt(time_part) == del * 300) {
                        switch (str) {
                            case "POP":
                                this.r_pro = nodeList.item(idx).getChildNodes().item(5).getTextContent();
                                break;
                            case "R06":
                                this.r_amo = nodeList.item(idx).getChildNodes().item(5).getTextContent();
                                break;
                            case "T3H":
                                this.tem = nodeList.item(idx).getChildNodes().item(5).getTextContent();
                                break;
                            case "SKY":
                                this.sky = nodeList.item(idx).getChildNodes().item(5).getTextContent();
                                break;
                            case "WSD":
                                this.win = nodeList.item(idx).getChildNodes().item(5).getTextContent();
                                break;
                        }
                    }
                }

                sendMsgToMainThread(this.r_pro, this.r_amo);
            }
            catch (Exception e) {
                Log.e("parseError", e.getMessage());
            }
        }

        @Override
        public void run() {
            temperature.append(this.tem+"℃");
            rain_amount.append(this.r_amo+"mm");
            rain_probability.append(this.r_pro+"%");
            wind.append(this.win+"m/s");

            switch (sky) {
                case "1":
                    // 맑음
                    status_weather.setImageResource(R.drawable.sunny);
                    break;
                case "3":
                    // 구름많음
                    status_weather.setImageResource(R.drawable.sun_cloud);
                    break;
                case "4":
                    //흐림
                    status_weather.setImageResource(R.drawable.cloudy);
                    break;
            }
        }

        private void sendMsgToMainThread(String rp, String ra) {
            msg=handler.obtainMessage();

            if (Integer.parseInt(rp) >= 60 && Double.parseDouble(ra) >= 0.5)
                msg.what=1;
            else
                msg.what=0;

            handler.sendMessage(msg);
        }
    }

    class MyThread implements Runnable {
        @Override
        public void run() {
            handler.post(new UIUpdate(getDataFromHTTP(url)));
        }
    }
}