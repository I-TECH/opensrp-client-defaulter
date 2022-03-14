package org.smartregister.kdp.activity;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.domain.Form;

import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.AllConstants;
import org.smartregister.kdp.R;
import org.smartregister.kdp.contract.NavigationMenuContract;
import org.smartregister.kdp.fragment.MeFragment;
import org.smartregister.kdp.fragment.KipOpdRegisterFragment;
import org.smartregister.kdp.presenter.KipOpdProfileActivityPresenter;
import org.smartregister.kdp.presenter.KipOpdRegisterActivityPresenter;
import org.smartregister.kdp.util.KipConstants;
import org.smartregister.kdp.view.NavDrawerActivity;
import org.smartregister.kdp.view.NavigationMenu;
import org.smartregister.opd.OpdLibrary;
import org.smartregister.opd.activity.BaseOpdRegisterActivity;
import org.smartregister.opd.contract.OpdRegisterActivityContract;
import org.smartregister.opd.fragment.BaseOpdRegisterFragment;
import org.smartregister.opd.pojo.OpdMetadata;
import org.smartregister.opd.pojo.RegisterParams;
import org.smartregister.opd.presenter.BaseOpdRegisterActivityPresenter;
import org.smartregister.opd.presenter.OpdProfileActivityPresenter;
import org.smartregister.opd.utils.OpdConstants;
import org.smartregister.opd.utils.OpdJsonFormUtils;
import org.smartregister.opd.utils.OpdUtils;
import org.smartregister.view.fragment.BaseRegisterFragment;

import java.util.HashMap;
import java.util.Map;

import timber.log.Timber;


public class KipOpdRegisterActivity extends BaseOpdRegisterActivity implements NavDrawerActivity, NavigationMenuContract {

    private NavigationMenu navigationMenu;

    @Override
    protected BaseOpdRegisterActivityPresenter createPresenter(@NonNull OpdRegisterActivityContract.View view, @NonNull OpdRegisterActivityContract.Model model) {
        return new KipOpdRegisterActivityPresenter(view, model);
    }

    @Override
    public NavigationMenu getNavigationMenu() {
        return navigationMenu;
    }

    @Override
    protected BaseRegisterFragment getRegisterFragment() {
        return new KipOpdRegisterFragment();
    }

    public void createDrawer() {
        navigationMenu = NavigationMenu.getInstance(this, null, null);
        if (navigationMenu != null) {
            navigationMenu.getNavigationAdapter().setSelectedView(KipConstants.DrawerMenu.OPD_CLIENTS);
            navigationMenu.runRegisterCount();
        }
    }

    @Override
    protected void onResumption() {
        super.onResumption();
        createDrawer();
    }

    @Override
    public void startFormActivity(String s, String s1, Map<String, String> map) {

    }


    @Override
    public void finishActivity() {
        finish();
    }

    @Override
    public void openDrawer() {
        if (navigationMenu != null) {
            navigationMenu.openDrawer();
        }
    }

    @Override
    public void closeDrawer() {
        NavigationMenu.closeDrawer();
    }

