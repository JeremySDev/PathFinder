package cs497.cs.wcu.edu.pathfinder;

import android.content.Context;
import android.util.Log;

import java.io.File;

/**
 * @author Jeremy Stilwell
 * @version 4/18/15.
 */
public class AppConstraints
{

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
