package org.smartregister.kdp.application;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;

import android.util.DisplayMetrics;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.evernote.android.job.JobManager;

import org.jetbrains.annotations.NotNull;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.child.util.DBConstants;
import org.smartregister.commonregistry.CommonFtsObject;
import org.smartregister.configurableviews.ConfigurableViewsLibrary;
import org.smartregister.configurableviews.helper.JsonSpecHelper;
import org.smartregister.kdp.BuildConfig;
import org.smartregister.kdp.activity.KipOpdProfileActivity;
import org.smartregister.kdp.activity.LoginActivity;
import org.smartregister.kdp.activity.OpdFormActivity;
import org.smartregister.kdp.configuration.KipOpdRegisterRowOptions;
import org.smartregister.kdp.configuration.OpdRegisterQueryProvider;
import org.smartregister.kdp.job.KipJobCreator;
import org.smartregister.kdp.processor.KipMiniProcessor;
import org.smartregister.kdp.processor.KipProcessorForJava;
import org.smartregister.kdp.repository.ClientRegisterTypeRepository;
import org.smartregister.kdp.repository.KipOpdVisitSummaryRepository;
import org.smartregister.kdp.repository.KipRepository;
import org.smartregister.kdp.repository.RecordCovidDefaulterFormRepository;
import org.smartregister.kdp.repository.RecordDefaulterFormRepository;
import org.smartregister.kdp.repository.UpdateCovidDefaulterFormRepository;
import org.smartregister.kdp.repository.UpdateDefaulterFormRepository;
import org.smartregister.kdp.util.KipChildUtils;
import org.smartregister.kdp.util.KipConstants;
import org.smartregister.kdp.util.KipOpdRegisterProviderMetadata;
import org.smartregister.location.helper.LocationHelper;
import org.smartregister.opd.OpdLibrary;
import org.smartregister.opd.configuration.OpdConfiguration;
import org.smartregister.opd.pojo.OpdDetails;
import org.smartregister.opd.pojo.OpdMetadata;
import org.smartregister.opd.pojo.OpdVisit;
import org.smartregister.opd.utils.OpdConstants;
import org.smartregister.opd.utils.OpdDbConstants;
import org.smartregister.receiver.SyncStatusBroadcastReceiver;
import org.smartregister.repository.Repository;
import org.smartregister.sync.ClientProcessorForJava;
import org.smartregister.sync.DrishtiSyncScheduler;
import org.smartregister.sync.helper.ECSyncHelper;
import org.smartregister.view.activity.DrishtiApplication;
import org.smartregister.view.receiver.TimeChangedBroadcastReceiver;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import io.fabric.sdk.android.Fabric;
import timber.log.Timber;

public class KipApplication extends DrishtiApplication implements TimeChangedBroadcastReceiver.OnTimeChangedListener {

    private static CommonFtsObject commonFtsObject;
    private static JsonSpecHelper jsonSpecHelper;
    private ECSyncHelper ecSyncHelper;
    private ClientRegisterTypeRepository registerTypeRepository;
    private KipOpdVisitSummaryRepository kipOpdVisitSummaryRepository;
    private RecordDefaulterFormRepository recordDefaulterFormRepository;
    private UpdateDefaulterFormRepository updateDefaulterFormRepository;
    private RecordCovidDefaulterFormRepository recordCovidDefaulterFormRepository;
    private UpdateCovidDefaulterFormRepository updateCovidDefaulterFormRepository;


    public static JsonSpecHelper getJsonSpecHelper() {
        return jsonSpecHelper;
    }

    public static CommonFtsObject createCommonFtsObject(android.content.Context context) {
        if (commonFtsObject == null) {
            commonFtsObject = new CommonFtsObject(getFtsTables());
            for (String ftsTable : commonFtsObject.getTables()) {
                commonFtsObject.updateSearchFields(ftsTable, getFtsSearchFields(ftsTable));
                commonFtsObject.updateSortFields(ftsTable, getFtsSortFields(ftsTable, context));
            }
        }
        return commonFtsObject;
    }

    private static String[] getFtsTables() {
        return new String[]{DBConstants.RegisterTable.CHILD_DETAILS, DBConstants.RegisterTable.MOTHER_DETAILS, DBConstants.RegisterTable.FATHER_DETAILS, DBConstants.RegisterTable.CLIENT};
    }

    private static String[] getFtsSearchFields(String tableName) {
        if (tableName.equals(DBConstants.RegisterTable.CLIENT)) {
            return new String[]{DBConstants.KEY.ZEIR_ID, DBConstants.KEY.FIRST_NAME, DBConstants.KEY.LAST_NAME};
        } else if (tableName.equals(DBConstants.RegisterTable.CHILD_DETAILS)) {
            return new String[]{DBConstants.KEY.LOST_TO_FOLLOW_UP, DBConstants.KEY.INACTIVE};
        } else if (tableName.equals(DBConstants.RegisterTable.MOTHER_DETAILS)) {
            return new String[]{KipConstants.KEY.MOTHER_GUARDIAN_NUMBER};
        } else if (tableName.equals(DBConstants.RegisterTable.FATHER_DETAILS)) {
            return new String[]{KipConstants.KEY.FATHER_PHONE};
        }
        return null;
    }

