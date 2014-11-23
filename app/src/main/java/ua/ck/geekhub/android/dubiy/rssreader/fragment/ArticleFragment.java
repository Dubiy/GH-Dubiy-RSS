package ua.ck.geekhub.android.dubiy.rssreader.fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Fragment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ListView;
import android.widget.Toast;

import ua.ck.geekhub.android.dubiy.rssreader.R;
import ua.ck.geekhub.android.dubiy.rssreader.activity.ArticleActivity;
import ua.ck.geekhub.android.dubiy.rssreader.activity.StartActivity;
import ua.ck.geekhub.android.dubiy.rssreader.entity.HabraPost;
import ua.ck.geekhub.android.dubiy.rssreader.utils.PostHolder;

public class ArticleFragment extends Fragment {
    private final String LOG_TAG = getClass().getSimpleName();
    public static final String ARG_ARTICLE_POSITION = ArticleActivity.ARG_ARTICLE_POSITION;
    private int articlePosition = -1;
    private WebView webView;

    private OnFragmentInteractionListener mListener;

    public ArticleFragment() {
        // Required empty public constructor
    }

    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(int position);
    }

    public void loadArticle(int position) {
        //Toast.makeText(getActivity().getApplicationContext(), "loadArticle " + position, Toast.LENGTH_SHORT).show();
        HabraPost post = PostHolder.getPost(position);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.loadDataWithBaseURL("", post.getContent(), "text/html", "UTF-8", "");
        if (getActivity().getClass().getSimpleName().equals(ArticleActivity.class.getSimpleName())) {
            getActivity().getActionBar().setTitle(post.getTitle());
            ListView listView = (ListView)getActivity().findViewById(R.id.left_drawer);
            listView.setItemChecked(position, true);

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt(ARG_ARTICLE_POSITION, position);
            editor.commit();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Bundle args = getActivity().getIntent().getExtras();
        if (args != null) {
            articlePosition = args.getInt(ARG_ARTICLE_POSITION, -1);
            if (articlePosition != -1) {
                loadArticle(articlePosition);
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_article, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        webView = (WebView)view.findViewById(R.id.webView);

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
}
