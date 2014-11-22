package ua.ck.geekhub.android.dubiy.rss.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import ua.ck.geekhub.android.dubiy.rss.R;
import ua.ck.geekhub.android.dubiy.rss.entity.HabraPost;

/**
 * Created by Gary on 08.11.2014.
 */
public class HabraAdapter extends ArrayAdapter {
    private Context mContext;
    private int layoutResourceId;
    private HabraPost data[] = null;

    public HabraAdapter(Context mContext, int layoutResourceId, HabraPost[] data) {
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
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            convertView = inflater.inflate(layoutResourceId, parent, false);
        }

        HabraPost objectItem = data[position];

        TextView textViewTitle = (TextView)convertView.findViewById(R.id.textViewTitle);
        TextView textViewDate = (TextView)convertView.findViewById(R.id.textViewDate);

        textViewTitle.setText(objectItem.getTitle());

        String resultDate = "";
        try {
            Date parsedDate = new SimpleDateFormat("EEE, dd MMM yyyy kk:mm:ss zzz").parse(objectItem.getPublishDate());
            resultDate = new SimpleDateFormat("dd/MM/yyyy kk:mm:ss").format(parsedDate);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        textViewDate.setText(resultDate);

        return convertView;

    }

}
