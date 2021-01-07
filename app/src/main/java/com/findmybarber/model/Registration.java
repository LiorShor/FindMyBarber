package com.findmybarber.model;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

public class Registration {
    public static boolean isEmailExist() {
        return false;
    }
    public static boolean isValidEmailAddress(String email) {
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();
    }
    public static void volleyPost(Context context, String  firstName, String lastName, String email, String phoneNumber, String password){
        String postUrl = "http://192.168.1.27:45457/api/user/addUser";
        RequestQueue requestQueue = Volley.newRequestQueue(context);
//        String id = UUID.randomUUID().toString().replace("-", "");
        JSONObject postData = new JSONObject();
        try {
            postData.put("ID", 0);
            postData.put("FirstName", firstName);
            postData.put("LastName", lastName);
            postData.put("Email", email);
            postData.put("PhoneNumber", phoneNumber);
            postData.put("Password", password);
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
}
