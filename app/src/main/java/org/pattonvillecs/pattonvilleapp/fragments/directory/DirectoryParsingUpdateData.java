package org.pattonvillecs.pattonvilleapp.fragments.directory;

import org.pattonvillecs.pattonvilleapp.DataSource;
import org.pattonvillecs.pattonvilleapp.Faculty;

import java.util.List;
import java.util.Map;

/**
 * Created by skaggsm on 2/10/17.
 */

public class DirectoryParsingUpdateData {
    public static final int DIRECTORY_LISTENER_ID = 512982357;

    private final Map<DataSource, List<Faculty>> directoryData;

    public DirectoryParsingUpdateData(Map<DataSource, List<Faculty>> directoryData) {
        this.directoryData = directoryData;
    }

    public Map<DataSource, List<Faculty>> getDirectoryData() {
        return directoryData;
    }

    //TODO: Store relevant data for updating listeners (The current list or something). Also store the list of running DirectoryAsyncTasks
}
