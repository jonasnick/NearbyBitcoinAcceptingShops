package com.nearbybitcoinacceptingshops;

import org.json.JSONException;
import org.json.JSONObject;

import android.location.Location;

public class OSMObject {
	private JSONObject completeObject;
	private JSONObject tags;
	private Location location;
	private float distanceToUser;

	public OSMObject(JSONObject object, Location userLocation)
			throws JSONException {
		super();
		this.completeObject = object;
		this.tags = object.optJSONObject("tags");

		/*
		 * ways don't have a location on their own, but they
		 */
		if (object.optString("type") == "way") {

		} else {
			double longitude = object.getDouble("lon");
			double latitude = object.getDouble("lat");
			this.location = new Location("OSM");
			this.location.setLongitude(longitude);
			this.location.setLatitude(latitude);
		}

		this.distanceToUser = userLocation.distanceTo(this.location);
	}

	public JSONObject getObject() {
		return completeObject;
	}

	public Location getLocation() {
		return location;
	}

	public float getDistanceToUser() {
		return distanceToUser;
	}

	public boolean hasTags() {
		return this.tags != null;
	}

	public boolean hasName() {
		return this.hasTags() && this.tags.has("name");
	}

	public String getName() {
		return this.tags.optString("name");
	}

	public boolean hasCity() {
		return this.hasTags() && this.tags.has("addr:city");
	}

	public String getCity() {
		return this.tags.optString("addr:city");
	}

	/*
	 * some nodes consist only of langitude, lattitude, type and id, without any
	 * additional information, return true for them
	 */
	public boolean isDumbNode() {
		return this.completeObject.length() <= 4;
	}

	public boolean hasWebsite() {
		return this.hasTags() && this.tags.has("website");
	}

	public String getWebsite() {
		return this.tags.optString("website");
	}

	public boolean hasAddress() {
		return this.hasTags() && this.tags.has("addr:housenumber")
				&& this.tags.has("addr:street")
				&& this.tags.has("addr:postcode") && this.tags.has("addr:city");
	}

	public String getAddress() {
		return this.tags.optString("addr:street") + " "
				+ this.tags.optString("addr:housenumber") + ", "
				+ this.tags.optString("addr:postcode") + " "
				+ this.tags.optString("addr:city");
	}

}
