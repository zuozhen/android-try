package com.gusturelock;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.widget.Toast;

import com.gusturelock.LockPatternView.Cell;
import com.gusturelock.LockPatternView.DisplayMode;
import com.zzdev.nicemove.MainActivity;
import com.zzdev.nicemove.R;

import java.util.List;

/*
 * Author: Ruils 心怀产品梦的安卓码农 
 * Blog: http://blog.csdn.net/ruils
 * QQ: 5452781
 * Email: 5452781@qq.com
 */

public class LockActivity extends Activity implements
        LockPatternView.OnPatternListener {
    private static final String TAG = "LockActivity";

    private List<Cell> lockPattern;
    private LockPatternView lockPatternView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences preferences = getSharedPreferences(MainActivity.LOCK, MODE_PRIVATE);
        String patternString = preferences.getString(MainActivity.LOCK_KEY,
                null);
        if (patternString == null) {
            finish();
            return;
        }
        lockPattern = LockPatternView.stringToPattern(patternString);
        setContentView(R.layout.activity_lock);
        lockPatternView = (LockPatternView) findViewById(R.id.lock_pattern);
        lockPatternView.setOnPatternListener(this);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        // disable back key
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onPatternStart() {
//        Log.d(TAG, "onPatternStart");
    }

    @Override
    public void onPatternCleared() {
//        Log.d(TAG, "onPatternCleared");
    }

    @Override
    public void onPatternCellAdded(List<Cell> pattern) {
//        Log.d(TAG, "onPatternCellAdded");
//        Log.e(TAG, LockPatternView.patternToString(pattern));
        // Toast.makeText(this, LockPatternView.patternToString(pattern),
        // Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPatternDetected(List<Cell> pattern) {
        Log.d(TAG, "onPatternDetected");

        if (pattern.equals(lockPattern)) {
            removePassword();
            finish();
        } else {
            lockPatternView.setDisplayMode(DisplayMode.Wrong);
            Toast.makeText(this, R.string.lockpattern_error, Toast.LENGTH_LONG)
                    .show();
        }

    }

    private void removePassword() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            boolean removePassword = extras.getBoolean(MainActivity.REMOVE_PASSWORD);
            SharedPreferences sharedPref = getSharedPreferences(MainActivity.LOCK, Context.MODE_PRIVATE);
            if (removePassword) {
                sharedPref.edit().remove(MainActivity.LOCK_KEY).apply();
            }
        }
    }

}
