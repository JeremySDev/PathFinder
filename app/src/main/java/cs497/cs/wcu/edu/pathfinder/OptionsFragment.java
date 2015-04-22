package cs497.cs.wcu.edu.pathfinder;

import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * OptionsFragment - this fragment is responsible for displaying and changing the paint color and
 * brush size settings.
 *
 * @author Jeremy Stilwell
 */
public class OptionsFragment extends Fragment implements AdapterView.OnItemClickListener
{

    Spinner spinnerPolyLineColor;
    Spinner spinnerSMarkerColor;
    private SharedPreferences settings;

    /* An array of colors available to the user */
    private final String[] listColors =
            new String[]{"black", "blue", "red", "green", "purple",
                    "orange", "dark blue", "dark purple", "dark green",
                    "dark purple", "dark orange"};

    /**
     * Called on create and displays a calculator window.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_options, container, false);

        //initialize the SharedPreferences object
        settings =
                this.getActivity().getSharedPreferences(AppConstraints.PREFS, Context.MODE_PRIVATE);
        //Get the current color setting
        //String color = settings.getString(AppConstraints.BRUSH_COLOR_PREF,
        // AppConstraints.DEFAULT_BRUSH_COLOR);
        //Get the current size setting
        //int size = settings.getInt(AppConstraints.BRUSH_SIZE_PREF,
        // AppConstraints.DEFAULT_BRUSH_SIZE);

        //Text views that tell the user the current settings
        spinnerPolyLineColor = (Spinner) rootView.findViewById(R.id.spinnerPolyLineColor);
        spinnerSMarkerColor = (Spinner) rootView.findViewById(R.id.spinnerSMarkerColor);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getActivity(),
                R.array.marker_colors, android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        spinnerSMarkerColor.setAdapter(adapter);


        //Set the text of the text views
        //textViewBrushColor.setText("Brush Color: " + color);
        //textViewBrushWidth.setText("Brush Width: " + size);

        //Initialize the list view and seek bar
        //ListView listViewColors = (ListView) rootView.findViewById(R.id.listViewColors);
        //SeekBar seekBarBrushWidth = (SeekBar) rootView.findViewById(R.id.seekBarBrushWidth);

        //set the seek bar to the current size setting
        //seekBarBrushWidth.setProgress(size);

        //Set up action listeners
        //listViewColors.setOnItemClickListener(this);
        //seekBarBrushWidth.setOnSeekBarChangeListener(this);

        //Instantiate the adapter and pass to it the layout to be displayed
        /*ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(this.getActivity(), R.layout.list_item, listColors);*/

        //listViewColors.setAdapter(adapter);
        return rootView;
    }

    @Override
    public void onItemClick(AdapterView<?> l, View view, int position, long id)
    {
        //Get the color the user picked
        String text = "" + ((TextView) view).getText();

        //Change the Textview
        //textViewBrushColor.setText("Brush Color: " + text);

        //Edit the shared prefs
        //settings.edit().putString(AppConstraints.BRUSH_COLOR_PREF, text.toLowerCase()).apply();
    }

}
