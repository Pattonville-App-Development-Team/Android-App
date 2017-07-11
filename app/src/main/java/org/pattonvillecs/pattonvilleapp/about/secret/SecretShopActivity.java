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

package org.pattonvillecs.pattonvilleapp.about.secret;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.Toast;

import org.pattonvillecs.pattonvilleapp.R;
import org.pattonvillecs.pattonvilleapp.preferences.PreferenceUtils;

import static org.pattonvillecs.pattonvilleapp.about.secret.SecretActivity.KEY_GP;

/**
 * Created by Mitchell on 5/13/2017.
 */

public class SecretShopActivity extends AppCompatActivity {
    static final String KEY_SD_CAMERAS = "sd_cameras";
    static final String KEY_HD_CAMERAS = "hd_cameras";
    static final String KEY_4K_CAMERAS = "4k_cameras";

    private Button buySDCameraButton, buyHDCameraButton, buy4KCameraButton;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_secret_shop);
        setTitle("Well Watcher Shop");

        sharedPreferences = PreferenceUtils.getSharedPreferences(this);

        buySDCameraButton = (Button) findViewById(R.id.shop_button_sd_camera);
        buyHDCameraButton = (Button) findViewById(R.id.shop_button_hd_camera);
        buy4KCameraButton = (Button) findViewById(R.id.shop_button_4k_camera);

        buySDCameraButton.setOnClickListener(v -> tryBuyCamera(KEY_SD_CAMERAS, false));
        buyHDCameraButton.setOnClickListener(v -> tryBuyCamera(KEY_HD_CAMERAS, false));
        buy4KCameraButton.setOnClickListener(v -> tryBuyCamera(KEY_4K_CAMERAS, false));

        buySDCameraButton.setOnLongClickListener(v -> {
            tryBuyCamera(KEY_SD_CAMERAS, true);
            return true;
        });
        buyHDCameraButton.setOnLongClickListener(v -> {
            tryBuyCamera(KEY_HD_CAMERAS, true);
            return true;
        });
        buy4KCameraButton.setOnLongClickListener(v -> {
            tryBuyCamera(KEY_4K_CAMERAS, true);
            return true;
        });
    }

    private void tryBuyCamera(String cameraKey, boolean untilBroke) {
        long gp = sharedPreferences.getLong(KEY_GP, 0);
        long cost = getCostForCamera(cameraKey);
        if (gp >= cost) {
            if (!untilBroke) {
                deductGP(cost);
                incrementNumCameras(cameraKey, 1);
                showBuySuccess(1);
            } else {
                long numToBuy = gp / cost;
                deductGP(numToBuy * cost);
                incrementNumCameras(cameraKey, numToBuy);
                showBuySuccess(numToBuy);
            }
        } else {
            showBuyFail();
        }
    }

    private void showBuySuccess(long purchased) {
        Toast.makeText(this, getResources().getQuantityString(R.plurals.secret_purchase_toast, (int) purchased, purchased), Toast.LENGTH_SHORT).show();
    }

    private long getCostForCamera(String cameraKey) {
        switch (cameraKey) {
            case KEY_SD_CAMERAS:
                return 5;
            case KEY_HD_CAMERAS:
                return 50;
            case KEY_4K_CAMERAS:
                return 500;
            default:
                throw new IllegalStateException("Unknown camera key!");
        }
    }

    private void incrementNumCameras(String cameraKey, long numCameras) {
        long priorCameras = sharedPreferences.getLong(cameraKey, 0);
        sharedPreferences.edit().putLong(cameraKey, priorCameras + numCameras).apply();
    }

    private void deductGP(long goToDeduct) {
        long priorBalance = sharedPreferences.getLong(KEY_GP, 0);
        sharedPreferences.edit().putLong(KEY_GP, priorBalance - goToDeduct).apply();
    }

    private void showBuyFail() {
        Toast.makeText(this, "You don't have enough wells to buy this item!", Toast.LENGTH_SHORT).show();
    }
}
