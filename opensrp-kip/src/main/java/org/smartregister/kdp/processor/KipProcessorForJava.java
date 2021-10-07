package org.smartregister.kdp.processor;

import android.content.ContentValues;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import android.text.TextUtils;

import com.google.gson.Gson;

import net.sqlcipher.database.SQLiteException;

import org.smartregister.CoreLibrary;
import org.smartregister.commonregistry.CommonFtsObject;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.domain.Client;
import org.smartregister.domain.Event;
import org.smartregister.domain.db.EventClient;
import org.smartregister.domain.Obs;
import org.smartregister.domain.jsonmapping.ClientClassification;
import org.smartregister.domain.jsonmapping.ClientField;
import org.smartregister.domain.jsonmapping.Column;
import org.smartregister.domain.jsonmapping.Table;
import org.smartregister.kdp.application.KipApplication;
import org.smartregister.kdp.exceptions.SmsReminderException;
import org.smartregister.kdp.exceptions.RecordDefaulterException;
import org.smartregister.kdp.exceptions.UpdateDefaulterTracingException;
import org.smartregister.kdp.pojo.RecordCovidDefaulterForm;
import org.smartregister.kdp.pojo.RecordDefaulterForm;
import org.smartregister.kdp.pojo.OpdSMSReminderForm;
import org.smartregister.kdp.pojo.UpdateCovidDefaulterForm;
import org.smartregister.kdp.pojo.UpdateDefaulterForm;
import org.smartregister.kdp.util.KipConstants;
import org.smartregister.opd.OpdLibrary;
import org.smartregister.opd.exception.CheckInEventProcessException;
import org.smartregister.opd.pojo.OpdDetails;
import org.smartregister.opd.processor.OpdMiniClientProcessorForJava;
import org.smartregister.opd.utils.OpdConstants;
import org.smartregister.opd.utils.OpdDbConstants;
import org.smartregister.opd.utils.OpdUtils;
import org.smartregister.sync.MiniClientProcessorForJava;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import timber.log.Timber;

public class KipProcessorForJava extends OpdMiniClientProcessorForJava implements MiniClientProcessorForJava {

    private static KipProcessorForJava instance;

    private HashMap<String, MiniClientProcessorForJava> processorMap = new HashMap<>();
    private HashMap<MiniClientProcessorForJava, List<Event>> unsyncEventsPerProcessor = new HashMap<>();

    private List<String> coreProcessedEvents = Arrays.asList(OpdConstants.EventType.OPD_REGISTRATION, OpdConstants.EventType.UPDATE_OPD_REGISTRATION);

    private SimpleDateFormat dateFormat = new SimpleDateFormat(OpdDbConstants.DATE_FORMAT, Locale.US);

    private KipProcessorForJava(Context context) {
        super(context);
        OpdMiniClientProcessorForJava opdMiniClientProcessorForJava = new OpdMiniClientProcessorForJava(context);
        addMiniProcessors(opdMiniClientProcessorForJava);
    }

    public void addMiniProcessors(MiniClientProcessorForJava... miniClientProcessorsForJava) {
        for (MiniClientProcessorForJava miniClientProcessorForJava : miniClientProcessorsForJava) {
            unsyncEventsPerProcessor.put(miniClientProcessorForJava, new ArrayList<>());

            HashSet<String> eventTypes = miniClientProcessorForJava.getEventTypes();

            for (String eventType : eventTypes) {
                processorMap.put(eventType, miniClientProcessorForJava);
            }
        }
    }

    public static KipProcessorForJava getInstance(Context context) {
        if (instance == null) {
            instance = new KipProcessorForJava(context);
        }
        return instance;
    }


