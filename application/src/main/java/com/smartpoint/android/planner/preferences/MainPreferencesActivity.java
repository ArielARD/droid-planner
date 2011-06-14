package com.smartpoint.android.planner.preferences;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import com.smartpoint.android.planner.R;

/**
 * Revision Info : $Author$ $Date$
 * Author  : dng
 * Created : 6/8/11 2:33 PM
 *
 * @author dng
 */
public class MainPreferencesActivity extends PreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}
