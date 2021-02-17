package com.findmybarber.view.fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.findmybarber.R;
import com.findmybarber.model.GetDBStoresList;
import com.findmybarber.model.GetStores;
import com.findmybarber.model.Store;
import com.findmybarber.model.StoreAdapter;
import com.findmybarber.view.activities.Login;
import com.findmybarber.view.activities.MainActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.findmybarber.view.activities.Login.dbStoresList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BarberSearch#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BarberSearch extends Fragment {
    private static final String TAG = "BarberSearchActivity";
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public BarberSearch() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BarberSearch.
     */
    // TODO: Rename and change types and number of parameters
    public static BarberSearch newInstance(String param1, String param2) {
        BarberSearch fragment = new BarberSearch();
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
            // TODO: Rename and change types of parameters
            String mParam1 = getArguments().getString(ARG_PARAM1);
            String mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_barber_search, container, false);
        List<Store> storesList = new ArrayList<>();
        GetStores getStores = new GetStores(getContext());
        dbStoresList.clear();
        GetDBStoresList getDBStoresList = new GetDBStoresList();
        try {
            storesList = getStores.execute().get();
            dbStoresList.addAll(getDBStoresList.execute().get());
            for (Store store : dbStoresList) {
                double distance = Math.round(GetStores.distance(store.getLatitude(), getStores.getSelfLatitude(), store.getLongitude(), getStores.getSelfLongitude(), 0, 0) / 1000 * 100.0) / 100.0;
                if (distance < 3) {
                    store.setDescription(distance + " km");
                    storesList.add(store);
                }
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        RecyclerView recyclerView = view.findViewById(R.id.storeslist);
        storesList.sort((s1, s2) -> Double.compare(s1.getDistance(), s2.getDistance()));
        StoreAdapter adapter = new StoreAdapter(getActivity(), storesList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(linearLayoutManager);
        return view;
    }
}



