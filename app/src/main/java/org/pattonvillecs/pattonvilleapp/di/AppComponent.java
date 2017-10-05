package org.pattonvillecs.pattonvilleapp.di;

import android.app.Application;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjectionModule;
import dagger.android.AndroidInjector;
import dagger.android.support.DaggerApplication;

/**
 * Created by Mitchell on 10/3/2017.
 */

@Singleton
@Component(modules = {
        AppModule.class,
        FragmentBuilderModule.class,
        AndroidInjectionModule.class
})
public interface AppComponent extends AndroidInjector<DaggerApplication> {
    @Override
    void inject(DaggerApplication instance);


    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder application(Application application);

        AppComponent build();
    }
}
