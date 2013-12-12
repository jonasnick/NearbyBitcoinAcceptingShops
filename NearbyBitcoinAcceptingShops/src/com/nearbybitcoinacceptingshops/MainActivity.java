package com.nearbybitcoinacceptingshops;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

public class MainActivity extends Activity {
	private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
	private ExpandableListView listView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		this.listView = (ExpandableListView) findViewById(R.id.listview);

		Intent intent = getIntent();

		final ArrayList<String> uris = intent.getStringArrayListExtra("fromWidget");
		if(uris!=null) {
			ArrayList<String> names = new ArrayList<String>();
			names.add("Google Maps");names.add("OpenStreetMap Entry");
			if(uris.size()>2) names.add("Website");
			
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
		    builder.setTitle("Choose Option")
	           .setItems(names.toArray(new String[names.size()]), new DialogInterface.OnClickListener() {
	               public void onClick(DialogInterface dialog, int which) {
	            	   Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
	       					Uri.parse(uris.get(which)));
		       			startActivity(intent);
	               }
		    });
		    builder.create();
		    builder.show();
			
		} else {
			this.updateData();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch (item.getItemId()) {
		case R.id.action_settings:
			openSettings();
			return true;
		case R.id.action_refresh:
			this.updateData();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void openSettings() {
		// TODO:

	}

	private void updateData() {
		class AsyncResultUser implements
				IUseAsyncTaskResult<ArrayList<OSMObject>> {
			private Context context;
			private ProgressDialog progressDialog;

			public AsyncResultUser(Context context,
					ProgressDialog progressDialog) {
				this.context = context;
				this.progressDialog = progressDialog;
			}

			@Override
			public void updateAsyncTaskResult(
					AsyncTaskResult<ArrayList<OSMObject>> result) {
				if (result.getResult() != null) {
					listView.setAdapter(new OSMObjectsExpandableListAdapter(
							this.context, result.getResult(), listView));
				} else {
					Toast.makeText(this.context, result.getError().toString(),
							Toast.LENGTH_LONG).show();
				}
				this.progressDialog.dismiss();

			}

		}
		ProgressDialog progressDialog = new ProgressDialog(this);
		progressDialog.setTitle("Fetching shop data...");
		progressDialog.setMessage("Please wait.");
		progressDialog.setCancelable(false);
		progressDialog.setIndeterminate(true);
		progressDialog.show();
		new GetShopListAsync(new AsyncResultUser(this, progressDialog), this)
				.execute();

	}

	/*
	 * Called when the Activity becomes visible.
	 */
	@Override
	protected void onStart() {
		super.onStart();
	}

	/*
	 * Called when the Activity is no longer visible.
	 */
	@Override
	protected void onStop() {
		// Disconnecting the client invalidates it.
		super.onStop();
	}

	/*
	 * Called by Location Services if the attempt to Location Services fails.
	 */
	public void onConnectionFailed(ConnectionResult connectionResult) {
		/*
		 * Google Play services can resolve some errors it detects. If the error
		 * has a resolution, try sending an Intent to start a Google Play
		 * services activity that can resolve error.
		 */
		if (connectionResult.hasResolution()) {
			try {
				// Start an Activity that tries to resolve the error
				connectionResult.startResolutionForResult(this,
						CONNECTION_FAILURE_RESOLUTION_REQUEST);
				/*
				 * Thrown if Google Play services canceled the original
				 * PendingIntent
				 */
			} catch (IntentSender.SendIntentException e) {
				// Log the error
				e.printStackTrace();
			}
		} else {
			/*
			 * If no resolution is available, display a dialog to the user with
			 * the error.
			 */
			// showErrorDialog(connectionResult.getErrorCode());
			// Toast.makeText(this,
			// "Connection to Google Play services failed with code " +
			// connectionResult.getErrorCode(), Toast.LENGTH_SHORT).show();
			Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(
					connectionResult.getErrorCode(), this, RESULT_OK);
			errorDialog.show();
		}

	}

}
