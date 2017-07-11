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

package org.pattonvillecs.pattonvilleapp.links;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.pattonvillecs.pattonvilleapp.R;
import org.pattonvillecs.pattonvilleapp.preferences.PreferenceUtils;

/**
 * Activity to handle the proper link to powerschool, differing between administrators, teachers,
 * and students & parents
 *
 * @author Nathan Skelton
 */
public class PowerschoolActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_school_list);
        setTitle("Powerschool Links");

        ListView listView = (ListView) findViewById(R.id.school_list_view);


        listView.setAdapter(new ArrayAdapter<>(getApplicationContext(),
                R.layout.simple_listview_item,
                R.id.simple_listview_item_textView,
                new String[]{"Student or Parent", "Teacher", "Administrator"}));
        listView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

        // Launch the proper link, using the position of the element.
        switch (position) {

            case 1:     // Teacher
                launchWebsite("https://powerschool.psdr3.org/teachers/pw.html");
                break;
            case 2:     // Administrator
                launchWebsite("https://powerschool.psdr3.org/admin/pw.html");
                break;
            default:    // Student & Parent

                // Check the preference regarding Powerschool app and handle accordingly
                if (PreferenceUtils.getPowerSchoolIntent(this)) {
                    launchPowerSchoolApp();
                } else {
                    launchWebsite("https://powerschool.psdr3.org");
                }
                break;
        }
    }

    private void launchPowerSchoolApp() {
        // If app installed, launch, if not, open play store to it
        if (getPackageManager().getLaunchIntentForPackage(getString(R.string.package_name_powerschool)) != null) {
            startActivity(getPackageManager().getLaunchIntentForPackage(getString(R.string.package_name_powerschool)));
        } else {

            // Open store app if there, if not open in browser
            try {
                launchWebsite("market://details?id="
                        + getString(R.string.package_name_powerschool));
            } catch (ActivityNotFoundException e) {
                launchWebsite("https://play.google.com/store/apps/details?id="
                        + getString(R.string.package_name_powerschool));
            }
        }
    }

    private void launchWebsite(String url) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
    }
}
