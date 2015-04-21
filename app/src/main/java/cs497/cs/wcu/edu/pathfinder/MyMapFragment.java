package cs497.cs.wcu.edu.pathfinder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.LinkedList;


/**
 *
 *
 *
 */
public class MyMapFragment extends Fragment /*implements LocationProvider.LocationCallback*/
{
    public static final String TAG = MyMapFragment.class.getSimpleName();
    private static int track = 0;
    private static boolean firstMarker = true;
    private static boolean lastMarker = false;
    View rootView = null;

    private SupportMapFragment fragment;
    private GoogleMap googleMap;
    private LinkedList<LatLng> points = new LinkedList<>();
    private LinkedList<Location> locations = new LinkedList<>();
    private Marker startPostion;
    private Marker endPostion;
    private MarkerOptions startPostionMarkerOptions;
    private MarkerOptions endPostionMarkerOptions;
    private Polyline route;
    private boolean trackRoute = true;
    private float routeDistance = 0.0f;
    private Location lastLocation;

    /**
     * The map has run once *
     */
    boolean runOnce = true;


    /**
     * Does the initial creation of the fragment*
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.setHasOptionsMenu(true);

        //mLocationProvider = new LocationProvider(this.getActivity(), this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        //Check if the rootview is null if so initilize it
        if (rootView == null)
        {
            rootView = inflater.inflate(R.layout.activity_maps, container, false);
        }
        return rootView;
    }

    /**
     * initilizeMap - If our Google Map is null we initilize it.

    private void initilizeMap()
    {

        if (googleMap == null)
        {
            Toast.makeText(this.getActivity().getApplicationContext(),
                    "Sorry! unable to create maps", Toast.LENGTH_SHORT)
                    .show();
        }
    }*/


    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        Log.v(TAG, "onActivityCreated called");

        //Find our map fragment
        FragmentManager fm = getChildFragmentManager();
        fragment = (SupportMapFragment) fm.findFragmentById(R.id.map);

        //If map fragment is still null replace it
        if (fragment == null)
        {
            fragment = SupportMapFragment.newInstance();
            fm.beginTransaction().replace(R.id.map, fragment).commit();
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        this.onStartMap();
        /////////////////////////////////////
        //REGISTERING THE BROADCAST RECEIVER
        ////////////////////////////////////////
        IntentFilter filter = new IntentFilter();
        filter.addAction(AppConstraints.LOCATION_BROADCAST);
        filter.addAction(AppConstraints.BROADCAST_TWO);
        filter.addAction(AppConstraints.BROADCAST_THREE);
        this.getActivity().registerReceiver(receiver, filter);
    }

    /**
     * onStartMap - Checks if the map is null if so it gets a map from fragment. Then it attaches a
     * OnMapLoaded Callback which tells the map find the last fixed location. Adds a
     * OnMyLocationChange Listener that tells the map to call handleNewLocation.
     */
    public void onStartMap()
    {
        Context ctx = this.getActivity();
        LocationManager lm = (LocationManager) ctx.getSystemService(ctx.LOCATION_SERVICE);

        // 5 miliseconds
        int minTime = 5000;

        // 10 meters
        int minDist = 10;

        //Check if the map is null if so it gets a map from fragment
        if (googleMap == null)
        {
            googleMap = fragment.getMap();
        }

        //googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);

        // When map has loaded animate the camera to the user position.
        googleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback()
        {
            @Override
            public void onMapLoaded()
            {
                MyMapFragment.this.goToLastFix();
            }
        });