    @Override
    public synchronized void processClient(List<EventClient> eventClients) throws Exception {

        ClientClassification clientClassification = assetJsonToJava("ec_client_classification.json",
                ClientClassification.class);
        if (!eventClients.isEmpty()) {
            List<Event> unsyncEvents = new ArrayList<>();
            for (EventClient eventClient : eventClients) {
                Event event = eventClient.getEvent();
                if (event == null) {
                    return;
                }

                String eventType = event.getEventType();
                if (eventType == null) {
                    continue;
                }
                if (eventType.equals(KipConstants.EventType.RECORD_DEFAULTER_FORM)) {
                    processRecordDefaulterForm(eventClient, clientClassification);
                } else if (eventType.equals(KipConstants.EventType.UPDATE_DEFAULT)) {
                    processUpdateDefaulterForm(eventClient, clientClassification);
                } else if (eventType.equals(KipConstants.EventType.RECORD_COVID_DEFAULTER_FORM)) {
                    processRecordCovidDefaulterForm(eventClient, clientClassification);
                } else if (eventType.equals(KipConstants.EventType.UPDATE_COVID_DEFAULT)) {
                    processUpdateCovidDefaulterForm(eventClient, clientClassification);
                }  else if (coreProcessedEvents.contains(eventType)) {
                    processKipCoreEvents(clientClassification, eventClient, event, eventType);
                } else if (processorMap.containsKey(eventType)) {
                    try {
                        processEventUsingMiniprocessor(clientClassification, eventClient, eventType);
                    } catch (Exception ex) {
                        Timber.e(ex);
                    }
                } else if (event.getEventType().equals(OpdConstants.EventType.CLOSE_OPD_VISIT)) {
                    try {
                        processOpdCloseVisitEvent(event);
                        CoreLibrary.getInstance().context().getEventClientRepository().markEventAsProcessed(eventClient.getEvent().getFormSubmissionId());
                    } catch (Exception e) {
                        Timber.e(e);
                    }
                }

            }

            // Unsync events that are should not be in this device
            processUnsyncEvents(unsyncEvents);

        }
    }

    private void processOpdCloseVisitEvent(@NonNull Event event) {
        Map<String, String> mapDetails = event.getDetails();
        //update visit end date
        if (mapDetails != null) {
            OpdDetails opdDetails = new OpdDetails(event.getBaseEntityId(), mapDetails.get(OpdConstants.JSON_FORM_KEY.VISIT_ID));
            opdDetails = OpdLibrary.getInstance().getOpdDetailsRepository().findOne(opdDetails);
            if (opdDetails != null) {
                opdDetails.setCurrentVisitEndDate(OpdUtils.convertStringToDate(OpdConstants.DateFormat.YYYY_MM_DD_HH_MM_SS, mapDetails.get(OpdConstants.JSON_FORM_KEY.VISIT_END_DATE)));
                boolean result = OpdLibrary.getInstance().getOpdDetailsRepository().saveOrUpdate(opdDetails);
                if (result) {
                    Timber.d("Opd processOpdCloseVisitEvent for %s saved", event.getBaseEntityId());
                    return;
                }
                Timber.e("Opd processOpdCloseVisitEvent for %s not saved", event.getBaseEntityId());
            } else {
                Timber.e("Opd Details for %s not found", mapDetails.toString());
            }
        } else {
            Timber.e("Opd Details for %s not found, event details is null", event.getBaseEntityId());
        }
    }

    public void processKipCoreEvents(ClientClassification clientClassification, EventClient eventClient, Event event, String eventType) throws Exception {
        if (eventType.equals(OpdConstants.EventType.OPD_REGISTRATION) && eventClient.getClient() != null) {
            KipApplication.getInstance().registerTypeRepository().addUnique(KipConstants.RegisterType.OPD, event.getBaseEntityId());
        }

        if (clientClassification != null) {
            processEventClient(clientClassification, eventClient, event);
        }
    }

    private void processUnsyncEvents(@NonNull List<Event> unsyncEvents) {
        if (!unsyncEvents.isEmpty()) {
            unSync(unsyncEvents);
        }

        for (MiniClientProcessorForJava miniClientProcessorForJava : unsyncEventsPerProcessor.keySet()) {
            List<Event> processorUnsyncEvents = unsyncEventsPerProcessor.get(miniClientProcessorForJava);
            miniClientProcessorForJava.unSync(processorUnsyncEvents);
        }
    }

    private void processEventClient(@NonNull ClientClassification clientClassification, @NonNull EventClient eventClient, @NonNull Event event) {
        Client client = eventClient.getClient();
        if (client != null) {
            try {
                processEvent(event, client, clientClassification);
            } catch (Exception e) {
                Timber.e(e);
            }
        }
    }

    private void processEventUsingMiniprocessor(ClientClassification clientClassification, EventClient eventClient, String eventType) throws Exception {
        MiniClientProcessorForJava miniClientProcessorForJava = processorMap.get(eventType);
        if (miniClientProcessorForJava != null) {
            List<Event> processorUnsyncEvents = unsyncEventsPerProcessor.get(miniClientProcessorForJava);
            if (processorUnsyncEvents == null) {
                processorUnsyncEvents = new ArrayList<Event>();
                unsyncEventsPerProcessor.put(miniClientProcessorForJava, processorUnsyncEvents);
            }

            completeProcessing(eventClient.getEvent());
            miniClientProcessorForJava.processEventClient(eventClient, processorUnsyncEvents, clientClassification);
        }
    }

