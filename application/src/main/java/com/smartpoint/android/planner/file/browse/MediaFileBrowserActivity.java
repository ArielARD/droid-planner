package com.smartpoint.android.planner.file.browse;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.*;
import android.widget.AdapterView;
import android.widget.Toast;
import com.smartpoint.android.planner.R;
import com.smartpoint.android.planner.operations.BaseFileOperationImpl;
import com.smartpoint.android.planner.operations.BaseFileOperations;
import com.smartpoint.android.planner.preferences.MainPreferencesActivity;
import com.smartpoint.android.planner.utils.MimeTypes;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Revision Info : $Author$ $Date$
 * Author  : dng
 * Created : 6/7/11 11:15 AM
 *
 * @author dng
 */
public final class MediaFileBrowserActivity extends AbstractFileBrowser {
    private BaseFileOperations commonFileOperations;
    private MimeTypes mimeTypes;

    public static final int OPERATION_OPEN = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getListView().setOnCreateContextMenuListener(this);
        commonFileOperations = new BaseFileOperationImpl();
        mimeTypes = new MimeTypes(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_exit:
                new AlertDialog.Builder(this)
                        .setMessage(R.string.message_exit)
                        .setTitle(R.string.title_exit)
                        .setPositiveButton(R.string.value_yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
                            }
                        })
                        .setNegativeButton(R.string.value_no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }).show();
                break;
            case R.id.menu_prefs:
                startActivity(new Intent(this, MainPreferencesActivity.class));
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        AdapterView.AdapterContextMenuInfo info;
        try {
            info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        } catch (ClassCastException e) {
            return;
        }
        if (info == null)
            return;
        final File selectedFile = fileDataProvider.get(info.position);
        if (selectedFile == null)
            return;
        menu.setHeaderTitle(R.string.actions_title);
        menu.add(0, OPERATION_OPEN, Menu.NONE, R.string.action_open);
        commonFileOperations.getMenuItems(menu, selectedFile);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info;
        try {
            info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        } catch (ClassCastException e) {
            return false;
        }
        if (info == null)
            return false;
        final File selectedFile = fileDataProvider.get(info.position);
        if (selectedFile == null)
            return false;
        switch (item.getItemId()) {
            case OPERATION_OPEN:
                try {
                    if (selectedFile.isDirectory() || fileDataProvider.isSymlink(selectedFile)) {
                        fileDataProvider.browseTo(selectedFile);
                    } else {
                        openFile(selectedFile);
                    }
                } catch (IOException e) {
                    return false;
                }
                return true;
            case BaseFileOperations.OPERATION_REMOVE:
                new AlertDialog.Builder(this)
                        .setMessage(getString(R.string.question_remove, selectedFile.getName()))
                        .setCancelable(true)
                        .setPositiveButton(R.string.value_yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogInterface, int i) {
                                int res = 0;
                                try {
                                    res = commonFileOperations.remove(selectedFile);
                                } catch (FileNotFoundException e) {
                                    MediaFileBrowserActivity
                                            .this.errorDeleteHandler(
                                            getString(R.string.error_not_found), selectedFile);
                                } catch (IOException e) {
                                    MediaFileBrowserActivity
                                            .this.errorDeleteHandler(selectedFile);
                                } finally {
                                    if (res == BaseFileOperations.OPERATION_OK) {
                                        fileDataProvider.browseTo(fileDataProvider.getCurrentDirectory());
                                    } else {
                                        MediaFileBrowserActivity
                                                .this.errorDeleteHandler(selectedFile);
                                    }
                                }
                            }
                        })
                        .setNegativeButton(R.string.value_no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .setOnCancelListener(new DialogInterface.OnCancelListener() {
                            public void onCancel(DialogInterface dialogInterface) {
                                dialogInterface.dismiss();
                            }
                        })
                        .create().show();
                return true;
            default:
                Toast.makeText(this,
                        getString(R.string.not_implemented,
                                item.getTitle()),
                        Toast.LENGTH_SHORT).show();
        }
        return super.onContextItemSelected(item);
    }

    private void errorDeleteHandler(File selectedFile) {
        Toast.makeText(this,
                getString(R.string.error_remove, selectedFile.getName()),
                Toast.LENGTH_LONG
        ).show();
    }

    private void errorDeleteHandler(String message, File selectedFile) {
        Toast.makeText(this, String.format(message, selectedFile.getName()),
                Toast.LENGTH_LONG
        ).show();
    }

    @Override
    public void openFile(File selectedFile) throws ActivityNotFoundException {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        String fileExt = mimeTypes.getFileExtension(selectedFile);
        if (fileExt != null) {
            Uri uri = Uri.fromFile(selectedFile);
            String mime = mimeTypes.getMimeByExtension(this, fileExt);
            try {
                if (mime != null) {
                    intent.setDataAndType(uri, mime);
                } else {
                    intent.setDataAndType(uri, getString(R.string.default_mime_type));
                }
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(this,
                        getString(R.string.activity_not_found, fileExt),
                        Toast.LENGTH_LONG).show();
            }
        }
    }
}
