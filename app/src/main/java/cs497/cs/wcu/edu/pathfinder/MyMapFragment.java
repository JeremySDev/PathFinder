package cs497.cs.wcu.edu.pathfinder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
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
public class MyMapFragment extends Fragment
{
    View rootView = null;

    /* Represents the map in our view*/
    public SupportMapFragment fragment;

    /* The map made from the fragment*/
    public GoogleMap googleMap;

    /* points in the users route */
    private LinkedList<LatLng> points = new LinkedList<>();

    /* locations in the users route */
    private LinkedList<Location> locations = new LinkedList<>();

    /* Marker representing the user starting location */
    private Marker startPosition;

    /* Marker representing the user ending location */
    private Marker endPosition;

    /* MarkerOptions for the startPositionMarker */
    private MarkerOptions startPostionMarkerOptions;

    /* MarkerOptions for the endPositionMarker */
    private MarkerOptions endPostionMarkerOptions;

    /* Polyline that shows the users path */
    private Polyline route;

    /* Whether or not to track the users route */
    private boolean trackRoute = false;

    /* The distance of a users route */
    private float routeDistance = 0.0f;

    /* The map has run once */
    private boolean firstLocate = true;

    /* Whether or not stop has been pressed */
    private boolean stopPressed = true;

    /* Whether or not record has been pressed */
    private boolean recPressed = true;

    /* A context object for the map class to use */
    private Context context;

    /* Image view for the record image */
    private ImageView record;

    /* A marker for the users current position */
    private Marker myCurrentLocation;

    /* A location for the users current position */
    private Location currentLocation;

    /* A LatLng for the user current position */
    private LatLng currentLatLng;

    /* Whether or not record has been pressed */
    private boolean runOnce = false;


