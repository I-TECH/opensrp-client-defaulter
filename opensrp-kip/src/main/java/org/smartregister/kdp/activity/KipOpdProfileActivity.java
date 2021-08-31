package org.smartregister.kdp.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.viewpager.widget.ViewPager;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
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
import org.smartregister.location.helper.LocationHelper;
import org.smartregister.opd.activity.BaseOpdProfileActivity;
import org.smartregister.opd.adapter.ViewPagerAdapter;
import org.smartregister.opd.presenter.OpdProfileActivityPresenter;
import org.smartregister.opd.utils.OpdConstants;
import org.smartregister.opd.utils.OpdJsonFormUtils;
import org.smartregister.opd.utils.OpdUtils;
import java.util.Map;
import java.util.Objects;

import timber.log.Timber;

public class KipOpdProfileActivity extends BaseOpdProfileActivity implements KipOpdProfileActivityContract.View {
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
                    case KipConstants.EventType.OPD_SMS_REMINDER:
                        showProgressDialog(R.string.saving_dialog_title);
                        ((KipOpdProfileActivityPresenter) presenter).saveOpdSMSReminderForm(encounterType, data);
                        onResumption();
                        break;
                    case KipConstants.EventType.RECORD_DEFAULTER_FORM:
                        showProgressDialog(R.string.saving_dialog_title);
                        saveLastVaccineGiven();
                        ((KipOpdProfileActivityPresenter) presenter).saveLastVaccineGiven(encounterType, data);
                        onResumption();
                        break;
                    case KipConstants.EventType.UPDATE_DEFAULT:
                        showProgressDialog(R.string.saving_dialog_title);
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
            MenuItem sendSmsReminder = menu.findItem(R.id.opd_menu_item_send_sms);
            if (closeMenu != null) {
                closeMenu.setEnabled(true);
                sendSmsReminder.setVisible(false);

                toggleViews(sendSmsReminder);
            }
        }

        return true;
    }

    private void toggleViews(MenuItem sendSmsReminder) {
        if (checkReminder()) {
            sendSmsReminder.setEnabled(true);
            sendSmsReminder.setVisible(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.opd_menu_item_send_sms){
        }
        return super.onOptionsItemSelected(item);
    }

//    private void sendReminderSms(){
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
//
//            if (checkSelfPermission(Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED){
//                Map<String, String> details = getClient().getDetails();
//                String client = details.get("first_name");
//                String messageTxt = formatMessage().trim();
//                String phone = phoneNumber().trim();
//                sendSmsReminder(messageTxt, phone);
//                org.smartregister.child.util.Utils.showToast(this, "Reminder Message Successfully Sent to "+ client);
//                ((KipOpdProfileActivityPresenter) this.presenter).startForm(KipOpdConstants.KipForms.OPD_SMS_REMINDER, Objects.requireNonNull(getClient()));
//            } else {
//                requestPermissions(new String[]{Manifest.permission.SEND_SMS}, 1);
//            }
//        }
//    }

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

    public String phoneNumber(){
        Map<String, String> details = getClient().getDetails();
        String phoneNum = details.get("phone_number");
        return phoneNum;
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



//    private String formatMessage(){
//        Map<String, String> details = getClient().getDetails();
//        String firstName = details.get("first_name");
//        String facilliTy = LocationHelper.getInstance().getOpenMrsReadableName(KipChildUtils.getCurrentLocality());
//
//        return "Dear parent, "+" " + firstName + " "+ KipConstants.TXT_SMS_REMINDER + " " + KipConstants.TXT + facilliTy;
//    }
//
//    public void sendSmsReminder(String messageTxt, String phone){
//        try {
//            SmsManager smsManager = SmsManager.getDefault();
//            smsManager.sendTextMessage(phone,null, messageTxt, null, null);
//        } catch (Exception e){
//            Timber.e(e, "Message could not be sent");
//        }
//    }
}
