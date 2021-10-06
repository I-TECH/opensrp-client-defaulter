package org.smartregister.kdp.repository;

import android.content.ContentValues;
import android.database.Cursor;

import androidx.annotation.NonNull;

import net.sqlcipher.database.SQLiteDatabase;

import org.apache.commons.lang3.NotImplementedException;
import org.smartregister.kdp.dao.UpdateDefaulterFormDao;
import org.smartregister.kdp.pojo.UpdateDefaulterForm;
import org.smartregister.kdp.util.KipConstants;
import org.smartregister.opd.utils.OpdDbConstants;
import org.smartregister.repository.BaseRepository;

import java.util.List;

public class UpdateDefaulterFormRepository extends BaseRepository implements UpdateDefaulterFormDao {

    private static final String INDEX_BASE_ENTITY_ID = "CREATE INDEX " + KipConstants.DbConstants.Tables.UPDATE_DEFAULTER_FORM_TABLE
            + "_" + KipConstants.DbConstants.Columns.UpdateDefaulterForm.BASE_ENTITY_ID + "_index ON " + KipConstants.DbConstants.Tables.UPDATE_DEFAULTER_FORM_TABLE +
            "(" + KipConstants.DbConstants.Columns.UpdateDefaulterForm.BASE_ENTITY_ID + " COLLATE NOCASE);";
    private String[] columns = new String[]{
            KipConstants.DbConstants.Columns.UpdateDefaulterForm.ID,
            KipConstants.DbConstants.Columns.UpdateDefaulterForm.BASE_ENTITY_ID,
            KipConstants.DbConstants.Columns.UpdateDefaulterForm.VISIT_ID,
            KipConstants.DbConstants.Columns.UpdateDefaulterForm.PHONE_TRACING_OUTCOME,
            KipConstants.DbConstants.Columns.UpdateDefaulterForm.PHYSICAL_TRACING_OUTCOME,
            KipConstants.DbConstants.Columns.UpdateDefaulterForm.PHONE_TRACING,
            KipConstants.DbConstants.Columns.UpdateDefaulterForm.PHYSICAL_TRACING,
            KipConstants.DbConstants.Columns.UpdateDefaulterForm.HOME_ADMINISTRATION_DATE,
            KipConstants.DbConstants.Columns.UpdateDefaulterForm.OTHER_FACILITY_ADMINISTRATION_DATE,
            KipConstants.DbConstants.Columns.UpdateDefaulterForm.OTHER_FACILITY_NAME,
            KipConstants.DbConstants.Columns.UpdateDefaulterForm.DATE_TO_CONFIRM_VACCINATION,
            KipConstants.DbConstants.Columns.UpdateDefaulterForm.MODE_OF_TRACING,
            KipConstants.DbConstants.Columns.UpdateDefaulterForm.AGE,
            KipConstants.DbConstants.Columns.UpdateDefaulterForm.DATE,
            KipConstants.DbConstants.Columns.UpdateDefaulterForm.CREATED_AT};

    public static void updateIndex(@NonNull SQLiteDatabase database) {
        database.execSQL(INDEX_BASE_ENTITY_ID);
    }

    @Override
    public boolean saveOrUpdate(UpdateDefaulterForm updateDefaulterForm) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(KipConstants.DbConstants.Columns.UpdateDefaulterForm.VISIT_ID, updateDefaulterForm.getVisitId());
        contentValues.put(KipConstants.DbConstants.Columns.UpdateDefaulterForm.ID, updateDefaulterForm.getId());
        contentValues.put(KipConstants.DbConstants.Columns.UpdateDefaulterForm.BASE_ENTITY_ID, updateDefaulterForm.getBaseEntityId());
        contentValues.put(KipConstants.DbConstants.Columns.UpdateDefaulterForm.PHONE_TRACING_OUTCOME, updateDefaulterForm.getPhoneTracingOutcome());
        contentValues.put(KipConstants.DbConstants.Columns.UpdateDefaulterForm.PHYSICAL_TRACING_OUTCOME, updateDefaulterForm.getPhysicalTracingOutcome());
        contentValues.put(KipConstants.DbConstants.Columns.UpdateDefaulterForm.PHONE_TRACING, updateDefaulterForm.getPhoneTracing());
        contentValues.put(KipConstants.DbConstants.Columns.UpdateDefaulterForm.PHYSICAL_TRACING, updateDefaulterForm.getPhysicalTracing());
        contentValues.put(KipConstants.DbConstants.Columns.UpdateDefaulterForm.HOME_ADMINISTRATION_DATE, updateDefaulterForm.getHomeAdministrationDate());
        contentValues.put(KipConstants.DbConstants.Columns.UpdateDefaulterForm.OTHER_FACILITY_ADMINISTRATION_DATE, updateDefaulterForm.getOtherFacilityAdministrationDate());
        contentValues.put(KipConstants.DbConstants.Columns.UpdateDefaulterForm.OTHER_FACILITY_NAME, updateDefaulterForm.getOtherFacilityName());
        contentValues.put(KipConstants.DbConstants.Columns.UpdateDefaulterForm.DATE_TO_CONFIRM_VACCINATION, updateDefaulterForm.getDateToConfirmVaccination());
        contentValues.put(KipConstants.DbConstants.Columns.UpdateDefaulterForm.MODE_OF_TRACING, updateDefaulterForm.getModeOfTracing());
        contentValues.put(KipConstants.DbConstants.Columns.UpdateDefaulterForm.AGE, updateDefaulterForm.getAge());
        contentValues.put(KipConstants.DbConstants.Columns.UpdateDefaulterForm.DATE, updateDefaulterForm.getDate());
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        contentValues.put(KipConstants.DbConstants.Columns.UpdateDefaulterForm.CREATED_AT, updateDefaulterForm.getCreatedAt());
        long rows = sqLiteDatabase.insertWithOnConflict(KipConstants.DbConstants.Tables.UPDATE_DEFAULTER_FORM_TABLE, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
        return rows != -1;
    }

