package com.nearbybitcoinacceptingshops;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

public class WidgetProvider extends AppWidgetProvider {

	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		// Perform this loop procedure for each App Widget that belongs to this
		// provider
		for (int i = 0; i < appWidgetIds.length; i++) {
			int appWidgetId = appWidgetIds[i];

			// Set up the intent that starts the StackViewService, which will
			// provide the views for this collection.
			Intent intent = new Intent(context, NBASWidgetService.class);
			// Add the app widget ID to the intent extras.
			intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
					appWidgetIds[i]);
			intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

			// Get the layout for the App Widget
			RemoteViews rv = new RemoteViews(context.getPackageName(),
					R.layout.appwidget);

			// Set up the RemoteViews object to use a RemoteViews adapter.
			// This adapter connects
			// to a RemoteViewsService through the specified intent.
			// This is how you populate the data.
			rv.setRemoteAdapter(R.id.listview, intent);

			// The empty view is displayed when the collection has no items.
			// It should be in the same layout used to instantiate the
			// RemoteViews
			// object above.
			rv.setEmptyView(R.id.listview, R.id.empty_view);
			// views.setTextViewText(R.id.listview,"text123");

			// Tell the AppWidgetManager to perform an update on the current app
			// widget
			appWidgetManager.updateAppWidget(appWidgetId, rv);
		}
	}

}
