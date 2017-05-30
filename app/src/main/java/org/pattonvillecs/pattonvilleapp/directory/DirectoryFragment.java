package org.pattonvillecs.pattonvilleapp.directory;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.annimon.stream.Stream;

import org.pattonvillecs.pattonvilleapp.DataSource;
import org.pattonvillecs.pattonvilleapp.R;

import java.util.Collections;

import eu.davidea.flexibleadapter.FlexibleAdapter;

public class DirectoryFragment extends Fragment {
    private RecyclerView directoryRecyclerView;
    private FlexibleAdapter<DirectoryItem> directoryAdapter;

    public DirectoryFragment() {
        // Required empty public constructor
    }

    public static DirectoryFragment newInstance() {
        return new DirectoryFragment();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().setTitle(R.string.title_fragment_directory);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_directory, container, false);
        directoryRecyclerView = (RecyclerView) layout.findViewById(R.id.directory_recyclerView);
        directoryAdapter = new FlexibleAdapter<>(null);

        directoryRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), OrientationHelper.VERTICAL, false));
        directoryRecyclerView.setAdapter(directoryAdapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        directoryRecyclerView.addItemDecoration(dividerItemDecoration);

        directoryAdapter.addItem(new DirectoryItem(DataSource.ALL));
        Stream.of(DataSource.ALL)
                .filter(dataSource -> dataSource != DataSource.EARLY_CHILDHOOD)
                .sorted(DataSource.DEFAULT_ORDERING)
                .map(dataSource -> new DirectoryItem(Collections.singleton(dataSource)))
                .forEach(directoryItem -> directoryAdapter.addItem(directoryItem));

        return layout;
    }
}
