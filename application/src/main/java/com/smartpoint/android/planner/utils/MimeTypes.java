package com.smartpoint.android.planner.utils;

import android.content.Context;
import com.smartpoint.android.planner.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;

/**
 * Revision Info : $Author$ $Date$
 * Author  : dng
 * Created : 6/10/11 12:46 PM
 *
 * @author dng
 */
public final class MimeTypes {
    private Map<String, String> extensionToMime = new HashMap<String, String>();

    public MimeTypes(Context context) {
        initMap(context);
    }

    private void initMap(Context context) {
        extensionToMime = new HashMap<String, String>();
        try {
            InputStream is = new GZIPInputStream(
                    context.getResources().openRawResource(R.raw.mimes)
            );
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            byte[] buf = new byte[16384];
            while (true) {
                int r = is.read(buf);
                if (r <= 0) break;
                os.write(buf, 0, r);
            }
            String[] lines = os.toString().split("\n");
            String mime = null;
            for (String val : lines) {
                if (val.length() == 0) mime = null;
                else if (mime == null) mime = val;
                else extensionToMime.put(val, mime);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to open mime db", e);
        }
    }

    public String getMimeByExtension(Context context, String extension) {
        if (extensionToMime == null) {
            initMap(context);
        }
        return extensionToMime.get(extension);
    }

    public String getFileExtension(File selectedFile) {
        String extension = null;
        int dot = selectedFile.getName().lastIndexOf(".");
        if (dot != -1) {
            extension = selectedFile.getName()
                    .substring(dot + 1).toLowerCase();
        }
        return extension;
    }
}
