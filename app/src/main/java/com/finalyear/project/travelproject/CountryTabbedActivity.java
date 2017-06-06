package com.finalyear.project.travelproject;

import android.support.design.widget.TabLayout;
import android.support.v7.widget.Toolbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class CountryTabbedActivity extends BaseActivity {


    private SectionsPagerAdapter mSectionsPagerAdapter;
    private static String countryName;
    private static final String TAG = "test";
    private ViewPager mViewPager; //The {@link ViewPager} that will host the section contents.
    private static String aboutText;
    private static String seeAndDoText;
    private static String safetyText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_country_tabbed);
        //get information passed from previous intent
        countryName = getIntent().getStringExtra("countryName").toLowerCase();
        Log.d(TAG, countryName);
        //add toolbar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setTitle(countryName.toUpperCase());
        setSupportActionBar(myToolbar);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        //reference text files relating to country stored in /assets/files
        aboutText = readTextFile("files/" + countryName + "_about.txt");
        seeAndDoText = readTextFile("files/" + countryName + "_things_to_do.txt");
        safetyText = readTextFile("files/" + countryName + "_safety.txt");
    }


    public String readTextFile(String fileName) {
        //new stringbuilder instance
        StringBuilder builder = new StringBuilder();
        BufferedReader buffer = null; //is null because might not get initialized
        try {
            //set the input stream to the filename passed
            buffer = new BufferedReader(new InputStreamReader(getAssets().open(fileName)));
            String line;
            //while there are lines left in the file
            while ((line = buffer.readLine()) != null) {
                //add the line and append a new line escape character
                builder.append(line).append("\n");
            }
            //return the string value of the StringBuilder object
            return builder.toString();
            //exception handling
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                buffer.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //return null if exception occurs
        return null;
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
            View rootView = inflater.inflate(R.layout.fragment_country_tabbed, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);

            if (getArguments().getInt(ARG_SECTION_NUMBER) == 1) {
                textView.setText(aboutText);
            } else if (getArguments().getInt(ARG_SECTION_NUMBER) == 2) {
                textView.setText(seeAndDoText);
            }
            if (getArguments().getInt(ARG_SECTION_NUMBER) == 3) {
                textView.setText(safetyText);
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
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "ABOUT";
                case 1:
                    return "SEE & DO";
                case 2:
                    return "SAFETY";
            }
            return null;
        }
    }
}
