package de.reneruck.traincheck;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.collections4.Predicate;

import java.util.List;

import de.reneruck.traincheck.connection.ApiRequest;
import de.reneruck.traincheck.connection.ApiResponseParser;
import de.reneruck.traincheck.models.StationData;
import de.reneruck.traincheck.models.Tracker;
import de.reneruck.traincheck.receivers.UpdateTrackingReceiver;

/**
 * Created by reneruck on 13/11/2015.
 * @author reneruck
 * @since 13/11/2015
 */
public class DownloaderTask extends AsyncTask<Integer, Void, List<StationData>> {
    private static final String DEBUG_TAG = "DownloadTask";
    private final NotificationManager mNotificationManager;
    private Tracker mTracker;

    private Context mContext;

    public DownloaderTask(Context context) {
        mContext = context;
        mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Override
    protected List<StationData> doInBackground(Integer ... trackerIds) {
        mTracker = loadTracker(trackerIds[0]);
        String station = mTracker.getPrimaryStation().replace(" ", "%20");
        List<StationData> stationDataList = ApiResponseParser.parseResult(new ApiRequest("http://api.irishrail.ie/realtime/realtime.asmx/getStationDataByNameXML?StationDesc=" + station + "&NumMins=60").execute());
        return selectMatchingTrains(stationDataList);
    }

    private Tracker loadTracker(int trackerId) {
        return Tracker.find(mContext, trackerId);
    }

    private List<StationData> selectMatchingTrains(List<StationData> stationDataList) {
        Predicate<StationData> selector = new Predicate<StationData>() {
            @Override
            public boolean evaluate(StationData entry) {
                return entry.getDestination().equals(mTracker.getDirectionPrimary()) | entry.getDirection().equals(mTracker.getDirectionPrimary());
            }
        };
        return ListUtils.select(stationDataList, selector);
    }

    @Override
    protected void onPostExecute(List<StationData> stationData) {
        StationData trainOne = stationData.get(0);

        if(trainOne != null) {
            String firstTrain = trainOne.getDestination() + " - " + trainOne.getSchDepart() + "(" + formatDelay(trainOne.getLate()) + ") In: " + trainOne.getDueIn() + "min";
            String secondTrain = "";
            if(stationData.size() > 1){
                StationData trainTwo = stationData.get(1);
                secondTrain = trainTwo.getDestination() + " - " + trainTwo.getSchDepart() + "(" + formatDelay(trainTwo.getLate()) + ") In: " + trainTwo.getDueIn() + "min";
            }
            int notificationCode = Integer.valueOf(trainOne.getTrainCode().substring(1, trainOne.getTrainCode().length()).trim());

            Notification notification = new Notification.Builder(mContext)
                    .setSmallIcon(R.drawable.ic_stat_train)  // the status icon
                    .setContentTitle(firstTrain)
                    .setTicker(firstTrain)  // the status text
                    .setWhen(System.currentTimeMillis())  // the time stamp
                    .setContentText(secondTrain)  // the contents of the entry
                    .setDeleteIntent(makeOnDeleteIntent(notificationCode, mTracker.getTrackerId()))  // The intent to send when the entry is clicked
                    .build();

            // Send the notification.
            mNotificationManager.notify(notificationCode, notification);
        }
    }

    private PendingIntent makeOnDeleteIntent(int notificationCode, int trackerId) {
        Intent deleteIntent = new Intent(mContext, UpdateTrackingReceiver.class);
        deleteIntent.putExtra("disabled", true);
        deleteIntent.putExtra("trackerId", trackerId);
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
