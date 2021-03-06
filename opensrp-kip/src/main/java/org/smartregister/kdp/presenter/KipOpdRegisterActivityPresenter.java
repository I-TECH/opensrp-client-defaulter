package org.smartregister.kdp.presenter;

import android.app.Activity;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.smartregister.domain.FetchStatus;
import org.smartregister.kdp.R;
import org.smartregister.kdp.interactor.OpdRegisterActivityInteractor;
import org.smartregister.kdp.view.NavigationMenu;
import org.smartregister.opd.contract.OpdRegisterActivityContract;
import org.smartregister.opd.pojo.OpdEventClient;
import org.smartregister.opd.pojo.RegisterParams;
import org.smartregister.opd.presenter.BaseOpdRegisterActivityPresenter;
import org.smartregister.opd.utils.OpdJsonFormUtils;
import org.smartregister.opd.utils.OpdUtils;

import java.util.List;

import timber.log.Timber;


public class KipOpdRegisterActivityPresenter extends BaseOpdRegisterActivityPresenter implements OpdRegisterActivityContract.Presenter, OpdRegisterActivityContract.InteractorCallBack {

    public KipOpdRegisterActivityPresenter(@NonNull OpdRegisterActivityContract.View view, @NonNull OpdRegisterActivityContract.Model model) {
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
