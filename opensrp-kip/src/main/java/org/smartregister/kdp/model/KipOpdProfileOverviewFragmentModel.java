package org.smartregister.kdp.model;

import androidx.annotation.NonNull;

import org.smartregister.kdp.application.KipApplication;
import org.smartregister.kdp.contract.KipOpdProfileOverviewFragmentContract;
import org.smartregister.kdp.pojo.ChvDetailsForm;
import org.smartregister.kdp.pojo.MissedVaccineForm;
import org.smartregister.kdp.pojo.RecordDefaulterForm;
import org.smartregister.kdp.pojo.TracingModeForm;
import org.smartregister.kdp.pojo.UpdateDefaulterForm;
import org.smartregister.opd.OpdLibrary;
import org.smartregister.opd.model.OpdProfileOverviewFragmentModel;
import org.smartregister.opd.pojo.OpdDetails;
import org.smartregister.opd.pojo.OpdVisit;
import org.smartregister.opd.utils.AppExecutors;

import java.util.Map;

public class KipOpdProfileOverviewFragmentModel extends OpdProfileOverviewFragmentModel implements KipOpdProfileOverviewFragmentContract.Model {
    private AppExecutors appExecutors;
    private OpdDetails opdDetails = null;
    private RecordDefaulterForm recordDefaulterForm = null;
    private UpdateDefaulterForm updateDefaulterForm = null;

    public KipOpdProfileOverviewFragmentModel() {
        this.appExecutors = new AppExecutors();
    }

    @Override
    public void fetchLastCheckAndVisit(final @NonNull String baseEntityId, @NonNull final KipOpdProfileOverviewFragmentContract.Model.OnFetchedCallback onFetchedCallback) {
        appExecutors.diskIO().execute(() -> {
            final OpdVisit visit = OpdLibrary.getInstance().getVisitRepository().getLatestVisit(baseEntityId);
            final Map<String, String> checkInMap = visit != null ? OpdLibrary.getInstance().getCheckInRepository().getCheckInByVisit(visit.getId()) : null;

            getOpdDetails(baseEntityId, visit);
            if (opdDetails != null && opdDetails.getCurrentVisitEndDate() == null) {
                getLastVaccineGivenForm(baseEntityId,visit);
                getUpdateDefaulterForm(baseEntityId,visit);
            }

            appExecutors.mainThread().execute(() -> onFetchedCallback.onFetched(checkInMap, visit, opdDetails, recordDefaulterForm,updateDefaulterForm));
        });
    }

    private void getOpdDetails(@NonNull String baseEntityId, OpdVisit visit) {
        opdDetails = null;

        if (visit != null) {
            opdDetails = new OpdDetails(baseEntityId, visit.getId());
            opdDetails = OpdLibrary.getInstance().getOpdDetailsRepository().findOne(opdDetails);
        }
    }

    private void getLastVaccineGivenForm(@NonNull String baseEntityId, OpdVisit visit) {
        recordDefaulterForm = null;

        if (visit != null) {
            recordDefaulterForm = new RecordDefaulterForm();
            recordDefaulterForm.setBaseEntityId(baseEntityId);
            recordDefaulterForm.setVisitId(visit.getId());
            recordDefaulterForm = KipApplication.getInstance().lastVaccineGivenFormRepository().findOneByVisit(recordDefaulterForm);
        }
    }

    private void getUpdateDefaulterForm(@NonNull String baseEntityId, OpdVisit visit) {
        updateDefaulterForm = null;

        if (visit != null) {
            updateDefaulterForm = new UpdateDefaulterForm();
            updateDefaulterForm.setBaseEntityId(baseEntityId);
            updateDefaulterForm.setVisitId(visit.getId());
            updateDefaulterForm = KipApplication.getInstance().updateDefaulterFormRepository().findOneByVisit(updateDefaulterForm);
        }
    }
}
