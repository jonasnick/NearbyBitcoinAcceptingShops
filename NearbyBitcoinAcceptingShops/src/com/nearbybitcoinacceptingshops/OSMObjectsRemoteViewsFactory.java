package com.nearbybitcoinacceptingshops;

import java.util.ArrayList;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService.RemoteViewsFactory;
import android.widget.Toast;

public class OSMObjectsRemoteViewsFactory implements RemoteViewsFactory,
		IUseAsyncTaskResult<ArrayList<OSMObject>> {
	private final Context context;
	private ArrayList<OSMObject> data;

	public OSMObjectsRemoteViewsFactory(Context context, Intent intent) {
		this.context = context;
		this.data = new ArrayList<OSMObject>();

	}

	@Override
	public void onCreate() {
		new GetShopListAsync(this, this.context).execute();
	}

	@Override
	public void updateAsyncTaskResult(
			AsyncTaskResult<ArrayList<OSMObject>> result) {
		if (result.getResult() != null) {
			this.data = result.getResult();

			AppWidgetManager appWidgetManager = AppWidgetManager
					.getInstance(context);
			int appWidgetIds[] = appWidgetManager
					.getAppWidgetIds(new ComponentName(context,
							WidgetProvider.class));
			appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds,
					R.id.listview);
			Log.d("OSMObjectsRemoteViewsFactory", "Updated AsyncTask result.");
		} else {
			Toast.makeText(context, result.getError().toString(),
					Toast.LENGTH_SHORT).show();
		}

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