    @NonNull
    @Override
    public HashSet<String> getEventTypes() {
        return null;
    }

    @Override
    public boolean canProcess(@NonNull String s) {
        return false;
    }

    @Override
    public void processEventClient(@NonNull EventClient eventClient, @NonNull List<Event> list, @androidx.annotation.Nullable ClientClassification clientClassification) throws Exception {

    }

    @Override
    public boolean unSync(List<Event> events) {
        try {

            if (events == null || events.isEmpty()) {
                return false;
            }

            ClientField clientField = assetJsonToJava("ec_client_fields.json", ClientField.class);
            return clientField != null;

        } catch (Exception e) {
            Timber.e(e);
        }

        return false;
    }

    @VisibleForTesting
    ContentValues processCaseModel(EventClient eventClient, Table table) {
        try {
            List<Column> columns = table.columns;
            ContentValues contentValues = new ContentValues();

            for (Column column : columns) {
                processCaseModel(eventClient.getEvent(), eventClient.getClient(), column, contentValues);
            }

            return contentValues;
        } catch (Exception e) {
            Timber.e(e);
        }
        return null;
    }

    @Override
    public String[] getOpenmrsGenIds() {
        return new String[]{"zeir_id"};
    }


    protected void processRecordDefaulterForm(@NonNull EventClient eventClient, @NonNull ClientClassification clientClassification) throws RecordDefaulterException {
        HashMap<String, String> keyValues = new HashMap<>();
        Event event = eventClient.getEvent();
        // Todo: This might not work as expected when openmrs_entity_ids are added
        generateKeyValuesFromEvent(event, keyValues);


        String visitId = event.getDetails().get(OpdConstants.VISIT_ID);
        String visitDateString = event.getDetails().get(OpdConstants.VISIT_DATE);
        String antigenAdministeredLast = keyValues.get(KipConstants.DbConstants.Columns.RecordDefaulerForm.ANTIGEN_ADMINISTERED_LAST);
        String admissionDate = keyValues.get(KipConstants.DbConstants.Columns.RecordDefaulerForm.ADMINISTRATION_DATE);
        String chvName = keyValues.get(KipConstants.DbConstants.Columns.RecordDefaulerForm.CHV_NAME);
        String chvPhoneNumber = keyValues.get(KipConstants.DbConstants.Columns.RecordDefaulerForm.CHV_PHONE_NUMBER);
        String missedDose = keyValues.get(KipConstants.DbConstants.Columns.RecordDefaulerForm.MISSED_VACCINE);
        String returnDate = keyValues.get(KipConstants.DbConstants.Columns.RecordDefaulerForm.RETURN_DATE);
        String birthDose = keyValues.get(KipConstants.DbConstants.Columns.RecordDefaulerForm.BIRTH_DOSE_ANTIGEN);
        String sixWksDose = keyValues.get(KipConstants.DbConstants.Columns.RecordDefaulerForm.SIX_WKS_ANTIGEN);
        String tenWksDose = keyValues.get(KipConstants.DbConstants.Columns.RecordDefaulerForm.TEN_WKS_ANTIGEN);
        String fourteeneenWksDose = keyValues.get(KipConstants.DbConstants.Columns.RecordDefaulerForm.FOURTEEN_WKS_ANTIGEN);
        String nineMonthsDose = keyValues.get(KipConstants.DbConstants.Columns.RecordDefaulerForm.NINE_MONTHS_ANTIGEN);
        String EighteenMonthsDose = keyValues.get(KipConstants.DbConstants.Columns.RecordDefaulerForm.EIGHTEEN_MONTHS_ANTIGEN);
        String age = keyValues.get(KipConstants.DbConstants.Columns.RecordDefaulerForm.AGE);
        String date = keyValues.get(KipConstants.DbConstants.Columns.RecordDefaulerForm.DATE);

        System.out.println();

        Date visitDate;
        try {
            visitDate = dateFormat.parse(visitDateString != null ? visitDateString : "");
        } catch (ParseException e) {
            Timber.e(e);
            visitDate = event.getEventDate().toDate();
        }

        if (visitDate != null && visitId != null) {
            // Start transaction
            OpdLibrary.getInstance().getRepository().getWritableDatabase().beginTransaction();
            boolean saved = saveRecordDefaulterForm(event, visitId,antigenAdministeredLast, admissionDate,missedDose, returnDate,chvName, chvPhoneNumber,
                    birthDose, sixWksDose,  tenWksDose,  fourteeneenWksDose, nineMonthsDose,  EighteenMonthsDose,  age,  date);
            if (!saved) {
                abortTransaction();
                throw new RecordDefaulterException(String.format("Visit (Record Defaulter Tracing) with id %s could not be saved in the db. Fail operation failed", visitId));
            }

            try {
                processEvent(event, eventClient.getClient(), clientClassification);
            } catch (Exception e) {
                Timber.e(e);
            }

            // Update the last interacted with of the user
            try {
                updateLastInteractedWith(event, visitId);
            } catch (SQLiteException | CheckInEventProcessException ex) {
                abortTransaction();
                throw new RecordDefaulterException("An error occurred saving last_interacted_with");
            }

            commitSuccessfulTransaction();
        } else {
            throw new RecordDefaulterException(String.format("OPD Record Defaulter Tracing event %s could not be processed because it the visitDate OR visitId is null", new Gson().toJson(event)));
        }
    }

