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
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ALARMS;

        public static Uri buildAlarmUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static final String TABLE_NAME = "alarm";

        public static final String COLUMN_LABEL = "label";
        public static final String COLUMN_TIME = "time";
        public static final String COLUMN_ACTIVE = "active";
        public static final String COLUMN_RAINTIME = "rainTime";
        public static final String COLUMN_SONG = "song";
        public static final String COLUMN_SNOOZE = "snooze";
//        public static final String COLUMN_REPEATDAYS = "repeatDays";


    }
}
