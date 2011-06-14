package com.smartpoint.android.planner.operations;

import android.view.ContextMenu;

import java.io.File;
import java.io.IOException;

/**
 * Revision Info : $Author$ $Date$
 * Author  : dng
 * Created : 6/10/11 1:29 PM
 *
 * @author dng
 */
public interface BaseFileOperations {
    public static final int OPERATION_OK = 0;
    public static final int OPERATION_FAILED = -1;

    public static final int OPERATION_REMOVE = 0;
    public static final int OPERATION_MOVE = 1;

    /**
     * Remove a file from file system.
     * @param fileToRemove File to remove
     * @return operation result code
     * @throws java.io.IOException if ant exception throws
     */
    public int remove(File fileToRemove) throws IOException;

    /**
     * Move a file to another place.
     * @param fileToMove File to be moved
     * @param pathTo destination path
     * @return operation result code
     * @throws java.io.IOException if ant exception throws
     */
    public int move(File fileToMove, String pathTo) throws IOException;

    public void getMenuItems(ContextMenu menu, File selectedFile);
}
