package org.pattonvillecs.pattonvilleapp.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.pattonvillecs.pattonvilleapp.R;

import java.util.Arrays;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DirectoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DirectoryFragment extends Fragment {
    private ListView mListView;
    private ArrayAdapter<String> listAdapter;

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
        mListView = (ListView) layout.findViewById(R.id.list_view_directory);

        String[] schools = this.getResources().getStringArray(R.array.schools);
        //new String[]{"Pattonville School District", "Pattonville High School", "Heights Middle School", "Holman Middle School", "Remington Traditional School", "Bridgeway Elementary School", "Drummond Elementary School", "Parkwood Elementary School", "Rose Acres Elementary School", "Willow Brook Elementary School", "Special School District"};
        //ArrayList<String> schoolsList = new ArrayList<String>();
        //schoolsList.addAll(Arrays.asList(schools));

        listAdapter = new ArrayAdapter<>(mListView.getContext(),
                android.R.layout.simple_list_item_1, Arrays.asList(schools));
        mListView.setAdapter(listAdapter);

        return layout;
    }

}
