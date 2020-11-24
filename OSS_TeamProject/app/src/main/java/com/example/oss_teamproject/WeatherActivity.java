package com.example.oss_teamproject;

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
import android.util.Log;
import android.util.Pair;
import android.view.View;
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
    TextView temperature, rain_probability, rain_amount, wind, sky_status;
    ImageView status_weather;

    Handler handler=new Handler();
    Intent intent;

    ArrayList<Pair<String, Pair<String, String>>> place_list;
    final String key="u8A%2B5H78lLJAQF4izW49VG32bMUGmjryumhVXumYzQrSKRHAaAraWH%2BiHa9TbwCgWZvq9zv%2FfqS2IoPAFQ57HQ%3D%3D";
    String url="http://apis.data.go.kr/1360000/VilageFcstInfoService/getVilageFcst";
    String gu, dong;
    String target_date, target_time;

    String date_today;
    int page;
    int del;
    int nx;
    int ny;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        getInfoFromIntent();
        initializeElement();
        setDate();
        temperature.setText(dong);
        /*
        setPage("20201125", "2010");

        url+="?serviceKey="+key;
        url+="&numOfRows=82";
        url+="&pageNo="+page;
        url+="&base_date="+date_today;
        url+="&base_time=2030";
        url+="&nx="+nx;
        url+="&ny="+ny;

        new Thread(new MyThread()).start();

         */
    }

    public void getInfoFromIntent() {
        intent = getIntent();

        gu=intent.getStringExtra("gu");
        dong=intent.getStringExtra("dong");
        target_date=intent.getStringExtra("date");
        target_time=intent.getStringExtra("time");
        nx=intent.getIntExtra("nx", 55);
        ny=intent.getIntExtra("ny", 127);
        place_list=(ArrayList<Pair<String, Pair<String, String>>>)intent.getSerializableExtra("place_list");
    }


    public void initializeElement() {
        temperature=findViewById(R.id.temperature);
        rain_probability=findViewById(R.id.rain_probability);
        rain_amount=findViewById(R.id.rain_amount);
        wind=findViewById(R.id.wind);
        status_weather=findViewById(R.id.status_weather);
    }

    public void setDate() {
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdfNow = new SimpleDateFormat("yyyyMMdd");
        date_today = String.valueOf(Integer.parseInt(sdfNow.format(date))-1);
    }

    public void setPage(String d, String t) {
        page=Integer.parseInt(d)-Integer.parseInt(date_today);
        del=Integer.parseInt(t)/300;
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

                Log.e("doc", document);
            }
            else
                Log.e("Fatal", "Wrong HTTP Connection");
        }
        catch (Exception e) {
            return "error1";
        }

        return document;
    }

    class UIUpdate implements Runnable {
        String tem, r_pro, r_amo, win, sky;

        public UIUpdate(String result) {
            try {
                String res="";
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                factory.setIgnoringElementContentWhitespace(true);
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document doc = builder.parse(new InputSource(new StringReader(result)));

                NodeList nodeList=doc.getElementsByTagName("item");
                for(int idx=0; idx<nodeList.getLength(); idx++) {
                    String str=nodeList.item(idx).getChildNodes().item(2).getTextContent();
                    String time_part=nodeList.item(idx).getChildNodes().item(4).getTextContent();

                    if(Integer.parseInt(time_part) == del * 300) {
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
            }
            catch (Exception e){
                Log.e("parseError", e.getMessage());
            }
        }

        @Override
        public void run() {
            temperature.setText(this.tem);
            rain_amount.setText(this.r_amo);
            rain_probability.setText(this.r_pro);
            wind.setText(this.win);

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
    }

    class MyThread implements Runnable {
        @Override
        public void run() {
            handler.post(new UIUpdate(getDataFromHTTP(url)));
        }
    }
}