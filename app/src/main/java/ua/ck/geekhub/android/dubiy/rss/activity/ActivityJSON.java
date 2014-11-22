package ua.ck.geekhub.android.dubiy.rss.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.gson.Gson;

import ua.ck.geekhub.android.dubiy.rss.R;
import ua.ck.geekhub.android.dubiy.rss.entity.HabraPost;
import ua.ck.geekhub.android.dubiy.rss.fragment.FragmentJsonContent;
import ua.ck.geekhub.android.dubiy.rss.fragment.FragmentJsonList;

public class ActivityJSON extends Activity implements FragmentJsonList.OnFragmentInteractionListener {
    private final String LOG_TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_json);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_json, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onLeftPaneItemSelected(Integer position, HabraPost habraPost) {
        Log.d(LOG_TAG, position.toString() + " " + habraPost.toString());

        FragmentJsonContent fragmentJsonContent = (FragmentJsonContent)getFragmentManager().findFragmentById(R.id.fragment_content);

        if (fragmentJsonContent == null) {
//            //ActivityB here
            Intent intent = new Intent(this, ActivityJSONright.class);
            intent.putExtra(ActivityJSONright.ARG_HABRAPOST, new Gson().toJson(habraPost));
            startActivity(intent);
        } else {
            fragmentJsonContent.LoadSomeContent(position, habraPost);
        }
    }
}
