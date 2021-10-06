package org.smartregister.kdp.processor;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.clientandeventmodel.FormEntityConstants;
import org.smartregister.domain.tag.FormTag;
import org.smartregister.kdp.repository.KipOpdDetailsRepository;
import org.smartregister.kdp.util.KipConstants;
import org.smartregister.kdp.util.KipOpdConstants;
import org.smartregister.opd.OpdLibrary;
import org.smartregister.opd.configuration.OpdFormProcessor;
import org.smartregister.opd.utils.OpdConstants;
import org.smartregister.opd.utils.OpdDbConstants;
import org.smartregister.opd.utils.OpdJsonFormUtils;
import org.smartregister.opd.utils.OpdUtils;
import org.smartregister.repository.EventClientRepository;
import org.smartregister.sync.ClientProcessor;
import org.smartregister.util.JsonFormUtils;
import org.smartregister.util.Utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import timber.log.Timber;
import static org.smartregister.opd.utils.OpdJsonFormUtils.METADATA;

public class KipMiniProcessor implements OpdFormProcessor<List<Event>> {
    /***
     * This method creates an event for each step in the Opd Diagnose and treatment form
     * @param jsonFormObject {@link JSONObject}
     * @param data {@link Intent}
     * @return {@link List}
     */
    @Nullable
    @Override
    public List<Event> processForm(@NonNull JSONObject jsonFormObject, @NonNull Intent data) throws JSONException {
        String entityId = OpdUtils.getIntentValue(data, OpdConstants.IntentKey.BASE_ENTITY_ID);
        String entityTable = OpdUtils.getIntentValue(data, OpdConstants.IntentKey.ENTITY_TABLE);
        JSONArray fieldsArray = OpdJsonFormUtils.getMultiStepFormFields(jsonFormObject);
        FormTag formTag = OpdJsonFormUtils.formTag(Utils.getAllSharedPreferences());
        JSONObject metadata = jsonFormObject.getJSONObject(OpdJsonFormUtils.METADATA);
        Event closeOpdEvent = JsonFormUtils.createEvent(fieldsArray, metadata, formTag, entityId, OpdConstants.EventType.OPD_CLOSE, entityTable);

        if (StringUtils.isNotBlank(entityId)) {
            Map<String, String> opdCheckInMap = OpdLibrary.getInstance().getCheckInRepository().getLatestCheckIn(entityId);
//            FormTag formTag = OpdJsonFormUtils.formTag(OpdUtils.getAllSharedPreferences());

            if (opdCheckInMap != null && !opdCheckInMap.isEmpty()) {
                String visitId = opdCheckInMap.get(OpdDbConstants.Column.OpdCheckIn.VISIT_ID);
                String visitDate = opdCheckInMap.get("date");
                String steps = jsonFormObject.optString(JsonFormConstants.COUNT);
                String encounterType = jsonFormObject.optString(JsonFormConstants.ENCOUNTER_TYPE);
                int numOfSteps = Integer.parseInt(steps);
                List<Event> eventList = new ArrayList<>();

                for (int j = 0; j < numOfSteps; j++) {
                    JSONObject step = jsonFormObject.optJSONObject(JsonFormConstants.STEP.concat(String.valueOf(j + 1)));
                    String bindType = step.optString(OpdConstants.BIND_TYPE);
                    JSONArray fields = step.optJSONArray(OpdJsonFormUtils.FIELDS);
                    JSONArray multiSelectListValueJsonArray = null;

                    Event baseEvent = JsonFormUtils.createEvent(fields, jsonFormObject.optJSONObject(METADATA), formTag, entityId, encounterType, bindType);
                    OpdJsonFormUtils.tagSyncMetadata(baseEvent);
                    baseEvent.addDetails(KipOpdConstants.VISIT_ID, visitId);
                    baseEvent.addDetails(KipOpdConstants.VISIT_DATE, visitDate);
                    baseEvent.setEntityType(encounterType);
                    baseEvent.setEventType(encounterType);

                    if (multiSelectListValueJsonArray != null) {
                        baseEvent.addDetails(OpdConstants.KEY.VALUE, multiSelectListValueJsonArray.toString());
                    }
                    eventList.add(baseEvent);
                }

                if (encounterType.equals(KipConstants.EventType.UPDATE_DEFAULT)){
                    closeOpdVisit(entityId,formTag,visitId,eventList);
                    KipOpdDetailsRepository.restDefaulterSchedule(entityId);
                }

                if (encounterType.equals(KipConstants.EventType.UPDATE_COVID_DEFAULT)){
                    closeOpdVisit(entityId,formTag,visitId,eventList);
                    KipOpdDetailsRepository.restCovidDefaulterSchedule(entityId);
                }

                return eventList;
            } else {
                Timber.e("Corresponding OpdCheckIn record for EntityId %s is missing", entityId);
                return null;
            }
        }
        processWomanDiedEvent(fieldsArray, closeOpdEvent);
        return null;
    }

