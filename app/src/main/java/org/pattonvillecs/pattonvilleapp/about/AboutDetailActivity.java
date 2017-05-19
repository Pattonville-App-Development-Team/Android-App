package org.pattonvillecs.pattonvilleapp.about;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.pattonvillecs.pattonvilleapp.R;

/**
 * Created by skaggsm on 5/15/17.
 */

public class AboutDetailActivity extends AppCompatActivity {
    private static final String KEY_NAME = "name", KEY_TEXT = "text", KEY_IMAGE = "image";
    private static final String TAG = AboutDetailActivity.class.getSimpleName();

    private String developerName, developerText;
    @DrawableRes
    private int developerImage;

    private TextView name, text;
    private ImageView image;

    public static Intent createIntent(Context context, String name, String text, @DrawableRes int image) {
        return new Intent(context, AboutDetailActivity.class)
                .putExtra(KEY_NAME, name)
                .putExtra(KEY_TEXT, text)
                .putExtra(KEY_IMAGE, image);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        supportFinishAfterTransition();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                supportFinishAfterTransition();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_about_detail);

        developerName = getIntent().getStringExtra(KEY_NAME);
        developerText = getIntent().getStringExtra(KEY_TEXT);
        developerImage = getIntent().getIntExtra(KEY_IMAGE, 0);

        name = (TextView) findViewById(R.id.developer_name);
        text = (TextView) findViewById(R.id.developer_text);
        image = (ImageView) findViewById(R.id.developer_image);

        name.setText(developerName);
        text.setText(developerText);

        supportPostponeEnterTransition();
        Picasso.with(this)
                .load(developerImage)
                .fit()
                .centerCrop()
                .transform(new CircleTransformation())
                .into(image, new Callback() {
                    @Override
                    public void onSuccess() {
                        supportStartPostponedEnterTransition();
                    }

                    @Override
                    public void onError() {
                        supportStartPostponedEnterTransition();
                    }
                });
    }
}
