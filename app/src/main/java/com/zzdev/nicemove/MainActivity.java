package com.zzdev.nicemove;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import net.rdrei.android.dirchooser.DirectoryChooserActivity;
import net.rdrei.android.dirchooser.DirectoryChooserConfig;

import java.io.File;


public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    private static final int REQUEST_SRC_DIRECTORY = 0;
    private static final int REQUEST_DST_DIRECTORY = 1;
    private static final String TAG = "DirChooserSample";
    private static EditText etSrcFolder;
    private static EditText etDstFolder;
    private static SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
//            getMenuInflater().inflate(R.menu.main, menu);
            getMenuInflater().inflate(R.menu.global, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void chooseDirectory(View view) {
        final Intent chooserIntent = new Intent(
                MainActivity.this,
                DirectoryChooserActivity.class);

        final DirectoryChooserConfig config = DirectoryChooserConfig.builder()
                .newDirectoryName("DirChooserSample")
                .allowReadOnlyDirectory(true)
                .allowNewDirectoryNameModification(true)
                .build();

        chooserIntent.putExtra(
                DirectoryChooserActivity.EXTRA_CONFIG,
                config);
        switch (view.getId()) {
            case (R.id.button):
                startActivityForResult(chooserIntent, REQUEST_SRC_DIRECTORY);
                break;
            case (R.id.button2):
                startActivityForResult(chooserIntent, REQUEST_DST_DIRECTORY);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_SRC_DIRECTORY) {
            Log.i(TAG, String.format("Return from DirChooser with result %d",
                    resultCode));

            if (resultCode == DirectoryChooserActivity.RESULT_CODE_DIR_SELECTED) {
                etSrcFolder
                        .setText(data
                                .getStringExtra(DirectoryChooserActivity.RESULT_SELECTED_DIR));
            }
        } else if (requestCode == REQUEST_DST_DIRECTORY) {
            if (resultCode == DirectoryChooserActivity.RESULT_CODE_DIR_SELECTED) {
                etDstFolder
                        .setText(data
                                .getStringExtra(DirectoryChooserActivity.RESULT_SELECTED_DIR));
            }
        }
    }

    public void deleteContent(View view) {
        File dstFolder = new File(etDstFolder.getText().toString());

        try {
            for (File f : dstFolder.listFiles()) {
//            Log.i("ZZ", f.getAbsolutePath());
                deleteContent(f);
            }
            Toast.makeText(MainActivity.this, getString(R.string.result_ok), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(MainActivity.this, getString(R.string.result_er), Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteContent(File fileOrDir) {
        if (fileOrDir.isDirectory())
            for (File f : fileOrDir.listFiles())
                f.delete();
        fileOrDir.delete();
    }

    public void copyContent(View view) {
        new AsyncTaskCopy(this).execute(etSrcFolder.getText().toString(), etDstFolder.getText().toString());
    }

    @Override
    protected void onStop() {
        super.onStop();

        SharedPreferences.Editor ed = sharedPref.edit();
        ed.putString("srcFolder", etSrcFolder.getText().toString());
        ed.putString("dstFolder", etDstFolder.getText().toString());
        ed.apply();
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

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);

            etSrcFolder = (EditText) rootView.findViewById(R.id.editText);
            etDstFolder = (EditText) rootView.findViewById(R.id.editText2);
            sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
            String srcFolder = sharedPref.getString("srcFolder", getString(R.string.folder_hint));
            String dstFolder = sharedPref.getString("dstFolder", getString(R.string.folder_hint));
            etSrcFolder.setText(srcFolder);
            etDstFolder.setText(dstFolder);

            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

}
