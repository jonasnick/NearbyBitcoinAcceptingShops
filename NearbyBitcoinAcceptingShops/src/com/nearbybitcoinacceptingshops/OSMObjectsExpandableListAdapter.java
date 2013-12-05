package com.nearbybitcoinacceptingshops;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.TextView;

public class OSMObjectsExpandableListAdapter extends BaseExpandableListAdapter {

	private final Context context;
	private ArrayList<OSMObject> data;

	public OSMObjectsExpandableListAdapter(Context context,
			ArrayList<OSMObject> data, ExpandableListView listView) {
		super();
		this.context = context;
		this.data = data;
		listView.setOnGroupClickListener(new SetFamiliarListener());
	}

	private class SetFamiliarListener implements OnGroupClickListener {
	
			@Override
			public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
				OSMObject obj = data.get(groupPosition);
				ObjectsDatabaseOpener db = new ObjectsDatabaseOpener(v.getContext());
				if (!obj.isFamiliar(db.getReadableDatabase())) {
					obj.setFamiliar(db.getWritableDatabase());
					notifyDataSetChanged();
				}
				return false;
			}
	
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
			Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
					Uri.parse(this.obj.getGoogleMapsURI()));
			v.getContext().startActivity(intent);
		}

	}

	private class OSMClickListener implements OnClickListener {
		private OSMObject obj;

		public OSMClickListener(OSMObject obj) {
			this.obj = obj;
		}

		@Override
		public void onClick(View v) {
			Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
					Uri.parse(this.obj.getOSMURI()));
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
		case 3:
			rowView.setText(obj.getRawJSON().toString());
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
		int color = obj.isFamiliar(new ObjectsDatabaseOpener(this.context)
				.getReadableDatabase()) ? Color.BLACK : Color.MAGENTA;
		rowView.setTextColor(color);

		String city = "";
		if (obj.hasCity())
			city = "in " + obj.getCity();

		String text = obj.getDistanceText() + " " + city;
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
