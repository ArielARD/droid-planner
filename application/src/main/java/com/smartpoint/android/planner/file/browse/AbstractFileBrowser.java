package com.smartpoint.android.planner.file.browse;

import android.app.ListActivity;
import android.content.ActivityNotFoundException;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;
import com.smartpoint.android.planner.R;

import java.io.File;
import java.io.IOException;

/**
 * Revision Info : $Author$ $Date$
 * Author  : dng
 * Created : 6/7/11 10:34 AM
 *
 * @author dng
 */
public abstract class AbstractFileBrowser extends ListActivity {

    protected FileDataProvider fileDataProvider;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fileDataProvider = new FileDataProvider(this);
        setListAdapter(fileDataProvider.getFileListAdapter());
        fileDataProvider.browseToRoot();
    }


    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        File selectedFile = fileDataProvider.get(position);
        if (selectedFile == null)
            return;
        try {
            if (selectedFile.equals(FileDataProvider.topDirectory)) {
                fileDataProvider.browseTo(fileDataProvider.getCurrentDirectory());
            } else if (selectedFile.equals(FileDataProvider.upperDirectory)) {
                if (fileDataProvider.getCurrentDirectory().getParentFile() != null) {
                    fileDataProvider.browseTo(fileDataProvider.getCurrentDirectory().getParentFile());
                }
            } else if (selectedFile.isDirectory() || fileDataProvider.isSymlink(selectedFile)) {
                if (fileDataProvider.isSymlink(selectedFile)) {
                    fileDataProvider.browseTo(selectedFile.getCanonicalFile());
                } else {
                    fileDataProvider.browseTo(selectedFile);
                }
            } else {
                openFile(selectedFile);
            }
        } catch (IOException e) {
            showException(R.string.error_open_file,
                    selectedFile.getAbsolutePath());
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (fileDataProvider.getCurrentDirectory().getParentFile() != null) {
                fileDataProvider.browseTo(fileDataProvider.getCurrentDirectory().getParentFile());
            } else {
                openOptionsMenu();
            }
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    private void showException(int stringId, Object... vars) {
        showException(getString(stringId, vars));
    }

    private void showException(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        getListView().setVisibility(View.GONE);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        getListView().setVisibility(View.VISIBLE);
    }

    protected void openFile(File selectedFile) throws ActivityNotFoundException {}
}
