package com.findmybarber.view.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.findmybarber.R;
import com.findmybarber.model.BookAdapter;
import com.findmybarber.model.GetBookingForCurrentUserList;
import com.findmybarber.model.StoreAdapter;
import com.findmybarber.view.activities.MainActivity;

import java.util.concurrent.ExecutionException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ActionMe#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ActionMe extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ActionMe() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment action_me.
     */
    // TODO: Rename and change types and number of parameters
    public static ActionMe newInstance(String param1, String param2) {
        ActionMe fragment = new ActionMe();
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
        GetBookingForCurrentUserList getBookingForCurrentUserList= new GetBookingForCurrentUserList(getContext());
        try {
            MainActivity.appointmentsForUserList.addAll(getBookingForCurrentUserList.execute().get());
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        View view = inflater.inflate(R.layout.fragment_action_me, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.bookingList);
//        MainActivity.appointmentsForUserList.sort((s1, s2) -> Double.compare(s1.getDate(), s2.getDate()));
        BookAdapter adapter = new BookAdapter(MainActivity.appointmentsForUserList, getActivity());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(linearLayoutManager);
        return view;
    }
}