    private static String[] getFtsSortFields(String tableName, android.content.Context context) {
        if (tableName.equals(KipConstants.TABLE_NAME.ALL_CLIENTS)) {
            List<String> names = new ArrayList<>();
            names.add(KipConstants.KEY.FIRST_NAME);
            names.add(OpdDbConstants.KEY.LAST_NAME);
            names.add(KipConstants.KEY.DOB);
            names.add(KipConstants.KEY.ZEIR_ID);
            names.add(KipConstants.KEY.LAST_INTERACTED_WITH);
            names.add(KipConstants.KEY.DOD);
            names.add(KipConstants.KEY.DATE_REMOVED);
            return names.toArray(new String[0]);
        } else if (tableName.equals(DBConstants.RegisterTable.CHILD_DETAILS)) {
            List<String> names = new ArrayList<>();
            names.add("relational_id");

            return names.toArray(new String[0]);
        }

        return null;
    }

    public static synchronized KipApplication getInstance() {
        return (KipApplication) mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        context = Context.getInstance();

        String lang = KipChildUtils.getLanguage(getApplicationContext());
        Locale locale = new Locale(lang);
        Resources res = getApplicationContext().getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = locale;
        res.updateConfiguration(conf, dm);

        context.updateApplicationContext(getApplicationContext());
        context.updateCommonFtsObject(createCommonFtsObject(context.applicationContext()));

        //Initialize Modules
        CoreLibrary.init(context, new KipSyncConfiguration(), BuildConfig.BUILD_TIMESTAMP);

        ConfigurableViewsLibrary.init(context);

        setupOpdLibrary();

        Fabric.with(this, new Crashlytics.Builder().core(new CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build()).build());

        SyncStatusBroadcastReceiver.init(this);
        LocationHelper.init(KipChildUtils.ALLOWED_LEVELS, KipChildUtils.DEFAULT_LOCATION_LEVEL);
        jsonSpecHelper = new JsonSpecHelper(this);

        //init Job Manager
        JobManager.create(this).addJobCreator(new KipJobCreator());
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

    }

    private void setupOpdLibrary() {
        OpdMetadata opdMetadata = new OpdMetadata(OpdConstants.JSON_FORM_KEY.NAME, OpdDbConstants.KEY.TABLE,
                OpdConstants.EventType.OPD_REGISTRATION, OpdConstants.EventType.UPDATE_OPD_REGISTRATION,
                OpdConstants.CONFIG, OpdFormActivity.class, KipOpdProfileActivity.class, true);

        opdMetadata.setFieldsWithLocationHierarchy(new HashSet<>(Collections.singletonList("village")));
        opdMetadata.setLookUpQueryForOpdClient(String.format("select id as _id, %s, %s, %s, %s, %s, %s, %s, national_id from " + OpdDbConstants.KEY.TABLE + " where [condition] ", OpdConstants.KEY.RELATIONALID, OpdConstants.KEY.FIRST_NAME,
                OpdConstants.KEY.LAST_NAME, OpdConstants.KEY.GENDER, OpdConstants.KEY.DOB, OpdConstants.KEY.BASE_ENTITY_ID, OpdDbConstants.KEY.OPENSRP_ID));
        OpdConfiguration opdConfiguration = new OpdConfiguration.Builder(OpdRegisterQueryProvider.class)
                .setOpdMetadata(opdMetadata)
                .setOpdRegisterProviderMetadata(KipOpdRegisterProviderMetadata.class)
                .setOpdRegisterRowOptions(KipOpdRegisterRowOptions.class)
                .addOpdFormProcessingClass(KipConstants.EventType.OPD_SMS_REMINDER, new KipMiniProcessor())
                .addOpdFormProcessingClass(KipConstants.EventType.RECORD_DEFAULTER_FORM, new KipMiniProcessor())
                .addOpdFormProcessingClass(KipConstants.EventType.UPDATE_DEFAULT, new KipMiniProcessor())
                .addOpdFormProcessingClass(KipConstants.EventType.UPDATE_COVID_DEFAULT, new KipMiniProcessor())
                .addOpdFormProcessingClass(KipConstants.EventType.RECORD_COVID_DEFAULTER_FORM, new KipMiniProcessor())
                .build();

        OpdLibrary.init(context, getRepository(), opdConfiguration, BuildConfig.VERSION_CODE, BuildConfig.DATABASE_VERSION);
    }

    @Override
    public void logoutCurrentUser() {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        getApplicationContext().startActivity(intent);
        context.userService().logoutSession();
    }

