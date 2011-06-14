package com.smartpoint.android.planner.operations;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.ContextMenu;
import android.view.Menu;
import com.smartpoint.android.planner.R;
import com.smartpoint.android.planner.utils.MimeTypes;

import java.io.File;
import java.io.IOException;

/**
 * Revision Info : $Author$ $Date$
 * Author  : dng
 * Created : 6/10/11 2:58 PM
 *
 * @author dng
 */
public class BaseFileOperationImpl implements BaseFileOperations {

    public int remove(File fileToRemove) throws IOException {
        return fileToRemove.delete()
                ? OPERATION_OK
                : OPERATION_FAILED;
    }

    public int move(File fileToMove, String pathTo) throws IOException {
        return fileToMove.renameTo(new File(pathTo, fileToMove.getAbsolutePath()))
                ? OPERATION_OK
                : OPERATION_FAILED;
    }

    public void getMenuItems(ContextMenu menu, File selectedFile) {
        menu.add(0, BaseFileOperations.OPERATION_REMOVE,
                Menu.NONE, R.string.action_remove);
        menu.add(0, BaseFileOperations.OPERATION_MOVE,
                Menu.NONE, R.string.action_move);

    }
}
