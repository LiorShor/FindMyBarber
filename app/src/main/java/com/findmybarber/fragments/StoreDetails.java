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
            Uri uri =cr.insert(Events.CONTENT_URI, values);

        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    public void addAttendees(){
        ContentResolver cr = this.getContext().getContentResolver();
        ContentValues values = new ContentValues();
        values.put(Attendees.ATTENDEE_NAME, "Idan");
        values.put(Attendees.ATTENDEE_EMAIL, "liorshor997@gmail.com");
        values.put(Attendees.ATTENDEE_RELATIONSHIP, Attendees.RELATIONSHIP_ATTENDEE);
        values.put(Attendees.ATTENDEE_TYPE, Attendees.TYPE_OPTIONAL);
//            values.put(Attendees.ATTENDEE_STATUS, Attendees.ATT);
        values.put(Attendees.EVENT_ID, 1);
        Uri uri1 = cr.insert(Attendees.CONTENT_URI, values);

    }
}