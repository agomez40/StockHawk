package com.udacity.stockhawk.util;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;

/**
 * Shared application utility methods.
 *
 * @author Luis Alberto Gómez Rodríguez (lagomez40@gmail.com)
 * @version 1.0.0 2017/05/18
 * @since 1.0.0 2017/05/18
 */
public final class AppUtils {

    /**
     * Formats a {@link Float} to a user read friendly money string.
     *
     * @param floatValue     The float value to format
     * @param locale         The locale to format, if null defaults to {@literal Locale.US}
     * @param withPlusPrefix {@literal true} to show a + prefix symbol on positive values, otherwise {@literal false}
     * @return The float value as money
     * @since 1.0.0 2017/05/18
     */
    public static String formatMoney(@NonNull Float floatValue, @Nullable Locale locale, boolean withPlusPrefix) {
        Locale useLocale = locale != null ? locale : Locale.US;

        DecimalFormat dollarFormat = (DecimalFormat) NumberFormat.getCurrencyInstance(useLocale);

        if (withPlusPrefix) {
            dollarFormat = (DecimalFormat) NumberFormat.getCurrencyInstance(useLocale);
            dollarFormat.setPositivePrefix("+$");
        }

        dollarFormat.setCurrency(Currency.getInstance(useLocale));

        return dollarFormat.format(floatValue);
    }

    /**
     * Formats a {@link Float} to a user read friendly percentage string.
     *
     * @param floatValue     The float value to format
     * @param locale         The locale to format, if null defaults to {@literal Locale.getDefault()}
     * @param withPlusPrefix {@literal true} to show a + symbol on positive values, otherwise {@literal false}
     * @return The float value as percentage
     * @since 1.0.0 2017/05/18
     */
    public static String percentageFormat(@NonNull Float floatValue, @Nullable Locale locale, boolean withPlusPrefix) {
        Locale useLocale = locale != null ? locale : Locale.getDefault();

        DecimalFormat percentageFormat = (DecimalFormat) NumberFormat.getPercentInstance(useLocale);
        percentageFormat.setMaximumFractionDigits(2);
        percentageFormat.setMinimumFractionDigits(2);

        if (withPlusPrefix) {
            percentageFormat.setPositivePrefix("+");
        }

        return percentageFormat.format(floatValue / 100);
    }
}
