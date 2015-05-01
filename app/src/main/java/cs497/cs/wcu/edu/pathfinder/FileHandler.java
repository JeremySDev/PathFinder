package cs497.cs.wcu.edu.pathfinder;

import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.EditText;
import android.widget.Toast;
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

    public FileHandler(Context context, String fileName)
    {
        this.context = context;
        this.routesDir = context.getDir("routes", Context.MODE_PRIVATE);
        this.fileToLoad = new File(routesDir, fileName);
    }

    public FileHandler(Marker startPosition, Marker endPosition, LinkedList<LatLng> points,
                       Context context)
    {
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.points = points;
        this.context = context;
        this.routesDir = context.getDir("routes", Context.MODE_PRIVATE);
    }


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
                        //If the user did not enter a name save the file as the current date
                        Date date = new Date();
                        String temp = date.toString().replaceAll(" ", "-");
                        String dateName =
                                new SimpleDateFormat("dd-M-yyyy").format(date).replaceAll(" ", "-");
                        if (value.equals(""))
                        {
                            value += "MyRoute_" + dateName + "_40mi" + /*temp +*/ ".xml";
                        }
                        //If the user did not give an extension to the file add the extension to it
                        if (!value.matches(".*\\.xml"))
                        {

                            value = value + "_" + dateName + "_40mi" + /*temp +*/ ".xml";

                        }
                        //call save to file and pass it the files name
                        Toast.makeText(context, value, Toast.LENGTH_SHORT).show();
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
     * saveToFile - take in a string of the filename you want to save too and then write the JSON
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

    public String routeToXML()
    {
        StringBuilder sb = new StringBuilder("");
        sb.append("<route>\n");
        sb.append("\t <points>\n");

        sb.append("\t\t<point>\n");
        sb.append("\t\t\t<lat>").append(startPosition.getPosition().latitude).append("</lat>\n");
        sb.append("\t\t\t<lng>").append(startPosition.getPosition().longitude).append("</lat>\n");
        sb.append("\t\t</point>\n");

        for (LatLng latLng : points)
        {
            sb.append("\t\t<point>\n");
            sb.append("\t\t\t<lat>").append(latLng.latitude).append("</lat>\n");
            sb.append("\t\t\t<lng>").append(latLng.longitude).append("</lat>\n");
            sb.append("\t\t</point>\n");
        }

        sb.append("\t\t<point>\n");
        sb.append("\t\t\t<lat>").append(endPosition.getPosition().latitude).append("</lat>\n");
        sb.append("\t\t\t<lng>").append(endPosition.getPosition().longitude).append("</lat>\n");
        sb.append("\t\t<point>\n");

        sb.append("\t<points>\n");
        sb.append("</route>\n");
        return sb.toString();
    }

    public String loadFile()
    {
        Scanner scanner;
        StringBuilder stringBuilder = new StringBuilder();
        try
        {
            scanner = new Scanner(fileToLoad);
            while (scanner.hasNext())
            {
                stringBuilder.append(scanner.next());
            }
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }

        String rawXML = stringBuilder.toString();
        return rawXML;
    }

}
