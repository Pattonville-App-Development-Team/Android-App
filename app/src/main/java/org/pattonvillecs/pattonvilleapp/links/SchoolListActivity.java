package org.pattonvillecs.pattonvilleapp.links;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;

import org.pattonvillecs.pattonvilleapp.DataSource;
import org.pattonvillecs.pattonvilleapp.R;

import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.common.SmoothScrollLinearLayoutManager;

/**
 * Activity to handle links for Peachjar and Nutrislice services
 *
 * @author Nathan Skelton
 */
public class SchoolListActivity extends AppCompatActivity {

    private final static String INTENT_PEACHJAR = "peachjar";

    private boolean peachjar;
    private RecyclerView schoolLinksRecyclerView;
    private FlexibleAdapter<SchoolLinkItem> schoolLinksAdapter;

    public static Intent newInstance(Context context, boolean peachjar) {
        return new Intent(context, SchoolListActivity.class).putExtra(INTENT_PEACHJAR, peachjar);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_school_links_list);

        peachjar = getIntent().getBooleanExtra(INTENT_PEACHJAR, false);

        // Set title appropriately
        if (peachjar) {
            setTitle(R.string.title_activity_peachjar);
        } else {
            setTitle(R.string.title_activity_nutrislice);
        }

        List<SchoolLinkItem> schools = Stream.of(DataSource.ALL)
                .filter(dataSource -> peachjar ? dataSource.peachjarLink.isPresent() : dataSource.nutrisliceLink.isPresent())
                .sorted(DataSource.DEFAULT_ORDERING)
                .map(dataSource -> {
                    if (peachjar)
                        return new PeachjarItem(dataSource);
                    else
                        return new NutrisliceItem(dataSource);
                })
                .collect(Collectors.toList());

        schoolLinksRecyclerView = (RecyclerView) findViewById(R.id.school_links_recyclerview);
        schoolLinksRecyclerView.setLayoutManager(new SmoothScrollLinearLayoutManager(this));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        schoolLinksRecyclerView.addItemDecoration(dividerItemDecoration);

        schoolLinksAdapter = new FlexibleAdapter<>(schools);
        schoolLinksRecyclerView.setAdapter(schoolLinksAdapter);

        // --- REFACTOR LINE ---

        /*
        List<HashMap<String, String>> homeNewsList = new ArrayList<>();
        for (int i = 0; i < 9; i++) {

            HashMap<String, String> newsListItem = new HashMap<>();
            newsListItem.put("image", Integer.toString(DirectoryFragment.getDrawableResourceForDataSource(schools.get(i))));
            newsListItem.put("school", schools.get(i).name);
            homeNewsList.add(newsListItem);
        }
        String[] homeNewsListFrom = {"image", "school"};

        int[] homeNewsListTo = {R.id.schools_listview_item_imageView, R.id.schools_listview_item_textView};

        ListView listView = (ListView) findViewById(R.id.school_list_view);
        listView.setAdapter(new SimpleAdapter(this, homeNewsList, R.layout.school_links_list_item,
                homeNewsListFrom, homeNewsListTo));
        listView.setOnItemClickListener(this);
        */
    }
}

