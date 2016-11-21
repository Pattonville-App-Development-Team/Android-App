package org.pattonvillecs.pattonvilleapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class FeedbackActivity extends AppCompatActivity {

    private EditText mEditView;
    private Button mButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.title_activity_feedback);
        setContentView(R.layout.activity_feedback);

        mEditView = (EditText) findViewById(R.id.feedback_editText);
        mButton = (Button) findViewById(R.id.feedback_button);

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditView.setText("");

                Toast.makeText(FeedbackActivity.this, "Feedback Sent", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
