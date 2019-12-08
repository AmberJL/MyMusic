package com.example.mymusic;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class HttpConnection {
    public static String getHttpRequest(String urlString){
        URL url;
        InputStream in = null;
        HttpURLConnection conn = null;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            url = new URL(urlString);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setDoInput(true);
            conn.connect();

            if(conn.getResponseCode() == HttpsURLConnection.HTTP_OK){
                in = conn.getInputStream();
                int len =0;
                byte[] buffer = new byte[1024];
                while((len= in.read(buffer))!=-1){
                    out.write(buffer, 0, len);
                }
                return out.toString();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            if(in!=null){
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if(conn!=null){
                conn.disconnect();
            }

            if(out!=null){
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        return null;
    }
    public String downloadMusic(String urlString){
        URL url;
        InputStream in = null;
        HttpURLConnection conn = null;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            url = new URL(urlString);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setDoInput(true);
            conn.connect();

            if(conn.getResponseCode() == HttpsURLConnection.HTTP_OK){
                in = conn.getInputStream();
                int len =0;
                byte[] buffer = new byte[1024];
                while((len= in.read(buffer))!=-1){
                    out.write(buffer, 0, len);
                }
                byte[] t = out.toByteArray();
                String contentType = conn.getContentType();
                if (contentType.contains("audio/")) {

//                    String format = getFormat(aue);
                    File file =  File.createTempFile("result",".ma4", Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)); // 打开mp3文件即可播放
                    // System.out.println( file.getAbsolutePath());
                    FileOutputStream os = new FileOutputStream(file);
                    os.write(t);
                    os.close();
                    System.out.println("audio file write to " + file.getAbsolutePath());
                    return  file.getAbsolutePath();
//                    MediaPlayer mediaPlayer = new MediaPlayer();
//                    mediaPlayer.setDataSource(file.getAbsolutePath());
//                    mediaPlayer.prepare();
//                    mediaPlayer.start();
                }

            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally{
            if(in!=null){
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if(conn!=null){
                conn.disconnect();
            }

            if(out!=null){
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

        return "";
    }




}