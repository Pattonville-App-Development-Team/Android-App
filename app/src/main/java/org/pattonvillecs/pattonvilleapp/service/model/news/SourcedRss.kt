/*
 * Copyright (C) 2018 Mitchell Skaggs, Keturah Gadson, Ethan Holtgrieve, Nathan Skelton, Pattonville School District
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

package org.pattonvillecs.pattonvilleapp.service.model.news

import com.github.magneticflux.rss.namespaces.standard.elements.Rss
import org.pattonvillecs.pattonvilleapp.DataSource

/**
 * Created by Mitchell Skaggs on 1/15/2018.
 *
 * @since 1.4.0
 */
data class SourcedRss(val dataSource: DataSource, val rss: Rss)