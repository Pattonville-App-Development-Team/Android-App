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

package org.pattonvillecs.pattonvilleapp.view.ui.about.detail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.annotation.DrawableRes
import android.view.MenuItem
import com.google.firebase.crash.FirebaseCrash
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import dagger.android.support.DaggerAppCompatActivity
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.common.FlexibleItemAnimator
import eu.davidea.flexibleadapter.common.SmoothScrollLinearLayoutManager
import eu.davidea.flexibleadapter.items.IFlexible
import kotlinx.android.synthetic.main.activity_about_detail.*
import org.pattonvillecs.pattonvilleapp.R
import org.pattonvillecs.pattonvilleapp.view.ui.about.CircleTransformation
import org.pattonvillecs.pattonvilleapp.viewmodel.about.detail.AboutDetailActivityViewModel
import org.pattonvillecs.pattonvilleapp.viewmodel.getViewModel
import javax.inject.Inject

/**
 * @author Mitchell Skaggs
 * @since 1.0.0
 */

class AboutDetailActivity : DaggerAppCompatActivity() {

    @Inject
    lateinit var picasso: Picasso

    override fun onBackPressed() {
        super.onBackPressed()
        supportFinishAfterTransition()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
        // Respond to the action bar's Up/Home button
            android.R.id.home -> {
                supportFinishAfterTransition()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_detail)

        val viewModel = getViewModel<AboutDetailActivityViewModel>()

        viewModel.setDeveloperName(intent.getStringExtra(KEY_NAME))
        viewModel.setDeveloperText(intent.getStringExtra(KEY_TEXT))
        viewModel.setDeveloperImageResource(intent.getIntExtra(KEY_IMAGE, -1))
        viewModel.setDeveloperLinks(intent.getParcelableArrayExtra(KEY_LINK_ITEMS).map { it as LinkItem })

        val linksAdapter = FlexibleAdapter<IFlexible<*>>(null, null, true)
        linksAdapter.setAnimationDelay(250L)

        developer_links.layoutManager = SmoothScrollLinearLayoutManager(this, SmoothScrollLinearLayoutManager.HORIZONTAL, false)
        developer_links.itemAnimator = FlexibleItemAnimator()
        developer_links.adapter = linksAdapter


        viewModel.developerName.observe(this::getLifecycle) {
            developer_name.text = it
        }
        viewModel.developerText.observe(this::getLifecycle) {
            developer_text.text = it
        }
        viewModel.developerImageResource.observe(this::getLifecycle) {
            it?.let {
                picasso.load(it)
                        .fit()
                        .centerCrop()
                        .transform(CircleTransformation)
                        .into(developer_image, object : Callback {
                            override fun onSuccess() {
                                supportStartPostponedEnterTransition()
                            }

                            override fun onError(e: Exception) {
                                FirebaseCrash.report(e)
                                supportStartPostponedEnterTransition()
                            }
                        })
            }
        }
        viewModel.developerLinks.observe(this::getLifecycle) {
            linksAdapter.updateDataSet(it.orEmpty(), true)
        }
        supportPostponeEnterTransition()
    }

    companion object {
        private const val KEY_NAME = "name"
        private const val KEY_TEXT = "text"
        private const val KEY_IMAGE = "image"
        private const val KEY_LINK_ITEMS = "linkItems"

        @JvmStatic
        fun createIntent(context: Context, name: String, text: String, @DrawableRes image: Int, linkItems: Array<LinkItem>): Intent {
            return Intent(context, AboutDetailActivity::class.java)
                    .putExtra(KEY_NAME, name)
                    .putExtra(KEY_TEXT, text)
                    .putExtra(KEY_IMAGE, image)
                    .putExtra(KEY_LINK_ITEMS, linkItems)
        }
    }
}
