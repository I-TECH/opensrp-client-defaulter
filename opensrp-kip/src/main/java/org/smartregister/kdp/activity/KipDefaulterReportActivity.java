package org.smartregister.kdp.activity;

import android.content.Intent;

import androidx.annotation.NonNull;

import org.json.JSONObject;
import org.smartregister.AllConstants;
import org.smartregister.kdp.R;
import org.smartregister.opd.activity.BaseOpdRegisterActivity;
import org.smartregister.opd.contract.OpdRegisterActivityContract;
import org.smartregister.opd.fragment.BaseOpdRegisterFragment;
import org.smartregister.opd.presenter.BaseOpdRegisterActivityPresenter;
import org.smartregister.opd.utils.OpdUtils;
import org.smartregister.view.fragment.BaseRegisterFragment;

import java.util.Map;

public class KipDefaulterReportActivity extends BaseOpdRegisterActivity {


    @Override
    protected BaseOpdRegisterActivityPresenter createPresenter(@NonNull OpdRegisterActivityContract.View view, @NonNull OpdRegisterActivityContract.Model model) {
        return null;
    }

    @Override
    public void startFormActivity(String formName, String entityId, String metaData) {
        if (mBaseFragment instanceof BaseOpdRegisterFragment) {
            String locationId = OpdUtils.context().allSharedPreferences().getPreference(AllConstants.CURRENT_LOCATION_ID);
            presenter().startForm(formName, entityId, metaData, locationId, null, null);
        } else {
            displayToast(getString(R.string.error_unable_to_start_form));
        }
    }

    @Override
    protected BaseRegisterFragment getRegisterFragment() {
        return null;
    }

    @Override
    public void startFormActivity(String s, String s1, Map<String, String> map) {

    }

    @Override
    public void startFormActivity(JSONObject jsonObject) {

    }

    @Override
    protected void onActivityResultExtended(int i, int i1, Intent intent) {

    }
}
