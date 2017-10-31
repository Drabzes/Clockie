package pxl.be.clockie;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

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

    }


    @Override
    public void setFields(int position) {
        DetailFragment detailFragment = (DetailFragment) getSupportFragmentManager().findFragmentById(R.id.detailFragment);
        detailFragment.setAllFields(position);
    }
}

