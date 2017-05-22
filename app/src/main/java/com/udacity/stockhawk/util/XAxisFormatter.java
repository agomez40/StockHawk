package com.udacity.stockhawk.util;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.udacity.stockhawk.data.HistoryEntry;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * X Axis date formatter, formats milliseconds to date.
 *
 * @author Luis Alberto Gómez Rodríguez (lagomez40@gmail.com)
 * @version 1.0.0 2017/05/18
 * @see IAxisValueFormatter
 * @since 1.0.0 2017/05/18
 */
public class XAxisFormatter implements IAxisValueFormatter {

    // Date formatter
    private final SimpleDateFormat simpleDateFormat;
    private final List<HistoryEntry> entries;

    /**
     * Constructor
     *
     * @param entries    The history entries
     * @param dateFormat The Date format to use, if null defaults to YYYY-MM-DD
     * @param locale     The locale, if null defaults to Locale.US
     * @since 1.0.0 2017/05/18
     */
    public XAxisFormatter(@NonNull List<HistoryEntry> entries, @Nullable String dateFormat, @Nullable Locale locale) {
        dateFormat = dateFormat != null ? dateFormat : "yyyy-MM-dd";
        locale = locale != null ? locale : Locale.US;
        this.entries = entries;
        this.simpleDateFormat = new SimpleDateFormat(dateFormat, locale);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        Date dateToDisplay = new Date();
        dateToDisplay.setTime((entries.get((int)value).getStockTime()));

        return simpleDateFormat.format(dateToDisplay);
    }
}
