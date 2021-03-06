package com.pregiel.odtwarzacz_pilot;

import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.pregiel.odtwarzacz_pilot.Playlist.Playlist;
import com.pregiel.odtwarzacz_pilot.Views.MyViewPager;
import com.pregiel.odtwarzacz_pilot.Views.PageListener;
import com.pregiel.odtwarzacz_pilot.Views.PilotView;
import com.pregiel.odtwarzacz_pilot.Views.PlaylistView;
import com.pregiel.odtwarzacz_pilot.Views.PreviewView;
import com.pregiel.odtwarzacz_pilot.connection.Connection;

public class MainActivity extends AppCompatActivity {

    public static final String[] SUPPORTED_AUDIO = {"MP3"};//{"*.AIFF", "*.MP3", "*.WAV"};
    public static final String[] SUPPORTED_VIDEO = {"MP4"};//{"*.FLV", "*.MP4"};

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private MyViewPager mViewPager;
    private static PilotView pilotView;
    private static PlaylistView playlistView;

    private static MainActivity instance;

    public static MainActivity getInstance() {
        return instance;
    }

    public static PilotView getPilotView() {
        return pilotView;
    }

    public static PlaylistView getPlaylistView() {
        return playlistView;
    }

    private static Playlist playlist;

    public static Playlist getPlaylist() {
        return playlist;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        instance = this;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(2);


        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new PageListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        pilotView = new PilotView();
        playlistView = new PlaylistView();

        playlist = new Playlist();
    }

    public MyViewPager getmViewPager() {
        return mViewPager;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.recent:
                final ConstraintLayout recentWindow = pilotView.getView().findViewById(R.id.recent_files_window);
                recentWindow.setVisibility(View.VISIBLE);
                mViewPager.setCurrentItem(0);
                return true;

            case R.id.queue:
                playlist.getQueue().showDialog();
                return true;

            case R.id.disconnect:
                Connection.disconnect();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }



        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            int currentTab = getArguments().getInt(ARG_SECTION_NUMBER);
            System.out.println(currentTab);
            View rootView = null;
            switch (currentTab) {
                case 1:
                    rootView = pilotView.makeView(inflater, container);
//                    rootView = inflater.inflate(R.layout.view_pilot, container, false);
                    break;

                case 2:
                    rootView = playlistView.makeView(inflater, container);
                    break;
            }
            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }


        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }
    }
}
