package org.pattonvillecs.pattonvilleapp.fragments.directory;

import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.util.Log;

import org.pattonvillecs.pattonvilleapp.DataSource;
import org.pattonvillecs.pattonvilleapp.PattonvilleApplication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by skaggsm on 2/10/17.
 */

public class DirectoryAsyncTask extends AsyncTask<Void, Void, Map<DataSource, List<Faculty>>> {
    private static final String TAG = DirectoryAsyncTask.class.getSimpleName();
    private final PattonvilleApplication pattonvilleApplication;
    private DataSource dataSource;

    public DirectoryAsyncTask(PattonvilleApplication pattonvilleApplication) {
        this.pattonvilleApplication = pattonvilleApplication;
    }

    @Override
    protected void onPreExecute() { // Run on UI thread
        super.onPreExecute();
        this.pattonvilleApplication.getRunningDirectoryAsyncTasks().add(this);
    }

    @Override
    protected Map<DataSource, List<Faculty>> doInBackground(Void... params) { // Run on different thread
        Map<DataSource, List<Faculty>> faculties = new HashMap<>();
        AssetManager man = pattonvilleApplication.getResources().getAssets();

        try {
            InputStream ims = man.open("Student_Directory.csv");
            BufferedReader reader = new BufferedReader(new InputStreamReader(ims));
            Log.d(TAG, reader.toString());
            String directoryLine;
            reader.readLine();
            while ((directoryLine = reader.readLine()) != null) {
                String[] person = directoryLine.split("\\s*,\\s*", -1);
                Faculty facultyMember = new Faculty(person);
                Log.v(TAG, "Current line: " + Arrays.toString(person) + person.length);
                DataSource data = facultyMember.getDirectoryKey();
                if (!faculties.containsKey(data)) {
                    faculties.put(data, new ArrayList<Faculty>());
                }
                faculties.get(data).add(facultyMember);
            }
        } catch (IOException e) {
            Log.e(TAG, "Error loading directory!", e);
        }

        return faculties;
    }

    @Override
    protected void onPostExecute(Map<DataSource, List<Faculty>> result) { // Run on UI thread
        super.onPostExecute(result);

        if (result != null) {
            for (DataSource key : result.keySet()) {
                Collections.sort(result.get(key), new Comparator<Faculty>() {
                    @Override
                    public int compare(Faculty o1, Faculty o2) {
                        if (o1.getRank() == o2.getRank()) {
                            if (o1.getLongDesc().equals(o2.getLongDesc())) {
                                if (o1.getLastName().equals(o2.getLastName())) {
                                    return o1.getFirstName().compareTo(o2.getFirstName());
                                } else {
                                    return o1.getLastName().compareTo(o2.getLastName());
                                }
                            } else {
                                return o1.getLongDesc().compareTo(o2.getLongDesc());
                            }
                        } else {
                            return o1.getRank() - o2.getRank();
                        }
                    }
                });
            }
            pattonvilleApplication.getDirectoryData().putAll(result);
        }

        this.pattonvilleApplication.getRunningDirectoryAsyncTasks().remove(this);
        this.pattonvilleApplication.updateDirectoryListeners();
    }

    @Override
    protected void onCancelled(Map<DataSource, List<Faculty>> faculties) {
        super.onCancelled(faculties);

        //TODO Handle if the application closes during
    }
}