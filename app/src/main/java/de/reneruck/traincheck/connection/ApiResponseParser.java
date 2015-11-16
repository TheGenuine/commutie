package de.reneruck.traincheck.connection;

import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import de.reneruck.traincheck.models.StationData;

/**
 * Created by reneruck on 14/11/2015.
 *
 * @author reneruck
 * @since 14/11/2015
 */
public class ApiResponseParser {

    private static final String LOG_TAG = "ApiParser";

    public static List<StationData> parseResult(InputStream is) {

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
                            Log.i(LOG_TAG, "traincode: " + readText(parser));
                        }
                        break;
                    case "Destination":
                        if (currentStationData != null) {
                            currentStationData.setDestination(readText(parser));
                            Log.i(LOG_TAG, "Destination: " + readText(parser));
                        }
                        break;
                    case "Duein":
                        if (currentStationData != null) {
                            currentStationData.setDueIn(Integer.parseInt(readText(parser)));
                            Log.i(LOG_TAG, "DueIn: " + readText(parser));
                        }
                        break;
                    case "Late":
                        if (currentStationData != null) {
                            currentStationData.setLate(Integer.parseInt(readText(parser)));
                            Log.i(LOG_TAG, "Late: " + readText(parser));
                        }
                        break;
                    case "Expdepart":
                        if (currentStationData != null) {
                            currentStationData.setExpDepart(readText(parser));
                            Log.i(LOG_TAG, "Expdepart: " + readText(parser));
                        }
                        break;
                    case "Schdepart":
                        if (currentStationData != null) {
                            currentStationData.setSchDepart(readText(parser));
                            Log.i(LOG_TAG, "Schdepart: " + readText(parser));
                        }
                        break;
                }
            }
            return results; //readFeed(parser);
        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return results;
    }

    private static String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }
}
