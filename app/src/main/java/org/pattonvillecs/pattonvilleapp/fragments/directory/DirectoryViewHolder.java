package org.pattonvillecs.pattonvilleapp.fragments.directory;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import org.pattonvillecs.pattonvilleapp.R;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.viewholders.FlexibleViewHolder;

/**
 * Created by gadsonk on 1/20/17.
 */

public class DirectoryViewHolder extends FlexibleViewHolder {
    private static final String TAG = "DirectoryViewHolder";
    final RecyclerView facultyView;
    final TextView nameText, longDesText, extensionText;
    final ImageButton emailButton;

    public DirectoryViewHolder(View view, FlexibleAdapter adapter) {
        this(view, adapter, false);
    }

    public DirectoryViewHolder(View view, FlexibleAdapter adapter, boolean stickyHeader) {
        super(view, adapter, stickyHeader);
        nameText = (TextView) view.findViewById(R.id.directory_facultyName_textView);
        longDesText = (TextView) view.findViewById(R.id.directory_facultyDepartment_textView);
        extensionText = (TextView) view.findViewById(R.id.directory_facultyExtension_textView);
        emailButton = (ImageButton) view.findViewById(R.id.directory_facultyEmail_imageButton);
        facultyView = (RecyclerView) view.findViewById(R.id.directory_detail_recyclerView);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        Log.i(TAG, "Clicked view: " + view);
    }
}


