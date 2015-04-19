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

    public static boolean isDirEmpty(Context context)
    {
        boolean dirEmpty;
        File dir = context.getFilesDir();
        boolean test = new File(dir.getName(), "routes").mkdir();
        File routesDir = context.getDir("routes", Context.MODE_PRIVATE);
        File[] filesInDir = routesDir.listFiles();

        for (File file : filesInDir)
        {
            Log.v("FILES", file.getName());
        }

        dirEmpty = filesInDir.length == 0;
        return dirEmpty;
    }
}
