package org.smartregister.kdp.repository;

import android.content.ContentValues;
import android.database.Cursor;

import androidx.annotation.NonNull;

import net.sqlcipher.database.SQLiteDatabase;

import org.apache.commons.lang3.NotImplementedException;
import org.smartregister.kdp.dao.RecordDefaulterFormDao;
import org.smartregister.kdp.pojo.RecordDefaulterForm;
import org.smartregister.kdp.util.KipConstants;
import org.smartregister.opd.utils.OpdDbConstants;
import org.smartregister.repository.BaseRepository;

import java.util.List;

public class RecordDefaulterFormRepository extends BaseRepository implements RecordDefaulterFormDao {

    private static final String INDEX_BASE_ENTITY_ID = "CREATE INDEX " + KipConstants.DbConstants.Tables.RECORD_DEFAULTER_FORM
            + "_" + KipConstants.DbConstants.Columns.VaccineRecord.BASE_ENTITY_ID + "_index ON " + KipConstants.DbConstants.Tables.RECORD_DEFAULTER_FORM +
            "(" + KipConstants.DbConstants.Columns.VaccineRecord.BASE_ENTITY_ID + " COLLATE NOCASE);";
    private String[] columns = new String[]{
            KipConstants.DbConstants.Columns.RecordDefaulerForm.ID,
            KipConstants.DbConstants.Columns.RecordDefaulerForm.BASE_ENTITY_ID,
            KipConstants.DbConstants.Columns.RecordDefaulerForm.VISIT_ID,
            KipConstants.DbConstants.Columns.RecordDefaulerForm.ANTIGEN_ADMINISTERED_LAST,
            KipConstants.DbConstants.Columns.RecordDefaulerForm.ADMINISTRATION_DATE,
            KipConstants.DbConstants.Columns.RecordDefaulerForm.MISSED_VACCINE,
            KipConstants.DbConstants.Columns.RecordDefaulerForm.RETURN_DATE,
            KipConstants.DbConstants.Columns.RecordDefaulerForm.CHV_NAME,
            KipConstants.DbConstants.Columns.RecordDefaulerForm.CHV_PHONE_NUMBER,
            KipConstants.DbConstants.Columns.RecordDefaulerForm.BIRTH_DOSE_ANTIGEN,
            KipConstants.DbConstants.Columns.RecordDefaulerForm.SIX_WKS_ANTIGEN,
            KipConstants.DbConstants.Columns.RecordDefaulerForm.TEN_WKS_ANTIGEN,
            KipConstants.DbConstants.Columns.RecordDefaulerForm.FOURTEEN_WKS_ANTIGEN,
            KipConstants.DbConstants.Columns.RecordDefaulerForm.NINE_MONTHS_ANTIGEN,
            KipConstants.DbConstants.Columns.RecordDefaulerForm.EIGHTEEN_MONTHS_ANTIGEN,
            KipConstants.DbConstants.Columns.RecordDefaulerForm.AGE,
            KipConstants.DbConstants.Columns.RecordDefaulerForm.DATE,
            KipConstants.DbConstants.Columns.RecordDefaulerForm.CREATED_AT};

    public static void updateIndex(@NonNull SQLiteDatabase database) {
        database.execSQL(INDEX_BASE_ENTITY_ID);
    }

