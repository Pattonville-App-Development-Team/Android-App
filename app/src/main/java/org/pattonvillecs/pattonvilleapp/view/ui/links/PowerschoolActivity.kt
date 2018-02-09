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

package org.pattonvillecs.pattonvilleapp.view.ui.links

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_powerschool_selection.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.pattonvillecs.pattonvilleapp.R
import org.pattonvillecs.pattonvilleapp.viewmodel.getViewModel
import org.pattonvillecs.pattonvilleapp.viewmodel.links.PowerSchoolActivityViewModel

/**
 * Activity to handle the proper link to powerschool, differing between administrators, teachers, and students/parents
 *
 * @author Nathan Skelton
 * @since 1.0.0
 */
class PowerschoolActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_powerschool_selection)
        title = "Powerschool Links"

        val viewModel = getViewModel<PowerSchoolActivityViewModel>()

        student_button.onClick {
            viewModel.studentButtonPressed()
        }

        teacher_button.onClick {
            viewModel.onTeacherButtonPressed()
        }

        administrator_button.onClick {
            viewModel.onAdministratorButtonPressed()
        }
    }
}
