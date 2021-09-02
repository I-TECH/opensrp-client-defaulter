package org.smartregister.kdp.model;

import org.smartregister.kdp.R;
import org.smartregister.kdp.contract.NavigationContract;
import org.smartregister.kdp.util.KipConstants;
import org.smartregister.util.Utils;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class NavigationModel implements NavigationContract.Model {
    private static NavigationModel instance;
    private static List<NavigationOption> navigationOptions = new ArrayList<>();

    public static NavigationModel getInstance() {
        if (instance == null)
            instance = new NavigationModel();

        return instance;
    }

    @Override
    public List<NavigationOption> getNavigationItems() {
        if (navigationOptions.size() == 0) {

            NavigationOption opdNavigationOption = new NavigationOption(R.mipmap.sidemenu_children,
                    R.mipmap.sidemenu_children_active, R.string.menu_opd_clients, KipConstants.DrawerMenu.OPD_CLIENTS,
                    0, true);
            if (opdNavigationOption.isEnabled()) {
                navigationOptions.add(opdNavigationOption);
            }
        }

        return navigationOptions;
    }

    @Override
    public String getCurrentUser() {
        String prefferedName = "";
        try {
            prefferedName = Utils.getPrefferedName().split(" ")[0];
        } catch (Exception e) {
            Timber.e(e, "NavigationModel --> getCurrentUser");
        }

        return prefferedName;
    }
}

