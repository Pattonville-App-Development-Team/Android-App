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
import android.view.MenuItem;

import org.pattonvillecs.pattonvilleapp.fragments.CalendarFragment;
import org.pattonvillecs.pattonvilleapp.fragments.HomeFragment;
import org.pattonvillecs.pattonvilleapp.fragments.NewsFragment;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";

    //private ResourceFragment resourceFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
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
                setTitle(R.string.title_fragment_home);
                break;

            case R.id.nav_news:
                fragment = NewsFragment.newInstance();
                setTitle(R.string.title_fragment_news);
                break;

            case R.id.nav_calendar:
                fragment = CalendarFragment.newInstance();
                setTitle(R.string.title_fragment_calendar);
                break;

            case R.id.nav_directory:
                setTitle(R.string.title_fragment_directory);
                break;

            case R.id.nav_nutrislice:
                break;

            case R.id.nav_peachjar:
                break;
            case R.id.nav_settings:
                startActivity(new Intent(this, SettingsActivity.class));
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.content_default, fragment, fragment.getClass().getSimpleName())
                    .commitNow();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
