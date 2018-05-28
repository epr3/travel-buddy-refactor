package ase.com.travel_buddy.Main;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.JsonObject;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;

import java.util.List;
import java.util.concurrent.Future;

import ase.com.travel_buddy.Adapters.CustomInfoWindowAdapter;
import ase.com.travel_buddy.Auth.LoginActivity;
import ase.com.travel_buddy.Database.TravelBuddyDatabase;
import ase.com.travel_buddy.Models.Moment;
import ase.com.travel_buddy.R;
import ase.com.travel_buddy.Services.GetService;
import ase.com.travel_buddy.Services.LocationService;
import ase.com.travel_buddy.Utils.AppContentProvider;
import ase.com.travel_buddy.Utils.SharedPreferencesBuilder;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private BroadcastReceiver broadcastReceiverLocation;
    private BroadcastReceiver broadcastReceiverGet;
    private BroadcastReceiver broadcastReceiverPost;
    private static final int REQUEST_CODE = 1000;
    private static final int ADD_MOMENT_REQUEST_CODE = 1001;
    private Location location;
    private Cursor data;
    private Future<JsonObject> getTask;
    private int dataCount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (SharedPreferencesBuilder.getSharedPreference(getApplicationContext(),"access_token").length() == 0) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        }
        setContentView(R.layout.activity_main);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        broadcastReceiverLocation = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                location = intent.getParcelableExtra("location");
            }
        };
        broadcastReceiverGet = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mMap.clear();
                data = getContentResolver().query(AppContentProvider.URI_MOMENTS, null, null, null, null);
                dataCount = data.getCount();
                setMarkers();
                unregisterReceiver(broadcastReceiverGet);
            }
        };
        broadcastReceiverPost = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mMap.clear();
                data = getContentResolver().query(AppContentProvider.URI_MOMENTS, null, null, null, null);
                dataCount = data.getCount();
                setMarkers();
            }
        };
        registerReceiver(broadcastReceiverPost, new IntentFilter("Finished post"));
        if (!checkPermissions()) {
            Intent intent = new Intent(MainActivity.this, LocationService.class);
            registerReceiver(broadcastReceiverLocation, new IntentFilter("location"));
            startService(intent);
        } else {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION
                    }, REQUEST_CODE);
        }

        shareMenuInitializer();

        data = getContentResolver().query(AppContentProvider.URI_MOMENTS, null, null, null, null);

        dataCount = data.getCount();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        setMarkers();

        CustomInfoWindowAdapter customInfoWindowAdapter = new CustomInfoWindowAdapter(this);

        mMap.setInfoWindowAdapter(customInfoWindowAdapter);
        if (data.getCount() == 0) {
            registerReceiver(broadcastReceiverGet, new IntentFilter("Finished get"));
            Intent serviceIntent = new Intent(getApplicationContext(), GetService.class);
            MainActivity.this.startService(serviceIntent);
        }

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Intent intent = new Intent(MainActivity.this, AddMomentActivity.class);
                intent.putExtra("latitude", String.format("%.2f", latLng.latitude));
                intent.putExtra("longitude", String.format("%.2f", latLng.longitude));
                startActivityForResult(intent, ADD_MOMENT_REQUEST_CODE);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(MainActivity.this, LocationService.class);
                    registerReceiver(broadcastReceiverLocation, new IntentFilter("location"));
                    startService(intent);
                }
                break;
        }
    }

    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED;
    }

    private void setMarkers() {
        if (data.getCount() != 0) {
            while (data.moveToNext()) {
                LatLng location = new LatLng(data.getDouble(data.getColumnIndex(Moment.COLUMN_LATITUDE)),
                        data.getDouble(data.getColumnIndex(Moment.COLUMN_LONGITUDE)));
                String description = data.getString(data.getColumnIndex(Moment.COLUMN_DESCRIPTION));
                String image = data.getString(data.getColumnIndex(Moment.COLUMN_IMAGE));
                String markerName = data.getString(
                        data.getColumnIndex(Moment.COLUMN_ICON));
                int resource = getResources()
                        .getIdentifier(markerName.toLowerCase(),
                                "drawable",
                                getPackageName());
                Bitmap img = BitmapFactory.decodeResource(getResources(), resource);
                mMap.addMarker(new MarkerOptions().position(location).title(description).snippet(image).icon(BitmapDescriptorFactory.fromBitmap(img)));
            }
            data.close();
        }
    }

    public void shareMenuInitializer()
    {
        final ImageView menuIcon = new ImageView(this);
        menuIcon.setImageResource(R.drawable.ic_menu_share);
        final com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton fab =
                new com.oguzdev.circularfloatingactionmenu.library.
                        FloatingActionButton.Builder(this)
                        .setContentView(menuIcon).build();

        SubActionButton.Builder builder = new SubActionButton.Builder(this);

        ImageView addIcon = new ImageView(this);
        addIcon.setImageResource(R.drawable.add);
        SubActionButton addBtn = builder.setContentView(addIcon).build();

        ImageView picIcon = new ImageView(this);
        picIcon.setImageResource(R.drawable.map);
        SubActionButton picBtn = builder.setContentView(picIcon).build();

        ImageView exitIcon = new ImageView(this);
        exitIcon.setImageResource(R.drawable.power);
        SubActionButton exitBtn = builder.setContentView(exitIcon).build();


        final FloatingActionMenu fam = new FloatingActionMenu.Builder(this)
                .addSubActionView(exitBtn)
                .addSubActionView(picBtn)
                .addSubActionView(addBtn)
                .attachTo(fab)
                .build();

        fam.setStateChangeListener(new FloatingActionMenu.MenuStateChangeListener() {
            @Override
            public void onMenuOpened(FloatingActionMenu floatingActionMenu) {
                menuIcon.setRotation(0);
                PropertyValuesHolder pvhR = PropertyValuesHolder.ofFloat(View.ROTATION,90);
                ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(menuIcon,pvhR);
                animator.start();
            }

            @Override
            public void onMenuClosed(FloatingActionMenu floatingActionMenu) {
                menuIcon.setRotation(90);
                PropertyValuesHolder pvhR = PropertyValuesHolder.ofFloat(View.ROTATION,0);
                ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(menuIcon,pvhR);
                animator.start();
            }
        });

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddMomentActivity.class);
                intent.putExtra("latitude", String.format("%.2f", location.getLatitude()));
                intent.putExtra("longitude", String.format("%.2f", location.getLongitude()));
                startActivityForResult(intent, ADD_MOMENT_REQUEST_CODE);
                fam.close(true);
            }
        });

        picBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getContentResolver().delete(AppContentProvider.URI_MOMENTS, null, null);
                registerReceiver(broadcastReceiverGet, new IntentFilter("Finished get"));
                Intent serviceIntent = new Intent(getApplicationContext(), GetService.class);
                MainActivity.this.startService(serviceIntent);
                fam.close(true);
            }
        });

        exitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences spreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = spreferences.edit();
                editor.remove("access_token");
                editor.commit();
                getContentResolver().delete(AppContentProvider.URI_MOMENTS, null, null);
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
                fam.close(true);
            }
        });

    }
}
