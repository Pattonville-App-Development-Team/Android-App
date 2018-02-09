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

package org.pattonvillecs.pattonvilleapp.viewmodel.about

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import eu.davidea.flexibleadapter.items.IFlexible
import org.pattonvillecs.pattonvilleapp.R
import org.pattonvillecs.pattonvilleapp.view.ui.about.DeveloperHeaderItem
import org.pattonvillecs.pattonvilleapp.view.ui.about.DeveloperItem
import org.pattonvillecs.pattonvilleapp.view.ui.about.detail.LinkItem
import org.pattonvillecs.pattonvilleapp.view.ui.about.secret.SecretDeveloperItem

/**
 * The ViewModel for the About activity. It makes the list of developers available to the activity.
 *
 * @since 1.2.0
 * @author Mitchell Skaggs
 */
class AboutActivityViewModel : ViewModel() {
    val developers: LiveData<List<IFlexible<*>>> by lazy {
        MutableLiveData<List<IFlexible<*>>>().apply {
            val androidHeader = DeveloperHeaderItem("Android Team")
            val iOSHeader = DeveloperHeaderItem("iOS Team")
            val teacherHeader = DeveloperHeaderItem("Instructors")

            value = listOf(
                    DeveloperItem(androidHeader, "Mitchell Skaggs", "Android Team Lead\n\nUniversity:\n\nMissouri University of Science and Technology", R.drawable.face_skaggs,
                            LinkItem(R.drawable.github_box, "https://github.com/magneticflux-"),
                            LinkItem(R.drawable.linkedin_box, "https://www.linkedin.com/in/mitchell-s-16085b13b")
                    ),
                    DeveloperItem(androidHeader, "Keturah Gadson", "University:\n\nHarvard University", R.drawable.face_gadson,
                            LinkItem(R.drawable.github_box, "https://github.com/gadsonk")
                    ),
                    DeveloperItem(androidHeader, "Ethan Holtgrieve", "University:\n\nTruman State University", R.drawable.face_holtgrieve,
                            LinkItem(R.drawable.github_box, "https://github.com/holtgrie")
                    ),
                    DeveloperItem(androidHeader, "Nathan Skelton", "University:\n\nMissouri University of Science and Technology", R.drawable.face_skelton,
                            LinkItem(R.drawable.github_box, "https://github.com/skeltonn"),
                            LinkItem(R.drawable.linkedin_box, "https://www.linkedin.com/in/nathaniel-skelton-8815a413b/")
                    ),
                    DeveloperItem(iOSHeader, "Joshua Zahner", "iOS Team Lead\n\nUniversity:\n\nUniversity of Miami", R.drawable.face_zahner,
                            LinkItem(R.drawable.github_box, "https://github.com/Ovec8hkin"),
                            LinkItem(R.drawable.linkedin_box, "https://www.linkedin.com/in/joshuazahner/")
                    ),
                    DeveloperItem(iOSHeader, "Mustapha Barrie", "University:\n\nWashington University in St. Louis", R.drawable.face_barrie,
                            LinkItem(R.drawable.github_box, "https://github.com/MustaphaB")
                    ),
                    DeveloperItem(iOSHeader, "Kevin Bowers", "University:\n\nUniversity of Missouri - Columbia", R.drawable.face_bowers,
                            LinkItem(R.drawable.github_box, "https://github.com/KevinBowers73")
                    ),
                    DeveloperItem(iOSHeader, "Micah Thompkins", "University:\n\nNorthwestern University", R.drawable.face_thompkins,
                            LinkItem(R.drawable.github_box, "https://github.com/MicahThompkins")
                    ),
                    DeveloperItem(teacherHeader, "Mr. Simmons", "Supervisor, Representative, Philosopher", R.drawable.face_simmons),
                    SecretDeveloperItem(teacherHeader, null, null, 0)
            )
        }
    }
}