package com.findmybarber.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
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

import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class ButtonAdapter extends BaseAdapter
{
    private Context mContext;
    private String timeSlots[];
    private List<String> takenTimeSlots;
    private LayoutInflater inflter;

    public ButtonAdapter(Context context, List<String> takenTimeSlots) {
        this.mContext = context;
        this.takenTimeSlots = takenTimeSlots;
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
        if(takenTimeSlots.contains(button.getText().toString()+":00")) {
            button.setOnClickListener(view ->
                    button.setClickable(false));
            button.setAlpha(.5f);
            button.setEnabled(false);
        }

        button.setOnClickListener(view -> {
            SharedPreferences sharedPreferences;
            sharedPreferences =  mContext.getSharedPreferences("book", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
/*            if(sharedPreferences.getString("position",null)!=null)
            {
                buttonfi
                button.setBackgroundResource(timeSlots[position]);
            }*/
            editor.putString("timeSlot", button.getText().toString());
            editor.putString("position", String.valueOf(position));


            editor.apply();
            button.setBackgroundResource(R.drawable.selectedtimeslot);
        });
        return grid;

    }

}
