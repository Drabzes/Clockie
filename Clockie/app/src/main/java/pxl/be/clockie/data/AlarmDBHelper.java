package pxl.be.clockie.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class AlarmDBHelper extends SQLiteOpenHelper {
    private Context context;

    private static final int DATABASE_VERSION = 10;
    static final String DATABASE_NAME = "alarm.db";

    public AlarmDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_ALARM_TABLE = "CREATE TABLE " + AlarmContract.AlarmEntry.TABLE_NAME + " (" +
                AlarmContract.AlarmEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                AlarmContract.AlarmEntry.COLUMN_LABEL + " TEXT NOT NULL, " +
                AlarmContract.AlarmEntry.COLUMN_TIME + " TEXT NOT NULL, " +
                AlarmContract.AlarmEntry.COLUMN_ACTIVE + " INTEGER NOT NULL, " +
                AlarmContract.AlarmEntry.COLUMN_CHECKRAIN + " TEXT, " +
                AlarmContract.AlarmEntry.COLUMN_CITY + " TEXT, " +
                AlarmContract.AlarmEntry.COLUMN_MONDAY + " INTEGER NOT NULL, " +
                AlarmContract.AlarmEntry.COLUMN_TUESDAY + " INTEGER NOT NULL, " +
                AlarmContract.AlarmEntry.COLUMN_WEDNESDAY + " INTEGER NOT NULL, " +
                AlarmContract.AlarmEntry.COLUMN_THURSDAY + " INTEGER NOT NULL, " +
                AlarmContract.AlarmEntry.COLUMN_FRIDAY + " INTEGER NOT NULL, " +
                AlarmContract.AlarmEntry.COLUMN_SATURDAY + " INTEGER NOT NULL, " +
                AlarmContract.AlarmEntry.COLUMN_SUNDAY + " INTEGER NOT NULL, " +
                AlarmContract.AlarmEntry.COLUMN_WEATHER + " TEXT);";
        db.execSQL(SQL_CREATE_ALARM_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//       TO DO: alter table!

        db.execSQL("DROP TABLE IF EXISTS " + AlarmContract.AlarmEntry.TABLE_NAME);
        onCreate(db);
    }
}
