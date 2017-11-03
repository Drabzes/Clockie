package pxl.be.clockie;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import pxl.be.clockie.data.AlarmContract;

public class DetailActivity extends AppCompatActivity implements DetailListener{

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.detail_activity);

        Intent intent = getIntent();
        int position = intent.getIntExtra("position", -1);

        if (position != -1) {
            setFields(position);
        }

        FloatingActionButton deleteButton = (FloatingActionButton) findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView hiddenId = (TextView) findViewById(R.id.hiddenId);

                long id = Long.parseLong(hiddenId.getText().toString());

                ContentResolver contentResolver = getContentResolver();
                Cursor cursor = contentResolver.query(
                        AlarmContract.AlarmEntry.CONTENT_URI,
                        new String[]{AlarmContract.AlarmEntry._ID},
                        AlarmContract.AlarmEntry._ID + " = ?",
                        new String[]{String.valueOf(id)}, null);

                if (cursor.moveToFirst()) {
                    contentResolver.delete(AlarmContract.AlarmEntry.CONTENT_URI, "_id='" + id + "'", null);
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                }
                cursor.close();
            }
        });

    }


    @Override
    public void setFields(int position) {
        DetailFragment detailFragment = (DetailFragment) getSupportFragmentManager().findFragmentById(R.id.detailFragment);
        detailFragment.setAllFields(position);
    }
}
