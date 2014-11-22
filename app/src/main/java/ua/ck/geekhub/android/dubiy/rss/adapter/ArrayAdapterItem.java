package ua.ck.geekhub.android.dubiy.rss.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.Random;

import ua.ck.geekhub.android.dubiy.rss.R;
import ua.ck.geekhub.android.dubiy.rss.entity.ObjectItem;

/**
 * Created by Gary on 26.10.2014.
 */
public class ArrayAdapterItem extends ArrayAdapter {

    private Context mContext;
    private int layoutResourceId;
    private ObjectItem data[] = null;
    private Random random = new Random();


    public ArrayAdapterItem(Context mContext, int layoutResourceId, ObjectItem[] data) {
        super(mContext, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.mContext = mContext;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        /*
         * The convertView argument is essentially a "ScrapView" as described is Lucas post
         * http://lucasr.org/2012/04/05/performance-tips-for-androids-listview/
         * It will have a non-null value when ListView is asking you recycle the row layout.
         * So, when convertView is not null, you should simply update its contents instead of inflating a new row layout.
         */
        if (convertView == null) {
            //inflate layout
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            convertView = inflater.inflate(layoutResourceId, parent, false);
        }

        //object item based on the position
        ObjectItem objectItem = data[position];

        //get the TextView and then set the text (item name) and tag (item ID) values
        TextView textViewItem = (TextView)convertView.findViewById(R.id.textViewItem);
        textViewItem.setTag(objectItem.getItemId());
        textViewItem.setText(objectItem.getItemName());
        textViewItem.setBackgroundColor(new Color().argb(128, random.nextInt(255), random.nextInt(255), random.nextInt(255)));
        return convertView;

    }


}
