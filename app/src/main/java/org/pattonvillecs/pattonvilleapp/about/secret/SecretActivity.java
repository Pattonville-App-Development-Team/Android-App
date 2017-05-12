package org.pattonvillecs.pattonvilleapp.about.secret;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import org.pattonvillecs.pattonvilleapp.R;

/**
 * Created by skaggsm on 5/12/17.
 */

public class SecretActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_secret);
        setTitle("Well Watcher");
    }
}
