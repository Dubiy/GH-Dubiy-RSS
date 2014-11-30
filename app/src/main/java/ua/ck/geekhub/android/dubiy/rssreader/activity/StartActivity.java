package ua.ck.geekhub.android.dubiy.rssreader.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import ua.ck.geekhub.android.dubiy.rssreader.R;
import ua.ck.geekhub.android.dubiy.rssreader.entity.HabraPost;
import ua.ck.geekhub.android.dubiy.rssreader.fragment.ArticleFragment;
import ua.ck.geekhub.android.dubiy.rssreader.fragment.TopicsFragment;
import ua.ck.geekhub.android.dubiy.rssreader.utils.PostHolder;


public class StartActivity extends BaseActivity implements TopicsFragment.OnFragmentInteractionListener, ArticleFragment.OnFragmentInteractionListener {

    private boolean isMultiPanel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        isMultiPanel = findViewById(R.id.fragment_article) != null;

        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        TopicsFragment topicsFragment = TopicsFragment.newInstance();
        fragmentTransaction.replace(R.id.fragment_topics, topicsFragment);

        if (isMultiPanel) {
            ArticleFragment articleFragment = new ArticleFragment();
            fragmentTransaction.replace(R.id.fragment_article, articleFragment);
        }
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        Log.d(LOG_TAG, "onBackPressed. isMultiPanel: " + isMultiPanel);
        if ( ! isMultiPanel) {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
        super.onBackPressed();


    }

    @Override
    protected void onResume() {
        super.onResume();
        isMultiPanel = findViewById(R.id.fragment_article) != null;
        invalidateOptionsMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.start, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // Called whenever we call invalidateOptionsMenu()
        menu.findItem(R.id.action_share).setVisible(isMultiPanel);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_about: {
                aboutAuthor();
            }
            break;
            case R.id.action_refresh: {
                FragmentManager fragmentManager = getFragmentManager();
                TopicsFragment topicsFragment = (TopicsFragment)fragmentManager.findFragmentById(R.id.fragment_topics);
                topicsFragment.refresh_posts();
            }
            break;
            case R.id.action_share: {
                int activeHabraPost = -1;
                ListView listView = (ListView) findViewById(R.id.listView);
                if (listView != null) {
                    activeHabraPost = listView.getCheckedItemPosition();
                }
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                HabraPost habraPost = PostHolder.getPost(activeHabraPost);
                intent.putExtra(Intent.EXTRA_TEXT, habraPost.getLink());
                intent.putExtra(android.content.Intent.EXTRA_SUBJECT, habraPost.getTitle());
                startActivity(Intent.createChooser(intent, "Share"));
            } break;
            default: {
                return super.onOptionsItemSelected(item);
            }
        }
        return true;


    }

    @Override
    public void onFragmentInteraction(int position) {
        if (isMultiPanel) {
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            ArticleFragment articleFragment = ArticleFragment.newInstance(position);
            fragmentTransaction.replace(R.id.fragment_article, articleFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        } else {
            Intent intent = new Intent(this, ArticleActivity.class);
            intent.putExtra(ArticleActivity.ARG_ACTIVE_HABRA_POST, position);
            startActivity(intent);
        }
    }
}
