package ase.com.travel_buddy.Services;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.Map;
import java.util.Set;

import ase.com.travel_buddy.Auth.LoginActivity;
import ase.com.travel_buddy.Models.Moment;
import ase.com.travel_buddy.R;
import ase.com.travel_buddy.Utils.AppContentProvider;
import ase.com.travel_buddy.Utils.SharedPreferencesBuilder;

public class GetService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        String idToken = SharedPreferencesBuilder.getSharedPreference(getApplicationContext(), "access_token");
        String userId = SharedPreferencesBuilder.getSharedPreference(getApplicationContext(), "user_id");
        Ion.with(getApplicationContext())
                .load(getString(R.string.firebase_db) + "points.json?orderBy=\"user_id\"&equalTo=\"" + userId + "\"&auth=" + idToken)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if (result.get("error") == null) {
                            Set<Map.Entry<String, JsonElement>> entrySet = result.entrySet();
                            for(Map.Entry<String,JsonElement> entry : entrySet){
                                JsonObject object = (JsonObject) result.get(entry.getKey());
                                ContentValues contentValues = new ContentValues();
                                contentValues.put(Moment.COLUMN_LONGITUDE, object.get(Moment.COLUMN_LONGITUDE).getAsDouble());
                                contentValues.put(Moment.COLUMN_LATITUDE, object.get(Moment.COLUMN_LATITUDE).getAsDouble());
                                contentValues.put(Moment.COLUMN_DESCRIPTION, object.get(Moment.COLUMN_DESCRIPTION).getAsString());
                                contentValues.put(Moment.COLUMN_IMAGE, object.get(Moment.COLUMN_IMAGE).getAsString());
                                contentValues.put(Moment.COLUMN_ICON, object.get(Moment.COLUMN_ICON).getAsString());
                                contentValues.put(Moment.COLUMN_USER_ID, SharedPreferencesBuilder.getSharedPreference(getApplicationContext(), "user_id"));
                                contentValues.put(Moment.COLUMN_API_ID, entry.getKey());
                                contentValues.put(Moment.COLUMN_CREATED_AT, object.get(Moment.COLUMN_CREATED_AT).getAsLong());
                                contentValues.put(Moment.COLUMN_UPDATED_AT, object.get(Moment.COLUMN_UPDATED_AT).getAsLong());
                                getContentResolver().insert(AppContentProvider.URI_MOMENTS, contentValues);
                            }
                        } else {
                            if (result.get("error").getAsString().equals("Auth token is expired")) {
                                SharedPreferences spreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                SharedPreferences.Editor editor = spreferences.edit();
                                editor.remove("access_token");
                                editor.commit();
                                startActivity(new Intent(GetService.this, LoginActivity.class));
                                stopSelf();
                            }
                        }
                        Intent broadcastIntent = new Intent();
                        broadcastIntent.setAction("Finished get");
                        sendBroadcast(broadcastIntent);
                        stopSelf();
                    }
                });
        return super.onStartCommand(intent, flags, startId);
    }
}
