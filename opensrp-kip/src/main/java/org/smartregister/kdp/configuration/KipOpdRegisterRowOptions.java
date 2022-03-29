package org.smartregister.kdp.configuration;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.kdp.R;
import org.smartregister.kdp.application.KipApplication;
import org.smartregister.kdp.util.KipConstants;
import org.smartregister.opd.configuration.OpdRegisterRowOptions;
import org.smartregister.opd.holders.OpdRegisterViewHolder;
import org.smartregister.opd.utils.OpdConstants;
import org.smartregister.opd.utils.OpdDbConstants;
import org.smartregister.opd.utils.OpdUtils;
import org.smartregister.view.contract.SmartRegisterClient;

import java.util.Date;
import java.util.Map;

public class KipOpdRegisterRowOptions implements OpdRegisterRowOptions {

    @Override
    public boolean isDefaultPopulatePatientColumn() {
        return false;
    }

    @Override
    public void populateClientRow(@NonNull Cursor cursor, @NonNull CommonPersonObjectClient commonPersonObjectClient
            , @NonNull SmartRegisterClient smartRegisterClient, @NonNull OpdRegisterViewHolder opdRegisterViewHolder) {
        Map<String, String> columnMaps = commonPersonObjectClient.getColumnmaps();

        String strVisitEndDate = columnMaps.get(OpdDbConstants.Column.OpdDetails.CURRENT_VISIT_END_DATE);

        Button dueButton = opdRegisterViewHolder.dueButton;
        Date visitEndDate = OpdUtils.convertStringToDate(OpdConstants.DateFormat.YYYY_MM_DD_HH_MM_SS, strVisitEndDate);
        if (strVisitEndDate != null) {
//            Date visitEndDate = OpdUtils.convertStringToDate(OpdConstants.DateFormat.YYYY_MM_DD_HH_MM_SS, strVisitEndDate);
            if (visitEndDate != null && KipApplication.getInstance().isPatientInTreatedState(visitEndDate)) {
                String treatedTime = OpdUtils.convertDate(visitEndDate, KipConstants.DateFormat.YYYY_MM_DD_H_MM_A);

                Context context = dueButton.getContext();
                dueButton.setText(String.format(context.getResources().getString(R.string.treated_at_time), treatedTime));
                dueButton.setTextColor(dueButton.getContext().getResources().getColor(R.color.green_overlay));
                dueButton.setAllCaps(false);
                dueButton.setBackgroundResource(R.color.transparent);
                return;
            }

            else if (KipApplication.getInstance().isPatientInTreatedStateAfter30Days(visitEndDate)){
                dueButton.setText(R.string.empty_text);
            }
        } else {

        String booleanString = columnMaps.get(OpdDbConstants.Column.OpdDetails.PENDING_DIAGNOSE_AND_TREAT);

        if (parseBoolean(booleanString)) {
            dueButton.setText(R.string.diagnose_and_treat);
        }

        else {
            dueButton.setText(R.string.listed_text);
        }

        dueButton.setBackgroundResource(R.color.transparent);

        }

    }

    private boolean parseBoolean(@Nullable String booleanString) {
        return (!TextUtils.isEmpty(booleanString) && (
                (booleanString.length() == 1 && "1".equals(booleanString))
                        || (booleanString.length() > 1 && Boolean.parseBoolean(booleanString))));
    }

    @Override
    public boolean isCustomViewHolder() {
        return false;
    }

    @Nullable
    @Override
    public OpdRegisterViewHolder createCustomViewHolder(@NonNull View itemView) {
        return null;
    }

    @Override
    public boolean useCustomViewLayout() {
        return false;
    }

    @Override
    public int getCustomViewLayoutId() {
        return 0;
    }
}