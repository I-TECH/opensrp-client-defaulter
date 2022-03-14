package org.smartregister.kdp.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.viewpager.widget.ViewPager;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.domain.FetchStatus;
import org.smartregister.kdp.R;
import org.smartregister.kdp.contract.KipOpdProfileActivityContract;
import org.smartregister.kdp.fragment.KipOpdProfileOverviewFragment;
import org.smartregister.kdp.fragment.KipOpdProfileVisitsFragment;
import org.smartregister.kdp.presenter.KipOpdProfileActivityPresenter;
import org.smartregister.kdp.repository.KipOpdDetailsRepository;
import org.smartregister.kdp.util.KipChildUtils;
import org.smartregister.kdp.util.KipConstants;
//import org.smartregister.kip.util.KipJsonFormUtils;
import org.smartregister.kdp.util.KipOpdConstants;
import org.smartregister.kdp.util.KipSyncUtil;
import org.smartregister.location.helper.LocationHelper;
import org.smartregister.opd.activity.BaseOpdProfileActivity;
import org.smartregister.opd.adapter.ViewPagerAdapter;
import org.smartregister.opd.presenter.OpdProfileActivityPresenter;
import org.smartregister.opd.utils.OpdConstants;
import org.smartregister.opd.utils.OpdJsonFormUtils;
import org.smartregister.opd.utils.OpdUtils;
import org.smartregister.receiver.SyncStatusBroadcastReceiver;

import java.util.Map;
import java.util.Objects;

import timber.log.Timber;

public class KipOpdProfileActivity extends BaseOpdProfileActivity implements KipOpdProfileActivityContract.View, SyncStatusBroadcastReceiver.SyncStatusListener {
    @Override
    protected void initializePresenter() {
        presenter = new KipOpdProfileActivityPresenter(this);
    }

    @Override
    protected ViewPager setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        KipOpdProfileOverviewFragment profileOverviewFragment = KipOpdProfileOverviewFragment.newInstance(this.getIntent().getExtras());
        setSendActionListenerForProfileOverview(profileOverviewFragment);

        KipOpdProfileVisitsFragment profileVisitsFragment = KipOpdProfileVisitsFragment.newInstance(this.getIntent().getExtras());
        setSendActionListenerToVisitsFragment(profileVisitsFragment);

        adapter.addFragment(profileOverviewFragment, this.getString(org.smartregister.opd.R.string.overview));
        adapter.addFragment(profileVisitsFragment, this.getString(org.smartregister.opd.R.string.visits));

        viewPager.setAdapter(adapter);
        return viewPager;
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
                    case KipConstants.EventType.RECORD_DEFAULTER_FORM:
                        showProgressDialog(R.string.saving_dialog_title);
                        saveLastVaccineGiven();
                        ((KipOpdProfileActivityPresenter) presenter).saveLastVaccineGiven(encounterType, data);
                        onResumption();
                        break;
                    case KipConstants.EventType.UPDATE_DEFAULT:
                        showProgressDialog(R.string.saving_dialog_title);
                        saveUpdateDefaulterForm();
                        ((KipOpdProfileActivityPresenter) presenter).saveUpdateDefaulterForm(encounterType, data);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(org.smartregister.opd.R.menu.menu_opd_profile_activity, menu);

        if (KipConstants.RegisterType.OPD.equalsIgnoreCase(getRegisterType())) {
            MenuItem closeMenu = menu.findItem(R.id.opd_menu_item_close_client);
            if (closeMenu != null) {
                closeMenu.setEnabled(true);
            }
        }

        return true;
    }

//    private void toggleViews(MenuItem sendSmsReminder) {
//        if (checkReminder()) {
//            sendSmsReminder.setEnabled(true);
//            sendSmsReminder.setVisible(false);
//        }
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id== R.id.opd_menu_item_sync){
            startSync();
        }
        return super.onOptionsItemSelected(item);
    }

    protected void showStartSyncToast() {
        Toast.makeText(this, getResources().getText(R.string.action_start_sync),
                Toast.LENGTH_SHORT).show();
    }

    protected void startSync() {
        if (!SyncStatusBroadcastReceiver.getInstance().isSyncing()) {
            initiateSync();
            showStartSyncToast();

        } else
            Toast.makeText(this, getResources().getText(R.string.sync_in_progress),
                    Toast.LENGTH_SHORT).show();
    }

    protected void initiateSync() {
        KipSyncUtil.initiateProfileSync();
    }

    @Override
    public void updateVaccineStockForm(Map<String, String> vaccineStock) {

    }

    @Override
    public String getVaccineDispenseForm() {
        return null;
    }

    @Override
    public void startForm(String formName) {
        ((KipOpdProfileActivityPresenter) this.presenter).startForm(formName, Objects.requireNonNull(getClient()));
    }

    private void saveLastVaccineGiven(){
        Map<String, String> details = getClient().getDetails();
        String baseEntityId = details.get(KipConstants.KEY.ID_LOWER_CASE);
        KipOpdDetailsRepository.updateLastVaccineGivenForm(baseEntityId);
    }

    private void saveUpdateDefaulterForm(){
        Map<String, String> details = getClient().getDetails();
        String baseEntityId = details.get(KipConstants.KEY.ID_LOWER_CASE);
        KipOpdDetailsRepository.updateDefaulterStatus(baseEntityId);
    }

    public boolean getLastVaccineGiven(){
        boolean isLastVaccine = false;
        Map<String, String> details = getPatientDetails();
        String lastVaccine = details.get("last_vaccine");
        if (StringUtils.isNotEmpty(lastVaccine) && lastVaccine.equalsIgnoreCase("1")){
            isLastVaccine = true;
        }
        return isLastVaccine;
    }

    public boolean getDefaulterUpdateStatus(){
        boolean isDefaulterStatus = false;
        Map<String, String> details = getPatientDetails();
        String defaulterStatus = details.get("defaulter_status");
        if (StringUtils.isNotEmpty(defaulterStatus) && defaulterStatus.equalsIgnoreCase("1")){
            isDefaulterStatus = true;
        }
        return isDefaulterStatus;
    }

    private Map<String, String> getPatientDetails() {
        return OpdUtils.getClientDemographicDetails(getClient().getDetails().get(KipConstants.KEY.ID_LOWER_CASE));
    }

    public boolean checkReminder(){
        boolean isEnrolledInSmsMessages = false;
        Map<String, String> details = getClient().getDetails();
        String tracing = details.get("defaulter_status");
        String phoneNumber = details.get("phone_number");
        if (phoneNumber != null && tracing != null){
            isEnrolledInSmsMessages = true;
        }
        return isEnrolledInSmsMessages;
    }


    public void openDefaulterForms(String form) {
        ((KipOpdProfileActivityPresenter) this.presenter).startForm(form, Objects.requireNonNull(getClient()));
    }

    @Override
    public void onSyncStart() {
        Toast.makeText(this, getResources().getText(R.string.syncing),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSyncInProgress(FetchStatus fetchStatus) {
        Toast.makeText(this, getResources().getText(R.string.syncing),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSyncComplete(FetchStatus fetchStatus) {
        Toast.makeText(this, getResources().getText(R.string.sync_complete),
                Toast.LENGTH_SHORT).show();
        getActionListenerForProfileOverview().onActionReceive();
        getActionListenerForVisitFragment().onActionReceive();
    }

    @Override
    protected void onResume() {
        super.onResume();
        SyncStatusBroadcastReceiver.getInstance().addSyncStatusListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        SyncStatusBroadcastReceiver.getInstance().removeSyncStatusListener(this);
    }
}
