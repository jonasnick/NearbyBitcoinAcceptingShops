package com.nearbybitcoinacceptingshops;

import android.content.Intent;
import android.widget.RemoteViewsService;

public class NBASWidgetService extends RemoteViewsService {
	@Override
	public RemoteViewsFactory onGetViewFactory(Intent intent) {
		return new OSMObjectsRemoteViewsFactory(this.getApplicationContext(),
				intent);
	}

}
