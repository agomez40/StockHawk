package com.udacity.stockhawk.ui.widget;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.data.PrefUtils;
import com.udacity.stockhawk.util.AppUtils;

/**
 * @author Luis Alberto Gómez Rodríguez (lagomez40@gmail.com)
 * @version 1.0.0 2017/05/18
 * @since 1.0.0 2017/05/18
 */
public class StockWidgetRemoteService extends RemoteViewsService {

    /**
     * {@inheritDoc}
     */
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ListRemoteViewFactory();
    }

    /**
     * Remote service factory implementation, allows the widget to refresh data and update the View.
     *
     * @author Luis Alberto Gómez Rodríguez (lagomez40@gmail.com)
     * @version 1.0.0 2017/05/18
     * @since 1.0.0 2017/05/18
     */
    private class ListRemoteViewFactory implements RemoteViewsService.RemoteViewsFactory {
        // The cursor
        private Cursor stockCursor = null;

        /**
         * {@inheritDoc}
         */
        @Override
        public void onCreate() {
            // nothing to do here
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void onDataSetChanged() {
            // Close the previous cursor
            if (stockCursor != null) {
                stockCursor.close();
            }

            // Binder
            final long identityToken = Binder.clearCallingIdentity();

            // Fill the data into a new cursor instance
            stockCursor = getContentResolver().query(Contract.Quote.URI,
                    Contract.Quote.QUOTE_COLUMNS.toArray(new String[]{}),
                    null,
                    null,
                    Contract.Quote.COLUMN_SYMBOL);

            // Bind the data to the remote view
            Binder.restoreCallingIdentity(identityToken);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void onDestroy() {
            // Close the cursor to avoid leaks
            if (stockCursor != null) {
                stockCursor.close();
                stockCursor = null;
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int getCount() {
            return stockCursor == null ? 0 : stockCursor.getCount();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public RemoteViews getViewAt(int position) {
            if (position == AdapterView.INVALID_POSITION || stockCursor == null
                    || !stockCursor.moveToPosition(position)) {
                return null;
            }

            RemoteViews remoteViews = new RemoteViews(getPackageName(),
                    R.layout.widget_list_item);

            String symbol = stockCursor.getString(Contract.Quote.POSITION_SYMBOL);
            float rawPrice = stockCursor.getFloat(Contract.Quote.POSITION_PRICE);
            float rawAbsoluteChange = stockCursor.getFloat(Contract.Quote.POSITION_ABSOLUTE_CHANGE);
            float rawPercentageChange = stockCursor.getFloat(Contract.Quote.POSITION_PERCENTAGE_CHANGE);

            if (rawAbsoluteChange > 0) {
                remoteViews.setInt(R.id.change, "setBackgroundResource", R.drawable.percent_change_pill_green);
            } else {
                remoteViews.setInt(R.id.change, "setBackgroundResource", R.drawable.percent_change_pill_green);
            }

            String price = AppUtils.formatMoney(rawPrice, null, false);
            String change = AppUtils.formatMoney(rawAbsoluteChange, null, true);
            String percentage = AppUtils.percentageFormat(rawPercentageChange, null, true);

            remoteViews.setTextViewText(R.id.symbol, symbol);
            remoteViews.setTextViewText(R.id.price, price);

            if (PrefUtils.getDisplayMode(getApplicationContext())
                    .equals(getApplicationContext().getString(R.string.pref_display_mode_absolute_key))) {
                remoteViews.setTextViewText(R.id.change, change);
            } else {
                remoteViews.setTextViewText(R.id.change, percentage);
            }

            final Intent fillInIntent = new Intent();
            Uri stockUri = Contract.Quote.makeUriForStock(symbol);
            fillInIntent.setData(stockUri);

            remoteViews.setOnClickFillInIntent(R.id.widget_list_item, fillInIntent);
            return remoteViews;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public RemoteViews getLoadingView() {
            return new RemoteViews(getPackageName(), R.layout.widget_list_item);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int getViewTypeCount() {
            return 1;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public long getItemId(int position) {
            if (stockCursor.moveToPosition(position)) {
                return stockCursor.getLong(Contract.Quote.POSITION_ID);
            } else {
                return position;
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean hasStableIds() {
            return true;
        }
    }
}
