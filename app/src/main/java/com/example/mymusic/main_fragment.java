package com.example.mymusic;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;



import java.io.IOException;


public class main_fragment extends Fragment {

    SQLiteDatabase mdatabase;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState){
            View view;
            view = inflater.inflate(R.layout.main_fragment,container,false);
            return view;
        }


        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            mdatabase = new SQLiteDbHelper(getActivity()).getWritableDatabase();
            View view = getActivity().findViewById(R.id.query);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(),SearchActivity.class);
                    startActivity(intent);
                }
            });

        }



}
