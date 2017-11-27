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

import android.app.Application;

import org.pattonvillecs.pattonvilleapp.PattonvilleApplication;
import org.pattonvillecs.pattonvilleapp.di.database.AppDatabaseModule;
import org.pattonvillecs.pattonvilleapp.di.job.FirebaseJobDispatcherModule;
import org.pattonvillecs.pattonvilleapp.di.network.CalendarRetrofitServiceModule;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjectionModule;
import dagger.android.AndroidInjector;

/**
 * Created by Mitchell on 10/3/2017.
 */

@Singleton
@Component(modules = {
        AppDatabaseModule.class,
        CalendarRetrofitServiceModule.class,
        FirebaseJobDispatcherModule.class,
        ActivityBuilderModule.class,
        FragmentBuilderModule.class,
        ChildFragmentBuilderModule.class,
        ServiceBuilderModule.class,
        AndroidInjectionModule.class
})
public interface AppComponent extends AndroidInjector<PattonvilleApplication> {
    @Override
    void inject(PattonvilleApplication instance);


    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder application(Application application);

        AppComponent build();
    }
}
