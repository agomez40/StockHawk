package com.udacity.stockhawk.util;

import com.udacity.stockhawk.data.HistoryEntry;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * Custom parser
 *
 * @author Luis Alberto Gómez Rodríguez (lagomez40@gmail.com)
 * @version 1.0.0 2017/05/22
 * @since 1.0.0 2017/05/22
 */
public final class Parser {
    /**
     * Parses a History entry
     *
     * @param history The string entry
     * @return the parsed entry
     * @since 1.0.0 2017/05/22
     */
    public static List<HistoryEntry> parseHistoryEntry(String history) {
        try {
            String[] values = history.split("\n");

            List<HistoryEntry> entries = new ArrayList<>(0);

            for (String value : values) {
                String[] dataPairs = value.split(",");

                HistoryEntry entry = new HistoryEntry();
                entry.setStockTime(Long.parseLong(dataPairs[0]));
                entry.setStockPrice(Float.parseFloat(dataPairs[1]));

                entries.add(entry);
            }

            return entries;
        } catch (Exception e) {
            Timber.e(e, e.getMessage());
            return null;
        }
    }
}
