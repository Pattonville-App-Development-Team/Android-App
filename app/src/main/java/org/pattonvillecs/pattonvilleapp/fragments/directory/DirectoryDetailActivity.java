package org.pattonvillecs.pattonvilleapp.fragments.directory;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.TextView;

import org.pattonvillecs.pattonvilleapp.DataSource;
import org.pattonvillecs.pattonvilleapp.PattonvilleApplication;
import org.pattonvillecs.pattonvilleapp.R;
import org.pattonvillecs.pattonvilleapp.fragments.calendar.data.CalendarParsingUpdateData;
import org.pattonvillecs.pattonvilleapp.listeners.PauseableListener;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import eu.davidea.flexibleadapter.common.SmoothScrollLinearLayoutManager;

public class DirectoryDetailActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    //TODO: Make PauseableListener<DirectoryParsingUpdateData> similar to Calendar*Fragment. Listener must create+attach+register when activity opens, unattach+unregister when it closes, pause when it pauses, resume when it resumes.
    private static final String TAG = "DirectoryDetailActivity";
    private static DataSource school;
    private RecyclerView facultyView;
    private DirectoryAdapter directoryAdapter;
    private ConcurrentMap<DataSource, List<Faculty>> directoryData = new ConcurrentHashMap<>();
    private PattonvilleApplication pattonvilleApplication;
    private PauseableListener<CalendarParsingUpdateData> listener;
    private List<Faculty> faculties;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_directory_detail);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        // Avoid null pointer warning with null check
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        pattonvilleApplication = PattonvilleApplication.get(this);

        Intent intent = getIntent();
        school = (DataSource) intent.getSerializableExtra("School");
        setTitle(school.shortName + " Directory");
        //TODO: Make constant field
        faculties = pattonvilleApplication.getDirectoryData().get(school);

        directoryAdapter = new DirectoryAdapter();

        for (Faculty faculty : faculties) {
            directoryAdapter.addItem(faculty);
        }
        directoryAdapter.notifyDataSetChanged();

        facultyView = (RecyclerView) findViewById(R.id.directory_detail_recyclerView);
        facultyView.setAdapter(directoryAdapter);
        facultyView.setLayoutManager(new SmoothScrollLinearLayoutManager(this, OrientationHelper.VERTICAL, false));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(facultyView.getContext(), DividerItemDecoration.VERTICAL);
        facultyView.addItemDecoration(dividerItemDecoration);

        ImageView schoolImage = (ImageView) findViewById(R.id.directory_school_image);
        switch (school) {
            /*case HIGH_SCHOOL:
                schoolImage.setImageResource(R.drawable.phs_building);
                break;
            case HEIGHTS_MIDDLE_SCHOOL:
                schoolImage.setImageResource(R.drawable.ht_building);
                break;
            case HOLMAN_MIDDLE_SCHOOL:
                schoolImage.setImageResource(R.drawable.ho_building);
                break;
            case REMINGTON_TRADITIONAL_SCHOOL:
                schoolImage.setImageResource(R.drawable.rt_building);
                break;
            case BRIDGEWAY_ELEMENTARY:
                schoolImage.setImageResource(R.drawable.bw_building);
                break;
            case DRUMMOND_ELEMENTARY:
                schoolImage.setImageResource(R.drawable.dr_building);
                break;
            case PARKWOOD_ELEMENTARY:
                schoolImage.setImageResource(R.drawable.pw_building);
                break;
            case ROSE_ACRES_ELEMENTARY:
                schoolImage.setImageResource(R.drawable.ra_building);
                break;
            case WILLOW_BROOK_ELEMENTARY:
                schoolImage.setImageResource(R.drawable.wb_building);
                break;*/
            case DISTRICT:
            default:
                schoolImage.setImageResource(R.drawable.psd_logo);
                break;
        }

        TextView schoolAddress = (TextView) findViewById(R.id.directory_address_textView);
        schoolAddress.setText(school.address);

        TextView schoolPhone = (TextView) findViewById(R.id.directory_phoneNumber_textView);
        schoolPhone.setText(school.mainNumber);

        TextView schoolAttendance = (TextView) findViewById(R.id.directory_attendanceNumber_textView);
        if (school.attendanceNumber.isPresent())
            schoolAttendance.setText(school.attendanceNumber.get());
        else
            schoolAttendance.setText(R.string.directory_info_unavaiable);

        TextView schoolFax = (TextView) findViewById(R.id.directory_faxNumber_textView);
        if (school.faxNumber.isPresent())
            schoolFax.setText(school.faxNumber.get());
        else
            schoolFax.setText(R.string.directory_info_unavaiable);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.activity_directorydetail, menu);
        initSearchView(menu);

        return true;
    }

    public void setDirectoryData(ConcurrentMap<DataSource, List<Faculty>> directoryData) {
        this.directoryData = directoryData;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.directory_detail_menu_link:
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(school.websiteURL));
                startActivity(browserIntent);
                break;

            case android.R.id.home:

                finish();
                break;
        }
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
            directoryAdapter.filterItems(faculties, 100L);
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

                    MenuItem listTypeItem = menu.findItem(R.id.directory_detail_menu_link);
                    if (listTypeItem != null)
                        listTypeItem.setVisible(false);
                    return true;
                }

                @Override
                public boolean onMenuItemActionCollapse(MenuItem item) {
                    MenuItem listTypeItem = menu.findItem(R.id.directory_detail_menu_link);
                    if (listTypeItem != null)
                        listTypeItem.setVisible(true);
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
