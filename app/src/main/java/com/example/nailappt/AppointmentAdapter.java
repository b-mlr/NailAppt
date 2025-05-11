package com.example.nailappt;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.content.res.ColorStateList;
import android.graphics.Color;


import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.Map;

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.Viewholder>{
    private ArrayList<Appointment> mAppointmentsData;
    private ArrayList<Appointment> getmAppointmentsDataAll;
    private Context mContext;
    private final UserRepository userRepo = new UserRepository();
    private static final String LOG_TAG = AdvertiseFragment.class.getName();

    private int lastPosition = -1;

    AppointmentAdapter(Context context, ArrayList<Appointment> appointmentsData){
        this.mAppointmentsData = (appointmentsData != null) ? appointmentsData : new ArrayList<>();
        this.getmAppointmentsDataAll = appointmentsData;
        this.mContext = context;
    }

    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Viewholder(LayoutInflater.from(mContext).inflate(R.layout.list_appointment, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AppointmentAdapter.Viewholder holder, int position) {
        Appointment currentAppointment = mAppointmentsData.get(position);

        holder.bindTo(currentAppointment);
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
        private TextView mLocation;
        private MaterialButton mListBtn;
        private TextView mPhoneNumTitle;
        private TextView mOtherContactTitle;

        public Viewholder(@NonNull View itemView) {
            super(itemView);

            mDateTime = itemView.findViewById(R.id.datetime);
            mAdvertiser = itemView.findViewById(R.id.advertiser);
            mPhoneNum = itemView.findViewById(R.id.phoneNumber);
            mPhoneNumTitle = itemView.findViewById(R.id.phoneNumberTitle);
            mOtherContact = itemView.findViewById(R.id.otherContact);
            mOtherContactTitle = itemView.findViewById(R.id.otherContactTitle);
            mLocation = itemView.findViewById(R.id.location);
            mListBtn = itemView.findViewById(R.id.listBtn);
            mListBtn.setIcon(ContextCompat.getDrawable(itemView.getContext(), R.drawable.ic_editapp));
            mListBtn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#864B6F")));


            itemView.findViewById(R.id.listBtn).setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Log.d("Activity", "List button pressed");
                }
            });

            Log.i(LOG_TAG, "Viewholder:" + mDateTime.toString() + mAdvertiser +mLocation+mPhoneNum + mOtherContact);

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
            mLocation.setText(formatLocation(currentItem.getLocation()));

            Log.i(LOG_TAG, "bindTo:" + mDateTime.toString() + mAdvertiser +mLocation+mPhoneNum + mOtherContact);

        }
    }
}


