package org.pattonvillecs.pattonvilleapp.about.secret;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.github.jinatonic.confetti.ConfettiManager;
import com.github.jinatonic.confetti.ConfettiSource;
import com.github.jinatonic.confetti.confetto.BitmapConfetto;
import com.squareup.picasso.Picasso;

import org.pattonvillecs.pattonvilleapp.PattonvilleApplication;
import org.pattonvillecs.pattonvilleapp.R;
import org.pattonvillecs.pattonvilleapp.preferences.OnSharedPreferenceKeyChangedListener;
import org.pattonvillecs.pattonvilleapp.preferences.PreferenceUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.pattonvillecs.pattonvilleapp.about.secret.SecretShopActivity.KEY_4K_CAMERAS;
import static org.pattonvillecs.pattonvilleapp.about.secret.SecretShopActivity.KEY_HD_CAMERAS;
import static org.pattonvillecs.pattonvilleapp.about.secret.SecretShopActivity.KEY_SD_CAMERAS;

/**
 * Created by skaggsm on 5/12/17.
 */

public class SecretActivity extends AppCompatActivity implements OnSharedPreferenceKeyChangedListener {
    static final String KEY_GP = "user_gp";
    private static final long BASE_MULTIPLIER = 1;
    private static final long SD_CAMERA_MULTIPLIER = 1;
    private static final long HD_CAMERA_MULTIPLIER = 4;
    private static final long _4K_CAMERA_MULTIPLIER = 8;
    private static final long BASE_INCREMENT = 1;

    private ImageButton wellButton;
    private TextView text;
    private Button resetButton;
    private SharedPreferences sharedPreferences;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_secret, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.secret_shop:
                startActivity(new Intent(this, SecretShopActivity.class));
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_secret);
        setTitle("Well Watcher");

        sharedPreferences = PreferenceUtils.getSharedPreferences(SecretActivity.this);

        wellButton = (ImageButton) findViewById(R.id.well_button);
        text = (TextView) findViewById(R.id.well_text);
        resetButton = (Button) findViewById(R.id.well_reset);

        Picasso.with(this)
                .load(R.drawable.well)
                .fit()
                .centerInside()
                .into(wellButton);

        wellButton.setOnClickListener(v -> performWellWatch());
        wellButton.setOnTouchListener((v, event) -> {
            if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                new ConfettiManager(
                        SecretActivity.this,
                        random -> new BitmapConfetto(getIncrementNumberBitmap()),
                        new ConfettiSource(Math.round(event.getX()), Math.round(event.getY())),
                        (ViewGroup) findViewById(android.R.id.content))
                        .setVelocityX(0, 500)
                        .setVelocityY(0, 500)
                        .setEmissionDuration(0)
                        .setNumInitialCount(1)
                        .setAccelerationY(3500)
                        .animate();
            }
            return false;
        });
        resetButton.setOnClickListener(v -> showReset());
    }

    private Bitmap getIncrementNumberBitmap() {
        String text = Long.toString(BASE_INCREMENT * getCurrentMultiplier());

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 18, getResources().getDisplayMetrics()));
        paint.setTypeface(Typeface.DEFAULT_BOLD);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            paint.setColor(getResources().getColor(R.color.colorPrimary, null));
        else
            paint.setColor(getResources().getColor(R.color.colorPrimary));

        float baseline = -paint.ascent(); // ascent() is negative
        int width = (int) (paint.measureText(text) + 0.5f); // round
        int height = (int) (baseline + paint.descent() + 0.5f);

        Bitmap image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(image);
        canvas.drawText(text, 0, baseline, paint);
        return image;
    }

    private void showReset() {
        new AlertDialog.Builder(this)
                .setMessage("Do you want to reset everything?")
                .setPositiveButton(android.R.string.yes, (dialog, which) -> resetSecret())
                .setCancelable(true)
                .show();
    }

    private void performWellWatch() {
        long value = sharedPreferences.getLong(KEY_GP, 0);
        long multiplier = getCurrentMultiplier();
        sharedPreferences.edit().putLong(KEY_GP, value + BASE_INCREMENT * multiplier).apply();
    }

    private void resetSecret() {
        sharedPreferences.edit()
                .putLong(KEY_GP, 0)
                .putLong(KEY_SD_CAMERAS, 0)
                .putLong(KEY_HD_CAMERAS, 0)
                .putLong(KEY_4K_CAMERAS, 0)
                .apply();
    }

    @Override
    protected void onResume() {
        super.onResume();
        PattonvilleApplication.get(this).registerOnPreferenceKeyChangedListener(this);
        updateStats();
    }

    @Override
    protected void onPause() {
        super.onPause();
        PattonvilleApplication.get(this).unregisterOnPreferenceKeyChangedListener(this);
    }

    @Override
    public Set<String> getListenedKeys() {
        return new HashSet<>(Arrays.asList(
                KEY_GP,
                KEY_SD_CAMERAS,
                KEY_HD_CAMERAS,
                KEY_4K_CAMERAS
        ));
    }

    @Override
    public void keyChanged(SharedPreferences sharedPreferences, String key) {
        switch (key) {
            case KEY_GP:
            case KEY_SD_CAMERAS:
            case KEY_HD_CAMERAS:
            case KEY_4K_CAMERAS:
                updateStats();
        }
    }

    private void updateStats() {
        text.setText(getString(R.string.wells_watched, sharedPreferences.getLong(KEY_GP, 0), getCurrentMultiplier()));
    }

    private long getCurrentMultiplier() {
        long currentSDCameras = sharedPreferences.getLong(KEY_SD_CAMERAS, 0);
        long currentHDCameras = sharedPreferences.getLong(KEY_HD_CAMERAS, 0);
        long current4KCameras = sharedPreferences.getLong(KEY_4K_CAMERAS, 0);

        return BASE_MULTIPLIER
                * Math.max(1, currentSDCameras * SD_CAMERA_MULTIPLIER)
                * Math.max(1, currentHDCameras * HD_CAMERA_MULTIPLIER)
                * Math.max(1, current4KCameras * _4K_CAMERA_MULTIPLIER);
    }
}
