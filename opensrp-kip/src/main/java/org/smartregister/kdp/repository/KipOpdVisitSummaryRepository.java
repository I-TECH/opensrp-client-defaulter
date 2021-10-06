package org.smartregister.kdp.repository;

import androidx.annotation.NonNull;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.kdp.pojo.KipOpdVisitSummary;
import org.smartregister.kdp.util.KipConstants;
import org.smartregister.opd.pojo.OpdVisitSummary;
import org.smartregister.opd.repository.OpdVisitSummaryRepository;
import org.smartregister.opd.utils.OpdConstants;
import org.smartregister.opd.utils.OpdDbConstants;
import org.smartregister.opd.utils.OpdUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

import timber.log.Timber;

public class KipOpdVisitSummaryRepository extends OpdVisitSummaryRepository {

    @Override
    public String[] visitSummaryColumns() {
        return new String[]{
                OpdDbConstants.Table.OPD_VISIT + "." + OpdDbConstants.Column.OpdVisit.VISIT_DATE,
                OpdDbConstants.Table.OPD_VISIT + "." + OpdDbConstants.Column.OpdVisit.ID,

//                KEPI defaulter Tracing

                KipConstants.DbConstants.Tables.RECORD_DEFAULTER_FORM + "." + KipConstants.DbConstants.Columns.RecordDefaulerForm.ANTIGEN_ADMINISTERED_LAST,
                KipConstants.DbConstants.Tables.RECORD_DEFAULTER_FORM + "." + KipConstants.DbConstants.Columns.RecordDefaulerForm.ADMINISTRATION_DATE,
                KipConstants.DbConstants.Tables.RECORD_DEFAULTER_FORM + "." + KipConstants.DbConstants.Columns.RecordDefaulerForm.MISSED_VACCINE,
                KipConstants.DbConstants.Tables.RECORD_DEFAULTER_FORM + "." + KipConstants.DbConstants.Columns.RecordDefaulerForm.RETURN_DATE,
                KipConstants.DbConstants.Tables.RECORD_DEFAULTER_FORM + "." + KipConstants.DbConstants.Columns.RecordDefaulerForm.CHV_NAME,
                KipConstants.DbConstants.Tables.RECORD_DEFAULTER_FORM + "." + KipConstants.DbConstants.Columns.RecordDefaulerForm.CHV_PHONE_NUMBER,
                KipConstants.DbConstants.Tables.RECORD_DEFAULTER_FORM + "." + KipConstants.DbConstants.Columns.RecordDefaulerForm.BIRTH_DOSE_ANTIGEN,
                KipConstants.DbConstants.Tables.RECORD_DEFAULTER_FORM + "." + KipConstants.DbConstants.Columns.RecordDefaulerForm.SIX_WKS_ANTIGEN,
                KipConstants.DbConstants.Tables.RECORD_DEFAULTER_FORM + "." + KipConstants.DbConstants.Columns.RecordDefaulerForm.TEN_WKS_ANTIGEN,
                KipConstants.DbConstants.Tables.RECORD_DEFAULTER_FORM + "." + KipConstants.DbConstants.Columns.RecordDefaulerForm.FOURTEEN_WKS_ANTIGEN,
                KipConstants.DbConstants.Tables.RECORD_DEFAULTER_FORM + "." + KipConstants.DbConstants.Columns.RecordDefaulerForm.NINE_MONTHS_ANTIGEN,
                KipConstants.DbConstants.Tables.RECORD_DEFAULTER_FORM + "." + KipConstants.DbConstants.Columns.RecordDefaulerForm.EIGHTEEN_MONTHS_ANTIGEN,

                KipConstants.DbConstants.Tables.UPDATE_DEFAULTER_FORM_TABLE + "." + KipConstants.DbConstants.Columns.UpdateDefaulterForm.PHYSICAL_TRACING,
                KipConstants.DbConstants.Tables.UPDATE_DEFAULTER_FORM_TABLE + "." + KipConstants.DbConstants.Columns.UpdateDefaulterForm.PHONE_TRACING,

                KipConstants.DbConstants.Tables.UPDATE_DEFAULTER_FORM_TABLE + "." + KipConstants.DbConstants.Columns.UpdateDefaulterForm.PHONE_TRACING_OUTCOME,
                KipConstants.DbConstants.Tables.UPDATE_DEFAULTER_FORM_TABLE + "." + KipConstants.DbConstants.Columns.UpdateDefaulterForm.PHYSICAL_TRACING_OUTCOME,
                KipConstants.DbConstants.Tables.UPDATE_DEFAULTER_FORM_TABLE + "." + KipConstants.DbConstants.Columns.UpdateDefaulterForm.HOME_ADMINISTRATION_DATE,
                KipConstants.DbConstants.Tables.UPDATE_DEFAULTER_FORM_TABLE + "." + KipConstants.DbConstants.Columns.UpdateDefaulterForm.OTHER_FACILITY_ADMINISTRATION_DATE,
                KipConstants.DbConstants.Tables.UPDATE_DEFAULTER_FORM_TABLE + "." + KipConstants.DbConstants.Columns.UpdateDefaulterForm.OTHER_FACILITY_NAME,
                KipConstants.DbConstants.Tables.UPDATE_DEFAULTER_FORM_TABLE + "." + KipConstants.DbConstants.Columns.UpdateDefaulterForm.DATE_TO_CONFIRM_VACCINATION,

//                Covid 19 Defaulter tracing
                KipConstants.DbConstants.Tables.RECORD_COVID_DEFAULTER_FORM + "." + KipConstants.DbConstants.Columns.RecordCovidDefaulerForm.COVID_ANTIGEN_ADMINISTERED_LAST,
                KipConstants.DbConstants.Tables.RECORD_COVID_DEFAULTER_FORM + "." + KipConstants.DbConstants.Columns.RecordCovidDefaulerForm.COVID_ADMINISTRATION_DATE,
                KipConstants.DbConstants.Tables.RECORD_COVID_DEFAULTER_FORM + "." + KipConstants.DbConstants.Columns.RecordCovidDefaulerForm.COVID_MISSED_VACCINE,
                KipConstants.DbConstants.Tables.RECORD_COVID_DEFAULTER_FORM + "." + KipConstants.DbConstants.Columns.RecordCovidDefaulerForm.COVID_RETURN_DATE,
                KipConstants.DbConstants.Tables.RECORD_COVID_DEFAULTER_FORM + "." + KipConstants.DbConstants.Columns.RecordCovidDefaulerForm.COVID_CHV_NAME,
                KipConstants.DbConstants.Tables.RECORD_COVID_DEFAULTER_FORM + "." + KipConstants.DbConstants.Columns.RecordCovidDefaulerForm.COVID_CHV_PHONE_NUMBER,

                KipConstants.DbConstants.Tables.UPDATE_COVID_DEFAULTER_FORM_TABLE + "." + KipConstants.DbConstants.Columns.UpdateCovidDefaulterForm.COVID_PHYSICAL_TRACING,
                KipConstants.DbConstants.Tables.UPDATE_COVID_DEFAULTER_FORM_TABLE + "." + KipConstants.DbConstants.Columns.UpdateCovidDefaulterForm.COVID_PHONE_TRACING,

                KipConstants.DbConstants.Tables.UPDATE_COVID_DEFAULTER_FORM_TABLE + "." + KipConstants.DbConstants.Columns.UpdateCovidDefaulterForm.COVID_PHONE_TRACING_OUTCOME,
                KipConstants.DbConstants.Tables.UPDATE_COVID_DEFAULTER_FORM_TABLE + "." + KipConstants.DbConstants.Columns.UpdateCovidDefaulterForm.COVID_PHYSICAL_TRACING_OUTCOME,
                KipConstants.DbConstants.Tables.UPDATE_COVID_DEFAULTER_FORM_TABLE + "." + KipConstants.DbConstants.Columns.UpdateCovidDefaulterForm.COVID_HOME_ADMINISTRATION_DATE,
                KipConstants.DbConstants.Tables.UPDATE_COVID_DEFAULTER_FORM_TABLE + "." + KipConstants.DbConstants.Columns.UpdateCovidDefaulterForm.COVID_OTHER_FACILITY_ADMINISTRATION_DATE,
                KipConstants.DbConstants.Tables.UPDATE_COVID_DEFAULTER_FORM_TABLE + "." + KipConstants.DbConstants.Columns.UpdateCovidDefaulterForm.COVID_OTHER_FACILITY_NAME,
                KipConstants.DbConstants.Tables.UPDATE_COVID_DEFAULTER_FORM_TABLE + "." + KipConstants.DbConstants.Columns.UpdateCovidDefaulterForm.COVID_DATE_TO_CONFIRM_VACCINATION,

        };
    }

