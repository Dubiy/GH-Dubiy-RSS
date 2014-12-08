package ua.ck.geekhub.android.dubiy.rssreader.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import ua.ck.geekhub.android.dubiy.rssreader.R;
import ua.ck.geekhub.android.dubiy.rssreader.activity.ArticleActivity;

/**
 * Created by Gary on 08.12.2014.
 */
public class RefreshPostsService extends Service {

    private Handler mHandler = new Handler();

    private Runnable periodicTask = new Runnable() {
        @Override
        public void run() {
            Toast.makeText(getApplicationContext(), "periodicTask", Toast.LENGTH_LONG).show();
            mHandler.postDelayed(periodicTask, 10000);
        }
    };

    @Override
    public void onCreate() {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this).
                setSmallIcon(R.drawable.ic_launcher).
                setContentTitle(getText(R.string.app_name)).
                setContentText("service started...");

        mHandler.postDelayed(periodicTask, 10000);

//                        setVibrate(vibrate);

        Intent resultIntent = new Intent(this, ArticleActivity.class);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0,resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);

        int mNotificationId = 1;
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotificationManager.notify(mNotificationId, mBuilder.build());
        startForeground(mNotificationId, mBuilder.build());


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {



        // If we get killed, after returning from here, restart
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "service onDestroy", Toast.LENGTH_SHORT).show();
        mHandler.removeCallbacks(periodicTask);
        super.onDestroy();
    }
}
