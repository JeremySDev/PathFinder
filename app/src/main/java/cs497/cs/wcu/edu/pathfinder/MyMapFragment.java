package cs497.cs.wcu.edu.pathfinder;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


/**
 *
 *
 *
 */
public class MyMapFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener
{
    private SupportMapFragment fragment;
    private GoogleMap mMap;

    private LocationProvider mLocationProvider;

    private GoogleApiClient mGoogleApiClient;
    public static final String TAG = MyMapFragment.class.getSimpleName();

    private LocationRequest mLocationRequest;

    private Marker startPostion;
    private Marker endPostion;
    private MarkerOptions startPostionMarkerOptions;
    private MarkerOptions endPostionMarkerOptions;

    /**
     * Does the initial creation of the fragment*
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.setHasOptionsMenu(true);
        mGoogleApiClient = new GoogleApiClient.Builder(this.getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        // Create the LocationRequest object
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1 * 1000); // 1 second, in milliseconds
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.activity_maps, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        FragmentManager fm = getChildFragmentManager();
        fragment = (SupportMapFragment) fm.findFragmentById(R.id.map);
        if (fragment == null)
        {
            fragment = SupportMapFragment.newInstance();
            fm.beginTransaction().replace(R.id.map, fragment).commit();
        }

        startPostionMarkerOptions = new MarkerOptions();
        endPostionMarkerOptions = new MarkerOptions();

        startPostionMarkerOptions.icon(
                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        endPostionMarkerOptions.icon(
                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
    }

    @Override
    public void onResume()
    {
        super.onResume();
        setUpMap();
        mGoogleApiClient.connect();
    }


    private void setUpMap()
    {
        if (mMap == null)
        {
            mMap = fragment.getMap();
        }
        //Latitude and longitude for stillwell
        double lat = 35.312297;
        double lng = -83.180138;

        //Latlng object for stillwell
        LatLng location = new LatLng(lat, lng);

        //Create cameraupdate object to zoom in and center on Stillwell
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(17);
        CameraUpdate center = CameraUpdateFactory.newLatLng(location);

        //Add a marker at the Latitude and longitude and title it Stillwell Building
        mMap.addMarker(
                new MarkerOptions().position(new LatLng(lat, lng)).title("Stillwell Building"));

        //Run the camera updates on the map object.
        mMap.moveCamera(center);
        mMap.animateCamera(zoom);
    }


    @Override
    public void onPause()
    {
        super.onPause();
        if (mGoogleApiClient.isConnected())
        {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }

    /**
     * onOptionsItemSelected - determines what happens when a user clicks on a menu item
     *
     * @param item the item the user selected.
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {

        switch (item.getItemId())
        {
            //if the user picked save open the name file dialog
            case R.id.action_record:
                Toast.makeText(this.getActivity().getApplicationContext(), "Record",
                        Toast.LENGTH_SHORT).show();
                return true;
            //if the user pressed undo call the customViews undo method
            case R.id.action_stop:
                Toast.makeText(this.getActivity().getApplicationContext(), "Stop",
                        Toast.LENGTH_SHORT).show();
                return true;
            //if the user pressed clear call the customViews clearScreen method
            case R.id.action_save:
                Toast.makeText(this.getActivity().getApplicationContext(), "Save",
                        Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onConnected(Bundle bundle)
    {
        Log.i(TAG, "Location services connected.");
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (location == null)
        {
            LocationServices.FusedLocationApi
                    .requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
        else
        {
            handleNewLocation(location);
        }
    }


    @Override
    public void onConnectionSuspended(int i)
    {
        Log.i(TAG, "Location services suspended. Please reconnect.");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult)
    {
        Log.i(TAG, "Location services connect failed");
    }

    @Override
    public void onLocationChanged(Location location)
    {
        handleNewLocation(location);
    }

    private void handleNewLocation(Location location)
    {
        Log.d(TAG, location.toString());

        double currentLatitude = location.getLatitude();
        double currentLongitude = location.getLongitude();

        LatLng latLng = new LatLng(currentLatitude, currentLongitude);

        MarkerOptions options = new MarkerOptions()
                .position(latLng)
                .title("I am here!");

        mMap.addMarker(options);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
    }
}
