package org.pattonvillecs.pattonvilleapp.about;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import org.pattonvillecs.pattonvilleapp.R;
import org.pattonvillecs.pattonvilleapp.about.detail.LinkItem;
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

        aboutAdapter.addItem(new DeveloperItem(androidHeader, "Mitchell Skaggs", "Android Team Lead\n\nUniversity:\n\nMissouri University of Science and Technology", R.drawable.face_skaggs,
                new LinkItem(R.drawable.github_box, "https://github.com/magneticflux-"),
                new LinkItem(R.drawable.linkedin_box, "https://www.linkedin.com/in/mitchell-s-16085b13b")
        ));
        aboutAdapter.addItem(new DeveloperItem(androidHeader, "Keturah Gadson", "University:\n\nHarvard University", R.drawable.face_gadson,
                new LinkItem(R.drawable.github_box, "https://github.com/gadsonk")
        ));
        aboutAdapter.addItem(new DeveloperItem(androidHeader, "Ethan Holtgrieve", "University:\n\nTruman State University", R.drawable.face_holtgrieve,
                new LinkItem(R.drawable.github_box, "https://github.com/holtgrie")
        ));
        aboutAdapter.addItem(new DeveloperItem(androidHeader, "Nathan Skelton", "University:\n\nMissouri University of Science and Technology", R.drawable.face_skelton,
                new LinkItem(R.drawable.github_box, "https://github.com/skeltonn"),
                new LinkItem(R.drawable.linkedin_box, "https://www.linkedin.com/in/nathaniel-skelton-8815a413b/")
        ));

        aboutAdapter.addItem(new DeveloperItem(iOSHeader, "Joshua Zahner", "iOS Team Lead\n\nUniversity:\n\nUniversity of Miami", R.drawable.face_zahner,
                new LinkItem(R.drawable.github_box, "https://github.com/Ovec8hkin"),
                new LinkItem(R.drawable.linkedin_box, "https://www.linkedin.com/in/joshuazahner/")
        ));
        aboutAdapter.addItem(new DeveloperItem(iOSHeader, "Mustapha Barrie", "University:\n\nWashington University in St. Louis", R.drawable.face_barrie,
                new LinkItem(R.drawable.github_box, "https://github.com/MustaphaB")
        ));
        aboutAdapter.addItem(new DeveloperItem(iOSHeader, "Kevin Bowers", "University:\n\nUniversity of Missouri - Columbia", R.drawable.face_bowers,
                new LinkItem(R.drawable.github_box, "https://github.com/KevinBowers73")
        ));
        aboutAdapter.addItem(new DeveloperItem(iOSHeader, "Micah Thompkins", "University:\n\nNorthwestern University", R.drawable.face_thompkins,
                new LinkItem(R.drawable.github_box, "https://github.com/MicahThompkins")
        ));

        aboutAdapter.addItem(new DeveloperItem(teacherHeader, "Mr. Simmons", "Supervisor, Representative, Philosopher", R.drawable.face_simmons));
        aboutAdapter.addItem(new SecretDeveloperItem(teacherHeader, null, null, 0));
    }
}
