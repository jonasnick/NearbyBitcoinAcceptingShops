package com.example.nearbybitcoinacceptingshops;

import android.app.Activity;
import android.app.Dialog;
import android.content.IntentSender;
import android.os.Bundle;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;

public class MainActivity extends Activity implements
		GooglePlayServicesClient.ConnectionCallbacks,
		GooglePlayServicesClient.OnConnectionFailedListener {
	private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
	private LocationClient mLocationClient;
	private ExpandableListView listView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		this.listView = (ExpandableListView) findViewById(R.id.listview);

		/*
		 * Create a new location client, using the enclosing class to handle
		 * callbacks.
		 */
		this.mLocationClient = new LocationClient(this, this, this);
	}

	/*
	 * Called when the Activity becomes visible.
	 */
	@Override
	protected void onStart() {
		super.onStart();
		// Connect the client.
		mLocationClient.connect();
	}

	/*
	 * Called when the Activity is no longer visible.
	 */
	@Override
	protected void onStop() {
		// Disconnecting the client invalidates it.
		mLocationClient.disconnect();
		super.onStop();
	}

	/*
	 * Called by Location Services when the request to connect the client
	 * finishes successfully. At this point, you can request the current
	 * location or start periodic updates
	 */
	@Override
	public void onConnected(Bundle arg0) {
		// Display the connection status
		Toast.makeText(this, mLocationClient.getLastLocation().toString(),
				Toast.LENGTH_SHORT).show();

		new OSMPullService(new ConstructListView(this, this.listView,
				mLocationClient.getLastLocation())).execute();

	}

	/*
	 * Called by Location Services if the connection to the location client
	 * drops because of an error.
	 */
	@Override
	public void onDisconnected() {
		// Display the connection status
		Toast.makeText(this, "Disconnected. Please re-connect.",
				Toast.LENGTH_SHORT).show();

	}

	/*
	 * Called by Location Services if the attempt to Location Services fails.
	 */
	@Override
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
