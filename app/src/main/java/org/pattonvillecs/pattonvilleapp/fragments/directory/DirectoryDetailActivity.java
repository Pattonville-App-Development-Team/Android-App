package org.pattonvillecs.pattonvilleapp.fragments.directory;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.pattonvillecs.pattonvilleapp.DataSource;
import org.pattonvillecs.pattonvilleapp.PattonvilleApplication;
import org.pattonvillecs.pattonvilleapp.R;
import org.pattonvillecs.pattonvilleapp.fragments.calendar.data.CalendarParsingUpdateData;
import org.pattonvillecs.pattonvilleapp.listeners.PauseableListener;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import eu.davidea.flexibleadapter.common.SmoothScrollLinearLayoutManager;

import static org.pattonvillecs.pattonvilleapp.fragments.calendar.CalendarEventDetailsActivity.PATTONVILLE_COORDINATES;

public class DirectoryDetailActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    public static final String KEY_DATASOURCES = "dataSources";
    //TODO: Make PauseableListener<DirectoryParsingUpdateData> similar to Calendar*Fragment. Listener must create+attach+register when activity opens, unattach+unregister when it closes, pause when it pauses, resume when it resumes.
    private static final String TAG = "DirectoryDetailActivity";
    private Set<DataSource> dataSources;
    private RecyclerView facultyView;
    private DirectoryAdapter directoryAdapter;
    private ConcurrentMap<DataSource, List<Faculty>> directoryData = new ConcurrentHashMap<>();
    private PattonvilleApplication pattonvilleApplication;
    private PauseableListener<CalendarParsingUpdateData> listener;
    private List<Faculty> faculties;
    private RelativeLayout directoryDetailRelativeLayout;
    private NestedScrollView directoryDetailNestedScrollView;

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_directory_detail);

        pattonvilleApplication = PattonvilleApplication.get(this);

        Intent intent = getIntent();
        //noinspection unchecked
        dataSources = (Set<DataSource>) intent.getSerializableExtra(KEY_DATASOURCES);

        DataSource dataSource;
        if (dataSources.size() == 1)
            dataSource = dataSources.iterator().next();
        else
            dataSource = DataSource.DISTRICT;

        setTitle(dataSource.shortName + " Directory");
        //TODO: Make constant field
        faculties = pattonvilleApplication.getDirectoryData().get(dataSource);


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
        facultyView.setFocusable(false);


        final ImageView schoolImage = (ImageView) findViewById(R.id.directory_school_image);

        int resource;
        switch (dataSources.iterator().next()) {
            case HIGH_SCHOOL:
                resource = R.drawable.highschool_building;
                break;
            case HEIGHTS_MIDDLE_SCHOOL:
                resource = R.drawable.heights_building;
                break;
            case HOLMAN_MIDDLE_SCHOOL:
                resource = R.drawable.holman_building;
                break;
            case REMINGTON_TRADITIONAL_SCHOOL:
                resource = R.drawable.remington_building;
                break;
            case BRIDGEWAY_ELEMENTARY:
                resource = R.drawable.brideway_building;
                break;
            case DRUMMOND_ELEMENTARY:
                resource = R.drawable.drummond_building;
                break;
            case PARKWOOD_ELEMENTARY:
                resource = R.drawable.parkwood_building;
                break;
            case ROSE_ACRES_ELEMENTARY:
                resource = R.drawable.roseacres_building;
                break;
            case WILLOW_BROOK_ELEMENTARY:
                resource = R.drawable.willowbrook_building;
                break;
            case DISTRICT:
            default:
                resource = R.drawable.learningcenter_building;
                break;
        }

        schoolImage.setImageBitmap(decodeSampledBitmapFromResource(getResources(), resource, 512, 512));

        directoryDetailRelativeLayout = (RelativeLayout) findViewById(R.id.directory_detail_relative_layout);
        directoryDetailNestedScrollView = (NestedScrollView) findViewById(R.id.directory_detail_nested_scroll);

        //Inflate the layout the textViews for this Activity
        TextView schoolName = (TextView) findViewById(R.id.directory_detail_schoolName);
        schoolName.setText(dataSource.name);

        TextView schoolAddress = (TextView) findViewById(R.id.directory_address_textView);
        schoolAddress.setText(dataSource.address);
        schoolAddress.setOnClickListener(v -> startMapsActivityForPattonvilleLocation(dataSource.address));

        TextView schoolPhone = (TextView) findViewById(R.id.directory_phoneNumber_textView);
        schoolPhone.setText(dataSource.mainNumber);
        schoolPhone.setOnClickListener(v -> {
            Intent phoneIntent = new Intent(Intent.ACTION_DIAL);
            //currently not working with pauseString extension = getExtension1();
            //currently not working with pause
            phoneIntent.setData(Uri.parse("tel:" + dataSource.mainNumber));
            startActivity(phoneIntent);
        });

        TextView schoolAttendance = (TextView) findViewById(R.id.directory_attendanceNumber_textView);
        dataSource.attendanceNumber.ifPresentOrElse(s -> {
            schoolAttendance.setText(s);
            schoolAttendance.setOnClickListener(v -> {
                Intent phoneIntent = new Intent(Intent.ACTION_DIAL);
                phoneIntent.setData(Uri.parse("tel:" + s));
                startActivity(phoneIntent);
            });
        }, () -> schoolAttendance.setText(R.string.directory_info_unavaiable));

        TextView schoolFax = (TextView) findViewById(R.id.directory_faxNumber_textView);
        dataSource.faxNumber.ifPresentOrElse(s -> {
            schoolFax.setText(s);
            schoolFax.setOnClickListener(v -> {
                Intent phoneIntent = new Intent(Intent.ACTION_DIAL);
                phoneIntent.setData(Uri.parse("tel:" + s));
                startActivity(phoneIntent);
            });
        }, () -> schoolFax.setText(R.string.directory_info_unavaiable));

        TextView websiteView = (TextView) findViewById(R.id.directory_website_textView);
        if (dataSource == DataSource.DISTRICT)
            websiteView.setText(R.string.title_district_website);
        else
            websiteView.setText(R.string.title_school_website);


        websiteView.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(dataSource.websiteURL));
            startActivity(browserIntent);
        });
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

    private void startMapsActivityForPattonvilleLocation(String location) {
        Uri gmmIntentUri = Uri.parse("geo:" + PATTONVILLE_COORDINATES + "?q=" + Uri.encode(location));
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
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
                    directoryDetailRelativeLayout.setVisibility(View.GONE);
                    return true;
                }

                @Override
                public boolean onMenuItemActionCollapse(MenuItem item) {
                    directoryDetailNestedScrollView.scrollTo(0, 0);
                    directoryDetailRelativeLayout.setVisibility(View.VISIBLE);
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
