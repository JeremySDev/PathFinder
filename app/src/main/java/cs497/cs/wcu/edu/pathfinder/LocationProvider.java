package cs497.cs.wcu.edu.pathfinder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import com.google.android.gms.maps.GoogleMap;

/**
 * @author Jeremy Stilwell
 * @version 4/21/15.
 */
public class LocationProvider
{

    //GoogleMap googleMap;

    String TAG = "Location Provider: ";

    Context mContext;

    MyMapFragment myMapFragment;

    public LocationProvider(Context context, MyMapFragment myMapFragment) {
        this.mContext = context;
        this.myMapFragment = myMapFragment;
        //this.googleMap = this.myMapFragment.googleMap;
    }

    /**
     * onStartMap - Checks if the map is null if so it gets a map from fragment. Then it attaches a
     * OnMapLoaded Callback which tells the map find the last fixed location. Adds a
     * OnMyLocationChange Listener that tells the map to call handleNewLocation.
     *
     * @param googleMap
     */
    public void onStartMap(GoogleMap googleMap)
    {
        LocationManager lm = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);

        // 5 miliseconds
        int minTime = 5000;

        // 10 meters
        int minDist = 10;

        //Check if the map is null if so it gets a map from fragment
        /*if (googleMap == null)
        {
            googleMap = this.myMapFragment.fragment.getMap();
        }*/

        //googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);

        // When map has loaded animate the camera to the user position.
        googleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback()
        {
            @Override
            public void onMapLoaded()
            {
                myMapFragment.goToLastFix();
            }
        });

        //Tells the map that when the position changes call handleNewLocation
        googleMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener()
        {
            @Override
            public void onMyLocationChange(Location location)
            {
                /*Toast.makeText(myMapFragment.getActivity(),
                        " My New Location " + location.toString(), Toast.LENGTH_LONG);*/
                Log.v(TAG, "My New Location: " + location.toString());
            }
        });

        //Enable listeners for GPS and Network provigers
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDist, oll);
        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTime, minDist, oll);
    }


    LocationListener oll = new LocationListener()
    {
        @Override
        public void onLocationChanged(Location newLocation)
        {
            Log.v(TAG, "Location Changed");
            myMapFragment.handleNewLocation(newLocation);
        }

        public void onStatusChanged(String provider, int status, Bundle extras)
        {
            Log.v(TAG, "Status Changed");
        }

        public void onProviderEnabled(String provider)
        {
            Log.v(TAG, "Provider Enabled");
        }

        public void onProviderDisabled(String provider)
        {
            Log.v(TAG, "Provider Disabled");
        }
    };

    public BroadcastReceiver receiver = new BroadcastReceiver()
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
                    myMapFragment.goToLocation(lat, lng);

                }//end if

            }//end for
            /*else if (intent.getAction().equals(AppConstraints.BROADCAST_TWO))
            {
                if (intent.getExtras() != null)
                {
                    Bundle b = intent.getExtras();
                    double lng = b.getDouble(AppConstraints.KEY_LONGITUDE);
                    double lat = b.getDouble(AppConstraints.KEY_LATITUDE);
                    int zoom = b.getInt(AppConstraints.KEY_ZOOM_LEVEL);
                    //myMapFragment.goToLocation(lat, lng, zoom);

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
                    //myMapFragment.goToLocation(lat, lng, zoom);

                }
            }*/
        }// end onReceive-----------------------------------------------------------------

    };
}