    /**
     * Does the initial creation of the fragment*
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //init context
        context = this.getActivity();

        //Set that we can use teh options menu
        this.setHasOptionsMenu(true);

        //Create an intent from our location service
        Intent i = new Intent(context, MyLocationService.class);

        //Start the location service
        context.startService(i);
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
        //Init teh image view with the one in our view
        record = (ImageView) rootView.findViewById(R.id.imageView);
        //is the first time locating
        firstLocate = true;
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

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
        //if map is null initialize it
        if (googleMap == null)
        {
            googleMap = fragment.getMap();
            //googleMap.getUiSettings().setZoomGesturesEnabled(false);
        }

        // register our broadcast receiver and give it an intent filter
        IntentFilter filter = new IntentFilter();
        filter.addAction(AppConstraints.LOCATION_BROADCAST);
        this.getActivity().registerReceiver(receiver, filter);

        //Check if we need to load a users saved route
        if (AppConstraints.loadMap)
        {
            loadPoints(AppConstraints.points);
            AppConstraints.loadMap = false;
        }

        if (!runOnce)
        {
            this.goToLastFix();
            runOnce = true;
        }
    }

    @Override
    public void onPause()
    {
        super.onPause();
        //unregister receiver
        //this.getActivity().unregisterReceiver(receiver);
    }


    @Override
    public void onDestroy()
    {
        super.onDestroy();
        //unregister receiver
        this.getActivity().unregisterReceiver(receiver);
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
            //handle pressing dialog twice
            case R.id.action_record:
                recordPress();
                return true;
            //if the user pressed undo call the customViews undo method
            case R.id.action_stop:
                stopPress();
                return true;
            //if the user pressed clear call the customViews clearScreen method
            case R.id.action_save:
                savePress();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * recordPress - called when the user presses record. Only runs if the user isn't already
     * recording. Tells the map to track users route, sets the start position of the route and makes
     * the imageview visible.
     */
    private void recordPress()
    {
        //Only start recording if not already recording
        if (stopPressed)
        {
            //Track the users route
            trackRoute = true;
            //Set the users start position
            this.setStartPostion();
            //Set stopped pressed to false
            stopPressed = false;
            //Set recording pressed to true
            recPressed = true;
            //Make the image view visible
            record.setVisibility(View.VISIBLE);
        }
        else
        {
            Toast.makeText(this.getActivity().getApplicationContext(),
                    "Press Stop Recording First", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * stopPress - called when the user presses stop. Only runs if the user is recording. Tells the
     * map to stop tracking the users route, sets the end position of the route and make
     * the imageview invisible.
     */
    private void stopPress()
    {
        //Check if we are recording
        if (!stopPressed)
        {
            //Set the end position by adding a marker
            this.setEndPostion();
            //Stop tracking the users location
            trackRoute = false;
            //show that they did press stop
            stopPressed = true;
            //set the image view to be invisible
            record.setVisibility(View.INVISIBLE);
        }
        else
        {
            Toast.makeText(this.getActivity().getApplicationContext(),
                    "Start Recording Route First",
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * savePress - called when the user presses save also calls the stopPressed method if the user
     * didn't press stop
     */
    private void savePress()
    {
        FileHandler fileHandler;
        if (!stopPressed)
        {
            stopPress();
        }
        if (recPressed)
        {
            fileHandler = new FileHandler(startPosition, endPosition, points, this.getActivity());
            fileHandler.openNameFileDialog();
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
    }

    /**
     * handleNewLocation - called when the a broadcast is received from our location service.
     * Handles the users new location by updating the current position marker and adding the point
     * to the list of points if we are tracking the user's route.
     *
     * @param location - the users new location.
     */
    public void handleNewLocation(Location location)
    {
        currentLocation = location;

        //Get the current Latitude and Longitude of the location
        double currentLatitude = currentLocation.getLatitude();
        double currentLongitude = currentLocation.getLongitude();

        //Create a LatLng object from our new location
        currentLatLng = new LatLng(currentLatitude, currentLongitude);

        //move the camera to the new position
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(currentLatLng));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(currentLatLng));

        //If the map is not zoomed in at 18 put it there
        if (googleMap.getCameraPosition().zoom != 18)
        {
            googleMap.moveCamera(CameraUpdateFactory.zoomTo(18));
            firstLocate = false;
        }

        //Change the position of the current location marker to the new location
        this.updateCurrentLocationMarker();

        //Distance between this point and the last one
        float disRecentPoints = 0.0f;

        //Check that we have enough location objects to compare
        if (locations.size() >= 1)
        {
            //get the distance between the most recent points
            disRecentPoints = currentLocation.distanceTo(locations.getLast());
        }

        //If were recording the user route add the location to that of the points
        if (trackRoute)
        {
            /*//Check of the distance change is too great or small if so don't include the location
            if (disRecentPoints >= 5.0f && disRecentPoints <= 40.0f)
            {*/

            routeDistance += disRecentPoints;
            points.add(currentLatLng);
            locations.add(currentLocation);
            this.drawPolyline();
        }
    }

    private void updateCurrentLocationMarker()
    {
        MarkerOptions options = options = new MarkerOptions()
                .position(currentLatLng)
                .title("You are here! ");
        options.icon(BitmapDescriptorFactory
                .defaultMarker(BitmapDescriptorFactory.HUE_AZURE));

        if (myCurrentLocation != null)
        {
            myCurrentLocation.remove();
        }

        myCurrentLocation = googleMap.addMarker(options);

        googleMap.animateCamera(CameraUpdateFactory.newLatLng(currentLatLng));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(currentLatLng));

        if (firstLocate)
        {
            googleMap.moveCamera(CameraUpdateFactory.zoomTo(18));
            firstLocate = false;
        }
        //SoundPlayer.vibrate(500, this.getActivity());
        //Toast for when we get a new location
        //Toast.makeText(this.getActivity().getApplicationContext(), "New Location Found",
        // Toast.LENGTH_SHORT).show();
    }

    private void setStartPostion()
    {

        Toast.makeText(this.getActivity().getApplicationContext(),
                "Set Start Point",
                Toast.LENGTH_SHORT).show();

        if (startPosition != null)
        {
            startPosition.remove();
        }
        if (myCurrentLocation != null)
        {
            myCurrentLocation.remove();
        }
        addStartPoint(currentLatLng);
        //Add the latlng object to a the linkedlist of points
        points.add(currentLatLng);
        locations.add(currentLocation);
    }

    public void addStartPoint(LatLng currentLatLng)
    {

        startPostionMarkerOptions = new MarkerOptions()
                .position(currentLatLng)
                .title("Start");
        startPostionMarkerOptions.icon(
                BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        startPosition = googleMap.addMarker(startPostionMarkerOptions);
    }

    private void setEndPostion()
    {
        Toast.makeText(this.getActivity().getApplicationContext(),
                "Set End Point",
                Toast.LENGTH_SHORT).show();
        if (endPosition != null)
        {
            endPosition.remove();
        }

        if (myCurrentLocation != null)
        {
            myCurrentLocation.remove();
        }

        addEndPoint(currentLatLng);

        //Add the latlng object to a the linkedlist of points
        points.add(currentLatLng);
        locations.add(currentLocation);
    }

    public void addEndPoint(LatLng currentLatLng)
    {
        endPostionMarkerOptions = new MarkerOptions()
                .position(currentLatLng)
                .title("End");
        endPostionMarkerOptions.icon(
                BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_RED));
        endPosition = googleMap.addMarker(endPostionMarkerOptions);
    }


    /**
     * drawPolyline - draws the polyline of the users route
     */
    public void drawPolyline()
    {
        PolylineOptions po = new PolylineOptions();
        po.addAll(points);
        po.color(Color.CYAN);
        po.width(20.0f);
        route = googleMap.addPolyline(po);
    }

    protected float routeDistance()
    {
        return routeDistance;
    }

    private BroadcastReceiver receiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();

            if (action.equals(AppConstraints.LOCATION_BROADCAST))
            {
                if (intent.getExtras() != null)
                {
                    Bundle b = intent.getExtras();
                    Location location = (Location) b.get("Location");
                    handleNewLocation(location);
                }
            }
        }
    };

    public void loadPoints(LinkedList<LatLng> points)
    {
        this.addStartPoint(points.getFirst());
        this.addEndPoint(points.getLast());
        this.points.addAll(points);
        this.drawPolyline();
    }

   /* public void onSaveInstanceState(Bundle savedInstanceState)
    {
        if (currentLocation != null)
        {

            savedInstanceState.putDouble("CurrentLatitude", currentLocation.getLatitude());
            savedInstanceState.putDouble("CurrentLongitude", currentLocation.getLongitude());
        }
        /*if (startPosition != null)
        {
            savedInstanceState.putDouble("StartLatitude", currentLocation.getLatitude());
            savedInstanceState.putDouble("StartLongitude", currentLocation.getLongitude());
        }
        if (st != null)
        {
            savedInstanceState.putDouble("StartLatitude", currentLocation.getLatitude());
            savedInstanceState.putDouble("StartLongitude", currentLocation.getLongitude());
        }
        super.onSaveInstanceState(savedInstanceState);
    }*/


}