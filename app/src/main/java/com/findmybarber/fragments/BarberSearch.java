package com.findmybarber.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.findmybarber.R;
import com.findmybarber.activities.APIReader;
import com.findmybarber.activities.MainActivity;
import com.findmybarber.model.Store;
import com.findmybarber.model.StoreAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BarberSearch#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BarberSearch extends Fragment {

    private RecyclerView recyclerView;
    private StoreAdapter adapter;

    private static final String TAG = "BarberSearchActivity";
    // Make sure to be using androidx.appcompat.app.ActionBarDrawerToggle version.
    private ActionBarDrawerToggle drawerToggle;
    private double longitude;
    private double latitude;
    private List<Store> storesList;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

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
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        storesList = new ArrayList<>();
        Log.d(TAG, "onCreate: ");

        find_Location(getContext());
        Log.d(TAG, String.valueOf(latitude +","+ longitude));


        // API REQ to fetch data
        GetStores getStores = new GetStores(getContext());
        getStores.execute();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_barber_search, container, false);
        View view1 =  inflater.inflate(R.layout.store_item, container, false);
        recyclerView = view.findViewById(R.id.storeslist);
        Button bookNow = view1.findViewById(R.id.bookNow);
        bookNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity mainActivity = (MainActivity) getActivity();
                assert mainActivity != null;
                mainActivity.loadSecondFragment();
            }
        });
        return view;
    }

    public static double distance(double lat1, double lat2, double lon1,
                                  double lon2, double el1, double el2) {

        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters

        double height = el1 - el2;

        distance = Math.pow(distance, 2) + Math.pow(height, 2);

        return Math.sqrt(distance);
    }

    public void find_Location(Context con) {

        Log.d("Find Location", "in find_location");
        String location_context = Context.LOCATION_SERVICE;
        LocationManager locationManager = (LocationManager) con.getSystemService(location_context);
        List<String> providers = locationManager.getProviders(true);
        for (String provider : providers) {
            if (ActivityCompat.checkSelfPermission(con, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(con, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            locationManager.requestLocationUpdates(provider, 1000, 0,
                    new LocationListener() {

                        public void onLocationChanged(Location location) {
                        }

                        public void onProviderDisabled(String provider) {
                        }

                        public void onProviderEnabled(String provider) {
                            find_Location(con);
                        }

                        public void onStatusChanged(String provider, int status,
                                                    Bundle extras) {
                        }
                    });
            Location location = locationManager.getLastKnownLocation(provider);
            if (location != null) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
            }
        }
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    private class GetStores extends AsyncTask<Void, Void, List<Store>> {

        private Context context;

        public GetStores(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected void onPostExecute(List<Store> result) {
            super.onPostExecute(result);
            storesList.addAll(result);
            storesList.sort((s1, s2) -> Double.compare(s1.getDistance(), s2.getDistance()));
            adapter = new StoreAdapter(getActivity(), storesList, context);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(linearLayoutManager);
        }

        @Override
        protected List<Store> doInBackground(Void... params) {
            List<Store> storesList = new ArrayList<>();
            String stream;
            String urlString = apiRequest();
            APIReader http = new APIReader();
            stream = http.getHTTPData(urlString);
            try {
                JSONObject object = new JSONObject(stream);
                JSONArray jsonMainArray = object.getJSONArray("results");
                for (int i = 0; i < jsonMainArray.length(); i++) {

                    JSONObject jsonObject = jsonMainArray.getJSONObject(i);
                    String ID = UUID.randomUUID().toString();
                    String name = jsonObject.getString("name");
                    String address = jsonObject.getString("vicinity");
                    double rank;
                    try { rank = Double.parseDouble(jsonObject.getString("rating"));
                    }
                    catch (JSONException ex) {
                        rank = 0;
                    }
                    JSONObject geometry = jsonObject.getJSONObject("geometry");

                    JSONObject location = geometry.getJSONObject("location");
                    double storeLatitude = location.getDouble("lat");
                    double storeLongitude = location.getDouble("lng");
                    double distance = Math.round(distance(storeLatitude, latitude, storeLongitude, longitude, 0, 0)/1000 *100.0)/100.0;
//                    String description
//                    long phoneNumber
                    Store store = new Store(ID,name, address, rank, distance +" km", 0, storeLatitude, storeLongitude, distance);
                    storesList.add(store);
                }
                return storesList;
            } catch (JSONException ex) {
                ex.printStackTrace();
            }
            return null;
        }
    }

    public String apiRequest()
    {
        StringBuilder sb = new StringBuilder();
        String API_KEY = "key=AIzaSyCxfal1FttVVLWd6TOwgmQbyE4cZLfWoPA";
        String API_LINK = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=";
        String radius = "radius=3000&";
        String type = "type=hair_care&";
        String latitude = String.valueOf(this.latitude);
        String longitude = String.valueOf(this.longitude);
        sb.append(API_LINK).append(latitude).append(",").append(longitude).append("&").append(radius).append(type).append(API_KEY);

        return sb.toString();
    }

}