package com.nearbybitcoinacceptingshops;

import java.util.ArrayList;

import org.json.JSONArray;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService.RemoteViewsFactory;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;

public class OSMObjectsRemoteViewsFactory implements RemoteViewsFactory,
		IUseAsyncJSONTaskResult, GooglePlayServicesClient.ConnectionCallbacks,
		GooglePlayServicesClient.OnConnectionFailedListener {
	private final Context context;
	private ArrayList<OSMObject> data;
	private LocationClient locationClient;
	private int appWidgetId;

	public OSMObjectsRemoteViewsFactory(Context context, Intent intent) {
		this.context = context;
		this.appWidgetId = intent.getIntExtra(
				AppWidgetManager.EXTRA_APPWIDGET_ID,
				AppWidgetManager.INVALID_APPWIDGET_ID);
		this.data = new ArrayList<OSMObject>();
	}

	@Override
	public void onCreate() {
		int googlePlayServicesCheck = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(this.context);
		if (googlePlayServicesCheck != ConnectionResult.SUCCESS) {
			GooglePlayServicesUtil.getErrorPendingIntent(
					googlePlayServicesCheck, this.context,
					android.app.Activity.RESULT_OK);
			Toast.makeText(context, "cant connect",
					Toast.LENGTH_SHORT).show();
		} else {
			/*
			 * Create a new location client, using the enclosing class to handle
			 * callbacks.
			 */
			this.locationClient = new LocationClient(this.context, this, this);
		}

	}

	@Override
	public void onConnected(Bundle arg0) {
		FillingShopListInitiater.initiateFillingShopList(this.context, this);

	}

	@Override
	public void updateAsyncTaskResult(AsyncTaskResult<JSONArray> result) {
		if (result.getResult() != null) {
			this.data = OSMObjectsProvider.extractOSMObjects(
					result.getResult(), this.locationClient.getLastLocation());
			
			AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
			int appWidgetIds[] = appWidgetManager.getAppWidgetIds(
			                           new ComponentName(context, WidgetProvider.class));
			appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.listview);
		} else {
			Toast.makeText(context, result.getError().toString(),
					Toast.LENGTH_SHORT).show();
		}

	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		Toast.makeText(context, "cant connect",
				Toast.LENGTH_SHORT).show();

	}

	@Override
	public void onDisconnected() {
		Toast.makeText(context, "cant connect",
				Toast.LENGTH_SHORT).show();

	}

	@Override
	public int getCount() {
		return this.data.size();
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public RemoteViews getLoadingView() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RemoteViews getViewAt(int position) {
		// Construct a remote views item based on the app widget item XML file,
		// and set the text based on the position.
		RemoteViews rv = new RemoteViews(this.context.getPackageName(),
				R.layout.widget_item);
		rv.setTextViewText(R.id.widget_item, this.data.get(position).getName());

		// Return the remote views object.
		return rv;
	}

	@Override
	public int getViewTypeCount() {
		// TODO Auto-generated method stub
		return 1;
	}

	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onDataSetChanged() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub

	}

}
