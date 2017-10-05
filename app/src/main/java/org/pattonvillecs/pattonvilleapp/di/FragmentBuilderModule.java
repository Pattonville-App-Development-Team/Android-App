package org.pattonvillecs.pattonvilleapp.di;

import org.pattonvillecs.pattonvilleapp.calendar.CalendarPinnedFragment;
import org.pattonvillecs.pattonvilleapp.di.scopes.PerChildFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * Created by Mitchell on 10/3/2017.
 */
@Module
public abstract class FragmentBuilderModule {

    @ContributesAndroidInjector
    @PerChildFragment
    abstract CalendarPinnedFragment contributeCalendarPinnedFragment();
}