    @NonNull
    public List<KipOpdVisitSummary> getKipOpdVisitSummaries(@NonNull String baseEntityId, int pageNo) {
        LinkedHashMap<String, KipOpdVisitSummary> opdVisitSummaries = new LinkedHashMap<>();

        Cursor mCursor = null;
        try {
            SQLiteDatabase db = getReadableDatabase();

            String[] visitIds = getVisitIds(baseEntityId, pageNo);
            String joinedIds = "'" + StringUtils.join(visitIds, "','") + "'";

            String query = "SELECT " + StringUtils.join(visitSummaryColumns(), ",") + " FROM " + OpdDbConstants.Table.OPD_VISIT +
                    " LEFT OUTER JOIN " + KipConstants.DbConstants.Tables.RECORD_DEFAULTER_FORM + " ON "
                    + KipConstants.DbConstants.Tables.RECORD_DEFAULTER_FORM + "." + KipConstants.DbConstants.Columns.RecordDefaulerForm.VISIT_ID + " = " + OpdDbConstants.Table.OPD_VISIT + "." + OpdDbConstants.Column.OpdVisit.ID +
                    " LEFT JOIN " + KipConstants.DbConstants.Tables.UPDATE_DEFAULTER_FORM_TABLE + " ON "
                    + KipConstants.DbConstants.Tables.UPDATE_DEFAULTER_FORM_TABLE + "." + KipConstants.DbConstants.Columns.UpdateDefaulterForm.VISIT_ID + " = " + OpdDbConstants.Table.OPD_VISIT + "." + OpdDbConstants.Column.OpdVisit.ID +
                    " LEFT OUTER JOIN " + KipConstants.DbConstants.Tables.RECORD_COVID_DEFAULTER_FORM + " ON "
                    + KipConstants.DbConstants.Tables.RECORD_COVID_DEFAULTER_FORM + "." + KipConstants.DbConstants.Columns.RecordCovidDefaulerForm.VISIT_ID + " = " + OpdDbConstants.Table.OPD_VISIT + "." + OpdDbConstants.Column.OpdVisit.ID +
                    " LEFT JOIN " + KipConstants.DbConstants.Tables.UPDATE_COVID_DEFAULTER_FORM_TABLE + " ON "
                    + KipConstants.DbConstants.Tables.UPDATE_COVID_DEFAULTER_FORM_TABLE + "." + KipConstants.DbConstants.Columns.UpdateCovidDefaulterForm.VISIT_ID + " = " + OpdDbConstants.Table.OPD_VISIT + "." + OpdDbConstants.Column.OpdVisit.ID +
                    " LEFT JOIN " + KipConstants.TABLE_NAME.ALL_CLIENTS + " ON "
                    + OpdDbConstants.Table.OPD_VISIT + "." + OpdDbConstants.Column.OpdVisit.BASE_ENTITY_ID + " = " + KipConstants.TABLE_NAME.ALL_CLIENTS + "." + OpdDbConstants.Column.OpdVisit.BASE_ENTITY_ID+
                    " WHERE " + OpdDbConstants.Table.OPD_VISIT + "." + OpdDbConstants.Column.OpdVisit.BASE_ENTITY_ID + " = '" + baseEntityId + "'"
                    + " AND " + OpdDbConstants.Table.OPD_VISIT + "." + OpdDbConstants.Column.OpdVisit.ID + " IN (" + joinedIds + ") " +
                    " ORDER BY " + OpdDbConstants.Table.OPD_VISIT + "." + OpdDbConstants.Column.OpdVisit.VISIT_DATE + " DESC";

            if (StringUtils.isNotBlank(baseEntityId)) {
                mCursor = db.rawQuery(query, null);

                if (mCursor != null) {
                    while (mCursor.moveToNext()) {
                        KipOpdVisitSummary visitSummaryResult = getVisitSummaryResult(mCursor);
                        String dateString = (new SimpleDateFormat(OpdConstants.DateFormat.YYYY_MM_DD_HH_MM_SS, Locale.ENGLISH)).format(visitSummaryResult.getVisitDate());

                        OpdVisitSummary existingOpdVisitSummary = opdVisitSummaries.get(dateString);
                        if (existingOpdVisitSummary != null) {
                            // Add any extra disease codes
                            String disease = visitSummaryResult.getDisease();
                            if (disease != null && !existingOpdVisitSummary.getDisease().contains(disease)) {
                                existingOpdVisitSummary.addDisease(disease);
                            }

                            // Add any extra treatments/medicines
                            OpdVisitSummary.Treatment treatment = visitSummaryResult.getTreatment();
                            if (treatment != null && treatment.getMedicine() != null && !existingOpdVisitSummary.getTreatments().containsKey(treatment.getMedicine())) {
                                existingOpdVisitSummary.addTreatment(treatment);
                            }

                            // Add any extra Tests
                            OpdVisitSummary.Test test = visitSummaryResult.getTest();
                            if (test != null && StringUtils.isNotBlank(test.getType())) {
                                existingOpdVisitSummary.addTest(test);
                            }
                        } else {
                            opdVisitSummaries.put(dateString, visitSummaryResult);
                        }
                    }

                }
            }


        } catch (Exception e) {
            Timber.e(e);
        } finally {
            if (mCursor != null) {
                mCursor.close();
            }
        }

        return new ArrayList<>(opdVisitSummaries.values());
    }

