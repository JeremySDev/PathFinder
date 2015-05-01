package cs497.cs.wcu.edu.pathfinder;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * FileLoadFragment - this fragment is responsible for displaying a list view of saved files and
 * load those files when one is selected.
 *
 * @author Jeremy Stilwell
 */
public class FileLoadFragment extends Fragment implements OnItemClickListener
{

    /* A file array of the files in the apps directory*/
    public File[] filelist;

    /* The directory of the app*/
    public File dir;
    public File routesDir;


    /* A list of text to display on our list view */
    private ArrayList<String> fileNames = new ArrayList<>();

    private ArrayList<String> datesOfRoutes = new ArrayList<>();

    private ArrayList<String> distances = new ArrayList<>();

    /**
     * Called to create the fragment view.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View rootView;

        if (!AppConstraints.isDirEmpty(this.getActivity()))
        {
            rootView = inflater.inflate(R.layout.fragment_file_load, container, false);

            this.listViewHelper(rootView);

            //Creating an internal dir;
            dir = this.getActivity().getFilesDir();
            //make a folder in the applications directory named routes
            boolean test = new File(dir.getName(), "routes").mkdir();
            routesDir = this.getActivity().getDir("routes", Context.MODE_PRIVATE);

            //this.makeTestFiles();
            //Get the files in the directory
            filelist = this.routesDir.listFiles();
            String[] fileNameArray;

            String routeName;
            String routeDate;
            String routeDis;
            //get all the names of files and add them to the theNamesOfFiles array
            for (File file : filelist)
            {
                fileNameArray = file.getName().split("_");
                Log.v("FILES", Arrays.toString(fileNameArray));
                if (fileNameArray.length >= 2)
                {
                    routeName = fileNameArray[0];
                    routeDate = fileNameArray[1];
                    routeDis = fileNameArray[2];

                    if (routeName.length() >= 10)
                    {
                        Log.v("FILENAME", routeName);
                        routeName = routeName.substring(0, 7);
                        routeName += "...";
                        Log.v("FILENAME", routeName);
                    }

                    fileNames.add(routeName);
                    datesOfRoutes.add(routeDate);
                    distances.add(routeDis.substring(0, routeDis.length() - 4));
                }
            }

        }
        else
        {
            rootView = inflater.inflate(R.layout.fragment_no_files, container, false);
        }
        this.isLocationEnabled();
        return rootView;
    }

    private void isLocationEnabled()
    {
        LocationManager lm = null;
        boolean gps_enabled = false;
        boolean network_enabled = false;
        final Context context = this.getActivity();
        if (lm == null)
        {
            lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        }
        try
        {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        }
        catch (Exception ex)
        {
        }
        try
        {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        }
        catch (Exception ex)
        {
        }

        if (!gps_enabled && !network_enabled)
        {
            AlertDialog.Builder dialog = new AlertDialog.Builder(context);
            dialog.setMessage(context.getResources().getString(R.string.gps_network_not_enabled));
            dialog.setPositiveButton(
                    context.getResources().getString(R.string.open_location_settings),
                    new DialogInterface.OnClickListener()
                    {

                        @Override
                        public void onClick(DialogInterface paramDialogInterface, int paramInt)
                        {
                            Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            context.startActivity(myIntent);
                            //get gps
                        }
                    });
            dialog.setNegativeButton(context.getString(R.string.cancel),
                    new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface paramDialogInterface, int paramInt)
                        {
                            //Auto-generated method stub
                        }
                    });
            dialog.show();
        }
    }

    //private void makeTestFiles()
    {

        File file1 = new File(routesDir, "ABCDABCDABCDABCDABCDABCDABCD_4-19-2015_22mi.xml");
        File file2 = new File(routesDir, "MyRoute2_4-20-2015_12mi.xml");
        File file3 = new File(routesDir, "MyRoute3_4-21-2015_30mi.xml");
        File file4 = new File(routesDir, "MyRoute4_4-22-2015_46mi.xml");
        File file5 = new File(routesDir, "MyRoute5_4-30-2015_53mi.xml");
        File[] files = new File[]{file1, file2, file3, file4, file5,};
        PrintWriter writer;
        for (File file : files)
        {
            try
            {
                writer = null;
                writer = new PrintWriter(file, "UTF-8");
                writer.println("Hello, World");
                writer.close();
            }
            catch (FileNotFoundException | UnsupportedEncodingException e)
            {
                e.printStackTrace();
            }
        }
    }

    private void listViewHelper(View rootView)
    {
        //Set the list view adapters for this view.
        ListView listView = (ListView) rootView.findViewById(R.id.list);

        //this class will be the on item click listener for the list.
        listView.setOnItemClickListener(this);
        //Instantiate the custom adapter and pass to it the layout to be displayed
        ArrayAdapter<String> adapter =
                new CustomListAdapter(this.getActivity(), R.layout.list_item, fileNames,
                        datesOfRoutes, distances);

        listView.setAdapter(adapter);
    }

    /*When an item in the list is clicked this is fired.*/
    @Override
    public void onItemClick(AdapterView<?> l, View view, int position, long id)
    {
        //get the name of the file
        String fileName = ((TextView) view).getText().toString();

        //pass the name to readFile
        this.readFile(fileName);

        //Let the user know the file was loaded
        Toast.makeText(this.getActivity().getApplicationContext(), "Loaded: " + fileName,
                Toast.LENGTH_SHORT).show();

    }

    /**
     * readFile - takes in a filename make a file from it reads it in and places its contents in
     * linesJSON
     *
     * @param fileName
     */
    public void readFile(String fileName)
    {

        /*//create a LinkedList JSONObjects
        LinkedList<JSONObject> linesJSON = new LinkedList<JSONObject>();

        //create a parser and obj
        JSONParser parser = new JSONParser();
        Object obj = null;
        Iterator iterator;
        //Create a file from the apps directory and the name of the file.
        File fileWithinMyDir = new File(dir, fileName);

        //Try to parse the JSON file
        try
        {
            obj = parser.parse(new FileReader(fileWithinMyDir));
        }
        catch (IOException | ParseException e)
        {
            e.printStackTrace();
        }

        //cast obj to an JSON array
        JSONArray jsonArray = (JSONArray) obj;
        //Check if it is null
        if (jsonArray != null)
        {
            //Get an iterator from
            iterator = jsonArray.iterator();

            //use iterator to get the JSON objects out and add them to linesJSON
            while (iterator.hasNext())
            {
                JSONObject temp = (JSONObject) iterator.next();
                linesJSON.add(temp);
            }
        }
        //Pass the linked list of lines to appcontext.
        AppContext.setLineJSON(linesJSON);*/
    }
}
