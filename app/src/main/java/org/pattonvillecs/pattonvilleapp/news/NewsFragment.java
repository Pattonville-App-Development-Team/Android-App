/*
 * Copyright (C) 2017 Mitchell Skaggs, Keturah Gadson, Ethan Holtgrieve, Nathan Skelton, Pattonville School District
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

package org.pattonvillecs.pattonvilleapp.news;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.annimon.stream.function.Function;

import org.pattonvillecs.pattonvilleapp.DataSource;
import org.pattonvillecs.pattonvilleapp.PattonvilleApplication;
import org.pattonvillecs.pattonvilleapp.R;
import org.pattonvillecs.pattonvilleapp.listeners.PauseableListener;
import org.pattonvillecs.pattonvilleapp.news.articles.NewsArticle;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.common.FlexibleItemDecoration;
import eu.davidea.flexibleadapter.common.SmoothScrollLinearLayoutManager;

import static org.pattonvillecs.pattonvilleapp.view.ui.spotlight.SpotlightHelper.showSpotlight;

/**
 * Fragment used within MainActivity to display the News tab
 *
 * @author Nathan Skelton
 */
public class NewsFragment extends Fragment implements SearchView.OnQueryTextListener {

    private static final String TAG = NewsFragment.class.getSimpleName();

    private SwipeRefreshLayout mRefreshLayout;
    private FlexibleAdapter<NewsArticle> newsArticleAdapter;
    private PauseableListener<NewsParsingUpdateData> listener;
    private PattonvilleApplication pattonvilleApplication;

    private List<NewsArticle> mNewsArticles;

    public NewsFragment() {
    }

    /**
     * Method that provides a new instance of NewsFragment
     *
     * @return A new NewsFragment
     */
    public static NewsFragment newInstance() {
        return new NewsFragment();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().setTitle(R.string.title_fragment_news);
    }

    @Override
    public void onStart() {
        super.onStart();

        listener.attach(pattonvilleApplication);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        pattonvilleApplication = PattonvilleApplication.get(getActivity());

        // Sets up listener for data changes and updates
        listener = new PauseableListener<NewsParsingUpdateData>(true) {
            @Override
            public int getIdentifier() {
                return NewsParsingUpdateData.NEWS_LISTENER_ID;
            }

            @Override
            public void onReceiveData(NewsParsingUpdateData data) {
                super.onReceiveData(data);
                Log.d(TAG, "Received new data!");
                Log.d(TAG, "Size: " + data.getRunningNewsAsyncTasks().size());

                setNewsArticles(data);

                checkRefresh(data);
            }

            @Override
            public void onResume(NewsParsingUpdateData data) {
                super.onResume(data);
                Log.d(TAG, "Received data after resume!");
                Log.d(TAG, "Size: " + data.getRunningNewsAsyncTasks().size());

                setNewsArticles(data);

                checkRefresh(data);
            }

            @Override
            public void onPause(NewsParsingUpdateData data) {
                super.onPause(data);
                Log.d(TAG, "Received data before pause!");
                Log.d(TAG, "Size: " + data.getRunningNewsAsyncTasks().size());

                checkRefresh(data);
            }

            private void setNewsArticles(NewsParsingUpdateData data) {
                List<NewsArticle> newNewsArticles = Stream.of(data.getNewsData())
                        .flatMap(new Function<Map.Entry<DataSource, List<NewsArticle>>, Stream<NewsArticle>>() {
                            @Override
                            public Stream<NewsArticle> apply(Map.Entry<DataSource, List<NewsArticle>> dataSourceListEntry) {
                                return Stream.of(dataSourceListEntry.getValue());
                            }
                        })
                        .sorted((o1, o2) -> -o1.getPublishDate().compareTo(o2.getPublishDate()))
                        .collect(Collectors.toList());

                Log.i(TAG, "Loaded news articles from " + data.getNewsData().keySet() + " " + newNewsArticles.size());
                mNewsArticles = newNewsArticles;
                newsArticleAdapter.updateDataSet(newNewsArticles, true); // Must be an unused list, copy it if needed
                newsArticleAdapter.getRecyclerView().smoothScrollToPosition(0);
            }

            private void checkRefresh(NewsParsingUpdateData data) {
                boolean refresh = data.getRunningNewsAsyncTasks().size() > 0;
                if (refresh && !mRefreshLayout.isRefreshing()) {
                    Log.d(TAG, "Starting refreshing");
                    mRefreshLayout.setRefreshing(true);
                } else if (!refresh && mRefreshLayout.isRefreshing()) {
                    Log.d(TAG, "Ending refreshing");
                    mRefreshLayout.setRefreshing(false);
                }
            }
        };
        pattonvilleApplication.registerPauseableListener(listener);
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_news, container, false);