    @NonNull
    public KipOpdVisitSummary getVisitSummaryResult(@NonNull Cursor cursor) {
        KipOpdVisitSummary opdVisitModel = new KipOpdVisitSummary();

        opdVisitModel.setAntigenAdminLast(cursor.getString(cursor.getColumnIndex(KipConstants.DbConstants.Columns.RecordDefaulerForm.ANTIGEN_ADMINISTERED_LAST)));
        opdVisitModel.setAdminDate(cursor.getString(cursor.getColumnIndex(KipConstants.DbConstants.Columns.RecordDefaulerForm.ADMINISTRATION_DATE)));
        opdVisitModel.setAntigenMissed(cursor.getString(cursor.getColumnIndex(KipConstants.DbConstants.Columns.RecordDefaulerForm.MISSED_VACCINE)));
        opdVisitModel.setReturnDate(cursor.getString(cursor.getColumnIndex(KipConstants.DbConstants.Columns.RecordDefaulerForm.RETURN_DATE)));
        opdVisitModel.setChvName(cursor.getString(cursor.getColumnIndex(KipConstants.DbConstants.Columns.RecordDefaulerForm.CHV_NAME)));
        opdVisitModel.setChvPhoneNumber(cursor.getString(cursor.getColumnIndex(KipConstants.DbConstants.Columns.RecordDefaulerForm.CHV_PHONE_NUMBER)));
//        opdVisitModel.setBirthDoseAntigen(cursor.getString(cursor.getColumnIndex(KipConstants.DbConstants.Columns.RecordDefaulerForm.BIRTH_DOSE_ANTIGEN)));
//        opdVisitModel.setSixWksAntigen(cursor.getString(cursor.getColumnIndex(KipConstants.DbConstants.Columns.RecordDefaulerForm.SIX_WKS_ANTIGEN)));
//        opdVisitModel.setTenWksAntigen(cursor.getString(cursor.getColumnIndex(KipConstants.DbConstants.Columns.RecordDefaulerForm.TEN_WKS_ANTIGEN)));
//        opdVisitModel.setFourteenWksAntigen(cursor.getString(cursor.getColumnIndex(KipConstants.DbConstants.Columns.RecordDefaulerForm.FOURTEEN_WKS_ANTIGEN)));
//        opdVisitModel.setNineMonthsAntigen(cursor.getString(cursor.getColumnIndex(KipConstants.DbConstants.Columns.RecordDefaulerForm.NINE_MONTHS_ANTIGEN)));
//        opdVisitModel.setEighteenMonthsAntigen(cursor.getString(cursor.getColumnIndex(KipConstants.DbConstants.Columns.RecordDefaulerForm.EIGHTEEN_MONTHS_ANTIGEN)));


        opdVisitModel.setPhoneTracing(cursor.getString(cursor.getColumnIndex(KipConstants.DbConstants.Columns.UpdateDefaulterForm.PHONE_TRACING)));
        opdVisitModel.setPhysicalTracing(cursor.getString(cursor.getColumnIndex(KipConstants.DbConstants.Columns.UpdateDefaulterForm.PHYSICAL_TRACING)));

        opdVisitModel.setPhoneTracingOutcome(cursor.getString(cursor.getColumnIndex(KipConstants.DbConstants.Columns.UpdateDefaulterForm.PHONE_TRACING_OUTCOME)));
        opdVisitModel.setPhysicalTracingOutcome(cursor.getString(cursor.getColumnIndex(KipConstants.DbConstants.Columns.UpdateDefaulterForm.PHYSICAL_TRACING_OUTCOME)));
        opdVisitModel.setHomeAdminDate(cursor.getString(cursor.getColumnIndex(KipConstants.DbConstants.Columns.UpdateDefaulterForm.HOME_ADMINISTRATION_DATE)));
        opdVisitModel.setOtherFacilityName(cursor.getString(cursor.getColumnIndex(KipConstants.DbConstants.Columns.UpdateDefaulterForm.OTHER_FACILITY_NAME)));
        opdVisitModel.setOtherFacilityAdminDate(cursor.getString(cursor.getColumnIndex(KipConstants.DbConstants.Columns.UpdateDefaulterForm.OTHER_FACILITY_ADMINISTRATION_DATE)));
        opdVisitModel.setDateToConfirmVaccination(cursor.getString(cursor.getColumnIndex(KipConstants.DbConstants.Columns.UpdateDefaulterForm.DATE_TO_CONFIRM_VACCINATION)));

//        Covid 19 Tracing
        opdVisitModel.setCovidAntigenAdminLast(cursor.getString(cursor.getColumnIndex(KipConstants.DbConstants.Columns.RecordCovidDefaulerForm.COVID_ANTIGEN_ADMINISTERED_LAST)));
        opdVisitModel.setCovidAdminDate(cursor.getString(cursor.getColumnIndex(KipConstants.DbConstants.Columns.RecordCovidDefaulerForm.COVID_ADMINISTRATION_DATE)));
        opdVisitModel.setCovidAntigenMissed(cursor.getString(cursor.getColumnIndex(KipConstants.DbConstants.Columns.RecordCovidDefaulerForm.COVID_MISSED_VACCINE)));
        opdVisitModel.setCovidReturnDate(cursor.getString(cursor.getColumnIndex(KipConstants.DbConstants.Columns.RecordCovidDefaulerForm.COVID_RETURN_DATE)));
        opdVisitModel.setCovidChvName(cursor.getString(cursor.getColumnIndex(KipConstants.DbConstants.Columns.RecordCovidDefaulerForm.COVID_CHV_NAME)));
        opdVisitModel.setCovidChvPhoneNumber(cursor.getString(cursor.getColumnIndex(KipConstants.DbConstants.Columns.RecordCovidDefaulerForm.COVID_CHV_PHONE_NUMBER)));


        opdVisitModel.setCovidPhoneTracing(cursor.getString(cursor.getColumnIndex(KipConstants.DbConstants.Columns.UpdateCovidDefaulterForm.COVID_PHONE_TRACING)));
        opdVisitModel.setCovidPhysicalTracing(cursor.getString(cursor.getColumnIndex(KipConstants.DbConstants.Columns.UpdateCovidDefaulterForm.COVID_PHYSICAL_TRACING)));

        opdVisitModel.setCovidPhoneTracingOutcome(cursor.getString(cursor.getColumnIndex(KipConstants.DbConstants.Columns.UpdateCovidDefaulterForm.COVID_PHONE_TRACING_OUTCOME)));
        opdVisitModel.setCovidPhysicalTracingOutcome(cursor.getString(cursor.getColumnIndex(KipConstants.DbConstants.Columns.UpdateCovidDefaulterForm.COVID_PHYSICAL_TRACING_OUTCOME)));
        opdVisitModel.setCovidHomeAdminDate(cursor.getString(cursor.getColumnIndex(KipConstants.DbConstants.Columns.UpdateCovidDefaulterForm.COVID_HOME_ADMINISTRATION_DATE)));
        opdVisitModel.setCovidOtherFacilityName(cursor.getString(cursor.getColumnIndex(KipConstants.DbConstants.Columns.UpdateCovidDefaulterForm.COVID_OTHER_FACILITY_NAME)));
        opdVisitModel.setCovidOtherFacilityAdminDate(cursor.getString(cursor.getColumnIndex(KipConstants.DbConstants.Columns.UpdateCovidDefaulterForm.COVID_OTHER_FACILITY_ADMINISTRATION_DATE)));
        opdVisitModel.setCovidDateToConfirmVaccination(cursor.getString(cursor.getColumnIndex(KipConstants.DbConstants.Columns.UpdateCovidDefaulterForm.COVID_DATE_TO_CONFIRM_VACCINATION)));


        opdVisitModel.setVisitDate(OpdUtils.convertStringToDate(OpdConstants.DateFormat.YYYY_MM_DD_HH_MM_SS, cursor.getString(cursor.getColumnIndex(OpdDbConstants.Column.OpdVisit.VISIT_DATE))));
        return opdVisitModel;
    }
}
