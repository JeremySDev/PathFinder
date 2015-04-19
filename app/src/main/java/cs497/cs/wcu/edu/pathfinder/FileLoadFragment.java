package cs497.cs.wcu.edu.pathfinder;


import android.content.Context;
import android.os.Bundle;
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

            //Set the list view adapters for this view.
            ListView listView = (ListView) rootView.findViewById(R.id.list);

            //this class will be the on item click listener for the list.
            listView.setOnItemClickListener(this);

            //Creating an internal dir;
            dir = this.getActivity().getFilesDir();
            routesDir = this.getActivity().getDir(dir.getAbsolutePath() + "routes", Context.MODE_PRIVATE);
/*
        File files = new File(dir, "MyRoute-Today-40mi.xml");
        PrintWriter writer;

        //Try and write the contents of linesJSON to the file
        /*try
        {
            writer = new PrintWriter(files, "UTF-8");
            writer.println("Hello, World");
            writer.close();
        }
        catch (FileNotFoundException | UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }*/


            //Get the files in the directory
            filelist = this.routesDir.listFiles();
            String[] fileNameArray;
            //get all the names of files and add them to the theNamesOfFiles array
            for (File file : filelist)
            {
                fileNameArray = file.getName().split("-");
                Log.v("FILE", Arrays.toString(fileNameArray));
                if (fileNameArray.length >= 2)
                {
                    fileNames.add(fileNameArray[0]);
                    datesOfRoutes.add(fileNameArray[1]);
                    distances.add(fileNameArray[2]);
                }
            }

            //Instantiate the custom adapter and pass to it the layout to be displayed
            ArrayAdapter<String> adapter =
                    new CustomListAdapter(this.getActivity(), R.layout.list_item, fileNames,
                            datesOfRoutes, distances);

            listView.setAdapter(adapter);
        }
        else
        {
            rootView = inflater.inflate(R.layout.fragment_no_files, container, false);
        }
        return rootView;
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
