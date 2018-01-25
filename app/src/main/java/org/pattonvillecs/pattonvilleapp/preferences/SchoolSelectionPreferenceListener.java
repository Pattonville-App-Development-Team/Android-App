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

package org.pattonvillecs.pattonvilleapp.preferences;

import java.util.Collections;
import java.util.Set;

/**
 * Created by Mitchell Skaggs on 1/26/17.
 */

public abstract class SchoolSelectionPreferenceListener implements OnSharedPreferenceKeyChangedListener {
    private static final Set<String> keys;

    static {
        keys = Collections.singleton(PreferenceUtils.SCHOOL_SELECTION_PREFERENCE_KEY);
    }

    @Override
    public Set<String> getListenedKeys() {
        return keys;
    }
}
