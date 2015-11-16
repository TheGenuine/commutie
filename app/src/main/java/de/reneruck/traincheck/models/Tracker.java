package de.reneruck.traincheck.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.apache.commons.lang3.BooleanUtils;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import de.reneruck.traincheck.database.DatabaseHelper;
import de.reneruck.traincheck.database.DbConfigs;

/**
 * Created by reneruck on 14/11/2015.
 *
 * @author Rene Ruck (reneruck@gmail.com)
 * @since 14/11/2015
 */
public class Tracker {

    private int trackerId = -1;
    private boolean[] days = new boolean[7];
    private String startTrackingAt = "00:00";
    private String stopTrackingAt = "00:00";
    private String primaryStation = "Adamstown";
    private String secondaryStation = "Adamstown";
    private String directionPrimary = "Northbound";
    private String directionSecondary = "Northbound";
    private boolean enabled = false;

    public Tracker() {
    }

    public Tracker(int trackerId, String startTrackingAt, String stopTrackingAt, String primaryStation, String directionPrimary, String secondaryStation,  String directionSecondary, boolean[] days, boolean enabled) {
        this.trackerId = trackerId;
        this.startTrackingAt = startTrackingAt;
        this.stopTrackingAt = stopTrackingAt;
        this.primaryStation = primaryStation;
        this.secondaryStation = secondaryStation;
        this.directionPrimary = directionPrimary;
        this.directionSecondary = directionSecondary;
        this.days = days;
        this.enabled = enabled;
    }

    public String getStartTrackingAt() {
        return startTrackingAt;
    }

    public String getStopTrackingAt() {
        return stopTrackingAt;
    }

    public String getPrimaryStation() {
        return primaryStation;
    }

    public String getSecondaryStation() {
        return secondaryStation;
    }

    public String getDirectionPrimary() {
        return directionPrimary;
    }

    public boolean[] getDays() {
        return days;
    }

    public boolean getDay(int index) {
        return this.days[index];
    }

