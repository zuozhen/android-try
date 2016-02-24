package com.zzdev.nicemove;

/**
 * Created by zz on 2/23/16.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import net.rdrei.android.dirchooser.DirectoryChooserActivity;
import net.rdrei.android.dirchooser.DirectoryChooserConfig;

import java.io.File;

/**
 * A placeholder fragment containing a simple view.
 */
public class FragmentOne extends Fragment implements View.OnClickListener {

    private static EditText etSrcFolder, etDstFolder;
    private Button btBrowseSrc, btBrowseDst, btDelete, btCopy;
    private static SharedPreferences sharedPref;

    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final int REQUEST_SRC_DIRECTORY = 0;
    private static final int REQUEST_DST_DIRECTORY = 1;
    private static final String TAG = "DirChooserSample";

    /**
     * From the Fragment documentation: Every fragment must have an empty constructor,
     * so it can be instantiated when restoring its activity's state.
     * It is strongly recommended that subclasses do not have other constructors with parameters,
     * since these constructors will not be called when the fragment is re-instantiated;
     * instead, arguments can be supplied by the caller with setArguments(Bundle)
     * and later retrieved by the Fragment with getArguments().
     *
     * ZZ: 所以没有重载构造函数 new FragmentOne(int sectionNumber), 而是新建一个函数来完成，并用setArguments来传递sectionNumber
     * 或者更简单的，直接使用new Fragment(), sectionNumber用hardcode（不同的fragment用不同的sectionNumber)
     */
    public static FragmentOne newInstance(int sectionNumber) {
        FragmentOne fragment = new FragmentOne();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public FragmentOne() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_one, container, false);

        etSrcFolder = (EditText) rootView.findViewById(R.id.editText);
        etDstFolder = (EditText) rootView.findViewById(R.id.editText2);
        sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        String srcFolder = sharedPref.getString("srcFolder", getString(R.string.folder_hint));
        String dstFolder = sharedPref.getString("dstFolder", getString(R.string.folder_hint));
        etSrcFolder.setText(srcFolder);
        etDstFolder.setText(dstFolder);

        btBrowseSrc = (Button) rootView.findViewById(R.id.button_browse_src);
        btBrowseSrc.setOnClickListener(this);
        btBrowseDst = (Button) rootView.findViewById(R.id.button_browse_dst);
        btBrowseDst.setOnClickListener(this);
        btDelete = (Button) rootView.findViewById(R.id.button_delete);
        btDelete.setOnClickListener(this);
        btCopy = (Button) rootView.findViewById(R.id.button_copy);
        btCopy.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER)); //最简单的 .onSectionAttached(1);
    }

    @Override
    public void onStop() {
        super.onStop();

        SharedPreferences.Editor ed = sharedPref.edit();
        ed.putString("srcFolder", etSrcFolder.getText().toString());
        ed.putString("dstFolder", etDstFolder.getText().toString());
        ed.apply();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_browse_src:
                chooseDirectory(v);
                break;
            case R.id.button_browse_dst:
                chooseDirectory(v);
                break;
            case R.id.button_delete:
                deleteContent(v);
                break;
            case R.id.button_copy:
                copyContent(v);
                break;
        }
    }

    public void chooseDirectory(View view) {
        final Intent chooserIntent = new Intent(
                getContext(),
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
            case (R.id.button_browse_src):
                startActivityForResult(chooserIntent, REQUEST_SRC_DIRECTORY);
                break;
            case (R.id.button_browse_dst):
                startActivityForResult(chooserIntent, REQUEST_DST_DIRECTORY);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
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
            Toast.makeText(getActivity(), getString(R.string.result_ok), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(getContext(), getString(R.string.result_er), Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteContent(File fileOrDir) {
        if (fileOrDir.isDirectory())
            for (File f : fileOrDir.listFiles()) {
                if (!f.delete())
                    Log.i("ZZ", String.format("Delete file: %s", f.getName()));
            }
        if (!fileOrDir.delete())
            Log.i("ZZ", String.format("Delete dir: %s", fileOrDir.getName()));
    }

    public void copyContent(View view) {
        new AsyncTaskCopy(getActivity()).execute(etSrcFolder.getText().toString(), etDstFolder.getText().toString());
    }
}
