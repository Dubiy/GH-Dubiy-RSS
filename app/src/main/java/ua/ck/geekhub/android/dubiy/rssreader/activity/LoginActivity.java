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
//import com.facebook.internal.ImageDownloader;
import com.facebook.model.GraphObject;
import com.facebook.widget.FacebookDialog;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ua.ck.geekhub.android.dubiy.rssreader.R;
import ua.ck.geekhub.android.dubiy.rssreader.fragment.ArticleFragment;
import ua.ck.geekhub.android.dubiy.rssreader.fragment.LoginFragment;
import ua.ck.geekhub.android.dubiy.rssreader.fragment.TopicsFragment;
import ua.ck.geekhub.android.dubiy.rssreader.utils.Const;
import ua.ck.geekhub.android.dubiy.rssreader.utils.ImageDownloader;

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
    private TextView username;
    private ImageView avatar;


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
        findViewById(R.id.sign_out_button).setOnClickListener(this);
        username = (TextView)findViewById(R.id.tv_username);
        avatar = (ImageView)findViewById(R.id.avatar);
    }

    @Override
    public void onClick(View view) {
        //Const.PARAM_STATUS;
        if (view != null) {
            if (view.getId() == R.id.sign_in_button
                    && !mGoogleApiClient.isConnecting()) {
                mSignInClicked = true;
                resolveSignInError();
            }

            if (view.getId() == R.id.sign_out_button) {
                if (mGoogleApiClient.isConnected()) {
                    Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
                    mGoogleApiClient.disconnect();
                    mGoogleApiClient.connect();

                    findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
                    findViewById(R.id.sign_out_button).setVisibility(View.GONE);

                    View facebookAuth = findViewById(R.id.authButton);
                    if (facebookAuth != null) {
                        facebookAuth.setVisibility(View.VISIBLE);
                    }

                    TextView tv_username = (TextView)findViewById(R.id.tv_username);
                    if (tv_username != null) {
                        tv_username.setText(getResources().getString(R.string.please_login));
                    }

                    ImageView avatar = (ImageView)findViewById(R.id.avatar);
                    if (avatar != null) {
                        avatar.setVisibility(View.GONE);
                    }
                }
            }
        }
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        mSignInClicked = false;
        findViewById(R.id.sign_in_button).setVisibility(View.GONE);
        findViewById(R.id.sign_out_button).setVisibility(View.VISIBLE);

        View facebookAuth = findViewById(R.id.authButton);
        if (facebookAuth != null) {
            facebookAuth.setVisibility(View.GONE);
        }
        sPref = getSharedPreferences(getResources().getString(R.string.shared_prefs_file), MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString("LoggedUser.name", Plus.PeopleApi.getCurrentPerson(mGoogleApiClient).getDisplayName());
        ed.putString("LoggedUser.photo", Plus.PeopleApi.getCurrentPerson(mGoogleApiClient).getImage().getUrl().toString());
        ed.commit();
        showProfile();
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    private void resolveSignInError() {
        if (mConnectionResult.hasResolution()) {
            try {
                mIntentInProgress = true;
                startIntentSenderForResult(mConnectionResult.getResolution().getIntentSender(),
                        RC_SIGN_IN, null, 0, 0, 0);
            } catch (IntentSender.SendIntentException e) {
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
            mConnectionResult = result;

            if (mSignInClicked) {
                resolveSignInError();
            }
        }
    }

    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
        if (state.isOpened()) {
            Log.i(FB_TAG, "Logged in...");

            View googlePlus = findViewById(R.id.sign_in_button);
            if (googlePlus != null) {
                googlePlus.setVisibility(View.GONE);
            }

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

                            sPref = getSharedPreferences(getResources().getString(R.string.shared_prefs_file), MODE_PRIVATE);
                            final SharedPreferences.Editor ed = sPref.edit();
                            ed.putString("LoggedUser.name", jso.getString("name"));
                            ed.putString("LoggedUser.id", jso.getString("id"));
                            ed.putString("LoggedUser.photo", "http://graph.facebook.com/" + jso.getString("id") + "/picture?type=large");

                            final DefaultHttpClient client = new DefaultHttpClient();
                            final HttpGet httpGet = new HttpGet("http://graph.facebook.com/" + jso.getString("id") + "/picture?type=large&redirect=false");
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        HttpResponse execute = client.execute(httpGet);
                                        final String responce = EntityUtils.toString(execute.getEntity());
                                        final JSONObject jsonObject = new JSONObject(responce);
                                        avatar.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                try {
                                                    ed.putString("LoggedUser.photo", jsonObject.getJSONObject("data").getString("url"));
                                                    Log.d("GARY_", jsonObject.getJSONObject("data").getString("url"));
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                                ed.commit();
                                                showProfile();
                                            }
                                        });
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }).start();
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

            View googlePlus = findViewById(R.id.sign_in_button);
            if (googlePlus != null) {
                googlePlus.setVisibility(View.VISIBLE);
            }

            TextView tv_username = (TextView)findViewById(R.id.tv_username);
            if (tv_username != null) {
                tv_username.setText(getResources().getString(R.string.please_login));
            }

            ImageView avatar = (ImageView)findViewById(R.id.avatar);
            if (avatar != null) {
                avatar.setVisibility(View.GONE);
            }
        }
    }

    private void showProfile() {
        sPref = getSharedPreferences(getResources().getString(R.string.shared_prefs_file), MODE_PRIVATE);
        username.setText(sPref.getString("LoggedUser.name", ""));
        new ImageDownloader(avatar).execute(sPref.getString("LoggedUser.photo", ""));

        ImageView avatar = (ImageView)findViewById(R.id.avatar);
        if (avatar != null) {
            avatar.setVisibility(View.VISIBLE);
        }
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
