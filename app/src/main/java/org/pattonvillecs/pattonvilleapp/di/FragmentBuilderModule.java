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