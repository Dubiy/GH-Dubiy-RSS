package ua.ck.geekhub.android.dubiy.rssreader.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import ua.ck.geekhub.android.dubiy.rssreader.R;
import ua.ck.geekhub.android.dubiy.rssreader.adapter.HabraAdapter;
import ua.ck.geekhub.android.dubiy.rssreader.fragment.ArticleFragment;
import ua.ck.geekhub.android.dubiy.rssreader.utils.PostHolder;

public class ArticleActivity extends Activity implements ArticleFragment.OnFragmentInteractionListener {
    public static final String ARG_ARTICLE_POSITION = "ARTICLE_POSITION";
    private int articlePosition = -2;
    private DrawerLayout drawerLayout;
    private ListView drawerList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerList = (ListView) findViewById(R.id.left_drawer);
        drawerList.setAdapter(new HabraAdapter(this, R.layout.habra_list_item, PostHolder.getPosts()));
        drawerList.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView adapterView, View view, int position, long id) {
                ArticleFragment articleFragment = (ArticleFragment)getFragmentManager().findFragmentById(R.id.fragment_article);
                articleFragment.loadArticle(position);
                drawerLayout.closeDrawer(drawerList);
//                view.
            }
        });

        /*
        Intent intent = getIntent();
        articlePosition = intent.getExtras().getInt(ARG_ARTICLE_POSITION, -3);
        Toast.makeText(getApplicationContext(), "articlePosition extra " + articlePosition, Toast.LENGTH_LONG).show();
        */
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
}
