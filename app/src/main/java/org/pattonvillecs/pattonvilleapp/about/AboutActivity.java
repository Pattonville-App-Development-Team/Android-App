package org.pattonvillecs.pattonvilleapp.about;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import org.pattonvillecs.pattonvilleapp.R;
import org.pattonvillecs.pattonvilleapp.about.secret.SecretDeveloperItem;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.common.FlexibleItemAnimator;
import eu.davidea.flexibleadapter.common.SmoothScrollGridLayoutManager;

/**
 * Created by skaggsm on 5/11/17.
 */

public class AboutActivity extends AppCompatActivity {

    private static final String TAG = AboutActivity.class.getSimpleName();

    private RecyclerView aboutRecyclerView;
    private FlexibleAdapter<DeveloperItem> aboutAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("About Us");
        setContentView(R.layout.activity_about);

        aboutRecyclerView = (RecyclerView) findViewById(R.id.about_us_recyclerview);
        aboutAdapter = new FlexibleAdapter<>(null);

        GridLayoutManager manager = new SmoothScrollGridLayoutManager(this, 2);
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (aboutAdapter.isHeader(aboutAdapter.getItem(position))) {
                    //Headers occupy 2 spans, which is the entire grid
                    return 2;
                } else {
                    //Normally occupy 1 span
                    return 1;
                }
            }
        });
        aboutRecyclerView.setLayoutManager(manager);
        aboutRecyclerView.setAdapter(aboutAdapter);
        aboutRecyclerView.setItemAnimator(new FlexibleItemAnimator());

        aboutAdapter.setDisplayHeadersAtStartUp(true);

        DeveloperHeaderItem androidHeader = new DeveloperHeaderItem("Android Team");
        DeveloperHeaderItem iOSHeader = new DeveloperHeaderItem("iOS Team");
        DeveloperHeaderItem teacherHeader = new DeveloperHeaderItem("Instructors");

        aboutAdapter.addItem(new DeveloperItem(androidHeader, "Mitchell Skaggs", "Android Team Lead\nCalendar, Intro, Databases, Directory", R.drawable.face_skaggs));
        aboutAdapter.addItem(new DeveloperItem(androidHeader, "Keturah Gadson", "Directory, Design, Planning", R.drawable.face_gadson));
        aboutAdapter.addItem(new DeveloperItem(androidHeader, "Nathan Skelton", "News, Preferences, Documentation", R.drawable.highschool_building));
        aboutAdapter.addItem(new DeveloperItem(androidHeader, "Ethan Holtgrieve", "Home, News, Iconography", R.drawable.highschool_building));

        aboutAdapter.addItem(new DeveloperItem(iOSHeader, "Joshua Zahner", "TBA", R.drawable.bridgeway_building));
        aboutAdapter.addItem(new DeveloperItem(iOSHeader, "Micah Thompkins", "TBA", R.drawable.face_thompkins));
        aboutAdapter.addItem(new DeveloperItem(iOSHeader, "Kevin Bowers", "TBA", R.drawable.bridgeway_building));
        aboutAdapter.addItem(new DeveloperItem(iOSHeader, "Mustapha Barrie", "TBA", R.drawable.face_barrie));

        aboutAdapter.addItem(new DeveloperItem(teacherHeader, "Mr. Simmons", "Supervisor, Philosopher,\nWatcher of Wells", R.drawable.face_simmons));
        aboutAdapter.addItem(new SecretDeveloperItem(teacherHeader, null, null, 0));
    }
}
