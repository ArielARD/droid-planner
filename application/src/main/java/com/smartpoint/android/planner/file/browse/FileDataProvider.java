package com.smartpoint.android.planner.file.browse;

import android.app.Activity;
import android.app.ProgressDialog;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.smartpoint.android.planner.R;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Revision Info : $Author$ $Date$
 * Author  : dng
 * Created : 6/7/11 5:24 PM
 *
 * @author dng
 */
public class FileDataProvider {
    public static final File rootDirectory = new File("/");
    public static final File topDirectory = new File(".");
    public static final File upperDirectory = new File("..");
    private File currentDirectory = rootDirectory;

    private final Activity context;
    private FileListAdapter fileListAdapter;
    private List<File> fileList = new ArrayList<File>();

    private ProgressDialog progressDialog;

    public FileDataProvider(final Activity context) {
        this.context = context;
        fileListAdapter = new FileListAdapter(context, R.id.fileListRow, fileList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                File selectedFile = getFileList().get(position);
                View row;
                if (convertView == null) {
                    row = context.getLayoutInflater()
                            .inflate(R.layout.file_row, parent, false);
                } else {
                    row = convertView;
                }
                TextView tv = (TextView) row.findViewById(R.id.fileListRow);
                TextView tvStat = (TextView) row.findViewById(R.id.fileStatText);
                ImageView iv = (ImageView) row.findViewById(R.id.fileIcon);
                tv.setText(selectedFile.getAbsolutePath());
                try {
                    if (!selectedFile.isDirectory() && !isSymlink(selectedFile)) {
                        tvStat.setText(humanReadableByteCount(
                                selectedFile.length(), false));
                        tvStat.setVisibility(View.VISIBLE);
                    } else {
                        tvStat.setVisibility(View.GONE);
                    }
                    iv.setImageResource(getIconForFile(selectedFile));
                } catch (IOException e) {
                    iv.setImageResource(R.drawable.file_icon);
                }
                return row;
            }
        };

        fileListAdapter.setNotifyOnChange(true);
    }

    public String humanReadableByteCount(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit)
            return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

    public boolean isSymlink(File file) throws IOException {
        if (file == null)
            throw new NullPointerException("File must not be null");
        File canon;
        if (file.getParent() == null) {
            canon = file;
        } else {
            File canonDir = file.getParentFile().getCanonicalFile();
            canon = new File(canonDir, file.getName());
        }
        return !canon.getCanonicalFile().equals(canon.getAbsoluteFile());
    }

    protected int getIconForFile(File selectedFile) throws IOException {
        if (selectedFile.isDirectory()) {
            return R.drawable.folder_icon;
        } else if (isSymlink(selectedFile)) {
            return R.drawable.symlink_icon;
        } else {
            return R.drawable.file_icon;
        }
    }

    public void browseToRoot() {
        browseTo(currentDirectory);
    }

    public synchronized void browseTo(final File fileDirectory) {
        if (fileDirectory.isDirectory()) {
            currentDirectory = fileDirectory;
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            progressDialog = ProgressDialog.show(context,
                    context.getString(R.string.working_progress_label),
                    context.getString(R.string.check_files_label, currentDirectory.getAbsolutePath()),
                    true, false);
            new Thread() {
                public void run() {
                    FileDataProvider.this
                            .threadFunc(fileDirectory);
                }
            }.start();
        }
    }

    private void threadFunc(final File fileDirectory) {
        if (fileDirectory == null)
            return;
        List<File> output = new ArrayList<File>();
        currentDirectory = fileDirectory;
        if (currentDirectory.getParentFile() != null) {
            output.add(topDirectory);
            output.add(upperDirectory);
        }
        File[] privFileList = currentDirectory.listFiles(new FilenameFilter() {
            public boolean accept(File file, String s) {
                boolean isAcceptable;
                File objectPath = new File(file, s);
                try {
                    isAcceptable = objectPath.isDirectory() || isSymlink(objectPath);
                } catch (IOException e) {
                    isAcceptable = false;
                }
                return isAcceptable;
            }
        });
        if (privFileList != null) {
            List<File> dirList = Arrays.asList(privFileList);
            Collections.sort(dirList);
            output.addAll(dirList);
        }
        // populate with files
        File[] privDirList = currentDirectory.listFiles(new FilenameFilter() {
            public boolean accept(File file, String s) {
                boolean isAcceptable;
                File objectPath = new File(file, s);
                try {
                    isAcceptable = !objectPath.isDirectory() && !isSymlink(objectPath);
                } catch (IOException e) {
                    isAcceptable = false;
                }
                return isAcceptable;
            }
        });
        if (privDirList != null) {
            List<File> filesList = Arrays.asList(privDirList);
            Collections.sort(filesList);
            output.addAll(filesList);
        }
        fileList.clear();
        fileList.addAll(output);
        output.clear();
        fileListAdapter.setDataFromAnyThread(fileList);
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    public File getCurrentDirectory() {
        return currentDirectory;
    }

    public File get(int position) {
        return fileList.get(position);
    }

    public FileListAdapter getFileListAdapter() {
        return fileListAdapter;
    }
}
