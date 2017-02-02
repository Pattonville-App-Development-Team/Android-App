package org.pattonvillecs.pattonvilleapp.listeners;

import android.support.annotation.CallSuper;

/**
 * Created by skaggsm on 2/2/17.
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
     * Called whenever new data is available <em>and</em> the listener is not paused.
     *
     * @param data
     */
    @CallSuper
    protected void onReceiveData(E data) {
        checkAttachedToListenable();
    }

    private void checkAttachedToListenable() {
        if (listenable == null)
            throw new IllegalStateException("attach(...) must be called before use!");
    }

    public final void pause() {
        checkAttachedToListenable();
        listenable.pause(this);
    }

    public final void resume() {
        checkAttachedToListenable();
        listenable.resume(this);
    }

    /**
     * Called when the listener is about to pause.
     *
     * @param data The final data to give.
     */
    @CallSuper
    protected void onPause(E data) {
        checkAttachedToListenable();
    }

    /**
     * Called as soon as the listener is resumed.
     *
     * @param data The current data at the time of resume.
     */
    @CallSuper
    protected void onResume(E data) {
        checkAttachedToListenable();
    }
}
