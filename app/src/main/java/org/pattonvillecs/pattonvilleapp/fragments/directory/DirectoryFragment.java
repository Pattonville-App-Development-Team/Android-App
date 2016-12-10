package org.pattonvillecs.pattonvilleapp.fragments.directory;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.annimon.stream.function.Function;

import org.pattonvillecs.pattonvilleapp.DataSource;
import org.pattonvillecs.pattonvilleapp.R;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DirectoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DirectoryFragment extends Fragment implements AdapterView.OnItemClickListener {
    private ListView mListView;
    private ArrayAdapter<String> listAdapter;
    private List<DataSource> schools;

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

        schools = Stream.of(DataSource.SCHOOLS)
                .sortBy(new Function<DataSource, String>() {
                    @Override
                    public String apply(DataSource dataSource) {
                        return dataSource.name;
                    }
                }).sortBy(new Function<DataSource, Integer>() {
                    @Override
                    public Integer apply(DataSource dataSource) {
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
                    }
                }).collect(Collectors.<DataSource>toList());

        listAdapter = new ArrayAdapter<>(mListView.getContext(),
                android.R.layout.simple_list_item_1,
                Stream.of(schools).map(new Function<DataSource, String>() {
                    @Override
                    public String apply(DataSource dataSource) {
                        return dataSource.name;
                    }
                }).collect(Collectors.<String>toList()));
        mListView.setAdapter(listAdapter);
        mListView.setOnItemClickListener(this);

        return layout;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        Intent intent = new Intent(getContext(), DirectoryDetailActivity.class);
        intent.putExtra("School", schools.get(position));

        Log.e("DIRECTORY", "WE GOT HERE :)");
        startActivity(intent);
        //how does this interact with the DataSource class?
    }
}
