package org.smartregister.kdp.listener;

import android.app.Activity;
import android.content.Intent;
import androidx.annotation.NonNull;
import android.view.View;

import org.smartregister.kdp.activity.KipOpdRegisterActivity;
import org.smartregister.kdp.adapter.NavigationAdapter;
import org.smartregister.kdp.util.KipConstants;
import org.smartregister.kdp.view.NavDrawerActivity;
import org.smartregister.kdp.view.NavigationMenu;

public class NavigationListener implements View.OnClickListener {

    private Activity activity;
    private NavigationAdapter navigationAdapter;

    public NavigationListener(Activity activity, NavigationAdapter adapter) {
        this.activity = activity;
        this.navigationAdapter = adapter;
    }

    @Override
    public void onClick(View v) {
        if (v.getTag() != null && v.getTag() instanceof String) {
            String tag = (String) v.getTag();

            switch (tag) {

                case KipConstants.DrawerMenu.OPD_CLIENTS:
                    navigateToActivity(KipOpdRegisterActivity.class);
                    break;

                default:
                    break;
            }
            navigationAdapter.setSelectedView(tag);
        }
    }

    private void navigateToActivity(@NonNull Class<?> clas) {
        NavigationMenu.closeDrawer();

        if (activity instanceof NavDrawerActivity) {
            ((NavDrawerActivity) activity).finishActivity();
        } else {
            activity.finish();
        }

        activity.startActivity(new Intent(activity, clas));
    }
}
