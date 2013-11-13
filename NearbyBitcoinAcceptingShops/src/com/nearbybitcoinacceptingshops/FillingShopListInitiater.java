package com.nearbybitcoinacceptingshops;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

public class FillingShopListInitiater {

	public static void initiateFillingShopList(Context context,
			IUseAsyncJSONTaskResult asyncTaskResultUser) {

		if (!FillingShopListInitiater.hasInternetConnection(context)) {
			Toast.makeText(context, "Can not connect to the internet.",
					Toast.LENGTH_SHORT).show();
			return;
		}

		ProgressDialog progressDialog = new ProgressDialog(context);
		progressDialog.setTitle("Fetching shop data...");
		progressDialog.setMessage("Please wait.");
		progressDialog.setCancelable(false);
		progressDialog.setIndeterminate(true);
		progressDialog.show();
		new OSMPullService(asyncTaskResultUser, progressDialog).execute();
	}

	private static boolean hasInternetConnection(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		return (activeNetwork != null && activeNetwork
				.isConnectedOrConnecting());
	}

}
