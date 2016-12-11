package com.example.wojtek.studentcompanion.Fragments;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wojtek.studentcompanion.Activities.MainActivity;
import com.example.wojtek.studentcompanion.DB.MarkerDBContract;
import com.example.wojtek.studentcompanion.DB.MarkerDBContract;
import com.example.wojtek.studentcompanion.DB.DatabaseHandler;
import com.example.wojtek.studentcompanion.DB.MarkerDBContract;
import com.example.wojtek.studentcompanion.DB.MarkerDBHandler;
import com.example.wojtek.studentcompanion.Manifest;
import com.example.wojtek.studentcompanion.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.identity.intents.Address;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.List;
import java.util.Map;

import static android.R.attr.id;

/**
 * Created by Wojtek on 29/11/2016.
 */
public class LocatorFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener, LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    private static final String TAG = "LOCATOR FRAGMENT";

    //Used for permission from the user
    public static int REQUEST_LOCATION = 1;

    private GoogleMap map;

    static public Location mLastLocation;
    private Location mLocation;
    private LatLng mLatLng;
    protected GoogleApiClient mGoogleApiClient;
    protected LocationRequest mLocationRequest;

    private MarkerDBHandler dbHelper;

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    public LocatorFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static LocatorFragment newInstance(int sectionNumber) {
        LocatorFragment fragment = new LocatorFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    //Method used for displaying the menu allowing the user to add a new marker to the map
    public Dialog addMarkerMenu(final LatLng point){
        final EditText taskEditText = new EditText(getActivity());
        taskEditText.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Add new marker")
                .setView(taskEditText)
                .setPositiveButton(R.string.todoAcceptBtn, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //The user input is used to set the title of the Marker
                        String userInput = taskEditText.getText().toString();

                        //Setting up the database to be written to
                        MarkerDBHandler dbHelper = new MarkerDBHandler(getActivity());
                        SQLiteDatabase db = dbHelper.getWritableDatabase();

                        //Put the user input into ContentValues format
                        ContentValues values = new ContentValues();
                        values.clear();
                        values.put(MarkerDBContract.TaskEntry.COL_MARKER_TITLE, userInput);
                        //The latitude and longitude is taken from the point LatLng value passed
                        //by the OnMapLongClick method
                        values.put(MarkerDBContract.TaskEntry.COL_MARKER_LATITUDE, point.latitude);
                        values.put(MarkerDBContract.TaskEntry.COL_MARKER_LONGITUDE, point.longitude);

                        //Inserts the user input into the database
                        db.insertWithOnConflict(
                                MarkerDBContract.TaskEntry.TABLE,
                                null,
                                values,
                                SQLiteDatabase.CONFLICT_IGNORE);

                        //The display function is ran again so the new marker can be seen on the map
                        displayMarkers();
                        //Toast message confirming the new marker
                        Toast.makeText(getActivity(),"Marker " + taskEditText.getText() + " added!", Toast.LENGTH_SHORT).show();

                    }
                })
                .setNegativeButton(R.string.todoCancelBtn, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Does nothing, just closes the alert dialog
                    }
                });
        return builder.create();
    }

    //Used to display all the markers from the database on the map
    public void displayMarkers(){
        //Firstly we clear the map from existing markers to avoid duplicates
        map.clear();

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(MarkerDBContract.TaskEntry.TABLE,
                new String[]{MarkerDBContract.TaskEntry._ID, MarkerDBContract.TaskEntry.COL_MARKER_TITLE,
                        MarkerDBContract.TaskEntry.COL_MARKER_LATITUDE,
                        MarkerDBContract.TaskEntry.COL_MARKER_LONGITUDE},
                null, null, null, null, null);

        //For every row in the database I extract the values from each column and use them to create
        //the markers
        while(cursor.moveToNext()) {
            int idx = cursor.getColumnIndex(MarkerDBContract.TaskEntry.COL_MARKER_TITLE);
            int idx2 = cursor.getColumnIndex(MarkerDBContract.TaskEntry.COL_MARKER_LATITUDE);
            int idx3 = cursor.getColumnIndex(MarkerDBContract.TaskEntry.COL_MARKER_LONGITUDE);

            String title = cursor.getString(idx);
            double lat = cursor.getDouble(idx2);
            double lng = cursor.getDouble(idx3);

            //Here I make a single LatLng value from the two values I extracted from the database
            LatLng markerPos = new LatLng(lat, lng);

            //Creation of the markers using the extracted values
            map.addMarker(new MarkerOptions()
                    .position(markerPos)
                    .title(title))
                    .setDraggable(true);
        }
    }

    //Method used for removing markers from the map and the database
    public Dialog removeMarkerMenu(final Marker marker){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Are you sure you want to remove this marker?")
                .setPositiveButton(R.string.markerRemove, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Set the SQL query to delete the task with a specific TITLE.
                        String sql = String.format("DELETE FROM %s WHERE %s = '%s'",
                                MarkerDBContract.TaskEntry.TABLE,
                                MarkerDBContract.TaskEntry.COL_MARKER_TITLE,
                                marker.getTitle());
                        SQLiteDatabase sqlDB = dbHelper.getWritableDatabase();
                        //The SQL query is executed here and the task is deleted from the DB.
                        sqlDB.execSQL(sql);

                        //displayMarkers function is ran again to remove the deleted marker from the map
                        displayMarkers();

                        //Toast message confirming deletion of the marker
                        Toast.makeText(getActivity(),"Marker " + marker.getTitle() + " removed!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(R.string.todoCancelBtn, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Does nothing, just closes the alert dialog
                    }
                });
        return builder.create();
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_locator, container, false);

        dbHelper = new MarkerDBHandler(getActivity());

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        mGoogleApiClient.connect();

        //FAB which moves the map to their current location at a zoomed in level.
        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCurrentLocation();
            }
        });

        //Here I display all the values from the database to the console for testing purposes.
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(MarkerDBContract.TaskEntry.TABLE,
                new String[]{MarkerDBContract.TaskEntry._ID, MarkerDBContract.TaskEntry.COL_MARKER_TITLE,
                MarkerDBContract.TaskEntry.COL_MARKER_LATITUDE,
                MarkerDBContract.TaskEntry.COL_MARKER_LONGITUDE},
                null, null, null, null, null);

        while(cursor.moveToNext()) {
            int idx = cursor.getColumnIndex(MarkerDBContract.TaskEntry.COL_MARKER_TITLE);
            int idx2 = cursor.getColumnIndex(MarkerDBContract.TaskEntry.COL_MARKER_LATITUDE);
            int idx3 = cursor.getColumnIndex(MarkerDBContract.TaskEntry.COL_MARKER_LONGITUDE);
            Log.d(TAG, "Marker: " + cursor.getString(idx) + " " + cursor.getDouble(idx2) + " " +
                    cursor.getDouble(idx3));
        }

        return rootView;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    //Get user permissions and set the LastLocation
    @Override
    public void onConnected(@Nullable Bundle connectionHint) {
        if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION);
        } else {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_LOCATION) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                onConnected(null);
            }
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        SupportMapFragment fragment = (SupportMapFragment)getChildFragmentManager().findFragmentById(R.id.map);
        fragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap){
        map = googleMap;
        map.setOnMapLongClickListener(this);

        /*Since Markers don't have long click listeners I use a drag listener to achieve the same
        effect*/
        map.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {
                removeMarkerMenu(marker).show();
            }

            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {

            }
        });
        //Used to display all the markers from the database to the map
        displayMarkers();
    }

    //Method used by the button to move the map to users current location at a zoomed in level
    public void getCurrentLocation(){
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mLocation = mLastLocation;
        mLatLng = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(mLatLng, 17));
    }

    //Method used to add new markers on the map
    public void onMapLongClick (LatLng point) {
        addMarkerMenu(point).show();
    }
}
