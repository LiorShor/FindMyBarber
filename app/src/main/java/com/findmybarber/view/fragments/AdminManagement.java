package com.findmybarber.view.fragments;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;

import com.findmybarber.R;
import com.findmybarber.model.Admin;
import com.findmybarber.model.Book;
import com.findmybarber.model.BookingAdapter;
import com.findmybarber.model.ButtonAdapter;
import com.findmybarber.model.Customer;
import com.findmybarber.model.GetBookingList;
import com.findmybarber.model.Registration;
import com.findmybarber.model.WrapContentLinearLayoutManager;
import com.findmybarber.view.activities.Login;
import com.findmybarber.view.activities.MainActivity;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AdminManagement#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AdminManagement extends Fragment implements BookingAdapter.ItemCallback{
    private final SimpleDateFormat dateFormatForMonth = new SimpleDateFormat("MMM - yyyy", Locale.getDefault());
    private final SimpleDateFormat dateFormatForDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private CompactCalendarView compactCalendarView;
    private SharedPreferences sharedBookPreferences;
    public static List<Book>bookingList = new ArrayList<>();
    private final List<String> takenTimeSlots = new ArrayList<>();
    public static List<Book>bookingListForDay = new ArrayList<>();
    private final List<String> bookingStringsListForDay = new ArrayList<>();
    private List<Event> events = new ArrayList<>();
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private Dialog createAppointment;
    private String date;
    private Admin admin;



    public AdminManagement() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AdminManagement.
     */
    // TODO: Rename and change types and number of parameters
    public static AdminManagement newInstance(String param1, String param2) {
        AdminManagement fragment = new AdminManagement();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences pref = getActivity().getSharedPreferences("CurrentUserPref",MODE_PRIVATE);
        admin = findAdminByStoreID(pref.getString("StoreID", null));
        GetBookingList getBookingList = new GetBookingList(admin.getStoreID());
        createAppointment = new Dialog(getContext(),R.style.PauseDialog);
        createAppointment.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        try {
            bookingList.addAll(getBookingList.execute().get());
            date = dateFormatForDate.format(System.currentTimeMillis());
        }
        catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_management, container, false);
        compactCalendarView = view.findViewById(R.id.compactcalendar_view);
        compactCalendarView.setUseThreeLetterAbbreviation(true);
        compactCalendarView.setFirstDayOfWeek(Calendar.SUNDAY);
        compactCalendarView.shouldDrawIndicatorsBelowSelectedDays(true);
        compactCalendarView.setIsRtl(false);
        compactCalendarView.displayOtherMonthDays(false);
        events = ConvertBookingIntoEvent(bookingList);
        compactCalendarView.addEvents(events);
        Calendar calendar = Calendar.getInstance();
        BookingAdapter adapter = new BookingAdapter(bookingStringsListForDay,bookingListForDay,getContext(), this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        RecyclerView recyclerView = view.findViewById(R.id.bookingList);
        recyclerView.setAdapter(adapter);
//        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setLayoutManager(new WrapContentLinearLayoutManager(getActivity()));
        TextView currMonth = view.findViewById(R.id.CurrMonth);
        Button newMeetingBt = view.findViewById(R.id.newMeeting);
        currMonth.setText(dateFormatForMonth.format(compactCalendarView.getFirstDayOfCurrentMonth()));
        compactCalendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                calendar.setTime(java.util.Calendar.getInstance().getTime());
                calendar.set(Calendar.HOUR_OF_DAY,0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);

                if(calendar.getTime().toString().equals(dateClicked.toString()) || !dateClicked.before(calendar.getTime())) {
                    date = dateFormatForDate.format(dateClicked);
                    newMeetingBt.setVisibility(View.VISIBLE);
                }
                else
                    newMeetingBt.setVisibility(View.INVISIBLE);
                String string;
                bookingStringsListForDay.clear();
                bookingListForDay.clear();
                for (Book book : bookingList) {
                    if(book.getDate().equals(dateFormatForDate.format(dateClicked))) {
                        Customer customer = Login.customersList.stream().filter(user -> user.getUserEmail().toLowerCase().equals(book.getEmailClient().toLowerCase())).findFirst().orElse(null);
                        string = "Meeting with " + customer.getUserName() + " "+ customer.getUserSurname() + " at " + book.getTime().toString();
                        bookingStringsListForDay.add(string);
                        bookingListForDay.add(book);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                currMonth.setText(dateFormatForMonth.format(compactCalendarView.getFirstDayOfCurrentMonth()));
            }
        });
        newMeetingBt.setOnClickListener(view1 -> {
            createAppointment.setContentView(R.layout.invite_client_dialog);
            createAppointment.show();
            updateList();
            clearHistory();
            takenTimeSlots.clear();
            createAppointment.setCanceledOnTouchOutside(true);
            GridView gridview = createAppointment.findViewById(R.id.gridview);
            for (Book book : bookingListForDay) { takenTimeSlots.add(book.getTime().toString()); }
            gridview.setAdapter(new ButtonAdapter(getContext(),takenTimeSlots));
            int pos = gridview.getFirstVisiblePosition();
            gridview.smoothScrollToPosition(pos);
            gridview.setNumColumns(4);
            TextView datetv = createAppointment.findViewById(R.id.date);
            datetv.setText("Date: "+date);
            EditText userEmail = createAppointment.findViewById(R.id.emailEditText);
            TextView emailError = createAppointment.findViewById(R.id.error_email);
            Button bookNow = createAppointment.findViewById(R.id.bookNow);
            bookNow.setOnClickListener(view2 ->
            {
                if(Validation(userEmail,emailError,gridview))
                {
                    UUID id = UUID.randomUUID();
                    emailError.setText("");
                    String time = sharedBookPreferences.getString("timeSlot", null);
                    String hour = time.split(":",2)[0];
                    String minute = time.split(":",2)[1];

                    try {
                        Date date1=new SimpleDateFormat("yyyy-MM-dd").parse(date);
                        calendar.setTime(date1);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hour));
                    calendar.set(Calendar.MINUTE, Integer.parseInt(minute));
                    calendar.set(Calendar.SECOND, 0);
                    Book book = new Book(id.toString(),admin.getStoreID(),userEmail.getText().toString(),admin.getUserEmail(),calendar);
                    bookingList.add(book);
                    bookingListForDay.add(book);

                    MainActivity.postBookAppointment(getContext(),book);
                    adapter.notifyItemInserted(bookingListForDay.size());
                    updateCompactCalendarView();
                    createAppointment.dismiss();
                }
            });
        });
        currMonth.setText(dateFormatForMonth.format(compactCalendarView.getFirstDayOfCurrentMonth()));
        return view;
    }

    private Boolean Validation(EditText userEmail, TextView emailError, GridView gridview){
        boolean flag = false;
        if(userEmail.getText().toString().equals("")) {
            userEmail.setBackgroundResource(R.drawable.red_error_style);
            emailError.setText(R.string.this_is_req);
            flag = true;
        }
        if(!Registration.isEmailExist(userEmail.getText().toString())) {
            userEmail.setBackgroundResource(R.drawable.red_error_style);
            emailError.setText(R.string.not_exists);
            flag = true;
        }
        else {
            userEmail.setBackgroundResource(R.drawable.textview_corner_style);
            emailError.setText("");
        }
        if(sharedBookPreferences.getString("timeSlot", null) == null)
        {
            gridview.setBackgroundColor(getResources().getColor(R.color.gridViewError));
            flag = true;
        }
        else
            gridview.setBackgroundColor(Color.WHITE);
        if(!flag) {
            userEmail.setBackgroundResource(R.drawable.textview_corner_style);
            emailError.setText("");
            return true;
        }
        return false;

    }
    private List<Event> ConvertBookingIntoEvent(List<Book> bookingList)
    {
        Calendar calendar = Calendar.getInstance();
        for (Book book : bookingList) {
            calendar.setTime(book.getTime());
            int year = Integer.parseInt(book.getDate().split("-",3)[0]);
            int month =  Integer.parseInt(book.getDate().split("-",3)[1]);
            int dayOfMonth =  Integer.parseInt(book.getDate().split("-",3)[2].split("T",2)[0]);
            calendar.set(year, month-1, dayOfMonth);
            events.add(new Event(R.color.colorAccent, calendar.getTimeInMillis(), "Event at " + new Date(calendar.getTimeInMillis())));
        }
        return events;
    }

    @Override
    public void updateList() {
        bookingListForDay.clear();
        for (Book book : bookingList) {
            if(book.getDate().equals(date)) {
                bookingListForDay.add(book);
            }
        }
    }

    @Override
    public void updateCompactCalendarView() {
        events.clear();
        compactCalendarView.removeAllEvents();
        events = ConvertBookingIntoEvent(bookingList);
        compactCalendarView.addEvents(events);
    }

    public static Admin findAdminByStoreID(String StoreID)
    {
        return Login.adminsList.stream().filter(admin1 -> admin1.getStoreID().equals(StoreID)).findFirst().orElse(null);
    }
    public void clearHistory(){
        sharedBookPreferences = getActivity().getSharedPreferences("book", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedBookPreferences.edit();
        editor.remove("timeSlot");
        editor.apply();
    }
}