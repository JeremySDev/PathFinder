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
import android.widget.Toast;

/**
 * @author Jeremy Stilwell
 * @version 4/28/15.
 */
public class MyLocationService extends Service implements LocationListener
{

    private static final int TWO_MINUTES = 1000 * 60 * 2;

    public LocationManager locationManager;
    //public LocationListener listener;
    public Location previousBestLocation = null;

    Intent intent;

    @Override
    public void onCreate()
    {
        super.onCreate();
        intent = new Intent(AppConstraints.LOCATION_BROADCAST);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // 5 miliseconds
        int minTime = 5000;
        // 10 meters
        int minDist = 10;

        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTime, minDist, this);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDist, this);
        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

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
        locationManager.removeUpdates((LocationListener) this);
    }

    /*public class MyLocationListener implements LocationListener
    {*/
    int i = 0;

    public void onLocationChanged(final Location loc)
    {

        if (isBetterLocation(loc, previousBestLocation))
        {
            intent.putExtra("Latitude", loc.getLatitude());
            intent.putExtra("Longitude", loc.getLongitude());
            intent.putExtra("Provider", loc.getProvider());
            intent.putExtra("Location", loc);
            sendBroadcast(intent);
            i++;
        }
    }

    public void onProviderDisabled(String provider)
    {
        Toast.makeText(getApplicationContext(), "GPS Disabled", Toast.LENGTH_SHORT).show();
    }


    public void onProviderEnabled(String provider)
    {
        Toast.makeText(getApplicationContext(), "GPS Enabled", Toast.LENGTH_SHORT).show();
    }


    public void onStatusChanged(String provider, int status, Bundle extras)
    {
        Log.v("StatChange", "Status Changed");
    }
    //}
}
