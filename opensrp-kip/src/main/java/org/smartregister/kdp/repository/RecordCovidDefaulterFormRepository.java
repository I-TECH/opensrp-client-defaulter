package org.smartregister.kdp.repository;

import android.content.ContentValues;
import android.database.Cursor;

import androidx.annotation.NonNull;

import net.sqlcipher.database.SQLiteDatabase;

import org.apache.commons.lang3.NotImplementedException;
import org.smartregister.kdp.dao.RecordCovidDefaulterFormDao;
import org.smartregister.kdp.pojo.RecordCovidDefaulterForm;
import org.smartregister.kdp.util.KipConstants;
import org.smartregister.opd.utils.OpdDbConstants;
import org.smartregister.repository.BaseRepository;

import java.util.List;

public class RecordCovidDefaulterFormRepository extends BaseRepository implements RecordCovidDefaulterFormDao {

    private static final String INDEX_BASE_ENTITY_ID = "CREATE INDEX " + KipConstants.DbConstants.Tables.RECORD_COVID_DEFAULTER_FORM
            + "_" + KipConstants.DbConstants.Columns.RecordCovidDefaulerForm.BASE_ENTITY_ID + "_index ON " + KipConstants.DbConstants.Tables.RECORD_COVID_DEFAULTER_FORM +
            "(" + KipConstants.DbConstants.Columns.RecordCovidDefaulerForm.BASE_ENTITY_ID + " COLLATE NOCASE);";
    private String[] columns = new String[]{
            KipConstants.DbConstants.Columns.RecordCovidDefaulerForm.ID,
            KipConstants.DbConstants.Columns.RecordCovidDefaulerForm.BASE_ENTITY_ID,
            KipConstants.DbConstants.Columns.RecordCovidDefaulerForm.VISIT_ID,
            KipConstants.DbConstants.Columns.RecordCovidDefaulerForm.COVID_ANTIGEN_ADMINISTERED_LAST,
            KipConstants.DbConstants.Columns.RecordCovidDefaulerForm.COVID_ADMINISTRATION_DATE,
            KipConstants.DbConstants.Columns.RecordCovidDefaulerForm.COVID_MISSED_VACCINE,
            KipConstants.DbConstants.Columns.RecordCovidDefaulerForm.COVID_RETURN_DATE,
            KipConstants.DbConstants.Columns.RecordCovidDefaulerForm.COVID_CHV_NAME,
            KipConstants.DbConstants.Columns.RecordCovidDefaulerForm.COVID_CHV_PHONE_NUMBER,
            KipConstants.DbConstants.Columns.RecordCovidDefaulerForm.AGE,
            KipConstants.DbConstants.Columns.RecordCovidDefaulerForm.DATE,
            KipConstants.DbConstants.Columns.RecordCovidDefaulerForm.CREATED_AT};

    public static void updateIndex(@NonNull SQLiteDatabase database) {
        database.execSQL(INDEX_BASE_ENTITY_ID);
    }

