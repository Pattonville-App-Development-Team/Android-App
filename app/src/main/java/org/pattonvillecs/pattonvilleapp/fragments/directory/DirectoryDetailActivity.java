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

        TextView schoolName = (TextView) findViewById(R.id.directory_detail_schoolName);
        schoolName.setText(school.name);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.directory_recycler_view);

    }

    private class DirectoryHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public DirectoryHolder(View itemView) {
            super(itemView);

        }


        @Override
        public void onClick(View v) {

        }
    }

}
