package org.pattonvillecs.pattonvilleapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.annimon.stream.function.Function;

import org.pattonvillecs.pattonvilleapp.fragments.directory.DirectoryFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by gadsonk on 2/8/17.
 */

public class SchoolListActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private List<DataSource> schools;
    private ListView mListView;

    private boolean peachjar;

    public static Intent newInstance(Context context, boolean peachjar) {

        Intent intent = new Intent(context, SchoolListActivity.class);
        intent.putExtra("peachjar", peachjar);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_school_list);
        setTitle("Nutrislice Links");

        peachjar = getIntent().getBooleanExtra("peachjar", false);

        mListView = (ListView) findViewById(R.id.school_list_view);

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

        List<HashMap<String, String>> homeNewsList = new ArrayList<>();
        for (int i = 0; i < 9; i++) {

            HashMap<String, String> newsListItem = new HashMap<String, String>();
            newsListItem.put("image", Integer.toString(DirectoryFragment.images[schools.get(i).id]));
            newsListItem.put("headline", schools.get(i).name);
            homeNewsList.add(newsListItem);
        }
        String[] homeNewsListFrom = {"image", "headline"};

        int[] homeNewsListTo = {R.id.schools_listview_item_imageView, R.id.schools_listview_item_textView};


        SimpleAdapter newsListSimpleAdapter = new SimpleAdapter(this, homeNewsList, R.layout.schools_listview_item, homeNewsListFrom, homeNewsListTo);
        mListView.setAdapter(newsListSimpleAdapter);
        mListView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

        if (peachjar) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(schools.get(position).peachjarLink.get())));
        } else {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(schools.get(position).nutrisliceLink.get())));
        }
    }
}

