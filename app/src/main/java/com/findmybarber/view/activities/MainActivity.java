package com.findmybarber.view.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.EditText;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.findmybarber.model.Admin;
import com.findmybarber.model.Customer;
import com.findmybarber.model.Store;
import com.findmybarber.view.fragments.About;
import com.findmybarber.view.fragments.EditProfile;
import com.findmybarber.R;
import com.findmybarber.model.Book;
import com.findmybarber.view.fragments.ActionMe;
import com.findmybarber.view.fragments.AddBarber;
import com.findmybarber.controller.asynctask.GetStorePhone;
import com.findmybarber.view.fragments.AdminManagement;
import com.findmybarber.view.fragments.StoreDetails;
import com.findmybarber.view.fragments.BarberSearch;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout mDrawer;
    private Toolbar toolbar;
    private String currUser;
    private FragmentManager fragmentManager;
    EditText searchText;
    private Dialog callDialog;
    private static final String TAG = "MainActivity";
    public static List<Book> bookingsList = new ArrayList<>();
    public static List<Book> appointmentsForUserList = new ArrayList<>();
    private NavigationView nvDrawer;
    private SharedPreferences userPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: ");
        // Set a Toolbar to replace the ActionBar.
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        userPref = getSharedPreferences("CurrentUserPref",MODE_PRIVATE);


        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        mDrawer = findViewById(R.id.drawer_layout);
        nvDrawer = findViewById(R.id.nvView);
        setupDrawerContent(nvDrawer);


        ActionBarDrawerToggle drawerToggle = setupDrawerToggle();
        drawerToggle.setDrawerIndicatorEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        drawerToggle.getDrawerArrowDrawable().setColor(ContextCompat.getColor(this, R.color.white));
        drawerToggle.syncState();
        hideItemOnAdminLogin();

        View headerView = nvDrawer.getHeaderView(0);
        TextView tvHeader = headerView.findViewById(R.id.textHeader);
        tvHeader.setText("Hello " + getFullName(userPref.getString("KeyUser",null)));


        if (!isLocationEnabled()) {
            showAlert();
        }
        else {
            if (userPref.getString("KeyUser",null) != null) {
                currUser = userPref.getString("KeyUser",null);
                if(checkIfAdmin(currUser)) {
                    Admin userAdmin = Login.adminsList.stream().filter(admin -> admin.getUserEmail().equals(currUser)).findAny().orElse(null);
                    SharedPreferences.Editor editor = userPref.edit();
                    editor.putString("StoreID",userAdmin.getStoreID());
                    editor.apply();
                    loadAdminManagement();
                    bottomNav.setVisibility(View.INVISIBLE);
                }
                else{
                    loadFirstFragment();
                    callDialog = new Dialog(this,R.style.PauseDialog);
                    callDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                }
            }
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener = item -> {
        // By using switch we can easily get
        // the selected fragment
        // by using there id.
        Fragment selectedFragment = null;
        switch (item.getItemId()) {
            case R.id.action_me:
                selectedFragment = new ActionMe();
                break;
/*            case R.id.action_favorites:
                selectedFragment = new ActionFavorites();
                break;*/
            case R.id.action_home:
                if(checkIfAdmin(userPref.getString("KeyUser",null))) {
                    selectedFragment = new AdminManagement();
                }
                else {
                    selectedFragment = new BarberSearch();
                }
                break;
        }
        // It will help to replace the
        // one fragment to other.
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.flContent, selectedFragment)
                .addToBackStack(null)
                .commit();
        return true;
    };

    public boolean checkIfAdmin(String email) {
        return Login.adminsList.stream().anyMatch(user -> user.getUserEmail().equals(email));
    }

    public Fragment getVisibleFragment(){
        FragmentManager fragmentManager = MainActivity.this.getSupportFragmentManager();
        List<Fragment> fragments = fragmentManager.getFragments();
        if(fragments != null){
            for(Fragment fragment : fragments){
                if(fragment != null && fragment.isVisible())
                    return fragment;
            }
        }
        return null;
    }

    @Override
    public void onBackPressed() {
        Fragment f = getVisibleFragment();
        if(!(f instanceof BarberSearch) && !(f instanceof AdminManagement) )
//            if(checkIfAdmin(currUser))
//                this.moveTaskToBack(true);
//            else
        {
            super.onBackPressed();
            this.moveTaskToBack(false);
        }
        else
            this.moveTaskToBack(true);
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }
    private void showAlert() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Enable Location")
                .setMessage("Your Locations Settings is set to 'Off'.\nPlease Enable Location to " +
                        "use this app")
                .setPositiveButton("Location Settings", (paramDialogInterface, paramInt) -> {
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(myIntent);
                })
                .setNegativeButton("Cancel", (paramDialogInterface, paramInt) -> {
                });
        dialog.show();
    }

    public void getBookingList(String storeID) {
        String url = "http://192.168.1.27:45455/api/book/getBookingList/" + storeID;
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject jsonObject = response.getJSONObject(i);
                        String id = jsonObject.getString("ID");
                        String storeID = jsonObject.getString("StoreID");
                        String emailClient = jsonObject.getString("EmailClient");
                        String emailStore = jsonObject.getString("EmailStore");
                        String date = jsonObject.getString("Date");
                        String time = jsonObject.getString("Time");
                        String hour = time.split(":",2)[0];
                        String minute = time.split(":",3)[1];
                        Calendar calendar = Calendar.getInstance();
                        int year = Integer.parseInt(date.split("-",3)[0]);
                        int month =  Integer.parseInt(date.split("-",3)[1]);
                        int dayOfMonth =  Integer.parseInt(date.split("-",3)[2].split("T",2)[0]);
                        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hour));
                        calendar.set(Calendar.MINUTE, Integer.parseInt(minute));
                        calendar.set(Calendar.SECOND, 0);
                        calendar.set(year, month-1, dayOfMonth);
                        Book book = new Book(id,storeID,emailClient,emailStore,calendar);
