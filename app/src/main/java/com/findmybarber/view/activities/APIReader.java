package com.findmybarber.view.activities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class APIReader {
    static String stream = null;

    public APIReader() {
    }
    public String getHTTPData(String urlString){
        try {
            URL url = new URL(urlString);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            if (httpURLConnection.getResponseCode() == 200) {
                BufferedReader r = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = r.readLine()) != null)
                    sb.append(line);
                stream = sb.toString();
                httpURLConnection.disconnect();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stream;
    }
}
