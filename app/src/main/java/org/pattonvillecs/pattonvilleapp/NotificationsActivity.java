package org.pattonvillecs.pattonvilleapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.TextView;

public class NotificationsActivity extends AppCompatActivity {

    private ListView mListView;
    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.title_activity_notifications);
        setContentView(R.layout.activity_notifications);

        mListView = (ListView) findViewById(R.id.notifications_list);


        mTextView = (TextView) findViewById(R.id.notifications_text);
        //ToDo: Change "if" statement for empty list, not adapter
        if (mListView.getAdapter() == null) {
            mTextView.setPadding(0, 32, 0, 0);
            mTextView.setText("No Recent Notifications");
        }

    }
}
