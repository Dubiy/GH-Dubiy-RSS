package ua.ck.geekhub.android.dubiy.rss.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import ua.ck.geekhub.android.dubiy.rss.R;
import ua.ck.geekhub.android.dubiy.rss.adapter.HabraAdapter;
import ua.ck.geekhub.android.dubiy.rss.entity.HabraPost;
import ua.ck.geekhub.android.dubiy.rss.utils.StaticDataHolder;

public class FragmentJsonList extends Fragment {
    private OnFragmentInteractionListener mListener;


    private String[] mEntries;
    private HabraPost[] mHabraPosts;
    private ListView mListView;
    private TextView mTextView;
    private JSONObject mJsonFeed;


    public FragmentJsonList() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_json_list, container, false);
        //TODO розібраться чому ScrollView не дружить з ListView
    }

    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mListView = (ListView)view.findViewById(R.id.listView);
        mTextView = (TextView)view.findViewById(R.id.textView);

        new Thread(new Runnable() {
            @Override
            public void run() {
                DefaultHttpClient client = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet("https://ajax.googleapis.com/ajax/services/feed/load?v=2.0&q=http://habrahabr.ru/rss/best/&num=20");
                try {
                    HttpResponse httpResponse = client.execute(httpGet);
                    final String response = EntityUtils.toString(httpResponse.getEntity());

                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray entries = jsonObject.getJSONObject("responseData").getJSONObject("feed").getJSONArray("entries");

//                    final String[] titles = new String[entries.length()];
                    final HabraPost[] posts = new HabraPost[entries.length()];
                    for (int i = 0; i < entries.length(); i++) {
                        posts[i] = new HabraPost();
//                        Log.d("GARE", posts[i].t);
                        posts[i].setTitle(entries.getJSONObject(i).getString("title"));
                        posts[i].setLink(entries.getJSONObject(i).getString("link"));
                        posts[i].setPublishDate(entries.getJSONObject(i).getString("publishedDate"));
                        posts[i].setShortContent(entries.getJSONObject(i).getString("contentSnippet"));
                        posts[i].setContent(entries.getJSONObject(i).getString("content"));
                    }
                    mHabraPosts = posts;
                    StaticDataHolder.Posts = posts;
                    view.post(new Runnable() {
                        @Override
                        public void run() {
//                            mListView.setAdapter(new ArrayAdapter<String>(view.getContext(), R.layout.drawer_list_item, mUrls));
                            mListView.setAdapter(new HabraAdapter(getActivity(), R.layout.habra_list_item, StaticDataHolder.Posts));
                            mListView.setOnItemClickListener(new ListView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView adapterView, View view, int position, long id) {
                                    mListener.onLeftPaneItemSelected(position, StaticDataHolder.Posts[position]);
                                }
                            });
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }






            }
        }).start();





    }

    /*public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onLeftPaneItemSelected(1);
        }
    }*/

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

    // interface
    public interface OnFragmentInteractionListener {
        public void onLeftPaneItemSelected(Integer position, HabraPost habraPost);
    }

}
