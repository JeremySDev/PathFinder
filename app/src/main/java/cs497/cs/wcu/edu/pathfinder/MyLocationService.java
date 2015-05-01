package cs497.cs.wcu.edu.pathfinder;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

/**
 * @author Jeremy Stilwell
 * @version 4/28/15.
 */
public class MyLocationService extends Service implements LocationListener
{

    private static final int TWO_MINUTES = 1000 * 60 * 2;

    public LocationManager locationManager;

    public Location previousBestLocation = null;

    Intent intent;

    @Override
    public void onCreate()
    {
        super.onCreate();
        //give our intent an action
        intent = new Intent(AppConstraints.LOCATION_BROADCAST);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        //Create a location manager
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // 5 miliseconds
        int minTime = 5000;
        // 10 meters
        int minDist = 10;

        //get location updates from the location manager
        locationManager
                .requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTime, minDist, this);
        locationManager
                .requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDist, this);
        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    /**
     * isBetterLocation - checks if the new location is better to use than the older location.
     *
     * @param location            old location
     * @param currentBestLocation newer location
     * @return which location is best to use
     */
    protected boolean isBetterLocation(Location location, Location currentBestLocation)
    {
        if (currentBestLocation == null)
        {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer)
        {
            return true;
            // If the new location is more than two minutes older, it must be worse
        }
        else if (isSignificantlyOlder)
        {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate)
        {
            return true;
        }
        else if (isNewer && !isLessAccurate)
        {
            return true;
        }
        else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider)
        {
            return true;
        }
        return false;
    }


    /**
     * Checks whether two providers are the same
     */
    private boolean isSameProvider(String provider1, String provider2)
    {
        if (provider1 == null)
        {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        Log.v("STOP_SERVICE", "DONE");
        locationManager.removeUpdates(this);
    }

    /**
     * onLocationChanged - when the location is changed send the new location in a broadcast
     *
     * @param loc the new location
     */
    public void onLocationChanged(final Location loc)
    {
        if (isBetterLocation(loc, previousBestLocation))
        {
            intent.putExtra("Latitude", loc.getLatitude());
            intent.putExtra("Longitude", loc.getLongitude());
            intent.putExtra("Provider", loc.getProvider());
            intent.putExtra("Location", loc);
            sendBroadcast(intent);
            SoundPlayer.makeNotificationSound(this);
        }
    }

    /* Required interface methods */
    public void onProviderDisabled(String provider)
    {

    }

    public void onProviderEnabled(String provider)
    {

    }

    public void onStatusChanged(String provider, int status, Bundle extras)
    {
        Log.v("StatChange", "Status Changed");
    }
}