    public String getDirectionSecondary() {
        return directionSecondary;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setStartTrackingAt(String startTrackingAt) {
        this.startTrackingAt = startTrackingAt;
    }

    public void setStopTrackingAt(String stopTrackingAt) {
        this.stopTrackingAt = stopTrackingAt;
    }

    public void setPrimaryStation(String primaryStation) {
        this.primaryStation = primaryStation;
    }

    public void setSecondaryStation(String secondaryStation) {
        this.secondaryStation = secondaryStation;
    }

    public void setDirectionPrimary(String directionPrimary) {
        this.directionPrimary = directionPrimary;
    }

    public void setDirectionSecondary(String directionSecondary) {
        this.directionSecondary = directionSecondary;
    }

    public void setDays(boolean[] days) {
        this.days = days;
    }

    public void setDay(int index, boolean status) {
        this.days[index] = status;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    public int getTrackerId() {
        return trackerId;
    }

    public void setTrackerId(int trackerId) {
        this.trackerId = trackerId;
    }


    public static List<Tracker> find(Context context, String query){
        DatabaseHelper databaseHelper = new DatabaseHelper(context, DbConfigs.databaseName, null, DbConfigs.databaseVersion);
        SQLiteDatabase readableDatabase = databaseHelper.getReadableDatabase();
        Cursor results = readableDatabase.query(true, DbConfigs.TABLE_TRACKERS, new String[]{"*"}, query, null, null, null, null, null);
        LinkedList trackers = new LinkedList<>();
        while (results.moveToNext()) {
            trackers.add(new Tracker(
                    results.getInt(results.getColumnIndex(DbConfigs.FIELD_ID)),
                    results.getString(results.getColumnIndex(DbConfigs.FIELD_START_TRACKING_AT)),
                    results.getString(results.getColumnIndex(DbConfigs.FIELD_STOP_TRACKING_AT)),
                    results.getString(results.getColumnIndex(DbConfigs.FIELD_PRIMARY_STATION)),
                    results.getString(results.getColumnIndex(DbConfigs.FIELD_DIRECTION_PRIMARY)),
                    results.getString(results.getColumnIndex(DbConfigs.FIELD_SECONDARY_STATION)),
                    results.getString(results.getColumnIndex(DbConfigs.FIELD_DIRECTION_SECONDARY)),
                    parseBooleanArray(results.getString(results.getColumnIndex(DbConfigs.FIELD_DAYS)).split(",")),
                    results.getInt(results.getColumnIndex(DbConfigs.FIELD_ENABLED)) > 1
            ));
        }
        return trackers;
    }

    public static Tracker find(Context context, int trackerId) {
        DatabaseHelper databaseHelper = new DatabaseHelper(context, DbConfigs.databaseName, null, DbConfigs.databaseVersion);
        SQLiteDatabase readableDatabase = databaseHelper.getReadableDatabase();
        Cursor query = readableDatabase.query(true, DbConfigs.TABLE_TRACKERS, new String[]{"*"}, "id = %d", new String[]{String.valueOf(trackerId)}, null, null, null, "1");
        query.moveToFirst();

        return new Tracker(
                query.getInt(query.getColumnIndex(DbConfigs.FIELD_ID)),
                query.getString(query.getColumnIndex(DbConfigs.FIELD_START_TRACKING_AT)),
                query.getString(query.getColumnIndex(DbConfigs.FIELD_STOP_TRACKING_AT)),
                query.getString(query.getColumnIndex(DbConfigs.FIELD_PRIMARY_STATION)),
                query.getString(query.getColumnIndex(DbConfigs.FIELD_DIRECTION_PRIMARY)),
                query.getString(query.getColumnIndex(DbConfigs.FIELD_SECONDARY_STATION)),
                query.getString(query.getColumnIndex(DbConfigs.FIELD_DIRECTION_SECONDARY)),
                parseBooleanArray(query.getString(query.getColumnIndex(DbConfigs.FIELD_DAYS)).split(",")),
                query.getInt(query.getColumnIndex(DbConfigs.FIELD_ENABLED)) > 1
        );
    }

    private static boolean[] parseBooleanArray(String[] split) {
        boolean[] result = new boolean[split.length];
        for (int i = 0; i < split.length ; i++) {
            result[i] = BooleanUtils.toBoolean(split[i]);
        }
        return result;
    }


    public boolean save(Context context) {
        DatabaseHelper databaseHelper = new DatabaseHelper(context, DbConfigs.databaseName, null, DbConfigs.databaseVersion);
        SQLiteDatabase writableDatabase = databaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        if(trackerId != -1) {
            values.put(DbConfigs.FIELD_ID, trackerId);
        }
        values.put(DbConfigs.FIELD_DAYS, Arrays.toString(days).substring(1, Arrays.toString(days).length() - 1));
        values.put(DbConfigs.FIELD_DIRECTION_PRIMARY, directionPrimary);
        values.put(DbConfigs.FIELD_DIRECTION_SECONDARY, directionPrimary);
        values.put(DbConfigs.FIELD_PRIMARY_STATION, primaryStation);
        values.put(DbConfigs.FIELD_SECONDARY_STATION, secondaryStation);
        values.put(DbConfigs.FIELD_START_TRACKING_AT, startTrackingAt);
        values.put(DbConfigs.FIELD_STOP_TRACKING_AT, stopTrackingAt);
        values.put(DbConfigs.FIELD_ENABLED, enabled);

        if(trackerId != -1) {
            return writableDatabase.update(DbConfigs.TABLE_TRACKERS, values, "id=?", new String[]{String.valueOf(trackerId)}) > 0;
        } else {
            trackerId = (int) writableDatabase.insertOrThrow(DbConfigs.TABLE_TRACKERS, null, values);
            return trackerId != -1;
        }
    }


    public String getStartTrackingAtFormatted() {
        return String.format("%02d", Integer.valueOf(startTrackingAt.split(":")[0])) + ":" +  String.format("%02d", Integer.valueOf(startTrackingAt.split(":")[1]));
    }

    public String getStopTrackingAtFormatted() {
        String[] split = stopTrackingAt.split(":");
        return String.format("%02d", Integer.valueOf(split[0])) + ":" +  String.format("%02d", Integer.valueOf(split[1]));
    }
}
