package org.smartregister.kdp.repository;

import android.content.Context;
import androidx.annotation.NonNull;

import net.sqlcipher.database.SQLiteDatabase;

import org.smartregister.AllConstants;
import org.smartregister.configurableviews.repository.ConfigurableViewsRepository;
import org.smartregister.kdp.BuildConfig;
import org.smartregister.kdp.application.KipApplication;
import org.smartregister.kdp.util.KipConstants;
import org.smartregister.opd.repository.OpdDetailsRepository;
import org.smartregister.opd.repository.OpdDiagnosisAndTreatmentFormRepository;
import org.smartregister.opd.repository.OpdDiagnosisDetailRepository;
import org.smartregister.opd.repository.OpdTestConductedRepository;
import org.smartregister.opd.repository.OpdTreatmentDetailRepository;
import org.smartregister.opd.repository.OpdVisitRepository;
import org.smartregister.repository.EventClientRepository;
import org.smartregister.repository.Repository;
import org.smartregister.repository.SettingsRepository;
import org.smartregister.repository.UniqueIdRepository;

import timber.log.Timber;


public class KipRepository extends Repository {

    protected SQLiteDatabase readableDatabase;
    protected SQLiteDatabase writableDatabase;

    private Context context;
    private String appVersionCodePref = KipConstants.Pref.APP_VERSION_CODE;

    public KipRepository(@NonNull Context context, @NonNull org.smartregister.Context openSRPContext) {
        super(context, AllConstants.DATABASE_NAME, BuildConfig.DATABASE_VERSION, openSRPContext.session(),
                KipApplication
                        .createCommonFtsObject(context), openSRPContext.sharedRepositoriesArray());
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        super.onCreate(database);
        EventClientRepository
                .createTable(database, EventClientRepository.Table.client, EventClientRepository.client_column.values());
        EventClientRepository
                .createTable(database, EventClientRepository.Table.event, EventClientRepository.event_column.values());
        ConfigurableViewsRepository.createTable(database);
        UniqueIdRepository.createTable(database);

        SettingsRepository.onUpgrade(database);

        OpdVisitRepository.createTable(database);
        OpdDetailsRepository.createTable(database);
        OpdDiagnosisAndTreatmentFormRepository.createTable(database);
        OpdDiagnosisDetailRepository.createTable(database);
        OpdTreatmentDetailRepository.createTable(database);
        OpdTestConductedRepository.createTable(database);


        ClientRegisterTypeRepository.createTable(database);
        runLegacyUpgrades(database);

        onUpgrade(database, 11, BuildConfig.DATABASE_VERSION);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Timber.w("Upgrading database from version %d to %d, which will destroy all old data", oldVersion, newVersion);

        int upgradeTo = oldVersion + 1;
        while (upgradeTo <= newVersion) {
            switch (upgradeTo) {
                case 2:
                    upgradeToVersion9(db);
                    break;

                default:
                    break;
            }
            upgradeTo++;
        }
    }

    @Override
    public SQLiteDatabase getReadableDatabase() {
        byte[] pass = KipApplication.getInstance().getPassword();
        if (pass != null && pass.length > 0) {
            return getReadableDatabase(pass);
        } else {
            throw new IllegalStateException("Password is blank");
        }
    }

    @Override
    public SQLiteDatabase getWritableDatabase() {
        byte[] pass = KipApplication.getInstance().getPassword();
        if (pass != null && pass.length > 0) {
            return getWritableDatabase(pass);
        } else {
            throw new IllegalStateException("Password is blank");
        }
    }


    @Override
    public synchronized SQLiteDatabase getWritableDatabase(String password) {
        if (writableDatabase == null || !writableDatabase.isOpen()) {
            if (writableDatabase != null) {
                writableDatabase.close();
            }
            writableDatabase = super.getWritableDatabase(password);
        }
        return writableDatabase;
    }

    @Override
    public synchronized SQLiteDatabase getReadableDatabase(String password) {
        try {
            if (readableDatabase == null || !readableDatabase.isOpen()) {
                if (readableDatabase != null) {
                    readableDatabase.close();
                }
                readableDatabase = super.getReadableDatabase(password);
            }
            return readableDatabase;
        } catch (Exception e) {
            Timber.e(e);
            return null;
        }

    }

    @Override
    public synchronized void close() {
        if (readableDatabase != null) {
            readableDatabase.close();
        }

        if (writableDatabase != null) {
            writableDatabase.close();
        }
        super.close();
    }

    private void runLegacyUpgrades(@NonNull SQLiteDatabase database) {
        upgradeToVersion9(database);
    }


    private void upgradeToVersion9(@NonNull SQLiteDatabase db) {
        try {
            OpdSMSReminderFormRepository.updateIndex(db);
            RecordDefaulterFormRepository.updateIndex(db);
            UpdateDefaulterFormRepository.updateIndex(db);
        } catch (Exception e) {
            Timber.e(e, " --> upgradeToVersion10 ");
        }
    }
}
