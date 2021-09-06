package org.smartregister.kdp.presenter;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.domain.Form;

import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.kdp.contract.KipOpdProfileActivityContract;
import org.smartregister.kdp.repository.KipOpdDetailsRepository;
import org.smartregister.kdp.util.KipConstants;
import org.smartregister.opd.OpdLibrary;
import org.smartregister.opd.contract.OpdProfileActivityContract;
import org.smartregister.opd.interactor.OpdProfileInteractor;
import org.smartregister.opd.model.OpdProfileActivityModel;
import org.smartregister.opd.pojo.OpdMetadata;
import org.smartregister.opd.presenter.OpdProfileActivityPresenter;
import org.smartregister.opd.tasks.FetchRegistrationDataTask;
import org.smartregister.opd.utils.OpdConstants;
import org.smartregister.opd.utils.OpdJsonFormUtils;
import org.smartregister.opd.utils.OpdUtils;
import org.smartregister.util.Utils;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;

import timber.log.Timber;

public class KipOpdProfileActivityPresenter extends OpdProfileActivityPresenter {
    private static final String SMS_REMINDER = "opd_sms_reminder";
    private final OpdProfileActivityModel model;
    private final WeakReference<KipOpdProfileActivityContract.View> mProfileView;
    private final OpdProfileActivityContract.Interactor mProfileInteractor;

    public KipOpdProfileActivityPresenter(KipOpdProfileActivityContract.View profileView) {
        super(profileView);
        mProfileView = new WeakReference<>(profileView);
        mProfileInteractor = new OpdProfileInteractor(this);
        model = new OpdProfileActivityModel();
    }

    @Nullable
    @Override
    public KipOpdProfileActivityContract.View getProfileView() {
        if (mProfileView != null) {
            return mProfileView.get();
        }

        return null;
    }

    @Override
    public void onUpdateRegistrationBtnCLicked(@NonNull String baseEntityId) {
        if (getProfileView() != null) {
            Utils.startAsyncTask(new FetchRegistrationDataTask(new WeakReference<>(getProfileView()), jsonForm -> {
                OpdMetadata metadata = OpdUtils.metadata();

                OpdProfileActivityContract.View profileView = getProfileView();
                if (profileView != null && metadata != null && jsonForm != null) {
                    Context context = profileView.getContext();
                    Intent intent = new Intent(context, metadata.getOpdFormActivity());
                    Form formParam = new Form();
                    formParam.setWizard(false);
                    formParam.setHideSaveLabel(true);
                    formParam.setNextLabel("");
                    JSONObject jsonFormObject = null;
                    try {
                        jsonFormObject = new JSONObject(jsonForm);
                    } catch (JSONException jsonException) {
                        jsonException.printStackTrace();
                    }

                    String encounterType = jsonFormObject.optString(OpdJsonFormUtils.ENCOUNTER_TYPE);
                    if (encounterType.equals(OpdConstants.EventType.UPDATE_OPD_REGISTRATION)) {
                        updateRegistrationDetails(jsonFormObject);
                    }

                    intent.putExtra(JsonFormConstants.JSON_FORM_KEY.FORM, formParam);
                    intent.putExtra(JsonFormConstants.JSON_FORM_KEY.JSON, jsonFormObject.toString());
                    profileView.startActivityForResult(intent, OpdJsonFormUtils.REQUEST_CODE_GET_JSON);
                }
            }), new String[]{baseEntityId});
        }
    }

    public JSONObject updateRegistrationDetails(@NonNull JSONObject form) {
        JSONObject jsonForm = new JSONObject();
        CommonPersonObjectClient client = getProfileView().getClient();
        Timber.i(client.toString());
        return jsonForm;
    }

    public void saveOpdSMSReminderForm(@NonNull String eventType, @Nullable Intent data) {
        String jsonString = null;
        if (data != null) {
            jsonString = data.getStringExtra(OpdConstants.JSON_FORM_EXTRA.JSON);
        }

        if (jsonString == null) {
            return;
        }

        List<Event> opdSmsReminder = null;
        if (eventType.equals(KipConstants.EventType.OPD_SMS_REMINDER)) {
            try {
                opdSmsReminder = OpdLibrary.getInstance().processOpdForm(jsonString, data);
                mProfileInteractor.saveEvents(opdSmsReminder, this);
            } catch (JSONException e) {
                Timber.e(e);
            }
        }

        KipOpdDetailsRepository.updateSmsReminder(opdSmsReminder.get(0).getBaseEntityId());
    }

    public void saveLastVaccineGiven(@NonNull String eventType, @Nullable Intent data) {
        String jsonString = null;
        if (data != null) {
            jsonString = data.getStringExtra(OpdConstants.JSON_FORM_EXTRA.JSON);
        }

        if (jsonString == null) {
            return;
        }

        if (eventType.equals(KipConstants.EventType.RECORD_DEFAULTER_FORM)) {
            try {
                List<Event> lastVaccineGiven = OpdLibrary.getInstance().processOpdForm(jsonString, data);
                mProfileInteractor.saveEvents(lastVaccineGiven, this);
            } catch (JSONException e) {
                Timber.e(e);
            }
        }
        onOpdEventSaved();
    }


    public void saveUpdateDefaulterForm(@NonNull String eventType, @Nullable Intent data) {
        String jsonString = null;
        if (data != null) {
            jsonString = data.getStringExtra(OpdConstants.JSON_FORM_EXTRA.JSON);
        }

        if (jsonString == null) {
            return;
        }


        List<Event> updateDefaulterForm = null;
        if (eventType.equals(KipConstants.EventType.UPDATE_DEFAULT)) {
            try {
                updateDefaulterForm = OpdLibrary.getInstance().processOpdForm(jsonString, data);
                mProfileInteractor.saveEvents(updateDefaulterForm, this);
            } catch (JSONException e) {
                Timber.e(e);
            }
        }
//        String baseEntityId = updateDefaulterForm.get(0).getBaseEntityId();
//        KipOpdDetailsRepository.restDefaulterSchedule(baseEntityId);
//        KipOpdDetailsRepository.updateDefaulterStatus(baseEntityId);
        onOpdEventSaved();
    }

    public String loadForm(@NonNull String formName, @NonNull CommonPersonObjectClient commonPersonObjectClient) {
        String form = "";
        try {
            HashMap<String, String> injectedValues = this.getInjectedFields(formName, commonPersonObjectClient.getCaseId());
            String locationId = OpdUtils.context().allSharedPreferences().getPreference("CURRENT_LOCATION_ID");
            form = String.valueOf(model.getFormAsJson(formName, commonPersonObjectClient.getCaseId(), locationId, injectedValues));
        } catch (JSONException exception) {
            Timber.e(exception);
        }
        return form;
    }


    @Override
    public void onOpdEventSaved() {
        KipOpdProfileActivityContract.View view = getProfileView();
        if (view != null) {
            view.getActionListenerForProfileOverview().onActionReceive();
            view.getActionListenerForVisitFragment().onActionReceive();
            view.hideProgressDialog();
        }
    }
}
