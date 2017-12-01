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

package org.pattonvillecs.pattonvilleapp.directory.detail;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.InputType;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;

import org.pattonvillecs.pattonvilleapp.DataSource;
import org.pattonvillecs.pattonvilleapp.PattonvilleApplication;
import org.pattonvillecs.pattonvilleapp.R;
import org.pattonvillecs.pattonvilleapp.directory.DirectoryParsingUpdateData;
import org.pattonvillecs.pattonvilleapp.listeners.PauseableListener;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import eu.davidea.fastscroller.FastScroller;
import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.common.FlexibleItemDecoration;
import eu.davidea.flexibleadapter.common.SmoothScrollLinearLayoutManager;
import eu.davidea.flexibleadapter.utils.FlexibleUtils;

import static org.pattonvillecs.pattonvilleapp.view.ui.calendar.details.CalendarEventDetailsActivity.PATTONVILLE_COORDINATES;
import static org.pattonvillecs.pattonvilleapp.view.ui.spotlight.SpotlightHelper.showSpotlight;

public class DirectoryDetailActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    public static final String KEY_DATASOURCES = "dataSources";
    private static final String TAG = "DirectoryDetailActivity";
    private Set<DataSource> dataSources;
    private FlexibleAdapter<DirectoryItem> directoryAdapter;
    private PattonvilleApplication pattonvilleApplication;
    private PauseableListener<DirectoryParsingUpdateData> listener;
    private List<Faculty> faculties;
    private DirectoryDetailHeaderItem headerItem;
    private FastScroller fastScroller;
    private SearchView searchView;

    public static Intent createIntent(Context context, Set<DataSource> dataSources) {
        return new Intent(context, DirectoryDetailActivity.class)
                .putExtra(KEY_DATASOURCES, (Serializable) dataSources);
    }

    public static void startMapsActivityForPattonvilleLocation(String location, Context context) {
        Uri gmmIntentUri = Uri.parse("geo:" + PATTONVILLE_COORDINATES + "?q=" + Uri.encode(location));
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        context.startActivity(mapIntent);
    }

    private static void setBubbleTextSize(FastScroller fastScroller) {
        //noinspection TryWithIdenticalCatches
        try {
            Field bubbleField = FastScroller.class.getDeclaredField("bubble");
            bubbleField.setAccessible(true);
            TextView bubble = (TextView) bubbleField.get(fastScroller);

            bubble.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            bubble.setBackgroundResource(R.drawable.fast_scroller_bubble_small);

            ((GradientDrawable) bubble.getBackground()).setColor(FlexibleUtils.fetchAccentColor(fastScroller.getContext(), Color.RED));
        } catch (NoSuchFieldException e) {
            Log.wtf(TAG, e);
        } catch (IllegalAccessException e) {
            Log.wtf(TAG, e);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        listener.unattach();
        pattonvilleApplication.unregisterPauseableListener(listener);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart: Called");
        listener.attach(pattonvilleApplication);
        setBubbleTextSize(fastScroller);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause: Called");
        listener.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume: Called");
        listener.resume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate: Called");
        setContentView(R.layout.activity_directory_detail);

        Intent intent = getIntent();
        //noinspection unchecked
        dataSources = (Set<DataSource>) intent.getSerializableExtra(KEY_DATASOURCES);

        pattonvilleApplication = PattonvilleApplication.get(this);
        listener = new PauseableListener<DirectoryParsingUpdateData>() {
            @Override
            public int getIdentifier() {
                return DirectoryParsingUpdateData.DIRECTORY_LISTENER_ID;
            }

            @Override
            public void onReceiveData(DirectoryParsingUpdateData data) {
                super.onReceiveData(data);
                updateDirectoryData(data);
            }

            @Override
            public void onResume(DirectoryParsingUpdateData data) {
                super.onResume(data);
                updateDirectoryData(data);
            }
        };
        pattonvilleApplication.registerPauseableListener(listener);

        boolean oneDataSourceProvided = dataSources.size() == 1;

        DataSource dataSource;
        if (oneDataSourceProvided)
            dataSource = dataSources.iterator().next();
        else if (dataSources.equals(DataSource.ALL)) {
            dataSource = null;
            setTitle("All Schools Directory");
        } else
            dataSource = DataSource.DISTRICT;

        if (dataSource != null)
            setTitle(dataSource.shortName + " Directory");

        directoryAdapter = new DirectoryAdapter<>(null, oneDataSourceProvided);
        RecyclerView facultyRecyclerView = (RecyclerView) findViewById(R.id.directory_detail_recyclerView);

        facultyRecyclerView.setAdapter(directoryAdapter);
        facultyRecyclerView.setLayoutManager(new SmoothScrollLinearLayoutManager(this, OrientationHelper.VERTICAL, false));
        FlexibleItemDecoration flexibleItemDecoration = new FlexibleItemDecoration(facultyRecyclerView.getContext());
        facultyRecyclerView.addItemDecoration(flexibleItemDecoration);
        facultyRecyclerView.setFocusable(false);

        fastScroller = (FastScroller) findViewById(R.id.fast_scroller);
        directoryAdapter.setFastScroller(fastScroller);//, Utils.fetchAccentColor(this, Color.RED)); // Default red to show an error

        directoryAdapter.setDisplayHeadersAtStartUp(true);
        if (dataSources.size() > 1)
            facultyRecyclerView.post(() -> directoryAdapter.setStickyHeaders(true)); //Needed because .post() is cleared by the UPDATE handler ID.

        if (dataSource != null) {
            headerItem = new DirectoryDetailHeaderItem(dataSource);
            directoryAdapter.addScrollableHeader(headerItem);
        }
    }

    private void updateDirectoryData(DirectoryParsingUpdateData data) {
        Map<DataSource, FacultyHeader> headerMap = new HashMap<>();
        faculties = Stream.of(dataSources)
                .filter(dataSource -> data.getDirectoryData().containsKey(dataSource))
                .flatMap(dataSource -> Stream.of(data.getDirectoryData().get(dataSource)))
                .map(faculty -> {
                    if (dataSources.size() != 1) {
                        DataSource dataSource = faculty.getDirectoryKey();

                        if (!headerMap.containsKey(dataSource))
                            headerMap.put(dataSource, new FacultyHeader(dataSource));

                        faculty.setHeader(headerMap.get(dataSource));
                    } else {
                        faculty.setHeader(null);
                    }

                    return faculty;
                })
                .collect(Collectors.toList());
        directoryAdapter.updateDataSet(new ArrayList<>(faculties), true);

        if (searchView != null) {
            String query = searchView.getQuery().toString();
            if (!query.isEmpty()) {
                Log.i(TAG, "Re-filtering by query");
                directoryAdapter.setSearchText(query);
                directoryAdapter.filterItems(new ArrayList<>(faculties));
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.activity_directorydetail, menu);
        initSearchView(menu);

        //This terrifies me...
        final ViewTreeObserver viewTreeObserver = getWindow().getDecorView().getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                View menuButton = findViewById(R.id.directory_detail_menu_search);
                // This could be called when the button is not there yet, so we must test for null
                if (menuButton != null) {
                    // Found it! Do what you need with the button
                    showSpotlight(DirectoryDetailActivity.this, menuButton, "DirectoryDetailActivity_MenuButtonSearch", "Staff with names, positions, or locations similar to your query will appear after typing", "Search");
                    // Now you can get rid of this listener
                    viewTreeObserver.removeOnGlobalLayoutListener(this);
                }
            }
        });

        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (directoryAdapter.hasNewSearchText(newText)) {
            Log.d(TAG, "onQueryTextChange newText: " + newText);
            directoryAdapter.setSearchText(newText);
            directoryAdapter.filterItems(new ArrayList<>(faculties), 250L);
        }
        return true;
    }

    /**
     * Method to setup the search functionality of the list
     * <p>
     * Refer to the Flexible Adapter documentation, as this is a near replica implementation
     *
     * @param menu Menu object of current options menu
     */

    private void initSearchView(final Menu menu) {
        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) this.getSystemService(Context.SEARCH_SERVICE);
        MenuItem searchItem = menu.findItem(R.id.directory_detail_menu_search);
        if (searchItem != null) {

            MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {

                @Override
                public boolean onMenuItemActionExpand(MenuItem item) {
                    directoryAdapter.removeAllScrollableHeaders();

                    //To lock current orientation
                    int currentOrientation = getResources().getConfiguration().orientation;
                    if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE)
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE); //locks landscape
                    else
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT); //locks port
                    return true;
                }

                @Override
                public boolean onMenuItemActionCollapse(MenuItem item) {
                    if (headerItem != null)
                        directoryAdapter.addScrollableHeader(headerItem);
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                    return true;
                }

            });

            searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
            searchView.setInputType(InputType.TYPE_TEXT_VARIATION_FILTER);
            searchView.setImeOptions(EditorInfo.IME_ACTION_DONE | EditorInfo.IME_FLAG_NO_FULLSCREEN);
            searchView.setQueryHint(getString(R.string.action_search));
            searchView.setSearchableInfo(searchManager.getSearchableInfo(this.getComponentName()));
            searchView.setOnQueryTextListener(this);
        }
    }
}
