package com.zzdev.nicemove;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by zz on 2/22/16.
 */
public class AsyncTaskCopy extends AsyncTask<String, Integer, Boolean> {

    /** progress dialog to show user that the background job is processing. */
    private ProgressDialog dialog;
    /** application context. */
    private Activity activity;

    public AsyncTaskCopy(Activity activity) {
        this.activity = activity;
        dialog = new ProgressDialog(activity);
    }

    protected void onPreExecute() {
        this.dialog.setMessage(this.activity.getString(R.string.progress_title));
        this.dialog.show();
    }

    protected Boolean doInBackground(String... args) {
        File srcFolder = new File(args[0]);
        File dstFolder = new File(args[1]);
        try {
            copyF(srcFolder, dstFolder);
            return true;
        } catch (Exception e) {
            return true;
        }
    }

    private void copyF(File srcF, File dstF) throws Exception {
        if (srcF.isDirectory()) {
            if (!dstF.exists())
                dstF.mkdir();
            String[] children = srcF.list();
            for (int i = 0; i < children.length; i++) {
                copyF(new File(srcF, children[i]), new File(dstF, children[i]));
                publishProgress((int) ((i / (float) children.length) * 100));
            }
        } else {
            InputStream in = new FileInputStream(srcF);
            OutputStream out = new FileOutputStream(dstF);

            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
        }
    }

    protected void onPostExecute(Boolean success) {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
        if (success)
            Toast.makeText(this.activity, this.activity.getString(R.string.result_ok), Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this.activity, this.activity.getString(R.string.result_ok), Toast.LENGTH_SHORT).show();
    }
}
