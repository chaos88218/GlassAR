package com.example.miles.glassar;

/**
 * Created by miles on 2015/8/23.
 */
import android.app.Application;
import android.preference.PreferenceManager;

import org.artoolkit.ar.base.assets.AssetHelper;

public class ARApplication extends Application {

    private static Application sInstance;

    // Anywhere in the application where an instance is required, this method
    // can be used to retrieve it.
    public static Application getInstance() {
        return sInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        ((ARApplication) sInstance).initializeInstance();
        PreferenceManager.setDefaultValues(this, org.artoolkit.ar.base.R.xml.preferences, false);
    }

    // Here we do one-off initialisation which should apply to all activities
    // in the application.
    protected void initializeInstance() {

        // Unpack assets to cache directory so native library can read them.
        // N.B.: If contents of assets folder changes, be sure to increment the
        // versionCode integer in the AndroidManifest.xml file.
        AssetHelper assetHelper = new AssetHelper(getAssets());
        assetHelper.cacheAssetFolder(getInstance(), "Data");
    }
}

