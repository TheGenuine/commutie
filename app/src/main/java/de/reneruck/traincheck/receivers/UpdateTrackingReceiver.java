package de.reneruck.traincheck.receivers;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import de.reneruck.traincheck.DownloaderTask;

/**
 * Documentation to follow.
 *
 * @author Rene Ruck (reneruck@gmail.com)
 * @since 14/11/2015
 */
public class UpdateTrackingReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        boolean disable = intent.getBooleanExtra("disabled", false);
        ConnectivityManager mConnMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = mConnMgr.getActiveNetworkInfo();
        if (!disable && networkInfo != null && networkInfo.isConnected() && networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            new DownloaderTask(context).execute();

//            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//            Intent serviceIntent = new Intent(context, UpdateTrackingReceiver.class);
//            PendingIntent pendingIntent= PendingIntent.getBroadcast(context, 1337, serviceIntent , PendingIntent.FLAG_UPDATE_CURRENT);
//            alarmManager.set(AlarmManager.RTC_WAKEUP, 3600, pendingIntent);
        }
    }
}