    protected void processUpdateDefaulterForm(@NonNull EventClient eventClient, @NonNull ClientClassification clientClassification) throws UpdateDefaulterTracingException {
        HashMap<String, String> keyValues = new HashMap<>();
        Event event = eventClient.getEvent();
        // Todo: This might not work as expected when openmrs_entity_ids are added
        generateKeyValuesFromEvent(event, keyValues);


        String visitId = event.getDetails().get(OpdConstants.VISIT_ID);
        String visitDateString = event.getDetails().get(OpdConstants.VISIT_DATE);
        String phoneTracingOutcome = keyValues.get(KipConstants.DbConstants.Columns.UpdateDefaulterForm.PHONE_TRACING_OUTCOME);
        String physicalTracingOutcome = keyValues.get(KipConstants.DbConstants.Columns.UpdateDefaulterForm.PHYSICAL_TRACING_OUTCOME);
        String phoneTracing = keyValues.get(KipConstants.DbConstants.Columns.UpdateDefaulterForm.PHONE_TRACING);
        String physicalTracing = keyValues.get(KipConstants.DbConstants.Columns.UpdateDefaulterForm.PHYSICAL_TRACING);
        String homeAdminDate = keyValues.get(KipConstants.DbConstants.Columns.UpdateDefaulterForm.HOME_ADMINISTRATION_DATE);
        String otherFacilityAdminDate = keyValues.get(KipConstants.DbConstants.Columns.UpdateDefaulterForm.OTHER_FACILITY_ADMINISTRATION_DATE);
        String otherFacilityName = keyValues.get(KipConstants.DbConstants.Columns.UpdateDefaulterForm.OTHER_FACILITY_NAME);
        String confirmVaccination = keyValues.get(KipConstants.DbConstants.Columns.UpdateDefaulterForm.DATE_TO_CONFIRM_VACCINATION);
        String tracingMode = keyValues.get(KipConstants.DbConstants.Columns.UpdateDefaulterForm.MODE_OF_TRACING);
        String age = keyValues.get(KipConstants.DbConstants.Columns.UpdateDefaulterForm.AGE);
        String date = keyValues.get(KipConstants.DbConstants.Columns.UpdateDefaulterForm.DATE);

        Date visitDate;
        try {
            visitDate = dateFormat.parse(visitDateString != null ? visitDateString : "");
        } catch (ParseException e) {
            Timber.e(e);
            visitDate = event.getEventDate().toDate();
        }

        if (visitDate != null && visitId != null) {
            // Start transaction
            OpdLibrary.getInstance().getRepository().getWritableDatabase().beginTransaction();
            boolean saved = saveUpdateDefaulterForm(event, visitId, phoneTracingOutcome,physicalTracingOutcome,phoneTracing,physicalTracing,homeAdminDate,otherFacilityName, otherFacilityAdminDate,confirmVaccination,tracingMode,age,date);
            if (!saved) {
                abortTransaction();
                throw new UpdateDefaulterTracingException(String.format("Visit (Update Defaulter Tracing) with id %s could not be saved in the db. Fail operation failed", visitId));
            }

            try {
                processEvent(event, eventClient.getClient(), clientClassification);
            } catch (Exception e) {
                Timber.e(e);
            }

            // Update the last interacted with of the user
            try {
                updateLastInteractedWith(event, visitId);
            } catch (SQLiteException | CheckInEventProcessException ex) {
                abortTransaction();
                throw new UpdateDefaulterTracingException("An error occurred saving last_interacted_with");
            }

            commitSuccessfulTransaction();
        } else {
            throw new UpdateDefaulterTracingException(String.format("OPD Update Defaulter Tracing event %s could not be processed because it the visitDate OR visitId is null", new Gson().toJson(event)));
        }
    }

