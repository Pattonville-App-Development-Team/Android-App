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

package org.pattonvillecs.pattonvilleapp.directory;

import org.pattonvillecs.pattonvilleapp.DataSource;
import org.pattonvillecs.pattonvilleapp.directory.detail.Faculty;

import java.util.List;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by skaggsm on 2/10/17.
 */

public class DirectoryParsingUpdateData {
    public static final int DIRECTORY_LISTENER_ID = 512982357;

    private final ConcurrentMap<DataSource, List<Faculty>> directoryData;

    public DirectoryParsingUpdateData(ConcurrentMap<DataSource, List<Faculty>> directoryData) {
        this.directoryData = directoryData;
    }

    public ConcurrentMap<DataSource, List<Faculty>> getDirectoryData() {
        return directoryData;
    }

    //TODO: Store relevant data for updating listeners (The current list or something). Also store the list of running DirectoryAsyncTasks
}
