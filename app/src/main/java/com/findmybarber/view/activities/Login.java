package com.findmybarber.view.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.findmybarber.R;
import com.findmybarber.model.Admin;
import com.findmybarber.model.Book;
import com.findmybarber.model.Customer;
import com.findmybarber.model.GetAdminList;
import com.findmybarber.model.Registration;
import com.findmybarber.model.Store;
import com.findmybarber.model.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.Permissions;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;


import static com.findmybarber.model.Registration.isEmailExist;
import static com.findmybarber.model.Registration.isValidEmailAddress;


public class Login extends AppCompatActivity implements View.OnClickListener {
    private EditText et_FirstName,et_LastName, et_Password, et_Email , et_Phone;
    private TextView txt_err1,txt_err2,txt_err3,txt_err4,txt_err5;
    private static final String KEY = "MainActivityKey";
    private static final String default_web_client_id = "606913807542-7cb6jf2dopalri7e2c4d3pkbf7n1sp5j.apps.googleusercontent.com";
    private static final String TAG = "SignInActivity";
    private static final String EMAIL = "email";
    private static final String USERNAME = "public_profile";
    private static final int RC_SIGN_IN = 9001;
    private Handler mainHandler = new Handler();
    GoogleSignInClient mGoogleSignInClient;
    private Dialog registerDialog;
    private Dialog loginDialog;
    private CallbackManager callbackManager;
    private FirebaseAuth mAuth;
    public static List<Customer> usersList = new ArrayList<>();
    public static List<Admin> adminsList = new ArrayList<>();
    public static List<Store> dbStoresList = new ArrayList<>();

    SharedPreferences pref;
    Customer customer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getCustomersList();
//        getAdminsList();
        GetAdminList getAdminList = new GetAdminList();
        try {
            adminsList.addAll(getAdminList.execute().get());
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        pref = getSharedPreferences("CurrentUserPref",MODE_PRIVATE); // Private data saved on your device
        if (pref.getString("KeyUser",null) != null) {
            Intent intent = new Intent(Login.this, MainActivity.class);
            startActivity(intent);
        }
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(default_web_client_id)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        callbackManager = CallbackManager.Factory.create();
        SignInButton signInButton = findViewById(R.id.sign_in_with_google);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        loginDialog = new Dialog(this,R.style.PauseDialog);
        loginDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        findViewById(R.id.sign_in_with_google).setOnClickListener(this);

        Dexter.withActivity(this).withPermissions(Manifest.permission.READ_CALENDAR,
                Manifest.permission.WRITE_CALENDAR).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                AccessToken accessToken = AccessToken.getCurrentAccessToken();
                if(accessToken!=null)
                {
                    //TODO:: need to transfer from activity to another in case of succsess on permissions
                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
            }
        }).check();
//        ConnectToDatabase();
    }


