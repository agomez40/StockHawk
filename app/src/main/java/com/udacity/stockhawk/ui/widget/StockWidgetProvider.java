package com.udacity.stockhawk.ui.widget;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.widget.RemoteViews;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.sync.QuoteSyncJob;
import com.udacity.stockhawk.ui.MainActivity;
import com.udacity.stockhawk.ui.detail.StockDetailActivity;

public class StockWidgetProvider extends AppWidgetProvider {
    @SuppressLint("ObsoleteSdkInt")
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int widgetId : appWidgetIds) {
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.layout_stock_widget);

            Intent intent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            remoteViews.setOnClickPendingIntent(R.id.widget_header, pendingIntent);

            // Set up the collection
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                setRemoteAdapter(context, remoteViews);
            } else {
                setRemoteAdapterV11(context, remoteViews);
            }

            Intent clickIntent = new Intent(context, StockDetailActivity.class);
            PendingIntent pendingIntentTemplate = TaskStackBuilder.create(context)
                    .addNextIntentWithParentStack(clickIntent)
                    .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

            remoteViews.setPendingIntentTemplate(R.id.lv_stock_widget, pendingIntentTemplate);
            remoteViews.setEmptyView(R.id.lv_stock_widget, R.id.tv_list_empty);
            remoteViews.setInt(R.id.stock_widget_content, "setBackgroundResource", R.color.grey200);
            remoteViews.setContentDescription(R.id.lv_stock_widget, context.getString(R.string.description_widget_item));

            appWidgetManager.updateAppWidget(widgetId, remoteViews);
        }

        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (QuoteSyncJob.ACTION_DATA_UPDATED.equals(intent.getAction())) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(
                    new ComponentName(context, getClass()));
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.lv_stock_widget);
        }
    }

    /**
     *
     * @param context
     * @param views
     */
    private void setRemoteAdapter(Context context, @NonNull final RemoteViews views) {
        views.setRemoteAdapter(R.id.lv_stock_widget,
                new Intent(context, StockWidgetRemoteService.class));
    }

    /**
     *
     * @param context
     * @param views
     */
    @SuppressWarnings("deprecation")
    private void setRemoteAdapterV11(Context context, @NonNull final RemoteViews views) {
        views.setRemoteAdapter(0, R.id.lv_stock_widget,
                new Intent(context, StockWidgetRemoteService.class));
    }
}
