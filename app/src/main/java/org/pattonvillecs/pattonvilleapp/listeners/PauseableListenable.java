package org.pattonvillecs.pattonvilleapp.listeners;

/**
 * Created by skaggsm on 2/2/17.
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
}
