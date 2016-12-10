package org.pattonvillecs.pattonvilleapp.fragments.directory;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import org.pattonvillecs.pattonvilleapp.DataSource;
import org.pattonvillecs.pattonvilleapp.R;

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
        schoolAttendance.setText(school.attendanceNumber);

        TextView schoolFax = (TextView) findViewById(R.id.directory_faxNumber_textView);
        schoolFax.setText(school.faxNumber);



        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.directory_recycler_view);

    }

    private class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ViewHolder(View itemView) {
            super(itemView);

        }


        @Override
        public void onClick(View v) {

        }
    }

}
