package de.reneruck.traincheck.database;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by reneruck on 16/11/2015.
 *
 * @author Rene Ruck (reneruck@gmail.com)
 * @since 16/11/2015
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String CREATE_TRACKERS = "CREATE  TABLE IF NOT EXISTS `" + DbConfigs.TABLE_TRACKERS+ "`" +
            " (`" + DbConfigs.FIELD_ID + "` INTEGER  PRIMARY KEY AUTOINCREMENT ," +
            "`" + DbConfigs.FIELD_PRIMARY_STATION + "` TEXT NULL ," +
            "`" + DbConfigs.FIELD_DIRECTION_PRIMARY + "` TEXT NULL ," +
            "`" + DbConfigs.FIELD_SECONDARY_STATION + "` TEXT NULL ," +
            "`" + DbConfigs.FIELD_DIRECTION_SECONDARY + "` TEXT NULL ," +
            "`" + DbConfigs.FIELD_START_TRACKING_AT + "` TEXT NULL ," +
            "`" + DbConfigs.FIELD_STOP_TRACKING_AT + "` TEXT NULL ," +
            "`" + DbConfigs.FIELD_DAYS + "` TEXT NULL ," +
            "`" + DbConfigs.FIELD_ENABLED+ "` BOOLEAN NULL)";


    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DbConfigs.databaseName, factory,  DbConfigs.databaseVersion);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(CREATE_TRACKERS);
        } catch (SQLException e) {
            System.err.println(e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
		/*
		 * Migrations should happen a here
		 */
        db.execSQL("DROP TABLE IF EXISTS " + DbConfigs.TABLE_TRACKERS + "");
        onCreate(db);
    }
}
