package org.pattonvillecs.pattonvilleapp.fragments.directory;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.pattonvillecs.pattonvilleapp.DataSource;
import org.pattonvillecs.pattonvilleapp.Faculty;
import org.pattonvillecs.pattonvilleapp.R;

import java.util.ArrayList;
import java.util.List;

public class DirectoryDetailActivity extends AppCompatActivity {

    private static DataSource school;
    //Intent

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_directory_detail);
        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);*/
        Intent intent = getIntent();
        school = (DataSource) intent.getSerializableExtra("School");

        //Inflate the layout the textViews for this Activity
        TextView schoolName = (TextView) findViewById(R.id.directory_detail_schoolName);
        schoolName.setText(school.name);

        TextView schoolAddress = (TextView) findViewById(R.id.directory_address_textView);
        schoolAddress.setText(school.address);

        TextView schoolPhone = (TextView) findViewById(R.id.directory_phoneNumber_textView);
        schoolPhone.setText(school.mainNumber);

        TextView schoolAttendance = (TextView) findViewById(R.id.directory_attendanceNumber_textView);
        if (school.attendanceNumber == null) {
            schoolAttendance.setText(R.string.directory_info_unavaiable);
        } else {
            schoolAttendance.setText(school.attendanceNumber);
        }

        TextView schoolFax = (TextView) findViewById(R.id.directory_faxNumber_textView);
        if (school.faxNumber == null) {
            schoolFax.setText(R.string.directory_info_unavaiable);
        } else {
            schoolFax.setText(school.faxNumber);
        }


        ListView listview = (ListView) findViewById(R.id.directory_detail_list_view);

        listview.setAdapter(new BaseAdapter() {
            List<Faculty> faculties = new ArrayList<Faculty>(10);

            {
                for (int i = 0; i < 10; i++) {
                    faculties.add(new Faculty().setFaculty());
                }
            }

            @Override
            public int getCount() {
                return faculties.size();
            }

            @Override
            public Faculty getItem(int i) {
                return faculties.get(i);
            }

            @Override
            public long getItemId(int i) {
                return i;
            }

            @Override
            public View getView(int i, View convertView, ViewGroup parent) {
                View view = convertView;
                if (view == null)
                    view = LayoutInflater.from(DirectoryDetailActivity.this).inflate(R.layout.list_faculty_item, parent, false);

                Faculty f = getItem(i);
                TextView nameTextView = (TextView) view.findViewById(R.id.directory_facultyName_textView);
                nameTextView.setText(f.getName());

                TextView departmentTextView = (TextView) view.findViewById(R.id.directory_facultyDepartment_textView);
                departmentTextView.setText(f.getDepartment());

                return view;
            }
        });
    }
}
