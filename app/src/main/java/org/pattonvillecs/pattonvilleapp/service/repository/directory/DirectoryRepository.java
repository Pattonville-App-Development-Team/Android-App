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

package org.pattonvillecs.pattonvilleapp.service.repository.directory;

import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import org.pattonvillecs.pattonvilleapp.service.model.DataSource;
import org.pattonvillecs.pattonvilleapp.service.model.directory.Directory;
import org.pattonvillecs.pattonvilleapp.service.model.directory.Faculty;
import org.pattonvillecs.pattonvilleapp.service.repository.AppDatabase;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import static java.util.Collections.singletonList;

/**
 * This class is the repository for the directory. It provides access to and helper overloads for the {@link DirectoryDao} object.
 *
 * @author Mitchell Skaggs
 * @since 1.3.0
 */

@Singleton
public class DirectoryRepository {
    @NonNull
    private final DirectoryDao directoryDao;

    @Inject
    public DirectoryRepository(@NonNull AppDatabase appDatabase) {
        this.directoryDao = appDatabase.directoryDao();
    }

    @NonNull
    public LiveData<List<Faculty>> getFacultyFromLocations(@NonNull List<DataSource> dataSources) {
        return directoryDao.getFacultyFromLocations(dataSources);
    }

    @NonNull
    public LiveData<List<Faculty>> getFacultyFromLocations(@NonNull DataSource dataSource) {
        return directoryDao.getFacultyFromLocations(singletonList(dataSource));
    }

    public void upsertAll(@NonNull List<Faculty> faculty) {
        directoryDao.upsertAll(faculty);
    }

    public void upsertAll(@NonNull Directory directory) {
        upsertAll(directory.getFaculty());
    }
}
