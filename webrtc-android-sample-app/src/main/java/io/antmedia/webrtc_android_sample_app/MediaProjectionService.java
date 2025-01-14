package io.antmedia.webrtc_android_sample_app;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.graphics.drawable.Icon;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.RequiresApi;

public class MediaProjectionService extends Service {

    private static MediaProjectionServiceListener listener;

    public interface MediaProjectionServiceListener{
        void mediaProjectionOnSuccess(MediaProjection mediaProjection);
    }

    public static void setListener(MediaProjectionServiceListener listener) {
        MediaProjectionService.listener = listener;
    }

    private static final String CHANNEL_ID = "WebRTC";
    private static final int NOTIFICATION_ID = 1;
    public static final String ACTION_STOP = "STOP";
    public static final String EXTRA_MEDIA_PROJECTION_DATA = "mediaProjectionData";

    private MediaProjectionManager mediaProjectionManager;
    public MediaProjection mediaProjection;

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onCreate() {
        super.onCreate();

        Notification notification = createNotification();

        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, getString(io.antmedia.webrtcandroidframework.R.string.app_name), NotificationManager.IMPORTANCE_NONE);
        getNotificationManager().createNotificationChannel(channel);

        startForeground(NOTIFICATION_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PROJECTION);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        
        if (intent != null) {
            String action = intent.getAction();
            if (ACTION_STOP.equals(action)) {
                stopSelf();
                return START_NOT_STICKY;
            }

            Intent data = intent.getParcelableExtra(EXTRA_MEDIA_PROJECTION_DATA);
            mediaProjectionManager = (MediaProjectionManager) getApplicationContext().getSystemService(MEDIA_PROJECTION_SERVICE);
            mediaProjection = mediaProjectionManager.getMediaProjection(Activity.RESULT_OK, data);

            listener.mediaProjectionOnSuccess(mediaProjection);
        }
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private Notification createNotification() {
        Notification.Builder notificationBuilder = new Notification.Builder(this, CHANNEL_ID);
        notificationBuilder.setContentTitle("Ant Media Server WebRTC Publish");
        notificationBuilder.setSmallIcon(R.drawable.ic_launcher);
        notificationBuilder.addAction(createStopAction());
        return notificationBuilder.build();
    }


    private Intent createStopIntent() {
        Intent intent = new Intent(this, MediaProjectionService.class);
        intent.setAction(ACTION_STOP);
        return intent;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private Notification.Action createStopAction() {
        Intent stopIntent = createStopIntent();
        PendingIntent stopPendingIntent;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            stopPendingIntent = PendingIntent.getForegroundService(this, 0, stopIntent, PendingIntent.FLAG_MUTABLE);
        } else {
            stopPendingIntent = PendingIntent.getService(this, 0, stopIntent, PendingIntent.FLAG_ONE_SHOT);
        }
        Icon stopIcon = Icon.createWithResource(this, io.antmedia.webrtcandroidframework.R.drawable.abc_vector_test);
        String stopString = "stop";
        Notification.Action.Builder actionBuilder = new Notification.Action.Builder(stopIcon, stopString, stopPendingIntent);
        return actionBuilder.build();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(true);
    }

    private NotificationManager getNotificationManager() {
        return (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    }
}