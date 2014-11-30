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
    private SimpleDateFormat simpleDateFormat;
    private SimpleDateFormat parseDateFormat;
    private List<HabraPost> habraPosts;

    public HabraAdapter(Context mContext, int layoutResourceId, List<HabraPost> habraPosts) {
        super(mContext, layoutResourceId, habraPosts);
        this.layoutResourceId = layoutResourceId;
        this.mContext = mContext;
        this.habraPosts = habraPosts;
        inflater = ((Activity) mContext).getLayoutInflater();
        simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy kk:mm:ss");
        parseDateFormat = new SimpleDateFormat("EEE, dd MMM yyyy kk:mm:ss zzz");
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
        TextView title;
        TextView date;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(layoutResourceId, parent, false);

            holder = new ViewHolder();
            holder.title = (TextView)convertView.findViewById(R.id.textViewTitle);
            holder.date = (TextView)convertView.findViewById(R.id.textViewDate);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }

        HabraPost habraPost = getHabraPost(position);
        holder.title.setText(habraPost.getTitle());

        try {
            Date parsedDate = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z").parse(habraPost.getPublishDate());
            holder.date.setText(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(parsedDate));
        } catch (ParseException e) {
            holder.date.setText(habraPost.getPublishDate());
            //Toast.makeText(convertView.getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
        return convertView;

    }



    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }
}

