package com.example.medicalreminder.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medicalreminder.ClinicFragment;
import com.example.medicalreminder.R;
import com.example.medicalreminder.model.Clinic;

import java.util.List;

public class ClinicItemListAdapter extends RecyclerView.Adapter<ClinicItemListAdapter.ListViewHolder> {

    private List<Clinic> clinicList;
    private LayoutInflater layoutInflater;
    private ClinicFragment clinicFragment;

    public ClinicItemListAdapter(Context context, List<Clinic> clinicList, ClinicFragment clinicFragment) {
        layoutInflater = LayoutInflater.from(context);
        this.clinicList = clinicList;
        this.clinicFragment = clinicFragment;
    }

    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.clinic_item, parent, false);
        return new ListViewHolder(view, clinicFragment);
    }

    @Override
    public void onBindViewHolder(@NonNull ListViewHolder holder, int position) {
        final int itemPosition = position;
        final Clinic clinicItem = clinicList.get(position);

        holder.clinicNameTextView.setText(clinicItem.getName());
        holder.clinicAddressTextView.setText(clinicItem.getAddress());
        holder.clinicZipcodeTextView.setText(clinicItem.getZipCode());
        holder.openPhoneNumberButton.setText(clinicItem.getPhoneNumber());
        if(clinicItem.getPhoneNumber().equals("519-208-1348")){
            holder.clinicImageView.setImageResource(R.drawable.pharmasave);
        }
        else{
            holder.clinicImageView.setImageResource(R.drawable.sobeys);
        }

        holder.openClinicDirectionButton.setOnClickListener(view -> {
            holder.onClinicItemListener.onOpenDirectionClick(clinicList.get(itemPosition));
        });

        holder.openPhoneNumberButton.setOnClickListener(view -> {
            holder.onClinicItemListener.onOpenPhoneClick(clinicList.get(itemPosition));
        });
    }

    @Override
    public int getItemCount() {
        return clinicList.size();
    }

    public class ListViewHolder extends RecyclerView.ViewHolder{
        TextView clinicNameTextView;
        TextView clinicAddressTextView;
        TextView clinicZipcodeTextView;
        ImageView clinicImageView;
        Button openClinicDirectionButton;
        Button openPhoneNumberButton;
        private OnClinicItemListener onClinicItemListener;

        ListViewHolder(View itemView, OnClinicItemListener onClinicItemListener) {
            super(itemView);
            this.onClinicItemListener = onClinicItemListener;
            clinicNameTextView = itemView.findViewById(R.id.clinicNameTextView);
            clinicAddressTextView = itemView.findViewById(R.id.clinicAddressTextView);
            clinicZipcodeTextView = itemView.findViewById(R.id.clinicZipcodeTextView);
            clinicImageView = itemView.findViewById(R.id.clinicImageView);
            openClinicDirectionButton = itemView.findViewById(R.id.clinicDirectionButton);
            openPhoneNumberButton = itemView.findViewById(R.id.clinicPhoneButton);
        }
    }

    public interface OnClinicItemListener {
        void onOpenDirectionClick(Clinic clinic);
        void onOpenPhoneClick(Clinic clinic);
    }
}
