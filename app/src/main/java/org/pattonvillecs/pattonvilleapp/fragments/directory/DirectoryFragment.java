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
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.annimon.stream.function.Function;

import org.pattonvillecs.pattonvilleapp.DataSource;
import org.pattonvillecs.pattonvilleapp.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DirectoryFragment extends Fragment implements AdapterView.OnItemClickListener {
    public static int[] images = {
            R.drawable.d_mascot,
            R.drawable.d_mascot,
            R.drawable.d_mascot,
            R.drawable.d_mascot,
            R.drawable.rm_mascot,
            R.drawable.br_mascot,
            R.drawable.dr_mascot,
            R.drawable.pw_mascot,
            R.drawable.ra_mascot,
            R.drawable.wb_mascot
    };
    private ListView mListView;
    private List<DataSource> schools;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_directory, container, false);
        mListView = (ListView) layout.findViewById(R.id.list_view_directory);

        schools = Stream.of(DataSource.ALL)
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

        List<HashMap<String, String>> homeNewsList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {

            HashMap<String, String> newsListItem = new HashMap<>();
            newsListItem.put("image", Integer.toString(images[i]));
            newsListItem.put("headline", schools.get(i).name);
            homeNewsList.add(newsListItem);
        }
        String[] homeNewsListFrom = {"image", "headline"};

        int[] homeNewsListTo = {R.id.schools_listview_item_imageView, R.id.schools_listview_item_textView};


        SimpleAdapter newsListSimpleAdapter = new SimpleAdapter(layout.getContext(), homeNewsList, R.layout.schools_listview_item, homeNewsListFrom, homeNewsListTo);
        mListView.setAdapter(newsListSimpleAdapter);
        mListView.setOnItemClickListener(this);

        return layout;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        Intent intent = new Intent(getContext(), DirectoryDetailActivity.class);
        intent.putExtra(DirectoryDetailActivity.KEY_DATASOURCE, schools.get(position));

        Log.d("DIRECTORY", "WE GOT HERE :)");
        startActivity(intent);
    }
}