        //Tells the map that when the position changes call handleNewLocation
        googleMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener()
        {
            @Override
            public void onMyLocationChange(Location location)
            {
                Toast.makeText(MyMapFragment.this.getActivity().getApplicationContext(),
                        " My New Location " + location.toString(), Toast.LENGTH_LONG);
            }
        });

        //Enable listeners for GPS and Network provigers
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDist, oll);
        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTime, minDist, oll);
    }

    @Override
    public void onPause()
    {
        Log.v(TAG, "On Pause Called");
        super.onPause();
        //SoundPlayer.makeSound(SoundPlayer.SOUND_BLIP6);
    }

    /**
     * Go to new location.
     *
     * @param lat The latitude to display.
     * @param lng The longitude to display
     */
    public void goToLocation(double lat, double lng)
    {

        //SoundPlayer.makeSound(SoundPlayer.SOUND_PROC_SOUND);
        this.goToLocation(lat, lng, 17);
    }

    /**
     * Go to new location.
     *
     * @param lat  The latitude to display.
     * @param lng  The longitude to display
     * @param zoom The zoom level of the application.
     */
    public void goToLocation(double lat, double lng, int zoom)
    {
        //Create a new latlng object from the lat and lng double params
        LatLng location = new LatLng(lat, lng);

        //Setup how the user will view the map
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(location) // Sets the center of the map to Mountain View
                .zoom(zoom)       // Sets the zoom
                .bearing(90)      // Sets the orientation of the camera to east
                .tilt(0)          // tilt 0 degrees
                .build();         // Creates a CameraPosition from the builder

        CameraUpdate center = CameraUpdateFactory.newCameraPosition(cameraPosition);

        //Position and zoom camera;
        googleMap.animateCamera(center);
        googleMap.moveCamera(center);
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
                trackRoute = true;
                return true;
            //if the user pressed undo call the customViews undo method
            case R.id.action_stop:
                Toast.makeText(this.getActivity().getApplicationContext(), "Stop",
                        Toast.LENGTH_SHORT).show();
                lastMarker = true;
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


    /**
     * goToLastFix - finds the users last fixed location.
     */
    public void goToLastFix()
    {
        Context ctx = this.getActivity();
        LocationManager lm = (LocationManager) ctx.getSystemService(ctx.LOCATION_SERVICE);
        Location location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        long fixTime;
        long time = System.currentTimeMillis();
        long age = 60001;

        //Try and get the time of the location object so that we can see how old it is
        try
        {
            fixTime = location.getTime();
            age = time - fixTime;
        }
        catch (NullPointerException npe)
        {
            npe.printStackTrace();
        }

        //if less than one hour use built in location.
        if (age > 1000 * 60)
        {
            location = new Location("Cullowhee");
            location.setLatitude(AppConstraints.CULLOWHEE.latitude);
            location.setLongitude(AppConstraints.CULLOWHEE.longitude);
        }

        double lat = location.getLatitude();
        double lng = location.getLongitude();

        this.goToLocation(lat, lng);
    }


    public void handleNewLocation(Location location)
    {
        Log.d(TAG, location.toString());
        SoundPlayer.vibrate(750, this.getActivity());
        //Toast for when we get a new location
        Toast.makeText(this.getActivity().getApplicationContext(), "New Location",
                Toast.LENGTH_SHORT).show();

        //Get the current Latitude and Longitude of the location
        double currentLatitude = location.getLatitude();
        double currentLongitude = location.getLongitude();

        //the distance between the last location and current one
        float disRecentPoints = 0.0f;

        //Create a LatLng object from our new location
        LatLng latLng = new LatLng(currentLatitude, currentLongitude);

        if (locations.size() >= 1)
        {
            disRecentPoints = location.distanceTo(locations.getLast());
            Toast.makeText(this.getActivity().getApplicationContext(),
                    "Distance: " + disRecentPoints,
                    Toast.LENGTH_SHORT).show();
        }

        //Check if we are supposed to track the route
        if (trackRoute)
        {
            //Marker for new location
            MarkerOptions options;


            //Add the latlng object to a the linkedlist of points
            points.add(latLng);

            //Check if its the start marker
            if (firstMarker)
            {
                startPostionMarkerOptions = new MarkerOptions()
                        .position(latLng)
                        .title("Start  " + track);
                startPostionMarkerOptions.icon(
                        BitmapDescriptorFactory
                                .defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                firstMarker = false;
            }
            else if (lastMarker)
            {
                endPostionMarkerOptions = new MarkerOptions()
                        .position(latLng)
                        .title("Finish " + track);
                endPostionMarkerOptions.icon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_RED));
                trackRoute = false;
            }
            else if (disRecentPoints >= 20.0f || disRecentPoints == 0.0f)
            {
                options = new MarkerOptions()
                        .position(latLng)
                        .title("I am here! " + track);
                options.icon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                googleMap.addMarker(options);
                track++;
            }

            startPostion = googleMap.addMarker(startPostionMarkerOptions);

            if (endPostionMarkerOptions != null)
            {
                endPostion = googleMap.addMarker(endPostionMarkerOptions);
            }

            locations.add(location);

            this.drawPolyline();
        }
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(17));
    }


    /**
     * drawPolyline - draws the polyline of the users route
     */
    public void drawPolyline()
    {
        PolylineOptions po = new PolylineOptions();
        po.addAll(points);
        po.color(Color.BLUE);
        po.width(20.0f);
        route = googleMap.addPolyline(po);
    }

    private float routeDistance()
    {
        float distance = 0.0f;

        for (int i = 0; i <= locations.size() - 1; i++)
        {
            distance += (locations.get(i)).distanceTo((locations.get(i + 1)));
        }

        return distance;
    }

    LocationListener oll = new LocationListener()
    {
        @Override
        public void onLocationChanged(Location newLocation)
        {
            Toast.makeText(MyMapFragment.this.getActivity().getApplicationContext(),
                    " New Location " + newLocation.toString(), Toast.LENGTH_LONG);
//            double lat = newLocation.getLatitude();
//            double lng = newLocation.getLongitude();
//            MyMapFragment.this.goToLocation(lat, lng);
            handleNewLocation(newLocation);
            //SoundPlayer.makeSound(SoundPlayer.SOUND_BLIP1);

        }//end onLocationChanged

        public void onStatusChanged(String provider, int status, Bundle extras)
        {
        }//end onStatisChanged

        public void onProviderEnabled(String provider)
        {
            Toast.makeText(MyMapFragment.this.getActivity().getApplicationContext(),
                    provider + " is on", Toast.LENGTH_LONG);
            //SoundPlayer.makeSound(SoundPlayer.SOUND_BLIP8);
        }//end onProviderEnabled.

        public void onProviderDisabled(String provider)
        {
            Toast.makeText(MyMapFragment.this.getActivity().getApplicationContext(),
                    provider + " is off", Toast.LENGTH_LONG);
            //SoundPlayer.makeSound(SoundPlayer.SOUND_BLIP7);
        } //end onProviderDisabled.

    };

    private BroadcastReceiver receiver = new BroadcastReceiver()
    {
        //--------------------------------------------------------------------------------

        /** Receives broadcast messages from the system. */
        //--------------------------------------------------------------------------------
        @Override
        public void onReceive(android.content.Context context,
                              android.content.Intent intent)
        {
            //I the received broadcast is this action do somthing.
            if (intent.getAction().equals(AppConstraints.LOCATION_BROADCAST))
            {
                if (intent.getExtras() != null)
                {
                    Bundle b = intent.getExtras();
                    double lng = b.getDouble("LNG");
                    double lat = b.getDouble("LAT");
                    MyMapFragment.this.goToLocation(lat, lng);

                }//end if

            }//end for
            else if (intent.getAction().equals(AppConstraints.BROADCAST_TWO))
            {
                if (intent.getExtras() != null)
                {
                    Bundle b = intent.getExtras();
                    double lng = b.getDouble(AppConstraints.KEY_LONGITUDE);
                    double lat = b.getDouble(AppConstraints.KEY_LATITUDE);
                    int zoom = b.getInt(AppConstraints.KEY_ZOOM_LEVEL);
                    MyMapFragment.this.goToLocation(lat, lng, zoom);

                }
            }
            else if (intent.getAction().equals(AppConstraints.BROADCAST_THREE))
            {
                if (intent.getExtras() != null)
                {
                    Bundle b = intent.getExtras();
                    double lng = b.getDouble(AppConstraints.KEY_LONGITUDE);
                    double lat = b.getDouble(AppConstraints.KEY_LATITUDE);
                    int zoom = b.getInt(AppConstraints.KEY_ZOOM_LEVEL);
                    MyMapFragment.this.goToLocation(lat, lng, zoom);

                }
            }
        }// end onReceive-----------------------------------------------------------------

    };


}

//        geo fix -83.182322 35.303268
//        geo fix -83.182631 35.310450
//        geo fix -83.182171 35.311357
//        geo fix -83.183470 35.310517
//        geo fix -83.181227 35.311235
//        geo fix -83.179745 35.313028
//        geo fix -83.181263 35.311295
//        geo fix -83.182494 35.305279
//        geo fix -83.183028 35.310717
//        geo fix -83.182880 35.307502
//        geo fix -83.183000 35.311440
//        geo fix -83.186424 35.309005
//        geo fix -83.186963 35.306355
//        geo fix -83.186638 35.310464
//        geo fix -83.184431 35.307116
//        geo fix -83.183299 35.309418
//        geo fix -83.182575 35.310000
//        geo fix -83.178967 35.311339
//        geo fix -83.181435 35.301412
//        geo fix -83.186925 35.309188
