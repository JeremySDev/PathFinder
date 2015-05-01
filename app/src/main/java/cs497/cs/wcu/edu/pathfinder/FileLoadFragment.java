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

import java.io.File;
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
    public File[] fileList;

    /* The directory of the app*/
    public File dir;
    public File routesDir;

    /* A list of text to display on our list view */
    private ArrayList<String> fileNames = new ArrayList<>();

    private ArrayList<String> datesOfRoutes = new ArrayList<>();

    private ArrayList<String> distances = new ArrayList<>();

    FileHandler fileHandler;

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
            fileList = this.routesDir.listFiles();
            String[] fileNameArray;

            String routeName;
            String routeDate;
            String routeDis;
            //get all the names of files and add them to the theNamesOfFiles array
            for (File file : fileList)
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

    /**
     * isLocationEnabled - checks if the user has GPS enable if not ask them if they want to
     */
    private void isLocationEnabled()
    {
        LocationManager lm = null;
        boolean gps_enabled = false;
        boolean network_enabled = false;
        final Context context = this.getActivity();

        lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        //Get whether they are enabled or not
        gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        //check their status if disable open a dialog
        if (!gps_enabled && !network_enabled)
        {
            //Open a dialog that take the user to the location settings
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

    /**
     * listViewHelper helps do all the work related to setting up and initializing a list view
     * @param rootView the view of the fragment.
     */
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
        String fileName = fileList[position].getName();

        //pass it to our file handler
        fileHandler = new FileHandler(this.getActivity(), fileName);

        //Parse the XML
        AppConstraints.parseXML(fileHandler.loadFile());

        //Load the map fragment
        sendFragChangeBroadcast();
    }

    /**
     * sendFragChangeBroadcast - sends a broadcast to change the fragment.
     */
    public void sendFragChangeBroadcast()
    {
        //Send broadcast to Tab Screen to switch the tab
        Intent i = new Intent();
        i.setAction(AppConstraints.TAB_BROADCAST);
        this.getActivity().sendBroadcast(i);
    }
}
