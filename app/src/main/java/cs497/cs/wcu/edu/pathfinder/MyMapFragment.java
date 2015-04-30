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
import android.util.Log;
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
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.StringReader;
import java.util.LinkedList;


/**
 *
 *
 *
 */
public class MyMapFragment extends Fragment
{
    public static final String TAG = MyMapFragment.class.getSimpleName();
    private static int track = 0;
    private static boolean firstMarker = true;
    private static boolean lastMarker = false;
    View rootView = null;

    public SupportMapFragment fragment;
    public GoogleMap googleMap;
    private LinkedList<LatLng> points = new LinkedList<>();
    private LinkedList<Location> locations = new LinkedList<>();
    private Marker startPosition;
    private Marker endPosition;
    private MarkerOptions startPostionMarkerOptions;
    private MarkerOptions endPostionMarkerOptions;
    private Polyline route;
    private boolean trackRoute = false;
    private float routeDistance = 0.0f;
    private Location lastLocation;

    //private LocationProvider locationProvider;

    /**
     * The map has run once *
     */
    boolean runOnce = true;
    boolean firstLocate = true;
    boolean stopPressed = true;

    Context context;

    ImageView record;

    Marker myCurrentLocation;

    Location currentLocation;

    LatLng currentLatLng;

    /**
     * Does the initial creation of the fragment*
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);


        context = this.getActivity();
        // use this to start and trigger a service


        ///locationProvider = new LocationProvider(this.getActivity(), this);
        this.setHasOptionsMenu(true);

        Intent i = new Intent(context, MyLocationService.class);
        context.startService(i);
        Toast.makeText(context, "Service Started", Toast.LENGTH_LONG).show();

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
        record = (ImageView) rootView.findViewById(R.id.imageView);
        firstLocate = true;
        return rootView;
    }

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

        if (savedInstanceState != null)
        {
            double savedLat = savedInstanceState.getDouble("CurrentLatitude");
            double savedLng = savedInstanceState.getDouble("CurrentLongitude");
            this.currentLatLng = new LatLng(savedLat, savedLng);
            this.updateCurrentLocationMarker();
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (googleMap == null)
        {
            googleMap = fragment.getMap();
            googleMap.setMyLocationEnabled(true);
        }

        /////////////////////////////////////
        //REGISTERING THE BROADCAST RECEIVER
        ////////////////////////////////////////
        IntentFilter filter = new IntentFilter();
        filter.addAction(AppConstraints.LOCATION_BROADCAST);
        //filter.addAction(AppConstraints.BROADCAST_TWO);
        //filter.addAction(AppConstraints.BROADCAST_THREE);
        this.getActivity().registerReceiver(receiver, filter);
    }


    @Override
    public void onPause()
    {
        Log.v(TAG, "On Pause Called");
        super.onPause();
        this.getActivity().unregisterReceiver(receiver);
        //SoundPlayer.makeSound(SoundPlayer.SOUND_BLIP6);
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
                stopPress(false);
                return true;
            //if the user pressed clear call the customViews clearScreen method
            case R.id.action_save:
                stopPress(true);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void recordPress()
    {
        if (stopPressed)
        {
            Toast.makeText(this.getActivity().getApplicationContext(), "Record",
                    Toast.LENGTH_SHORT).show();
            trackRoute = true;
            this.setStartPostion();
            stopPressed = false;
            record.setVisibility(View.VISIBLE);
        }
        else
        {
            Toast.makeText(this.getActivity().getApplicationContext(),
                    "Press Stop Recording First",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void stopPress(boolean isSave)
    {
        if (!stopPressed)
        {
            Toast.makeText(this.getActivity().getApplicationContext(), "Stop",
                    Toast.LENGTH_SHORT).show();
            this.setEndPostion();
            stopPressed = true;
            record.setVisibility(View.VISIBLE);
        }
        else
        {
            Toast.makeText(this.getActivity().getApplicationContext(),
                    "Start Recording Route First",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public String routeToXML()
    {
        StringBuilder sb = new StringBuilder("");
        sb.append("<route>\n");

        sb.append("\t<startmarker>\n");
        sb.append("\t\t<title>" + startPosition.getTitle() + "</title>\n");
        sb.append("\t\t<lat>" + startPosition.getPosition().latitude + "</lat>\n");
        sb.append("\t\t<lng>" + startPosition.getPosition().longitude + "</lat>\n");
        sb.append("\t</startmarker>\n");

        sb.append("\t<endmarker>\n");
        sb.append("\t\t<title>" + endPosition.getTitle() + "</title>\n");
        sb.append("\t\t<lat>" + endPosition.getPosition().latitude + "</lat>\n");
        sb.append("\t\t<lng>" + endPosition.getPosition().longitude + "</lat>\n");
        sb.append("\t</endmarker>\n");

        sb.append("\t <points>\n");
        for (LatLng latLng : points)
        {
            sb.append("\t\t<point>\n");
            sb.append("\t\t\t<lat>" + latLng.latitude + "</lat>\n");
            sb.append("\t\t\t<lng>" + latLng.longitude + "</lat>\n");
            sb.append("\t\t</point>\n");
        }//end for
        sb.append("\t<points>\n");
        sb.append("</route>\n");
        return sb.toString();
    }

    public void parseXML(String rawXML)
    {
        Log.w("AndroidParseXMLActivity", "Start Parsing");
        SAXParserFactory factory = SAXParserFactory.newInstance();
        try
        {
            SAXParser saxParser = factory.newSAXParser();
            XMLReader xmlreader = saxParser.getXMLReader();
            MarkerXMLHandler handler = new MarkerXMLHandler();
            xmlreader.setContentHandler(handler);
            //myData = handler.getObjectList();
            //Objects to read the stream.
            InputSource inStream = new InputSource();
            inStream.setCharacterStream(new StringReader(rawXML));
            //Parse the input stream
            //xmlreader.parse(inStream);
            //Get the map markers from the handler.
            //mapMarkers = handler.getMapMarkers();
        }
        catch (ParserConfigurationException | SAXException e)
        {
            Toast.makeText(this.getActivity(), "Error reading xml file.", Toast.LENGTH_LONG).show();
            e.printStackTrace();
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

        //this.goToLocation(lat, lng);
    }

    public void handleNewLocation(Location location)
    {
        currentLocation = location;

        if (location.hasAccuracy())
        {
            Log.d(TAG, "accuracy " + currentLocation.getAccuracy());
        }
        //SoundPlayer.vibrate(500, this.getActivity());
        SoundPlayer.makeNotificationSound(this.getActivity());

        //Get the current Latitude and Longitude of the location
        double currentLatitude = currentLocation.getLatitude();
        double currentLongitude = currentLocation.getLongitude();

        //Create a LatLng object from our new location
        currentLatLng = new LatLng(currentLatitude, currentLongitude);

        googleMap.animateCamera(CameraUpdateFactory.newLatLng(currentLatLng));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(currentLatLng));

        if (googleMap.getCameraPosition().zoom != 17)
        {
            googleMap.moveCamera(CameraUpdateFactory.zoomTo(17));
            firstLocate = false;
        }

        this.updateCurrentLocationMarker();

        float disRecentPoints = 0.0f;

        if (locations.size() >= 1)
        {
            disRecentPoints = currentLocation.distanceTo(locations.getLast());
            Toast.makeText(this.getActivity().getApplicationContext(),
                    "Distance: " + disRecentPoints,
                    Toast.LENGTH_SHORT).show();
        }

        //Start location
        if (trackRoute)
        {
            if (disRecentPoints >= 5.0f && disRecentPoints <= 40.0f)
            {
                points.add(currentLatLng);
                track++;
            }
            locations.add(currentLocation);
        }
        this.drawPolyline();
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
            googleMap.moveCamera(CameraUpdateFactory.zoomTo(17));
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

        startPostionMarkerOptions = new MarkerOptions()
                .position(currentLatLng)
                .title("Start");
        startPostionMarkerOptions.icon(
                BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

        startPosition = googleMap.addMarker(startPostionMarkerOptions);
        //Add the latlng object to a the linkedlist of points
        points.add(currentLatLng);
        locations.add(currentLocation);
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

        endPostionMarkerOptions = new MarkerOptions()
                .position(currentLatLng)
                .title("End");
        endPostionMarkerOptions.icon(
                BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));
        endPosition = googleMap.addMarker(endPostionMarkerOptions);
        //Add the latlng object to a the linkedlist of points
        points.add(currentLatLng);
        locations.add(currentLocation);
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

    private float routeDistance()
    {
        float distance = 0.0f;

        for (int i = 0; i <= locations.size() - 1; i++)
        {
            distance += (locations.get(i)).distanceTo((locations.get(i + 1)));
        }

        return distance;
    }

    private BroadcastReceiver receiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();
            Log.v("RECEIVE", "I Received " + action);
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