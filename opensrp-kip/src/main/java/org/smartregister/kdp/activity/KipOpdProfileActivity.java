package org.smartregister.kdp.activity;

import android.content.Intent;

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
import org.smartregister.kdp.util.KipConstants;
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
                        saveUpdateDefaulterForm();
                        ((KipOpdProfileActivityPresenter) presenter).saveUpdateDefaulterForm(encounterType, data);
                        onResumption();
                        break;
                    case KipConstants.EventType.UPDATE_COVID_DEFAULT:
                        showProgressDialog(R.string.saving_dialog_title);
                        saveUpdateCovidDefaulterStatusForm();
                        ((KipOpdProfileActivityPresenter) presenter).saveUpdateCovidDefaulterForm(encounterType, data);
                        onResumption();
                        break;
                    case KipConstants.EventType.RECORD_COVID_DEFAULTER_FORM:
                        showProgressDialog(R.string.saving_dialog_title);
                        saveCovidDefaulterForm();
                        ((KipOpdProfileActivityPresenter) presenter).saveRecordCovidDefaulter(encounterType, data);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        int id = item.getItemId();

        if (id == R.id.opd_menu_item_close_client){
            openCloseForm();
        }
        else if (id == R.id.opd_menu_item_send_sms){
        }
        return true;
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

    public int getAge() {
        return Integer.parseInt(((KipOpdProfileActivityPresenter) this.presenter).getAge(getClient()));
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

    private void saveCovidDefaulterForm(){
        Map<String, String> details = getClient().getDetails();
        String baseEntityId = details.get(KipConstants.KEY.ID_LOWER_CASE);
        KipOpdDetailsRepository.updateCovidDefaulterForm(baseEntityId);
    }

    private void saveUpdateCovidDefaulterStatusForm(){
        Map<String, String> details = getClient().getDetails();
        String baseEntityId = details.get(KipConstants.KEY.ID_LOWER_CASE);
        KipOpdDetailsRepository.updateCovidDefaulterStatus(baseEntityId);
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

    public boolean getCovidDefaulter(){
        boolean isCovidDefaulter = false;
        Map<String, String> details = getPatientDetails();
        String covidDefaulter = details.get("covid_defaulter");
        if (StringUtils.isNotEmpty(covidDefaulter) && covidDefaulter.equalsIgnoreCase("1")){
            isCovidDefaulter = true;
        }
        return isCovidDefaulter;
    }

    public boolean getCovid19Defaulter(){
        boolean isCovidDefaulter = false;
        Map<String, String> details = getPatientDetails();
        String covidDefaulter = details.get("covid_19_defaulter");
        if (StringUtils.isNotEmpty(covidDefaulter) && covidDefaulter.equalsIgnoreCase("Covid 19")){
            isCovidDefaulter = true;
        }
        return isCovidDefaulter;
    }

    private String covid19Defaulter(){
        Map<String, String> details = getPatientDetails();
        String covidDefaulter = details.get("covid_19_defaulter");
        return covidDefaulter;
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

    public boolean getCovid19DefaulterUpdateStatus(){
        boolean isDefaulterStatus = false;
        Map<String, String> details = getPatientDetails();
        String defaulterStatus = details.get("update_covid_defaulter_form");
        if (StringUtils.isNotEmpty(defaulterStatus) && defaulterStatus.equalsIgnoreCase("1")){
            isDefaulterStatus = true;
        }
        return isDefaulterStatus;
    }

    private Map<String, String> getPatientDetails() {
        return OpdUtils.getClientDemographicDetails(getClient().getDetails().get(KipConstants.KEY.ID_LOWER_CASE));
    }


    public void openDefaulterForms(String form) {
        ((KipOpdProfileActivityPresenter) this.presenter).startForm(form, Objects.requireNonNull(getClient()));
    }

    public void openCovid19Forms(String form) {
        ((KipOpdProfileActivityPresenter) this.presenter).startForm(KipConstants.JSON_FORM.OPD_COVID_DEFAULTER_FORM, Objects.requireNonNull(getClient()));
    }

    public void openUpdateCovid19Forms(String form) {
        ((KipOpdProfileActivityPresenter) this.presenter).startForm(KipConstants.JSON_FORM.OPD_UPDATE_COVID_DEFAULTER_FORM, Objects.requireNonNull(getClient()));
    }

}
