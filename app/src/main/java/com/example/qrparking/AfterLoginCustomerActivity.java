package com.example.qrparking;



import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.File;
import java.io.FileNotFoundException;

public class AfterLoginCustomerActivity extends AppCompatActivity {
    String parkingspot;
    String username;
    String key;
    String password;
    String name;
    String id;
    TextView tv;
    DatabaseReference dataUsers;
    private static final String LOG_TAG = "Barcode Scanner API";
    private static final int PHOTO_REQUEST = 10;
    private TextView scanResults;
    private BarcodeDetector detector;
    private Uri imageUri;
    private static final int REQUEST_WRITE_PERMISSION = 20;
    private static final String SAVED_INSTANCE_URI = "uri";
    private static final String SAVED_INSTANCE_RESULT = "result";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after_login_customer);
        tv=(TextView)findViewById(R.id.textView);
        Bundle bundle = getIntent().getExtras();
        username = bundle.getString("username");
        key=bundle.getString("key");
        password=bundle.getString("password");
        name=bundle.getString("name");

        FirebaseApp.initializeApp(this);
        dataUsers = FirebaseDatabase.getInstance().getReference("users");
        final Query userQuery = dataUsers.orderByChild("username").equalTo(username);

        userQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot datas: dataSnapshot.getChildren()){
                    //String dpass=datas.child("password").getValue().toString();
                    name=datas.child("name").getValue().toString();


                }
                tv.setText("Welcome "+name);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        Button button = (Button) findViewById(R.id.button);
        scanResults = (TextView) findViewById(R.id.scan_results);
        if (savedInstanceState != null) {
            imageUri = Uri.parse(savedInstanceState.getString(SAVED_INSTANCE_URI));
            scanResults.setText(savedInstanceState.getString(SAVED_INSTANCE_RESULT));
            parkingspot = savedInstanceState.getString(SAVED_INSTANCE_RESULT);
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users").child(key);
            User user = new User(name, username, password, parkingspot);
            ref.setValue(user);

        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(AfterLoginCustomerActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_PERMISSION);
            }
        });
        detector = new BarcodeDetector.Builder(getApplicationContext())
                .setBarcodeFormats(Barcode.DATA_MATRIX | Barcode.QR_CODE)
                .build();
        if (!detector.isOperational()) {
            scanResults.setText("Could not set up the detector!");
            return;
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_WRITE_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    takePicture();
                } else {
                    Toast.makeText(AfterLoginCustomerActivity.this, "Permission Denied!", Toast.LENGTH_SHORT).show();
                }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PHOTO_REQUEST && resultCode == RESULT_OK) {
            launchMediaScanIntent();
            try {
                Bitmap bitmap = decodeBitmapUri(this, imageUri);
                if (detector.isOperational() && bitmap != null) {
                    Frame frame = new Frame.Builder().setBitmap(bitmap).build();
                    SparseArray<Barcode> barcodes = detector.detect(frame);
                    for (int index = 0; index < barcodes.size(); index++) {
                        Barcode code = barcodes.valueAt(index);
                        scanResults.setText(scanResults.getText() + code.displayValue + "\n");

                        //Required only if you need to extract the type of barcode
                        /*int type = barcodes.valueAt(index).valueFormat;
                        switch (type) {
                            case Barcode.CONTACT_INFO:
                                Log.i(LOG_TAG, code.contactInfo.title);
                                break;
                            case Barcode.EMAIL:
                                Log.i(LOG_TAG, code.email.address);
                                break;
                            case Barcode.ISBN:
                                Log.i(LOG_TAG, code.rawValue);
                                break;
                            case Barcode.PHONE:
                                Log.i(LOG_TAG, code.phone.number);
                                break;
                            case Barcode.PRODUCT:
                                Log.i(LOG_TAG, code.rawValue);
                                break;
                            case Barcode.SMS:
                                Log.i(LOG_TAG, code.sms.message);
                                break;
                            case Barcode.TEXT:
                                Log.i(LOG_TAG, code.rawValue);
                                break;
                            case Barcode.URL:
                                Log.i(LOG_TAG, "url: " + code.url.url);
                                break;
                            case Barcode.WIFI:
                                Log.i(LOG_TAG, code.wifi.ssid);
                                break;
                            case Barcode.GEO:
                                Log.i(LOG_TAG, code.geoPoint.lat + ":" + code.geoPoint.lng);
                                break;
                            case Barcode.CALENDAR_EVENT:
                                Log.i(LOG_TAG, code.calendarEvent.description);
                                break;
                            case Barcode.DRIVER_LICENSE:
                                Log.i(LOG_TAG, code.driverLicense.licenseNumber);
                                break;
                            default:
                                Log.i(LOG_TAG, code.rawValue);
                                break;
                        }*/
                    }
                    parkingspot=scanResults.getText().toString();
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users").child(key);
                    User user = new User(name, username, password, parkingspot);
                    ref.setValue(user);
                    if (barcodes.size() == 0) {
                        scanResults.setText("Scan Failed: Found nothing to scan");
                    }
                } else {
                    scanResults.setText("Could not set up the detector!");
                }
            } catch (Exception e) {
                Toast.makeText(this, "Failed to load Image", Toast.LENGTH_SHORT)
                        .show();
                Log.e(LOG_TAG, e.toString());
            }
        }
    }

    private void takePicture() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photo = new File(Environment.getExternalStorageDirectory(), "picture.jpg");
        imageUri = FileProvider.getUriForFile(AfterLoginCustomerActivity.this,
                BuildConfig.APPLICATION_ID + ".provider", photo);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, PHOTO_REQUEST);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (imageUri != null) {
            outState.putString(SAVED_INSTANCE_URI, imageUri.toString());
            outState.putString(SAVED_INSTANCE_RESULT, scanResults.getText().toString());
        }
        super.onSaveInstanceState(outState);
    }

    private void launchMediaScanIntent() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(imageUri);
        this.sendBroadcast(mediaScanIntent);
    }

    private Bitmap decodeBitmapUri(Context ctx, Uri uri) throws FileNotFoundException {
        int targetW = 600;
        int targetH = 600;
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(ctx.getContentResolver().openInputStream(uri), null, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;

        return BitmapFactory.decodeStream(ctx.getContentResolver()
                .openInputStream(uri), null, bmOptions);
    }

}
