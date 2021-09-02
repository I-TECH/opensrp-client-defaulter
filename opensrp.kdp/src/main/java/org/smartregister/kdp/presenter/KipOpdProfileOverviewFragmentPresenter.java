package org.smartregister.kdp.presenter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.jeasy.rules.api.Facts;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.kdp.application.KipApplication;
import org.smartregister.kdp.contract.KipOpdProfileOverviewFragmentContract;
import org.smartregister.kdp.model.KipOpdProfileOverviewFragmentModel;
import org.smartregister.kdp.pojo.ChvDetailsForm;
import org.smartregister.kdp.pojo.RecordDefaulterForm;
import org.smartregister.kdp.pojo.MissedVaccineForm;
import org.smartregister.kdp.pojo.TracingModeForm;
import org.smartregister.kdp.pojo.UpdateDefaulterForm;
import org.smartregister.kdp.util.KipConstants;
import org.smartregister.opd.OpdLibrary;
import org.smartregister.opd.domain.YamlConfig;
import org.smartregister.opd.domain.YamlConfigItem;
import org.smartregister.opd.domain.YamlConfigWrapper;
import org.smartregister.opd.pojo.OpdDetails;
import org.smartregister.opd.pojo.OpdVisit;
import org.smartregister.opd.presenter.OpdProfileOverviewFragmentPresenter;
import org.smartregister.opd.utils.FilePath;
import org.smartregister.opd.utils.OpdConstants;
import org.smartregister.opd.utils.OpdDbConstants;
import org.smartregister.opd.utils.OpdFactsUtil;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

public class KipOpdProfileOverviewFragmentPresenter extends OpdProfileOverviewFragmentPresenter implements KipOpdProfileOverviewFragmentContract.Presenter {
    private KipOpdProfileOverviewFragmentModel model;
    private static CommonPersonObjectClient client;
    private WeakReference<KipOpdProfileOverviewFragmentContract.View> view;

    public KipOpdProfileOverviewFragmentPresenter(@NonNull KipOpdProfileOverviewFragmentContract.View view) {
        super(view);
        this.view = new WeakReference<>(view);
        model = new KipOpdProfileOverviewFragmentModel();
    }

    @Override
    public void loadOverviewFacts(@NonNull String baseEntityId, @NonNull final OnFinishedCallback onFinishedCallback) {
        model.fetchLastCheckAndVisit(baseEntityId, (opdCheckIn, opdVisit, opdDetails,recordDefaulterForm, updateDefaulterForm) -> loadOverviewDataAndDisplay(opdCheckIn, opdVisit, opdDetails, recordDefaulterForm, updateDefaulterForm, onFinishedCallback));
    }

    @Override
    public void loadOverviewDataAndDisplay(@Nullable Map<String, String> opdCheckIn, @Nullable OpdVisit opdVisit, @Nullable OpdDetails opdDetails, @NonNull RecordDefaulterForm recordDefaulterForm, @NonNull UpdateDefaulterForm updateDefaulterForm, @NonNull final OnFinishedCallback onFinishedCallback) {
        List<YamlConfigWrapper> yamlConfigListGlobal = new ArrayList<>(); //This makes sure no data duplication happens
        Facts facts = new Facts();
        setDataFromCheckIn(opdCheckIn, opdVisit, opdDetails, facts);

        if (recordDefaulterForm != null){
            generateLastVaccineGivenFormFacts(recordDefaulterForm, facts);
        }

        if (updateDefaulterForm != null){
            generateUpdateDefaulterFormFacts(updateDefaulterForm, facts);
        }

        try {
            generateYamlConfigList(facts, yamlConfigListGlobal);
        } catch (IOException ioException) {
            Timber.e(ioException);
        }

        onFinishedCallback.onFinished(facts, yamlConfigListGlobal);
    }

    public void generateLastVaccineGivenFormFacts(@Nullable RecordDefaulterForm recordDefaulterForm, @NonNull Facts facts){
        OpdFactsUtil.putNonNullFact(facts, KipConstants.DbConstants.Columns.RecordDefaulerForm.ANTIGEN_ADMINISTERED_LAST, recordDefaulterForm.getAntigenAdministeredLast());
        OpdFactsUtil.putNonNullFact(facts, KipConstants.DbConstants.Columns.RecordDefaulerForm.ADMINISTRATION_DATE, recordDefaulterForm.getAdministrationDate());
        OpdFactsUtil.putNonNullFact(facts, KipConstants.DbConstants.Columns.RecordDefaulerForm.MISSED_VACCINE, recordDefaulterForm.getMissedDoses());
        OpdFactsUtil.putNonNullFact(facts, KipConstants.DbConstants.Columns.RecordDefaulerForm.RETURN_DATE, recordDefaulterForm.getReturnDate());
        OpdFactsUtil.putNonNullFact(facts, KipConstants.DbConstants.Columns.RecordDefaulerForm.CHV_NAME, recordDefaulterForm.getChvName());
        OpdFactsUtil.putNonNullFact(facts, KipConstants.DbConstants.Columns.RecordDefaulerForm.CHV_PHONE_NUMBER, recordDefaulterForm.getChvPhoneNumber());
        OpdFactsUtil.putNonNullFact(facts, KipConstants.DbConstants.Columns.RecordDefaulerForm.BIRTH_DOSE_ANTIGEN, recordDefaulterForm.getBirthDoseAntigen());
        OpdFactsUtil.putNonNullFact(facts, KipConstants.DbConstants.Columns.RecordDefaulerForm.SIX_WKS_ANTIGEN, recordDefaulterForm.getSixWksAntigen());
        OpdFactsUtil.putNonNullFact(facts, KipConstants.DbConstants.Columns.RecordDefaulerForm.TEN_WKS_ANTIGEN, recordDefaulterForm.getTenWksAntigen());
        OpdFactsUtil.putNonNullFact(facts, KipConstants.DbConstants.Columns.RecordDefaulerForm.FOURTEEN_WKS_ANTIGEN, recordDefaulterForm.getFourteenWksAntigen());
        OpdFactsUtil.putNonNullFact(facts, KipConstants.DbConstants.Columns.RecordDefaulerForm.NINE_MONTHS_ANTIGEN, recordDefaulterForm.getNineMonthsAntigen());
        OpdFactsUtil.putNonNullFact(facts, KipConstants.DbConstants.Columns.RecordDefaulerForm.EIGHTEEN_MONTHS_ANTIGEN, recordDefaulterForm.getEighteenMonthsAntigen());
    }