    protected void processRecordCovidDefaulterForm(@NonNull EventClient eventClient, @NonNull ClientClassification clientClassification) throws RecordDefaulterException {
        HashMap<String, String> keyValues = new HashMap<>();
        Event event = eventClient.getEvent();
        // Todo: This might not work as expected when openmrs_entity_ids are added
        generateKeyValuesFromEvent(event, keyValues);


        String visitId = event.getDetails().get(OpdConstants.VISIT_ID);
        String visitDateString = event.getDetails().get(OpdConstants.VISIT_DATE);
        String antigenAdministeredLast = keyValues.get(KipConstants.DbConstants.Columns.RecordCovidDefaulerForm.COVID_ANTIGEN_ADMINISTERED_LAST);
        String admissionDate = keyValues.get(KipConstants.DbConstants.Columns.RecordCovidDefaulerForm.COVID_ADMINISTRATION_DATE);
        String chvName = keyValues.get(KipConstants.DbConstants.Columns.RecordCovidDefaulerForm.COVID_CHV_NAME);
        String chvPhoneNumber = keyValues.get(KipConstants.DbConstants.Columns.RecordCovidDefaulerForm.COVID_CHV_PHONE_NUMBER);
        String missedDose = keyValues.get(KipConstants.DbConstants.Columns.RecordCovidDefaulerForm.COVID_MISSED_VACCINE);
        String returnDate = keyValues.get(KipConstants.DbConstants.Columns.RecordCovidDefaulerForm.COVID_RETURN_DATE);
        String age = keyValues.get(KipConstants.DbConstants.Columns.RecordCovidDefaulerForm.AGE);
        String date = keyValues.get(KipConstants.DbConstants.Columns.RecordCovidDefaulerForm.DATE);

        Date visitDate;
        try {
            visitDate = dateFormat.parse(visitDateString != null ? visitDateString : "");
        } catch (ParseException e) {
            Timber.e(e);
            visitDate = event.getEventDate().toDate();
        }

        if (visitDate != null && visitId != null) {
            // Start transaction
            OpdLibrary.getInstance().getRepository().getWritableDatabase().beginTransaction();
            boolean saved = saveRecordCovidDefaulterForm(event, visitId,antigenAdministeredLast, admissionDate,missedDose, returnDate,chvName, chvPhoneNumber, age,  date);
            if (!saved) {
                abortTransaction();
                throw new RecordDefaulterException(String.format("Visit (Record Covid Defaulter Tracing) with id %s could not be saved in the db. Fail operation failed", visitId));
            }

            try {
                processEvent(event, eventClient.getClient(), clientClassification);
            } catch (Exception e) {
                Timber.e(e);
            }

            // Update the last interacted with of the user
            try {
                updateLastInteractedWith(event, visitId);
            } catch (SQLiteException | CheckInEventProcessException ex) {
                abortTransaction();
                throw new RecordDefaulterException("An error occurred saving last_interacted_with");
            }

            commitSuccessfulTransaction();
        } else {
            throw new RecordDefaulterException(String.format("OPD Record Defaulter Tracing event %s could not be processed because it the visitDate OR visitId is null", new Gson().toJson(event)));
        }
    }

