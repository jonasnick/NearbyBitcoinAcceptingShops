package com.nearbybitcoinacceptingshops;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

public class OSMObjectsExpandableListAdapter extends BaseExpandableListAdapter {

	private final Context context;
	private ArrayList<OSMObject> data;

	public OSMObjectsExpandableListAdapter(Context context,
			ArrayList<OSMObject> data) {
		super();
		this.context = context;
		this.data = data;
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return null;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	private class MapListener implements OnClickListener {
		private OSMObject obj;

		public MapListener(OSMObject obj) {
			this.obj = obj;
		}

		@Override
		public void onClick(View v) {
			if (obj.hasAddress()) {
				Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
						Uri.parse("http://maps.google.com/maps?q="
								+ this.obj.getName() + " "
								+ this.obj.getAddress()));
				v.getContext().startActivity(intent);

			} else {
				Location storeLocation = this.obj.getLocation();
				String storeName = "";
				if (this.obj.hasName())
					storeName = this.obj.getName();

				String sLocation = Double.toString(storeLocation.getLatitude())
						+ "," + Double.toString(storeLocation.getLongitude());
				Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
						Uri.parse("http://maps.google.com/maps?q=loc:"
								+ sLocation + " " + "(" + storeName + ")"));
				v.getContext().startActivity(intent);
			}
		}

	}

	private class OSMClickListener implements OnClickListener {
		private OSMObject obj;

		public OSMClickListener(OSMObject obj) {
			this.obj = obj;
		}

		@Override
		public void onClick(View v) {
			String sType = obj.getObject().optString("type");
			Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
					Uri.parse("http://www.openstreetmap.org/browse/" + sType
							+ "/" + obj.getObject().optString("id")));
			v.getContext().startActivity(intent);

		}

	}

	private class WebsiteListener implements OnClickListener {
		private OSMObject obj;

		public WebsiteListener(OSMObject obj) {
			this.obj = obj;
		}

		@Override
		public void onClick(View v) {
			if (obj.hasWebsite()) {
				Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
						Uri.parse(obj.getWebsite()));
				v.getContext().startActivity(intent);
			}
		}

	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		TextView rowView = (TextView) inflater.inflate(
				android.R.layout.simple_list_item_1, parent, false);
		OSMObject obj = this.data.get(groupPosition);

		switch (childPosition) {
		case 0:
			rowView.setText("Google Maps");
			MapListener mapListener = new MapListener(obj);
			rowView.setOnClickListener(mapListener);
			break;
		case 1:
			rowView.setText("OpenStreetMap");
			OSMClickListener osmClickListener = new OSMClickListener(obj);
			rowView.setOnClickListener(osmClickListener);
			break;
		case 2:
			String website = "";
			if (obj.hasWebsite())
				website = obj.getWebsite();
			WebsiteListener siteListener = new WebsiteListener(obj);
			rowView.setOnClickListener(siteListener);
			rowView.setText(website);
			break;
		}

		return rowView;

	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return (this.data.get(groupPosition).hasWebsite() ? 3 : 2);
	}

	@Override
	public Object getGroup(int groupPosition) {
		return this.data.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return this.data.size();
	}

	@Override
	public long getGroupId(int position) {
		return position;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		TextView rowView = (TextView) inflater.inflate(
				android.R.layout.simple_expandable_list_item_1, parent, false);
		OSMObject obj = this.data.get(groupPosition);
		String sDistanceToUser = Float.toString((float) Math.round(obj
				.getDistanceToUser() / 100) / 10);

		String name = "";
		String city = "";
		if (obj.hasName())
			name = obj.getName();
		if (obj.hasCity())
			city = "in " + obj.getCity();

		String text = sDistanceToUser + "km " + name + " " + city;
		// + obj.getObject().toString();

		rowView.setText(text);

		return rowView;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

}
