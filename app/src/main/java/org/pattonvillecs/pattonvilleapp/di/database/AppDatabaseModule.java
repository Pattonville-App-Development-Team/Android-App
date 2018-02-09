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

package org.pattonvillecs.pattonvilleapp.di.database;

import android.app.Application;
import android.arch.persistence.room.Room;

import org.pattonvillecs.pattonvilleapp.di.AppModule;
import org.pattonvillecs.pattonvilleapp.service.repository.AppDatabase;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

import static org.pattonvillecs.pattonvilleapp.service.repository.AppDatabase.init;

/**
 * Created by Mitchell on 10/4/2017.
 */

@Module(includes = AppModule.class)
public class AppDatabaseModule {
    @Provides
    @Singleton
    static AppDatabase provideAppDatabase(Application application) {
        return init(Room.databaseBuilder(application, AppDatabase.class, "app_database")).build();
    }
}
