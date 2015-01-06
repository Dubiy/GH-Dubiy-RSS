package ua.ck.geekhub.android.dubiy.rssreader.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import ua.ck.geekhub.android.dubiy.rssreader.R;
import ua.ck.geekhub.android.dubiy.rssreader.activity.StartActivity;
import ua.ck.geekhub.android.dubiy.rssreader.entity.PostEntity;
import ua.ck.geekhub.android.dubiy.rssreader.utils.Const;
import ua.ck.geekhub.android.dubiy.rssreader.utils.PostLoader;

/**
 * Created by Gary on 08.12.2014.
 */
public class RefreshPostsService extends Service {
    private final static int mNotificationId = 1;
    private Handler mHandler = new Handler();
    private NotificationManager mNotificationManager;
    private PostEntity lastPostEntity;
    protected final String LOG_TAG = "GARY_" + getClass().getSimpleName();
    private Thread thread;
    private NotificationCompat.Builder mBuilder;
    private PendingIntent resultPendingIntent;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat ("hh:mm:ss");

    @Override
    public void onCreate() {
        Log.d(LOG_TAG, "onCreate");
    }





    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(LOG_TAG, "onStartCommand. testValue = " + intent.getStringExtra("testValue"));

        if (thread != null) {
            thread.interrupt();
        }
        MyRun myRun = new MyRun(startId);
        thread = new Thread(myRun);
        thread.start();

        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(this).
                setSmallIcon(R.drawable.ic_launcher).
                setContentTitle(getText(R.string.app_name)).
                setContentText("service started...");
        Intent resultIntent = new Intent(this, StartActivity.class);



        resultPendingIntent = PendingIntent.getActivity(this, 0,resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        startForeground(mNotificationId, mBuilder.build());

        return START_REDELIVER_INTENT;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        Log.d(LOG_TAG, "onDestroy");
        Toast.makeText(this, "service onDestroy", Toast.LENGTH_SHORT).show();
        thread.interrupt();
        stopSelf();
        super.onDestroy();
    }


    class MyRun implements Runnable {
        int startId;

        public MyRun(int startId) {
            this.startId = startId;
        }

        public void run() {
            int newPostsCount = 0;
            Intent intent = new Intent(Const.BROADCAST_ACTION);
//            intent.putExtra(Const.PARAM_STATUS, 123);
//            sendBroadcast(intent);

            Looper.prepare();
            while (true) {
                try {
//                    intent.putExtra(Const.PARAM_STATUS, 129);
//                    sendBroadcast(intent);

                    //do some stuff

                    PostLoader postLoader = new PostLoader(getApplicationContext());
                    newPostsCount = postLoader.refresh_posts();

                    if (newPostsCount > 0) {

                        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext()).
                                setSmallIcon(R.drawable.ic_launcher).
                                setContentTitle(getText(R.string.app_name)).
                                setContentText("Loaded new posts!").
                                setNumber(newPostsCount).
                                setAutoCancel(true);
                        builder.setContentIntent(resultPendingIntent);
                        mNotificationManager.notify(mNotificationId + 1, builder.build());
                    }

                    mBuilder.setContentText("Last check: " + simpleDateFormat.format(new Date()));
                    startForeground(mNotificationId, mBuilder.build());

                    TimeUnit.SECONDS.sleep(Const.FEED_REFRESH_INTERVAL);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }
            }
            stop();
        }

        void stop() {
            stopSelfResult(startId);
        }
    }
}
