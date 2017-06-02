/*
 * Copyright (C) 2017  Mitchell Skaggs, Keturah Gadson, Ethan Holtgrieve, and Nathan Skelton
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.pattonvillecs.pattonvilleapp.about.detail;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.pattonvillecs.pattonvilleapp.R;
import org.pattonvillecs.pattonvilleapp.about.CircleTransformation;

import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.common.SmoothScrollLinearLayoutManager;

/**
 * Created by skaggsm on 5/15/17.
 */

public class AboutDetailActivity extends AppCompatActivity {
    private static final String KEY_NAME = "name", KEY_TEXT = "text", KEY_IMAGE = "image", KEY_LINK_ITEMS = "linkItems";
    private static final String TAG = AboutDetailActivity.class.getSimpleName();

    private String developerName, developerText;
    @DrawableRes
    private int developerImage;

    private TextView name, text;
    private ImageView image;
    private RecyclerView links;
    private FlexibleAdapter<LinkItem> linksAdapter;
    private List<LinkItem> developerLinks;

    public static Intent createIntent(Context context, String name, String text, @DrawableRes int image, LinkItem[] linkItems) {
        return new Intent(context, AboutDetailActivity.class)
                .putExtra(KEY_NAME, name)
                .putExtra(KEY_TEXT, text)
                .putExtra(KEY_IMAGE, image)
                .putExtra(KEY_LINK_ITEMS, linkItems);
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
        developerLinks = Stream.of(getIntent().getParcelableArrayExtra(KEY_LINK_ITEMS)).map(parcelable -> (LinkItem) parcelable).collect(Collectors.toList());

        name = (TextView) findViewById(R.id.developer_name);
        text = (TextView) findViewById(R.id.developer_text);
        image = (ImageView) findViewById(R.id.developer_image);
        links = (RecyclerView) findViewById(R.id.developer_links);
        linksAdapter = new FlexibleAdapter<>(developerLinks);

        name.setText(developerName);
        text.setText(developerText);

        links.setAdapter(linksAdapter);
        links.setLayoutManager(new SmoothScrollLinearLayoutManager(this, SmoothScrollLinearLayoutManager.HORIZONTAL, false));

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