    protected void processUpdateCovidDefaulterForm(@NonNull EventClient eventClient, @NonNull ClientClassification clientClassification) throws UpdateDefaulterTracingException {
        HashMap<String, String> keyValues = new HashMap<>();
        Event event = eventClient.getEvent();
        // Todo: This might not work as expected when openmrs_entity_ids are added
        generateKeyValuesFromEvent(event, keyValues);


        String visitId = event.getDetails().get(OpdConstants.VISIT_ID);
        String visitDateString = event.getDetails().get(OpdConstants.VISIT_DATE);
        String phoneTracingOutcome = keyValues.get(KipConstants.DbConstants.Columns.UpdateCovidDefaulterForm.COVID_PHONE_TRACING_OUTCOME);
        String physicalTracingOutcome = keyValues.get(KipConstants.DbConstants.Columns.UpdateCovidDefaulterForm.COVID_PHYSICAL_TRACING_OUTCOME);
        String phoneTracing = keyValues.get(KipConstants.DbConstants.Columns.UpdateCovidDefaulterForm.COVID_PHONE_TRACING);
        String physicalTracing = keyValues.get(KipConstants.DbConstants.Columns.UpdateCovidDefaulterForm.COVID_PHYSICAL_TRACING);
        String homeAdminDate = keyValues.get(KipConstants.DbConstants.Columns.UpdateCovidDefaulterForm.COVID_HOME_ADMINISTRATION_DATE);
        String otherFacilityAdminDate = keyValues.get(KipConstants.DbConstants.Columns.UpdateCovidDefaulterForm.COVID_OTHER_FACILITY_ADMINISTRATION_DATE);
        String otherFacilityName = keyValues.get(KipConstants.DbConstants.Columns.UpdateCovidDefaulterForm.COVID_OTHER_FACILITY_NAME);
        String confirmVaccination = keyValues.get(KipConstants.DbConstants.Columns.UpdateCovidDefaulterForm.COVID_DATE_TO_CONFIRM_VACCINATION);
        String tracingMode = keyValues.get(KipConstants.DbConstants.Columns.UpdateCovidDefaulterForm.COVID_MODE_OF_TRACING);
        String age = keyValues.get(KipConstants.DbConstants.Columns.UpdateCovidDefaulterForm.AGE);
        String date = keyValues.get(KipConstants.DbConstants.Columns.UpdateCovidDefaulterForm.DATE);

        Date visitDate;
        try {
            visitDate = dateFormat.parse(visitDateString != null ? visitDateString : "");
        } catch (ParseException e) {
            Timber.e(e);
            visitDate = event.getEventDate().toDate();
        }

        if (visitDate != null && visitId != null) {
            // Start transaction
            OpdLibrary.getInstance().getRepository().getWritableDatabase().beginTransaction();
            boolean saved = saveUpdateCovidDefaulterForm(event, visitId, phoneTracingOutcome,physicalTracingOutcome,phoneTracing,physicalTracing,homeAdminDate,otherFacilityName, otherFacilityAdminDate,confirmVaccination,tracingMode,age,date);
            if (!saved) {
                abortTransaction();
                throw new UpdateDefaulterTracingException(String.format("Visit (Update Defaulter Tracing) with id %s could not be saved in the db. Fail operation failed", visitId));
            }

            try {
                processEvent(event, eventClient.getClient(), clientClassification);
            } catch (Exception e) {
                Timber.e(e);
            }

            // Update the last interacted with of the user
            try {
                updateLastInteractedWith(event, visitId);
            } catch (SQLiteException | CheckInEventProcessException ex) {
                abortTransaction();
                throw new UpdateDefaulterTracingException("An error occurred saving last_interacted_with");
            }

            commitSuccessfulTransaction();
        } else {
            throw new UpdateDefaulterTracingException(String.format("OPD Update Defaulter Tracing event %s could not be processed because it the visitDate OR visitId is null", new Gson().toJson(event)));
        }
    }


    private boolean saveRecordDefaulterForm(Event event, String visitId, String antigenAdministeredLast, String admissionDate, String missedDose, String returnDate, String chvName, String chvPhoneNumber, String birthDose, String sixWksDose, String tenWksDose, String fourteeneenWksDose, String nineMonthsDose, String EighteenMonthsDose, String age, String date) {
        RecordDefaulterForm recordDefaulterForm = new RecordDefaulterForm();
        recordDefaulterForm.setVisitId(visitId);
        recordDefaulterForm.setId(visitId);
        recordDefaulterForm.setBaseEntityId(event.getBaseEntityId());
        recordDefaulterForm.setAntigenAdministeredLast(antigenAdministeredLast);
        recordDefaulterForm.setAdministrationDate(admissionDate);
        recordDefaulterForm.setMissedDoses(missedDose);
        recordDefaulterForm.setReturnDate(returnDate);
        recordDefaulterForm.setChvName(chvName);
        recordDefaulterForm.setChvPhoneNumber(chvPhoneNumber);
        recordDefaulterForm.setBirthDoseAntigen(birthDose);
        recordDefaulterForm.setSixWksAntigen(sixWksDose);
        recordDefaulterForm.setTenWksAntigen(tenWksDose);
        recordDefaulterForm.setFourteenWksAntigen(fourteeneenWksDose);
        recordDefaulterForm.setNineMonthsAntigen(nineMonthsDose);
        recordDefaulterForm.setEighteenMonthsAntigen(EighteenMonthsDose);
        recordDefaulterForm.setAge(age);
        recordDefaulterForm.setCreatedAt(OpdUtils.convertDate(event.getEventDate().toLocalDate().toDate(), OpdDbConstants.DATE_FORMAT));
        recordDefaulterForm.setDate(date);

        return KipApplication.getInstance().lastVaccineGivenFormRepository().saveOrUpdate(recordDefaulterForm);
    }

