package org.smartregister.kdp.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.smartregister.kdp.R;
import org.smartregister.kdp.listener.NavigationListener;
import org.smartregister.kdp.model.NavigationOption;
import org.smartregister.kdp.util.KipConstants;

import java.util.List;
import java.util.Locale;

public class NavigationAdapter extends RecyclerView.Adapter<NavigationAdapter.MyViewHolder> {

    private List<NavigationOption> navigationOptionList;
    private String selectedView = KipConstants.DrawerMenu.OPD_CLIENTS;
    private View.OnClickListener onClickListener;
    private Context context;

    public NavigationAdapter(List<NavigationOption> navigationOptions, Activity context) {
        this.navigationOptionList = navigationOptions;
        this.context = context;
        this.onClickListener = new NavigationListener(context, this);
    }

    public void setSelectedView(String selectedView) {
        this.selectedView = selectedView;
        this.notifyDataSetChanged();
    }

    public String getSelectedView() {
        return selectedView;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.navigation_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        NavigationOption model = navigationOptionList.get(position);
        holder.tvName.setText(context.getResources().getText(model.getTitleID()));
        holder.tvCount.setText(String.format(Locale.getDefault(), "%d", model.getRegisterCount()));
        holder.ivIcon.setImageResource(model.getResourceID());

        holder.getView().setTag(model.getMenuTitle());

        if (selectedView != null && selectedView.equals(model.getMenuTitle())) {
            holder.tvCount.setTextColor(context.getResources().getColor(R.color.holo_blue));
            holder.tvName.setTextColor(context.getResources().getColor(R.color.holo_blue));
            holder.ivIcon.setImageResource(model.getResourceActiveID());
        } else {
            holder.tvCount.setTextColor(Color.WHITE);
            holder.tvName.setTextColor(Color.WHITE);
            holder.ivIcon.setImageResource(model.getResourceID());
        }
    }

    @Override
    public int getItemCount() {
        return navigationOptionList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName, tvCount;
        private ImageView ivIcon;
        private View myView;

        private MyViewHolder(View view) {
            super(view);
            tvName = view.findViewById(R.id.tvName);
            tvCount = view.findViewById(R.id.tvCount);
            ivIcon = view.findViewById(R.id.ivIcon);

            if (onClickListener != null) {
                view.setOnClickListener(onClickListener);
            }

            myView = view;
        }

        public View getView() {
            return myView;
        }
    }

}





