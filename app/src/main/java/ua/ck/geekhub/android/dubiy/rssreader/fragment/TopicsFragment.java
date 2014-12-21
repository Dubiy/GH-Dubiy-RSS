package ua.ck.geekhub.android.dubiy.rssreader.fragment;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import ua.ck.geekhub.android.dubiy.rssreader.R;
import ua.ck.geekhub.android.dubiy.rssreader.adapter.HabraAdapter;
import ua.ck.geekhub.android.dubiy.rssreader.database.DBHelper;
import ua.ck.geekhub.android.dubiy.rssreader.entity.HabraPost;
import ua.ck.geekhub.android.dubiy.rssreader.utils.PostLoader;

//import static ua.ck.geekhub.android.dubiy.rssreader.utils.PostHolder.getPosts;

public class TopicsFragment extends BaseFragment {
    private ListView listView;
    private OnFragmentInteractionListener mListener;
    private View view;

    public static TopicsFragment newInstance() {
        TopicsFragment fragment = new TopicsFragment();
        //Bundle args = new Bundle();
        //args.putString(ARG_PARAM1, param1);
        //args.putString(ARG_PARAM2, param2);
        //fragment.setArguments(args);
        return fragment;
    }

    public TopicsFragment() {
    }

    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(long postId);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_topics, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view = view;
        listView = (ListView) view.findViewById(R.id.listView);

        if (savedInstanceState == null) {
            refresh_posts();
        }


        DBHelper dbHelper = DBHelper.getInstance(getActivity());
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ArrayList<HabraPost> habraPosts = new ArrayList<HabraPost>();
        Cursor cursor = db.query(HabraPost.TABLE_NAME, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            int columnIndexId = cursor.getColumnIndex(HabraPost._ID);
            int columnIndexTitle = cursor.getColumnIndex(HabraPost.COLUMN_TITLE);
            int columnIndexLink = cursor.getColumnIndex(HabraPost.COLUMN_LINK);
            int columnIndexDate = cursor.getColumnIndex(HabraPost.COLUMN_DATE);
            int columnIndexContent = cursor.getColumnIndex(HabraPost.COLUMN_CONTENT);
            do {
                HabraPost habraPost = new HabraPost();
                habraPost.setId(cursor.getLong(columnIndexId));
                habraPost.setTitle(cursor.getString(columnIndexTitle));
                habraPost.setLink(cursor.getString(columnIndexLink));
                habraPost.setDate(cursor.getLong(columnIndexDate));
                habraPost.setContent(cursor.getString(columnIndexContent));
                habraPosts.add(habraPost);
            } while (cursor.moveToNext());
        } else {
            //empty table
        }
        cursor.close();

        listView.setAdapter(new HabraAdapter(getActivity(), R.layout.habra_list_item, habraPosts));
        listView.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView adapterView, View view, int position, long id) {
                long postId = new Long(((TextView)view.findViewById(R.id.textViewItemId)).getText().toString());
//                Toast.makeText(getActivity(), "postID: " + postId, Toast.LENGTH_SHORT).show();
                mListener.onFragmentInteraction(postId);
            }
        });
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void refresh_posts() {
//        DBHelper dbHelper = DBHelper.getInstance(getActivity());
//        SQLiteDatabase db = dbHelper.getWritableDatabase();

//        ContentValues values = new ContentValues();
//        values.put(PostTable.COLUMN_TITLE, "Gary AHAHA database");
//        values.put(PostTable.COLUMN_DATE, 1500005);
//        values.put(PostTable.COLUMN_LINK, "http://garik.pp.ua");
//        values.put(PostTable.COLUMN_CONTENT, "Hello, Hello, Hello, i dunno");
//        values.put(PostTable.COLUMN_FAVOURITE, 1);
//
//        long rowId = db.insert(PostTable.TABLE_NAME, null, values);
        PostLoader postLoader = new PostLoader(getActivity(), view);
        postLoader.refresh_posts();
    }


}
