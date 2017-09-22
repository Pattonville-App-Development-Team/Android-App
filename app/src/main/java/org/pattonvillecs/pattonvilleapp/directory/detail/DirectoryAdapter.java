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

package org.pattonvillecs.pattonvilleapp.directory.detail;

import android.support.annotation.Nullable;

import org.apache.commons.text.WordUtils;

import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.IFlexible;

/**
 * Created by gadsonk on 1/20/17.
 */

public class DirectoryAdapter<T extends IFlexible> extends FlexibleAdapter<T> {
    private final boolean useLongDesc;

    public DirectoryAdapter(@Nullable List<T> items, boolean useLongDesc) {
        super(items);
        this.useLongDesc = useLongDesc;
    }

    @Override
    public String onCreateBubbleText(int position) {
        T item = getItem(position);
        if (item instanceof Faculty) {
            Faculty faculty = (Faculty) item;

            if (useLongDesc)
                return WordUtils.capitalizeFully(faculty.getLongDesc());
            else
                return faculty.getDirectoryKey().name;
        } else
            return "";
    }
}
