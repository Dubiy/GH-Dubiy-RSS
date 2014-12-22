package ua.ck.geekhub.android.dubiy.rssreader.fragment;

import android.app.ActionBar;
import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import ua.ck.geekhub.android.dubiy.rssreader.R;
import ua.ck.geekhub.android.dubiy.rssreader.adapter.HabraAdapter;
import ua.ck.geekhub.android.dubiy.rssreader.database.DBHelper;
import ua.ck.geekhub.android.dubiy.rssreader.entity.PostEntity;
import ua.ck.geekhub.android.dubiy.rssreader.utils.PostLoader;

//import static ua.ck.geekhub.android.dubiy.rssreader.utils.PostHolder.getPosts;

public class TopicsFragment extends BaseFragment {
    private ListView listView;
    private OnFragmentInteractionListener mListener;
    private View view;
    private boolean showFavouritePosts = false;

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

        final ActionBar actionBar = getActivity().getActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

        final String[] dropdownValues = getResources().getStringArray(R.array.action_bar_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(actionBar.getThemedContext(), android.R.layout.simple_spinner_item, android.R.id.text1, dropdownValues);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Set up the dropdown list navigation in the action bar.
        actionBar.setListNavigationCallbacks(adapter, new ActionBar.OnNavigationListener() {
            @Override
            public boolean onNavigationItemSelected(int position, long id) {
//                Log.d(LOG_TAG, "item selected id: " + id + ". position: " + position);
                if (id == 1) {
                    showFavouritePosts = true;
                } else {
                    showFavouritePosts = false;
                }
                reload_list();
                return true;
            }
        });






    }

    private void reload_list() {
        DBHelper dbHelper = DBHelper.getInstance(getActivity());
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String selection = null;
        String[] selectionArgs = null;
        if (showFavouritePosts) {
            selection = PostEntity.COLUMN_FAVOURITE + " = ?";
            selectionArgs = new String[] { String.valueOf(1)};
        }

        ArrayList<PostEntity> postEntities = new ArrayList<PostEntity>();
        Cursor cursor = db.query(PostEntity.TABLE_NAME, null, selection, selectionArgs, null, null, null);


        if (cursor.moveToFirst()) {
            int columnIndexId = cursor.getColumnIndex(PostEntity._ID);
            int columnIndexTitle = cursor.getColumnIndex(PostEntity.COLUMN_TITLE);
            int columnIndexLink = cursor.getColumnIndex(PostEntity.COLUMN_LINK);
            int columnIndexDate = cursor.getColumnIndex(PostEntity.COLUMN_DATE);
            int columnIndexContent = cursor.getColumnIndex(PostEntity.COLUMN_CONTENT);
            do {
                PostEntity postEntity = new PostEntity();
                postEntity.setId(cursor.getLong(columnIndexId));
                postEntity.setTitle(cursor.getString(columnIndexTitle));
                postEntity.setLink(cursor.getString(columnIndexLink));
                postEntity.setDate(cursor.getLong(columnIndexDate));
                postEntity.setContent(cursor.getString(columnIndexContent));
                postEntities.add(postEntity);
            } while (cursor.moveToNext());
        } else {
            //empty table
        }
        cursor.close();

        listView.setAdapter(new HabraAdapter(getActivity(), R.layout.habra_list_item, postEntities));
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
            PostLoader postLoader = new PostLoader(getActivity(), view);
        postLoader.refresh_posts();
    }


}
