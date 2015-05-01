package cs497.cs.wcu.edu.pathfinder;

import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.EditText;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.Scanner;

/**
 * Handles saving and loading routes to the map
 *
 * @author Jeremy Stilwell
 * @version 4/30/15.
 */
public class FileHandler extends Application
{

    Marker startPosition;
    Marker endPosition;
    LinkedList<LatLng> points;
    File routesDir;
    File fileToLoad;
    final Context context;
    float routeDistance;

    /**
     * Called when loading a file
     *
     * @param context  a context object
     * @param fileName - the name of the file to load
     */
    public FileHandler(Context context, String fileName)
    {
        this.context = context;
        //Get the director that contains the saved routes
        this.routesDir = context.getDir("routes", Context.MODE_PRIVATE);
        //get the file we want to load
        this.fileToLoad = new File(routesDir, fileName);
    }

    /**
     * Called when saving a route to a file
     *
     * @param startPosition the start position marker
     * @param endPosition   the end position marker
     * @param points        the points in the route
     * @param routeDistance
     * @param context       a context object
     */
    public FileHandler(Marker startPosition, Marker endPosition, LinkedList<LatLng> points,
                       float routeDistance, Context context)
    {
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.points = points;
        this.routeDistance = routeDistance;
        this.context = context;
        this.routesDir = context.getDir("routes", Context.MODE_PRIVATE);
    }

    /**
     * called when the user wants to save their route to a file. Its opens a dialog for the user to
     * enter the name of the file
     */
    public void openNameFileDialog()
    {
        //Create an edit text to get user input from the dialog
        final EditText input = new EditText(context);

        //Build an alert dialog
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setMessage("Save File As");

        // Launch the dialog and hook up the ok button
        alertDialogBuilder.setPositiveButton(R.string.ok_button,
                new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1)
                    {
                        //get the users input
                        String value = "" + input.getText();
                        //get the date for the name of the file
                        Date date = new Date();
                        String temp = date.toString().replaceAll(" ", "-");
                        String dateName =
                                new SimpleDateFormat("dd-M-yyyy").format(date).replaceAll(" ", "-");
                        if (value.equals(""))
                        {
                            value +=
                                    "MyRoute_" + dateName + "_" + routeDistance + /*temp +*/ ".xml";
                        }
                        //If the user did not give an extension to the file add the extension to it
                        if (!value.matches(".*\\.xml"))
                        {

                            value = value + "_" + dateName + "_" + routeDistance + /*temp +*/".xml";

                        }
                        //call save to file and pass it the files name
                        saveToFile(value);
                    }
                });
        //Create the alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        //give it the text view
        alertDialog.setView(input);
        //show the dialog
        alertDialog.show();
    }

    /**
     * saveToFile - take in a string of the filename you want to save too and then write the XML
     * data to that file.
     *
     * @param fileName - the name of the file we will save to.
     */
    public void saveToFile(String fileName)
    {

        //Create a file from the filename passed in and the apps directory
        File fileWithinMyDir = new File(routesDir, fileName);

        PrintWriter writer;
        //Try and write the contents of linesJSON to the file
        try
        {
            writer = new PrintWriter(fileWithinMyDir, "UTF-8");
            writer.print(this.routeToXML());
            writer.close();
        }
        catch (FileNotFoundException | UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * routeToXML turns the route into a string of XML
     *
     * @return a string that has the routes XML
     */
    public String routeToXML()
    {
        StringBuilder sb = new StringBuilder("");
        sb.append("<route>\n");
        sb.append("\t <points>\n");

        //add the start point
        sb.append("\t\t<point>\n");
        sb.append("\t\t\t<lat>").append(startPosition.getPosition().latitude).append("</lat>\n");
        sb.append("\t\t\t<lng>").append(startPosition.getPosition().longitude).append("</lng>\n");
        sb.append("\t\t</point>\n");


        for (LatLng latLng : points)
        {
            sb.append("\t\t<point>\n");
            sb.append("\t\t\t<lat>").append(latLng.latitude).append("</lat>\n");
            sb.append("\t\t\t<lng>").append(latLng.longitude).append("</lng>\n");
            sb.append("\t\t</point>\n");
        }

        //add the end point
        sb.append("\t\t<point>\n");
        sb.append("\t\t\t<lat>").append(endPosition.getPosition().latitude).append("</lat>\n");
        sb.append("\t\t\t<lng>").append(endPosition.getPosition().longitude).append("</lng>\n");
        sb.append("\t\t</point>\n");

        sb.append("\t</points>\n");
        sb.append("</route>\n");
        return sb.toString();
    }

    /**
     * loadFile reads in the file we want to load and puts its XML into a string
     *
     * @return the string version of the file we want to load
     */
    public String loadFile()
    {
        //create a scanner and string builder
        Scanner scanner;
        StringBuilder stringBuilder = new StringBuilder();
        try
        {
            //init the scanner with the file we want to load
            scanner = new Scanner(fileToLoad);
            //read the contents of the file
            while (scanner.hasNext())
            {
                stringBuilder.append(scanner.next());
            }
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }

        //return the string from the string builder
        return stringBuilder.toString();
    }
}
