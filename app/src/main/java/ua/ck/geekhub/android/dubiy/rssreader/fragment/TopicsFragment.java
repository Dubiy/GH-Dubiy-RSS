package ua.ck.geekhub.android.dubiy.rssreader.fragment;

import android.app.Activity;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;

import ua.ck.geekhub.android.dubiy.rssreader.R;
import ua.ck.geekhub.android.dubiy.rssreader.adapter.ArrayAdapterItem;
import ua.ck.geekhub.android.dubiy.rssreader.adapter.HabraAdapter;
import ua.ck.geekhub.android.dubiy.rssreader.database.DBHelper;
import ua.ck.geekhub.android.dubiy.rssreader.database.PostTable;
import ua.ck.geekhub.android.dubiy.rssreader.entity.HabraPost;
import ua.ck.geekhub.android.dubiy.rssreader.utils.PostHolder;
import ua.ck.geekhub.android.dubiy.rssreader.utils.PostLoader;

import static ua.ck.geekhub.android.dubiy.rssreader.utils.PostHolder.getPosts;

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
        public void onFragmentInteraction(int position);
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

//        listView.setAdapter(new HabraAdapter(getActivity(), R.layout.habra_list_item, PostHolder.getPosts()));
        listView.setAdapter(new HabraAdapter(getActivity(), R.layout.habra_list_item, PostHolder.getPosts()));
        listView.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView adapterView, View view, int position, long id) {
                mListener.onFragmentInteraction(position);
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
        DBHelper dbHelper = DBHelper.getInstance(getActivity());
        if (dbHelper != null) {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            Log.d(LOG_TAG, "helper: " + dbHelper.hashCode());
            Log.d(LOG_TAG, "db: " + db.hashCode());
        } else {
            Log.d(LOG_TAG, "helper == null");

        }

//        ContentValues values = new ContentValues();
//        values.put(PostTable.COLUMN_TITLE, "Hello");
//        values.put(PostTable.COLUMN_DATE, 1500005);
//        values.put(PostTable.COLUMN_LINK, "http://garik.pp.ua");
//        values.put(PostTable.COLUMN_CONTENT, "Hello, Hello, Hello, i dunno");
//
//        long rowId = db.insert(PostTable.TABLE_NAME, null, values);




        PostLoader postLoader = new PostLoader(getActivity(), view);
        postLoader.refresh_posts();
    }


}