    @Override
    public boolean saveOrUpdate(RecordDefaulterForm recordDefaulterForm) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(KipConstants.DbConstants.Columns.RecordDefaulerForm.VISIT_ID, recordDefaulterForm.getVisitId());
        contentValues.put(KipConstants.DbConstants.Columns.RecordDefaulerForm.ID, recordDefaulterForm.getId());
        contentValues.put(KipConstants.DbConstants.Columns.RecordDefaulerForm.BASE_ENTITY_ID, recordDefaulterForm.getBaseEntityId());
        contentValues.put(KipConstants.DbConstants.Columns.RecordDefaulerForm.ANTIGEN_ADMINISTERED_LAST, recordDefaulterForm.getAntigenAdministeredLast());
        contentValues.put(KipConstants.DbConstants.Columns.RecordDefaulerForm.ADMINISTRATION_DATE, recordDefaulterForm.getAdministrationDate());
        contentValues.put(KipConstants.DbConstants.Columns.RecordDefaulerForm.MISSED_VACCINE, recordDefaulterForm.getMissedDoses());
        contentValues.put(KipConstants.DbConstants.Columns.RecordDefaulerForm.RETURN_DATE, recordDefaulterForm.getReturnDate());
        contentValues.put(KipConstants.DbConstants.Columns.RecordDefaulerForm.CHV_NAME, recordDefaulterForm.getChvName());
        contentValues.put(KipConstants.DbConstants.Columns.RecordDefaulerForm.CHV_PHONE_NUMBER, recordDefaulterForm.getChvPhoneNumber());
        contentValues.put(KipConstants.DbConstants.Columns.RecordDefaulerForm.BIRTH_DOSE_ANTIGEN, recordDefaulterForm.getBirthDoseAntigen());
        contentValues.put(KipConstants.DbConstants.Columns.RecordDefaulerForm.SIX_WKS_ANTIGEN, recordDefaulterForm.getSixWksAntigen());
        contentValues.put(KipConstants.DbConstants.Columns.RecordDefaulerForm.TEN_WKS_ANTIGEN, recordDefaulterForm.getTenWksAntigen());
        contentValues.put(KipConstants.DbConstants.Columns.RecordDefaulerForm.FOURTEEN_WKS_ANTIGEN, recordDefaulterForm.getFourteenWksAntigen());
        contentValues.put(KipConstants.DbConstants.Columns.RecordDefaulerForm.NINE_MONTHS_ANTIGEN, recordDefaulterForm.getNineMonthsAntigen());
        contentValues.put(KipConstants.DbConstants.Columns.RecordDefaulerForm.EIGHTEEN_MONTHS_ANTIGEN, recordDefaulterForm.getEighteenMonthsAntigen());
        contentValues.put(KipConstants.DbConstants.Columns.RecordDefaulerForm.AGE, recordDefaulterForm.getAge());
        contentValues.put(KipConstants.DbConstants.Columns.RecordDefaulerForm.DATE, recordDefaulterForm.getDate());
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        contentValues.put(KipConstants.DbConstants.Columns.VaccineRecord.CREATED_AT, recordDefaulterForm.getCreatedAt());
        long rows = sqLiteDatabase.insertWithOnConflict(KipConstants.DbConstants.Tables.RECORD_DEFAULTER_FORM, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
        return rows != -1;
    }

    @Override
    public boolean save(RecordDefaulterForm recordDefaulterForm) {
        throw new NotImplementedException("not implemented");
    }

    @Override
    public RecordDefaulterForm findOne(RecordDefaulterForm recordDefaulterForm) {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor cursor = sqLiteDatabase.query(KipConstants.DbConstants.Tables.RECORD_DEFAULTER_FORM
                , columns
                , KipConstants.DbConstants.Columns.RecordDefaulerForm.BASE_ENTITY_ID + " = ? "
                , new String[]{recordDefaulterForm.getBaseEntityId()}
                , null
                , null
                , OpdDbConstants.Column.OpdCheckIn.CREATED_AT + " DESC "
                , "1");

        if (cursor.getCount() == 0) {
            return null;
        }

        RecordDefaulterForm defaulterForm = null;
        if (cursor.moveToNext()) {
            defaulterForm = new RecordDefaulterForm(
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
                    cursor.getString(14),
                    cursor.getString(15),
                    cursor.getString(16),
                    cursor.getString(17));
        }
        cursor.close();
        return defaulterForm;
    }

    @Override
    public boolean delete(RecordDefaulterForm recordDefaulterForm) {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        int rows = sqLiteDatabase.delete(KipConstants.DbConstants.Tables.RECORD_DEFAULTER_FORM
                , KipConstants.DbConstants.Columns.RecordDefaulerForm.BASE_ENTITY_ID + " = ? "
                , new String[]{recordDefaulterForm.getBaseEntityId()});

        return rows > 0;
    }

    @Override
    public List<RecordDefaulterForm> findAll() {
        throw new NotImplementedException("Not Implemented");
    }

    public RecordDefaulterForm findOneByVisit(RecordDefaulterForm recordDefaulterForm) {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor cursor = sqLiteDatabase.query(KipConstants.DbConstants.Tables.RECORD_DEFAULTER_FORM
                , columns
                , KipConstants.DbConstants.Columns.CalculateRiskFactor.BASE_ENTITY_ID + " = ? AND " + KipConstants.DbConstants.Columns.CalculateRiskFactor.VISIT_ID + " = ?"
                , new String[]{recordDefaulterForm.getBaseEntityId(), recordDefaulterForm.getVisitId()}
                , null
                , null
                , OpdDbConstants.Column.OpdCheckIn.CREATED_AT + " DESC "
                , "1");

        if (cursor.getCount() == 0) {
            return null;
        }

        RecordDefaulterForm defaulterForm = null;
        if (cursor.moveToNext()) {
            defaulterForm = new RecordDefaulterForm(
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
                    cursor.getString(14),
                    cursor.getString(15),
                    cursor.getString(16),
                    cursor.getString(17));
        }
        cursor.close();

        return defaulterForm;
    }
}
