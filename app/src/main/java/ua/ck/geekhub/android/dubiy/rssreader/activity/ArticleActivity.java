package ua.ck.geekhub.android.dubiy.rssreader.activity;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import ua.ck.geekhub.android.dubiy.rssreader.R;
import ua.ck.geekhub.android.dubiy.rssreader.adapter.HabraAdapter;
import ua.ck.geekhub.android.dubiy.rssreader.database.DBHelper;
import ua.ck.geekhub.android.dubiy.rssreader.entity.HabraPost;
import ua.ck.geekhub.android.dubiy.rssreader.fragment.ArticleFragment;
import ua.ck.geekhub.android.dubiy.rssreader.utils.PostLoader;

public class ArticleActivity extends BaseActivity implements ArticleFragment.OnFragmentInteractionListener {
    private long postId = -1;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private ListView drawerList;
    private String tmpActionBarTitle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);


        if (savedInstanceState != null) {
            postId = savedInstanceState.getInt(ArticleFragment.ARG_POST_ID);
        } else {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                postId = extras.getLong(ArticleFragment.ARG_POST_ID);
            } else {
                Log.d(LOG_TAG, "EMPTY EXTRAZ. MAYBE IT'S NotIFY");
            }
        }

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerList = (ListView) findViewById(R.id.left_drawer);


        DBHelper dbHelper = DBHelper.getInstance(getApplicationContext());
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

        drawerList.setAdapter(new HabraAdapter(this, R.layout.habra_list_item, habraPosts));
        drawerList.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView adapterView, View view, int position, long id) {
                long postId = new Long(((TextView)view.findViewById(R.id.textViewItemId)).getText().toString());
                showArticle(postId, true);
                drawerLayout.closeDrawer(drawerList);
            }
        });
        //TODO set active item
//        drawerList.setItemChecked(postId, true);

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

        showArticle(postId, false);
    }

    private void showArticle(long id, boolean addToBackStack) {
        postId = id;
        ArticleFragment articleFragment = ArticleFragment.newInstance(id);
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_article, articleFragment);
        if (addToBackStack) {
            fragmentTransaction.addToBackStack(null);
        }
        fragmentTransaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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
                HabraPost habraPost = new HabraPost();
                if (habraPost.loadFromDatabase(this ,postId)) {
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_TEXT, habraPost.getLink());
                    intent.putExtra(android.content.Intent.EXTRA_SUBJECT, habraPost.getTitle());
                    startActivity(Intent.createChooser(intent, "Share"));
                } else {
                    Toast.makeText(this, "Post not found", Toast.LENGTH_LONG).show();
                }
            } break;
            default: {
                return super.onOptionsItemSelected(item);
            }
        }
        return true;
    }

    @Override
    public void onFragmentInteraction(long postId) {
        Toast.makeText(getApplicationContext(), "Fragments interaction (ArticleActivity) " + postId, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(ArticleFragment.ARG_POST_ID, postId);
    }
}
