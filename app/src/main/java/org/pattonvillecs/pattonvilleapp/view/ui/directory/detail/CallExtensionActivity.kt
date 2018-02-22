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

package org.pattonvillecs.pattonvilleapp.view.ui.directory.detail

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.telephony.PhoneNumberUtils
import org.jetbrains.anko.alert
import org.jetbrains.anko.appcompat.v7.Appcompat
import org.jetbrains.anko.longToast
import org.jetbrains.anko.makeCall
import org.pattonvillecs.pattonvilleapp.R
import org.pattonvillecs.pattonvilleapp.service.model.directory.PhoneNumber

/**
 * This activity handles calling a phone number and extension.
 *
 * @author Mitchell Skaggs
 * @since 1.0.0
 */

class CallExtensionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            showConfirmCallDialog()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CALL_PHONE), REQUEST_CALL)
        }
    }

    private fun showConfirmCallDialog() {
        val phoneNumber = intent.getParcelableExtra<PhoneNumber>(EXTRA_PHONE_NUMBER)

        alert(Appcompat) {
            message = "Are you sure that your want to call the extension ${phoneNumber.extension1!!}?"
            positiveButton(android.R.string.ok) {
                val successful = makeCall(DISTRICT_VOICEMAIL_PHONE
                        + PhoneNumberUtils.PAUSE
                        + PhoneNumberUtils.PAUSE
                        + PhoneNumberUtils.PAUSE
                        + phoneNumber.extension1)
                if (!successful)
                    longToast(R.string.no_phone_capabilities_found).show()
                finish()
            }
            negativeButton(android.R.string.cancel) {
                finish()
            }
            onCancelled {
                finish()
            }
        }.show()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CALL && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            showConfirmCallDialog()
        } else {
            longToast(R.string.phone_denied).show()
            finish()
        }

    }

    override fun onBackPressed() {
        finish()
    }

    companion object {
        private const val EXTRA_PHONE_NUMBER = "phone_number"
        private const val DISTRICT_VOICEMAIL_PHONE = "3142138010"
        private const val REQUEST_CALL = 0
        private const val TAG = "CallExtensionActivity"

        fun newIntent(c: Context, phoneNumber: PhoneNumber): Intent {
            val intent = Intent(c, CallExtensionActivity::class.java)
            intent.putExtra(EXTRA_PHONE_NUMBER, phoneNumber)
            return intent
        }
    }
}
