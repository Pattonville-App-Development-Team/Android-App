package org.pattonvillecs.pattonvilleapp.di;

import org.pattonvillecs.pattonvilleapp.di.database.AppDatabaseModule;

import dagger.Module;

/**
 * Created by Mitchell on 10/3/2017.
 */

@Module(includes = {AppDatabaseModule.class})
public class AppModule {
}
