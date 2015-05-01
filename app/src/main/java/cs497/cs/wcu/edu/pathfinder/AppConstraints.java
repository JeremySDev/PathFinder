package cs497.cs.wcu.edu.pathfinder;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import com.google.android.gms.maps.model.LatLng;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.LinkedList;

/**
 * @author Jeremy Stilwell
 * @version 4/18/15.
 */
public class AppConstraints extends Application
{
    /* Name of preferences file */
    public static final String PREFS = "cs497.cs.wcu.edu.pathfinder";

    /* A tab change is occurring */
    public static final String TAB_BROADCAST = "broadcast_tab";
    public static final String TAB_BROADCAST2 = "broadcast_tab2";

    /* Acts as an intent filter */
    public static final String LOCATION_BROADCAST = "edu.wcu.location_broadcast";

    public static final LatLng CULLOWHEE = new LatLng(35.308016, -83.165131);

    /* Tell the map fragment if it needs to load a map */
    public static boolean loadMap = false;

    /* List of the points in a route */
    public static LinkedList<LatLng> points = new LinkedList<>();

    @Override
    public void onCreate()
    {
        super.onCreate();
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

    /**
     * parseXML - does the work of
     *
     * @param rawXML - a string form of the XML in teh saved file.
     */
    public static void parseXML(String rawXML)
    {
        Log.w("AndroidParseXMLActivity", "Start Parsing");
        SAXParserFactory factory = SAXParserFactory.newInstance();
        try
        {
            SAXParser saxParser = factory.newSAXParser();
            XMLReader xmlreader = saxParser.getXMLReader();

            MarkerXMLHandler handler = new MarkerXMLHandler();
            xmlreader.setContentHandler(handler);

            //Objects to read the stream.
            InputSource inStream = new InputSource();
            inStream.setCharacterStream(new StringReader(rawXML));

            //Parse the input stream
            xmlreader.parse(inStream);

            //Get the map points from the handler.
            points.addAll(handler.getMapMarkers());
        }
        catch (ParserConfigurationException | SAXException | IOException e)
        {
            e.printStackTrace();
        }
    }
}

