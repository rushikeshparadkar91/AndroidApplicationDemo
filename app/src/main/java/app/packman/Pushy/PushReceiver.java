package app.packman.Pushy;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;

import app.packman.R;
import app.packman.activity.HomeActivity;

/**
 * Created by mlshah on 4/2/16.
 */
public class PushReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        String notificationTitle = "Packman";
        String notificationDesc = "No message received";

        // Attempt to grab the message
        // property from the payload
        if ( intent.getStringExtra("message") != null )
        {
            notificationDesc = intent.getStringExtra("message");
        }

        //-----------------------------
        // Create pending intent
        // without a real intent
        //-----------------------------
        Intent notificationIntent=new Intent(context, HomeActivity.class);
        PendingIntent pi=PendingIntent.getActivity(context, 1, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        //-----------------------------
        // Create a test notification
        //-----------------------------

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        Notification notification = builder.setContentIntent(pi)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true).setContentTitle(notificationTitle)
                .setContentText(notificationDesc).build();
        //mNM.notify(NOTIFICATION, notification);
        //Notification notification = new Notification(android.R.drawable.ic_dialog_info, notificationDesc, System.currentTimeMillis());

        //-----------------------------
        // Sound + vibrate + light
        //-----------------------------

        notification.defaults = Notification.DEFAULT_ALL;

        //-----------------------------
        // Get notification manager
        //-----------------------------

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        //-----------------------------
        // Issue the notification
        //-----------------------------

        mNotificationManager.notify(0, notification);
    }
}