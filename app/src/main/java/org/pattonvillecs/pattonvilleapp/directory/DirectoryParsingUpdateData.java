package org.pattonvillecs.pattonvilleapp.directory;

import org.pattonvillecs.pattonvilleapp.DataSource;
import org.pattonvillecs.pattonvilleapp.directory.detail.Faculty;

import java.util.List;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by skaggsm on 2/10/17.
 */

public class DirectoryParsingUpdateData {
    public static final int DIRECTORY_LISTENER_ID = 512982357;

    private final ConcurrentMap<DataSource, List<Faculty>> directoryData;

    public DirectoryParsingUpdateData(ConcurrentMap<DataSource, List<Faculty>> directoryData) {
        this.directoryData = directoryData;
    }

    public ConcurrentMap<DataSource, List<Faculty>> getDirectoryData() {
        return directoryData;
    }

    //TODO: Store relevant data for updating listeners (The current list or something). Also store the list of running DirectoryAsyncTasks
}
