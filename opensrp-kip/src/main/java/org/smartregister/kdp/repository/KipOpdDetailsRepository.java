package org.smartregister.kdp.repository;

import android.content.ContentValues;

import org.smartregister.child.provider.RegisterQueryProvider;
import org.smartregister.kdp.util.KipConstants;
import org.smartregister.opd.repository.OpdDetailsRepository;
import org.smartregister.repository.Repository;
import org.smartregister.view.activity.DrishtiApplication;

import java.util.Calendar;

import timber.log.Timber;

public class KipOpdDetailsRepository extends OpdDetailsRepository {

    private static final String SMS_REMINDER = "opd_sms_reminder";
    public static final String LAST_VACCINE_GIVEN = "last_vaccine";
    public static final String COVID_DEFAULTER = "covid_defaulter";
    public static final String UPDATE_COVID_DEFAULTER_STATUS = "update_covid_defaulter_form";
    public static final String UPDATE_DEFAULTER_STATUS = "defaulter_status";


    public static void updateSmsReminder(String baseEntityId) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(SMS_REMINDER, 1);

        updatePatient(baseEntityId, contentValues, getRegisterQueryProvider().getDemographicTable());
        updateLastInteractedWith(baseEntityId);
    }

    public static void updateDefaulterStatus(String baseEntityId){
        ContentValues contentValues = new ContentValues();
        contentValues.put(UPDATE_DEFAULTER_STATUS, 1);
        contentValues.put(LAST_VACCINE_GIVEN, "");

        updatePatient(baseEntityId, contentValues, getRegisterQueryProvider().getDemographicTable());
        updateLastInteractedWith(baseEntityId);
    }

    public static void updateLastVaccineGivenForm(String baseEntityId){
        ContentValues contentValues = new ContentValues();
        contentValues.put(LAST_VACCINE_GIVEN, 1);
        contentValues.put(UPDATE_DEFAULTER_STATUS, "");

        updatePatient(baseEntityId, contentValues, getRegisterQueryProvider().getDemographicTable());
        updateLastInteractedWith(baseEntityId);
    }


    public static void updateCovidDefaulterStatus(String baseEntityId){
        ContentValues contentValues = new ContentValues();
        contentValues.put(UPDATE_COVID_DEFAULTER_STATUS, 1);
        contentValues.put(COVID_DEFAULTER, "");

        updatePatient(baseEntityId, contentValues, getRegisterQueryProvider().getDemographicTable());
        updateLastInteractedWith(baseEntityId);
    }

    public static void updateCovidDefaulterForm(String baseEntityId){
        ContentValues contentValues = new ContentValues();
        contentValues.put(COVID_DEFAULTER, 1);
        contentValues.put(UPDATE_COVID_DEFAULTER_STATUS, "");

        updatePatient(baseEntityId, contentValues, getRegisterQueryProvider().getDemographicTable());
        updateLastInteractedWith(baseEntityId);
    }

    public static void restDefaulterSchedule(String baseEntityId){
        ContentValues contentValues = new ContentValues();
        contentValues.put(LAST_VACCINE_GIVEN, "");
        contentValues.put(UPDATE_DEFAULTER_STATUS, "");
        updatePatient(baseEntityId, contentValues, getRegisterQueryProvider().getDemographicTable());
        updateLastInteractedWith(baseEntityId);
    }

    public static void restCovidDefaulterSchedule(String baseEntityId){
        ContentValues contentValues = new ContentValues();
        contentValues.put(COVID_DEFAULTER, "");
        contentValues.put(UPDATE_COVID_DEFAULTER_STATUS, "");
        updatePatient(baseEntityId, contentValues, getRegisterQueryProvider().getDemographicTable());
        updateLastInteractedWith(baseEntityId);
    }


    public static void updatePatient(String baseEntityId, ContentValues contentValues, String table) {
        getMasterRepository().getWritableDatabase()
                .update(table, contentValues, KipConstants.KeyUtils.BASE_ENTITY_ID + " = ?",
                        new String[]{baseEntityId});
        Timber.i("-->Patient updated %s",baseEntityId);
    }

    private static void updateLastInteractedWith(String baseEntityId) {
        ContentValues lastInteractedWithContentValue = new ContentValues();
        lastInteractedWithContentValue.put(KipConstants.KeyUtils.LAST_INTERACTED_WITH, Calendar.getInstance().getTimeInMillis());
        updatePatient(baseEntityId, lastInteractedWithContentValue, getRegisterQueryProvider().getDemographicTable());
    }

    protected static Repository getMasterRepository() {
        return DrishtiApplication.getInstance().getRepository();
    }

    private static RegisterQueryProvider getRegisterQueryProvider() {
        return new RegisterQueryProvider();
    }
}
