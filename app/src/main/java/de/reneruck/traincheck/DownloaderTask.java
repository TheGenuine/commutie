package de.reneruck.traincheck;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by reneruck on 13/11/2015.
 * @author reneruck
 * @since 13/11/2015
 */
public class DownloaderTask extends AsyncTask<String, Void, List<StationData>> {
    private static final String DEBUG_TAG = "DownalodTask";

    private Context activityContext;

    public DownloaderTask(Context activityContext) {
        this.activityContext = activityContext;
    }

    @Override
    protected List<StationData> doInBackground(String... endpoints) {

        // params comes from the execute() call: params[0] is the url.
        try {
            return downloadUrl(endpoints[0]);
        } catch (IOException e) {
            return new ArrayList<StationData>();
        }
    }

    private List<StationData> downloadUrl(String endpoint) throws IOException {
        InputStream is = null;

        try {
            URL url = new URL(endpoint);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            int response = conn.getResponseCode();
            Log.d(DEBUG_TAG, "The response is: " + response);
            is = conn.getInputStream();

            List<StationData> stationData = parseResult(is);
            return stationData;

        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    private List<StationData> parseResult(InputStream is) throws IOException {
        List<StationData> results = new ArrayList<StationData>();
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(is, null);
            parser.nextTag();
            return results ; //readFeed(parser);
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            is.close();
        }
        return results;
    }

    // onPostExecute displays the results of the AsyncTask.
    @Override
    protected void onPostExecute(List results) {
        if(results.isEmpty()){
//            "Unable to retrieve web page. URL may be invalid.";
        } else {

        }
//        textView.setText(result);
    }
}
