package com.udacity.stockhawk.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.udacity.stockhawk.R;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * A SharedPreferences Helper, allows CRUD operations on the application
 * SharedPreferences file.
 *
 * @version 1.0.0 2017/05/15
 * @see PreferenceManager
 * @see SharedPreferences
 * @since 1.0.0 2017/05/15
 */
public final class PrefUtils {

    /**
     * Constructor
     *
     * @since 1.0.0 2017/05/15
     */
    private PrefUtils() {
    }

    /**
     * Gets a {@link Set} of stock strings.
     *
     * @param context The application context.
     * @return The Set of strings
     * @since 1.0.0 2017/05/15
     */
    public static Set<String> getStocks(Context context) {
        String stocksKey = context.getString(R.string.pref_stocks_key);
        String initializedKey = context.getString(R.string.pref_stocks_initialized_key);
        String[] defaultStocksList = context.getResources().getStringArray(R.array.default_stocks);

        HashSet<String> defaultStocks = new HashSet<>(Arrays.asList(defaultStocksList));
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        boolean initialized = prefs.getBoolean(initializedKey, false);

        if (!initialized) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(initializedKey, true);
            editor.putStringSet(stocksKey, defaultStocks);
            editor.apply();
            return defaultStocks;
        }
        return prefs.getStringSet(stocksKey, new HashSet<String>());

    }

    /**
     * Edits a Stock entry, adding or removing the symbol value.
     *
     * @param context The application context
     * @param symbol  The symbol to add or remove
     * @param add     {@literal true} to add a new entry to the Set, otherwise {@literal false}
     */
    private static void editStockPref(Context context, String symbol, Boolean add) {
        String key = context.getString(R.string.pref_stocks_key);
        Set<String> stocks = getStocks(context);

        if (add) {
            stocks.add(symbol);
        } else {
            stocks.remove(symbol);
        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putStringSet(key, stocks);
        editor.apply();
    }

    /**
     * Adds a stock symbol.
     *
     * @param context The application context
     * @param symbol  The stock symbol to add
     * @since 1.0.0 2017/05/15
     */
    public static void addStock(Context context, String symbol) {
        editStockPref(context, symbol, true);
    }

    /**
     * Removes a stock symbol from the Set.
     *
     * @param context The application context
     * @param symbol  The stock symbol to remove
     * @since 1.0.0 2017/05/15
     */
    public static void removeStock(Context context, String symbol) {
        editStockPref(context, symbol, false);
    }

    /**
     * Gets the current display mode.
     *
     * @param context The application context
     * @return The current preference display mode
     * @since 1.0.0 2017/05/15
     */
    public static String getDisplayMode(Context context) {
        String key = context.getString(R.string.pref_display_mode_key);
        String defaultValue = context.getString(R.string.pref_display_mode_default);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(key, defaultValue);
    }

    /**
     * Toggles the a change and saves the display mode preference
     *
     * @param context The application context
     * @since 1.0.0 2017/05/15
     */
    public static void toggleDisplayMode(Context context) {
        String key = context.getString(R.string.pref_display_mode_key);
        String absoluteKey = context.getString(R.string.pref_display_mode_absolute_key);
        String percentageKey = context.getString(R.string.pref_display_mode_percentage_key);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        String displayMode = getDisplayMode(context);

        SharedPreferences.Editor editor = prefs.edit();

        if (displayMode.equals(absoluteKey)) {
            editor.putString(key, percentageKey);
        } else {
            editor.putString(key, absoluteKey);
        }

        editor.apply();
    }
}
