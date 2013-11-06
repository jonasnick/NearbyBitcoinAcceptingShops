package com.nearbybitcoinacceptingshops;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.os.AsyncTask;

public class OSMPullService extends
		AsyncTask<Void, Void, AsyncTaskResult<JSONArray>> {

	private IUseAsyncJSONTaskResult resultUser;
	private ProgressDialog progressDialog;

	public OSMPullService(IUseAsyncJSONTaskResult resultUser,
			ProgressDialog progressDialog) {
		this.resultUser = resultUser;
		this.progressDialog = progressDialog;
	}

	@Override
	protected AsyncTaskResult<JSONArray> doInBackground(Void... arg0) {
		try {
			URL url = new URL(
					"http://overpass.osm.rambler.ru/cgi/interpreter?data=[out:json];%28node[%22payment:bitcoin%22=yes];way[%22payment:bitcoin%22=yes];%3E;%29;out;");
			HttpURLConnection urlConnection = (HttpURLConnection) url
					.openConnection();

			InputStream in = new BufferedInputStream(
					urlConnection.getInputStream());
			String result = this.readStream(in);

			JSONObject jsonObject = new JSONObject(result);
			JSONArray jsonArray = jsonObject.getJSONArray("elements");

			return new AsyncTaskResult<JSONArray>(jsonArray);
		} catch (Exception e) {
			return new AsyncTaskResult<JSONArray>(e);
		}

	}

	@Override
	protected void onPostExecute(AsyncTaskResult<JSONArray> result) {
		this.progressDialog.dismiss();
		this.resultUser.update(result);
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
