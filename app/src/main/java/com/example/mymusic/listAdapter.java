package com.example.mymusic;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class listAdapter extends ArrayAdapter<MusicInfo> {

    private int resourseId;
    public listAdapter(Context context, int resource, List<MusicInfo> objects) {
        super(context, resource, objects);
        this.resourseId=resource;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        MusicInfo list = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourseId,parent,false);
        TextView textView = view.findViewById(R.id.name);
        textView.setText(list.Songname);
        TextView textView1 = view.findViewById(R.id.singer);
        textView1.setText(list.singer);
        ImageView imageView = view.findViewById(R.id.isPlaying);
//        imageView.setBackground();
        return view;

    }
}
