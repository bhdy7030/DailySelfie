package labs.course.dailyselfie;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by yuan on 8/22/15.
 */
public class AlarmNotificationReceiver extends BroadcastReceiver {
    private static final int MY_NOTIFICATION_ID = 1;

    private final CharSequence tickerText = "Time for another selfie";
    private final CharSequence contentTitle = "Daily Selfie";
    private final CharSequence contentText = "Time for another selfie";

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent mNotificationIntent = new Intent(context, MainActivity.class);

        PendingIntent mContentIntent = PendingIntent.getActivity(context, 0,
                mNotificationIntent, Intent.FLAG_ACTIVITY_NEW_TASK);

        Notification.Builder notificationBuilder = new Notification.Builder(
                context).setTicker(tickerText)
                .setSmallIcon(android.R.drawable.stat_sys_warning)
                .setAutoCancel(true).setContentTitle(contentTitle)
                .setContentText(contentText).setContentIntent(mContentIntent);

        NotificationManager mNotificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(MY_NOTIFICATION_ID,notificationBuilder.build());
    }
}