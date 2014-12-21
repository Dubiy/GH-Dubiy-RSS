package ua.ck.geekhub.android.dubiy.rssreader.adapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ua.ck.geekhub.android.dubiy.rssreader.R;
import ua.ck.geekhub.android.dubiy.rssreader.entity.HabraPost;

/**
 * Created by Gary on 08.11.2014.
 */
public class HabraAdapter extends ArrayAdapter {
    private Context mContext;
    private int layoutResourceId;
    private HabraPost data[] = null;
    private LayoutInflater inflater;
    private SimpleDateFormat resultDateFormat;
    private SimpleDateFormat parseDateFormat;
    private List<HabraPost> habraPosts;

    public HabraAdapter(Context mContext, int layoutResourceId, List<HabraPost> habraPosts) {
        super(mContext, layoutResourceId, habraPosts);
        this.layoutResourceId = layoutResourceId;
        this.mContext = mContext;
        this.habraPosts = habraPosts;
        inflater = ((Activity) mContext).getLayoutInflater();
        parseDateFormat = new SimpleDateFormat("E, dd MMM yyyy kk:mm:ss z", Locale.ENGLISH);
        resultDateFormat = new SimpleDateFormat("dd/MM/yyyy kk:mm:ss");
    }

    @Override
    public int getCount() {
        return habraPosts.size();
    }

    @Override
    public Object getItem(int position) {
        return habraPosts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public HabraPost getHabraPost (int position) {
        return (HabraPost) getItem(position);
    }

    static class ViewHolder {
        TextView itemId;
        TextView title;
        TextView date;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(layoutResourceId, parent, false);

            holder = new ViewHolder();
            holder.itemId = (TextView)convertView.findViewById(R.id.textViewItemId);
            holder.title = (TextView)convertView.findViewById(R.id.textViewTitle);
            holder.date = (TextView)convertView.findViewById(R.id.textViewDate);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }

        HabraPost habraPost = getHabraPost(position);
        holder.title.setText(habraPost.getTitle());
        holder.itemId.setText(String.valueOf(habraPost.getId()));
        holder.date.setText(resultDateFormat.format(habraPost.getDate()));
        return convertView;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }
}

