package com.udacity.stockhawk;

import android.app.Application;

import timber.log.Timber;

/**
 * Main Application class, initializes the Application modules and components.
 *
 * @version 1.0.0 2017/05/15
 * @see Application
 * @since 1.0.0 2017/05/15
 */
public class StockHawkApp extends Application {
    /**
     * {@inheritDoc}
     */
    @Override
    public void onCreate() {
        super.onCreate();

        // Init Timber logging
        if (BuildConfig.DEBUG) {
            Timber.uprootAll();
            Timber.plant(new Timber.DebugTree());
        }
    }
}
