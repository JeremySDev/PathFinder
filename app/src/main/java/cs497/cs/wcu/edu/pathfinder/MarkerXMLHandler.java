package cs497.cs.wcu.edu.pathfinder;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * @author Jeremy Stilwell
 * @version 4/15/2015.
 */
public class MarkerXMLHandler extends DefaultHandler
{
    private Marker startPosition;
    private Marker endPosition;

    private MarkerOptions startPostionMarkerOptions;
    private MarkerOptions endPostionMarkerOptions;

    private LinkedList<LatLng> points = new LinkedList<>();

    /**
     * A global list of map markers*
     */
    private ArrayList<MarkerOptions> mapMarkers;

    /**
     * A temp map marker built to get each marker element*
     */
    LatLng temp;

    /**
     * A temporary lat lng*
     */
    double tmpLatN, tmpLngN;

    /**
     * The current element being read by the XML parser*
     */
    String currentElement;

    /**
     * Build a string for the title*
     */
    String tmpTitle = "";

    /**
     * Build a string for the latitude*
     */
    String tmpLat = "";

    /**
     * Build a String for the longitude*
     */
    String tmpLng = "";


    /**
     * Handle XML and store the result in the arrayList.
     */
    public MarkerXMLHandler()
    {
        points = new LinkedList<>();
    }

    /**
     * Return the array list of completed map markers.
     *
     * @return
     */
    public LinkedList<LatLng> getMapMarkers()
    {
        return points;
    }

    public Marker[] getStartEndMarkers()
    {
        return new Marker[]{startPosition, endPosition};
    }


    /**
     * @param uri        The xml name space uri
     * @param localName  The name of the xml tag opening i.e. marker
     * @param qName      The fully qualified name of the tag opening i.e. xs:marker.
     * @param attributes Any attributes that happen to be with the tag.
     * @throws org.xml.sax.SAXException SAXException Any SaxException,
     *                                  possibly wrapping another exception
     */
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes)
            throws SAXException
    {
        if (qName.equals("lng"))
        {
            currentElement = tmpLng;
        }
        else if (qName.equals("lat"))
        {
            currentElement = tmpLat;
        }
        else
        {
            currentElement = null;
        }

    }

    /**
     * When closing tag is reached.
     *
     * @param uri
     * @param localName The name of the closing tag i.e. lat
     * @param qName     The fully qualified name of the closing tag i.e. xs:lat
     * @throws org.xml.sax.SAXException SAXException Any SaxException,
     *                                  possibly wrapping another exception
     */
    @Override
    public void endElement(String uri, String localName, String qName)
            throws SAXException
    {

        if (qName.equals("lng"))
        {
            tmpLngN = Double.parseDouble(currentElement);
        }
        else if (qName.equals("lat"))
        {
            tmpLatN = Double.parseDouble(currentElement);
        }

        if (qName.equals("point"))
        {
            temp = new LatLng(tmpLatN, tmpLngN);
            points.add(temp);
            temp = null;
        }

        currentElement = null;
    }

    /**
     * Read the characters in.
     *
     * @param ch     An array of characters read.
     * @param start  The start position of the character array.
     * @param length The number of characters to use from the array.
     * @throws org.xml.sax.SAXException Any SaxException, possibly wrapping another exception.
     */
    @Override
    public void characters(char[] ch, int start, int length) throws SAXException
    {

        if (currentElement != null)
        {
            currentElement = currentElement + new String(ch, start, length);
        }
    }
}
