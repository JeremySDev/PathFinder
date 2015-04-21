package cs497.cs.wcu.edu.pathfinder;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import com.google.android.gms.maps.model.LatLng;

import java.io.File;

/**
 * @author Jeremy Stilwell
 * @version 4/18/15.
 */
public class AppConstraints extends Application
{

    //**Intent Key for zoom**
    public static final String KEY_TAB ="key_tab";

    //**Intent Key for zoom**
    public static final String KEY_ZOOM_LEVEL ="zoom level";

    /**Intent key for latitude**/
    public static final String KEY_LATITUDE = "LAT";

    /**Intent key for longitude**/
    public static final String KEY_LONGITUDE = "LNG";

    /**Intend key for fubar**/
    public static final String KEY_FOOBAR = "cs497.cs.wcu.edu.pathfinder.FOOBAR";

    /**A tab change is occouring**/
    public static final String TAB_BROADCAST ="broadcast_tab";

    /**Acts as an intent filter**/
    public static  final String LOCATION_BROADCAST ="edu.wcu.location_broadcast";

    /**A first broadcast**/
    public static  final String BROADCAST_ONE = "edu.wcu.location_broadcast_one";

    /**A second broadcast**/
    public static  final String BROADCAST_TWO = "edu.wcu.location_broadcast_two";

    /**A third broadcast**/
    public static  final String BROADCAST_THREE = "edu.wcu.location_broadcast_three";

    /**A forth broadcast**/
    public static  final String BROADCAST_FOUR = "edu.wcu.location_broadcast_four";
    public static final LatLng CULLOWHEE = new LatLng(35.308016, -83.165131);

    private static Context instance;

    @Override
    public void onCreate() {
        super.onCreate();

        //Maintain an instance of this for static access
        instance = this.getApplicationContext();
    }

    /**
     * Get the class's instance.
     * @return The single instance that is maintained as a static field.
     */
    //==========================================================================
    public static Context getInstance(){
        return instance;
    }

    /**
     * isDirEmpty - checks if the folder that route files are stored in is empty
     *
     * @param context - a context object
     * @return boolean whether the directory is empty or not
     */
    public static boolean isDirEmpty(Context context)
    {
        //Return value
        boolean dirEmpty;
        //The directory where routes are stored
        File routesDir = context.getDir("routes", Context.MODE_PRIVATE);
        //A list of files in the directory
        File[] filesInDir = routesDir.listFiles();

        for (File file : filesInDir)
        {
            Log.v("FILES", file.getName());
        }
        //Find out if the directory is empty
        dirEmpty = filesInDir.length == 0;
        //Return
        return dirEmpty;
    }
}
