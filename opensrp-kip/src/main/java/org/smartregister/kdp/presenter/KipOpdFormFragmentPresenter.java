package org.smartregister.kdp.presenter;

import android.view.View;
import android.widget.AdapterView;

import com.vijay.jsonwizard.interactors.JsonFormInteractor;

import org.smartregister.kdp.R;
import org.smartregister.kdp.fragment.KipOpdFormFragment;
import org.smartregister.opd.fragment.BaseOpdFormFragment;
import org.smartregister.opd.presenter.OpdFormFragmentPresenter;
import org.smartregister.util.AppHealthUtils;


public class KipOpdFormFragmentPresenter extends OpdFormFragmentPresenter {

    private KipOpdFormFragment formFragment;


    public KipOpdFormFragmentPresenter(BaseOpdFormFragment formFragment, JsonFormInteractor jsonFormInteractor) {
        super(formFragment, jsonFormInteractor);
        this.formFragment = (KipOpdFormFragment) formFragment;
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        super.onItemSelected(parent, view, position, id);
        String key = (String) parent.getTag(R.id.key);

    }

}
