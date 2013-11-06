package com.nearbybitcoinacceptingshops;

import org.json.JSONArray;

import android.content.Context;
import android.location.Location;
import android.widget.ExpandableListView;

public class ConstructListView implements IUseAsyncJSONTaskResult {
	private Context context;
	private ExpandableListView listView;
	private Location userLocation;

	ConstructListView(Context context, ExpandableListView listView,
			Location userLocation) {
		this.context = context;
		this.listView = listView;
		this.userLocation = userLocation;
	}

	@Override
	public void update(AsyncTaskResult<JSONArray> result) {
		if (result.getResult() != null) {
			JSONArrayAdapter adapter = new JSONArrayAdapter(this.context,
					result.getResult(), this.userLocation);
			// this.outputView.setText(result.getResult().toString());
			this.listView.setAdapter(adapter);
		} else {
			// this.outputView.setText(result.getError().toString());
		}
	}

}
