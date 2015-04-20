package cs497.cs.wcu.edu.pathfinder;

import android.graphics.Color;
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
public class MyMapFragment extends Fragment implements LocationProvider.LocationCallback
{

    public static final String TAG = MyMapFragment.class.getSimpleName();

    private SupportMapFragment fragment;

    private GoogleMap mMap;

    private LocationProvider mLocationProvider;

    private LinkedList<LatLng> points = new LinkedList<>();
    private LinkedList<Location> locations = new LinkedList<>();

    private Marker startPostion;
    private Marker endPostion;

    private MarkerOptions startPostionMarkerOptions;
    private MarkerOptions endPostionMarkerOptions;

    private Polyline route;

    private static int track = 0;

    private boolean trackRoute = true;

    private static boolean firstMarker = true;
    private static boolean lastMarker = false;

    private float routeDistance = 0.0f;
    private Location lastLocation;


    /**
     * Does the initial creation of the fragment*
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.setHasOptionsMenu(true);

        mLocationProvider = new LocationProvider(this.getActivity(), this);
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
        mLocationProvider.connect();
    }

    @Override
    public void onPause()
    {
        super.onPause();
        mLocationProvider.disconnect();
    }

    private void setUpMap()
    {
        if (mMap == null)
        {
            mMap = fragment.getMap();
        }
        //mMap.setMyLocationEnabled(true);
        //mMap.getMyLocation();
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


    public void handleNewLocation(Location location)
    {
        Log.d(TAG, location.toString());
        //Toast for when we get a new location
        Toast.makeText(this.getActivity().getApplicationContext(), "New Location",
                Toast.LENGTH_SHORT).show();

        //Get the current Latitude and Longitude of the location
        double currentLatitude = location.getLatitude();
        double currentLongitude = location.getLongitude();

        //the distance between the last location and current one
        float disRecentPoints;

        //Create a LatLng object from our new location
        LatLng latLng = new LatLng(currentLatitude, currentLongitude);

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
                mMap.addMarker(startPostionMarkerOptions);
                firstMarker = false;
            }
            else if (lastMarker)
            {
                endPostionMarkerOptions = new MarkerOptions()
                        .position(latLng)
                        .title("Finish " + track);
                mMap.addMarker(endPostionMarkerOptions);
                trackRoute = false;
            }
            else
            {
                options = new MarkerOptions()
                        .position(latLng)
                        .title("I am here! " + track);
                mMap.addMarker(options);
                disRecentPoints = location.distanceTo(locations.getLast());

                Toast.makeText(this.getActivity().getApplicationContext(), "Distance: " + disRecentPoints,
                        Toast.LENGTH_SHORT).show();
            }

            locations.add(location);
            track++;
            this.drawPolyline();
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(17));
    }

    public void drawPolyline()
    {
        /*points.add(new LatLng(35.303268, -83.182322));
        points.add(new LatLng(35.310450, -83.182631));
        points.add(new LatLng(35.311357, -83.182171));
        points.add(new LatLng(35.310517, -83.183470));
        points.add(new LatLng(35.311235, -83.181227));
        points.add(new LatLng(35.313028, -83.179745));
        points.add(new LatLng(35.311295, -83.181263));
        points.add(new LatLng(35.305279, -83.182494));
        points.add(new LatLng(35.310717, -83.183028));
        points.add(new LatLng(35.307502, -83.182880));
        points.add(new LatLng(35.311440, -83.183000));
        points.add(new LatLng(35.309005, -83.186424));
        points.add(new LatLng(35.306355, -83.186963));
        points.add(new LatLng(35.310464, -83.186638));
        points.add(new LatLng(35.307116, -83.184431));
        points.add(new LatLng(35.309418, -83.183299));
        points.add(new LatLng(35.310000, -83.182575));
        points.add(new LatLng(35.311339, -83.178967));
        points.add(new LatLng(35.301412, -83.181435));
        points.add(new LatLng(35.309188, -83.186925));*/

        PolylineOptions po = new PolylineOptions();
        po.addAll(points);
        po.color(Color.BLUE);
        po.width(20.0f);
        route = mMap.addPolyline(po);
    }

    private float routeDistance()
    {
        float distance = 0.0f;

        for (int i = 0; i <= locations.size()-1; i++)
        {
            distance += (locations.get(i)).distanceTo((locations.get(i+1)));
        }

        return distance;
    }




}