    @Override
    public Repository getRepository() {
        try {
            if (repository == null) {
                repository = new KipRepository(getInstance().getApplicationContext(), context);
            }
        } catch (UnsatisfiedLinkError e) {
            Timber.e(e, "KipApplication --> getRepository");
        }
        return repository;
    }

    public Context getContext() {
        return context;
    }

    @NotNull
    @Override
    public ClientProcessorForJava getClientProcessor() {
        return KipProcessorForJava.getInstance(this);
    }

    @Override
    public void onTerminate() {
        Timber.i("Application is terminating. Stopping sync scheduler and resetting isSyncInProgress setting.");
        cleanUpSyncState();
        TimeChangedBroadcastReceiver.destroy(this);
        SyncStatusBroadcastReceiver.destroy(this);
        super.onTerminate();
    }

    protected void cleanUpSyncState() {
        try {
            DrishtiSyncScheduler.stop(getApplicationContext());
            context.allSharedPreferences().saveIsSyncInProgress(false);
        } catch (Exception e) {
            Timber.e(e, "KipApplication --> cleanUpSyncState");
        }
    }

    @Override
    public void onTimeChanged() {
        String username = getContext().userService().getAllSharedPreferences().fetchRegisteredANM();
        context.userService().forceRemoteLogin(username);
        logoutCurrentUser();
    }

    @Override
    public void onTimeZoneChanged() {
        String username = getContext().userService().getAllSharedPreferences().fetchRegisteredANM();
        context.userService().forceRemoteLogin(username);
        logoutCurrentUser();
    }

    public Context context() {
        return context;
    }

    public ECSyncHelper getEcSyncHelper() {
        if (ecSyncHelper == null) {
            ecSyncHelper = ECSyncHelper.getInstance(getApplicationContext());
        }
        return ecSyncHelper;
    }

    public RecordDefaulterFormRepository lastVaccineGivenFormRepository(){
        if (recordDefaulterFormRepository == null){
            recordDefaulterFormRepository = new RecordDefaulterFormRepository();
        }
        return recordDefaulterFormRepository;
    }

    public UpdateDefaulterFormRepository updateDefaulterFormRepository(){
        if (updateDefaulterFormRepository == null){
            updateDefaulterFormRepository = new UpdateDefaulterFormRepository();
        }
        return updateDefaulterFormRepository;
    }

    public RecordCovidDefaulterFormRepository recordCovidDefaulterFormRepository(){

        if (recordCovidDefaulterFormRepository == null){
            recordCovidDefaulterFormRepository = new RecordCovidDefaulterFormRepository();
        }
        return recordCovidDefaulterFormRepository;
    }

    public UpdateCovidDefaulterFormRepository updateCovidDefaulterFormRepository(){
        if (updateCovidDefaulterFormRepository == null){
            updateCovidDefaulterFormRepository = new UpdateCovidDefaulterFormRepository();
        }
        return updateCovidDefaulterFormRepository;
    }

    public KipOpdVisitSummaryRepository kipOpdVisitSummaryRepository(){
        if (kipOpdVisitSummaryRepository == null){
            kipOpdVisitSummaryRepository = new KipOpdVisitSummaryRepository();
        }
        return kipOpdVisitSummaryRepository;
    }

    public ClientRegisterTypeRepository registerTypeRepository() {
        if (registerTypeRepository == null) {
            this.registerTypeRepository = new ClientRegisterTypeRepository();
        }
        return this.registerTypeRepository;
    }

    /**
     * This method enables us to configure how-long ago we should consider a valid check-in so that
     * we enable the next step which is DIAGNOSE & TREAT. This method returns the latest date that a check-in
     * should be so that it can be considered for moving to DIAGNOSE & TREAT
     *
     * @return Date
     */
    @NonNull
    public Date getLatestValidCheckInDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -28);

        return calendar.getTime();
    }

    /**
     * This checks if the patient can perform a Check-In evaluated based on their latest visit details & opd details. This however does not consider the TREATED status
     * which appears after a visit is completed within the same day. If you need to consider the TREATED status, you should first call {@link #isPatientInTreatedState(Date)}
     * and then call this method if the result is false.
     *
     * @param visit
     * @param opdDetails
     * @return
     */
    public boolean canPatientCheckInInsteadOfDiagnoseAndTreat(@Nullable OpdVisit visit, @Nullable OpdDetails opdDetails) {
        Date latestValidCheckInDate = KipApplication.getInstance().getLatestValidCheckInDate();

        // If we are past the 28 days or so, then the status should be check-in
        // If your opd
        return visit == null || visit.getVisitDate().before(latestValidCheckInDate) || (opdDetails != null && opdDetails.getCurrentVisitEndDate() != null);
    }

    public boolean isClientCurrentlyCheckedIn(@Nullable OpdVisit opdVisit, @Nullable OpdDetails opdDetails) {
        return !canPatientCheckInInsteadOfDiagnoseAndTreat(opdVisit, opdDetails);
    }
}

