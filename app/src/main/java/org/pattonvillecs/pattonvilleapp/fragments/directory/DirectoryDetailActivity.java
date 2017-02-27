package org.pattonvillecs.pattonvilleapp.fragments.directory;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import org.pattonvillecs.pattonvilleapp.DataSource;
import org.pattonvillecs.pattonvilleapp.Faculty;
import org.pattonvillecs.pattonvilleapp.PattonvilleApplication;
import org.pattonvillecs.pattonvilleapp.R;
import org.pattonvillecs.pattonvilleapp.fragments.calendar.data.CalendarParsingUpdateData;
import org.pattonvillecs.pattonvilleapp.listeners.PauseableListener;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import eu.davidea.flexibleadapter.common.SmoothScrollLinearLayoutManager;

public class DirectoryDetailActivity extends AppCompatActivity {
    //TODO: Make PauseableListener<DirectoryParsingUpdateData> similar to Calendar*Fragment. Listener must create+attach+register when activity opens, unattach+unregister when it closes, pause when it pauses, resume when it resumes.
    private static final String TAG = "DirectoryDetailActivity";
    private static DataSource school;
    private RecyclerView facultyView;
    private DirectoryAdapter directoryAdapter;
    private ConcurrentMap<DataSource, List<Faculty>> directoryData = new ConcurrentHashMap<>();
    private PattonvilleApplication pattonvilleApplication;
    private PauseableListener<CalendarParsingUpdateData> listener;
    private List<Faculty> faculties;
    //Intent

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_directory_detail);

        pattonvilleApplication = PattonvilleApplication.get(this);

        Intent intent = getIntent();
        school = (DataSource) intent.getSerializableExtra("School");
        setTitle(school.shortName + " Directory");
        //TODO: Make constant field
        faculties = pattonvilleApplication.getDirectoryData().get(school);

        directoryAdapter = new DirectoryAdapter();

        for (Faculty faculty : faculties) {
            directoryAdapter.addItem(new DirectoryFlexibleItem(this, faculty));
        }
        directoryAdapter.notifyDataSetChanged();

        facultyView = (RecyclerView) findViewById(R.id.directory_detail_recyclerView);
        facultyView.setAdapter(directoryAdapter);
        facultyView.setLayoutManager(new SmoothScrollLinearLayoutManager(this, OrientationHelper.VERTICAL, false));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(facultyView.getContext(), DividerItemDecoration.VERTICAL);
        facultyView.addItemDecoration(dividerItemDecoration);





        //Inflate the layout the textViews for this Activity
        TextView schoolName = (TextView) findViewById(R.id.directory_detail_schoolName);
        schoolName.setText(school.name);

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

        TextView websiteView = (TextView) findViewById(R.id.directory_website_textView);
        if (school == DataSource.DISTRICT) {
            websiteView.setText("District Website");
        } else {
            websiteView.setText("School Website");
        }

        websiteView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(school.websiteURL));
                startActivity(browserIntent);
            }
        });
    }

    public void setDirectoryData(ConcurrentMap<DataSource, List<Faculty>> directoryData) {
        this.directoryData = directoryData;
    }
}
