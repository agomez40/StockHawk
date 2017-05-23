package com.udacity.stockhawk.util;

import android.support.annotation.Nullable;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.util.Currency;
import java.util.Locale;

/**
 * Displays de price as the Y axis, formatting the amounts.
 *
 * @author Luis Alberto Gómez Rodríguez (lagomez40@gmail.com)
 * @version 1.0.0 2017/05/18
 * @see IAxisValueFormatter
 * @since 1.0.0 2017/05/18
 */
public class YAxisFormatter implements IAxisValueFormatter {
    private final Locale locale;

    /**
     * Constructor
     *
     * @param locale The locale
     * @since 1.0.0 2017/05/18
     */
    public YAxisFormatter(@Nullable Locale locale) {
        locale = locale != null ? locale : Locale.US;
        this.locale = locale;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        return AppUtils.formatMoney(value, locale, false) + " " +Currency.getInstance(locale);
    }
}
