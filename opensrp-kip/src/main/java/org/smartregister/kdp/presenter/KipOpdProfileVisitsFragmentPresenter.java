package org.smartregister.kdp.presenter;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.core.util.Pair;

import org.jeasy.rules.api.Facts;
import org.smartregister.kdp.R;
import org.smartregister.kdp.contract.KipOpdProfileVisitsFragmentContract;
import org.smartregister.kdp.interactor.KipOpdProfileVisitsFragmentInteractor;
import org.smartregister.kdp.pojo.KipOpdVisitSummary;
import org.smartregister.kdp.util.KipOpdConstants;
import org.smartregister.opd.OpdLibrary;
import org.smartregister.opd.contract.OpdProfileVisitsFragmentContract;
import org.smartregister.opd.domain.YamlConfig;
import org.smartregister.opd.domain.YamlConfigItem;
import org.smartregister.opd.domain.YamlConfigWrapper;
import org.smartregister.opd.pojo.OpdVisitSummaryResultModel;
import org.smartregister.opd.presenter.OpdProfileVisitsFragmentPresenter;
import org.smartregister.opd.utils.FilePath;
import org.smartregister.opd.utils.OpdConstants;
import org.smartregister.opd.utils.OpdFactsUtil;
import org.smartregister.opd.utils.OpdUtils;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import timber.log.Timber;

public class KipOpdProfileVisitsFragmentPresenter extends OpdProfileVisitsFragmentPresenter implements KipOpdProfileVisitsFragmentContract.Presenter {
    private WeakReference<KipOpdProfileVisitsFragmentContract.View> mProfileView;
    private KipOpdProfileVisitsFragmentContract.Interactor mProfileInteractor;

    private int currentPageNo = 0;
    private int totalPages = 0;

    public KipOpdProfileVisitsFragmentPresenter(@NonNull KipOpdProfileVisitsFragmentContract.View profileView) {
        super(profileView);
        mProfileView = new WeakReference<>(profileView);
        mProfileInteractor = new KipOpdProfileVisitsFragmentInteractor(this);
    }

    @Override
    public void onDestroy(boolean isChangingConfiguration) {
        mProfileView = null;//set to null on destroy

        // Inform interactor
        if (mProfileInteractor != null) {
            mProfileInteractor.onDestroy(isChangingConfiguration);
        }

        // Activity destroyed set interactor to null
        if (!isChangingConfiguration) {
            mProfileInteractor = null;
        }
    }

    @Override
    public void loadKipVisits(@NonNull String baseEntityId, @NonNull final OnKipFinishedCallback onFinishedCallback) {
        if (mProfileInteractor != null) {
            mProfileInteractor.fetchKipVisits(baseEntityId, currentPageNo, new OnKipVisitsLoadedCallback() {

                @Override
                public void onKipVisitsLoaded(@NonNull List<KipOpdVisitSummary> opdVisitSummaries) {
                    updatePageCounter();

                    ArrayList<Pair<YamlConfigWrapper, Facts>> items = new ArrayList<>();
                    populateKipWrapperDataAndFacts(opdVisitSummaries, items);
                    onFinishedCallback.onKipFinished(opdVisitSummaries, items);
                }
            });

        }
    }

    private void updatePageCounter() {
        String pageCounterTemplate = getString(org.smartregister.opd.R.string.current_page_of_total_pages);

        OpdProfileVisitsFragmentContract.View profileView = getProfileView();
        if (profileView != null && pageCounterTemplate != null) {
            profileView.showPageCountText(String.format(pageCounterTemplate, (currentPageNo + 1), totalPages));

            profileView.showPreviousPageBtn(currentPageNo > 0);
            profileView.showNextPageBtn(currentPageNo < (totalPages - 1));
        }
    }

