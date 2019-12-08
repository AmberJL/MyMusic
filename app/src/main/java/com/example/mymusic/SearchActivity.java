package com.example.mymusic;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {
    ArrayList<MusicInfo> List;
    ListView listView;

    SQLiteDatabase mdatabase;
    MediaPlayer mediaPlayer;
    Button play;
    boolean op = false;
    TextView nowTime;
    TextView maxTime;
    TextView songName;
    SeekBar seekBar;
    SeekBarThread r;
    TextView songSinger;
    boolean like = false;
    MusicInfo musicInfo;
    Button add;
    String NowPath = "";
    private Handler handlerr = new Handler() {


        @Override
        public void handleMessage(final Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    setAda();
                    break;
            }
        }
    };
    private Handler handler = new Handler() {


        @Override
        public void handleMessage(final Message msg) {
            super.handleMessage(msg);
            seekBar.setProgress(msg.what);
            int min = msg.what/60/1000;
            int sec = (msg.what-(60*min*1000))/1000;
            String time;
            if(sec<10){
                time = min+":0"+sec;
            }else{
                time = min+":"+sec;
            }

            nowTime.setText(time);
        }
    };
    public void setAda(){
        listAdapter adapter = new listAdapter(this,R.layout.word_item,List);
        listView.setAdapter(adapter);
    }
    class SeekBarThread implements Runnable {

        @Override
        public void run() {
            while (mediaPlayer != null) {
                // 将SeekBar位置设置到当前播放位置
                if(mediaPlayer!=null&&!op) {
                    try{
                        if (mediaPlayer.isPlaying()) {
                            handler.sendEmptyMessage(mediaPlayer.getCurrentPosition());
                            try {
                                // 每100毫秒更新一次位置
                                Thread.sleep(80);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (Exception e){
                        e.printStackTrace();

                    }

                }


            }

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        add = findViewById(R.id.addMusic);
        songSinger = findViewById(R.id.SongSinger);
        mdatabase = new SQLiteDbHelper(this).getWritableDatabase();
        seekBar = findViewById(R.id.listen_progress);
        play = findViewById(R.id.play);
        songName = findViewById(R.id.SongName);
        nowTime = findViewById(R.id.nowTime);
        maxTime = findViewById(R.id.maxTime);
        listView = findViewById(R.id.SearchList);
        final EditText input = findViewById(R.id.input);
        Button search = findViewById(R.id.search);
        Button back = findViewById(R.id.back);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer!=null){
                    if(!like){
                        like = true;
                        Toast.makeText(SearchActivity.this,"收藏成功",Toast.LENGTH_SHORT).show();
                        ContentValues contentValues = new ContentValues();
                        contentValues.put("SongName",musicInfo.Songname);
                        contentValues.put("path",NowPath);
                        contentValues.put("singer",musicInfo.singer);
                        mdatabase.insert("music",null,contentValues);
                        add.setBackground(getResources().getDrawable(R.drawable.like));

                    }else{
                        //取消喜欢
                        like = false;
                        Toast.makeText(SearchActivity.this,"取消收藏",Toast.LENGTH_SHORT).show();
                        mdatabase.delete("music","path = '"+NowPath+"'",null);
                        add.setBackground(getResources().getDrawable(R.drawable.nolike));
                    }

                }

            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchActivity.this.finish();
            }
        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                op = true;

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if(mediaPlayer!=null){
                    mediaPlayer.seekTo(seekBar.getProgress());
                    int min = seekBar.getProgress()/60/1000;
                    int sec = (seekBar.getProgress()-(min*60*1000))/1000;
                    String time = min+":"+sec;
                    nowTime.setText(time);
                }
                op = false;
            }
        });
        listView.setBackgroundColor(Color.WHITE);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                System.out.println("click_start"+listView.getFirstVisiblePosition());
                System.out.println("click_end"+listView.getLastVisiblePosition());
                for(int i=0;i<listView.getLastVisiblePosition()-listView.getFirstVisiblePosition();i++)
                {
                    View v = listView.getChildAt(i);
                    ImageView image = v.findViewById(R.id.isPlaying);
                    image.setBackgroundColor(Color.WHITE);
                }

                ImageView isPlaying = view.findViewById(R.id.isPlaying);
                isPlaying.setBackground(getResources().getDrawable(R.drawable.playing));
                musicInfo = List.get(position);
                final MusicInfo music = List.get(position);
                like = false;
                songSinger.setText(musicInfo.singer);
                add.setBackground(getResources().getDrawable(R.drawable.nolike));
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        NowPath = getPlayMusicPath(music.Songmid);
                        if(!NowPath.equals("")){
                            if(mediaPlayer!=null)
                            {
                                mediaPlayer.stop();
                                mediaPlayer.release();
                            }
                            mediaPlayer = new MediaPlayer();
                            try {
                                mediaPlayer.setDataSource(NowPath);
                                mediaPlayer.prepare();
                                mediaPlayer.start();

                                //设置进度条大小为当前音频最长毫秒数
                                seekBar.setMax(mediaPlayer.getDuration());
                                int min = mediaPlayer.getDuration()/1000/60;
                                int sec = (mediaPlayer.getDuration()-(min*60*1000))/1000;
                                String time;
                                if(sec<10){
                                    time = min+":0"+sec;
                                }else{
                                    time = min+":"+sec;
                                }
                                maxTime.setText(time);
                                songName.setText(music.Songname);
                                play.setBackground(getResources().getDrawable(R.drawable.stop));
                                r = new SeekBarThread();
                                r.run();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }
                    }
                }).start();

            }
        });
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean operate = false;
                if(mediaPlayer!=null){

                    if(mediaPlayer.isPlaying()){
                        play.setBackground(getResources().getDrawable(R.drawable.play));
                        mediaPlayer.pause();
                        System.out.println("你点了暂停");
                        operate = true;
                    }
                }
                if (mediaPlayer!=null&&!operate){

                    if(!mediaPlayer.isPlaying()){
                        play.setBackground(getResources().getDrawable(R.drawable.stop));
                        mediaPlayer.start();
                        System.out.println("你点了开始");
                    }
                }
            }
        });
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String res = input.getText().toString();
                if(!res.equals("")){
                    new Thread(new Runnable() {
                        @Override
                        public void run() {


                            List =  SearchMusic(res);
                            for( int i=0;i<List.size();i++)
                            {
                                System.out.println("name:"+List.get(i).Songname+"mid:"+List.get(i).Songmid);
                            }
                            Message ms = new Message();
                            ms.what = 1;
                            handlerr.sendMessage(ms);
                        }
                    }).start();
                }
            }
        });


    }
    public String getPlayMusicPath(String id){
        String Token = HttpConnection.getHttpRequest("https://c.y.qq.com/base/fcgi-bin/fcg_music_express_mobile3.fcg?format=json205361747&platform=yqq&cid=205361747&songmid="+id+"&filename=C400"+id+".m4a&guid=126548448");
        System.out.println("Token:" + Token);
        String Vkey = "";
        int i = 0;
        char s;
        String sum;
        i = Token.indexOf("vkey");
        i+=7;
        s = Token.charAt(i);
        sum="";
        while(s!='"'){
            sum+=s;
            i++;
            s = Token.charAt(i);
        }
        Vkey = sum;
        System.out.println("Vkey:"+Vkey);
        String playUrl = "http://ws.stream.qqmusic.qq.com/C400"+id+".m4a?fromtag=0&guid=126548448&vkey="+Vkey;
        System.out.println("playUrl:"+playUrl);
        HttpConnection ssr = new HttpConnection();
        return ssr.downloadMusic(playUrl);
    }
    public ArrayList<MusicInfo> SearchMusic(String name){
        ArrayList<MusicInfo> MusicList = new ArrayList<>();
        //搜索歌名，返回歌单json数据AllSongInfo
        String AllSongInfo = HttpConnection.getHttpRequest("https://c.y.qq.com/soso/fcgi-bin/client_search_cp?aggr=1&cr=1&flag_qc=0&p=1&n=10&w="+name);
        String Songmid = "";
        if(AllSongInfo!=null)
        {
            String[] AllSongSplit = AllSongInfo.split(",");
            //查找mid
            for(int i = 0;i < AllSongSplit.length;i++)
            {
                System.out.println(AllSongSplit[i]);
                int index = AllSongSplit[i].indexOf("songmid");
                System.out.println("index:"+index);
                if(index!=-1){
                    index+=10;
                    char s = AllSongSplit[i].charAt(index);
                    String sum="";
                    while(s!='"'){
                        sum+=s;
                        index++;
                        s = AllSongSplit[i].charAt(index);
                        System.out.println("sum:"+sum);
                    }
                    MusicList.add(new MusicInfo(sum,""));
                }
            }
            //查找歌名
            int j=0;
            for(int i = 0;i < AllSongSplit.length;i++)
            {
                int index = AllSongSplit[i].indexOf("songname\"");
                if(index!=-1){
                    index+=11;
                    char s = AllSongSplit[i].charAt(index);
                    String sum="";
                    while(s!='"'){
                        sum+=s;
                        index++;
                        s = AllSongSplit[i].charAt(index);
                    }
                    if(j<MusicList.size())
                    {
                        MusicList.get(j).Songname = sum;
                        j++;
                    }

                }
            }
            //查找歌手名
            j=0;
            for(int i = 0;i < AllSongSplit.length;i++)
            {
                int index = AllSongSplit[i].indexOf("\"name\"");
                if(index!=-1){
                    index+=8;
                    char s = AllSongSplit[i].charAt(index);
                    String sum="";
                    while(s!='"'){
                        sum+=s;
                        index++;
                        s = AllSongSplit[i].charAt(index);
                    }
                    if(j<MusicList.size())
                    {
                        MusicList.get(j).singer = sum;
                        j++;
                    }

                }
            }



        }
        return MusicList;
    }
    @Override
    public void onDestroy(){
        super.onDestroy();

        if(mediaPlayer!=null){
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
