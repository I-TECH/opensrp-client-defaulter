package org.smartregister.kdp.activity;

import android.os.Bundle;

import com.vijay.jsonwizard.activities.JsonWizardFormActivity;

import org.smartregister.kdp.R;


public class EditJsonFormActivity extends JsonWizardFormActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setConfirmCloseMessage(getString(R.string.any_changes_you_make));
        super.onCreate(savedInstanceState);
    }
}
