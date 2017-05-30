package org.pattonvillecs.pattonvilleapp.directory.detail;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneNumberUtils;
import android.util.Log;

/**
 * Created by skaggsm on 5/9/17.
 */

public class CallExtensionActivity extends AppCompatActivity {
    private static final String EXTRA_FACULTY = "faculty";
    private static final String DISTRICT_VOICEMAIL_PHONE = "3142138010";
    private static final int REQUEST_CALL = 0;
    private static final String TAG = CallExtensionActivity.class.getSimpleName();

    public static Intent newIntent(Context c, Faculty faculty) {
        Intent intent = new Intent(c, CallExtensionActivity.class);
        intent.putExtra(EXTRA_FACULTY, (Parcelable) faculty);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            showConfirmCallDialog();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL);
        }
    }

    private void showConfirmCallDialog() {
        final Faculty faculty = getIntent().getParcelableExtra(EXTRA_FACULTY);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure that you want to call " + faculty.getFormattedFullName() + "?")
                .setPositiveButton(android.R.string.yes, (dialogInterface, i) -> {
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse("tel:"
                            + DISTRICT_VOICEMAIL_PHONE
                            + PhoneNumberUtils.PAUSE
                            + PhoneNumberUtils.PAUSE
                            + PhoneNumberUtils.PAUSE
                            + faculty.getExtension1()));
                    try {
                        startActivity(intent);
                    } catch (SecurityException e) {
                        Log.wtf(TAG, e);
                    }
                    finish();
                })
                .setNegativeButton(android.R.string.no, (dialogInterface, i) -> finish())
                .setOnDismissListener(dialog -> finish())
                .create()
                .show();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CALL && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            showConfirmCallDialog();
        } else {
            finish();
        }

    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
