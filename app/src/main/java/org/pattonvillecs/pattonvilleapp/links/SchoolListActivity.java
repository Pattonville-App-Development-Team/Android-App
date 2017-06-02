/*
 * Copyright (C) 2017  Mitchell Skaggs, Keturah Gadson, Ethan Holtgrieve, and Nathan Skelton
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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

    private final static String INTENT_IS_PEACHJAR = "isPeachjar";

    private boolean isPeachjar;
    private RecyclerView schoolLinksRecyclerView;
    private FlexibleAdapter<SchoolLinkItem> schoolLinksAdapter;

    public static Intent newInstance(Context context, boolean peachjar) {
        return new Intent(context, SchoolListActivity.class).putExtra(INTENT_IS_PEACHJAR, peachjar);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_school_links_list);

        isPeachjar = getIntent().getBooleanExtra(INTENT_IS_PEACHJAR, false);

        // Set title appropriately
        if (isPeachjar) {
            setTitle(R.string.title_activity_peachjar);
        } else {
            setTitle(R.string.title_activity_nutrislice);
        }

        List<SchoolLinkItem> schools = Stream.of(DataSource.ALL)
                .filter(dataSource -> isPeachjar ? dataSource.peachjarLink.isPresent() : dataSource.nutrisliceLink.isPresent())
                .sorted(DataSource.DEFAULT_ORDERING)
                .map(dataSource -> isPeachjar ? new PeachjarItem(dataSource) : new NutrisliceItem(dataSource))
                .collect(Collectors.toList());

        schoolLinksRecyclerView = (RecyclerView) findViewById(R.id.school_links_recyclerview);
        schoolLinksRecyclerView.setLayoutManager(new SmoothScrollLinearLayoutManager(this));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        schoolLinksRecyclerView.addItemDecoration(dividerItemDecoration);

        schoolLinksAdapter = new FlexibleAdapter<>(schools);
        schoolLinksRecyclerView.setAdapter(schoolLinksAdapter);
    }
}

