package de.reneruck.traincheck.connection;

import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by reneruck on 14/11/2015.
 *
 * @author reneruck
 * @since 14/11/2015
 */
public class ApiRequest {

    private static final String DEBUG_TAG = "ApiRequest";
    private String mEndpoint = "";

    public ApiRequest(String endpoint) {
        this.mEndpoint = endpoint;
    }

    public InputStream execute() {
        InputStream is = null;
        try {
            URL url = new URL(mEndpoint);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000); // in ms
            conn.setConnectTimeout(15000); // in ms
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            int response = conn.getResponseCode();
            Log.d(DEBUG_TAG, "The response is: " + response);
            return conn.getInputStream();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return is;
    }
}
