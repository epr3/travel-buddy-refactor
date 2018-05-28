package ase.com.travel_buddy.Services;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.os.IBinder;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.Future;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import ase.com.travel_buddy.Models.Moment;
import ase.com.travel_buddy.R;
import ase.com.travel_buddy.Utils.AppContentProvider;
import ase.com.travel_buddy.Utils.SharedPreferencesBuilder;

public class PostService extends Service {
    private Future<JsonObject> postTask = null;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        JsonObject postObject = new JsonObject();
        postObject.addProperty(Moment.COLUMN_LONGITUDE, intent.getStringExtra(Moment.COLUMN_LONGITUDE));
        postObject.addProperty(Moment.COLUMN_LATITUDE, intent.getStringExtra(Moment.COLUMN_LATITUDE));
        postObject.addProperty(Moment.COLUMN_IMAGE, intent.getStringExtra(Moment.COLUMN_IMAGE));
        postObject.addProperty(Moment.COLUMN_DESCRIPTION, intent.getStringExtra(Moment.COLUMN_DESCRIPTION));
        postObject.addProperty(Moment.COLUMN_USER_ID, SharedPreferencesBuilder.getSharedPreference(getApplicationContext(), "user_id"));
        postObject.addProperty(Moment.COLUMN_ICON, intent.getStringExtra(Moment.COLUMN_ICON));
        final Long created_at = System.currentTimeMillis() / 1000L;
        final Long updated_at = System.currentTimeMillis() / 1000L;
        postObject.addProperty(Moment.COLUMN_CREATED_AT, created_at);
        postObject.addProperty(Moment.COLUMN_UPDATED_AT, updated_at);

        String idToken = SharedPreferencesBuilder.getSharedPreference(getApplicationContext(), "access_token");
        postTask = Ion.with(getApplicationContext())
                .load(getString(R.string.firebase_db) + "points.json?auth=" + idToken)
                .setJsonObjectBody(postObject)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if (result.get("error") == null) {
                            ContentValues values = new ContentValues();
                            values.put(Moment.COLUMN_LONGITUDE, intent.getStringExtra(Moment.COLUMN_LONGITUDE));
                            values.put(Moment.COLUMN_LATITUDE, intent.getStringExtra(Moment.COLUMN_LATITUDE));
                            values.put(Moment.COLUMN_IMAGE, intent.getStringExtra(Moment.COLUMN_IMAGE));
                            values.put(Moment.COLUMN_ICON, intent.getStringExtra(Moment.COLUMN_ICON));
                            values.put(Moment.COLUMN_DESCRIPTION, intent.getStringExtra(Moment.COLUMN_DESCRIPTION));
                            values.put(Moment.COLUMN_USER_ID, SharedPreferencesBuilder.getSharedPreference(getApplicationContext(), "user_id"));
                            values.put(Moment.COLUMN_API_ID, result.get("name").getAsString());
                            values.put(Moment.COLUMN_CREATED_AT, created_at);
                            values.put(Moment.COLUMN_UPDATED_AT, updated_at);

                            getContentResolver().insert(AppContentProvider.URI_MOMENTS, values);
                        } else {
                            e.printStackTrace();
                        }
                        Intent broadcastIntent = new Intent();
                        broadcastIntent.setAction("Finished post");
                        sendBroadcast(broadcastIntent);
                        stopSelf();
                    }
                });
        return super.onStartCommand(intent, flags, startId);
    }
}
