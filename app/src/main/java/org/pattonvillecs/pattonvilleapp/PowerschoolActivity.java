package org.pattonvillecs.pattonvilleapp;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class PowerschoolActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_school_list);
        setTitle("Powerschool Links");

        mListView = (ListView) findViewById(R.id.school_list_view);


        mListView.setAdapter(new ArrayAdapter<>(getApplicationContext(),
                R.layout.simple_listview_item,
                R.id.simple_listview_item_textView,
                new String[]{"Student or Parent", "Teacher", "Administrator"}));
        mListView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

        switch (position) {

            case 1:
                launchWebsite("https://powerschool.psdr3.org/teachers/pw.html");
                break;
            case 2:
                launchWebsite("https://powerschool.psdr3.org/admin/pw.html");
                break;
            default:

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
