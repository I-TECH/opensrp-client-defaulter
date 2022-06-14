package org.smartregister.kdp.presenter;

import android.app.Activity;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.apache.commons.lang3.tuple.Triple;
import org.json.JSONException;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.domain.FetchStatus;
import org.smartregister.kdp.R;
import org.smartregister.kdp.application.KipApplication;
import org.smartregister.kdp.interactor.OpdRegisterActivityInteractor;
import org.smartregister.kdp.util.KipConstants;
import org.smartregister.kdp.view.NavigationMenu;
import org.smartregister.opd.OpdLibrary;
import org.smartregister.opd.contract.OpdRegisterActivityContract;
import org.smartregister.opd.interactor.OpdProfileInteractor;
import org.smartregister.opd.pojo.OpdEventClient;
import org.smartregister.opd.pojo.RegisterParams;
import org.smartregister.opd.presenter.BaseOpdRegisterActivityPresenter;
import org.smartregister.opd.utils.OpdConstants;
import org.smartregister.opd.utils.OpdJsonFormUtils;
import org.smartregister.opd.utils.OpdUtils;

import java.util.Collections;
import java.util.List;

import timber.log.Timber;


public class KipOpdRegisterActivityPresenter extends BaseOpdRegisterActivityPresenter implements OpdRegisterActivityContract.Presenter,
        OpdRegisterActivityContract.InteractorCallBack{


    public KipOpdRegisterActivityPresenter(@NonNull OpdRegisterActivityContract.View view,
                                           @NonNull OpdRegisterActivityContract.Model model) {
        super(view, model);

    }

    @Override
    public void saveForm(@NonNull String jsonString, @NonNull RegisterParams registerParams) {
        try {
            if (registerParams.getFormTag() == null) {
                registerParams.setFormTag(OpdJsonFormUtils.formTag(OpdUtils.getAllSharedPreferences()));
            }
            List<OpdEventClient> opdEventClientList = model.processRegistration(jsonString, registerParams.getFormTag());
            if (opdEventClientList == null || opdEventClientList.isEmpty()) {
                return;
            }
            registerParams.setEditMode(false);
            interactor.saveRegistration(opdEventClientList, jsonString, registerParams, this);

        } catch (Exception e) {
            Timber.e(e);
        }
    }


    public void saveWeeklyReport(@NonNull String eventType, @Nullable Intent data) {
        String jsonString = null;
        if (data != null) {
            jsonString = data.getStringExtra(OpdConstants.JSON_FORM_EXTRA.JSON);
        }

        if (jsonString == null) {
            return;
        }

        if (eventType.equals(KipConstants.EventType.OPD_WEEKLY_REPORT)) {
            try {
                Event weeklyReport = KipApplication.getInstance().processDefaulterReportForm(eventType,jsonString,data);
                interactor.saveEvents(Collections.singletonList(weeklyReport), this);
            } catch (JSONException e) {
                Timber.e(e);
            }
        }
    }

    @NonNull
    @Override
    public OpdRegisterActivityContract.Interactor createInteractor() {
        return new OpdRegisterActivityInteractor();
    }

    @Override
    public void onNoUniqueId() {
        if (getView() != null) {
            getView().displayShortToast(R.string.no_unique_id);
        }
    }

    @Override
    public void onUniqueIdFetched(@NonNull Triple<String, String, String> triple, @NonNull String entityId) {
        try {
            startForm(triple.getLeft(), entityId, triple.getMiddle(), triple.getRight(), null, null);
        } catch (Exception e) {
            Timber.e(e);
            if (getView() != null) {
                getView().displayToast(R.string.error_unable_to_start_form);
            }
        }
    }

    @Override
    public void onRegistrationSaved(boolean isEdit) {
        if (getView() != null) {
            getView().refreshList(FetchStatus.fetched);
            getView().hideProgressDialog();
            NavigationMenu navigationMenu = NavigationMenu.getInstance((Activity) viewReference.get(), null, null);
            if (navigationMenu != null) {
                navigationMenu.runRegisterCount();
            }
        }
    }

    @Nullable
    private OpdRegisterActivityContract.View getView() {
        if (viewReference != null) {
            return viewReference.get();
        } else {
            return null;
        }
    }

    @Override
    public void saveLanguage(String language) {
        model.saveLanguage(language);

        if (getView() != null) {
            getView().displayToast(String.format(getView().getContext().getString(R.string.language_x_selected), language));
        }
    }
}
