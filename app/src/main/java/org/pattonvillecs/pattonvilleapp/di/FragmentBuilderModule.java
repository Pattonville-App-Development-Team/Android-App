package org.pattonvillecs.pattonvilleapp.di;

import org.pattonvillecs.pattonvilleapp.calendar.CalendarFragment;
import org.pattonvillecs.pattonvilleapp.di.scopes.PerFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * Created by Mitchell Skaggs on 11/22/2017.
 */

@Module
public abstract class FragmentBuilderModule {
    @ContributesAndroidInjector
    @PerFragment
    abstract CalendarFragment contributeCalendarFragment();
}
