package de.reneruck.traincheck;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import java.io.InputStream;
import java.util.List;

import de.reneruck.traincheck.connection.ApiRequest;
import de.reneruck.traincheck.connection.ApiResponseParser;
import de.reneruck.traincheck.models.StationData;
import de.reneruck.traincheck.receivers.UpdateTrackingReceiver;

/**
 * Created by reneruck on 13/11/2015.
 * @author reneruck
 * @since 13/11/2015
 */
public class DownloaderTask extends AsyncTask<String, Void, List<StationData>> {
    private static final String DEBUG_TAG = "DownalodTask";
    private final NotificationManager mNotificationManager;

    private Context mContext;

    private String[] stations = {"LDWNE", "ASHTN", ""};

    public DownloaderTask(Context context) {
        mContext = context;
        mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Override
    protected List<StationData> doInBackground(String... endpoints) {
        String station = "ASHTN";
        InputStream is = new ApiRequest("http://api.irishrail.ie/realtime/realtime.asmx/getStationDataByCodeXML?StationCode=" + station).execute();

        List<StationData> stationDataList = ApiResponseParser.parseResult(is);
        return pickTrackedOnes(stationDataList);
    }

    private List<StationData> pickTrackedOnes(List<StationData> stationDataList) {
        return stationDataList.subList(0, 1);
    }

    @Override
    protected void onPostExecute(List<StationData> stationDatas) {
        StationData train = stationDatas.get(0);
        String notificationText = train.getDestination() + " - " + train.getExpDepart() + " (" + formatDelay(train.getLate()) + ")";
        int notificationCode = Integer.valueOf(train.getTrainCode().substring(1, train.getTrainCode().length()));

        Notification notification = new Notification.Builder(mContext)
                .setSmallIcon(R.drawable.ic_stat_train)  // the status icon
                .setContentTitle(notificationText)
                .setTicker(notificationText)  // the status text
                .setWhen(System.currentTimeMillis())  // the time stamp
                .setContentText(notificationText)  // the contents of the entry
                .setDeleteIntent(makeOnDeleteIntent(notificationCode))  // The intent to send when the entry is clicked
        .build();

        // Send the notification.
        mNotificationManager.notify(notificationCode, notification);
    }

    private PendingIntent makeOnDeleteIntent(int notificationCode) {
        Intent deleteIntent = new Intent(mContext, UpdateTrackingReceiver.class);
        deleteIntent.putExtra("disabled", true);
        return PendingIntent.getBroadcast(mContext, notificationCode, deleteIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private String formatDelay(int late) {
        if(late >= 0) {
            return "+" + late;
        } else {
            return String.valueOf(late);
        }
    }
}