    @Override
    public void onNextPageClicked() {
        if (currentPageNo < totalPages && getProfileView() != null && getProfileView().getClientBaseEntityId() != null) {
            currentPageNo++;

            loadKipVisits(getProfileView().getClientBaseEntityId(), new OnKipFinishedCallback() {
                @Override
                public void onKipFinished(@NonNull List<KipOpdVisitSummary> opdVisitSummaries, @NonNull ArrayList<Pair<YamlConfigWrapper, Facts>> items) {
                    if (getProfileView() != null) {
                        getProfileView().displayKipVisits(opdVisitSummaries, items);
                    }
                }
            });
        }
    }

    @Override
    public void onPreviousPageClicked() {
        if (currentPageNo > 0 && getProfileView() != null && getProfileView().getClientBaseEntityId() != null) {
            currentPageNo--;

            loadKipVisits(getProfileView().getClientBaseEntityId(), new OnKipFinishedCallback() {
                @Override
                public void onKipFinished(@NonNull List<KipOpdVisitSummary> opdVisitSummaries, @NonNull ArrayList<Pair<YamlConfigWrapper, Facts>> items) {
                    if (getProfileView() != null) {
                        getProfileView().displayKipVisits(opdVisitSummaries, items);
                    }
                }
            });
        }
    }

    @Override
    public void loadPageCounter(@NonNull String baseEntityId) {
        if (mProfileInteractor != null) {
            mProfileInteractor.fetchVisitsPageCount(baseEntityId, visitsPageCount -> {
                totalPages = visitsPageCount;
                updatePageCounter();
            });
        }
    }

