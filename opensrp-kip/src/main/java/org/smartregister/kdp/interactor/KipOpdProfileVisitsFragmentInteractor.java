package org.smartregister.kdp.interactor;

import androidx.annotation.NonNull;

import org.smartregister.kdp.application.KipApplication;
import org.smartregister.kdp.contract.KipOpdProfileVisitsFragmentContract;
import org.smartregister.kdp.pojo.KipOpdVisitSummary;
import org.smartregister.opd.OpdLibrary;
import org.smartregister.opd.contract.OpdProfileVisitsFragmentContract;
import org.smartregister.opd.interactor.OpdProfileVisitsFragmentInteractor;
import org.smartregister.opd.pojo.OpdVisitSummary;
import org.smartregister.opd.utils.AppExecutors;

import java.util.List;

public class KipOpdProfileVisitsFragmentInteractor extends OpdProfileVisitsFragmentInteractor implements KipOpdProfileVisitsFragmentContract.Interactor {
    private KipOpdProfileVisitsFragmentContract.Presenter mProfileFrgamentPresenter;
    private AppExecutors appExecutors;

    public KipOpdProfileVisitsFragmentInteractor(@NonNull KipOpdProfileVisitsFragmentContract.Presenter presenter) {
        super(presenter);
        this.mProfileFrgamentPresenter = presenter;
        appExecutors = new AppExecutors();
    }

    @Override
    public void onDestroy(boolean isChangingConfiguration) {
        if (!isChangingConfiguration) {
            mProfileFrgamentPresenter = null;
        }
    }

    @Override
    public void refreshProfileView(@NonNull String baseEntityId, boolean isForEdit) {
        // Todo: We will have an implementation for refresh view
    }


    @Override
    public void fetchKipVisits(@NonNull String baseEntityId, int pageNo, @NonNull KipOpdProfileVisitsFragmentContract.Presenter.OnKipVisitsLoadedCallback onVisitsLoadedCallback) {
        appExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                final List<KipOpdVisitSummary> summaries = KipApplication.getInstance().kipOpdVisitSummaryRepository().getKipOpdVisitSummaries(baseEntityId, pageNo);

                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        onVisitsLoadedCallback.onKipVisitsLoaded(summaries);
                    }
                });
            }
        });
    }

    @Override
    public void fetchVisitsPageCount(@NonNull final String baseEntityId, @NonNull final OnFetchVisitsPageCountCallback onFetchVisitsPageCountCallback) {
        appExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                final int visitsPageCount = KipApplication.getInstance().kipOpdVisitSummaryRepository().getVisitPageCount(baseEntityId);

                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        onFetchVisitsPageCountCallback.onFetchVisitsPageCount(visitsPageCount);
                    }
                });
            }
        });
    }
}