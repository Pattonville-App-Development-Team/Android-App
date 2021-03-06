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

package org.pattonvillecs.pattonvilleapp.service.repository.news

import com.github.magneticflux.rss.namespaces.standard.elements.Rss
import com.google.common.util.concurrent.ListenableFuture
import org.pattonvillecs.pattonvilleapp.service.model.DataSource
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Created by Mitchell Skaggs on 12/14/2017.
 */
interface NewsRetrofitService {
    @GET("{datasource}/news/?plugin=xml&leaves")
    fun getRss(@Path(value = "datasource") dataSource: DataSource): ListenableFuture<Rss>

    @GET("{datasource}/news/{id}")
    fun getArticle(@Path(value = "datasource") dataSource: DataSource, @Path(value = "id") id: String): ListenableFuture<Rss>
}