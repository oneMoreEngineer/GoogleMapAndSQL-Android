package edu.sjsu.android.googlemapandsql;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import edu.sjsu.android.googlemapandsql.databinding.ActivityMapsBinding;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        LoaderManager.LoaderCallbacks<Cursor> {

    private final LatLng LOCATION_UNIV = new LatLng(37.335371, -121.881050);
    private final LatLng LOCATION_CS = new LatLng(37.333714, -121.881860);
    private GoogleMap map;
    private ActivityMapsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

//        // Add a marker in Sydney and move the camera
//        //LatLng sydney = new LatLng(-34, 151);
//        map.addMarker(new MarkerOptions().position(LOCATION_CS).title("Find Me Here"));
        map.moveCamera(CameraUpdateFactory.newLatLng(LOCATION_UNIV));

        LoaderManager.getInstance(this).initLoader(0, null, this); // Invoke LoaderCallbacks to retrieve and draw already saved locations in map

        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {

                map.addMarker(new MarkerOptions().position(point));
                LocationInsertTask insertion = new LocationInsertTask();
                ContentValues values = new ContentValues();
                values.put(LocationsDB.LAT, point.latitude);
                values.put(LocationsDB.LNG, point.longitude);
                values.put(LocationsDB.ZOOM, map.getCameraPosition().zoom);
                insertion.execute(values);
                Toast.makeText(getBaseContext(), "Marker is added to the Map",
                        Toast.LENGTH_SHORT).show();
            }
        });

        map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng point) {
                map.clear();
                LocationDeleteTask deletion = new LocationDeleteTask();
                deletion.execute();
                Toast.makeText(getBaseContext(), "All markers are removed",
                        Toast.LENGTH_SHORT).show();
            }
        });

    }



    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Loader<Cursor> c = null;
        Uri uri = LocationsContentProvider.CONTENT_URI;
        c = new CursorLoader(this, uri, null, null,
                null, null);
        return c;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) {
        int locationCount = 0;
        double lat = 0;
        double lng = 0;
        float zoom = 0;
        if (arg1 != null) {
            locationCount = arg1.getCount();
            arg1.moveToFirst();
        } else {
            locationCount = 0;
        }
        for (int i = 0; i < locationCount; i++) {
            lat = arg1.getDouble(arg1.getColumnIndex(LocationsDB.LAT));
            lng = arg1.getDouble(arg1.getColumnIndex(LocationsDB.LNG));
            zoom = arg1.getFloat(arg1.getColumnIndex(LocationsDB.ZOOM));
            LatLng location = new LatLng(lat, lng);
            map.addMarker(new MarkerOptions().position(location));
            arg1.moveToNext();
        }
        if (locationCount > 0) {
            map.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(lat, lng)));
            map.animateCamera(CameraUpdateFactory.zoomTo(zoom));
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }

    public void onClick_CS(View v) {
        map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(LOCATION_CS, 18);
        map.animateCamera(update);
    }

    public void onClick_Univ(View v) {
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(LOCATION_UNIV, 14);
        map.animateCamera(update);

    }

    public void onClick_City(View v) {
        map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(LOCATION_UNIV, 10);
        map.animateCamera(update);

    }

    private class LocationInsertTask extends AsyncTask<ContentValues, Void, Void> {
        @Override
        protected Void doInBackground(ContentValues... contentValues) {
            getContentResolver().insert(LocationsContentProvider.CONTENT_URI, contentValues[0]);
            return null;
        }
    }

    private class LocationDeleteTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            getContentResolver().delete(LocationsContentProvider.CONTENT_URI, null, null);
            return null;
        }
    }

}