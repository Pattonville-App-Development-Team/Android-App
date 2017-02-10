package org.pattonvillecs.pattonvilleapp.fragments.directory;

import android.os.AsyncTask;

import org.pattonvillecs.pattonvilleapp.Faculty;
import org.pattonvillecs.pattonvilleapp.PattonvilleApplication;

import java.util.List;

/**
 * Created by skaggsm on 2/10/17.
 */

public class DirectoryAsyncTask extends AsyncTask<Void, Double, List<Faculty>> {
    private final PattonvilleApplication pattonvilleApplication;

    public DirectoryAsyncTask(PattonvilleApplication pattonvilleApplication) {
        this.pattonvilleApplication = pattonvilleApplication;
    }

    @Override
    protected void onPreExecute() { // Run on UI thread
        super.onPreExecute();
        //TODO Add to list of running DirectoryAsyncTasks
    }

    @Override
    protected List<Faculty> doInBackground(Void... params) { // Run on different thread

        //TODO: Load csv file

        //Read file in here
        //i.e. publishProgress(currentLine / (double) totalLines);

        return null;
    }

    @Override
    protected void onProgressUpdate(Double... values) { // Run on UI thread
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(List<Faculty> faculties) { // Run on UI thread
        super.onPostExecute(faculties);

        //TODO: Save the loaded data to the PattonvilleApplication instance, call update method, remove from list of running DirectoryAsyncTasks
    }

    @Override
    protected void onCancelled(List<Faculty> faculties) {
        super.onCancelled(faculties);

        //TODO Handle if the application closes during
    }
}
