package ua.ck.geekhub.android.dubiy.rss.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import ua.ck.geekhub.android.dubiy.rss.R;
import ua.ck.geekhub.android.dubiy.rss.fragment.FragmentMP_detail;
import ua.ck.geekhub.android.dubiy.rss.fragment.FragmentMP_list;

public class ActivityMultipane extends Activity implements FragmentMP_list.OnLeftPaneSelectedListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multipane);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_multipane, menu);
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
    public void onLeftPaneItemSelected(int position) {
        FragmentMP_detail fragmentMP_detail = (FragmentMP_detail)getFragmentManager().findFragmentById(R.id.fragment_mp_detail);
        if (fragmentMP_detail == null) {
//            //ActivityB here
            Intent intent = new Intent(this, ActivityMultipaneRight.class);
            intent.putExtra(FragmentMP_detail.ARG_LEFTPANEITEMPOSITION, position);
            startActivity(intent);
        } else {
            fragmentMP_detail.LoadSomeContent(position);
        }
    }
}
