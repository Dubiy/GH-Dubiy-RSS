package ua.ck.geekhub.android.dubiy.rssreader.activity;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.facebook.AppEventsLogger;

import ua.ck.geekhub.android.dubiy.rssreader.R;
import ua.ck.geekhub.android.dubiy.rssreader.fragment.LoginFragment;
import ua.ck.geekhub.android.dubiy.rssreader.fragment.TopicsFragment;

/**
 * Created by Gary on 06.01.2015.
 */
public class LoginActivity extends FragmentActivity {
    private LoginFragment loginFragment;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginFragment = new LoginFragment();
            /*getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragment_login, loginFragment)
                    .commit();*/

        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        TopicsFragment topicsFragment = TopicsFragment.newInstance();
        fragmentTransaction.replace(R.id.fragment_login, loginFragment);
        fragmentTransaction.commit();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        AppEventsLogger.deactivateApp(this);
    }
}
