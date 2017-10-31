package pxl.be.clockie;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import butterknife.ButterKnife;
import pxl.be.clockie.data.AlarmDBHelper;

public class MainActivity extends AppCompatActivity implements DetailListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ButterKnife.bind(this);

        Button stopAlarmButton = (Button) findViewById(R.id.stopAlarm);
        stopAlarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
                intent.putExtra("alarmIsOn", false);
                sendBroadcast(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void addAlarmButtonClicked(View view) {
        Intent intent = new Intent(this, AddAlarmActivity.class);
        startActivity(intent);
    }


    @Override
    public void setFields(int position) {
        if (findViewById(R.id.detailFragment) != null) {
            DetailFragment detailFragment = (DetailFragment) getSupportFragmentManager().findFragmentById(R.id.detailFragment);
            detailFragment.setAllFields(position);
        } else {
            Intent intent = new Intent(this, DetailActivity.class);
            intent.putExtra("position", position);
            startActivity(intent);
        }
    }
}