    @Override
    protected void onActivityResultExtended(int requestCode, int resultCode, Intent data) {
        if (requestCode == OpdJsonFormUtils.REQUEST_CODE_GET_JSON && resultCode == RESULT_OK) {
            try {
                String jsonString = data.getStringExtra(OpdConstants.JSON_FORM_EXTRA.JSON);
                Timber.d("JSONResult : %s", jsonString);

                JSONObject form = new JSONObject(jsonString);
                String encounterType = form.getString(OpdJsonFormUtils.ENCOUNTER_TYPE);
                if (encounterType.equals(OpdUtils.metadata().getRegisterEventType())) {
                    RegisterParams registerParam = new RegisterParams();
                    registerParam.setEditMode(false);
                    registerParam.setFormTag(OpdJsonFormUtils.formTag(OpdUtils.context().allSharedPreferences()));
                    showProgressDialog(R.string.saving_dialog_title);
                    presenter().saveForm(jsonString, registerParam);
                } else if (encounterType.equals(OpdConstants.EventType.CHECK_IN)) {
                    showProgressDialog(R.string.saving_dialog_title);
                    presenter().saveVisitOrDiagnosisForm(encounterType, data);
                } else if (encounterType.equals(OpdConstants.EventType.DIAGNOSIS_AND_TREAT)) {
                    showProgressDialog(R.string.saving_dialog_title);
                    presenter().saveVisitOrDiagnosisForm(encounterType, data);
                }

            } catch (JSONException e) {
                Timber.e(e);
            }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == OpdJsonFormUtils.REQUEST_CODE_GET_JSON && resultCode == RESULT_OK) {
            try {
                String jsonString = data.getStringExtra(OpdConstants.JSON_FORM_EXTRA.JSON);
                Timber.d("JSON-Result : %s", jsonString);

                JSONObject form = new JSONObject(jsonString);
                String encounterType = form.getString(OpdJsonFormUtils.ENCOUNTER_TYPE);

                switch (encounterType) {
                    case KipConstants.EventType.OPD_WEEKLY_REPORT:
                        showProgressDialog(R.string.saving_dialog_title);
                        ((KipOpdRegisterActivityPresenter) this.presenter).saveWeeklyReport(encounterType, data);
                        onResumption();
                        break;

                    case OpdConstants.EventType.OPD_CLOSE:
                        showProgressDialog(org.smartregister.opd.R.string.saving_dialog_title);
                        ((OpdProfileActivityPresenter) this.presenter).saveCloseForm(encounterType, data);
                        break;
                    default:
                        break;
                }

            } catch (JSONException e) {
                Timber.e(e);
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
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
    public void startFormActivity(String formName, String entityId, String metaData, @Nullable HashMap<String, String> injectedFieldValues, @Nullable String entityTable) {
        if (mBaseFragment instanceof BaseOpdRegisterFragment) {
            String locationId = OpdUtils.context().allSharedPreferences().getPreference(AllConstants.CURRENT_LOCATION_ID);
            presenter().startForm(formName, entityId, metaData, locationId, injectedFieldValues, entityTable);
        } else {
            displayToast(getString(R.string.error_unable_to_start_form));
        }
    }

    @Override
    public void startFormActivity(JSONObject jsonForm) {
        Intent intent = new Intent(this, OpdLibrary.getInstance().getOpdConfiguration().getOpdMetadata().getOpdFormActivity());
        intent.putExtra(OpdConstants.JSON_FORM_EXTRA.JSON, jsonForm.toString());

        Form form = new Form();
        form.setWizard(false);
        form.setHideSaveLabel(true);
        form.setNextLabel("");



        intent.putExtra(JsonFormConstants.JSON_FORM_KEY.FORM, form);
        startActivityForResult(intent, OpdJsonFormUtils.REQUEST_CODE_GET_JSON);
    }

    @Override
    public void startFormActivity(@NonNull JSONObject jsonForm, @Nullable HashMap<String, String> parcelableData) {
        OpdMetadata opdMetadata = OpdLibrary.getInstance().getOpdConfiguration().getOpdMetadata();
        if (opdMetadata != null) {
            Intent intent = new Intent(this, opdMetadata.getOpdFormActivity());
            Form form = new Form();
            form.setWizard(false);
            form.setName("");

            String encounterType = jsonForm.optString(OpdJsonFormUtils.ENCOUNTER_TYPE);

            if (encounterType.equals(OpdConstants.EventType.DIAGNOSIS_AND_TREAT)) {
                form.setName(OpdConstants.EventType.DIAGNOSIS_AND_TREAT);
                form.setWizard(true);
            }

            form.setHideSaveLabel(true);
            form.setPreviousLabel("");
            form.setNextLabel("");
            form.setHideNextButton(false);
            form.setHidePreviousButton(false);

            intent.putExtra(OpdConstants.JSON_FORM_EXTRA.JSON, jsonForm.toString());
            intent.putExtra(JsonFormConstants.JSON_FORM_KEY.FORM, form);
            if (parcelableData != null) {
                for (String intentKey : parcelableData.keySet()) {
                    intent.putExtra(intentKey, parcelableData.get(intentKey));
                }
            }
            startActivityForResult(intent, OpdJsonFormUtils.REQUEST_CODE_GET_JSON);


        } else {
            Timber.e(new Exception(), "FormActivity cannot be started because OpdMetadata is NULL");
        }
    }


    @Override
    public void switchToBaseFragment() {
        Intent intent = new Intent(this, KipOpdRegisterActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public OpdRegisterActivityContract.Presenter presenter() {
        return (OpdRegisterActivityContract.Presenter) presenter;
    }

    @Override
    public void startRegistration() {
        //Do nothing
    }

    @Override
    protected Fragment[] getOtherFragments() {
        ME_POSITION = 1;

        Fragment[] fragments = new Fragment[1];
        fragments[ME_POSITION - 1] = new MeFragment();

        return fragments;
    }
}