    @NonNull
    private Facts generateOpdVisitSummaryFact(@NonNull KipOpdVisitSummary opdVisitSummary) {
        Facts facts = new Facts();

        if (opdVisitSummary.getVisitDate() != null) {
            OpdFactsUtil.putNonNullFact(facts, OpdConstants.FactKey.OpdVisit.VISIT_DATE, OpdUtils.convertDate(opdVisitSummary.getVisitDate(), OpdConstants.DateFormat.d_MMM_yyyy_hh_mm_ss));
        }

//        OpdFactsUtil.putNonNullFact(facts, OpdConstants.FactKey.OpdVisit.TEST_TYPE, opdVisitSummary.getTestType());
        OpdFactsUtil.putNonNullFact(facts, OpdConstants.FactKey.OpdVisit.DIAGNOSIS, opdVisitSummary.getDiagnosis());
        OpdFactsUtil.putNonNullFact(facts, OpdConstants.FactKey.OpdVisit.DIAGNOSIS_TYPE, opdVisitSummary.getDiagnosisType());
        OpdFactsUtil.putNonNullFact(facts, OpdConstants.FactKey.OpdVisit.DIAGNOSIS_SAME, opdVisitSummary.getIsDiagnosisSame());
        OpdFactsUtil.putNonNullFact(facts, OpdConstants.FactKey.OpdVisit.TREATMENT_TYPE_SPECIFY, opdVisitSummary.getTreatmentTypeSpecify());
        OpdFactsUtil.putNonNullFact(facts, OpdConstants.FactKey.OpdVisit.TREATMENT_TYPE, OpdUtils.cleanStringArray(opdVisitSummary.getTreatmentType()));


        // Put the diseases text
        String diseasesText = generateDiseasesText(opdVisitSummary);
        OpdFactsUtil.putNonNullFact(facts, OpdConstants.FactKey.OpdVisit.DISEASE_CODE, diseasesText);

        // Put the treatment text
        HashMap<String, OpdVisitSummaryResultModel.Treatment> treatments = opdVisitSummary.getTreatments();
        String medicationText = generateMedicationText(treatments);
        OpdFactsUtil.putNonNullFact(facts, OpdConstants.FactKey.OpdVisit.TREATMENT, medicationText);

        // Put the test text
        HashMap<String, List<OpdVisitSummaryResultModel.Test>> test = opdVisitSummary.getTests();
        String testText = generateTestText(test);

        OpdFactsUtil.putNonNullFact(facts, OpdConstants.FactKey.OpdVisit.TESTS, testText);

        OpdFactsUtil.putNonNullFact(facts, KipOpdConstants.FactKey.OpdVisit.ANTIGEN_ADMINISTERED_LAST, opdVisitSummary.getAntigenAdminLast());
        OpdFactsUtil.putNonNullFact(facts, KipOpdConstants.FactKey.OpdVisit.DADMINISTRATION_DATE, opdVisitSummary.getAdminDate());
        OpdFactsUtil.putNonNullFact(facts, KipOpdConstants.FactKey.OpdVisit.ANTIGEN_MISSED, opdVisitSummary.getAntigenMissed());
        OpdFactsUtil.putNonNullFact(facts, KipOpdConstants.FactKey.OpdVisit.RETURN_DATE, opdVisitSummary.getReturnDate());


        OpdFactsUtil.putNonNullFact(facts, KipOpdConstants.FactKey.OpdVisit.CHV_PHONE_NUMBER, opdVisitSummary.getChvPhoneNumber());
        OpdFactsUtil.putNonNullFact(facts, KipOpdConstants.FactKey.OpdVisit.CHW_NAME, opdVisitSummary.getChvName());

        OpdFactsUtil.putNonNullFact(facts, KipOpdConstants.FactKey.OpdVisit.PHONE_TRACING, opdVisitSummary.getPhoneTracing());
        OpdFactsUtil.putNonNullFact(facts, KipOpdConstants.FactKey.OpdVisit.PHYSICAL_TRACING, opdVisitSummary.getPhysicalTracing());

        OpdFactsUtil.putNonNullFact(facts, KipOpdConstants.FactKey.OpdVisit.PHONE_TRACING_OUTCOME, opdVisitSummary.getPhoneTracingOutcome());
        OpdFactsUtil.putNonNullFact(facts, KipOpdConstants.FactKey.OpdVisit.PHYSICAL_TRACING_OUTCOME, opdVisitSummary.getPhysicalTracingOutcome());
        OpdFactsUtil.putNonNullFact(facts, KipOpdConstants.FactKey.OpdVisit.HOME_ADMINISTRATION_DATE, opdVisitSummary.getHomeAdminDate());
        OpdFactsUtil.putNonNullFact(facts, KipOpdConstants.FactKey.OpdVisit.OTHER_FACILITY_ADMINISTRATION_DATE, opdVisitSummary.getOtherFacilityAdminDate());
        OpdFactsUtil.putNonNullFact(facts, KipOpdConstants.FactKey.OpdVisit.OTHER_FACILITY_NAME, opdVisitSummary.getOtherFacilityName());
        OpdFactsUtil.putNonNullFact(facts, KipOpdConstants.FactKey.OpdVisit.DID_NOT_CONDUCT_A_PHYSICAL_VISIT, opdVisitSummary.getReasonNotConductPhysicalVisit());
        OpdFactsUtil.putNonNullFact(facts, KipOpdConstants.FactKey.OpdVisit.DATE_TO_CONFIRM_VACCINATION, opdVisitSummary.getDateToConfirmVaccination());



        // Add translate-able labels
        setLabelsInFacts(facts);

        return facts;
    }

