package com.udacity.stockhawk.sync;

import android.app.IntentService;
import android.content.Intent;

import timber.log.Timber;

/**
 * Simple IntentService implementation that handles
 * asynchronous request to Sync the Local SQLite database with the cloud data.
 *
 * @version 1.0.0 2017/05/15
 * @see IntentService
 * @since 1.0.0 2017/05/15
 */
public class QuoteIntentService extends IntentService {

    /**
     * Constructor
     *
     * @since 1.0.0 2017/05/15
     */
    public QuoteIntentService() {
        super(QuoteIntentService.class.getSimpleName());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        Timber.d("Intent handled");
        QuoteSyncJob.getQuotes(getApplicationContext());
    }
}
