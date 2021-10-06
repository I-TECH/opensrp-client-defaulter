package org.smartregister.kdp.contract;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.smartregister.kdp.pojo.RecordDefaulterForm;
import org.smartregister.kdp.pojo.UpdateDefaulterForm;
import org.smartregister.opd.contract.OpdProfileOverviewFragmentContract;
import org.smartregister.opd.pojo.OpdDetails;
import org.smartregister.opd.pojo.OpdVisit;

import java.util.Map;

public interface KipOpdProfileOverviewFragmentContract extends OpdProfileOverviewFragmentContract {
    interface Presenter extends OpdProfileOverviewFragmentContract.Presenter {
        void loadOverviewDataAndDisplay(@Nullable Map<String, String> opdCheckIn, @Nullable OpdVisit opdVisit, @Nullable OpdDetails opdDetails, @NonNull RecordDefaulterForm recordDefaulterForm, @NonNull UpdateDefaulterForm updateDefaulterForm, @NonNull final OnFinishedCallback onFinishedCallback);
    }

    interface View extends OpdProfileOverviewFragmentContract.View {
    }

    interface Model extends OpdProfileOverviewFragmentContract.Model {
        void fetchLastCheckAndVisit(@NonNull String baseEntityId, @NonNull KipOpdProfileOverviewFragmentContract.Model.OnFetchedCallback onFetchedCallback);

        interface OnFetchedCallback {
            void onFetched(@Nullable Map<String, String> opdCheckIn, @Nullable OpdVisit opdVisit, @Nullable OpdDetails opdDetails, @NonNull RecordDefaulterForm recordDefaulterForm, @NonNull UpdateDefaulterForm updateDefaulterForm);
        }
    }
}
