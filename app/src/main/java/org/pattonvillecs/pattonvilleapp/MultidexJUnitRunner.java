package org.pattonvillecs.pattonvilleapp;

import android.os.Bundle;
import android.support.multidex.MultiDex;
import android.support.test.runner.AndroidJUnitRunner;


/**
 * Created by skaggsm on 1/19/17.
 */

public class MultidexJUnitRunner extends AndroidJUnitRunner {
    @Override
    public void onCreate(Bundle arguments) {
        MultiDex.install(getTargetContext());
        super.onCreate(arguments);
    }
}