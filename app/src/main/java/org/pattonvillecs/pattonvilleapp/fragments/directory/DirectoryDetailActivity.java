package org.pattonvillecs.pattonvilleapp.fragments.directory;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import org.pattonvillecs.pattonvilleapp.DataSource;
import org.pattonvillecs.pattonvilleapp.Faculty;
import org.pattonvillecs.pattonvilleapp.R;


import eu.davidea.flexibleadapter.common.SmoothScrollLinearLayoutManager;

public class DirectoryDetailActivity extends AppCompatActivity {
    private RecyclerView facultyView;
    private DirectoryAdapter directoryAdapter;

    private static DataSource school;
    //Intent

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_directory_detail);

        directoryAdapter = new DirectoryAdapter();
        for(int i = 0; i < 10; i++) {
            directoryAdapter.addItem(new DirectoryFlexibleItem(new Faculty().setFaculty()));
        }

        facultyView = (RecyclerView) findViewById(R.id.directory_detail_recyclerView);
        facultyView.setAdapter(directoryAdapter);
        facultyView.setLayoutManager(new SmoothScrollLinearLayoutManager(this, OrientationHelper.VERTICAL, false));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(facultyView.getContext(), DividerItemDecoration.VERTICAL);
        facultyView.addItemDecoration(dividerItemDecoration);

        Intent intent = getIntent();
        school = (DataSource) intent.getSerializableExtra("School");

        setTitle(school.shortName + " Directory");


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
        if(school.name.equalsIgnoreCase("pattonville school district")){
            websiteView.setText("District Website");
        } else { websiteView.setText("School Website");}

        websiteView.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(school.websiteURL));
                startActivity(browserIntent);
            }
        });
    }
}
