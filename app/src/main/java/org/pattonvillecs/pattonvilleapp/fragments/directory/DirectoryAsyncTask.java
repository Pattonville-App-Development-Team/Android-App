package org.pattonvillecs.pattonvilleapp.fragments.directory;

import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.util.Log;

import org.pattonvillecs.pattonvilleapp.DataSource;
import org.pattonvillecs.pattonvilleapp.Faculty;
import org.pattonvillecs.pattonvilleapp.PattonvilleApplication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by skaggsm on 2/10/17.
 */

public class DirectoryAsyncTask extends AsyncTask<DataSource, Void, Map<DataSource, List<Faculty>>> {
    private static final String TAG = DirectoryAsyncTask.class.getSimpleName();
    private final PattonvilleApplication pattonvilleApplication;
    private DataSource dataSource;

    public DirectoryAsyncTask(PattonvilleApplication pattonvilleApplication) {
        this.pattonvilleApplication = pattonvilleApplication;
    }

    @Override
    protected void onPreExecute() { // Run on UI thread
        super.onPreExecute();
        //TODO Add to list of running DirectoryAsyncTasks
    }

    @Override
    protected Map<DataSource, List<Faculty>> doInBackground(DataSource... params) { // Run on different thread


        Map<DataSource, List<Faculty>> faculties = new HashMap<>();

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
                DataSource data = getDataSourceFromString(person[3]);
                if (!faculties.containsKey(data)) {
                    faculties.put(data, new ArrayList<Faculty>());
                }

                faculties.get(data).add(new Faculty(person));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return faculties;
    }

    private DataSource getDataSourceFromString(String location) {
        DataSource data = null;
        switch (location.trim().toUpperCase()) {
            case "BRIDGEWAY ELEMENTARY":
                data = DataSource.BRIDGEWAY_ELEMENTARY;
                break;
            case "ROBERT DRUMMOND ELEMENTARY":
                data = DataSource.DRUMMOND_ELEMENTARY;
                break;
            case "HOLMAN MIDDLE SCHOOL":
                data = DataSource.HOLMAN_MIDDLE_SCHOOL;
                break;
            case "PATTONVILLE HEIGHTS":
                data = DataSource.HEIGHTS_MIDDLE_SCHOOL;
                break;
            case "PARKWOOD ELEMENTARY":
                data = DataSource.PARKWOOD_ELEMENTARY;
                break;
            case "ROSE ACRES ELEMENTARY":
                data = DataSource.ROSE_ACRES_ELEMENTARY;
                break;
            case "REMINGTON TRADITIONAL":
                data = DataSource.REMINGTON_TRADITIONAL_SCHOOL;
                break;
            case "PATTONVILLE HIGH SCHOOL":
            case "POSITIVE SCHOOL":
                data = DataSource.HIGH_SCHOOL;
                break;
            case "WILLOW BROOK ELEMENTARY":
                data = DataSource.WILLOW_BROOK_ELEMENTARY;
                break;
            case "LEARNING CENTER":
            default:
                data = DataSource.DISTRICT;
        }
        return data;
    }

    @Override
    protected void onPostExecute(Map<DataSource, List<Faculty>> result) { // Run on UI thread
        super.onPostExecute(result);

        if (result != null) {
            pattonvilleApplication.getDirectoryData().putAll(result);
        }

        pattonvilleApplication.updateDirectoryListeners();

        //TODO: Save the loaded data to the PattonvilleApplication instance, call update method, remove from list of running DirectoryAsyncTasks
    }

    @Override
    protected void onCancelled(Map<DataSource, List<Faculty>> faculties) {
        super.onCancelled(faculties);

        //TODO Handle if the application closes during
    }
}