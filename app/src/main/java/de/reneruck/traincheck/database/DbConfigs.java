package de.reneruck.traincheck.database;

public class DbConfigs {

	public static final String TABLE_TRACKERS = "trackers";
	public static final String FIELD_ID = "id";
    public static final String FIELD_START_TRACKING_AT = "startTrackingAt";
    public static final String FIELD_STOP_TRACKING_AT = "stopTrackingAt";
    public static final String FIELD_PRIMARY_STATION = "primaryStation";
    public static final String FIELD_SECONDARY_STATION = "secondaryStation";
    public static final String FIELD_DIRECTION_PRIMARY = "directionPrimary";
    public static final String FIELD_DIRECTION_SECONDARY = "directionSecondary";
    public static final String FIELD_DAYS = "days";
    public static final String FIELD_ENABLED = "enabled";

    public static String databaseName = "trackers.db";
	public static int databaseVersion = 1;

}
