package com.chaspe.simplelight;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Button;
import android.widget.RemoteViews;


/**
 * Implementation of App Widget functionality.
 */
public class LightWidget extends AppWidgetProvider {
    public static final String TAG = "LightWidget";

    public static final String EXTRA_CHANGE = "CHANGE";
    public static final String EXTRA_STROBE = "STROBE";
    public static final String EXTRA_FREQUENCY = "FREQUENCY";


    private static boolean strobe = false;
    private static int frequency = 0;
    private static boolean change = true;



    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        final int N = appWidgetIds.length;

        // Perform this loop procedure for each App Widget that belongs to this provider
        for (int i=0; i<N; i++) {
            int appWidgetId = appWidgetIds[i];

            // Create an Intent to launch ExampleActivity
            Intent intent = new Intent();
            intent.setAction("com.chaspe.simplelight.LightService");
            //intent.putExtra(LightFragment.EXTRA_CHANGE, change);
            //intent.putExtra(LightFragment.EXTRA_STROBE, strobe);
            //intent.putExtra(LightFragment.EXTRA_FREQUENCY, frequency);
            PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, 0);

            // Get the layout for the App Widget and attach an on-click listener
            // to the button
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.light_widget);
            views.setOnClickPendingIntent(R.id.widget_button, pendingIntent);

            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }



    @Override
    public void onEnabled(Context context) {
        Log.d(TAG, "widget enabled");
        // Enter relevant functionality for when the first widget is created

    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }


}


