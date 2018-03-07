/*
 * Copyright (C) 2017 - 2018 Mitchell Skaggs, Keturah Gadson, Ethan Holtgrieve, Nathan Skelton, Pattonville School District
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.pattonvillecs.pattonvilleapp.di.image;

import android.app.Application;

import com.google.firebase.crash.FirebaseCrash;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.provider.PicassoProvider;

import org.pattonvillecs.pattonvilleapp.di.AppModule;
import org.pattonvillecs.pattonvilleapp.di.network.OkHttpClientModule;

import java.lang.reflect.Field;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;

/**
 * This module creates a singleton Picasso instance given an OkHttpClient. It also sets the Picasso singleton instance to be the newly created Picasso object.
 *
 * @author Mitchell Skaggs
 * @since 1.2.0
 */

@Module(includes = {OkHttpClientModule.class, AppModule.class})
public class PicassoModule {
    @Provides
    @Singleton
    static Picasso providePicasso(Application application, OkHttpClient okHttpClient) {
        Picasso picasso = new Picasso.Builder(application)
                .downloader(new OkHttp3Downloader(okHttpClient))
                .build();

        try {
            Field instanceField = PicassoProvider.class.getDeclaredField("instance");
            instanceField.setAccessible(true);
            instanceField.set(null, picasso);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            FirebaseCrash.report(e);
        }

        return picasso;
    }
}
