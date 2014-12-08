package ua.ck.geekhub.android.dubiy.rssreader.activity;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
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
import ua.ck.geekhub.android.dubiy.rssreader.utils.PostLoader;

public class ArticleActivity extends BaseActivity implements ArticleFragment.OnFragmentInteractionListener {
    public static final String ARG_ACTIVE_HABRA_POST = "activeHabraPost";
    private int activeHabraPost = -1;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private ListView drawerList;
    private String tmpActionBarTitle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);


        if (savedInstanceState != null) {
            activeHabraPost = savedInstanceState.getInt(ARG_ACTIVE_HABRA_POST);
        } else {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                activeHabraPost = extras.getInt(ARG_ACTIVE_HABRA_POST);
            } else {
                Log.d(LOG_TAG, "EMPTY EXTRAZ. MAYBE IT'S NotIFY");
            }
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

        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.drawable.ic_drawer, R.string.app_name, R.string.app_name) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                tmpActionBarTitle = (String) getActionBar().getTitle();
                getActionBar().setTitle(R.string.app_name);
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                getActionBar().setTitle(tmpActionBarTitle);
                invalidateOptionsMenu();
            }
        };
        drawerLayout.setDrawerListener(drawerToggle);

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
    public boolean onPrepareOptionsMenu(Menu menu) {
        // Called whenever we call invalidateOptionsMenu()
        boolean drawerOpen = drawerLayout.isDrawerOpen(drawerList);
        menu.findItem(R.id.action_refresh).setVisible(drawerOpen);
        menu.findItem(R.id.action_share).setVisible( ! drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh: {
                PostLoader postLoader = new PostLoader(this, drawerLayout);
                postLoader.refresh_posts();
            }
            break;
            case R.id.action_share: {
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
        Toast.makeText(getApplicationContext(), "Fragments interaction (ArticleActivity) " + position, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(ARG_ACTIVE_HABRA_POST, activeHabraPost);
    }
}
