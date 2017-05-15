package com.udacity.stockhawk.sync;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;

import timber.log.Timber;

/**
 * A scheduled Job to perform local syncs of Quotes.
 *
 * @version 1.0.0 2017/05/15
 * @see JobService
 * @since 1.0.0 2017/05/15
 */
public class QuoteJobService extends JobService {

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        Timber.d("Intent handled");
        Intent nowIntent = new Intent(getApplicationContext(), QuoteIntentService.class);
        getApplicationContext().startService(nowIntent);
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }
}