    private boolean saveUpdateDefaulterForm(Event event, String visitId, String phoneTracingOutcome,String physicalTracingOutcome,String phoneTracing,String physicalTracing, String homeAdministrationDate, String otherFacilityName, String otherFacilityAdministrationDate, String confirmVaccination, String tracingMode, String age, String date) {
        UpdateDefaulterForm recordDefaulterForm = new UpdateDefaulterForm();
        recordDefaulterForm.setVisitId(visitId);
        recordDefaulterForm.setId(visitId);
        recordDefaulterForm.setBaseEntityId(event.getBaseEntityId());
        recordDefaulterForm.setPhoneTracingOutcome(phoneTracingOutcome);
        recordDefaulterForm.setPhysicalTracingOutcome(physicalTracingOutcome);
        recordDefaulterForm.setPhoneTracing(phoneTracing);
        recordDefaulterForm.setPhysicalTracing(physicalTracing);
        recordDefaulterForm.setHomeAdministrationDate(homeAdministrationDate);
        recordDefaulterForm.setOtherFacilityName(otherFacilityName);
        recordDefaulterForm.setOtherFacilityAdministrationDate(otherFacilityAdministrationDate);
        recordDefaulterForm.setDateToConfirmVaccination(confirmVaccination);
        recordDefaulterForm.setModeOfTracing(tracingMode);
        recordDefaulterForm.setAge(age);
        recordDefaulterForm.setCreatedAt(OpdUtils.convertDate(event.getEventDate().toLocalDate().toDate(), OpdDbConstants.DATE_FORMAT));
        recordDefaulterForm.setDate(date);

        return KipApplication.getInstance().updateDefaulterFormRepository().saveOrUpdate(recordDefaulterForm);
    }

    private boolean saveRecordCovidDefaulterForm(Event event, String visitId, String antigenAdministeredLast, String admissionDate, String missedDose, String returnDate, String chvName, String chvPhoneNumber, String age, String date) {
        RecordCovidDefaulterForm recordDefaulterForm = new RecordCovidDefaulterForm();
        recordDefaulterForm.setVisitId(visitId);
        recordDefaulterForm.setId(visitId);
        recordDefaulterForm.setBaseEntityId(event.getBaseEntityId());
        recordDefaulterForm.setCovidAntigenAdministeredLast(antigenAdministeredLast);
        recordDefaulterForm.setCovidAdministrationDate(admissionDate);
        recordDefaulterForm.setCovidMissedDoses(missedDose);
        recordDefaulterForm.setCovidReturnDate(returnDate);
        recordDefaulterForm.setCovidChvName(chvName);
        recordDefaulterForm.setCovidChvPhoneNumber(chvPhoneNumber);
        recordDefaulterForm.setAge(age);
        recordDefaulterForm.setCreatedAt(OpdUtils.convertDate(event.getEventDate().toLocalDate().toDate(), OpdDbConstants.DATE_FORMAT));
        recordDefaulterForm.setDate(date);

        return KipApplication.getInstance().recordCovidDefaulterFormRepository().saveOrUpdate(recordDefaulterForm);
    }

