/*
 * Copyright (C) 2017 - 2018 Mitchell Skaggs, Keturah Gadson, Ethan Holtgrieve, Nathan Skelton, Pattonville School District
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

package org.pattonvillecs.pattonvilleapp.di;

import org.pattonvillecs.pattonvilleapp.di.scopes.PerActivity;
import org.pattonvillecs.pattonvilleapp.view.ui.MainActivity;
import org.pattonvillecs.pattonvilleapp.view.ui.about.detail.AboutDetailActivity;
import org.pattonvillecs.pattonvilleapp.view.ui.calendar.details.CalendarEventDetailsActivity;
import org.pattonvillecs.pattonvilleapp.view.ui.directory.detail.all.AllDataSourcesDirectoryDetailActivity;
import org.pattonvillecs.pattonvilleapp.view.ui.directory.detail.single.SingleDataSourceDirectoryDetailActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * Created by Mitchell Skaggs on 11/22/2017.
 *
 * @since 1.2.0
 */

@Module
abstract class ActivityBuilderModule {
    @ContributesAndroidInjector
    @PerActivity
    abstract CalendarEventDetailsActivity contributeCalendarEventDetailsActivity();

    @ContributesAndroidInjector
    @PerActivity
    abstract MainActivity contributeMainActivity();

    @ContributesAndroidInjector
    @PerActivity
    abstract AboutDetailActivity contributeAboutDetailActivity();

    @ContributesAndroidInjector
    @PerActivity
    abstract AllDataSourcesDirectoryDetailActivity contributeAllDataSourcesDirectoryDetailActivity();

    @ContributesAndroidInjector
    @PerActivity
    abstract SingleDataSourceDirectoryDetailActivity contributeSingleDataSourceDirectoryDetailActivity();
}
