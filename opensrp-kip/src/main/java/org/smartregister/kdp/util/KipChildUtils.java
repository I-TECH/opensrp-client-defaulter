package org.smartregister.kdp.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import androidx.annotation.NonNull;

import com.google.common.reflect.TypeToken;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONException;
import org.smartregister.child.util.Utils;
import org.smartregister.domain.form.FormLocation;
import org.smartregister.kdp.BuildConfig;
import org.smartregister.kdp.application.KipApplication;
import org.smartregister.kdp.listener.OnLocationChangeListener;
import org.smartregister.kdp.view.NavigationMenu;
import org.smartregister.kdp.widget.KipTreeViewDialog;
import org.smartregister.location.helper.LocationHelper;
import org.smartregister.util.AssetHandler;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import java.util.concurrent.TimeUnit;

import timber.log.Timber;

public class KipChildUtils extends Utils {
    private static Context context = KipApplication.getInstance().getApplicationContext();

    public static final ArrayList<String> ALLOWED_LEVELS;
    public static final String FACILITY = "Facility";
    public static final String DEFAULT_LOCATION_LEVEL = "Health Facility";
    public static final SimpleDateFormat DB_DF = new SimpleDateFormat("yyyy-MM-dd");
    public static final String LANGUAGE = "language";
    private static final String PREFERENCES_FILE = "lang_prefs";

    static {
        ALLOWED_LEVELS = new ArrayList<>();
        ALLOWED_LEVELS.add(DEFAULT_LOCATION_LEVEL);
        ALLOWED_LEVELS.add(FACILITY);
    }

    public static String getLanguage(Context ctx) {
        SharedPreferences sharedPref = ctx.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
        return sharedPref.getString(LANGUAGE, "en");
    }

    public static Context setAppLocale(Context context, String language) {
        Context newContext = context;
        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Resources res = newContext.getResources();
        Configuration config = new Configuration(res.getConfiguration());
        config.setLocale(locale);
        newContext = newContext.createConfigurationContext(config);
        return newContext;
    }

    @NonNull
    public static ArrayList<String> getLocationLevels() {
        return new ArrayList<>(Arrays.asList(BuildConfig.LOCATION_LEVELS));
    }

    @NonNull
    public static ArrayList<String> getHealthFacilityLevels() {
        return new ArrayList<>(Arrays.asList(BuildConfig.HEALTH_FACILITY_LEVELS));
    }

    @NonNull
    public static String getCurrentLocality() {
        String selectedLocation = KipApplication.getInstance().context().allSharedPreferences().fetchCurrentLocality();
        if (StringUtils.isBlank(selectedLocation)) {
            selectedLocation = LocationHelper.getInstance().getDefaultLocation();
            KipApplication.getInstance().context().allSharedPreferences().saveCurrentLocality(selectedLocation);
        }
        return selectedLocation;
    }

    public static void showLocations(@Nullable Activity context,
                                     @NonNull OnLocationChangeListener onLocationChangeListener,
                                     @Nullable NavigationMenu navigationMenu) {
        try {
            ArrayList<String> allLevels = getLocationLevels();
            ArrayList<String> healthFacilities = getHealthFacilityLevels();
            ArrayList<String> defaultLocation = (ArrayList<String>) LocationHelper.getInstance().generateDefaultLocationHierarchy(allLevels);
            List<FormLocation> upToFacilities = LocationHelper.getInstance().generateLocationHierarchyTree(false, healthFacilities);
            String upToFacilitiesString = AssetHandler.javaToJsonString(upToFacilities, new TypeToken<List<FormLocation>>() {
            }.getType());
            KipTreeViewDialog treeViewDialog = new KipTreeViewDialog(context,
                    new JSONArray(upToFacilitiesString), defaultLocation, new ArrayList<>());

            treeViewDialog.setCancelable(true);
            treeViewDialog.setCanceledOnTouchOutside(true);
            treeViewDialog.setOnDismissListener(dialog -> {
                ArrayList<String> treeViewDialogName = treeViewDialog.getName();
                if (!treeViewDialogName.isEmpty()) {
                    String newLocation = treeViewDialogName.get(treeViewDialogName.size() - 1);
                    KipApplication.getInstance().context().allSharedPreferences().saveCurrentLocality(newLocation);
                    onLocationChangeListener.updateUi(newLocation);
                    if (navigationMenu != null) {
                        navigationMenu.updateUi(newLocation);
                    }
                }

            });
            treeViewDialog.show();
        } catch (JSONException e) {
            Timber.e(e);
        }
    }

    public static void updateSyncStatus(boolean isComplete) {
        KipApplication.getInstance().context().allSharedPreferences().savePreference("syncComplete", String.valueOf(isComplete));
    }
    
}
