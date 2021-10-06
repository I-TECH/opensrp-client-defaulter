package org.smartregister.kdp.repository;

import android.content.ContentValues;
import android.database.Cursor;

import androidx.annotation.NonNull;

import net.sqlcipher.database.SQLiteDatabase;

import org.apache.commons.lang3.NotImplementedException;
import org.smartregister.kdp.dao.UpdateCovidDefaulterFormDao;
import org.smartregister.kdp.pojo.UpdateCovidDefaulterForm;
import org.smartregister.kdp.util.KipConstants;
import org.smartregister.opd.utils.OpdDbConstants;
import org.smartregister.repository.BaseRepository;

import java.util.List;

public class UpdateCovidDefaulterFormRepository extends BaseRepository implements UpdateCovidDefaulterFormDao {

    private static final String INDEX_BASE_ENTITY_ID = "CREATE INDEX " + KipConstants.DbConstants.Tables.UPDATE_COVID_DEFAULTER_FORM_TABLE
            + "_" + KipConstants.DbConstants.Columns.UpdateCovidDefaulterForm.BASE_ENTITY_ID + "_index ON " + KipConstants.DbConstants.Tables.UPDATE_COVID_DEFAULTER_FORM_TABLE +
            "(" + KipConstants.DbConstants.Columns.UpdateCovidDefaulterForm.BASE_ENTITY_ID + " COLLATE NOCASE);";
    private String[] columns = new String[]{
            KipConstants.DbConstants.Columns.UpdateCovidDefaulterForm.ID,
            KipConstants.DbConstants.Columns.UpdateCovidDefaulterForm.BASE_ENTITY_ID,
            KipConstants.DbConstants.Columns.UpdateCovidDefaulterForm.VISIT_ID,
            KipConstants.DbConstants.Columns.UpdateCovidDefaulterForm.COVID_PHONE_TRACING_OUTCOME,
            KipConstants.DbConstants.Columns.UpdateCovidDefaulterForm.COVID_PHYSICAL_TRACING_OUTCOME,
            KipConstants.DbConstants.Columns.UpdateCovidDefaulterForm.COVID_PHONE_TRACING,
            KipConstants.DbConstants.Columns.UpdateCovidDefaulterForm.COVID_PHYSICAL_TRACING,
            KipConstants.DbConstants.Columns.UpdateCovidDefaulterForm.COVID_HOME_ADMINISTRATION_DATE,
            KipConstants.DbConstants.Columns.UpdateCovidDefaulterForm.COVID_OTHER_FACILITY_ADMINISTRATION_DATE,
            KipConstants.DbConstants.Columns.UpdateCovidDefaulterForm.COVID_OTHER_FACILITY_NAME,
            KipConstants.DbConstants.Columns.UpdateCovidDefaulterForm.COVID_DATE_TO_CONFIRM_VACCINATION,
            KipConstants.DbConstants.Columns.UpdateCovidDefaulterForm.COVID_MODE_OF_TRACING,
            KipConstants.DbConstants.Columns.UpdateCovidDefaulterForm.AGE,
            KipConstants.DbConstants.Columns.UpdateCovidDefaulterForm.DATE,
            KipConstants.DbConstants.Columns.UpdateCovidDefaulterForm.CREATED_AT};

    public static void updateIndex(@NonNull SQLiteDatabase database) {
        database.execSQL(INDEX_BASE_ENTITY_ID);
    }

