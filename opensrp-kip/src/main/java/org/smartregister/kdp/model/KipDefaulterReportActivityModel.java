package org.smartregister.kdp.model;

import androidx.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.domain.tag.FormTag;
import org.smartregister.location.helper.LocationHelper;
import org.smartregister.opd.contract.OpdRegisterActivityContract;
import org.smartregister.opd.pojo.OpdEventClient;
import org.smartregister.opd.utils.OpdJsonFormUtils;
import org.smartregister.opd.utils.OpdUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class KipDefaulterReportActivityModel implements OpdRegisterActivityContract.Model{

    @Override
    public void registerViewConfigurations(List<String> list) {

    }

    @Override
    public void unregisterViewConfiguration(List<String> list) {

    }

    @Override
    public void saveLanguage(String s) {

    }

    @Override
    public String getLocationId(@Nullable String locationName) {
        return LocationHelper.getInstance().getOpenMrsLocationId(locationName);
    }

    @Override
    public List<OpdEventClient> processRegistration(String jsonString, FormTag formTag) {
        List<OpdEventClient> opdEventClientList = new ArrayList<>();
        OpdEventClient opdEventClient = OpdJsonFormUtils.processOpdDetailsForm(jsonString, formTag);

        if (opdEventClient == null) {
            return null;
        }

        opdEventClientList.add(opdEventClient);
        return opdEventClientList;
    }

    @Nullable
    @Override
    public JSONObject getFormAsJson(String formName, String entityId, String currentLocationId) throws JSONException {
        return getFormAsJson(formName, entityId, currentLocationId, null);
    }

    @Nullable
    @Override
    public JSONObject getFormAsJson(String formName, String entityId,
                                    String currentLocationId, @Nullable HashMap<String, String> injectedValues) throws JSONException {
        JSONObject form = OpdUtils.getJsonFormToJsonObject(formName);
        if (form != null) {
            return OpdJsonFormUtils.getFormAsJson(form, formName, entityId, currentLocationId, injectedValues);
        }
        return null;
    }

    @Override
    public String getInitials() {
        return null;
    }
}
