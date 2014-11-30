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

public class ArticleFragment extends BaseFragment {
    public static final String ARG_ACTIVE_HABRA_POST = "activeHabraPost";
    private int activeHabraPost = -1;
    private WebView webView;
    private OnFragmentInteractionListener mListener;

    public static ArticleFragment newInstance(int activeHabraPost) {
        ArticleFragment fragment = new ArticleFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_ACTIVE_HABRA_POST, activeHabraPost);
        fragment.setArguments(args);
        return fragment;
    }

    public ArticleFragment() {
        // Required empty public constructor
    }

    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(int position);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            activeHabraPost = getArguments().getInt(ARG_ACTIVE_HABRA_POST);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(ARG_ACTIVE_HABRA_POST, activeHabraPost);
    }


    public void loadArticle(int position) {
        activeHabraPost = position;

        if (position != -1) {
            HabraPost post = PostHolder.getPost(position);
            webView.loadDataWithBaseURL("", post.getContent(), "text/html", "UTF-8", "");
            if (getActivity().getClass().getSimpleName().equals(ArticleActivity.class.getSimpleName())) {
                getActivity().getActionBar().setTitle(post.getTitle());
            }
        } else {
            webView.loadUrl("file:///android_asset/short_info.html");
//            webView.loadDataWithBaseURL("", "hahaha, hello", "text/html", "UTF-8", "");
        }


    }

    @Override
    public void onResume() {
        super.onResume();
        loadArticle(activeHabraPost);
        ListView drawerList = (ListView) getActivity().findViewById(R.id.left_drawer);
        if (drawerList != null) {
            drawerList.setItemChecked(activeHabraPost, true);
        }
        ListView listView = (ListView) getActivity().findViewById(R.id.listView);
        if (listView != null) {
            listView.setItemChecked(activeHabraPost, true);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_article, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        webView = (WebView) view.findViewById(R.id.webView);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
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
