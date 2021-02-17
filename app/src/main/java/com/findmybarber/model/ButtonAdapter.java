package com.findmybarber.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

import com.findmybarber.R;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class ButtonAdapter extends BaseAdapter
{
    private final Context mContext;
    private final String[] timeSlots;

    private final List<String> takenTimeSlots;
    private final LayoutInflater inflater;
    private static View prevView = null;

    public ButtonAdapter(Context context, List<String> takenTimeSlots) {
        this.mContext = context;
        this.takenTimeSlots = takenTimeSlots;
        this.timeSlots = new String[36];
        inflater = (LayoutInflater.from(context));

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
            grid = inflater.inflate(R.layout.timeslot, parent, false);
            Button button = grid.findViewById(R.id.button); // get the reference of ImageView
            button.setTextColor(Color.WHITE);
            button.setText(timeSlots[position]);
            if(takenTimeSlots.contains(button.getText().toString()+":00"))
            {
                button.setClickable(false);
                button.setAlpha(.5f);
                button.setEnabled(false);
            }

            button.setOnClickListener(view -> {
                SharedPreferences sharedPreferences;
                sharedPreferences =  mContext.getSharedPreferences("book", MODE_PRIVATE);
                if(prevView!= null)
                {
                    Button button1 = prevView.findViewById(R.id.button);
                    button1.setBackgroundResource(R.drawable.timeslot);
                    button1.setTextColor(Color.WHITE);
                }
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("timeSlot", button.getText().toString());
                prevView = view;
                editor.apply();
                button.setBackgroundResource(R.drawable.selectedtimeslot);
                button.setTextColor(Color.RED);
            });

        }else{
            grid = (View)convertView;
        }

        return grid;

    }

}
