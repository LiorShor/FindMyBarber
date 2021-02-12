package com.findmybarber.view.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.findmybarber.R;
import com.findmybarber.model.Book;
import com.findmybarber.model.BookingAdapter;
import com.findmybarber.model.GetBookingList;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
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
    public static List<Book>bookingList = new ArrayList<>();
    private final List<Book>bookingListForDay = new ArrayList<>();
    private List<Event> events = new ArrayList<>();
    private final List<String> bookingStringsListForDay = new ArrayList<>();
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

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
        String storeID = pref.getString("StoreID", null);
        GetBookingList getBookingList = new GetBookingList(storeID);
        try {
            bookingList.addAll(getBookingList.execute().get());
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
        compactCalendarView.setFirstDayOfWeek(Calendar.MONDAY);
        compactCalendarView.shouldDrawIndicatorsBelowSelectedDays(true);
        compactCalendarView.setIsRtl(false);
        compactCalendarView.displayOtherMonthDays(false);
        events = ConvertBookingIntoEvent(bookingList);
        compactCalendarView.addEvents(events);
        BookingAdapter adapter = new BookingAdapter(bookingStringsListForDay,bookingListForDay,getContext(), this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        RecyclerView recyclerView = view.findViewById(R.id.bookingList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(linearLayoutManager);
        TextView currMonth = view.findViewById(R.id.CurrMonth);
        currMonth.setText(dateFormatForMonth.format(compactCalendarView.getFirstDayOfCurrentMonth()));
        compactCalendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                String string;
                bookingStringsListForDay.clear();
                bookingListForDay.clear();
                for (Book book : bookingList) {
                    if(book.getDate().equals(dateFormatForDate.format(dateClicked))) {
                        string = "Meeting with " + book.getEmailClient() + " at " + book.getTime().toString();
                        bookingStringsListForDay.add(string);
                        bookingListForDay.add(book);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {

            }
        });
        recyclerView.setOnClickListener(view1 ->
                adapter.notifyDataSetChanged() );

        currMonth.setText(dateFormatForMonth.format(compactCalendarView.getFirstDayOfCurrentMonth()));
        return view;
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
    public void updateCompactCalendarView() {
        events.clear();
        compactCalendarView.removeAllEvents();
        events = ConvertBookingIntoEvent(bookingList);
        compactCalendarView.addEvents(events);
    }
}