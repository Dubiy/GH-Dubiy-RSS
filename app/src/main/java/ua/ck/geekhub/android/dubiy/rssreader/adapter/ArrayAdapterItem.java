package ua.ck.geekhub.android.dubiy.rssreader.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import ua.ck.geekhub.android.dubiy.rssreader.R;

/**
 * Created by Gary on 22.11.2014.
 */
public class ArrayAdapterItem extends ArrayAdapter {
    private Context context;
    private int layoutResourceId;
    private String[] data;

    public ArrayAdapterItem(Context context, int layoutResourceId, String[] data) {
        super(context, layoutResourceId, data);
        this.context = context;
        this.layoutResourceId = layoutResourceId;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
//        HabraViewHolder holder;
//        HabraPost


        if (convertView == null) {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            convertView = inflater.inflate(layoutResourceId, parent, false);
        }

        TextView textView = (TextView)convertView.findViewById(R.id.textViewItem);
        textView.setText(this.data[position]);


        return convertView;
    }
}
