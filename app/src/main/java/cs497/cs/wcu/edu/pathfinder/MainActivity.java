package cs497.cs.wcu.edu.pathfinder;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;


public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks
{

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    boolean dirEmpty;

    String tag = "IS EMPTY";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
        //Get the applications directory
        File dir = this.getApplicationContext().getFilesDir();
        //make a folder in the applications directory named routes
        boolean test = new File(dir.getName(), "routes").mkdir();

        dirEmpty = AppConstraints.isDirEmpty(this.getApplicationContext());


    }

    @Override
    public void onResume()
    {
        super.onResume();
        //////////////////////////////////////////
        //REGISTERING THE BROADCAST RECEIVER
        ////////////////////////////////////////
        IntentFilter filter = new IntentFilter();
        filter.addAction(AppConstraints.TAB_BROADCAST);
        this.registerReceiver(tab_receiver, filter);
    }

    @Override
    public void onPause()
    {
        super.onPause();
        this.unregisterReceiver(tab_receiver);

    }

    @Override
    public void onNavigationDrawerItemSelected(int position)
    {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fileLoadFragment = new FileLoadFragment();
        Fragment mapFragment = new MyMapFragment();
        Fragment optionsFragment = new OptionsFragment();

        if (position == 0)
        {
            fragmentManager.beginTransaction().replace(R.id.container, fileLoadFragment).commit();
        }
        if (position == 1)
        {
            fragmentManager.beginTransaction().replace(R.id.container, mapFragment).commit();
        }
        if (position == 2)
        {
            fragmentManager.beginTransaction().replace(R.id.container, optionsFragment).commit();
        }
    }

    public void onSectionAttached(int number)
    {
        switch (number)
        {
            case 1:
                mTitle = getString(R.string.title1_saved_routes);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
        }
    }

    public void restoreActionBar()
    {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        if (!mNavigationDrawerFragment.isDrawerOpen())
        {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment
    {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber)
        {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment()
        {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState)
        {
            //View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return inflater.inflate(R.layout.fragment_main, container, false);
        }

        @Override
        public void onAttach(Activity activity)
        {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }


    /**
     * An inner class to handle receive broadcasts
     */
    private BroadcastReceiver tab_receiver = new BroadcastReceiver()
    {
        /** Receives broadcast messages from the system. */
        @Override
        public void onReceive(Context context, Intent intent)
        {
            FragmentManager fragmentManager = getSupportFragmentManager();
            Fragment mapFragment = new MyMapFragment();
            //IF tab broadcast do this.
            if (intent.getAction().equals(AppConstraints.TAB_BROADCAST))
            {
                AppConstraints.loadMap = true;
                fragmentManager.beginTransaction().replace(R.id.container, mapFragment).commit();
                sendFragChangeBroadcast();
            }
        }
    };

    public void sendFragChangeBroadcast()
    {
        //Send broadcast to Tab Screen to switch the tab
        Intent i = new Intent();
        i.setAction(AppConstraints.TAB_BROADCAST2);
        this.sendBroadcast(i);
    }
}

