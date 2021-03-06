package org.smartregister.kdp.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.smartregister.kdp.R;
import org.smartregister.kdp.presenter.MePresenter;
import org.smartregister.view.activity.DrishtiApplication;
import org.smartregister.view.contract.MeContract;

public class MeFragment extends org.smartregister.view.fragment.MeFragment implements MeContract.View {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_me, container, false);
    }

    protected void initializePresenter() {
        presenter = new MePresenter(this);
    }


    @Override
    protected void onViewClicked(View view) {
        int viewId = view.getId();
        if (viewId == R.id.logout_section) {
            DrishtiApplication.getInstance().logoutCurrentUser();
        }
    }
}
