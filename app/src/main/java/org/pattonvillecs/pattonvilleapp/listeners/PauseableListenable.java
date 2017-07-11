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

package org.pattonvillecs.pattonvilleapp.listeners;

/**
 * Created by Mitchell Skaggs on 2/2/17.
 */

public interface PauseableListenable {
    /**
     * Must call {@link PauseableListener#onPause(Object data)} to complete the request
     *
     * @param pauseableListener the listener to pause
     */
    void pause(PauseableListener<?> pauseableListener);

    /**
     * Must call {@link PauseableListener#onResume(Object data)} to complete the request
     *
     * @param pauseableListener the listener to resume
     */
    void resume(PauseableListener<?> pauseableListener);

    void registerPauseableListener(PauseableListener<?> pauseableListener);

    void unregisterPauseableListener(PauseableListener<?> pauseableListener);
}
