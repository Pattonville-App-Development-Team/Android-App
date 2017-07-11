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

import android.support.annotation.CallSuper;

/**
 * Created by Mitchell Skaggs on 2/2/17.
 */

public abstract class PauseableListener<E> {

    protected PauseableListenable listenable = null;
    protected boolean paused;

    public PauseableListener() {
        this(false);
    }

    public PauseableListener(boolean startsPaused) {
        this.paused = startsPaused;
    }

    /**
     * Used to establish a two-way link between listener and listened.
     *
     * @param listenable the new listenable to attach
     * @return the prior attached listenable or {@code null} if none
     */
    public PauseableListenable attach(PauseableListenable listenable) {
        PauseableListenable pauseableListenable = this.listenable;
        this.listenable = listenable;
        return pauseableListenable;
    }

    /**
     * Used to sever a two-way link between listener and listened. Clears the currently attached listenable.
     *
     * @return the prior attached listenable or {@code null} if none
     */
    public PauseableListenable unattach() {
        PauseableListenable pauseableListenable = this.listenable;
        this.listenable = null;
        return pauseableListenable;
    }

    /**
     * Used to determine how to handle this listener.
     *
     * @return a unique {@code long} that determines the response of the listened.
     */
    public abstract int getIdentifier();

    /**
     * Called whenever new data is available <em>and</em> the listener is not paused.
     *
     * @param data
     */
    @CallSuper
    public void onReceiveData(E data) {
        checkAttachedToListenable();
    }

    private void checkAttachedToListenable() {
        if (listenable == null)
            throw new IllegalStateException("attach(...) must be called before use!");
    }

    public final void pause() {
        checkAttachedToListenable();
        this.paused = true;
        listenable.pause(this);
    }

    public final void resume() {
        checkAttachedToListenable();
        this.paused = false;
        listenable.resume(this);
    }

    /**
     * Called when the listener is about to pause.
     *
     * @param data The final data to give.
     */
    @CallSuper
    public void onPause(E data) {
        checkAttachedToListenable();
    }

    /**
     * Called as soon as the listener is resumed.
     *
     * @param data The current data at the time of resume.
     */
    @CallSuper
    public void onResume(E data) {
        checkAttachedToListenable();
    }

    public boolean isPaused() {
        return paused;
    }
}
