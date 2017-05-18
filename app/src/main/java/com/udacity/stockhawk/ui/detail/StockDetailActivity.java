package com.udacity.stockhawk.ui.detail;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.data.PrefUtils;
import com.udacity.stockhawk.sync.QuoteSyncJob;
import com.udacity.stockhawk.util.AppUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A detail view for each Stock, shows a graph using the stock's value over time.
 *
 * @author Luis Alberto Gómez Rodríguez (lagomez40@gmail.com)
 * @version 1.0.0 2017/05/15
 * @see AppCompatActivity
 * @see LoaderManager
 * @since 1.0.0 2017/05/15
 */
public class StockDetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int STOCK_LOADER = 0;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tv_symbol)
    TextView tvSymbol;
    @BindView(R.id.tv_price)
    TextView tvPrice;
    @BindView(R.id.tv_change)
    TextView tvChange;
    @BindView(R.id.appbar)
    AppBarLayout appbar;
    @BindView(R.id.chart)
    LineChart chart;

    private Uri uri;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_detail);
        ButterKnife.bind(this);

        // Setup the toolbar
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Get the passed data
        uri = getIntent().getData();

        if (uri != null) {
            // Load the data using the content provider
            getSupportLoaderManager().initLoader(STOCK_LOADER, null, this);


            QuoteSyncJob.initialize(this);
            QuoteSyncJob.syncImmediately(this);
        } else {
            // no data to display :(
            finish();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this,
                uri,
                Contract.Quote.QUOTE_COLUMNS.toArray(new String[]{}),
                null, null, Contract.Quote.COLUMN_SYMBOL);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Set up the data
        if (data.moveToFirst()) {
            String stockName = data.getString(Contract.Quote.POSITION_STOCK_NAME);
            String symbol = data.getString(Contract.Quote.POSITION_SYMBOL);
            String price = AppUtils.formatMoney(data.getFloat(Contract.Quote.POSITION_PRICE), null, false);

            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(stockName);
            }

            tvSymbol.setText(symbol);
            tvPrice.setText(price);

            setHeaderData(data);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Refresh data
    }

    /**
     * Sets the header data
     *
     * @param data The data to set
     * @since 1.0.0 2017/05/18
     */
    private void setHeaderData(Cursor data) {

        float rawAbsoluteChange = data.getFloat(Contract.Quote.POSITION_ABSOLUTE_CHANGE);
        float percentageChange = data.getFloat(Contract.Quote.POSITION_PERCENTAGE_CHANGE);

        if (rawAbsoluteChange > 0) {
            tvChange.setBackgroundResource(R.drawable.percent_change_pill_green);
        } else {
            tvChange.setBackgroundResource(R.drawable.percent_change_pill_red);
        }

        String change = AppUtils.formatMoney(rawAbsoluteChange, null, true);
        String percentage = AppUtils.percentageFormat(percentageChange, null, true);

        if (PrefUtils.getDisplayMode(this)
                .equals(getString(R.string.pref_display_mode_absolute_key))) {
            tvChange.setText(change);
        } else {
            tvChange.setText(percentage);
        }
    }
}
