package com.nearbybitcoinacceptingshops;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

public class WidgetProvider extends AppWidgetProvider {

	public static final String LAUNCH_ACTION = "com.nearbybitcoinacceptingshops.LAUNCH_ACTION";
	public static final String URI_ITEM = "com.nearbybitcoinacceptingshops.URI_ITEM";

	// Called when the BroadcastReceiver receives an Intent broadcast.
	// Checks to see whether the intent's action is TOAST_ACTION. If it is, the
	// app widget
	// displays a Toast message for the current item.
	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(LAUNCH_ACTION)) {
			String intentURI = intent.getStringExtra(URI_ITEM);
			Intent launchIntent = new Intent(
					android.content.Intent.ACTION_VIEW, Uri.parse(intentURI));
			launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(launchIntent);
		}
		super.onReceive(context, intent);
	}

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

			// This section makes it possible for items to have individualized
			// behavior.
			// It does this by setting up a pending intent template. Individuals
			// items of a collection
			// cannot set up their own pending intents. Instead, the collection
			// as a whole sets
			// up a pending intent template, and the individual items set a
			// fillInIntent
			// to create unique behavior on an item-by-item basis.
			Intent toastIntent = new Intent(context, WidgetProvider.class);
			// Set the action for the intent.
			// When the user touches a particular view, it will have the effect
			// of
			// broadcasting TOAST_ACTION.
			toastIntent.setAction(WidgetProvider.LAUNCH_ACTION);
			toastIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
					appWidgetIds[i]);
			intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
			PendingIntent toastPendingIntent = PendingIntent.getBroadcast(
					context, 0, toastIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			rv.setPendingIntentTemplate(R.id.listview, toastPendingIntent);

			// Tell the AppWidgetManager to perform an update on the current app
			// widget
			appWidgetManager.updateAppWidget(appWidgetId, rv);
		}
	}

}
