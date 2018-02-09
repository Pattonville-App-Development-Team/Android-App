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

package org.pattonvillecs.pattonvilleapp.di.network

import android.os.AsyncTask
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import org.pattonvillecs.pattonvilleapp.service.repository.directory.DirectoryConverterFactory
import org.pattonvillecs.pattonvilleapp.service.repository.directory.DirectoryRetrofitService
import retrofit2.Retrofit
import retrofit2.adapter.guava.GuavaCallAdapterFactory
import javax.inject.Singleton

/**
 * This module creates a singleton DirectoryRetrofitService.
 *
 * @author Mitchell Skaggs
 * @since 1.3.0
 */
@Module(includes = [(OkHttpClientModule::class)])
object DirectoryRetrofitServiceModule {
    @Provides
    @Singleton
    @JvmStatic
    fun provideDirectoryRetrofitService(okHttpClient: OkHttpClient): DirectoryRetrofitService {
        return Retrofit.Builder()
                .baseUrl("https://forms.psdr3.org/psdapp/")
                .client(okHttpClient)
                .callbackExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
                .addCallAdapterFactory(GuavaCallAdapterFactory.create())
                .addConverterFactory(DirectoryConverterFactory)
                .build()
                .create(DirectoryRetrofitService::class.java)
    }
}