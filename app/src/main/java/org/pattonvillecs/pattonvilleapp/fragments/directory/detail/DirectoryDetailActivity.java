package org.pattonvillecs.pattonvilleapp.fragments.directory.detail;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.InputType;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;

import org.pattonvillecs.pattonvilleapp.DataSource;
import org.pattonvillecs.pattonvilleapp.PattonvilleApplication;
import org.pattonvillecs.pattonvilleapp.R;
import org.pattonvillecs.pattonvilleapp.fragments.directory.DirectoryParsingUpdateData;
import org.pattonvillecs.pattonvilleapp.listeners.PauseableListener;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import eu.davidea.fastscroller.FastScroller;
import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.common.SmoothScrollLinearLayoutManager;
import eu.davidea.flexibleadapter.utils.Utils;

import static org.pattonvillecs.pattonvilleapp.fragments.calendar.CalendarEventDetailsActivity.PATTONVILLE_COORDINATES;

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

            ((GradientDrawable) bubble.getBackground()).setColor(Utils.fetchAccentColor(fastScroller.getContext(), Color.RED));
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


        DataSource dataSource;
        if (dataSources.size() == 1)
            dataSource = dataSources.iterator().next();
        else
            dataSource = DataSource.DISTRICT;

        setTitle(dataSource.shortName + " Directory");

        directoryAdapter = new DirectoryAdapter<>(null);
        RecyclerView facultyRecyclerView = (RecyclerView) findViewById(R.id.directory_detail_recyclerView);

        facultyRecyclerView.setAdapter(directoryAdapter);
        facultyRecyclerView.setLayoutManager(new SmoothScrollLinearLayoutManager(this, OrientationHelper.VERTICAL, false));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(facultyRecyclerView.getContext(), DividerItemDecoration.VERTICAL);
        facultyRecyclerView.addItemDecoration(dividerItemDecoration);
        facultyRecyclerView.setFocusable(false);

        fastScroller = (FastScroller) findViewById(R.id.fast_scroller);
        directoryAdapter.setFastScroller(fastScroller, Utils.fetchAccentColor(this, Color.RED)); // Default red to show an error

        headerItem = new DirectoryDetailHeaderItem(dataSource);
        directoryAdapter.addScrollableHeader(headerItem);
        directoryAdapter.setDisplayHeadersAtStartUp(true);
    }

    private void updateDirectoryData(DirectoryParsingUpdateData data) {
        faculties = Stream.of(dataSources)
                .flatMap(dataSource -> Stream.of(data.getDirectoryData().get(dataSource)))
                .collect(Collectors.toList());
        //noinspection unchecked
        directoryAdapter.updateDataSet(new ArrayList<>(faculties), true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.activity_directorydetail, menu);
        initSearchView(menu);

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
            //noinspection unchecked
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
                    return true;
                }

                @Override
                public boolean onMenuItemActionCollapse(MenuItem item) {
                    directoryAdapter.addScrollableHeader(headerItem);
                    return true;
                }

            });

            SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
            searchView.setInputType(InputType.TYPE_TEXT_VARIATION_FILTER);
            searchView.setImeOptions(EditorInfo.IME_ACTION_DONE | EditorInfo.IME_FLAG_NO_FULLSCREEN);
            searchView.setQueryHint(getString(R.string.action_search));
            searchView.setSearchableInfo(searchManager.getSearchableInfo(this.getComponentName()));
            searchView.setOnQueryTextListener(this);
        }
    }
}
