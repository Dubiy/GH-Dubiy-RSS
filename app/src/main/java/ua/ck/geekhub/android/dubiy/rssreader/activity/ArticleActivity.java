package ua.ck.geekhub.android.dubiy.rssreader.activity;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import ua.ck.geekhub.android.dubiy.rssreader.R;
import ua.ck.geekhub.android.dubiy.rssreader.adapter.HabraAdapter;
import ua.ck.geekhub.android.dubiy.rssreader.entity.HabraPost;
import ua.ck.geekhub.android.dubiy.rssreader.fragment.ArticleFragment;
import ua.ck.geekhub.android.dubiy.rssreader.utils.PostHolder;

public class ArticleActivity extends BaseActivity implements ArticleFragment.OnFragmentInteractionListener {
    private final String LOG_TAG = LOG_TAG_PREFIX + getClass().getSimpleName();
    public static final String ARG_ACTIVE_HABRA_POST = "activeHabraPost";
    private int activeHabraPost = -1;
    private DrawerLayout drawerLayout;
    private ListView drawerList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);

        if (savedInstanceState != null) {
            activeHabraPost = savedInstanceState.getInt(ARG_ACTIVE_HABRA_POST);
        } else {
            Bundle extras = getIntent().getExtras();
            activeHabraPost = extras.getInt(ARG_ACTIVE_HABRA_POST);
        }

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerList = (ListView) findViewById(R.id.left_drawer);
        drawerList.setAdapter(new HabraAdapter(this, R.layout.habra_list_item, PostHolder.getPosts()));
        drawerList.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView adapterView, View view, int position, long id) {
                showArticle(position, true);
                drawerLayout.closeDrawer(drawerList);
            }
        });
        drawerList.setItemChecked(activeHabraPost, true);

        showArticle(activeHabraPost, false);
    }

    private void showArticle(int position, boolean addToBackStack) {
        activeHabraPost = position;
        ArticleFragment articleFragment = ArticleFragment.newInstance(position);
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_article, articleFragment);
        if (addToBackStack) {
            fragmentTransaction.addToBackStack(null);
        }
        fragmentTransaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.article, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(int position) {
        Toast.makeText(getApplicationContext(), "Fragments interaction (ArticleActivity) " + position, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(ARG_ACTIVE_HABRA_POST, activeHabraPost);
    }
}
