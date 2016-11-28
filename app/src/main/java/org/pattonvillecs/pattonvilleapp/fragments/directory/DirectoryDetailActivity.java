package org.pattonvillecs.pattonvilleapp.fragments.directory;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import org.pattonvillecs.pattonvilleapp.R;

public class DirectoryDetailActivity extends AppCompatActivity {

    private static int SCHOOL_ID;

    //Intent

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_directory_detail);
        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);*/
        Intent intent = getIntent();
        //make the final constant for the school the extra that was passed
    }

}
