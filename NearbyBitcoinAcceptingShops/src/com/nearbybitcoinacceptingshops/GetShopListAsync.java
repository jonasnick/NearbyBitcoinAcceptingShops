package com.nearbybitcoinacceptingshops;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;

public class GetShopListAsync extends
		AsyncTask<Void, Void, AsyncTaskResult<ArrayList<OSMObject>>> {
	private Location lastLocation;
	private JSONArray shopJsonArray;
	private Object syncObject = new Object();
	private Context context;
	private IUseAsyncTaskResult<ArrayList<OSMObject>> asyncTaskResultUser;

	public GetShopListAsync(
			IUseAsyncTaskResult<ArrayList<OSMObject>> asyncTaskResultUser,
			Context context) {
		this.asyncTaskResultUser = asyncTaskResultUser;
		this.context = context;
	}

	private class LocationClientSync implements
			GooglePlayServicesClient.ConnectionCallbacks,
			GooglePlayServicesClient.OnConnectionFailedListener, Runnable {
		private LocationClient locationClient;
		private GetShopListAsync parentObject;
		private Context context;

		LocationClientSync(GetShopListAsync parentObject, Context context) {
			this.parentObject = parentObject;
			this.context = context;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			int googlePlayServicesCheck = GooglePlayServicesUtil
					.isGooglePlayServicesAvailable(context);
			if (googlePlayServicesCheck != ConnectionResult.SUCCESS) {
				GooglePlayServicesUtil.getErrorPendingIntent(
						googlePlayServicesCheck, context,
						android.app.Activity.RESULT_OK);
			} else {
				/*
				 * Create a new location client, using the enclosing class to
				 * handle callbacks.
				 */
				this.locationClient = new LocationClient(context, this, this);
				this.locationClient.connect();
				Log.d("LocationClientSync", "Launched location client");
			}

		}

		@Override
		public void onConnectionFailed(ConnectionResult arg0) {
			// TODO Auto-generated method stub
			this.parentObject.setLastLocation(null);
			this.locationClient.disconnect();
			this.parentObject.continueFlow();

		}

		@Override
		public void onConnected(Bundle arg0) {
			this.parentObject.setLastLocation(this.locationClient
					.getLastLocation());
			this.parentObject.continueFlow();

		}

		@Override
		public void onDisconnected() {
			// TODO Auto-generated method stub

		}

	}

	private AsyncTaskResult<JSONArray> getOnlineData() {
		try {
			Log.d("LocationClientSync", "doInBackground begin");
			URL url = new URL(
					"http://overpass.osm.rambler.ru/cgi/interpreter?data=[out:json];%28node[%22payment:bitcoin%22=yes];way[%22payment:bitcoin%22=yes];%3E;%29;out;");
			HttpURLConnection urlConnection = (HttpURLConnection) url
					.openConnection();

			InputStream in = new BufferedInputStream(
					urlConnection.getInputStream());
			String result = this.readStream(in);

			JSONObject jsonObject = new JSONObject(result);
			JSONArray jsonArray = jsonObject.getJSONArray("elements");
			Log.d("LocationClientSync", "doInBackground end");

			return new AsyncTaskResult<JSONArray>(jsonArray);
		} catch (Exception e) {
			return new AsyncTaskResult<JSONArray>(e);
		}
	}

	public void setLastLocation(Location lastLocation) {
		this.lastLocation = lastLocation;
		Log.d("LocationClientSync", "Set last location");
	}

	public void setShopJsonArray(JSONArray shopJsonArray) {
		this.shopJsonArray = shopJsonArray;
		Log.d("LocationClientSync", "Set shopJsonArray");
	}

	public void continueFlow() {
		synchronized (this.syncObject) {
			this.syncObject.notify();
		}
	}

	@Override
	protected AsyncTaskResult<ArrayList<OSMObject>> doInBackground(
			Void... params) {
		Log.d("LocationClientSync", "Initiated getShopList");
		// connect to location client
		Thread t = new Thread(new LocationClientSync(this, context));
		t.start();
		synchronized (this.syncObject) {
			try {
				this.syncObject.wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				return new AsyncTaskResult<ArrayList<OSMObject>>(e);
			}
		}
		Log.d("LocationClientSync", "Return from LocationClientSync call");

		if (!GetShopListAsync.hasInternetConnection(this.context))
			return new AsyncTaskResult<ArrayList<OSMObject>>(new Exception(
					"No internet connection"));
		AsyncTaskResult<JSONArray> result = getOnlineData();
		if (result.getResult() == null)
			return new AsyncTaskResult<ArrayList<OSMObject>>(result.getError());
		this.shopJsonArray = result.getResult();

		Log.d("LocationClientSync", "Return from OSMPullService call");

		if (this.lastLocation != null) {
			ArrayList<OSMObject> list = OSMObjectsProvider.extractOSMObjects(
					this.shopJsonArray, this.lastLocation);
			Log.d("LocationClientSync", "list ele " + list.get(0).getName());
			return (new AsyncTaskResult<ArrayList<OSMObject>>(list));
		} else
			return new AsyncTaskResult<ArrayList<OSMObject>>(new Exception(
					"Can't get location"));
	}

	protected void onPostExecute(AsyncTaskResult<ArrayList<OSMObject>> result) {
		this.asyncTaskResultUser.updateAsyncTaskResult(result);

	}

	private static boolean hasInternetConnection(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		return (activeNetwork != null && activeNetwork
				.isConnectedOrConnecting());
	}

	private String readStream(InputStream is) {
		try {
			ByteArrayOutputStream bo = new ByteArrayOutputStream();
			int i = is.read();
			while (i != -1) {
				bo.write(i);
				i = is.read();
			}
			return bo.toString();
		} catch (IOException e) {
			return "";
		}
	}

}
