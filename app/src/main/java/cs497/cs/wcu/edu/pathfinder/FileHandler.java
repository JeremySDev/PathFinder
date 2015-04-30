package cs497.cs.wcu.edu.pathfinder;

import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.LinkedList;

/**
 * @author Jeremy Stilwell
 * @version 4/30/15.
 */
public class FileHandler extends Application
{

    Marker startPosition;
    Marker endPosition;
    LinkedList<LatLng> points;
    File dir;

    public FileHandler(Marker startPosition, Marker endPosition, LinkedList<LatLng> points)
    {
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.points = points;
        this.dir = this.getApplicationContext().getFilesDir();
    }

    public void openNameFileDialog()
    {
        //Create an edit text to get user input from the dialog
        final EditText input = new EditText(this);
        //Create a context for the paint fragment so that it can save the file
        final Context context = this;

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

                        if (value.equals(""))
                        {
                            Date date = new Date();
                            //replace spaces with -
                            value = date.toString().replaceAll(" ", "-");
                        }
                        //If the user did not give an extension to the file add the extension to it
                        if (!value.matches(".*\\.json"))
                        {
                            value = value + ".json";
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
     * saveToFile - take in a string of the filename you want to save too and then write the JSON
     * data to that file.
     *
     * @param fileName - the name of the file we will save to.
     */
    public void saveToFile(String fileName)
    {

        //Create a file from the filename passed in and the apps directory
        File fileWithinMyDir = new File(dir, fileName);

        PrintWriter writer;
        //Try and write the contents of linesJSON to the file
        try
        {
            writer = new PrintWriter(fileWithinMyDir, "UTF-8");
            writer.println(this.routeToXML());
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

        sb.append("\t<startmarker>\n");
        sb.append("\t\t<title>" + startPosition.getTitle() + "</title>\n");
        sb.append("\t\t<lat>" + startPosition.getPosition().latitude + "</lat>\n");
        sb.append("\t\t<lng>" + startPosition.getPosition().longitude + "</lat>\n");
        sb.append("\t</startmarker>\n");

        sb.append("\t<endmarker>\n");
        sb.append("\t\t<title>" + endPosition.getTitle() + "</title>\n");
        sb.append("\t\t<lat>" + endPosition.getPosition().latitude + "</lat>\n");
        sb.append("\t\t<lng>" + endPosition.getPosition().longitude + "</lat>\n");
        sb.append("\t</endmarker>\n");

        sb.append("\t <points>\n");
        for (LatLng latLng : points)
        {
            sb.append("\t\t<point>\n");
            sb.append("\t\t\t<lat>" + latLng.latitude + "</lat>\n");
            sb.append("\t\t\t<lng>" + latLng.longitude + "</lat>\n");
            sb.append("\t\t</point>\n");
        }//end for
        sb.append("\t<points>\n");
        sb.append("</route>\n");
        return sb.toString();
    }

    public void parseXML(String rawXML)
    {
        Log.w("AndroidParseXMLActivity", "Start Parsing");
        SAXParserFactory factory = SAXParserFactory.newInstance();
        try
        {
            SAXParser saxParser = factory.newSAXParser();
            XMLReader xmlreader = saxParser.getXMLReader();
            MarkerXMLHandler handler = new MarkerXMLHandler();
            xmlreader.setContentHandler(handler);
            //myData = handler.getObjectList();
            //Objects to read the stream.
            InputSource inStream = new InputSource();
            inStream.setCharacterStream(new StringReader(rawXML));
            //Parse the input stream
            //xmlreader.parse(inStream);
            //Get the map markers from the handler.
            //mapMarkers = handler.getMapMarkers();
        }
        catch (ParserConfigurationException | SAXException e)
        {
            Toast.makeText(this, "Error reading xml file.", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
}
