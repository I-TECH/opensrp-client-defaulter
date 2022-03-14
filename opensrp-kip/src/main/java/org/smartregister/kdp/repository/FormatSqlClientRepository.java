package org.smartregister.kdp.repository;

import android.database.Cursor;

import net.sqlcipher.database.SQLiteDatabase;

import org.smartregister.kdp.domain.InvalidClient;
import org.smartregister.repository.BaseRepository;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class FormatSqlClientRepository extends BaseRepository {

    public static final String CLIENT_TABLE_NAME = "client";
    public static final String JSON_COLUMN = "json";
    public static final String BASE_ENTITY_ID = "baseEntityId";
    public static final String VALIDATION_STATUS = "validationStatus";
    public static final String[] CLIENT_TABLE_COLUMNS = {BASE_ENTITY_ID,JSON_COLUMN};

    public FormatSqlClientRepository(){super();}

    public List<InvalidClient> getInvalidClient(){
        String validationStatus = "Invalid";
        List<InvalidClient> clients = new ArrayList<>();
        try (Cursor cursor = getReadableDatabase().query(CLIENT_TABLE_NAME, CLIENT_TABLE_COLUMNS, VALIDATION_STATUS + " = ?", new String[] {validationStatus}, null, null, " 1 ASC")) {
            clients = readAll(cursor);
        } catch (Exception e){
            Timber.e(e, "getInvalidClient");
        }
        Timber.d("--->getInvalidClient() %s",clients.toString());
        return clients;
    }

    private List<InvalidClient> readAll(Cursor cursor){

        List<InvalidClient> client = new ArrayList<>();
        if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()){
            cursor.moveToFirst();
            while (cursor.getCount() > 0 && !cursor.isAfterLast()){
                client.add(getClients(cursor));
                cursor.moveToNext();
            }
        }
        return client;
    }

    private InvalidClient getClients(Cursor cursor) {
        InvalidClient invalidClient = new InvalidClient();

        invalidClient.setBaseEntityId(cursor.getString(cursor.getColumnIndex(BASE_ENTITY_ID)));
        invalidClient.setJsonObject(cursor.getString(cursor.getColumnIndex(JSON_COLUMN)));
        return invalidClient;
    }

    public SQLiteDatabase getDatabase(){
        return getWritableDatabase();
    }

}
