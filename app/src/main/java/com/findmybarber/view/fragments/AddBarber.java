package com.findmybarber.view.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.findmybarber.R;
import com.findmybarber.controller.asynctask.AddStore;
import com.findmybarber.controller.asynctask.GetCoordinatesByAddress;
import com.findmybarber.model.Store;
import com.findmybarber.view.activities.MainActivity;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddBarber#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddBarber extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AddBarber() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddBarber.
     */
    // TODO: Rename and change types and number of parameters
    public static AddBarber newInstance(String param1, String param2) {
        AddBarber fragment = new AddBarber();
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
        View view =  inflater.inflate(R.layout.fragment_add_barber, container, false);
        MainActivity mainActivity = (MainActivity) getActivity();
        TextView tvStoreName = view.findViewById(R.id.editTextStoreName);
        TextView tvAddress = view.findViewById(R.id.editTextAddress);
        TextView tvPhoneNumber = view.findViewById(R.id.editTextPhone);
        TextView tvDescription = view.findViewById(R.id.editTextDescription);

        TextView errStoreName = view.findViewById(R.id.tv_error_1);
        TextView errAddress = view.findViewById(R.id.tv_error_2);
        TextView errPhoneNumber = view.findViewById(R.id.tv_error_3);
        TextView errDescription= view.findViewById(R.id.tv_error_4);

        Button buttonAddStore = view.findViewById(R.id.bt_add_store);

        buttonAddStore.setOnClickListener(v -> {
            String txtStoreName = tvStoreName.getText().toString();
            String txtAddress = tvAddress.getText().toString();
            String txtPhoneNumber = tvPhoneNumber.getText().toString();
            String txtDescription = tvDescription.getText().toString();

            if(inputValidation(txtStoreName, txtAddress, txtPhoneNumber, txtDescription, errStoreName, errAddress, errPhoneNumber, errDescription, tvStoreName, tvAddress, tvPhoneNumber, tvDescription)) {
                GetCoordinatesByAddress getCoordinatesByAddress = new GetCoordinatesByAddress(txtAddress);
                try {
                    String response = getCoordinatesByAddress.execute().get();
                    Store store = new Store(UUID.randomUUID().toString(), txtStoreName, txtAddress, 0, null, txtDescription, txtPhoneNumber,
                            Double.parseDouble(response.split(",", 2)[0]), Double.parseDouble(response.split(",", 2)[1]));
                    assert mainActivity != null;
                    AddStore addStore = new AddStore(store, mainActivity.getCurrentUserEmail());
                    addStore.execute().get();
                    Toast.makeText(mainActivity, "Congratulations, you are now a new BARBER!", Toast.LENGTH_SHORT).show();
                    mainActivity.logout();
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        tvStoreName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tvStoreName.setBackgroundResource(R.drawable.textview_corner_style);
                errStoreName.setText("");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        tvAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tvAddress.setBackgroundResource(R.drawable.textview_corner_style);
                errAddress.setText("");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        tvPhoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tvPhoneNumber.setBackgroundResource(R.drawable.textview_corner_style);
                errPhoneNumber.setText("");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        tvDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tvDescription.setBackgroundResource(R.drawable.textview_corner_style);
                errDescription.setText("");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return view;
    }

    private boolean inputValidation(String txtStoreName, String txtAddress, String txtPhoneNumber, String txtDescription, TextView errStoreName, TextView errAddress, TextView errPhoneNumber, TextView errDescription, TextView tvStoreName, TextView tvAddress, TextView tvPhoneNumber, TextView tvDescription) {
        boolean flag = true;
        
        if(txtStoreName.isEmpty()) {
            tvStoreName.setBackgroundResource(R.drawable.red_error_style);
            errStoreName.setText(R.string.this_is_req);
            flag = false;
        }

        if(txtAddress.isEmpty()) {
            tvAddress.setBackgroundResource(R.drawable.red_error_style);
            errAddress.setText(R.string.this_is_req);
            flag = false;
        }

        if(txtPhoneNumber.length() != 10) {
            tvPhoneNumber.setBackgroundResource(R.drawable.red_error_style);
            errPhoneNumber.setText(R.string.illegal_phone);
            flag = false;
        }

        if(txtPhoneNumber.isEmpty()) {
            tvPhoneNumber.setBackgroundResource(R.drawable.red_error_style);
            errPhoneNumber.setText(R.string.this_is_req);
            flag = false;
        }

        if(txtDescription.isEmpty()) {
            tvDescription.setBackgroundResource(R.drawable.red_error_style);
            errDescription.setText(R.string.this_is_req);
            flag = false;
        }
        return flag;
    }
}