/*
    public void getMixedList() {
        usersList.addAll(customersList);
        usersList.addAll(adminsList);
    }
*/

    public void getCustomersList() {
        String url = "http://192.168.1.2:45455/api/user/getUserClientsList";
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    Gson gson = new Gson();
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject jsonObject = response.getJSONObject(i);
                        Customer customer = gson.fromJson(jsonObject.toString(), Customer.class);
                        usersList.add(customer);
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


    /*public void getAdminsList() {
        String url = "http://192.168.1.2:45455/api/user/getUserAdminsList";
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    Gson gson = new Gson();
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject jsonObject = response.getJSONObject(i);
                        Admin admin = gson.fromJson(jsonObject.toString(), Admin.class);
                        adminsList.add(admin);
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
*/
    @Override
    public void onStart() {
        super.onStart();
    }

    public void register(View view) {
        boolean flag = false;
        et_FirstName = registerDialog.findViewById(R.id.editTextFirstName);
        et_LastName = registerDialog.findViewById(R.id.editTextLastName);
        et_Password = registerDialog.findViewById(R.id.editTextPassword);
        et_Email = registerDialog.findViewById(R.id.editTextEmailAddress);
        et_Phone = registerDialog.findViewById(R.id.editTextPhone);
        txt_err1 = registerDialog.findViewById(R.id.tv_error_1);
        txt_err2 = registerDialog.findViewById(R.id.tv_error_2);
        txt_err3 = registerDialog.findViewById(R.id.tv_error_3);
        txt_err4 = registerDialog.findViewById(R.id.tv_error_4);
        txt_err5 = registerDialog.findViewById(R.id.tv_error_5);
        String firstName = et_FirstName.getText().toString();
        String lastName = et_LastName.getText().toString();
        String password = et_Password.getText().toString();
        String email = et_Email.getText().toString();
        String phone = et_Phone.getText().toString();
        if (validation(firstName, lastName, password, email, phone)) {
//            customer = new Customer(firstName, lastName, email, phone, password);
            flag = true;
        }
        if(flag) {
//            createUser(customer);
            Registration.volleyPost(getApplicationContext(),firstName, lastName, email, phone, password);
            customer = new Customer(firstName, lastName, email, phone, password);
            usersList.add(customer);
            registerDialog.dismiss();
        }
    }

    private boolean validation(String firstName,String lastName,String password,String email,String phone){
        boolean flag = true;
        if(firstName.matches(".*\\d.*")) {
            et_FirstName.setBackgroundResource(R.drawable.red_error_style);
            txt_err1.setText(R.string.illegal_name);
            flag = false;
        }
        else if(firstName.equals("")) {
            et_FirstName.setBackgroundResource(R.drawable.red_error_style);
            txt_err1.setText(R.string.this_is_req);
            flag = false;
        }
        if(lastName.matches(".*\\d.*")) {
            et_LastName.setBackgroundResource(R.drawable.red_error_style);
            txt_err2.setText(R.string.illegal_surname);
            flag = false;
        }
        else if(lastName.equals("")) {
            et_LastName.setBackgroundResource(R.drawable.red_error_style);
            txt_err2.setText(R.string.this_is_req);
            flag = false;
        }
        if(password.equals("")) {
            et_Password.setBackgroundResource(R.drawable.red_error_style);
            txt_err3.setText(R.string.this_is_req);
            flag = false;
        }
        if(phone.equals("")) {
            et_Phone.setBackgroundResource(R.drawable.red_error_style);
            txt_err5.setText(R.string.this_is_req);
            flag = false;
        }
        else if(phone.length() != 10) {
            et_Phone.setBackgroundResource(R.drawable.red_error_style);
            txt_err5.setText(R.string.illegal_phone);
            flag = false;
        }
        if(email.equals("")) {
            et_Email.setBackgroundResource(R.drawable.red_error_style);
            txt_err4.setText(R.string.this_is_req);
            flag = false;
        }
        else if (!isValidEmailAddress(email))
        {
            et_Email.setBackgroundResource(R.drawable.red_error_style);
            txt_err4.setText(R.string.illegal_email);
            flag = false;
        }
        if (isEmailExist(email))
        {
            et_Email.setBackgroundResource(R.drawable.red_error_style);
            txt_err4.setText(R.string.exist_acc);
        }
        return flag;
    }

    private void showRegisterDialog(){
/*        threadRunnable runnable = new threadRunnable();
        new Thread(runnable).start();*/
        registerDialog.setContentView(R.layout.activity_registration);
        registerDialog.show();

    }

    public void dismiss(View view) {
        if(view.getId()==R.id.img_close)
            registerDialog.dismiss();
        else
            loginDialog.dismiss();
    }

    public void backToLogin(View view) {
        registerDialog.dismiss();
        loginDialog.setContentView(R.layout.activity_sign_in);
        loginDialog.show();
    }

    public void showLoginDialog(View view) {
/*        threadRunnable runnable = new threadRunnable();
        new Thread(runnable).start();*/
        loginDialog.setContentView(R.layout.activity_sign_in);
        loginDialog.show();
//        goToRegister(view);
    }

    public void loginFunc(View view) {
        EditText emailText = loginDialog.findViewById(R.id.etLoginEmailAddress);
        String email = emailText.getText().toString();
        EditText passwordText = loginDialog.findViewById(R.id.etLoginPassword);
        String password = passwordText.getText().toString();
        if(!checkIfEmpty(password,email) && checkCredentials(email, password)) {
            pref = getApplicationContext().getSharedPreferences("CurrentUserPref",MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
                editor.putString("KeyUser",email);
                editor.putString("KeyPassword",password);
                editor.apply();

            Intent intent = new Intent(Login.this, MainActivity.class);
            startActivity(intent);
        }
        else {
            emailText.setBackgroundResource(R.drawable.red_error_style);
            passwordText.setBackgroundResource(R.drawable.red_error_style);
        }
    }

    private boolean checkCredentials(String email, String password) {
        return  adminsList.stream().anyMatch(user -> user.getUserEmail().equals(email) && user.getUserPassword().equals(password))
        || usersList.stream().anyMatch(user -> user.getUserEmail().equals(email) && user.getUserPassword().equals(password));
    }

    private boolean checkIfEmpty(String password,String email){
        return (password.equals("")&&email.equals(""));
    }

    public void goToRegister(View view) {
        loginDialog.dismiss();
        registerDialog = new Dialog(this, R.style.PauseDialog);
        registerDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        showRegisterDialog();
    }
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.sign_in_with_google) {
            signIn();
            // ...
        }
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
/*        threadRunnable runnable = new threadRunnable();
        new Thread(runnable).start();*/
    }

    private void signOut() {
        mGoogleSignInClient.signOut().addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                // [START_EXCLUDE]
            }
        });
    }

    public void loginWithFireBase(String email,String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            Toast.makeText(Login.this, "Authentication succsess.",
                                    Toast.LENGTH_SHORT).show();
//                                FirebaseUser user = mAuth.getCurrentUser();
                            Intent intent = new Intent(Login.this, MainActivity.class);
                            startActivity(intent);
                            // Sign in success, update UI with the signed-in user's information
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.d(TAG, "onComplete: ",task.getException());
                            Toast.makeText(Login.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        // ...
                    }
                });
    }
    public void facebookLogin(View view) {
        LoginButton loginButton = findViewById(R.id.login_button);
        loginButton.setPermissions(Arrays.asList(EMAIL,USERNAME));
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(FacebookException exception) {
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }
    AccessTokenTracker tokenTracker = new AccessTokenTracker() {
        @Override
        protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
            if(currentAccessToken!=null)//user logged out
                loadUserProfile((currentAccessToken));
        }
    };

    //This func make GraphAPIRequest from Facebook site to get the Username
    private void loadUserProfile(AccessToken accessToken){
        final Intent intent = new Intent(this, MainActivity.class);
        GraphRequest request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                try {
                    String firstName = object.getString("first_name");
                    String lastName = object.getString("last_name");
                    String email = object.getString("email");
                    String id = object.getString("id");
                    String[] array = {firstName,lastName,email,id};
                    intent.putExtra(KEY,array);
//                    startActivity(intent);
//                    String imageUrl = "Https://graph.facebook.com/"+id+"/picture?type=normal";
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        Bundle parameters = new Bundle();
        parameters.putString("fields","first_name,last_name,email,id");
        request.setParameters(parameters);
        request.executeAsync();
    }
    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(Login.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            Intent intent = new Intent(Login.this, MainActivity.class);
            intent.putExtra("Account",account);
            startActivity(intent);
        } catch (ApiException e) {
            Log.w(TAG, "handleSignInResult:error", e);
            Toast.makeText(this, "fuck", Toast.LENGTH_SHORT).show();

        }
    }

    class threadRunnable implements Runnable{
        @Override
        public void run() {
            Handler mainHandler = new Handler(Looper.getMainLooper());
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    loginDialog.setContentView(R.layout.activity_sign_in);
                    loginDialog.show();
/*                    Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                    startActivityForResult(signInIntent, RC_SIGN_IN);*/
                }
            });
        }
    }
}