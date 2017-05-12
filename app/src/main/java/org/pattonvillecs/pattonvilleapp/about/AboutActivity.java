package org.pattonvillecs.pattonvilleapp.about;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;

import org.pattonvillecs.pattonvilleapp.R;

import java.text.NumberFormat;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.common.SmoothScrollStaggeredLayoutManager;

/**
 * Created by skaggsm on 5/11/17.
 */

public class AboutActivity extends AppCompatActivity {

    private RecyclerView aboutRecyclerView;
    private FlexibleAdapter<DeveloperItem> aboutAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("About Us");
        setContentView(R.layout.activity_about);

        aboutRecyclerView = (RecyclerView) findViewById(R.id.about_us_recyclerview);
        aboutAdapter = new FlexibleAdapter<>(null);

        aboutRecyclerView.setLayoutManager(new SmoothScrollStaggeredLayoutManager(this, 2, StaggeredGridLayoutManager.VERTICAL));
        aboutRecyclerView.setAdapter(aboutAdapter);

        for (int i = 0; i < 100; i++) {
            aboutAdapter.addItem(new DeveloperItem("Test Item no. " + NumberFormat.getIntegerInstance().format(Math.pow(10, i)), R.drawable.highschool_building));
        }
    }
}
