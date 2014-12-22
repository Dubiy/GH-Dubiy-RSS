package ua.ck.geekhub.android.dubiy.rssreader.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import java.util.Random;

import ua.ck.geekhub.android.dubiy.rssreader.R;
import ua.ck.geekhub.android.dubiy.rssreader.activity.StartActivity;
import ua.ck.geekhub.android.dubiy.rssreader.entity.PostEntity;

/**
 * Created by Gary on 08.12.2014.
 */
public class RefreshPostsService extends Service {
    private final static int mNotificationId = 1;
    private Handler mHandler = new Handler();
    private NotificationManager mNotificationManager;
    private PostEntity lastPostEntity;
    Thread thread;

    @Override
    public void onCreate() {

        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this).
                setSmallIcon(R.drawable.ic_launcher).
                setContentTitle(getText(R.string.app_name)).
                setContentText("service started...");

//                        setVibrate(vibrate);

        Intent resultIntent = new Intent(this, StartActivity.class);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0,resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);

        startForeground(mNotificationId, mBuilder.build());


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final Random random = new Random();
/*
        Log.d("GARY_gdfgdf", "onStartCommand");

        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                while (true) {

                    lastHabraPost = PostHolder.getPost(0);
                    if (lastHabraPost != null) {
                        PostLoader postLoader = new PostLoader(getApplicationContext());
                        postLoader.refresh_posts();
                    }

                    if (PostHolder.getPost(0) != null && lastHabraPost != null) {

                        if ( ! PostHolder.getPost(0).getTitle().equals(lastHabraPost.getTitle())) {
                            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext()).
                                    setSmallIcon(R.drawable.ic_launcher).
                                    setContentTitle(getText(R.string.app_name)).
                                    setContentText("Detected new post!").
                                    setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
                            Intent resultIntent = new Intent(getApplicationContext(), StartActivity.class);
                            PendingIntent resultPendingIntent = PendingIntent.getActivity(getApplicationContext(), 0,resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                            mBuilder.setContentIntent(resultPendingIntent);

                            mNotificationManager.notify(mNotificationId, mBuilder.build());
                        }
                    }

                    try {
                        TimeUnit.SECONDS.sleep(1000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }
            }
        });
        thread.start();
*/
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
        thread.interrupt();
//        thread.
        stopSelf();
        super.onDestroy();
    }
}