//                        Book book = gson.fromJson(jsonObject.toString(), Book.class);
                        bookingsList.add(book);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        requestQueue.add(jsonArrayRequest);
    }

    public static void postBookAppointment(Context context,Book book){
        String postUrl = "http://192.168.1.27:45455/api/book/bookAppointment";
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        JSONObject postData = new JSONObject();
        try {
            postData.put("ID", book.getID());
            postData.put("StoreID", book.getStoreID());
            postData.put("EmailClient", book.getEmailClient());
            postData.put("EmailStore", book.getEmailStore());
            postData.put("Date", book.getDate());
            postData.put("Time", book.getTime());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, postUrl, postData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                System.out.println(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        requestQueue.add(jsonObjectRequest);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        if(nvDrawer.getCheckedItem() != null)
                            nvDrawer.getCheckedItem().setChecked(false);
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

        switch(menuItem.getItemId()) {
            case R.id.nav_new_barber:
                fragment = new AddBarber();
                break;
            case R.id.nav_edit_profile:
                fragment = new EditProfile();
                break;
            case R.id.nav_about:
                fragment = new About();
                break;
            case R.id.nav_home:
                if(checkIfAdmin(userPref.getString("KeyUser",null))) {
                    fragment = new AdminManagement();
                }
                else {
                    fragment = new BarberSearch();
                }
                break;
            case R.id.logout:
                logout();
                break;

            default:
//                fragmentClass = FirstFragment.class; //TODO: navigate to another activity
        }

        if(fragment != null) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.flContent, fragment)
                        .addToBackStack(null)
                        .commit();
            }

        // Highlight the selected item has been done by NavigationView
//        menuItem.setChecked(false);
        // Set action bar title
        setTitle(menuItem.getTitle());
        // Close the navigation drawer
        mDrawer.closeDrawers();
    }

    public void logout() {
        SharedPreferences.Editor editor = userPref.edit();
        editor.remove("KeyUser");
        editor.remove("KeyPassword");
        editor.apply();
        Intent intent = new Intent(MainActivity.this, Login.class);
        startActivity(intent);
    }

    public String getCurrentUserEmail() {
        return userPref.getString("KeyUser",null);
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

    public void loadFirstFragment() {
        fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.flContent, new BarberSearch()).commit();
    }
    public void loadAdminManagement() {
        fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
       fragmentTransaction.add(R.id.flContent, new AdminManagement()).commit();
    }
    public void loadStoreDetails() {
        fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.flContent, new StoreDetails()).addToBackStack(null).commit();
    }

    public void SearchStore(View view) {
        searchText = findViewById(R.id.searchText);

        String storeName = searchText.getText().toString().toLowerCase();
        Store store = Login.dbStoresList.stream().filter(store1 -> store1.getName().toLowerCase().equals(storeName)).findAny().orElse(null);
        if(store == null) { //Search in Google API
            GetStorePhone getStorePhone = new GetStorePhone(storeName,getApplicationContext());
            try {
                String phoneNumber = getStorePhone.execute().get();
                callDialog.setContentView(R.layout.calldialog);
                callDialog.show();
                Button callNow = callDialog.findViewById(R.id.call);
                callNow.setOnClickListener(view1 -> {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + phoneNumber));//change the number
                    getApplicationContext().startActivity(callIntent);
                });
                Button cancel = callDialog.findViewById(R.id.cancel);
                cancel.setOnClickListener(view1 -> callDialog.dismiss());

            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }
        else
        {
            loadStoreDetails();
            SharedPreferences sharedPreferences;
            sharedPreferences = getSharedPreferences("store", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("storeName", store.getName());
            editor.putString("storeID", store.getID());
            editor.apply();
        }
    }

    public void loadBarberSearch() {
        fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.flContent, new BarberSearch()).addToBackStack(null).commit();
    }

    public String getFullName(String userEmail) {
        if(!checkIfAdmin(userEmail)) {
            Customer customer = Login.customersList.stream().filter(c-> c.getUserEmail().equals(userEmail)).findAny().get();
            return customer.getUserName() + " " + customer.getUserSurname();
        }
        else {
            Admin admin = Login.adminsList.stream().filter(adm-> adm.getUserEmail().equals(userEmail)).findAny().get();
            return admin.getUserName() + " " + admin.getUserSurname();
        }
    }

    private void hideItemOnAdminLogin()
    {
        if(checkIfAdmin(userPref.getString("KeyUser",null))) {
            Menu nav_Menu = nvDrawer.getMenu();
            nav_Menu.findItem(R.id.nav_new_barber).setVisible(false);
        }
    }
}