    @Override
    public boolean save(UpdateDefaulterForm updateDefaulterForm) {
        throw new NotImplementedException("not implemented");
    }

    @Override
    public UpdateDefaulterForm findOne(UpdateDefaulterForm updateDefaulterForm) {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor cursor = sqLiteDatabase.query(KipConstants.DbConstants.Tables.UPDATE_DEFAULTER_FORM_TABLE
                , columns
                , KipConstants.DbConstants.Columns.RecordDefaulerForm.BASE_ENTITY_ID + " = ? "
                , new String[]{updateDefaulterForm.getBaseEntityId()}
                , null
                , null
                , OpdDbConstants.Column.OpdCheckIn.CREATED_AT + " DESC "
                , "1");

        if (cursor.getCount() == 0) {
            return null;
        }

        UpdateDefaulterForm updateDefaulter = null;
        if (cursor.moveToNext()) {
            updateDefaulter = new UpdateDefaulterForm(
                    cursor.getString(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getString(5),
                    cursor.getString(6),
                    cursor.getString(7),
                    cursor.getString(8),
                    cursor.getString(9),
                    cursor.getString(10),
                    cursor.getString(11),
                    cursor.getString(12),
                    cursor.getString(13),
                    cursor.getString(14));
        }
        cursor.close();
        return updateDefaulter;
    }

    @Override
    public boolean delete(UpdateDefaulterForm updateDefaulterForm) {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        int rows = sqLiteDatabase.delete(KipConstants.DbConstants.Tables.UPDATE_DEFAULTER_FORM_TABLE
                , KipConstants.DbConstants.Columns.RecordDefaulerForm.BASE_ENTITY_ID + " = ? "
                , new String[]{updateDefaulterForm.getBaseEntityId()});

        return rows > 0;
    }

    @Override
    public List<UpdateDefaulterForm> findAll() {
        throw new NotImplementedException("Not Implemented");
    }

    public UpdateDefaulterForm findOneByVisit(UpdateDefaulterForm updateDefaulterForm) {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor cursor = sqLiteDatabase.query(KipConstants.DbConstants.Tables.UPDATE_DEFAULTER_FORM_TABLE
                , columns
                , KipConstants.DbConstants.Columns.UpdateDefaulterForm.BASE_ENTITY_ID + " = ? AND " + KipConstants.DbConstants.Columns.UpdateDefaulterForm.VISIT_ID + " = ?"
                , new String[]{updateDefaulterForm.getBaseEntityId(), updateDefaulterForm.getVisitId()}
                , null
                , null
                , OpdDbConstants.Column.OpdCheckIn.CREATED_AT + " DESC "
                , "1");

        if (cursor.getCount() == 0) {
            return null;
        }

        UpdateDefaulterForm UpdateDefaulter = null;
        if (cursor.moveToNext()) {
            UpdateDefaulter = new UpdateDefaulterForm(
                    cursor.getString(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getString(5),
                    cursor.getString(6),
                    cursor.getString(7),
                    cursor.getString(8),
                    cursor.getString(9),
                    cursor.getString(10),
                    cursor.getString(11),
                    cursor.getString(12),
                    cursor.getString(13),
                    cursor.getString(14));
        }
        cursor.close();

        return UpdateDefaulter;
    }
}

