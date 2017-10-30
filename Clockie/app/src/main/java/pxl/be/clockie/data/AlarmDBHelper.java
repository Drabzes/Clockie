package pxl.be.clockie.data;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import pxl.be.clockie.data.AlarmContract.AlarmEntry;

public class AlarmDBHelper extends SQLiteOpenHelper {
    private Context context;

    private static final int DATABASE_VERSION = 4;
    static final String DATABASE_NAME = "alarm.db";

    public AlarmDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_ALARM_TABLE = "CREATE TABLE " + AlarmEntry.TABLE_NAME + " (" +
                        AlarmEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        AlarmEntry.COLUMN_LABEL + " TEXT NOT NULL, " +
                        AlarmEntry.COLUMN_TIME + " TEXT NOT NULL, " +
                        AlarmContract.AlarmEntry.COLUMN_ACTIVE + " INTEGER NOT NULL, " +
                        AlarmContract.AlarmEntry.COLUMN_RAINTIME + " TEXT, " +
//                        AlarmContract.AlarmEntry.COLUMN_REPEATDAYS + " INTEGER NOT NULL, " +
                        AlarmContract.AlarmEntry.COLUMN_SNOOZE + " INTEGER NOT NULL, " +
                        AlarmContract.AlarmEntry.COLUMN_SONG + " TEXT);";
        db.execSQL(SQL_CREATE_ALARM_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//       TO DO: alter table!

        db.execSQL("DROP TABLE IF EXISTS " + AlarmEntry.TABLE_NAME);
        onCreate(db);
    }
}
