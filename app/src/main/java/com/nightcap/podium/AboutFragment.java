package com.nightcap.podium;

import android.app.Activity;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Fragment used to display information about the app.
 * 
 * @author Paul
 *
 */
public class AboutFragment extends Fragment {
	/**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static AboutFragment newInstance(int sectionNumber) {
        AboutFragment fragment = new AboutFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public static AboutFragment newInstance() {
        AboutFragment fragment = new AboutFragment();
        return fragment;
    }

    public AboutFragment() {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
//        ((MainActivity) activity).onSectionAttached(
//                getArguments().getInt(ARG_SECTION_NUMBER));

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_about, container, false);
        setHasOptionsMenu(false);

        // Version number
        TextView versionView = (TextView) rootView.findViewById(R.id.about_version);
        String versionNumber = "<Unknown>";

        try {
			versionNumber = getActivity().getApplicationContext().getPackageManager()
				.getPackageInfo(getActivity().getApplicationContext().getPackageName(), 0)
				.versionName;
		} catch (NameNotFoundException e) {
		    e.printStackTrace();
		}
		String versionText = getString(R.string.about_version)+ " " + versionNumber;
		versionView.setText(versionText);

        // Build date
        TextView buildDateView = (TextView) rootView.findViewById(R.id.about_build_date);
        String buildDateText = getString(R.string.about_build_date) + ": "
                + getString(R.string.build_date);
        buildDateView.setText(buildDateText);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        // List view
        String[] stringArray = getResources().getStringArray(R.array.media_names);
        ArrayAdapter adapter = new ArrayAdapter<>(getContext(), R.layout.about_list_item, stringArray);
        ListView listView = (ListView) getActivity().findViewById(R.id.about_list);
        listView.setAdapter(adapter);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
//        inflater.inflate(R.menu.menu_about, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

//        if (id == R.id.action_licences) {
//            Toast.makeText(getActivity().getApplicationContext(), "Show open source licences", Toast.LENGTH_SHORT).show();
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }
}
