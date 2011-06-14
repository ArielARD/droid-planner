package com.smartpoint.android.planner.operations;

import java.io.File;

/**
 * Revision Info : $Author$ $Date$
 * Author  : dng
 * Created : 6/10/11 1:34 PM
 *
 * @author dng
 */
public interface ClipboardOperations {

    public int copy(File fileToCopy);

    public int paste(File fileToPaste);

}
