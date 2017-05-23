package com.udacity.stockhawk.ui.detail;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.DefaultValueFormatter;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.data.HistoryEntry;
import com.udacity.stockhawk.data.PrefUtils;
import com.udacity.stockhawk.util.AppUtils;
import com.udacity.stockhawk.util.Parser;
import com.udacity.stockhawk.util.XAxisFormatter;
import com.udacity.stockhawk.util.YAxisFormatter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

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

    private String currentSymbol;

    private int currentFilter = 0;

    private List<HistoryEntry> historyEntries;

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

            currentSymbol = symbol;

            // Fetch the history data for the current symbol
            historyEntries = getHistoryData();

            if (historyEntries != null && !historyEntries.isEmpty()) {
                drawChart();
            } else {
                // Show a message to the user
                Toast.makeText(this, R.string.message_no_history_data, Toast.LENGTH_SHORT).show();
            }
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

    /**
     * Gets the historic data for the current stock symbol.
     *
     * @return a collection of {@link HistoryEntry}
     * @since 1.0.0 2017/05/22
     */
    private List<HistoryEntry> getHistoryData() {
        Cursor cursor = null;

        try {
            List<HistoryEntry> historyEntries = null;

            String[] projection = {Contract.Quote.COLUMN_HISTORY};
            String selection = Contract.Quote.COLUMN_SYMBOL + " = ?";
            String[] selectionArgs = {currentSymbol};

            cursor = getContentResolver().query(
                    Contract.Quote.URI,
                    projection,
                    selection,
                    selectionArgs,
                    null);

            if (cursor != null && cursor.getCount() > 0) {
                if (cursor.moveToFirst()) {
                    String data = cursor.getString(cursor.getColumnIndex(Contract.Quote.COLUMN_HISTORY));
                    historyEntries = Parser.parseHistoryEntry(data);
                }
            }

            return historyEntries;
        } catch (Exception e) {
            Timber.e(e, e.getMessage());
            return null;
        } finally {
            if (cursor != null) cursor.close();
        }
    }

    /**
     * Draws a line chart using the History entries.
     *
     * @since 1.0.0 2017/05/22
     */
    private void drawChart() {
        List<Entry> entries = getEntries();
        LineDataSet lineDataSet = new LineDataSet(entries,
                getString(R.string.symbol_detail_title, currentSymbol));

        chart.setExtraOffsets(2f, 8f, 2f, 4f);

        // Colors
        lineDataSet.setColor(ContextCompat.getColor(this, R.color.material_green_700));
        lineDataSet.setValueTextSize(12f);
        lineDataSet.setValueFormatter(new DefaultValueFormatter(2));
        lineDataSet.setValueTextColor(ContextCompat.getColor(this, R.color.material_green_700));
        lineDataSet.setCircleColor(ContextCompat.getColor(this, R.color.material_red_700));

        LineData lineData = new LineData(lineDataSet);
        chart.setData(lineData);

        // remove description
        Description desc = new Description();
        desc.setEnabled(false);
        chart.setDescription(desc);

        // XAxis
        XAxisFormatter xAxisFormatter = new XAxisFormatter(historyEntries, null, null);

        XAxis xAxis = chart.getXAxis();
        xAxis.setLabelRotationAngle(-45);
        xAxis.setEnabled(true);
        xAxis.setDrawLabels(true);
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawGridLines(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTH_SIDED);
        xAxis.setTextColor(ContextCompat.getColor(this, R.color.grey200));
        xAxis.setTextSize(12f);
        xAxis.setAxisLineWidth(2f);
        xAxis.setAxisLineColor(ContextCompat.getColor(this, R.color.grey200));
        xAxis.setValueFormatter(xAxisFormatter);

        // YAxis Left setup
        YAxisFormatter yAxisFormatter = new YAxisFormatter(Locale.getDefault());
        YAxis yAxisLeft = chart.getAxisLeft();
        yAxisLeft.setEnabled(true);
        yAxisLeft.setDrawLabels(true);
        yAxisLeft.setDrawAxisLine(true);
        yAxisLeft.setDrawGridLines(true);
        yAxisLeft.setTextSize(12f);
        yAxisLeft.setAxisLineWidth(2f);
        yAxisLeft.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        yAxisLeft.setTextColor(ContextCompat.getColor(this, R.color.grey200));
        yAxisLeft.setAxisLineColor(ContextCompat.getColor(this, R.color.grey200));
        yAxisLeft.setValueFormatter(yAxisFormatter);

        // YAxis RTL setup
        YAxis yAxisRight = chart.getAxisRight();
        yAxisRight.setEnabled(true);
        yAxisRight.setDrawLabels(true);
        yAxisRight.setDrawAxisLine(true);
        yAxisRight.setDrawGridLines(true);
        xAxis.setTextSize(12f);
        yAxisRight.setAxisLineWidth(2f);
        yAxisRight.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        yAxisRight.setTextColor(ContextCompat.getColor(this, R.color.grey200));
        yAxisRight.setAxisLineColor(ContextCompat.getColor(this, R.color.grey200));
        yAxisRight.setValueFormatter(new YAxisFormatter(Locale.getDefault()));
        yAxisRight.setValueFormatter(yAxisFormatter);

        // trigger a redraw
        chart.invalidate();
    }

    private List<Entry> getEntries() {
        List<Entry> entries = new ArrayList<>(0);
        for (int i = 0; i < historyEntries.size(); i++) {
            entries.add(new Entry(i, historyEntries.get(i).getStockPrice(), historyEntries.get(i)));
        }

        return entries;
    }
}
