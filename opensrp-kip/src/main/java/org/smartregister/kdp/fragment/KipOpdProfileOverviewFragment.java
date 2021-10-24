package org.smartregister.kdp.fragment;

import android.os.Bundle;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.apache.commons.lang3.ArrayUtils;
import org.json.JSONException;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.kdp.R;
import org.smartregister.kdp.activity.KipOpdProfileActivity;
import org.smartregister.kdp.contract.KipOpdProfileOverviewFragmentContract;
import org.smartregister.kdp.presenter.KipOpdProfileOverviewFragmentPresenter;
import org.smartregister.kdp.service.FormatClientOpensrpId;
import org.smartregister.kdp.util.KipConstants;
import org.smartregister.opd.activity.BaseOpdProfileActivity;
import org.smartregister.opd.adapter.OpdProfileOverviewAdapter;
import org.smartregister.opd.contract.OpdProfileOverviewFragmentContract;
import org.smartregister.opd.fragment.OpdProfileOverviewFragment;
import org.smartregister.opd.utils.OpdConstants;
import org.smartregister.opd.utils.OpdDbConstants;

public class KipOpdProfileOverviewFragment extends OpdProfileOverviewFragment implements KipOpdProfileOverviewFragmentContract.View {
    private ConstraintLayout mainView;
    private LinearLayout opdCheckinSectionLayout;
    private Button checkInDiagnoseAndTreatBtn;
    private TextView opdCheckedInTv;
    private Button checkInUpdateDefaulterFormBtn,checkInRecordDefaulterFormBtn, checkInRecordCovidDefaulterFormBtn, checkInUpdateCovidDefaulterFormBtn;
    CommonPersonObjectClient commonPersonObjectClient;
    private String baseEntityId;
    private OpdProfileOverviewFragmentContract.Presenter presenter;


