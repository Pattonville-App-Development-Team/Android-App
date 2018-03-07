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

import com.google.common.util.concurrent.ListenableFuture;

import org.pattonvillecs.pattonvilleapp.service.model.directory.Directory;

import retrofit2.http.GET;

/**
 * Created by Mitchell Skaggs on 12/8/2017.
 */
public interface DirectoryRetrofitService {
    @GET("directory.csv")
    ListenableFuture<Directory> getDirectory();
}
