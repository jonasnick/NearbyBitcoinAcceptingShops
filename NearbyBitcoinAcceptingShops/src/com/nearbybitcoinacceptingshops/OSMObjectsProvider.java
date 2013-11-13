package com.nearbybitcoinacceptingshops;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.location.Location;
import android.util.Log;

public class OSMObjectsProvider {
	private static class OSMObjectComparator implements Comparator<OSMObject> {
		@Override
		public int compare(OSMObject obj1, OSMObject obj2) {
			return Float.compare(obj1.getDistanceToUser(),
					obj2.getDistanceToUser());
		}

	}

	public static ArrayList<OSMObject> extractOSMObjects(JSONArray jsonArray,
			Location userLocation) {
		/*
		 * first round of data processing, find all the nodes that don't have
		 * bitcoin as payment option. They are most likely only there to define
		 * "ways"
		 */
		ArrayList<OSMObject> data = new ArrayList<OSMObject>(); // return list
		ArrayList<JSONObject> nodesAndWays = new ArrayList<JSONObject>();
		HashMap<String, JSONObject> wayDefiningNodes = new HashMap<String, JSONObject>();
		for (int i = 0; i < jsonArray.length(); i++) {
			try {
				JSONObject obj = jsonArray.getJSONObject(i);
				if (!obj.has("id")) {
					// bogus data
					Log.d("JSONArrayAdapter", "Bogus:" + obj.toString());
					continue;
				} else if (!obj.has("tags")
						|| (obj.has("tags") && !(obj.optJSONObject("tags")
								.optString("payment:bitcoin").equals("yes")))) {
					wayDefiningNodes.put(obj.getString("id"), obj);
				} else {
					nodesAndWays.add(obj);
				}
			} catch (JSONException e) {
				continue;
			}

		}

		for (int i = 0; i < nodesAndWays.size(); i++) {
			try {
				JSONObject obj = nodesAndWays.get(i);
				/*
				 * Ways don't have a location (latitude, longitude) on their
				 * own. Only the nodes that they provide in an Array have a
				 * location.
				 */
				if (obj.optString("type").equals("way")) {
					JSONArray nodes = obj.optJSONArray("nodes");
					for (int j = 0; j < nodes.length(); j++) {
						try {
							String nodeId = nodes.getString(j);
							if (!wayDefiningNodes.containsKey(nodeId))
								continue;
							JSONObject node = wayDefiningNodes.get(nodeId);
							obj.put("lat", node.optDouble("lat"));
							obj.put("lon", node.optDouble("lon"));
						} catch (JSONException e) {
							continue;
						}
					}
				}

				OSMObject oObj = new OSMObject(obj, userLocation);
				data.add(oObj);
			} catch (JSONException e) {
				continue;
			}
		}
		Collections.sort(data, new OSMObjectComparator());
		return data;
	}
}
