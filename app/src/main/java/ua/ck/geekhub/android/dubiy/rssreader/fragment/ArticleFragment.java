package ua.ck.geekhub.android.dubiy.rssreader.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;

import ua.ck.geekhub.android.dubiy.rssreader.R;
import ua.ck.geekhub.android.dubiy.rssreader.activity.ArticleActivity;
import ua.ck.geekhub.android.dubiy.rssreader.entity.HabraPost;

public class ArticleFragment extends BaseFragment {
    public static final String ARG_POST_ID = "postId";
    private long postId = -1;
    private WebView webView;
    private OnFragmentInteractionListener mListener;

    public static ArticleFragment newInstance(long postId) {
        ArticleFragment fragment = new ArticleFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_POST_ID, postId);
        fragment.setArguments(args);
        return fragment;
    }

    public ArticleFragment() {
        // Required empty public constructor
    }

    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(long postId);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            postId = getArguments().getLong(ARG_POST_ID);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(ARG_POST_ID, postId);
    }


    public void loadArticle(long id) {
        postId = id;

        if (postId != -1) {

            HabraPost habraPost = new HabraPost();
            if (habraPost.loadFromDatabase(getActivity(), postId)) {
                webView.loadDataWithBaseURL("", habraPost.getContent(), "text/html", "UTF-8", "");
                if (getActivity().getClass().getSimpleName().equals(ArticleActivity.class.getSimpleName())) {
                    getActivity().getActionBar().setTitle(habraPost.getTitle());
                }
            } else {
                show404();
            }
        } else {
            showInfo();
        }
    }

    private void showInfo() {
        webView.loadUrl("file:///android_asset/short_info.html");
    }

    private void show404() {
        webView.loadUrl("file:///android_asset/page_404.html");
    }

    @Override
    public void onResume() {
        super.onResume();
        loadArticle(postId);

        //TODO set active item in listView
        /*ListView drawerList = (ListView) getActivity().findViewById(R.id.left_drawer);
        if (drawerList != null) {
            drawerList.setItemChecked(activeHabraPost, true);
        }
        ListView listView = (ListView) getActivity().findViewById(R.id.listView);
        if (listView != null) {
            listView.setItemChecked(activeHabraPost, true);
        }*/
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
