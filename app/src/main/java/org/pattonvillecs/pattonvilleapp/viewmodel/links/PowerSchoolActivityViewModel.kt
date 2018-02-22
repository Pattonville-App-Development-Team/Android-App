/*
 * Copyright (C) 2017 Mitchell Skaggs, Keturah Gadson, Ethan Holtgrieve, Nathan Skelton, Pattonville School District
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

package org.pattonvillecs.pattonvilleapp.viewmodel.links

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.content.Intent
import org.jetbrains.anko.browse
import org.pattonvillecs.pattonvilleapp.R
import org.pattonvillecs.pattonvilleapp.preferences.PreferenceUtils
import org.pattonvillecs.pattonvilleapp.viewmodel.app

/**
 * @author Mitchell Skaggs
 * @since 1.2.0
 */
class PowerSchoolActivityViewModel(application: Application) : AndroidViewModel(application) {
    fun studentButtonPressed() {
        if (PreferenceUtils.getPowerSchoolIntent(app)) {
            launchPowerSchoolApp()
        } else {
            app.browse("https://powerschool.psdr3.org", true)
        }
    }

    private fun launchPowerSchoolApp() {
        val packageManager = app.packageManager
        val packageName = app.getString(R.string.package_name_powerschool)

        // If app installed, launch, if not, open play store to it
        if (packageManager.getLaunchIntentForPackage(packageName) != null) {
            app.startActivity(packageManager.getLaunchIntentForPackage(packageName).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        } else {
            app.browse("https://play.google.com/store/apps/details?id=$packageName", true)
        }
    }

    fun onTeacherButtonPressed() {
        app.browse("https://powerschool.psdr3.org/teachers/pw.html", true)
    }

    fun onAdministratorButtonPressed() {
        app.browse("https://powerschool.psdr3.org/admin/pw.html", true)
    }
}