    @Override
    public boolean saveOrUpdate(UpdateCovidDefaulterForm updateCovidDefaulterForm) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(KipConstants.DbConstants.Columns.UpdateCovidDefaulterForm.VISIT_ID, updateCovidDefaulterForm.getVisitId());
        contentValues.put(KipConstants.DbConstants.Columns.UpdateCovidDefaulterForm.ID, updateCovidDefaulterForm.getId());
        contentValues.put(KipConstants.DbConstants.Columns.UpdateCovidDefaulterForm.BASE_ENTITY_ID, updateCovidDefaulterForm.getBaseEntityId());
        contentValues.put(KipConstants.DbConstants.Columns.UpdateCovidDefaulterForm.COVID_PHONE_TRACING_OUTCOME, updateCovidDefaulterForm.getCovidPhoneTracingOutcome());
        contentValues.put(KipConstants.DbConstants.Columns.UpdateCovidDefaulterForm.COVID_PHYSICAL_TRACING_OUTCOME, updateCovidDefaulterForm.getCovidPhysicalTracingOutcome());
        contentValues.put(KipConstants.DbConstants.Columns.UpdateCovidDefaulterForm.COVID_PHONE_TRACING, updateCovidDefaulterForm.getCovidPhoneTracing());
        contentValues.put(KipConstants.DbConstants.Columns.UpdateCovidDefaulterForm.COVID_PHYSICAL_TRACING, updateCovidDefaulterForm.getCovidPhysicalTracing());
        contentValues.put(KipConstants.DbConstants.Columns.UpdateCovidDefaulterForm.COVID_HOME_ADMINISTRATION_DATE, updateCovidDefaulterForm.getCovidHomeAdministrationDate());
        contentValues.put(KipConstants.DbConstants.Columns.UpdateCovidDefaulterForm.COVID_OTHER_FACILITY_ADMINISTRATION_DATE, updateCovidDefaulterForm.getCovidOtherFacilityAdministrationDate());
        contentValues.put(KipConstants.DbConstants.Columns.UpdateCovidDefaulterForm.COVID_OTHER_FACILITY_NAME, updateCovidDefaulterForm.getCovidOtherFacilityName());
        contentValues.put(KipConstants.DbConstants.Columns.UpdateCovidDefaulterForm.COVID_DATE_TO_CONFIRM_VACCINATION, updateCovidDefaulterForm.getCovidDateToConfirmVaccination());
        contentValues.put(KipConstants.DbConstants.Columns.UpdateCovidDefaulterForm.COVID_MODE_OF_TRACING, updateCovidDefaulterForm.getCovidModeOfTracing());
        contentValues.put(KipConstants.DbConstants.Columns.UpdateCovidDefaulterForm.AGE, updateCovidDefaulterForm.getAge());
        contentValues.put(KipConstants.DbConstants.Columns.UpdateCovidDefaulterForm.DATE, updateCovidDefaulterForm.getDate());
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        contentValues.put(KipConstants.DbConstants.Columns.UpdateCovidDefaulterForm.CREATED_AT, updateCovidDefaulterForm.getCreatedAt());
        long rows = sqLiteDatabase.insertWithOnConflict(KipConstants.DbConstants.Tables.UPDATE_COVID_DEFAULTER_FORM_TABLE, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
        return rows != -1;
    }

    @Override
    public boolean save(UpdateCovidDefaulterForm updateCovidDefaulterForm) {
        throw new NotImplementedException("not implemented");
    }

    @Override
    public UpdateCovidDefaulterForm findOne(UpdateCovidDefaulterForm updateCovidDefaulterForm) {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor cursor = sqLiteDatabase.query(KipConstants.DbConstants.Tables.UPDATE_COVID_DEFAULTER_FORM_TABLE
                , columns
                , KipConstants.DbConstants.Columns.RecordCovidDefaulerForm.BASE_ENTITY_ID + " = ? "
                , new String[]{updateCovidDefaulterForm.getBaseEntityId()}
                , null
                , null
                , OpdDbConstants.Column.OpdCheckIn.CREATED_AT + " DESC "
                , "1");

        if (cursor.getCount() == 0) {
            return null;
        }

        UpdateCovidDefaulterForm updateDefaulter = null;
        if (cursor.moveToNext()) {
            updateDefaulter = new UpdateCovidDefaulterForm(
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
    public boolean delete(UpdateCovidDefaulterForm updateDefaulterForm) {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        int rows = sqLiteDatabase.delete(KipConstants.DbConstants.Tables.UPDATE_DEFAULTER_FORM_TABLE
                , KipConstants.DbConstants.Columns.RecordCovidDefaulerForm.BASE_ENTITY_ID + " = ? "
                , new String[]{updateDefaulterForm.getBaseEntityId()});

        return rows > 0;
    }

    @Override
    public List<UpdateCovidDefaulterForm> findAll() {
        throw new NotImplementedException("Not Implemented");
    }

    public UpdateCovidDefaulterForm findOneByVisit(UpdateCovidDefaulterForm updateDefaulterForm) {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor cursor = sqLiteDatabase.query(KipConstants.DbConstants.Tables.UPDATE_DEFAULTER_FORM_TABLE
                , columns
                , KipConstants.DbConstants.Columns.UpdateCovidDefaulterForm.BASE_ENTITY_ID + " = ? AND " + KipConstants.DbConstants.Columns.UpdateCovidDefaulterForm.VISIT_ID + " = ?"
                , new String[]{updateDefaulterForm.getBaseEntityId(), updateDefaulterForm.getVisitId()}
                , null
                , null
                , OpdDbConstants.Column.OpdCheckIn.CREATED_AT + " DESC "
                , "1");

        if (cursor.getCount() == 0) {
            return null;
        }

        UpdateCovidDefaulterForm UpdateDefaulter = null;
        if (cursor.moveToNext()) {
            UpdateDefaulter = new UpdateCovidDefaulterForm(
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
