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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Activity to handle links for Peachjar and Nutrislice services
 *
 * @author Nathan Skelton
 */
public class SchoolListActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    public static final int[] IMAGES = {
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
    private final static String INTENT_ARG = "peachjar";

    private List<DataSource> schools;

    private boolean peachjar;

    public static Intent newInstance(Context context, boolean peachjar) {

        Intent intent = new Intent(context, SchoolListActivity.class);
        intent.putExtra(INTENT_ARG, peachjar);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_school_list);

        peachjar = getIntent().getBooleanExtra(INTENT_ARG, false);

        // Set title appropriately
        if (peachjar) {
            setTitle(getString(R.string.title_activity_peachjar));
        } else {
            setTitle(getString(R.string.title_activity_nutrislice));
        }

        schools = Stream.of(DataSource.SCHOOLS)
                .sorted(DataSource.DEFAULT_ORDERING)
                .collect(Collectors.toList());

        List<HashMap<String, String>> homeNewsList = new ArrayList<>();
        for (int i = 0; i < 9; i++) {

            HashMap<String, String> newsListItem = new HashMap<>();
            newsListItem.put("image", Integer.toString(IMAGES[schools.get(i).id]));
            newsListItem.put("school", schools.get(i).name);
            homeNewsList.add(newsListItem);
        }
        String[] homeNewsListFrom = {"image", "school"};

        int[] homeNewsListTo = {R.id.schools_listview_item_imageView, R.id.schools_listview_item_textView};

        ListView listView = (ListView) findViewById(R.id.school_list_view);
        listView.setAdapter(new SimpleAdapter(this, homeNewsList, R.layout.schools_listview_item,
                homeNewsListFrom, homeNewsListTo));
        listView.setOnItemClickListener(this);
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

