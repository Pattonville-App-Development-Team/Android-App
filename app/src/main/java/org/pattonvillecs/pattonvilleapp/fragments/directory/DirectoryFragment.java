package org.pattonvillecs.pattonvilleapp.fragments.directory;


import android.os.Bundle;
import android.support.annotation.DrawableRes;
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
    public static final int[] images = {
            R.drawable.d_mascot,
            R.drawable.hs_mascot,
            R.drawable.d_mascot,
            R.drawable.d_mascot,
            R.drawable.rm_mascot,
            R.drawable.br_mascot,
            R.drawable.dr_mascot,
            R.drawable.pw_mascot,
            R.drawable.ra_mascot,
            R.drawable.wb_mascot,
            R.drawable.d_mascot
    };
    private RecyclerView directoryRecyclerView;
    private FlexibleAdapter<DirectoryItem> directoryAdapter;

    public DirectoryFragment() {
        // Required empty public constructor
    }

    @DrawableRes
    public static int getDrawableResourceForDataSource(DataSource dataSource) {
        switch (dataSource) {
            case DISTRICT:
                return R.drawable.d_mascot;
            case HIGH_SCHOOL:
                return R.drawable.hs_mascot;
            case HEIGHTS_MIDDLE_SCHOOL:
                return R.drawable.d_mascot;
            case HOLMAN_MIDDLE_SCHOOL:
                return R.drawable.d_mascot;
            case REMINGTON_TRADITIONAL_SCHOOL:
                return R.drawable.rm_mascot;
            case BRIDGEWAY_ELEMENTARY:
                return R.drawable.br_mascot;
            case DRUMMOND_ELEMENTARY:
                return R.drawable.dr_mascot;
            case ROSE_ACRES_ELEMENTARY:
                return R.drawable.ra_mascot;
            case PARKWOOD_ELEMENTARY:
                return R.drawable.pw_mascot;
            case WILLOW_BROOK_ELEMENTARY:
                return R.drawable.wb_mascot;
            default:
                return R.drawable.d_mascot;
        }
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
                .sortBy(dataSource -> dataSource.name)
                .sortBy(dataSource -> {
                    if (!dataSource.isDisableable)
                        return 0;
                    else if (dataSource.isHighSchool)
                        return 1;
                    else if (dataSource.isMiddleSchool)
                        return 2;
                    else if (dataSource.isElementarySchool)
                        return 3;
                    else
                        return 4;
                })
                .map(dataSource -> new DirectoryItem(Collections.singleton(dataSource)))
                .forEach(directoryItem -> directoryAdapter.addItem(directoryItem));


        return layout;
    }
}
