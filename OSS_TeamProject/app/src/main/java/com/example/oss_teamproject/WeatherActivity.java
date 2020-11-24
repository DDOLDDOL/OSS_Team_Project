package com.example.oss_teamproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class WeatherActivity extends AppCompatActivity {
    final String url="http://apis.data.go.kr/1360000/VilageFcstInfoService/getVilageFcst";
    final String key="u8A%2B5H78lLJAQF4izW49VG32bMUGmjryumhVXumYzQrSKRHAaAraWH%2BiHa9TbwCgWZvq9zv%2FfqS2IoPAFQ57HQ%3D%3D";

    Handler handler=new Handler();
    String date_today;
    int page;
    int del;
    int nx;
    int ny;

    String w_url, page_source;
    TextView txt_loc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        Intent intent = getIntent();
        String str=intent.getStringExtra("gu") + " " + intent.getStringExtra("dong");

        //new Thread(new MyThread()).start();
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
    class UIUpdate implements Runnable {
        String result=url+"\n";

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
                                this.result += "강우 확률: " + nodeList.item(idx).getChildNodes().item(5).getTextContent() + "\n";
                                break;
                            case "R06":
                                this.result += "강수량: " + nodeList.item(idx).getChildNodes().item(5).getTextContent() + "\n";
                                break;
                            case "T3H":
                                this.result += "기온: " + nodeList.item(idx).getChildNodes().item(5).getTextContent() + "\n";
                                break;
                            case "SKY":
                                this.result += "하늘 상태: " + nodeList.item(idx).getChildNodes().item(5).getTextContent() + "\n";
                                break;
                            case "WSD":
                                this.result += "\n";
                                break;
                        }
                    }
                }

                this.result+="\n"+res;
            }
            catch (Exception e){
                this.result=e.getMessage();
                Log.e("parseError", e.getMessage());
            }
        }

        @Override
        public void run() {
            txt.setText(result);
        }
    }

    class MyThread implements Runnable {
        @Override
        public void run() {
            handler.post(new UIUpdate(getDataFromHTTP(url)));
        }
    }
}