    private void setLabelsInFacts(@NonNull Facts facts) {

        OpdFactsUtil.putNonNullFact(facts, KipOpdConstants.FactKey.OpdVisit.ANTIGEN_ADMINISTERED_LAST_LABEL, getString(R.string.antigen_administered_last_label));
        OpdFactsUtil.putNonNullFact(facts, KipOpdConstants.FactKey.OpdVisit.DADMINISTRATION_DATE_LABEL, getString(R.string.administration_date_label));
        OpdFactsUtil.putNonNullFact(facts, KipOpdConstants.FactKey.OpdVisit.ANTIGEN_MISSED_LABEL, getString(R.string.antigen_missed_label));
        OpdFactsUtil.putNonNullFact(facts, KipOpdConstants.FactKey.OpdVisit.RETURN_DATE_LABEL, getString(R.string.return_date_label));



        OpdFactsUtil.putNonNullFact(facts, KipOpdConstants.FactKey.OpdVisit.CHV_PHONE_NUMBER_LABEL, getString(R.string.chv_phone_number_label));
        OpdFactsUtil.putNonNullFact(facts, KipOpdConstants.FactKey.OpdVisit.CHW_NAME_LABEL, getString(R.string.chv_name_label));

        OpdFactsUtil.putNonNullFact(facts, KipOpdConstants.FactKey.OpdVisit.PHONE_TRACING_LABEL, getString(R.string.phone_tracing_label));
        OpdFactsUtil.putNonNullFact(facts, KipOpdConstants.FactKey.OpdVisit.PHYSICAL_TRACING_LABEL, getString(R.string.physical_tracing_label));

        OpdFactsUtil.putNonNullFact(facts, KipOpdConstants.FactKey.OpdVisit.TRACING_OUTCOME_LABEL, getString(R.string.tracing_outcome_label));
        OpdFactsUtil.putNonNullFact(facts, KipOpdConstants.FactKey.OpdVisit.HOME_ADMINISTRATION_DATE_LABEL, getString(R.string.home_administration_date_label));
        OpdFactsUtil.putNonNullFact(facts, KipOpdConstants.FactKey.OpdVisit.OTHER_FACILITY_ADMINISTRATION_DATE_LABEL, getString(R.string.other_facility_administration_date_label));
        OpdFactsUtil.putNonNullFact(facts, KipOpdConstants.FactKey.OpdVisit.OTHER_FACILITY_NAME_LABEL, getString(R.string.other_facility_name_label));
        OpdFactsUtil.putNonNullFact(facts, KipOpdConstants.FactKey.OpdVisit.DID_NOT_CONDUCT_A_PHYSICAL_VISIT, getString(R.string.did_not_conduct_a_visit_label));
        OpdFactsUtil.putNonNullFact(facts, KipOpdConstants.FactKey.OpdVisit.DATE_TO_CONFIRM_VACCINATION_LABEL, getString(R.string.date_to_confirm_vaccination_label));
    }

    @Nullable
    @Override
    public KipOpdProfileVisitsFragmentContract.View getProfileView() {
        if (mProfileView != null) {
            return mProfileView.get();
        } else {
            return null;
        }
    }

    @Nullable
    public String getString(@StringRes int stringId) {
        KipOpdProfileVisitsFragmentContract.View profileView = getProfileView();
        if (profileView != null) {
            return profileView.getString(stringId);
        }

        return null;
    }

    @Override
    public void populateKipWrapperDataAndFacts(@NonNull List<KipOpdVisitSummary> opdVisitSummaries, @NonNull ArrayList<Pair<YamlConfigWrapper, Facts>> items) {
        for (KipOpdVisitSummary opdVisitSummary : opdVisitSummaries) {
            Facts facts = generateOpdVisitSummaryFact(opdVisitSummary);
            Iterable<Object> ruleObjects = null;

            try {
                ruleObjects = OpdLibrary.getInstance().readYaml(FilePath.FILE.OPD_VISIT_ROW);
            } catch (IOException e) {
                Timber.e(e);
            }

            if (ruleObjects != null) {
                for (Object ruleObject : ruleObjects) {
                    YamlConfig yamlConfig = (YamlConfig) ruleObject;
                    if (yamlConfig.getGroup() != null) {
                        items.add(new Pair<>(new YamlConfigWrapper(yamlConfig.getGroup(), null, null), facts));
                    }

                    if (yamlConfig.getSubGroup() != null) {
                        items.add(new Pair<>(new YamlConfigWrapper(null, yamlConfig.getSubGroup(), null), facts));
                    }

                    List<YamlConfigItem> configItems = yamlConfig.getFields();

                    if (configItems != null) {
                        for (YamlConfigItem configItem : configItems) {
                            String relevance = configItem.getRelevance();
                            if (relevance != null && OpdLibrary.getInstance().getOpdRulesEngineHelper()
                                    .getRelevance(facts, relevance)) {
                                YamlConfigWrapper yamlConfigWrapper = new YamlConfigWrapper(null, null, configItem);
                                items.add(new Pair<>(yamlConfigWrapper, facts));
                            }
                        }
                    }
                }
            }
        }
    }
}