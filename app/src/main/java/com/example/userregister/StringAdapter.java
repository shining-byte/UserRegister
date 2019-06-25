package com.example.userregister;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class StringAdapter extends ArrayAdapter<String> {

    private int views;

    public StringAdapter(Context content, int view, List<String> list){
        super(content,view,list);
        this.views = view;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String contents = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(views, parent, false);
        TextView textView = (TextView)view.findViewById(R.id.show_text);
        textView.setText(contents);
        return view;
    }
}
