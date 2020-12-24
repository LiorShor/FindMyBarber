package com.findmybarber.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.nfc.cardemulation.HostApduService;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.ArrayMap;
import android.util.Log;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.findmybarber.R;
import com.findmybarber.model.Store;
import com.findmybarber.model.StoreAdapter;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class BarberSearchActivity extends AppCompatActivity {
    private DrawerLayout mDrawer;
    private Toolbar toolbar;
    private NavigationView nvDrawer;
    private RecyclerView recyclerView;
    private StoreAdapter adapter;

    private static final String TAG = "BarberSearchActivity";
    // Make sure to be using androidx.appcompat.app.ActionBarDrawerToggle version.
    private ActionBarDrawerToggle drawerToggle;
    private double longitude;
    private double latitude;
    private List<Store> storesList;
//    LocationManager mLocationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barber_search);

        // Set a Toolbar to replace the ActionBar.
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        nvDrawer = (NavigationView) findViewById(R.id.nvView);
        setupDrawerContent(nvDrawer);
        drawerToggle = setupDrawerToggle();
        drawerToggle.setDrawerIndicatorEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        drawerToggle.getDrawerArrowDrawable().setColor(ContextCompat.getColor(this, R.color.white));
        drawerToggle.syncState();

        recyclerView = findViewById(R.id.storeslist);
        storesList = new ArrayList<>();

        find_Location(this);
        Log.d(TAG, String.valueOf(latitude +","+ longitude));


        // API REQ to fetch data
        GetStores getStores = new GetStores(this);
        getStores.execute();

        if (!isLocationEnabled())
            showAlert();
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    public void find_Location(Context con) {

        Log.d("Find Location", "in find_location");
        String location_context = Context.LOCATION_SERVICE;
        LocationManager locationManager = (LocationManager) con.getSystemService(location_context);
        List<String> providers = locationManager.getProviders(true);
        for (String provider : providers) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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

    private void showAlert() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Enable Location")
                .setMessage("Your Locations Settings is set to 'Off'.\nPlease Enable Location to " +
                        "use this app")
                .setPositiveButton("Location Settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    }
                });
        dialog.show();
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }
    private ActionBarDrawerToggle setupDrawerToggle() {
        // NOTE: Make sure you pass in a valid toolbar reference.  ActionBarDrawToggle() does not require it
        // and will not render the hamburger icon without it.
        return new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.app_name,  R.string.action_settings);
    }
    public void selectDrawerItem(MenuItem menuItem) {
        // Create a new fragment and specify the fragment to show based on nav item clicked
        Fragment fragment = null;
        Class fragmentClass = null; //TODO: DELETE
        switch(menuItem.getItemId()) {
            case R.id.nav_first_fragment:
//                fragmentClass = FirstFragment.class; //TODO: navigate to another activity
                break;
            case R.id.nav_second_fragment:
//                fragmentClass = SecondFragment.class; //TODO: navigate to another activity
                break;
            case R.id.nav_third_fragment:
//                fragmentClass = ThirdFragment.class; //TODO: navigate to another activity
                break;
            default:
//                fragmentClass = FirstFragment.class; //TODO: navigate to another activity
        }

        try {
            assert fragmentClass != null;
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        assert fragment != null;
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();

        // Highlight the selected item has been done by NavigationView
        menuItem.setChecked(true);
        // Set action bar title
        setTitle(menuItem.getTitle());
        // Close the navigation drawer
        mDrawer.closeDrawers();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawer.openDrawer(GravityCompat.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
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

            adapter = new StoreAdapter(storesList, context);
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
                    double latitude = location.getDouble("lat");
                    double longitude = location.getDouble("lng");
//                    String description
//                    long phoneNumber
                    Store store = new Store(ID,name, address, rank, "", 0, latitude, longitude);
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