package org.pattonvillecs.pattonvilleapp.fragments.directory;

import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.util.Log;

import org.pattonvillecs.pattonvilleapp.Faculty;
import org.pattonvillecs.pattonvilleapp.PattonvilleApplication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by skaggsm on 2/10/17.
 */

public class DirectoryAsyncTask extends AsyncTask<Void, Double, List<Faculty>> {
    private static final String TAG = DirectoryAsyncTask.class.getSimpleName();
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
        List<Faculty> faculties = new ArrayList<>();

        //TODO: Load csv file
        AssetManager man = pattonvilleApplication.getResources().getAssets();
        try {
            InputStream ims = man.open("Student_Directory.csv");
            BufferedReader reader = new BufferedReader(new InputStreamReader(ims));
            Log.i(TAG, reader.toString());
            String directoryLine = reader.readLine();
            while ((directoryLine = reader.readLine()) != null) {
                String[] person = directoryLine.split("\\s*,\\s*", -1);
                Log.i(TAG, Arrays.toString(person) + person.length);
                faculties.add(new Faculty(person));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return faculties;
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