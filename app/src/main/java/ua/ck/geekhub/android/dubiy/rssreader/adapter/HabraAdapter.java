package ua.ck.geekhub.android.dubiy.rssreader.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import ua.ck.geekhub.android.dubiy.rssreader.R;
import ua.ck.geekhub.android.dubiy.rssreader.entity.PostEntity;

/**
 * Created by Gary on 08.11.2014.
 */
public class HabraAdapter extends ArrayAdapter {
    private Context mContext;
    private int layoutResourceId;
    private PostEntity data[] = null;
    private LayoutInflater inflater;
    private SimpleDateFormat resultDateFormat;
    private SimpleDateFormat parseDateFormat;
    private List<PostEntity> postEntities;
    private Long selectedPostId = 0L;

    public HabraAdapter(Context mContext, int layoutResourceId, List<PostEntity> postEntities) {
        super(mContext, layoutResourceId, postEntities);
        this.layoutResourceId = layoutResourceId;
        this.mContext = mContext;
        this.postEntities = postEntities;
        inflater = ((Activity) mContext).getLayoutInflater();
        parseDateFormat = new SimpleDateFormat("E, dd MMM yyyy kk:mm:ss z", Locale.ENGLISH);
        resultDateFormat = new SimpleDateFormat("dd/MM/yyyy kk:mm:ss");
    }

    @Override
    public int getCount() {
        return postEntities.size();
    }

    @Override
    public Object getItem(int position) {
        return postEntities.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public PostEntity getHabraPost (int position) {
        return (PostEntity) getItem(position);
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

        PostEntity postEntity = getHabraPost(position);
        holder.title.setText(postEntity.getTitle());
        if (postEntity.getId() == selectedPostId) {
            convertView.setSelected(true);
        }
        holder.itemId.setText(String.valueOf(postEntity.getId()));
        holder.date.setText(resultDateFormat.format(postEntity.getDate()));
        return convertView;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }
}

