package com.example.oss_teamproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

// 명헌
public class WeatherActivity extends AppCompatActivity {
    String w_url, page_source;
    TextView txt_loc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        txt_loc=findViewById(R.id.txt_loc);
        Intent intent=getIntent();
        txt_loc.setText(intent.getStringExtra("gu")+" "+intent.getStringExtra("dong"));

        /*
        w_url="http://apis.data.go.kr/1360000/VilageFcstInfoService/getUltraSrtNcst?serviceKey=u8A%2B5H78lLJAQF4izW49VG32bMUGmjryumhVXumYzQrSKRHAaAraWH%2BiHa9TbwCgWZvq9zv%2FfqS2IoPAFQ57HQ%3D%3D&numOfRows=10&pageNo=1&base_date=20201006&base_time=0600&nx=55&ny=127";
        page_source=connectURL(w_url);
         */
    }

    public String connectURL(String targetURL) {
        String document=null;

        try {
            URL url = new URL(targetURL);
            HttpURLConnection httpConnect = (HttpURLConnection) url.openConnection();

            httpConnect.setRequestMethod("GET");
            httpConnect.setRequestProperty("Content-type", "application/xml");

            int responseCode = httpConnect.getResponseCode();
            BufferedReader buffRead;

            //응답 코드를 분석, 정상적인 연결인지 확인하고 정상 연결 여부에 따라 스트림 설정
            if (responseCode == 200) {
                buffRead = new BufferedReader(new InputStreamReader(httpConnect.getInputStream()));
                System.out.println("[!] 응답 코드 200. 정상적으로 연결되었습니다.");
            } else if (responseCode > 200 && responseCode < 400) {
                buffRead = new BufferedReader(new InputStreamReader(httpConnect.getErrorStream()));
                System.out.println("[!] 응답 코드 " + responseCode + ". 연결에는 성공했으나 정상 연결이 아닙니다.");
            } else {
                buffRead = new BufferedReader(new InputStreamReader(httpConnect.getErrorStream()));
                System.out.println("[!] 응답 코드 " + responseCode + ". 정상 연결에 성공하지 못했습니다.");
            }

            StringBuffer result = new StringBuffer();
            String line;

            while ((line = buffRead.readLine()) != null) {
                result.append(line);
            }

            //연결 종료 및 result 출력
            buffRead.close();
            httpConnect.disconnect();
            System.out.println(result);
        }
        catch (Exception e) {
            txt_loc.setText("error");
        }
        return "succeed";
    }
}