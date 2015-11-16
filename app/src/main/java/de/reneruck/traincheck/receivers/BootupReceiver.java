package de.reneruck.traincheck.receivers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by reneruck on 09/11/2015.
 */
public class BootupReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        AlarmManager alarmService = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent serviceIntent = new Intent(context, UpdateTrackingReceiver.class);
        PendingIntent pendingIntent= PendingIntent.getBroadcast(context, 1337, serviceIntent , PendingIntent.FLAG_UPDATE_CURRENT);

//        Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
//        cal.set(year + 1900, month, day, hour, minute, second);
//        cal.getTime().getTime();

        long twentyFourHours = 86400000;
        long nextAlarm = System.currentTimeMillis();

        alarmService.setRepeating(AlarmManager.RTC_WAKEUP, nextAlarm, twentyFourHours, pendingIntent);
        alarmService.set(AlarmManager.RTC_WAKEUP, 30000, pendingIntent);
    }
}
