package org.pattonvillecs.pattonvilleapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import org.pattonvillecs.pattonvilleapp.fragments.CalendarFragment;
import org.pattonvillecs.pattonvilleapp.fragments.HomeFragment;
import org.pattonvillecs.pattonvilleapp.fragments.NewsFragment;
import org.pattonvillecs.pattonvilleapp.fragments.ResourceFragment;

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
        Log.e(TAG, "savedInstanceState == null is " + (savedInstanceState == null));
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);
        if (savedInstanceState == null) { // First run
            mNavigationView.setCheckedItem(R.id.nav_home);
            this.onNavigationItemSelected(mNavigationView.getMenu().findItem(R.id.nav_home));
        }

        ResourceFragment.retrieveResourceFragment(getSupportFragmentManager());
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
                //setTitle(R.string.title_fragment_directory); Do in Fragment
                break;

            case R.id.nav_nutrislice:
                break;

            case R.id.nav_peachjar:
                break;

            case R.id.nav_settings:
                startActivity(new Intent(this, SettingsActivity.class));
        }

        if (fragment != null) {
            final FragmentManager fragmentManager = getSupportFragmentManager();
            final Fragment finalFragment = fragment;
            mDrawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
                @Override
                public void onDrawerSlide(View drawerView, float slideOffset) {
                }

                @Override
                public void onDrawerOpened(View drawerView) {
                }

                @Override
                public void onDrawerClosed(View drawerView) {
                    fragmentManager.beginTransaction()
                            .replace(R.id.content_default, finalFragment)
                            .commitNow();
                    mDrawerLayout.removeDrawerListener(this);
                }

                @Override
                public void onDrawerStateChanged(int newState) {
                }
            });
        }

        supportInvalidateOptionsMenu();

        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}
