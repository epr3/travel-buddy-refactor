package ase.com.travel_buddy.Services;


import android.content.Intent;
import android.widget.RemoteViewsService;

import ase.com.travel_buddy.Widgets.TravelBuddyWidgetFactory;


public class WidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new TravelBuddyWidgetFactory(getApplicationContext());
    }
}