        newsArticleAdapter = new FlexibleAdapter<>(null);

        RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.news_recyclerView);
        recyclerView.setLayoutManager(new SmoothScrollLinearLayoutManager(getContext()));
        recyclerView.setAdapter(newsArticleAdapter);
        // Adds item divider between elements
        recyclerView.addItemDecoration(new FlexibleItemDecoration(getContext()));

        mRefreshLayout = (SwipeRefreshLayout) root.findViewById(R.id.news_refreshLayout);
        mRefreshLayout.setOnRefreshListener(() -> pattonvilleApplication.hardRefreshNewsData());
        mRefreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimaryDark);

        return root;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        listener.unattach();
        pattonvilleApplication.unregisterPauseableListener(listener);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause called");
        listener.pause();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume called");
        listener.resume();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_news_menu_main, menu);
        initSearchView(menu);

        //This terrifies me...
        final ViewTreeObserver viewTreeObserver = getActivity().getWindow().getDecorView().getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                View menuButton = getActivity().findViewById(R.id.news_menu_refresh);
                // This could be called when the button is not there yet, so we must test for null
                if (menuButton != null) {
                    // Found it! Do what you need with the button
                    showSpotlight(getActivity(), menuButton, "NewsFragment_MenuButtonRefresh", "Tap here to manually check for new news articles. The app periodically updates news on its own.", "Refresh");
                    // Now you can get rid of this listener
                    viewTreeObserver.removeOnGlobalLayoutListener(this);
                }
            }
        });
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                View menuButton = getActivity().findViewById(R.id.news_menu_search);
                // This could be called when the button is not there yet, so we must test for null
                if (menuButton != null) {
                    // Found it! Do what you need with the button
                    showSpotlight(getActivity(), menuButton, "NewsFragment_MenuButtonSearch", "Tap here to search news titles and sources similar to your query.", "Search");
                    // Now you can get rid of this listener
                    viewTreeObserver.removeOnGlobalLayoutListener(this);
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.news_menu_refresh:
                pattonvilleApplication.hardRefreshNewsData();

                Toast.makeText(getContext(), "Refreshing", Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (newsArticleAdapter.hasNewSearchText(newText)) {
            Log.d(TAG, "onQueryTextChange newText: " + newText);
            newsArticleAdapter.setSearchText(newText);
            newsArticleAdapter.filterItems(new ArrayList<>(mNewsArticles), 100L);
        }
        mRefreshLayout.setEnabled(!newsArticleAdapter.hasSearchText());
        return true;
    }

    /**
     * Method to setup the search functionality of the list
     * <p>
     * Refer to the Flexible Adapter documentation, as this is a near replica implementation
     *
     * @param menu Menu object of current options menu
     */
    private void initSearchView(final Menu menu) {
        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        MenuItem searchItem = menu.findItem(R.id.news_menu_search);
        if (searchItem != null) {

            MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
                @Override
                public boolean onMenuItemActionExpand(MenuItem item) {
                    MenuItem listTypeItem = menu.findItem(R.id.news_menu_refresh);
                    if (listTypeItem != null)
                        listTypeItem.setVisible(false);
                    return true;
                }

                @Override
                public boolean onMenuItemActionCollapse(MenuItem item) {
                    MenuItem listTypeItem = menu.findItem(R.id.news_menu_refresh);
                    if (listTypeItem != null)
                        listTypeItem.setVisible(true);
                    return true;
                }
            });

            SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
            searchView.setInputType(InputType.TYPE_TEXT_VARIATION_FILTER);
            searchView.setImeOptions(EditorInfo.IME_ACTION_DONE | EditorInfo.IME_FLAG_NO_FULLSCREEN);
            searchView.setQueryHint(getString(R.string.action_search));
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
            searchView.setOnQueryTextListener(this);
        }
    }
}