    private void closeOpdVisit(String entityId, FormTag formTag, String visitId, List<Event> eventList) {
        Event closeOpdVisit = JsonFormUtils.createEvent(new JSONArray(), new JSONObject(), formTag, entityId, OpdConstants.EventType.CLOSE_OPD_VISIT, "");
        closeOpdVisit.setEventType(OpdConstants.EventType.CLOSE_OPD_VISIT);
        closeOpdVisit.setEntityType(OpdConstants.EventType.CLOSE_OPD_VISIT);
        OpdJsonFormUtils.tagSyncMetadata(closeOpdVisit);
        closeOpdVisit.addDetails(OpdConstants.JSON_FORM_KEY.VISIT_ID, visitId);
        closeOpdVisit.addDetails(OpdConstants.JSON_FORM_KEY.VISIT_END_DATE, OpdUtils.convertDate(new Date(), OpdConstants.DateFormat.YYYY_MM_DD_HH_MM_SS));
        eventList.add(closeOpdVisit);
    }

    protected void processWomanDiedEvent(JSONArray fieldsArray, Event event) throws JSONException {
        if (OpdConstants.KEY.DIED.equals(JsonFormUtils.getFieldValue(fieldsArray, OpdConstants.JSON_FORM_KEY.OPD_CLOSE_REASON))) {
            event.setEventType(OpdConstants.EventType.DEATH);
            createDeathEventObject(event, fieldsArray);
        }
    }

    private void createDeathEventObject(Event event, JSONArray fieldsArray) throws JSONException {
        JSONObject eventJson = new JSONObject(JsonFormUtils.gson.toJson(event));

        EventClientRepository db = OpdLibrary.getInstance().eventClientRepository();

        JSONObject client = db.getClientByBaseEntityId(eventJson.getString(ClientProcessor.baseEntityIdJSONKey));
        String dateOfDeath = JsonFormUtils.getFieldValue(fieldsArray, OpdConstants.JSON_FORM_KEY.DATE_OF_DEATH);
        client.put(OpdConstants.JSON_FORM_KEY.DEATH_DATE, StringUtils.isNotBlank(dateOfDeath) ? OpdUtils.reverseHyphenSeperatedValues(dateOfDeath, "-") : OpdUtils.getTodaysDate());
        client.put(FormEntityConstants.Person.deathdate_estimated.name(), false);
        client.put(OpdConstants.JSON_FORM_KEY.DEATH_DATE_APPROX, false);

        JSONObject attributes = client.getJSONObject(OpdConstants.JSON_FORM_KEY.ATTRIBUTES);
        attributes.put(OpdConstants.JSON_FORM_KEY.DATE_REMOVED, OpdUtils.getTodaysDate());

        db.addorUpdateClient(event.getBaseEntityId(), client);

        db.addEvent(event.getBaseEntityId(), eventJson);

        Event updateClientDetailsEvent = (Event) new Event().withBaseEntityId(event.getBaseEntityId())
                .withEventDate(DateTime.now().toDate()).withEventType(OpdUtils.metadata().getUpdateEventType()).withLocationId(event.getLocationId())
                .withProviderId(event.getLocationId()).withEntityType(event.getEntityType())
                .withFormSubmissionId(JsonFormUtils.generateRandomUUIDString()).withDateCreated(new Date());

        JSONObject eventJsonUpdateClientEvent = new JSONObject(JsonFormUtils.gson.toJson(updateClientDetailsEvent));

        db.addEvent(event.getBaseEntityId(), eventJsonUpdateClientEvent);
    }



}
