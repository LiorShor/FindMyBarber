package com.findmybarber.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.findmybarber.R;
import com.findmybarber.view.activities.Login;
import com.findmybarber.view.activities.MainActivity;
import com.findmybarber.view.fragments.StoreDetails;

import static android.content.Context.MODE_PRIVATE;

public class ButtonAdapter extends BaseAdapter
{
    private Context mContext;
//    private int btn_id;
//    private int total_btns = 4;
    private String timeSlots[];
    LayoutInflater inflter;

    public ButtonAdapter(Context context) {
        this.mContext = context;
        this.timeSlots = new String[36];
        inflter = (LayoutInflater.from(context));

        int k = 0;
        for(int i = 10; i < 19; i++)
        {
            for (int j = 0; j < 4 ; j++) {
                if(j == 0)
                    timeSlots[k++] = i + ":" + "00";
                else
                    timeSlots[k++] = i + ":" + j * 15;
            }
        }
    }

    @Override
    public int getCount() {
        return timeSlots.length;
    }

    @Override
    public Object getItem(int i) {
        return timeSlots[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View grid;

        if(convertView == null){
            grid = new View(mContext);
            grid = inflter.inflate(R.layout.timeslot, parent, false);
        }else{
            grid = (View)convertView;
        }

        Button button = grid.findViewById(R.id.button); // get the reference of ImageView
        button.setText(timeSlots[position]);


//        button.setOnClickListener(view -> {
//            SharedPreferences sharedPreferences;
//            sharedPreferences =  mContext.getSharedPreferences("button", MODE_PRIVATE);
//            SharedPreferences.Editor editor = sharedPreferences.edit();
//            editor.putString("timeSlot", button.getText().toString());
//            editor.apply();
//        });
//
//        button.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View v)
//            {
//                SharedPreferences sharedPreferences;
//                sharedPreferences =  mContext.getSharedPreferences("button", MODE_PRIVATE);
//                SharedPreferences.Editor editor = sharedPreferences.edit();
//                editor.putString("timeSlot", button.getText().toString());
//                editor.apply();
//            }
//        });

//        Button btn;
//
//        if (view == null) {
//            btn = new Button(mContext);
//            btn.setText("Button " + (++btn_id));
//        } else {
//            btn = (Button) view;
//        }
//
//        btn.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View v)
//            {
//                Toast.makeText(v.getContext(), "Button #" + (i + 1), Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        return btn;
        return grid;

    }

}
