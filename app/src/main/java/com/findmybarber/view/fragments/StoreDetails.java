package com.findmybarber.view.fragments;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.GridView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;

import com.findmybarber.R;
import com.findmybarber.model.Admin;
import com.findmybarber.model.Book;
import com.findmybarber.model.ButtonAdapter;
import com.findmybarber.model.Store;
import com.findmybarber.view.activities.Login;
import com.findmybarber.view.activities.MainActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;

import static android.content.Context.MODE_PRIVATE;
import static android.provider.CalendarContract.*;
import static com.findmybarber.view.activities.MainActivity.bookingsList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StoreDetails#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StoreDetails extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private TimePicker timePicker;
    private CalendarView calendarView;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public StoreDetails() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StoreDetails.
     */
    // TODO: Rename and change types and number of parameters
    public static StoreDetails newInstance(String param1, String param2) {
        StoreDetails fragment = new StoreDetails();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_store_details, container, false);
        TextView textView = view.findViewById(R.id.storeName);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("store", MODE_PRIVATE);
        SharedPreferences sharedUserPreferences = getActivity().getSharedPreferences("CurrentUserPref",MODE_PRIVATE);
        textView.setText(sharedPreferences.getString("storeName", null));
        Button createAppointment = view.findViewById(R.id.makeAppointment);

        GridView gridview = view.findViewById(R.id.gridview);
        gridview.setAdapter(new ButtonAdapter(getContext()));
        gridview.setNumColumns(4);

//        timePicker = view.findViewById(R.id.datePicker1);

        calendarView = view.findViewById(R.id.calendarView);
        setTimePickerInterval(timePicker);
        Calendar calendar = Calendar.getInstance();
        calendarView.setOnDateChangeListener((calendarView, year, month, dayOfMonth) ->
                        calendar.set(year,month,dayOfMonth)
        );

//        Button button = gridview.findViewById(R.id.button);
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                SharedPreferences sharedPreferences;
//                sharedPreferences =  getContext().getSharedPreferences("button", MODE_PRIVATE);
//                SharedPreferences.Editor editor = sharedPreferences.edit();
//                editor.putString("timeSlot", button.getText().toString());
//                editor.apply();
//            }
//        });


        createAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String storeID = sharedPreferences.getString("storeID", null);
                String storeName = sharedPreferences.getString("storeName", null);
                String clientEmail = sharedUserPreferences.getString("KeyUser", null);
//                String time = sharedPreferences.getString("timeSlot", null);
                Admin admin = null;
                for (Admin admin1:Login.adminsList) {
                    if(admin1.getStoreID() != null && admin1.getStoreID().equals(storeID))
                        admin = admin1;
                }
                String storeEmail = admin.getUserEmail();
                SyncEvent(1,1,"Appointment at "+ storeName,System.currentTimeMillis(),
                        "You have a new apppointment at "+ storeName + "\nDon't be late !!!");
                addAttendees(admin.getUserEmail(),admin.getUserName() +" "+ admin.getUserSurname());

                calendar.set(Calendar.HOUR_OF_DAY, timePicker.getHour());
                calendar.set(Calendar.MINUTE, timePicker.getMinute() * 15);
                calendar.set(Calendar.SECOND, 0);

                UUID id = UUID.randomUUID();
                Book book = new Book(id.toString(), storeID , clientEmail, storeEmail,calendar);
                MainActivity mainActivity = (MainActivity) getActivity();
                bookingsList.add(book);
                assert mainActivity != null;
                mainActivity.postBookAppointment(book);
            }
        });
        return view;
    }

    public void SyncEvent(long id, int meeting_id, String EventName,
                          long Stime, String Description) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone("GMT-1"));
        Date dt = new Date(System.currentTimeMillis());
        Date dt1 =  new Date(System.currentTimeMillis() + 1000 * 60 * 15);
        try {
            Calendar beginTime = Calendar.getInstance();
            cal.setTime(dt);
            beginTime.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
                    cal.get(Calendar.DATE), cal.get(Calendar.HOUR_OF_DAY),
                    cal.get(Calendar.MINUTE));
            Calendar endTime = Calendar.getInstance();
            cal.setTime(dt1);
            endTime.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
                    cal.get(Calendar.DATE), cal.get(Calendar.HOUR_OF_DAY),
                    cal.get(Calendar.MINUTE));
            ContentResolver cr = this.getContext().getContentResolver();
            ContentValues values = new ContentValues();
            values.put(Events.DTSTART, beginTime.getTimeInMillis());
            values.put(Events.DTEND, endTime.getTimeInMillis());
            values.put(Events.TITLE, EventName);
            values.put(Events.DESCRIPTION, Description);
            values.put(Events.CALENDAR_ID, id);
            values.put(Events.EVENT_TIMEZONE, TimeZone.getDefault().getID());
            values.put(Events._ID, meeting_id);
            cr.insert(Events.CONTENT_URI, values);

        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    public void addAttendees(String barberEmail,String barberName){
        ContentResolver cr = this.getContext().getContentResolver();
        ContentValues values = new ContentValues();
        values.put(Attendees.ATTENDEE_NAME, barberName);
        values.put(Attendees.ATTENDEE_EMAIL, barberEmail);
        values.put(Attendees.ATTENDEE_RELATIONSHIP, Attendees.RELATIONSHIP_ATTENDEE);
        values.put(Attendees.ATTENDEE_TYPE, Attendees.TYPE_OPTIONAL);
        values.put(Attendees.EVENT_ID, 1);
        cr.insert(Attendees.CONTENT_URI, values);
    }

    private void setTimePickerInterval(TimePicker timePicker) {
        try {
            int TIME_PICKER_INTERVAL = 15;
            NumberPicker minutePicker = (NumberPicker) timePicker.findViewById(Resources.getSystem().getIdentifier(
                    "minute", "id", "android"));
            minutePicker.setMinValue(0);
            minutePicker.setMaxValue((60 / TIME_PICKER_INTERVAL) - 1);
            List<String> displayedValues = new ArrayList<String>();
            for (int i = 0; i < 60; i += TIME_PICKER_INTERVAL) {
                displayedValues.add(String.format("%02d", i));
            }
            minutePicker.setDisplayedValues(displayedValues.toArray(new String[0]));
        } catch (Exception e) {
            Log.e("", "Exception: " + e);
        }
    }
}