    @Override
    public boolean saveOrUpdate(RecordCovidDefaulterForm recordCovidDefaulterForm) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(KipConstants.DbConstants.Columns.RecordCovidDefaulerForm.VISIT_ID, recordCovidDefaulterForm.getVisitId());
        contentValues.put(KipConstants.DbConstants.Columns.RecordCovidDefaulerForm.ID, recordCovidDefaulterForm.getId());
        contentValues.put(KipConstants.DbConstants.Columns.RecordCovidDefaulerForm.BASE_ENTITY_ID, recordCovidDefaulterForm.getBaseEntityId());
        contentValues.put(KipConstants.DbConstants.Columns.RecordCovidDefaulerForm.COVID_ANTIGEN_ADMINISTERED_LAST, recordCovidDefaulterForm.getCovidAntigenAdministeredLast());
        contentValues.put(KipConstants.DbConstants.Columns.RecordCovidDefaulerForm.COVID_ADMINISTRATION_DATE, recordCovidDefaulterForm.getCovidAdministrationDate());
        contentValues.put(KipConstants.DbConstants.Columns.RecordCovidDefaulerForm.COVID_MISSED_VACCINE, recordCovidDefaulterForm.getCovidMissedDoses());
        contentValues.put(KipConstants.DbConstants.Columns.RecordCovidDefaulerForm.COVID_RETURN_DATE, recordCovidDefaulterForm.getCovidReturnDate());
        contentValues.put(KipConstants.DbConstants.Columns.RecordCovidDefaulerForm.COVID_CHV_NAME, recordCovidDefaulterForm.getCovidChvName());
        contentValues.put(KipConstants.DbConstants.Columns.RecordCovidDefaulerForm.COVID_CHV_PHONE_NUMBER, recordCovidDefaulterForm.getCovidChvPhoneNumber());
        contentValues.put(KipConstants.DbConstants.Columns.RecordCovidDefaulerForm.AGE, recordCovidDefaulterForm.getAge());
        contentValues.put(KipConstants.DbConstants.Columns.RecordCovidDefaulerForm.DATE, recordCovidDefaulterForm.getDate());
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        contentValues.put(KipConstants.DbConstants.Columns.RecordCovidDefaulerForm.CREATED_AT, recordCovidDefaulterForm.getCreatedAt());
        long rows = sqLiteDatabase.insertWithOnConflict(KipConstants.DbConstants.Tables.RECORD_COVID_DEFAULTER_FORM, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
        return rows != -1;
    }

    @Override
    public boolean save(RecordCovidDefaulterForm recordCovidDefaulterForm) {
        throw new NotImplementedException("not implemented");
    }

    @Override
    public RecordCovidDefaulterForm findOne(RecordCovidDefaulterForm recordCovidDefaulterForm) {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor cursor = sqLiteDatabase.query(KipConstants.DbConstants.Tables.RECORD_COVID_DEFAULTER_FORM
                , columns
                , KipConstants.DbConstants.Columns.RecordCovidDefaulerForm.BASE_ENTITY_ID + " = ? "
                , new String[]{recordCovidDefaulterForm.getBaseEntityId()}
                , null
                , null
                , OpdDbConstants.Column.OpdCheckIn.CREATED_AT + " DESC "
                , "1");

        if (cursor.getCount() == 0) {
            return null;
        }

        RecordCovidDefaulterForm covidDefaulterForm = null;
        if (cursor.moveToNext()) {
            covidDefaulterForm = new RecordCovidDefaulterForm(
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
                    cursor.getString(11));
        }
        cursor.close();
        return covidDefaulterForm;
    }

    @Override
    public boolean delete(RecordCovidDefaulterForm recordCovidDefaulterForm) {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        int rows = sqLiteDatabase.delete(KipConstants.DbConstants.Tables.RECORD_COVID_DEFAULTER_FORM
                , KipConstants.DbConstants.Columns.RecordCovidDefaulerForm.BASE_ENTITY_ID + " = ? "
                , new String[]{recordCovidDefaulterForm.getBaseEntityId()});

        return rows > 0;
    }

    @Override
    public List<RecordCovidDefaulterForm> findAll() {
        throw new NotImplementedException("Not Implemented");
    }

    public RecordCovidDefaulterForm findOneByVisit(RecordCovidDefaulterForm recordCovidDefaulterForm) {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor cursor = sqLiteDatabase.query(KipConstants.DbConstants.Tables.RECORD_COVID_DEFAULTER_FORM
                , columns
                , KipConstants.DbConstants.Columns.RecordCovidDefaulerForm.BASE_ENTITY_ID + " = ? AND " + KipConstants.DbConstants.Columns.RecordCovidDefaulerForm.VISIT_ID + " = ?"
                , new String[]{recordCovidDefaulterForm.getBaseEntityId(), recordCovidDefaulterForm.getVisitId()}
                , null
                , null
                , OpdDbConstants.Column.OpdCheckIn.CREATED_AT + " DESC "
                , "1");

        if (cursor.getCount() == 0) {
            return null;
        }

        RecordCovidDefaulterForm covidDefaulterForm = null;
        if (cursor.moveToNext()) {
            covidDefaulterForm = new RecordCovidDefaulterForm(
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
                    cursor.getString(11));
        }
        cursor.close();

        return covidDefaulterForm;
    }
}
