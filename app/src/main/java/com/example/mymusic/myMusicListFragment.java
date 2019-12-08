package com.example.mymusic;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

public class myMusicListFragment extends Fragment {
    SQLiteDatabase mDataBase;
    ArrayList<MusicInfo> arrayList;
    ListView listView;
    MusicInfo musicInfo;
    MediaPlayer mediaPlayer;
    Button play;
    boolean op = false;
    TextView nowTime;
    TextView maxTime;
    Button down;
    TextView songName;
    SeekBar seekBar;
    SeekBarThread r;
    String NowPath;
    Button mode;
    Button up;
    TextView singer;
    int nowId;
    //1  单曲,2 列表,3 随机
    int Mode = 1;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState){
        View view;
        view = inflater.inflate(R.layout.music_list_fragment,container,false);


        return view;
    }
    private Handler handler = new Handler() {

        @Override
        public void handleMessage(final Message msg) {
            super.handleMessage(msg);
            System.out.println("msg:"+msg.what);
            seekBar.setProgress(msg.what);
            System.out.println("prosetOK");
            int min = msg.what/60/1000;
            int sec = (msg.what-(60*min*1000))/1000;
            String time;
            if(sec<10){
                time = min + ":0" + sec;
            }else{
                time = min+":"+sec;
            }
            nowTime.setText(time);
            if(nowTime.getText().toString().equals(maxTime.getText().toString())){
                System.out.println("will_temp");
                AutoNextMusic();

            }
            System.out.println("setTime:"+nowTime.getText().toString());

        }
    };
    class SeekBarThread implements Runnable {
        @Override
        public void run() {
            while (mediaPlayer != null) {
//                 将SeekBar位置设置到当前播放位置
                if(mediaPlayer!=null&&!op) {
                    try{
                        if (mediaPlayer.isPlaying()) {
                            System.out.println("now:"+mediaPlayer.getCurrentPosition());
                            Message ms = new Message();
                            ms.what = mediaPlayer.getCurrentPosition();
                            handler.sendMessage(ms);
                            //播放完毕要下一首了
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
    public void PreMusic(){
        switch (Mode){
            case 1:
            case 2:
                if(arrayList!=null){
                    if(arrayList.size()>0){
                        if(nowId-1>=0){
                            nowId--;
                        }else{
                            nowId = arrayList.size()-1;
                        }
                        setPlaying(nowId);
                    }
                }


                if(mediaPlayer!=null){
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    mediaPlayer = new MediaPlayer();
                    singer.setText(arrayList.get(nowId).singer);
                    try {
                        mediaPlayer.setDataSource(arrayList.get(nowId).Path);
                        mediaPlayer.prepare();
                        mediaPlayer.start();
                        //设置进度条大小为当前音频最长毫秒数
                        seekBar.setMax(mediaPlayer.getDuration());
                        System.out.println("time:"+mediaPlayer.getDuration());
                        int min = mediaPlayer.getDuration() / 1000 / 60;
                        System.out.println("min:"+min);
                        int sec = (mediaPlayer.getDuration() - (min * 60 * 1000)) / 1000;
                        String time;
                        if(sec<10){
                            time = min+":0"+sec;
                        }else{
                            time = min+":"+sec;
                        }
                        maxTime.setText(time);
                        System.out.println("setOk:"+maxTime.getText().toString());
                        songName.setText(arrayList.get(nowId).Songname);
                        play.setBackground(getResources().getDrawable(R.drawable.stop));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case 3:
                if(arrayList!=null){
                    if(arrayList.size()>0){
                        int temp;
                        Random random = new Random();
                        temp = random.nextInt(arrayList.size());
                        while(temp==nowId){
                            temp = random.nextInt(arrayList.size());
                        }
                        nowId = temp;
                        setPlaying(nowId);
                    }
                }


                if(mediaPlayer!=null){
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    mediaPlayer = new MediaPlayer();
                    singer.setText(arrayList.get(nowId).singer);
                    try {
                        mediaPlayer.setDataSource(arrayList.get(nowId).Path);
                        mediaPlayer.prepare();
                        mediaPlayer.start();
                        //设置进度条大小为当前音频最长毫秒数
                        seekBar.setMax(mediaPlayer.getDuration());
                        System.out.println("time:"+mediaPlayer.getDuration());
                        int min = mediaPlayer.getDuration() / 1000 / 60;
                        System.out.println("min:"+min);
                        int sec = (mediaPlayer.getDuration() - (min * 60 * 1000)) / 1000;
                        String time;
                        if(sec<10){
                            time = min+":0"+sec;
                        }else{
                            time = min+":"+sec;
                        }
                        maxTime.setText(time);
                        System.out.println("setOk:"+maxTime.getText().toString());
                        songName.setText(arrayList.get(nowId).Songname);
                        play.setBackground(getResources().getDrawable(R.drawable.stop));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }
    public void setPlaying(int playing){
        //listView.getLastVisiblePosition()-listView.getFirstVisiblePosition()
        System.out.println("start:"+listView.getFirstVisiblePosition());
        for(int i=0;i<listView.getLastVisiblePosition()-listView.getFirstVisiblePosition()+1;i++)
        {
            View v = listView.getChildAt(i);
            ImageView image = v.findViewById(R.id.isPlaying);
            image.setBackgroundColor(Color.WHITE);
        }
        ImageView image = listView.getChildAt(playing).findViewById(R.id.isPlaying);
        image.setBackground(getResources().getDrawable(R.drawable.playing));
    }
    public void NextMusic(){
        switch (Mode){
            case 1:
            case 2:
                if(arrayList!=null){
                    if(arrayList.size()>0){
                        nowId = (nowId+1)%arrayList.size();
                        setPlaying(nowId);
                    }
                }


                if(mediaPlayer!=null){
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    mediaPlayer = new MediaPlayer();
                    singer.setText(arrayList.get(nowId).singer);
                    try {
                        mediaPlayer.setDataSource(arrayList.get(nowId).Path);
                        mediaPlayer.prepare();
                        mediaPlayer.start();
                        //设置进度条大小为当前音频最长毫秒数
                        seekBar.setMax(mediaPlayer.getDuration());
                        System.out.println("time:"+mediaPlayer.getDuration());
                        int min = mediaPlayer.getDuration() / 1000 / 60;
                        System.out.println("min:"+min);
                        int sec = (mediaPlayer.getDuration() - (min * 60 * 1000)) / 1000;
                        String time;
                        if(sec<10){
                            time = min+":0"+sec;
                        }else{
                            time = min+":"+sec;
                        }
                        maxTime.setText(time);
                        System.out.println("setOk:"+maxTime.getText().toString());
                        songName.setText(arrayList.get(nowId).Songname);
                        play.setBackground(getResources().getDrawable(R.drawable.stop));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case 3:
                if(arrayList!=null){
                    if(arrayList.size()>0){
                        int temp;
                        Random random = new Random();
                        temp = random.nextInt(arrayList.size());
                        while(temp==nowId){
                            temp = random.nextInt(arrayList.size());
                        }
                        nowId = temp;
                        setPlaying(nowId);
                    }
                }


                if(mediaPlayer!=null){
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    mediaPlayer = new MediaPlayer();
                    singer.setText(arrayList.get(nowId).singer);
                    try {
                        mediaPlayer.setDataSource(arrayList.get(nowId).Path);
                        mediaPlayer.prepare();
                        mediaPlayer.start();
                        //设置进度条大小为当前音频最长毫秒数
                        seekBar.setMax(mediaPlayer.getDuration());
                        System.out.println("time:"+mediaPlayer.getDuration());
                        int min = mediaPlayer.getDuration() / 1000 / 60;
                        System.out.println("min:"+min);
                        int sec = (mediaPlayer.getDuration() - (min * 60 * 1000)) / 1000;
                        String time;
                        if(sec<10){
                            time = min+":0"+sec;
                        }else{
                            time = min+":"+sec;
                        }
                        maxTime.setText(time);
                        System.out.println("setOk:"+maxTime.getText().toString());
                        songName.setText(arrayList.get(nowId).Songname);
                        play.setBackground(getResources().getDrawable(R.drawable.stop));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    @Override
    public void onDestroyView() {

        super.onDestroyView();
        if(mediaPlayer!=null){
            if(mediaPlayer.isPlaying()){
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
            }
        }

    }
    //自动播放下一首
    public void AutoNextMusic(){
        switch (Mode){
            case 1:
                if(mediaPlayer!=null){
                    mediaPlayer.seekTo(0);
                }
                break;
            case 2:
                nowId = (nowId+1)%arrayList.size();
                singer.setText(arrayList.get(nowId).singer);
                setPlaying(nowId);
                if(mediaPlayer!=null){
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    mediaPlayer = new MediaPlayer();
                    try {
                        mediaPlayer.setDataSource(arrayList.get(nowId).Path);
                        mediaPlayer.prepare();
                        mediaPlayer.start();
                        //设置进度条大小为当前音频最长毫秒数
                        seekBar.setMax(mediaPlayer.getDuration());
                        System.out.println("time:"+mediaPlayer.getDuration());
                        int min = mediaPlayer.getDuration() / 1000 / 60;
                        System.out.println("min:"+min);
                        int sec = (mediaPlayer.getDuration() - (min * 60 * 1000)) / 1000;
                        String time;
                        if(sec<10){
                            time = min+":0"+sec;
                        }else{
                            time = min+":"+sec;
                        }
                        maxTime.setText(time);
                        System.out.println("setOk:"+maxTime.getText().toString());
                        songName.setText(arrayList.get(nowId).Songname);
                        play.setBackground(getResources().getDrawable(R.drawable.stop));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case 3:
                int temp;
                Random random = new Random();
                temp = random.nextInt(arrayList.size());
                while(temp==nowId){
                    temp = random.nextInt(arrayList.size());
                }
                nowId = temp;
                setPlaying(nowId);
                singer.setText(arrayList.get(nowId).singer);
                if(mediaPlayer!=null){
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    mediaPlayer = new MediaPlayer();
                    try {
                        mediaPlayer.setDataSource(arrayList.get(nowId).Path);
                        mediaPlayer.prepare();
                        mediaPlayer.start();
                        //设置进度条大小为当前音频最长毫秒数
                        seekBar.setMax(mediaPlayer.getDuration());
                        System.out.println("time:"+mediaPlayer.getDuration());
                        int min = mediaPlayer.getDuration() / 1000 / 60;
                        System.out.println("min:"+min);
                        int sec = (mediaPlayer.getDuration() - (min * 60 * 1000)) / 1000;
                        String time;
                        if(sec<10){
                            time = min+":0"+sec;
                        }else{
                            time = min+":"+sec;
                        }
                        maxTime.setText(time);
                        System.out.println("setOk:"+maxTime.getText().toString());
                        songName.setText(arrayList.get(nowId).Songname);
                        play.setBackground(getResources().getDrawable(R.drawable.stop));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }
    public void init(){
        arrayList = new ArrayList<MusicInfo>();
        Cursor cursor = mDataBase.query("music",null,
                "SongName like '%"+"%"+"%'",
                null, null, null, null);
        while(cursor.moveToNext()){

            String name = cursor.getString(1);
            String path = cursor.getString(2);
            String singer = cursor.getString(3);
            MusicInfo musicInfo = new MusicInfo();
            musicInfo.Path = path;
            musicInfo.Songname = name;
            musicInfo.singer = singer;
            arrayList.add(musicInfo);

        }
        cursor.close();
        listAdapter adapter = new listAdapter(getActivity(),R.layout.word_item,arrayList);
        listView.setAdapter(adapter);
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listView = getActivity().findViewById(R.id.MusicList);
        up = getActivity().findViewById(R.id.up);
        down = getActivity().findViewById(R.id.down);
        singer = getActivity().findViewById(R.id.SongSinger);
        mode = getActivity().findViewById(R.id.mode);
        mDataBase = new SQLiteDbHelper(getActivity()).getWritableDatabase();
        init();
        seekBar = getActivity().findViewById(R.id.listen_progress);
        play = getActivity().findViewById(R.id.play);
        songName = getActivity().findViewById(R.id.SongName);
        nowTime = getActivity().findViewById(R.id.nowTime);
        maxTime = getActivity().findViewById(R.id.maxTime);
        down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NextMusic();
            }
        });
        mode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Mode++;
                if(Mode>3)
                    Mode = 1;
                switch (Mode){
                    case 1:
                        mode.setBackground(getResources().getDrawable(R.drawable.one));
                        Toast.makeText(getActivity(),"单曲循环",Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
                        mode.setBackground(getResources().getDrawable(R.drawable.list));
                        Toast.makeText(getActivity(),"列表循环",Toast.LENGTH_SHORT).show();
                        break;
                    case 3:
                        mode.setBackground(getResources().getDrawable(R.drawable.random));
                        Toast.makeText(getActivity(),"随机播放",Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
        up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PreMusic();
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
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                nowId = position;
                setPlaying(position);
                musicInfo = arrayList.get(position);
                System.out.println("你点击的第"+position+"首");
                NowPath = musicInfo.Path;
                if(!NowPath.equals("")) {
                    if (mediaPlayer != null) {
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
                        System.out.println("time:"+mediaPlayer.getDuration());
                        int min = mediaPlayer.getDuration() / 1000 / 60;
                        System.out.println("min:"+min);
                        int sec = (mediaPlayer.getDuration() - (min * 60 * 1000)) / 1000;
                        String time;
                        if(sec<10){
                            time = min+":0"+sec;
                        }else{
                            time = min+":"+sec;
                        }
                        maxTime.setText(time);
                        singer.setText(musicInfo.singer);
                        System.out.println("setOk:"+maxTime.getText().toString());
                        songName.setText(musicInfo.Songname);
                        play.setBackground(getResources().getDrawable(R.drawable.stop));
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                r = new SeekBarThread();
                                r.run();
                            }
                        }).start();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                new AlertDialog.Builder(getActivity()).setTitle("是否删除"+arrayList.get(position).Songname+"?")
                        .setPositiveButton("删除", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mDataBase.delete("music","path = '"+arrayList.get(position).Path+"'",null);
                                Toast.makeText(getActivity(),"删除成功",Toast.LENGTH_SHORT).show();
                                init();
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).show();
                return false;
            }
        });

    }
}
