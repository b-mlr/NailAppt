package com.example.nailappt;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.Map;

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.Viewholder>{
    private ArrayList<Appointment> mAppointmentsData;
    private Context mContext;
    private String mode;

    public interface OnBookListener {
        void onBookRequested(Appointment appointment);
    }
    private final OnBookListener bookListener;
    private final UserRepository userRepo = new UserRepository();
    private final AppointmentRepository appointmentRepo = new AppointmentRepository();
    private static final String LOG_TAG = AdvertiseFragment.class.getName();


    AppointmentAdapter(Context context, ArrayList<Appointment> appointmentsData, String mode, OnBookListener listener){
        this.mAppointmentsData = (appointmentsData != null) ? appointmentsData : new ArrayList<>();
        this.mContext = context;
        this.mode = mode;
        this.bookListener = listener;
    }

    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Viewholder(LayoutInflater.from(mContext).inflate(R.layout.list_appointment, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AppointmentAdapter.Viewholder holder, int position) {
        Appointment currentAppointment = mAppointmentsData.get(position);
        holder.bindTo(currentAppointment);

        if("advertiseFragment".equals(mode)) {
            holder.mDeleteBtn.setOnClickListener(v -> {
                new MaterialAlertDialogBuilder(holder.itemView.getContext())
                        .setTitle("Időpont törlése")
                        .setMessage("Biztosan törölni szeretné ezt az időpontot?")
                        .setNegativeButton("Mégse", ((dialog, which) -> {
                            dialog.dismiss();
                        }))
                        .setPositiveButton("Törlés", ((dialog, which) -> {
                            String appointmentId = currentAppointment.getAppointmentID();

                            appointmentRepo.deleteAppointment(appointmentId).addOnSuccessListener(task -> {
                                Toast successToast = Toast.makeText(holder.itemView.getContext(), "Sikeres törlés!", Toast.LENGTH_SHORT);
                                successToast.show();

                                if (position != RecyclerView.NO_POSITION) {
                                    mAppointmentsData.remove(position);
                                    notifyItemRemoved(position);
                                }

                            }).addOnFailureListener(e -> {
                                Log.e(LOG_TAG, "Törlés hiba: " + e.getMessage());
                                Toast.makeText(holder.itemView.getContext(), "Hiba történt a törléskor", Toast.LENGTH_SHORT).show();
                            });
                        }))
                        .show();
            });

            holder.mListBtn.setOnClickListener(v -> {
                Intent intent = new Intent(mContext, ManageAdvertActivity.class);
                intent.putExtra("appointmentID", currentAppointment.getAppointmentID());
                mContext.startActivity(intent);
            });
        }  else if ("profileActivity".equals(mode)){
            holder.mDeleteBtn.setOnClickListener(v -> {
                new MaterialAlertDialogBuilder(holder.itemView.getContext())
                        .setTitle("Időpont lemondása")
                        .setMessage("Biztosan le szeretné mondani ezt az időpontot?")
                        .setNegativeButton("Mégse", ((dialog, which) -> {
                            dialog.dismiss();
                        }))
                        .setPositiveButton("Lemondás", ((dialog, which) -> {
                            String appointmentId = currentAppointment.getAppointmentID();

                            appointmentRepo.cancelAppointment(appointmentId).addOnSuccessListener(task -> {
                                Toast successToast = Toast.makeText(holder.itemView.getContext(), "Sikeres lemondás!", Toast.LENGTH_SHORT);
                                successToast.show();

                                if (position != RecyclerView.NO_POSITION) {
                                    mAppointmentsData.remove(position);
                                    notifyItemRemoved(position);
                                }

                            }).addOnFailureListener(e -> {
                                Log.e(LOG_TAG, "Törlés hiba: " + e.getMessage());
                                Toast.makeText(holder.itemView.getContext(), "Hiba történt a lemondáskor", Toast.LENGTH_SHORT).show();
                            });
                        }))
                        .show();
            });
        } else if ("bookingFragment".equals(mode)){
            holder.mListBtn.setOnClickListener( v -> {
                if ( bookListener != null){
                    bookListener.onBookRequested(currentAppointment);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return (mAppointmentsData != null) ? mAppointmentsData.size() : 0;
    }

    public String formatLocation(Map<String,String> location){
        return location.get("postcode") + ", " + location.get("city") + " " + location.get("address");
    }

    class Viewholder extends RecyclerView.ViewHolder{
        private TextView mDateTime;
        private TextView mAdvertiser;
        private TextView mPhoneNum;
        private TextView mOtherContact;
        private TextView mBookedBy;
        private TextView mLocation;
        private MaterialButton mListBtn;
        private TextView mPhoneNumTitle;
        private TextView mOtherContactTitle;
        private TextView mBookedByTitle;
        private MaterialButton mDeleteBtn;

        public Viewholder(@NonNull View itemView) {
            super(itemView);

            mDateTime = itemView.findViewById(R.id.datetime);
            mAdvertiser = itemView.findViewById(R.id.advertiser);
            mPhoneNum = itemView.findViewById(R.id.phoneNumber);
            mPhoneNumTitle = itemView.findViewById(R.id.phoneNumberTitle);
            mOtherContact = itemView.findViewById(R.id.otherContact);
            mOtherContactTitle = itemView.findViewById(R.id.otherContactTitle);
            mBookedBy = itemView.findViewById(R.id.bookedby);
            mBookedByTitle = itemView.findViewById(R.id.bookedTitle);
            mLocation = itemView.findViewById(R.id.location);
            mListBtn = itemView.findViewById(R.id.listBtn);
            mDeleteBtn = itemView.findViewById(R.id.deleteBtn);
            if("advertiseFragment".equals(mode)) {
                mListBtn.setIcon(ContextCompat.getDrawable(itemView.getContext(), R.drawable.ic_editapp));
                mListBtn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#864B6F")));
            } else if("bookingFragment".equals(mode)){
                mDeleteBtn.setVisibility(View.GONE);
            } else if("profileActivity".equals(mode)){
                mListBtn.setVisibility(View.GONE);
            }


        }

        public void bindTo(Appointment currentItem){
            String datetime = currentItem.getDate() + " " + currentItem.getTime() + " - ";
            mDateTime.setText(datetime);

            userRepo.getUserbyID(currentItem.getAdvertiserID()).addOnCompleteListener( task -> {
               if(task.isSuccessful()){
                   Map<String, Object> userData = task.getResult();

                   String firstName = userData.get("firstName").toString();
                   String lastName = userData.get("lastName").toString();
                   String fullName = (!lastName.isEmpty() ? lastName : "") + " " + (!firstName.isEmpty() ? firstName : "" );
                   mAdvertiser.setText(fullName);
               }
            });

            if(!currentItem.getPhoneNum().isEmpty()) {
                mPhoneNum.setText(currentItem.getPhoneNum());
            } else {
                mPhoneNumTitle.setVisibility(View.GONE);
                mPhoneNum.setVisibility(View.GONE);
            }
            if(!currentItem.getOtherContact().isEmpty()) {
                mOtherContact.setText(currentItem.getOtherContact());
            } else {
                mOtherContactTitle.setVisibility(View.GONE);
                mOtherContact.setVisibility(View.GONE);
            }
            if(currentItem.getBookedByID().equals("null")){
                mBookedByTitle.setVisibility(View.GONE);
                mBookedBy.setVisibility(View.GONE);
            } else {
                userRepo.getUserbyID(currentItem.getBookedByID()).addOnCompleteListener( task -> {
                    if(task.isSuccessful()){
                        Map<String, Object> userData = task.getResult();

                        String firstName = userData.get("firstName").toString();
                        String lastName = userData.get("lastName").toString();
                        String fullName = (!lastName.isEmpty() ? lastName : "") + " " + (!firstName.isEmpty() ? firstName : "" );
                        mBookedBy.setText(fullName);
                    }
                });
            }
            mLocation.setText(formatLocation(currentItem.getLocation()));

            Log.i(LOG_TAG, "bindTo:" + mDateTime.toString() + mAdvertiser +mLocation+mPhoneNum + mOtherContact);

        }
    }

}



