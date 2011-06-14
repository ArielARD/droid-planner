package com.smartpoint.android.planner.file.actions.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;
import com.smartpoint.android.planner.R;

import java.io.*;

/**
 * Revision Info : $Author$ $Date$
 * Author  : dng
 * Created : 6/9/11 5:33 PM
 *
 * @author dng
 */
public class TextViewActivity extends Activity {

    public static final int VIEW_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.text_view);
        TextView textView = (TextView) findViewById(R.id.textViewContent);
        Intent requestedIntent = getIntent();
        String selectedFilePath = requestedIntent.getStringExtra("filePath");
        if (selectedFilePath != null && !TextUtils.isEmpty(selectedFilePath)) {
            try {
                textView.setText(readFromFile(selectedFilePath));
            } catch (IOException e) {
                setResult(RESULT_CANCELED);
            }
        }
        setResult(RESULT_OK);
    }

    private String readFromFile(String file) throws IOException {
        String thisLine;
        String result = "";
        FileInputStream fin = new FileInputStream(file);
        BufferedReader myInput = new BufferedReader
                (new InputStreamReader(fin));
        while ((thisLine = myInput.readLine()) != null) {
            result += thisLine;
        }
        return result;
    }
}
