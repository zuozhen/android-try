package com.zzdev.nicemove;

/**
 * Created by zz on 2/23/16.
 */

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

/**
 * A placeholder fragment containing a simple view.
 */
public class FragmentTwo extends Fragment implements View.OnClickListener {

    private Button btOh;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_two, container, false);

        btOh = (Button) rootView.findViewById(R.id.bt_oh);
        btOh.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(2); //最简单的 .onSectionAttached(1);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_oh:
                Toast.makeText(getActivity(), "Yeah...", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
