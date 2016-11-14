package org.pattonvillecs.pattonvilleapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class FeedbackActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.title_activity_feedback);
        setContentView(R.layout.activity_feedback);
    }
}
