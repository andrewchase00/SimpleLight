package com.chaspe.simplelight;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;


/**
                                                                                                    * Created by Andrew Chase on 2/3/2015.
                                                                                                    * Widget that has one button that sends an intent to LightService to switch the
                                                                                                    * light on or off with the strobe setting disabled.
 */
public class LightWidget extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        final int N = appWidgetIds.length;

        for (int i = 0; i < N; i++) {                                                               // Does set up for all instances of this Widget
            int appWidgetId = appWidgetIds[i];

            Intent intent = new Intent();                                                           // Set up a Pending Intent to send an intent to LightService when the button is clicked
            intent.setAction("com.chaspe.simplelight.LightService");
            PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, 0);
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.light_widget);
            views.setOnClickPendingIntent(R.id.widget_button, pendingIntent);

            appWidgetManager.updateAppWidget(appWidgetId, views);                                   // Tell the AppWidgetManager to perform an update on the current app widget
        }
    }
}

