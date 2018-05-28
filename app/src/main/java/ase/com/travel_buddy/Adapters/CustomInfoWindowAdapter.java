package ase.com.travel_buddy.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import ase.com.travel_buddy.R;

public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
    private final View ContentView;

    public CustomInfoWindowAdapter(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        ContentView =  inflater.inflate(R.layout.custom_map_info, null);
    }
    @Override
    public View getInfoContents(Marker marker) {

        TextView google_info_description = ContentView.findViewById(R.id.google_info_description);
        google_info_description.setText(marker.getTitle());
        ImageView google_info_image = ContentView.findViewById(R.id.google_info_image);
        byte[] decodedString = Base64.decode(marker.getSnippet(), Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        google_info_image.setImageBitmap(decodedByte);

        return ContentView;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }
}
