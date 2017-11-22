/*
 * Copyright (C) 2017 Mitchell Skaggs, Keturah Gadson, Ethan Holtgrieve, Nathan Skelton, Pattonville School District
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

package org.pattonvillecs.pattonvilleapp.about;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import org.pattonvillecs.pattonvilleapp.R;
import org.pattonvillecs.pattonvilleapp.about.detail.LinkItem;
import org.pattonvillecs.pattonvilleapp.about.secret.SecretDeveloperItem;

import java.util.Arrays;
import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.common.FlexibleItemAnimator;
import eu.davidea.flexibleadapter.common.SmoothScrollGridLayoutManager;
import eu.davidea.flexibleadapter.items.IFlexible;

/**
 * This Activity displays a grid of the developers who worked on the project, organized by team. The blank space at the end of the list may be long-pressed to reveal an easter egg.
 *
 * @author Mitchell Skaggs
 * @since 1.0.0
 */

public class AboutActivity extends AppCompatActivity {

    private FlexibleAdapter<IFlexible> aboutAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("About Us");
        setContentView(R.layout.activity_about);

        DeveloperHeaderItem androidHeader = new DeveloperHeaderItem("Android Team");
        DeveloperHeaderItem iOSHeader = new DeveloperHeaderItem("iOS Team");
        DeveloperHeaderItem teacherHeader = new DeveloperHeaderItem("Instructors");

        List<IFlexible> items = Arrays.asList(
                new DeveloperItem(androidHeader, "Mitchell Skaggs", "Android Team Lead\n\nUniversity:\n\nMissouri University of Science and Technology", R.drawable.face_skaggs,
                        new LinkItem(R.drawable.github_box, "https://github.com/magneticflux-"),
                        new LinkItem(R.drawable.linkedin_box, "https://www.linkedin.com/in/mitchell-s-16085b13b")
                ),
                new DeveloperItem(androidHeader, "Keturah Gadson", "University:\n\nHarvard University", R.drawable.face_gadson,
                        new LinkItem(R.drawable.github_box, "https://github.com/gadsonk")
                ),
                new DeveloperItem(androidHeader, "Ethan Holtgrieve", "University:\n\nTruman State University", R.drawable.face_holtgrieve,
                        new LinkItem(R.drawable.github_box, "https://github.com/holtgrie")
                ),
                new DeveloperItem(androidHeader, "Nathan Skelton", "University:\n\nMissouri University of Science and Technology", R.drawable.face_skelton,
                        new LinkItem(R.drawable.github_box, "https://github.com/skeltonn"),
                        new LinkItem(R.drawable.linkedin_box, "https://www.linkedin.com/in/nathaniel-skelton-8815a413b/")
                ),
                new DeveloperItem(iOSHeader, "Joshua Zahner", "iOS Team Lead\n\nUniversity:\n\nUniversity of Miami", R.drawable.face_zahner,
                        new LinkItem(R.drawable.github_box, "https://github.com/Ovec8hkin"),
                        new LinkItem(R.drawable.linkedin_box, "https://www.linkedin.com/in/joshuazahner/")
                ),
                new DeveloperItem(iOSHeader, "Mustapha Barrie", "University:\n\nWashington University in St. Louis", R.drawable.face_barrie,
                        new LinkItem(R.drawable.github_box, "https://github.com/MustaphaB")
                ),
                new DeveloperItem(iOSHeader, "Kevin Bowers", "University:\n\nUniversity of Missouri - Columbia", R.drawable.face_bowers,
                        new LinkItem(R.drawable.github_box, "https://github.com/KevinBowers73")
                ),
                new DeveloperItem(iOSHeader, "Micah Thompkins", "University:\n\nNorthwestern University", R.drawable.face_thompkins,
                        new LinkItem(R.drawable.github_box, "https://github.com/MicahThompkins")
                ),
                new DeveloperItem(teacherHeader, "Mr. Simmons", "Supervisor, Representative, Philosopher", R.drawable.face_simmons),
                new SecretDeveloperItem(teacherHeader, null, null, 0));

        aboutAdapter = new FlexibleAdapter<>(items);
        aboutAdapter.setDisplayHeadersAtStartUp(true);

        RecyclerView aboutRecyclerView = findViewById(R.id.about_us_recyclerview);
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
        aboutRecyclerView.setItemAnimator(new FlexibleItemAnimator());

        aboutRecyclerView.setAdapter(aboutAdapter);
    }
}
