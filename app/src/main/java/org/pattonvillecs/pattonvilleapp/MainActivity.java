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
import android.view.MenuItem;

import org.pattonvillecs.pattonvilleapp.fragments.CalendarFragment;
import org.pattonvillecs.pattonvilleapp.fragments.HomeFragment;
import org.pattonvillecs.pattonvilleapp.fragments.NewsFragment;
import org.pattonvillecs.pattonvilleapp.fragments.ResourceFragment;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String KEY_CURRENT_ITEM = "CURRENT_ITEM";

    private static final String TAG = "MainActivity";

    private ResourceFragment resourceFragment;

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

        resourceFragment = (ResourceFragment) getSupportFragmentManager().findFragmentByTag(ResourceFragment.FRAGMENT_TAG);
        if (resourceFragment == null) {
            Log.e("MainActivity", "ResourceFragment null, recreating...");
            resourceFragment = new ResourceFragment();
            getSupportFragmentManager().beginTransaction().add(resourceFragment, ResourceFragment.FRAGMENT_TAG).commitNow();
        }

        int currentItem = (Integer) resourceFragment.getOrDefault(KEY_CURRENT_ITEM, R.id.nav_home);

        MenuItem currentMenuItem = navigationView.getMenu().findItem(currentItem);
        currentMenuItem.setChecked(true);
        onNavigationItemSelected(currentMenuItem);
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

        if (item.getItemId() != R.id.nav_settings)
            resourceFragment.put(KEY_CURRENT_ITEM, item.getItemId());

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
                    .replace(R.id.content_default, fragment)
                    .commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
