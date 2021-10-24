package org.smartregister.kdp.service;

import android.content.ContentValues;

import net.sqlcipher.database.SQLiteDatabase;

import org.apache.commons.lang3.RandomStringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.kdp.application.KipApplication;
import org.smartregister.kdp.domain.InvalidClient;
import org.smartregister.kdp.repository.FormatSqlClientRepository;

import java.util.List;

import timber.log.Timber;

import static org.smartregister.kdp.repository.FormatSqlClientRepository.BASE_ENTITY_ID;
import static org.smartregister.kdp.repository.FormatSqlClientRepository.CLIENT_TABLE_NAME;
import static org.smartregister.kdp.repository.FormatSqlClientRepository.JSON_COLUMN;

public class FormatClientOpensrpId {

    FormatSqlClientRepository client = KipApplication.getInstance().formatSqlDateRepository();
    List<InvalidClient> invalidClients = client.getInvalidClient();

    public void getClientsJson() throws JSONException {
        SQLiteDatabase database = client.getDatabase();

        for (int i = 0; i<invalidClients.size() ; i++){
            JSONObject json = new JSONObject(
                    invalidClients.get(i).getJsonObject()
            );

            String opensrpId = json.getJSONObject("identifiers").getString("OPENSRP_ID");
            String formatedOpensrpId = formatOpenSrpId(opensrpId);



////            remove OPENSRP_ID
            json.getJSONObject("identifiers").remove("OPENSRP_ID");

//            put OPENSRP_ID
            json.getJSONObject("identifiers").put("OPENSRP_ID",formatedOpensrpId);


            try {
                String baseId = json.getString(BASE_ENTITY_ID);
                String clientJsonString = String.valueOf(json);
                database.beginTransaction();
                ContentValues contentValues = new ContentValues();
                contentValues.put(JSON_COLUMN,clientJsonString);
                database.update(CLIENT_TABLE_NAME, contentValues, BASE_ENTITY_ID + " = ?", new String[] {baseId});
                database.setTransactionSuccessful();
                Timber.d("-->Clients table update %s", contentValues);

            } catch (Exception e){
                Timber.e(e, "-->updateInvalidClients");
            } finally {
                database.endTransaction();
            }

            System.out.println(json);
        }
    }

    private String formatOpenSrpId(String opensrpId){
        String format = RandomStringUtils.randomAlphanumeric(4);
        String formatedId = opensrpId + format ;

        return formatedId;
    }

}
