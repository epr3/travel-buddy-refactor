package ase.com.travel_buddy.Widgets;

import android.content.Context;
import android.database.Cursor;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.ArrayList;

import ase.com.travel_buddy.Models.Moment;
import ase.com.travel_buddy.R;
import ase.com.travel_buddy.Utils.AppContentProvider;

public class TravelBuddyWidgetFactory implements RemoteViewsService.RemoteViewsFactory {
    private ArrayList<Moment> momentList;
    private Context context;
    private Cursor data;

    public TravelBuddyWidgetFactory(Context context) {
        this.context = context;
    }

    @Override
    public void onCreate() {
        data = context.getContentResolver().query(AppContentProvider.URI_MOMENTS, null, null, null);
        momentList = new ArrayList<>();
        while (data.moveToNext()) {
            Moment moment = new Moment();
            moment.setLongitude(data.getDouble(data.getColumnIndex(Moment.COLUMN_LONGITUDE)));
            moment.setLatitude(data.getDouble(data.getColumnIndex(Moment.COLUMN_LATITUDE)));
            moment.setDescription(data.getString(data.getColumnIndex(Moment.COLUMN_DESCRIPTION)));
            momentList.add(moment);
        }
        data.close();
    }

    @Override
    public void onDataSetChanged() {

        data = context.getContentResolver().query(AppContentProvider.URI_MOMENTS, null, null, null);
        momentList = new ArrayList<>();
        while (data.moveToNext()) {
            Moment moment = new Moment();
            moment.setLongitude(data.getDouble(data.getColumnIndex(Moment.COLUMN_LONGITUDE)));
            moment.setLatitude(data.getDouble(data.getColumnIndex(Moment.COLUMN_LATITUDE)));
            moment.setDescription(data.getString(data.getColumnIndex(Moment.COLUMN_DESCRIPTION)));
            momentList.add(moment);
        }
        data.close();
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return momentList.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.travel_buddy_widget_row);
        rv.setTextViewText(R.id.moment_widget_location, "Latitude: " +
                momentList.get(position).getLatitude() +
                " Longitude: " +
                momentList.get(position).getLongitude());
        rv.setTextViewText(R.id.moment_widget_description, momentList.get(position).getDescription());

        return rv;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
