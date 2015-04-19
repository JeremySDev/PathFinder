package cs497.cs.wcu.edu.pathfinder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;


/**
 * @author Jeremy Stilwell
 * @version 3/3/15.
 */
public class CustomListAdapter extends ArrayAdapter<String>
{
    /* A list of text to display on our list view */
    private ArrayList<String> routeNames;

    private ArrayList<String> dates;

    private ArrayList<String> distances;

    /* The context of the activity hosting the list */
    private Context context;

    int viewSource;/* A reference to the XML layout that defines a row view. */

    /**
     * Constructor for a custom list Adapter.
     *
     * @param context            The context of the activity that holds the list view.
     * @param textViewResourceId The resource of the view displaying the list contents.
     * @param fileNames          An array list of String objects to render.
     */
    public CustomListAdapter(Context context, int textViewResourceId, ArrayList<String> fileNames,
                             ArrayList<String> datesOfRoutes, ArrayList<String> distances)
    {
        super(context, textViewResourceId, fileNames);
        this.context = context;

        this.routeNames = fileNames;
        this.dates = datesOfRoutes;
        this.distances = distances;

        viewSource = textViewResourceId;
    }

    /**
     * Get the view at the given position in the list view.
     * THIS IS OVERRIDEN FROM THR ARRAY ADAPTER CLASS.
     *
     * @param position     The position in the Array list of items which is to be rendered.
     * @param listItemView The layout view that defines the design and components of each list
     *                     view row.
     * @param parent       The parent container that holds the list view.
     */
    @Override
    public View getView(int position, View listItemView, ViewGroup parent)
    {
        View v = listItemView;

        if (v == null)//Only create if null - recycling a good idea
        {
            LayoutInflater inflater =
                    (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(viewSource, parent, false);
        }

        String routeText = this.routeNames.get(position);
        String dateText = this.dates.get(position);
        String distanceText = this.distances.get(position);

        //Get the views from the list item XML
        TextView filenameTextView = (TextView) v.findViewById(R.id.filename_text_view);
        TextView dateTextView = (TextView) v.findViewById(R.id.date_text_view);
        TextView distanceTextView = (TextView) v.findViewById(R.id.distance_text_view);

        //Set texts on views
        filenameTextView.setText(routeText);
        dateTextView.setText(dateText);
        distanceTextView.setText(distanceText);


        return (v);//Return the layout view populated with data.
    }
}