    public static KipOpdProfileOverviewFragment newInstance(Bundle bundle) {
        Bundle args = bundle;
        KipOpdProfileOverviewFragment fragment = new KipOpdProfileOverviewFragment();
        if (args == null) {
            args = new Bundle();
        }
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void onCreation() {
        presenter = new KipOpdProfileOverviewFragmentPresenter(this);

        if (getArguments() != null) {
            CommonPersonObjectClient commonPersonObjectClient = (CommonPersonObjectClient) getArguments()
                    .getSerializable(OpdConstants.IntentKey.CLIENT_OBJECT);

            if (commonPersonObjectClient != null) {
                presenter.setClient(commonPersonObjectClient);
                baseEntityId = commonPersonObjectClient.getCaseId();
            }
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.opd_fragment_profile_overview, container, false);
        mainView = view.findViewById(R.id.main_view);
        opdCheckinSectionLayout = mainView.findViewById(R.id.ll_opdFragmentProfileOverview_checkinLayout);
        opdCheckedInTv = mainView.findViewById(R.id.tv_opdFragmentProfileOverview_checkedInTitle);
        checkInDiagnoseAndTreatBtn = mainView.findViewById(R.id.btn_opdFragmentProfileOverview_diagnoseAndTreat);
        checkInRecordDefaulterFormBtn = view.findViewById(R.id.btn_opdFragmentProfileOverview_record_defaulter_form);
        checkInRecordCovidDefaulterFormBtn = view.findViewById(R.id.btn_opdFragmentProfileOverview_RecordCovidDefaulterForm);
        checkInUpdateCovidDefaulterFormBtn = view.findViewById(R.id.btn_opdFragmentProfileOverview_UpdateCovidDefaulterForm);
        checkInUpdateDefaulterFormBtn = view.findViewById(R.id.btn_opdFragmentProfileOverview_update_defaulter_form);
        commonPersonObjectClient = (CommonPersonObjectClient) getArguments().getSerializable(OpdConstants.IntentKey.CLIENT_OBJECT);

        return view;
    }

//    private void checkInActionDialog(String form) {
//        if (getActivity() != null) {
//            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//            builder.setTitle("Select an action to proceed");
//            builder.setItems(formsToOpen(form), (dialog, position) -> {
//                FragmentActivity activity = getActivity();
//                if (position == 0) {
//                    if (activity instanceof KipOpdProfileActivity) {
//                        ((KipOpdProfileActivity) activity).openDefaulterForms(form);
//                    }
//                }
//                if (position==1) {
//                    if (activity instanceof KipOpdProfileActivity && !((KipOpdProfileActivity) activity).getCovidDefaulter()) {
//                        ((KipOpdProfileActivity) activity).openCovid19Forms(form);
//                    }
//                }
//
//                if (position == 1 && ((KipOpdProfileActivity) activity).getCovidDefaulter()) {
//                    if (activity instanceof KipOpdProfileActivity) {
//                        ((KipOpdProfileActivity) activity).openUpdateCovid19Forms(form);
//                    }
//                }
//
//            });
//
//            AlertDialog dialog = builder.create();
//            dialog.show();
//        }
//    }

    private void checkInActionDialog(String form) {
        if (getActivity() != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Select an action to proceed");
            builder.setItems(formsToOpen(form), (dialog, position) -> {
                FragmentActivity activity = getActivity();
                if (position == 0) {
                    if (activity instanceof KipOpdProfileActivity) {
                        ((KipOpdProfileActivity) activity).openDefaulterForms(form);
                    }
                }
                if (position == 1) {
                    if (activity instanceof KipOpdProfileActivity) {
                        ((KipOpdProfileActivity) activity).openDefaulterForms(form);
                    }
                }

            });

            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    private String[] formsToOpen(String form) {
        FragmentActivity activity = getActivity();
        String[] forms = new String[]{""};

        updateFormToDisplay(form, forms);

        return forms;
    }

    private void updateFormToDisplay(String form, String[] forms) {

        if (form.equalsIgnoreCase(KipConstants.JSON_FORM.OPD_UPDATE_DEFAULTER_FORM )){
            forms[0] = getString(R.string.update_defaulter_form);
        } else {
            forms[0] = getString(R.string.record_defaulter_form);

        }
    }

//    private String[] formsToOpen(String form) {
//        FragmentActivity activity = getActivity();
//        String[] forms = new String[]{"",""};
//
//
//        updateCovid19FormToDisplay(form, forms);
//
//        updateFormToDisplay(form, forms);
//
//
//        if (activity instanceof KipOpdProfileActivity){
//            if (!((KipOpdProfileActivity) activity).getCovid19Defaulter()) {
//                forms = ArrayUtils.remove(forms, 1);
//            }
//        }
//
//        return forms;
//    }
//
//    private void updateFormToDisplay(String form, String[] forms) {
//
//        if (form.equalsIgnoreCase(KipConstants.JSON_FORM.OPD_UPDATE_DEFAULTER_FORM )){
//            forms[0] = getString(R.string.update_defaulter_form);
//        } else {
//
//            forms[0] = getString(R.string.record_defaulter_form);
//
//        }
//    }

    private void updateCovid19FormToDisplay(String form, String[] forms){
        if (form.equalsIgnoreCase(KipConstants.JSON_FORM.OPD_UPDATE_COVID_DEFAULTER_FORM)){
            forms[1] = getString(R.string.update_covid_defaulter_form);
        }
        else {
            forms[1] = getString(R.string.record_covid_defaulter_form);
        }
    }

    @Override
    protected void onResumption() {
        if (baseEntityId != null) {
            presenter.loadOverviewFacts(baseEntityId, (facts, yamlConfigListGlobal) -> {
                if (getActivity() != null && facts != null && yamlConfigListGlobal != null) {
                    Boolean isPendingDiagnoseAndTreat = facts.get(OpdDbConstants.Column.OpdDetails.PENDING_DIAGNOSE_AND_TREAT);
                    isPendingDiagnoseAndTreat = isPendingDiagnoseAndTreat == null ? Boolean.FALSE : isPendingDiagnoseAndTreat;

                    FragmentActivity activity = getActivity();

//                    if (isPendingDiagnoseAndTreat ) {
//                        opdCheckedInTv.setText(R.string.opd_checked_in);
//
//                        showRecordCovidDefaulterFormBtn();
//
//                        showDiagnoseAndTreatBtn();
//
//                            if (((KipOpdProfileActivity) activity).getLastVaccineGiven()) {
//                                checkInDiagnoseAndTreatBtn.setVisibility(View.GONE);
//                                checkInRecordDefaulterFormBtn.setVisibility(View.GONE);
//                                checkInUpdateDefaulterFormBtn.setVisibility(View.VISIBLE);
//                                showUpdateDefaulterFormBtn();
//                            }
//                            if (((KipOpdProfileActivity) activity).getCovidDefaulter()) {
//                                checkInUpdateCovidDefaulterFormBtn.setVisibility(View.VISIBLE);
//                                checkInDiagnoseAndTreatBtn.setVisibility(View.GONE);
//                                checkInRecordCovidDefaulterFormBtn.setVisibility(View.GONE);
//                                showUpdateCovidDefaulterFormBtn();
//                            }
//
//                    } else  {
//                        opdCheckedInTv.setText(R.string.defaulter);
//                        showCheckInBtn();
//                    }

                    if (isPendingDiagnoseAndTreat ) {
                        opdCheckedInTv.setText(R.string.opd_checked_in);
                        showDiagnoseAndTreatBtn();
                        if (((KipOpdProfileActivity) activity).getLastVaccineGiven()){
                            checkInDiagnoseAndTreatBtn.setVisibility(View.GONE);
                            checkInRecordDefaulterFormBtn.setVisibility(View.GONE);
                            checkInUpdateDefaulterFormBtn.setVisibility(View.VISIBLE);
                            showUpdateDefaulterFormBtn();
                        } else {
                            checkInUpdateDefaulterFormBtn.setVisibility(View.GONE);
                            opdCheckedInTv.setText(R.string.opd_checked_in);
//                            showCheckInBtn();
                        }

                    } else  {
                        opdCheckedInTv.setText(R.string.defaulter);
                        showCheckInBtn();
                    }

                    OpdProfileOverviewAdapter adapter = new OpdProfileOverviewAdapter(getActivity(), yamlConfigListGlobal, facts);
                    adapter.notifyDataSetChanged();
                    // set up the RecyclerView
                    RecyclerView recyclerView = getActivity().findViewById(R.id.profile_overview_recycler);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    recyclerView.setAdapter(adapter);
                }
            });
        }
    }

    private void showCheckInBtn() {
        if (getActivity() != null) {
            opdCheckinSectionLayout.setVisibility(View.VISIBLE);
            checkInDiagnoseAndTreatBtn.setText(R.string.start_tracing);
            checkInDiagnoseAndTreatBtn.setBackgroundResource(R.drawable.check_in_btn_overview_bg);
            checkInDiagnoseAndTreatBtn.setTextColor(getActivity().getResources().getColorStateList(R.color.check_in_btn_overview_text_color));
            checkInDiagnoseAndTreatBtn.setOnClickListener(v -> {
                FragmentActivity activity = getActivity();
//                FormatSqlDateRepository update = KipApplication.getInstance().formatSqlDateRepository();
//                update.updateInvalidClients();
//                FormatClientOpensrpId clientDate = new FormatClientOpensrpId();
//                try {
//                    clientDate.getClientsJson();
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }

                if (activity instanceof BaseOpdProfileActivity) {
                    ((BaseOpdProfileActivity) activity).openCheckInForm();
                }
            });
        }
    }

    private void showDiagnoseAndTreatBtn() {
        if (getActivity() != null) {
            opdCheckinSectionLayout.setVisibility(View.VISIBLE);
            checkInDiagnoseAndTreatBtn.setText(R.string.diagnose_and_treat);
            checkInDiagnoseAndTreatBtn.setBackgroundResource(R.drawable.diagnose_treat_bg);
            checkInDiagnoseAndTreatBtn.setTextColor(getActivity().getResources().getColor(R.color.diagnose_treat_txt_color));
            checkInDiagnoseAndTreatBtn.setOnClickListener(v -> {
                checkInActionDialog(KipConstants.JSON_FORM.OPD_RECORD_DEFAULTER_FORM);
            });
        }
    }

    private void showUpdateDefaulterFormBtn() {
        if (getActivity() != null) {
            opdCheckinSectionLayout.setVisibility(View.VISIBLE);
            checkInUpdateDefaulterFormBtn.setText(R.string.diagnose_and_treat);
            checkInUpdateDefaulterFormBtn.setBackgroundResource(R.drawable.diagnose_treat_bg);
            checkInUpdateDefaulterFormBtn.setTextColor(getActivity().getResources().getColor(R.color.diagnose_treat_txt_color));
            checkInUpdateDefaulterFormBtn.setOnClickListener(v -> {
                checkInActionDialog(KipConstants.JSON_FORM.OPD_UPDATE_DEFAULTER_FORM);
            });
        }
    }

    private void showRecordCovidDefaulterFormBtn() {
        if (getActivity() != null) {
            opdCheckinSectionLayout.setVisibility(View.VISIBLE);
            checkInRecordCovidDefaulterFormBtn.setText(R.string.diagnose_and_treat);
            checkInRecordCovidDefaulterFormBtn.setBackgroundResource(R.drawable.diagnose_treat_bg);
            checkInRecordCovidDefaulterFormBtn.setTextColor(getActivity().getResources().getColor(R.color.diagnose_treat_txt_color));
            checkInRecordCovidDefaulterFormBtn.setOnClickListener(v -> {
                checkInActionDialog(KipConstants.JSON_FORM.OPD_COVID_DEFAULTER_FORM);
            });
        }
    }

    private void showUpdateCovidDefaulterFormBtn() {
        if (getActivity() != null) {
            opdCheckinSectionLayout.setVisibility(View.VISIBLE);
            checkInUpdateCovidDefaulterFormBtn.setText(R.string.diagnose_and_treat);
            checkInUpdateCovidDefaulterFormBtn.setBackgroundResource(R.drawable.diagnose_treat_bg);
            checkInUpdateCovidDefaulterFormBtn.setTextColor(getActivity().getResources().getColor(R.color.diagnose_treat_txt_color));
            checkInUpdateCovidDefaulterFormBtn.setOnClickListener(v -> {
                checkInActionDialog(KipConstants.JSON_FORM.OPD_UPDATE_COVID_DEFAULTER_FORM);
            });
        }
    }

    @Override
    public void onActionReceive() {
        onCreation();
        onResumption();
    }
}
