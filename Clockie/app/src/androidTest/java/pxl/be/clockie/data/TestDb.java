/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package pxl.be.clockie.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import pxl.be.clockie.Alarm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class TestDb{

    Context appContext = InstrumentationRegistry.getTargetContext();

    public static final String LOG_TAG = TestDb.class.getSimpleName();

    void deleteDatabase() {
        appContext.deleteDatabase(AlarmDBHelper.DATABASE_NAME);
    }

    public void setUp() {
        deleteDatabase();
    }

    @Test
    public void testCreateDb() throws Throwable {
        final HashSet<String> tableNameHashSet = new HashSet<>();
        tableNameHashSet.add(AlarmContract.AlarmEntry.TABLE_NAME);

        SQLiteDatabase db = new AlarmDBHelper(
                this.appContext).getWritableDatabase();
        assertEquals(true, db.isOpen());

        // have we created the tables we want?
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        assertTrue("Error: This means that the database has not been created correctly",
                c.moveToFirst());

        // verify that the tables have been created
        do {
            tableNameHashSet.remove(c.getString(0));
        } while( c.moveToNext() );

        // if this fails, it means that your database doesn't contain both the location entry
        // and weather entry tables
        assertTrue("Error: Your database was created without both the location entry and weather entry tables",
                tableNameHashSet.isEmpty());

        // now, do our tables contain the correct columns?
        c = db.rawQuery("PRAGMA table_info(" + AlarmContract.AlarmEntry.TABLE_NAME + ")",
                null);

        assertTrue("Error: This means that we were unable to query the database for table information.",
                c.moveToFirst());

        // Build a HashSet of all of the column names we want to look for
        final HashSet<String> columnHashSet = new HashSet<>();
        columnHashSet.add(AlarmContract.AlarmEntry._ID);
        columnHashSet.add(AlarmContract.AlarmEntry.COLUMN_LABEL);
        columnHashSet.add(AlarmContract.AlarmEntry.COLUMN_TIME);
        columnHashSet.add(AlarmContract.AlarmEntry.COLUMN_RAINTIME);
        columnHashSet.add(AlarmContract.AlarmEntry.COLUMN_SONG);

        int columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            columnHashSet.remove(columnName);
        } while(c.moveToNext());

        // if this fails, it means that your database doesn't contain all of the required location
        // entry columns
        assertTrue("Error: The database doesn't contain all of the required location entry columns",
                columnHashSet.isEmpty());
        db.close();
    }

    @Test
    public void testAlarmTable() {
        SQLiteDatabase db = new AlarmDBHelper(this.appContext).getWritableDatabase();

        ContentValues testValues = new ContentValues();
        testValues.put(AlarmContract.AlarmEntry.COLUMN_LABEL, "Test alarm");
        testValues.put(AlarmContract.AlarmEntry.COLUMN_TIME, "11:10");
        testValues.put(AlarmContract.AlarmEntry.COLUMN_RAINTIME, "11:20");
        testValues.put(AlarmContract.AlarmEntry.COLUMN_SONG, "song");

        long rowId = db.insert(AlarmContract.AlarmEntry.TABLE_NAME, null, testValues);

        assertTrue("Error: Insert failed", rowId != -1);

        Cursor cursor = db.query(
                AlarmContract.AlarmEntry.TABLE_NAME,
                null, null, null, null, null, null
        );

        assertTrue("Error: no records returned from alarm query", cursor.moveToFirst());

        Set<Map.Entry<String, Object>> valueSet = testValues.valueSet();
        String error = "Error: alarm query validation failed";
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = cursor.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not found. " + error, idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals("Value '" + entry.getValue().toString() +
                    "' did not match the expected value '" +
                    expectedValue + "'. " + error, expectedValue, cursor.getString(idx));
        }

        //only one record so moveToNext should fail
        assertFalse("Error: more than one record is returned", cursor.moveToNext());

        cursor.close();
        db.close();
    }
}
