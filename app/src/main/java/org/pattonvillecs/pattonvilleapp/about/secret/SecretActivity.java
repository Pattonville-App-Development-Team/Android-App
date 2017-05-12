package org.pattonvillecs.pattonvilleapp.about.secret;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.pattonvillecs.pattonvilleapp.PattonvilleApplication;
import org.pattonvillecs.pattonvilleapp.R;
import org.pattonvillecs.pattonvilleapp.preferences.OnSharedPreferenceKeyChangedListener;
import org.pattonvillecs.pattonvilleapp.preferences.PreferenceUtils;

import java.util.Collections;
import java.util.Set;

/**
 * Created by skaggsm on 5/12/17.
 */

public class SecretActivity extends AppCompatActivity implements OnSharedPreferenceKeyChangedListener {
    private static final String KEY_WELLS_WATCHED = "wells_watched";

    private ImageButton wellButton;
    private TextView text;
    private Button resetButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_secret);
        setTitle("Well Watcher");

        wellButton = (ImageButton) findViewById(R.id.well_button);
        text = (TextView) findViewById(R.id.well_text);
        resetButton = (Button) findViewById(R.id.well_reset);

        Picasso.with(this)
                .load(R.drawable.well)
                .fit()
                .centerInside()
                .into(wellButton);
        wellButton.setOnClickListener(v -> {
            SharedPreferences sharedPreferences = PreferenceUtils.getSharedPreferences(SecretActivity.this);
            byte value = (byte) sharedPreferences.getInt(KEY_WELLS_WATCHED, 0);
            sharedPreferences.edit().putInt(KEY_WELLS_WATCHED, value + 1).apply();
        });

        resetButton.setOnClickListener(v -> {
            SharedPreferences sharedPreferences = PreferenceUtils.getSharedPreferences(SecretActivity.this);
            sharedPreferences.edit().putInt(KEY_WELLS_WATCHED, 0).apply();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        PattonvilleApplication.get(this).registerOnPreferenceKeyChangedListener(this);
        this.keyChanged(PreferenceUtils.getSharedPreferences(this), KEY_WELLS_WATCHED);
    }

    @Override
    protected void onPause() {
        super.onPause();
        PattonvilleApplication.get(this).unregisterOnPreferenceKeyChangedListener(this);
    }

    @Override
    public Set<String> getListenedKeys() {
        return Collections.singleton(KEY_WELLS_WATCHED);
    }

    @Override
    public void keyChanged(SharedPreferences sharedPreferences, String key) {
        text.setText(getString(R.string.wells_watched, sharedPreferences.getInt(key, 0)));
    }
}
