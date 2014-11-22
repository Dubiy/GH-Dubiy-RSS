package ua.ck.geekhub.android.dubiy.rss.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import ua.ck.geekhub.android.dubiy.rss.R;
import ua.ck.geekhub.android.dubiy.rss.adapter.HabraAdapter;
import ua.ck.geekhub.android.dubiy.rss.fragment.FragmentJsonContent;
import ua.ck.geekhub.android.dubiy.rss.utils.StaticDataHolder;

public class ActivityJSONright extends Activity {

    public final static String ARG_HABRAPOST = "HABRAPOST";

    private String[] mMenuItems;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jsonright);

        mMenuItems = getResources().getStringArray(R.array.drawer_menu);
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        mDrawerList = (ListView)findViewById(R.id.left_drawer);

//        mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, mMenuItems));
//        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        //TODO виділити акивний пункт в списку
        mDrawerList.setAdapter(new HabraAdapter(this, R.layout.habra_list_item, StaticDataHolder.Posts));
        mDrawerList.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override

            public void onItemClick(AdapterView adapterView, View view, int position, long id) {

                FragmentJsonContent fragmentJsonContent = (FragmentJsonContent)getFragmentManager().findFragmentById(R.id.fragment);
                if (fragmentJsonContent != null) {
//                    Toast.makeText(getApplicationContext(), "Item Clicked " + position, Toast.LENGTH_LONG).show();
                    fragmentJsonContent.LoadSomeContent(position, StaticDataHolder.Posts[position]);
                }
                mDrawerLayout.closeDrawer(mDrawerList);
            }
        });

        //TODO зробить щоб ActionBar переносило назад, в попередню актівіті
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_jsonright, menu);
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
}