    public void generateUpdateDefaulterFormFacts(@Nullable UpdateDefaulterForm updateDefaulterForm, @NonNull Facts facts){
        OpdFactsUtil.putNonNullFact(facts, KipConstants.DbConstants.Columns.UpdateDefaulterForm.PHONE_TRACING_OUTCOME, updateDefaulterForm.getPhoneTracingOutcome());
        OpdFactsUtil.putNonNullFact(facts, KipConstants.DbConstants.Columns.UpdateDefaulterForm.PHYSICAL_TRACING_OUTCOME, updateDefaulterForm.getPhysicalTracingOutcome());
        OpdFactsUtil.putNonNullFact(facts, KipConstants.DbConstants.Columns.UpdateDefaulterForm.PHONE_TRACING, updateDefaulterForm.getPhoneTracing());
        OpdFactsUtil.putNonNullFact(facts, KipConstants.DbConstants.Columns.UpdateDefaulterForm.PHYSICAL_TRACING, updateDefaulterForm.getPhysicalTracing());
        OpdFactsUtil.putNonNullFact(facts, KipConstants.DbConstants.Columns.UpdateDefaulterForm.HOME_ADMINISTRATION_DATE, updateDefaulterForm.getHomeAdministrationDate());
        OpdFactsUtil.putNonNullFact(facts, KipConstants.DbConstants.Columns.UpdateDefaulterForm.OTHER_FACILITY_ADMINISTRATION_DATE, updateDefaulterForm.getOtherFacilityAdministrationDate());
        OpdFactsUtil.putNonNullFact(facts, KipConstants.DbConstants.Columns.UpdateDefaulterForm.OTHER_FACILITY_NAME, updateDefaulterForm.getOtherFacilityName());
        OpdFactsUtil.putNonNullFact(facts, KipConstants.DbConstants.Columns.UpdateDefaulterForm.DATE_TO_CONFIRM_VACCINATION, updateDefaulterForm.getDateToConfirmVaccination());
    }

    private void generateYamlConfigList(@NonNull Facts facts, @NonNull List<YamlConfigWrapper> yamlConfigListGlobal) throws IOException {
        Iterable<Object> ruleObjects = loadFile(FilePath.FILE.OPD_PROFILE_OVERVIEW);
        for (Object ruleObject : ruleObjects) {
            List<YamlConfigWrapper> yamlConfigList = new ArrayList<>();
            int valueCount = 0;

            YamlConfig yamlConfig = (YamlConfig) ruleObject;
            if (yamlConfig.getGroup() != null) {
                yamlConfigList.add(new YamlConfigWrapper(yamlConfig.getGroup(), null, null));
            }

            if (yamlConfig.getSubGroup() != null) {
                yamlConfigList.add(new YamlConfigWrapper(null, yamlConfig.getSubGroup(), null));
            }

            List<YamlConfigItem> configItems = yamlConfig.getFields();

            if (configItems != null) {

                for (YamlConfigItem configItem : configItems) {
                    String relevance = configItem.getRelevance();
                    if (relevance != null && OpdLibrary.getInstance().getOpdRulesEngineHelper()
                            .getRelevance(facts, relevance)) {
                        yamlConfigList.add(new YamlConfigWrapper(null, null, configItem));
                        valueCount += 1;
                    }
                }
            }

            if (valueCount > 0) {
                yamlConfigListGlobal.addAll(yamlConfigList);
            }
        }
    }

    private Iterable<Object> loadFile(@NonNull String filename) throws IOException {
        return OpdLibrary.getInstance().readYaml(filename);
    }

    @Override
    public void setClient(@NonNull CommonPersonObjectClient client) {
        this.client = client;
    }

    private static CommonPersonObjectClient commonPersonObjectClient = client;

    @Override
    public void setDataFromCheckIn(@Nullable Map<String, String> checkIn, @Nullable OpdVisit visit, @Nullable OpdDetails opdDetails, @NonNull Facts facts) {
        String unknownString = getString(org.smartregister.opd.R.string.unknown);
        if (checkIn != null) {
            // Client is currently checked-in, show the current check-in details
            if (KipApplication.getInstance().isClientCurrentlyCheckedIn(visit, opdDetails)) {
                OpdFactsUtil.putNonNullFact(facts, OpdConstants.FactKey.ProfileOverview.VISIT_TYPE, checkIn.get("visit_type"));
            }
        }

        boolean shouldCheckIn = KipApplication.getInstance().canPatientCheckInInsteadOfDiagnoseAndTreat(visit, opdDetails);
        facts.put(OpdDbConstants.Column.OpdDetails.PENDING_DIAGNOSE_AND_TREAT, !shouldCheckIn);

    }

}
