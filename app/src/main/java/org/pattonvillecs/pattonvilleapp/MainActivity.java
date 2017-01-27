package org.pattonvillecs.pattonvilleapp;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import org.pattonvillecs.pattonvilleapp.fragments.HomeFragment;
import org.pattonvillecs.pattonvilleapp.fragments.ResourceFragment;
import org.pattonvillecs.pattonvilleapp.fragments.calendar.CalendarFragment;
import org.pattonvillecs.pattonvilleapp.fragments.calendar.CalendarPinnedFragment;
import org.pattonvillecs.pattonvilleapp.fragments.directory.DirectoryFragment;
import org.pattonvillecs.pattonvilleapp.fragments.news.NewsFragment;

import java.io.File;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";
    private DrawerLayout mDrawerLayout;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "savedInstanceState == null is " + (savedInstanceState == null));

        checkAppIntro();

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Log.d("SELECTED SCHOOLS", "These are selected: " + PreferenceUtils.getSelectedSchoolsSet(this));

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();


        NavigationView mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);
        if (savedInstanceState == null) { // First run
            mNavigationView.setCheckedItem(R.id.nav_home);
            this.onNavigationItemSelected(mNavigationView.getMenu().findItem(R.id.nav_home));
        }

        ResourceFragment.retrieveResourceFragment(getSupportFragmentManager());
        enableHttpResponseCache();
    }

    private void checkAppIntro() {
        SharedPreferences sharedPreferences = PreferenceUtils.getSharedPreferences(this);

        //  Create a new boolean and preference and set it to true
        //TODO: CHANGE BEFORE PR
        boolean isFirstStart = true; //sharedPreferences.getBoolean(PreferenceUtils.APP_INTRO_FIRST_START_PREFERENCE_KEY, true);

        //  If the activity has never started before...
        if (isFirstStart) {
            Intent i = new Intent(this, PattonvilleAppIntro.class);

            SharedPreferences.Editor editor = sharedPreferences.edit();

            //  Edit preference to make it false because we don't want this to run again
            editor.putBoolean(PreferenceUtils.APP_INTRO_FIRST_START_PREFERENCE_KEY, false);
            editor.apply();

            startActivity(i);
        }
    }

    /**
     * This is to create an HTTP cache that we can use to prevent constant downloads when loading articles
     */
    private void enableHttpResponseCache() {
        try {
            long httpCacheSize = 10 * 1024 * 1024; // 10 MiB
            File httpCacheDir = new File(getCacheDir(), "http");
            Class.forName("android.net.http.HttpResponseCache")
                    .getMethod("install", File.class, long.class)
                    .invoke(null, httpCacheDir, httpCacheSize);
        } catch (Exception httpResponseCacheNotAvailable) {
            Log.d(TAG, "HTTP response cache is unavailable.");
        }
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.

        Fragment fragment = null;

        switch (item.getItemId()) {
            case R.id.nav_home:
                fragment = HomeFragment.newInstance();
                break;

            case R.id.nav_news:
                fragment = NewsFragment.newInstance();
                break;

            case R.id.nav_calendar:
                fragment = CalendarFragment.newInstance();
                break;

            case R.id.nav_directory:
                fragment = DirectoryFragment.newInstance();
                break;

            case R.id.nav_nutrislice:
                launchNutrislice();
                break;

            case R.id.nav_peachjar:
                startActivity(new Intent(this, PeachjarActivity.class));
                break;

            case R.id.nav_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;

            case R.id.nav_powerschool:
                launchPowerSchool();
                break;

            case R.id.nav_activities:
                launchWebsite("http://pirates.psdr3.org");
                break;

            case R.id.nav_psd:
                launchWebsite("http://www.psdr3.org");
                break;

            case R.id.nav_feedback:
                startActivity(new Intent(this, FeedbackActivity.class));
                break;

            case R.id.nav_notifications:
                startActivity(new Intent(this, NotificationsActivity.class));
        }

        if (fragment != null) {
            final FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.content_default, fragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .commit();
        }

        supportInvalidateOptionsMenu();
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void launchPowerSchool() {
        // Check between using app or browser
        if (PreferenceUtils.getPowerSchoolIntent(this)) {

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
        } else {
            launchWebsite("https://powerschool.psdr3.org");
        }
    }

    private void launchNutrislice() {

        // If app installed, launch, if not, open play store to it
        if (getPackageManager().getLaunchIntentForPackage(getString(R.string.package_name_nutrislice)) != null) {
            startActivity(getPackageManager().getLaunchIntentForPackage(getString(R.string.package_name_nutrislice)));
        } else {

            // Open store app if there, if not open in browser
            try {
                launchWebsite("market://details?id=" + getString(R.string.package_name_nutrislice));
            } catch (ActivityNotFoundException e) {
                launchWebsite("https://play.google.com/store/apps/details?id="
                        + getString(R.string.package_name_nutrislice));
            }
        }
    }

    private void launchWebsite(String url) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
    }

    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
        int id;
        if (fragment instanceof NewsFragment) id = R.id.nav_news;
        else if (fragment instanceof DirectoryFragment) id = R.id.nav_directory;
        else if (fragment instanceof CalendarFragment) id = R.id.nav_calendar;
        else if (fragment instanceof CalendarPinnedFragment) id = R.id.nav_calendar;
        else return;
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        if (navigationView != null) {
            navigationView.setCheckedItem(id);
        }
    }
}
