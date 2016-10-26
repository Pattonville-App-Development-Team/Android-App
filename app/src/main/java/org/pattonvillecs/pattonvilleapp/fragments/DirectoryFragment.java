package org.pattonvillecs.pattonvilleapp.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.pattonvillecs.pattonvilleapp.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DirectoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DirectoryFragment extends Fragment {

    public DirectoryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment DirectoryFragment.
     */
    // TODO: Rename and change types and number of parameters
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
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_directory, container, false);

        return layout;
    }

}
