package pxl.be.clockie.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;


public class AlarmContract {

    public static final String CONTENT_AUTHORITY = "pxl.be.clockie";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_ALARMS = "alarm";

    public static final class AlarmEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_ALARMS).build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ALARMS;

        public static Uri buildAlarmUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static final String TABLE_NAME = "alarm";

        public static final String COLUMN_LABEL = "label";
        public static final String COLUMN_TIME = "time";
        public static final String COLUMN_ACTIVE = "active";
        public static final String COLUMN_CHECKRAIN = "rainTime";
        public static final String COLUMN_CITY = "city";
        public static final String COLUMN_MONDAY = "monday";
        public static final String COLUMN_TUESDAY = "tuesday";
        public static final String COLUMN_WEDNESDAY = "wednesday";
        public static final String COLUMN_THURSDAY = "thursday";
        public static final String COLUMN_FRIDAY = "friday";
        public static final String COLUMN_SATURDAY = "saturday";
        public static final String COLUMN_SUNDAY = "sunday";
        public static final String COLUMN_WEATHER = "weather";
    }
}
