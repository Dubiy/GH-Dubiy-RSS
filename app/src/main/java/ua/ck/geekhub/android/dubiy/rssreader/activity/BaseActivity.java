package ua.ck.geekhub.android.dubiy.rssreader.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import ua.ck.geekhub.android.dubiy.rssreader.R;

/**
 * Created by Gary on 29.11.2014.
 */
public class BaseActivity extends Activity {
    protected final String LOG_TAG = "GARY_" + getClass().getSimpleName();

    public void aboutAuthor() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.app_name)
                .setMessage("Created by Igor Dubiy, 2014")
                .setNegativeButton(R.string.goto_site, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getResources().getString(R.string.author_site)));
                        if (intent.resolveActivity(getPackageManager()) != null) {
                            startActivity(intent);
                        } else {
                            Toast.makeText(getApplicationContext(), "App not available", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //ok, just close
                    }
                })
                .setIcon(R.drawable.ic_launcher)
                .show();
    }



}
