package com.findmybarber.view.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.findmybarber.R;
import com.findmybarber.model.Admin;
import com.findmybarber.model.Customer;
import com.findmybarber.model.EditUserProfile;
import com.findmybarber.model.User;
import com.findmybarber.model.UserType;
import com.findmybarber.view.activities.Login;
import com.findmybarber.view.activities.MainActivity;

import java.util.concurrent.ExecutionException;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EditProfile#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditProfile extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public EditProfile() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EditProfile.
     */
    // TODO: Rename and change types and number of parameters
    public static EditProfile newInstance(String param1, String param2) {
        EditProfile fragment = new EditProfile();
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
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);
        SharedPreferences sharedUserPreferences = getContext().getSharedPreferences("CurrentUserPref",MODE_PRIVATE);
        String userEmail = sharedUserPreferences.getString("KeyUser",null);
        MainActivity mainActivity = (MainActivity) getActivity();
        String fullName = mainActivity.getFullName(userEmail);

        TextView firstName = view.findViewById(R.id.editText_FirstName);
        TextView lastName = view.findViewById(R.id.editText_LastName);
        TextView errorFirst = view.findViewById(R.id.tv_error_first);
        TextView errorLast = view.findViewById(R.id.tv_error_last);
        Button buttonEdit = view.findViewById(R.id.bt_edit_profile);

        firstName.setText(fullName.split(" ", 2)[0]);
        lastName.setText(fullName.split(" ", 2)[1]);

        buttonEdit.setOnClickListener(v -> {
            String txtFirstName = firstName.getText().toString();
            String txtLastName = lastName.getText().toString();
            if(inputValidation(txtFirstName, txtLastName, errorFirst, errorLast, firstName, lastName)) {
                Customer customer = Login.customersList.stream().filter(c-> c.getUserEmail().equals(userEmail)).findAny().get();
                if(customer!= null) {
                    if(!txtFirstName.isEmpty())
                        customer.setFirstName(firstName.getText().toString());
                    if(!txtLastName.isEmpty())
                        customer.setLastName(lastName.getText().toString());
                }
                else {
                    Admin admin = Login.adminsList.stream().filter(adm-> adm.getUserEmail().equals(userEmail)).findAny().get();
                    if(!txtFirstName.isEmpty())
                        admin.setFirstName(firstName.getText().toString());
                    if(!txtLastName.isEmpty())
                        admin.setLastName(lastName.getText().toString());
                }
                EditUserProfile editUserProfile = new EditUserProfile(userEmail, txtFirstName, txtLastName);
                editUserProfile.execute();
                mainActivity.loadBarberSearch();
                Toast.makeText(getContext(), "Your name has been changed successfully", Toast.LENGTH_SHORT).show();
            }
        });

        return  view;
    }

    private boolean inputValidation(String firstName, String lastName, TextView errorFirst, TextView errorLast, TextView tvFirst, TextView tvLast) {
        boolean flag = true;
        if(firstName.matches(".*\\d.*")) {
            tvFirst.setBackgroundResource(R.drawable.red_error_style);
            errorFirst.setText(R.string.illegal_name);
            flag = false;
        }
        if(firstName.isEmpty()) {
            tvFirst.setBackgroundResource(R.drawable.red_error_style);
            errorFirst.setText(R.string.this_is_req);
            flag = false;
        }

        if(lastName.matches(".*\\d.*")) {
            tvLast.setBackgroundResource(R.drawable.red_error_style);
            errorLast.setText(R.string.illegal_surname);
            flag = false;
        }

        if(lastName.isEmpty()) {
            tvLast.setBackgroundResource(R.drawable.red_error_style);
            errorLast.setText(R.string.this_is_req);
            flag = false;
        }
        return flag;
    }
}