package ase.com.travel_buddy.Main;

import android.content.ComponentName;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.features.ReturnMode;
import com.esafirm.imagepicker.model.Image;

import java.io.ByteArrayOutputStream;

import ase.com.travel_buddy.R;
import ase.com.travel_buddy.Services.PostService;

public class AddMomentActivity extends AppCompatActivity {

    private ImageView selectImage;
    private TextView latitude;
    private TextView longitude;
    private EditText description;
    private Spinner dialogSpinner;
    private String[] listOfObjects;
    private TypedArray images;
    private ImageView spinnerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_moment);

        listOfObjects = getResources().getStringArray(R.array.object_array);
        images = getResources().obtainTypedArray(R.array.object_image);
        spinnerView = findViewById(R.id.spinner_view);

        dialogSpinner = findViewById(R.id.icon_spinner);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(AddMomentActivity.this, android.R.layout.simple_spinner_item, listOfObjects);
        dialogSpinner.setAdapter(spinnerAdapter);

        dialogSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinnerView.setImageResource(images.getResourceId(dialogSpinner.getSelectedItemPosition(), -1));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Button selectImageButton = findViewById(R.id.select_image_button);

        selectImage = findViewById(R.id.select_image_image_view);
        latitude = findViewById(R.id.latitude);
        longitude = findViewById(R.id.longitude);
        description = findViewById(R.id.description);

        selectImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.create(AddMomentActivity.this)
                        .returnMode(ReturnMode.ALL)
                        .single()
                        .start();
            }
        });

        latitude.setText(getIntent().getStringExtra("latitude"));
        longitude.setText(getIntent().getStringExtra("longitude"));

        Button button = findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BitmapDrawable drawable = (BitmapDrawable) selectImage.getDrawable();
                Bitmap bitmap = drawable.getBitmap();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
                byte[] imageBytes = baos.toByteArray();
                String imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);
                Intent serviceIntent = new Intent(getApplicationContext(), PostService.class);
                serviceIntent.putExtra("longitude", longitude.getText().toString());
                serviceIntent.putExtra("latitude", latitude.getText().toString());
                serviceIntent.putExtra("description", description.getText().toString());
                serviceIntent.putExtra("image", imageString);
                serviceIntent.putExtra("icon", dialogSpinner.getSelectedItem().toString());
                AddMomentActivity.this.startService(serviceIntent);
                finish();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Image image = ImagePicker.getFirstImageOrNull(data);
        selectImage.setImageBitmap(BitmapFactory.decodeFile(image.getPath()));
        super.onActivityResult(requestCode, resultCode, data);
    }
}
