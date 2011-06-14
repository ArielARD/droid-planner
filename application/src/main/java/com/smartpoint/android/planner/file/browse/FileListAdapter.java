package com.smartpoint.android.planner.file.browse;

import android.content.Context;
import android.os.Handler;
import android.widget.ArrayAdapter;

import java.io.File;
import java.util.List;

/**
 * Revision Info : $Author$ $Date$
 * Author  : dng
 * Created : 6/7/11 5:50 PM
 *
 * @author dng
 */
public abstract class FileListAdapter extends ArrayAdapter<File> {
    private List<File> fileList;

    FileListAdapter(Context context, int textViewResourceId, List<File> objects) {
        super(context, textViewResourceId, objects);
        this.fileList = objects;
    }

    final Handler mHandler = new Handler();

    void setDataFromAnyThread(final List<File> newData) {
        mHandler.post(new Runnable() {
            public void run() {
                fileList = newData;
                notifyDataSetChanged();
            }
        });
    }

    public List<File> getFileList() {
        return fileList;
    }
}
