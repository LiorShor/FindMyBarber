package com.findmybarber.fragments;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.provider.CalendarContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.findmybarber.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import static android.content.Context.MODE_PRIVATE;
import static android.provider.CalendarContract.*;

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
        textView.setText(sharedPreferences.getString("storeName", null));
        Button createApp = view.findViewById(R.id.makeApp);
        createApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SyncEvent(1,1,"Shlomo",System.currentTimeMillis(),"ya sexy");
                addAttendees();
            }
        });
        return view;
    }
    public static String getDate(long milliSeconds, String dateFormat)
    {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    public void SyncEvent(long id, int meeting_id, String EventName,
                          long Stime, String Description) {

        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone("GMT-1"));
        Date dt = new Date(System.currentTimeMillis());
        Date dt1 =  new Date(System.currentTimeMillis() + 1000 * 60 * 60);
//        try{
//            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//            dt  = dateFormat.parse(String.valueOf(System.currentTimeMillis()));
//            dt1 =  dateFormat.parse(String.valueOf(System.currentTimeMillis())+ 1000 * 60 * 60) ;
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }

        try {
//            dt = new (getDate(Stime,"yyyy-MM-dd HH:mm"));
//            dt1 =  new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(String.valueOf(System.currentTimeMillis()+ 1000 * 60 * 60)) ;

            Calendar beginTime = Calendar.getInstance();
            cal.setTime(dt);

            // beginTime.set(2013, 7, 25, 7, 30);
            beginTime.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
                    cal.get(Calendar.DATE), cal.get(Calendar.HOUR_OF_DAY),
                    cal.get(Calendar.MINUTE));

            Calendar endTime = Calendar.getInstance();
            cal.setTime(dt1);

            // endTime.set(2013, 7, 25, 14, 30);
            // endTime.set(year, month, day, hourOfDay, minute);
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
            Uri uri =cr.insert(Events.CONTENT_URI, values);
            long eventID = Long.parseLong(uri.getLastPathSegment());

        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    public void addAttendees(){
        ContentResolver cr = this.getContext().getContentResolver();
        ContentValues values = new ContentValues();
        values.put(Attendees.ATTENDEE_NAME, "Idan");
        values.put(Attendees.ATTENDEE_EMAIL, "idanpol1@gmail.com");
        values.put(Attendees.ATTENDEE_RELATIONSHIP, Attendees.RELATIONSHIP_ATTENDEE);
        values.put(Attendees.ATTENDEE_TYPE, Attendees.TYPE_OPTIONAL);
//            values.put(Attendees.ATTENDEE_STATUS, Attendees.ATT);
        values.put(Attendees.EVENT_ID, 1);
        Uri uri1 = cr.insert(Attendees.CONTENT_URI, values);

    }
    /*  public static long pushAppointmentsToCalender(Activity curActivity, String title, String addInfo, String place, int status, long startDate, boolean needReminder, boolean needMailService) {
     *//***************** Event: note(without alert) *******************//*

        String eventUriString = "content://com.android.calendar/events";
        ContentValues eventValues = new ContentValues();

        eventValues.put("calendar_id", 1); // id, We need to choose from
        // our mobile for primary
        // its 1
        eventValues.put("title", title);
        eventValues.put("description", addInfo);
        eventValues.put("eventLocation", place);

        long endDate = startDate + 1000 * 60 * 60; // For next 1hr

        eventValues.put("dtstart", startDate);
        eventValues.put("dtend", endDate);

        // values.put("allDay", 1); //If it is bithday alarm or such
        // kind (which should remind me for whole day) 0 for false, 1
        // for true
        eventValues.put("eventStatus", status); // This information is
        // sufficient for most
        // entries tentative (0),
        // confirmed (1) or canceled
        // (2):
        eventValues.put("eventTimezone", "UTC/GMT +2:00");
        *//*Comment below visibility and transparency  column to avoid java.lang.IllegalArgumentException column visibility is invalid error *//*

     *//*eventValues.put("visibility", 3); // visibility to default (0),
                                        // confidential (1), private
                                        // (2), or public (3):
    eventValues.put("transparency", 0); // You can control whether
                                        // an event consumes time
                                        // opaque (0) or transparent
                                        // (1).
      *//*
        eventValues.put("hasAlarm", 1); // 0 for false, 1 for true

        Uri eventUri = curActivity.getApplicationContext().getContentResolver().insert(Uri.parse(eventUriString), eventValues);
        long eventID = Long.parseLong(eventUri.getLastPathSegment());

        if (needReminder) {
            *//***************** Event: Reminder(with alert) Adding reminder to event *******************//*

            String reminderUriString = "content://com.android.calendar/reminders";

            ContentValues reminderValues = new ContentValues();

            reminderValues.put("event_id", eventID);
            reminderValues.put("minutes", 5); // Default value of the
            // system. Minutes is a
            // integer
            reminderValues.put("method", 1); // Alert Methods: Default(0),
            // Alert(1), Email(2),
            // SMS(3)

            Uri reminderUri = curActivity.getApplicationContext().getContentResolver().insert(Uri.parse(reminderUriString), reminderValues);
        }

        *//***************** Event: Meeting(without alert) Adding Attendies to the meeting *******************//*

        if (needMailService) {
            String attendeuesesUriString = "content://com.android.calendar/attendees";

            *//********
     * To add multiple attendees need to insert ContentValues multiple
     * times
     ***********//*
            ContentValues attendeesValues = new ContentValues();

            attendeesValues.put("event_id", eventID);
            attendeesValues.put("attendeeName", "xxxxx"); // Attendees name
            attendeesValues.put("attendeeEmail", "yyyy@gmail.com");// Attendee
            // E
            // mail
            // id
            attendeesValues.put("attendeeRelationship", 0); // Relationship_Attendee(1),
            // Relationship_None(0),
            // Organizer(2),
            // Performer(3),
            // Speaker(4)
            attendeesValues.put("attendeeType", 0); // None(0), Optional(1),
            // Required(2), Resource(3)
            attendeesValues.put("attendeeStatus", 0); // NOne(0), Accepted(1),
            // Decline(2),
            // Invited(3),
            // Tentative(4)

            Uri attendeuesesUri = curActivity.getApplicationContext().getContentResolver().insert(Uri.parse(attendeuesesUriString), attendeesValues);
        }

        return eventID;

    }*/
}