    private boolean saveUpdateCovidDefaulterForm(Event event, String visitId, String phoneTracingOutcome,String physicalTracingOutcome,String phoneTracing,String physicalTracing, String homeAdministrationDate, String otherFacilityName, String otherFacilityAdministrationDate, String confirmVaccination, String tracingMode, String age, String date) {
        UpdateCovidDefaulterForm recordDefaulterForm = new UpdateCovidDefaulterForm();
        recordDefaulterForm.setVisitId(visitId);
        recordDefaulterForm.setId(visitId);
        recordDefaulterForm.setBaseEntityId(event.getBaseEntityId());
        recordDefaulterForm.setCovidPhoneTracingOutcome(phoneTracingOutcome);
        recordDefaulterForm.setCovidPhysicalTracingOutcome(physicalTracingOutcome);
        recordDefaulterForm.setCovidPhoneTracing(phoneTracing);
        recordDefaulterForm.setCovidPhysicalTracing(physicalTracing);
        recordDefaulterForm.setCovidHomeAdministrationDate(homeAdministrationDate);
        recordDefaulterForm.setCovidOtherFacilityName(otherFacilityName);
        recordDefaulterForm.setCovidOtherFacilityAdministrationDate(otherFacilityAdministrationDate);
        recordDefaulterForm.setCovidDateToConfirmVaccination(confirmVaccination);
        recordDefaulterForm.setCovidModeOfTracing(tracingMode);
        recordDefaulterForm.setAge(age);
        recordDefaulterForm.setCreatedAt(OpdUtils.convertDate(event.getEventDate().toLocalDate().toDate(), OpdDbConstants.DATE_FORMAT));
        recordDefaulterForm.setDate(date);

        return KipApplication.getInstance().updateCovidDefaulterFormRepository().saveOrUpdate(recordDefaulterForm);
    }


    private void abortTransaction() {
        if (OpdLibrary.getInstance().getRepository().getWritableDatabase().inTransaction()) {
            OpdLibrary.getInstance().getRepository().getWritableDatabase().endTransaction();
        }
    }

    private void commitSuccessfulTransaction() {
        if (OpdLibrary.getInstance().getRepository().getWritableDatabase().inTransaction()) {
            OpdLibrary.getInstance().getRepository().getWritableDatabase().setTransactionSuccessful();
            OpdLibrary.getInstance().getRepository().getWritableDatabase().endTransaction();
        }
    }

    private void updateLastInteractedWith(@NonNull Event event, @NonNull String visitId) throws
            CheckInEventProcessException {
        String tableName = OpdUtils.metadata().getTableName();

        String lastInteractedWithDate = String.valueOf(new Date().getTime());

        ContentValues contentValues = new ContentValues();
        contentValues.put("last_interacted_with", lastInteractedWithDate);

        int recordsUpdated = OpdLibrary.getInstance().getRepository().getWritableDatabase()
                .update(tableName, contentValues, "base_entity_id = ?", new String[]{event.getBaseEntityId()});

        if (recordsUpdated < 1) {
            abortTransaction();
            throw new CheckInEventProcessException(String.format("Updating last interacted with for visit %s for base_entity_id %s in table %s failed"
                    , visitId
                    , event.getBaseEntityId()
                    , tableName));
        }

        // Update FTS
        CommonRepository commonrepository = OpdLibrary.getInstance().context().commonrepository(tableName);

        ContentValues contentValues1 = new ContentValues();
        contentValues1.put("last_interacted_with", lastInteractedWithDate);

        boolean isUpdated = false;

        if (commonrepository.isFts()) {
            recordsUpdated = OpdLibrary.getInstance().getRepository().getWritableDatabase()
                    .update(CommonFtsObject.searchTableName(tableName), contentValues, CommonFtsObject.idColumn + " = ?", new String[]{event.getBaseEntityId()});
            isUpdated = recordsUpdated > 0;
        }

        if (!isUpdated) {
            abortTransaction();
            throw new CheckInEventProcessException(String.format("Updating last interacted with for visit %s for base_entity_id %s in table %s failed"
                    , visitId
                    , event.getBaseEntityId()
                    , tableName));
        }
    }

    @VisibleForTesting
    Date getDate() {
        return new Date();
    }

    private void generateKeyValuesFromEvent(@NonNull Event
                                                    event, @NonNull HashMap<String, String> keyValues) {
        List<Obs> obs = event.getObs();

        for (Obs observation : obs) {
            String key = observation.getFormSubmissionField();
            List<Object> values = observation.getValues();

            if (values.size() > 0) {
                String value = (String) values.get(0);

                if (!TextUtils.isEmpty(value)) {

                    if (values.size() > 1) {
                        value = values.toString();
                    }

                    keyValues.put(key, value);
                    continue;
                }
            }

            List<Object> humanReadableValues = observation.getHumanReadableValues();
            if (humanReadableValues.size() > 0) {
                String value = (String) humanReadableValues.get(0);

                if (!TextUtils.isEmpty(value)) {

                    if (values.size() > 1) {
                        value = values.toString();
                    }

                    keyValues.put(key, value);
                    continue;
                }
            }
        }
    }


}