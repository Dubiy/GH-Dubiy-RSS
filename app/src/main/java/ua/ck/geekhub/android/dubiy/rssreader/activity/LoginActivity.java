package ua.ck.geekhub.android.dubiy.rssreader.activity;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AppEventsLogger;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphObject;
import com.facebook.widget.FacebookDialog;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;

import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import ua.ck.geekhub.android.dubiy.rssreader.R;
import ua.ck.geekhub.android.dubiy.rssreader.fragment.ArticleFragment;
import ua.ck.geekhub.android.dubiy.rssreader.fragment.LoginFragment;
import ua.ck.geekhub.android.dubiy.rssreader.fragment.TopicsFragment;

/**
 * Created by Gary on 06.01.2015.
 */
public class LoginActivity extends FragmentActivity implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    protected UiLifecycleHelper mUiHelper;
    private static final String FB_TAG = "Facebook_TAG";
    private SharedPreferences sPref;
    private boolean mSignInClicked;
    private ConnectionResult mConnectionResult;
    private GoogleApiClient mGoogleApiClient;
    private boolean mIntentInProgress;
    private static final int RC_SIGN_IN = 0;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_login);
        mUiHelper = new UiLifecycleHelper(this, callback);
        mUiHelper.onCreate(savedInstanceState);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .build();

        findViewById(R.id.sign_in_button).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view != null) {
            if (view.getId() == R.id.sign_in_button
                    && !mGoogleApiClient.isConnecting()) {
                mSignInClicked = true;
                resolveSignInError();
            }
        }
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        mSignInClicked = false;
        Toast.makeText(this, "User is connected!", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    private void resolveSignInError() {
        if (mConnectionResult.hasResolution()) {
            try {
                mIntentInProgress = true;
                startIntentSenderForResult(mConnectionResult.getResolution().getIntentSender(),
                        RC_SIGN_IN, null, 0, 0, 0);
            } catch (IntentSender.SendIntentException e) {
                // The intent was canceled before it was sent.  Return to the default
                // state and attempt to connect to get an updated ConnectionResult.
                mIntentInProgress = false;
                mGoogleApiClient.connect();
            }
        }
    }

    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    protected void onStop() {
        super.onStop();

        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    public void onConnectionFailed(ConnectionResult result) {
        if (!mIntentInProgress) {
            // Store the ConnectionResult so that we can use it later when the user clicks
            // 'sign-in'.
            mConnectionResult = result;

            if (mSignInClicked) {
                // The user has already clicked 'sign-in' so we attempt to resolve all
                // errors until the user is signed in, or they cancel.
                resolveSignInError();
            }
        }
    }





    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
        if (state.isOpened()) {
            Log.i(FB_TAG, "Logged in...");

            new Request(
                    session,
                    "/v1.0/me",
                    null,
                    HttpMethod.GET,
                    new Request.Callback() {
                        public void onCompleted(Response response) {
                            try
                            {
                                GraphObject go  = response.getGraphObject();
                                JSONObject jso = go.getInnerJSONObject();
                                Log.d("GARY_", jso.getString("id"));
                                Log.d("GARY_", jso.getString("name"));
                                Log.d("GARY_", "http://graph.facebook.com/781960851853823/picture?type=large");
                                sPref = getSharedPreferences(getResources().getString(R.string.shared_prefs_file), MODE_PRIVATE);
                                SharedPreferences.Editor ed = sPref.edit();
                                ed.putString("LoggedUser.name", jso.getString("name"));
                                ed.putString("LoggedUser.id", jso.getString("id"));
                                ed.putString("LoggedUser.photo", "http://graph.facebook.com/" + jso.getString("id") + "/picture?type=large");
                                ed.commit();
                                showProfile();
                            }
                            catch ( Throwable t )
                            {
                                t.printStackTrace();
                            }
                        }
                    }
            ).executeAsync();

        } else if (state.isClosed()) {
            Log.i(FB_TAG, "Logged out...");
        }
    }

    private void showProfile() {
        sPref = getSharedPreferences(getResources().getString(R.string.shared_prefs_file), MODE_PRIVATE);
        ((TextView)findViewById(R.id.tv_username)).setText(sPref.getString("LoggedUser.name", ""));



        ((ImageView)findViewById(R.id.avatar)).setImageURI(Uri.parse(sPref.getString("LoggedUser.photo", "")));

    }

    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            if (resultCode != RESULT_OK) {
                mSignInClicked = false;
            }

            mIntentInProgress = false;

            if (!mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
            }
        }

        mUiHelper.onActivityResult(requestCode, resultCode, data, new FacebookDialog.Callback() {
            @Override
            public void onError(FacebookDialog.PendingCall pendingCall, Exception error, Bundle data) {
                Log.e(FB_TAG, String.format("Error: %s", error.toString()));
                Toast.makeText(getApplicationContext(), getString(R.string.shared_error_message), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onComplete(FacebookDialog.PendingCall pendingCall, Bundle data) {
                Log.e(FB_TAG, "Success!");
                Toast.makeText(getApplicationContext(), getString(R.string.shared_success_message), Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        mUiHelper.onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mUiHelper.onResume();
    }


    @Override
    public void onPause() {
        super.onPause();
        mUiHelper.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mUiHelper.onDestroy();
    }
}
