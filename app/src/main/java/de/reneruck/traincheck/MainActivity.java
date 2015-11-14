package de.reneruck.traincheck;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.download_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConnectivityManager connMgr = (ConnectivityManager)
                        getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.isConnected()) {
                    fetchData();
                } else {
                    // display error
                    ((TextView)findViewById(R.id.message)).setText("No Internet Connection.");
                }

            }
        });
    }

    private void fetchData() {
        ((TextView)findViewById(R.id.message)).setText("Connecting...");
        String url  = "http://api.irishrail.ie/realtime/realtime.asmx/getStationDataByNameXML?StationDesc=Ashtown&NumMins=45";
        AsyncTask<String, Void, List<StationData>> execute = new DownloaderTask(getApplicationContext()).execute(url);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class DownloaderTask extends AsyncTask<String, Void, List<StationData>> {
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

                return parseResult(is);

            } finally {
                if (is != null) {
                    is.close();
                }
            }
        }

        private List<StationData> parseResult(InputStream is) throws IOException {
            is = new ByteArrayInputStream(dummyText.getBytes());
            List<StationData> results = new ArrayList<StationData>();
            try {
                XmlPullParser parser = Xml.newPullParser();
                parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                parser.setInput(is, null);
                parser.nextTag();
                parser.require(XmlPullParser.START_TAG, null, "ArrayOfObjStationData");

                StationData currentStationData = null;

                while (parser.next() != XmlPullParser.END_DOCUMENT) {
                    int eventType = parser.getEventType();
                    if (parser.getEventType() != XmlPullParser.START_TAG) {
                        if(parser.getEventType() == XmlPullParser.END_TAG && parser.getName().equals("objStationData")){
                            results.add(currentStationData);
                            currentStationData = null;
                        }else {
                            continue;
                        }
                    }
                    String name = parser.getName();
                    switch (name){
                        case "objStationData":
                            System.out.println("new StationData: " + readText(parser));
                            currentStationData = new StationData();
                            break;
                        case "Traincode":
                            if (currentStationData != null) {
                                currentStationData.setTrainCode(readText(parser));
                                System.out.println("traincode: " + readText(parser));
                            }
                            break;
                        case "Destination":
                            if (currentStationData != null) {
                                currentStationData.setDestination(readText(parser));
                                System.out.println("Destination: " + readText(parser));
                            }
                            break;
                        case "Duein":
                            if (currentStationData != null) {
                                currentStationData.setDueIn(Integer.parseInt(readText(parser)));
                                System.out.println("DueIn: " + readText(parser));
                            }
                            break;
                        case "Late":
                            if (currentStationData != null) {
                                currentStationData.setLate(Integer.parseInt(readText(parser)));
                                System.out.println("Late: " + readText(parser));
                            }
                            break;
                        case "Expdepart":
                            if (currentStationData != null) {
                                currentStationData.setExpDepart(readText(parser));
                                System.out.println("Expdepart: " + readText(parser));
                            }
                            break;
                        case "Schdepart":
                            if (currentStationData != null) {
                                currentStationData.setSchDepart(readText(parser));
                                System.out.println("Schdepart: " + readText(parser));
                            }
                            break;
                        default:
                            continue;
//                            skip(parser);
                    }
                }
                return results; //readFeed(parser);
            } catch (XmlPullParserException | IOException e) {
                e.printStackTrace();
            } finally {
                is.close();
            }
            return results;
        }

        private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                throw new IllegalStateException();
            }
            int depth = 1;
            while (depth != 0) {
                switch (parser.next()) {
                    case XmlPullParser.END_TAG:
                        depth--;
                        break;
                    case XmlPullParser.START_TAG:
                        depth++;
                        break;
                }
            }
        }

        private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
            String result = "";
            if (parser.next() == XmlPullParser.TEXT) {
                result = parser.getText();
                parser.nextTag();
            }
            return result;
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(List<StationData> results) {
            String text = ((TextView) findViewById(R.id.message)).getText().toString();
            String message = "";
            if(results.isEmpty()){
                message = "Unable to retrieve web page. URL may be invalid.";
            } else {
                StringBuilder builder = new StringBuilder("Loading results \n");
                for (StationData data : results) {
                    builder.append(data.toString() + "\n");
                }
                message = builder.toString();
            }
            ((TextView) findViewById(R.id.message)).setText(text + "\n" + message);
        }

        private String dummyText = "<ArrayOfObjStationData xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns=\"http://api.irishrail.ie/realtime/\">\n" +
                "<objStationData>\n" +
                "<Servertime>2015-11-13T20:29:30</Servertime>\n" +
                "<Traincode>D931</Traincode>\n" +
                "<Stationfullname>Ashtown</Stationfullname>\n" +
                "<Stationcode>ASHTN</Stationcode>\n" +
                "<Querytime>20:29:30</Querytime>\n" +
                "<Traindate>13 Nov 2015</Traindate>\n" +
                "<Origin>Dublin Pearse</Origin>\n" +
                "<Destination>Maynooth</Destination>\n" +
                "<Origintime>21:10</Origintime>\n" +
                "<Destinationtime>21:56</Destinationtime>\n" +
                "<Status>No Information</Status>\n" +
                "<Lastlocation/>\n" +
                "<Duein>60</Duein>\n" +
                "<Late>0</Late>\n" +
                "<Exparrival>21:29</Exparrival>\n" +
                "<Expdepart>21:29</Expdepart>\n" +
                "<Scharrival>21:29</Scharrival>\n" +
                "<Schdepart>21:29</Schdepart>\n" +
                "<Direction>Northbound</Direction>\n" +
                "<Traintype>Train</Traintype>\n" +
                "<Locationtype>S</Locationtype>\n" +
                "</objStationData>\n" +
                "<objStationData>\n" +
                "<Servertime>2015-11-13T20:29:30</Servertime>\n" +
                "<Traincode>P669</Traincode>\n" +
                "<Stationfullname>Ashtown</Stationfullname>\n" +
                "<Stationcode>ASHTN</Stationcode>\n" +
                "<Querytime>20:29:30</Querytime>\n" +
                "<Traindate>13 Nov 2015</Traindate>\n" +
                "<Origin>Maynooth</Origin>\n" +
                "<Destination>Dublin Pearse</Destination>\n" +
                "<Origintime>20:05</Origintime>\n" +
                "<Destinationtime>20:50</Destinationtime>\n" +
                "<Status>En Route</Status>\n" +
                "<Lastlocation>Arrived Castleknock</Lastlocation>\n" +
                "<Duein>3</Duein>\n" +
                "<Late>-1</Late>\n" +
                "<Exparrival>20:32</Exparrival>\n" +
                "<Expdepart>20:32</Expdepart>\n" +
                "<Scharrival>20:32</Scharrival>\n" +
                "<Schdepart>20:33</Schdepart>\n" +
                "<Direction>Southbound</Direction>\n" +
                "<Traintype>Train</Traintype>\n" +
                "<Locationtype>S</Locationtype>\n" +
                "</objStationData>\n" +
                "<objStationData>\n" +
                "<Servertime>2015-11-13T20:29:30</Servertime>\n" +
                "<Traincode>P670</Traincode>\n" +
                "<Stationfullname>Ashtown</Stationfullname>\n" +
                "<Stationcode>ASHTN</Stationcode>\n" +
                "<Querytime>20:29:30</Querytime>\n" +
                "<Traindate>13 Nov 2015</Traindate>\n" +
                "<Origin>Maynooth</Origin>\n" +
                "<Destination>Dublin Pearse</Destination>\n" +
                "<Origintime>20:47</Origintime>\n" +
                "<Destinationtime>21:34</Destinationtime>\n" +
                "<Status>No Information</Status>\n" +
                "<Lastlocation/>\n" +
                "<Duein>44</Duein>\n" +
                "<Late>0</Late>\n" +
                "<Exparrival>21:12</Exparrival>\n" +
                "<Expdepart>21:13</Expdepart>\n" +
                "<Scharrival>21:12</Scharrival>\n" +
                "<Schdepart>21:13</Schdepart>\n" +
                "<Direction>Southbound</Direction>\n" +
                "<Traintype>Train</Traintype>\n" +
                "<Locationtype>S</Locationtype>\n" +
                "</objStationData>\n" +
                "<objStationData>\n" +
                "<Servertime>2015-11-13T20:29:30</Servertime>\n" +
                "<Traincode>P671</Traincode>\n" +
                "<Stationfullname>Ashtown</Stationfullname>\n" +
                "<Stationcode>ASHTN</Stationcode>\n" +
                "<Querytime>20:29:30</Querytime>\n" +
                "<Traindate>13 Nov 2015</Traindate>\n" +
                "<Origin>Maynooth</Origin>\n" +
                "<Destination>Dublin Pearse</Destination>\n" +
                "<Origintime>21:05</Origintime>\n" +
                "<Destinationtime>21:49</Destinationtime>\n" +
                "<Status>No Information</Status>\n" +
                "<Lastlocation/>\n" +
                "<Duein>62</Duein>\n" +
                "<Late>0</Late>\n" +
                "<Exparrival>21:30</Exparrival>\n" +
                "<Expdepart>21:31</Expdepart>\n" +
                "<Scharrival>21:30</Scharrival>\n" +
                "<Schdepart>21:31</Schdepart>\n" +
                "<Direction>Southbound</Direction>\n" +
                "<Traintype>Train</Traintype>\n" +
                "<Locationtype>S</Locationtype>\n" +
                "</objStationData>\n" +
                "</ArrayOfObjStationData>";
    }


}
