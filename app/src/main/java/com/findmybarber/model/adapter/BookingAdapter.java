package com.findmybarber.model.adapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.findmybarber.R;
import com.findmybarber.model.Book;
import com.findmybarber.model.Customer;
import com.findmybarber.model.User;
import com.findmybarber.view.activities.Login;
import com.findmybarber.view.fragments.AdminManagement;
import org.json.JSONException;
import org.json.JSONObject;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.ViewHolder> {
    private final SimpleDateFormat dateFormatForDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private final SimpleDateFormat dateFormatForTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
    private final List<String> mBookString;
    private final List<Book> mBook;
    private final Context context;
    private Dialog modifyBookingDialog;
    private Customer user;
    private final ItemCallback itemCallback;
    public BookingAdapter(List<String> mBookString,List<Book> books,Context context,ItemCallback itemCallback) {
        this.mBookString = mBookString;
        this.mBook = books;
        this.context = context;
        this.itemCallback = itemCallback;
    }
    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final View itemView;
        public TextView bookingInfo;
        public ViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            this.bookingInfo = itemView.findViewById(R.id.text);
        }
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View bookView = inflater.inflate(R.layout.event_item, parent, false);
        modifyBookingDialog = new Dialog(context,R.style.PauseDialog);

        modifyBookingDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        return new ViewHolder(bookView);

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        String book = mBookString.get(viewHolder.getAdapterPosition());
        Button editButton = viewHolder.itemView.findViewById(R.id.editButton);
        viewHolder.bookingInfo.setText(book);
        editButton.setOnClickListener(view -> {
            int pos = viewHolder.getAdapterPosition();
            Book modifyBook = mBook.get(pos);
            for (User customer: Login.customersList) {
                if(customer.getUserEmail().equals(modifyBook.getEmailClient())) {
                    user = (Customer) customer;
                }
            }
            modifyBookingDialog.setContentView(R.layout.edit_meeting);
            modifyBookingDialog.show();
            modifyBookingDialog.setCanceledOnTouchOutside(true);
            ImageView dismiss = modifyBookingDialog.findViewById(R.id.dismiss);
            TextView bookingWith = modifyBookingDialog.findViewById(R.id.meetingWith);
            EditText timeEditText = modifyBookingDialog.findViewById(R.id.timeEditText);
            timeEditText.setText(modifyBook.getTime().toString());
            setContactName();
            setContactPhone();
            bookingWith.setText("Edit Meeting with " + user.getUserName());
            EditText dateEditText = modifyBookingDialog.findViewById(R.id.dateEditText);
            dateEditText.setText(modifyBook.getDate());
            dismiss.setOnClickListener(view1 -> modifyBookingDialog.dismiss());
            saveChanges(modifyBook,dateEditText,timeEditText,viewHolder.getAdapterPosition());
        });
        Button deleteButton =  viewHolder.itemView.findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(view1 -> {
            Book modifyBook = mBook.get(viewHolder.getAdapterPosition());
            AdminManagement.bookingList.remove(modifyBook);
            mBook.remove(viewHolder.getAdapterPosition());
            mBookString.remove(viewHolder.getAdapterPosition());
            RemoveFromDBPost(context,modifyBook);
            notifyItemRemoved(viewHolder.getAdapterPosition());
            itemCallback.updateCompactCalendarView();

        });
        Date dateClicked = null;
        Calendar todaysDate = Calendar.getInstance();
        todaysDate.set(Calendar.HOUR_OF_DAY, 0);
        todaysDate.set(Calendar.MINUTE, 0);
        todaysDate.set(Calendar.SECOND, 0);
        todaysDate.set(Calendar.MILLISECOND, 0);
        try {
            dateClicked = dateFormatForDate.parse(mBook.get(0).getDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if(dateClicked.before(todaysDate.getTime())) {
            editButton.setVisibility(View.INVISIBLE);
            deleteButton.setVisibility(View.INVISIBLE);
        }
        else{
            editButton.setVisibility(View.VISIBLE);
            deleteButton.setVisibility(View.VISIBLE);
        }

    }

    @SuppressLint("SetTextI18n")
    private void setContactName() {
        TextView contactName = modifyBookingDialog.findViewById(R.id.contactName);
        contactName.setText(user.getUserName()+ " "+ user.getUserSurname());
    }

    @Override
    public int getItemCount() {
        return mBookString.size();
    }

    public static void RemoveFromDBPost(Context context, Book book){
        String postUrl = "http://192.168.1.27:45455/api/Book/removeAppointment";
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        JSONObject postData = new JSONObject();
        try {
            postData.put("ID",book.getID());
            postData.put("StoreID",book.getStoreID());
            postData.put("EmailClient",book.getEmailClient());
            postData.put("EmailStore",book.getEmailStore());
            postData.put("Date",book.getDate());
            postData.put("Time",book.getTime());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, postUrl, postData, response -> {}, Throwable::printStackTrace);
        requestQueue.add(jsonObjectRequest);
    }
    public static void EditDBPost(Context context, Book book){
        String postUrl = "http://192.168.1.27:45455/api/Book/editBookingAppointment";
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        JSONObject postData = new JSONObject();
        try {
            postData.put("ID",book.getID());
            postData.put("StoreID",book.getStoreID());
            postData.put("EmailClient",book.getEmailClient());
            postData.put("EmailStore",book.getEmailStore());
            postData.put("Date",book.getDate());
            postData.put("Time",book.getTime());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, postUrl, postData, response -> {}, Throwable::printStackTrace);
        requestQueue.add(jsonObjectRequest);
    }

    public interface ItemCallback {
        void updateCompactCalendarView();
        void updateList();
    }

    public void saveChanges(Book modifyBook,TextView dateEditText,TextView timeEditText,int position){
        Button saveChanges = modifyBookingDialog.findViewById(R.id.saveChanges);
        saveChanges.setOnClickListener(view1 -> {
            modifyBook.setDate(dateEditText.getText().toString());
            String time = timeEditText.getText().toString();
            Calendar calendar = Calendar.getInstance();
            String hour = time.split(":",2)[0];
            String minute = time.split(":",3)[1];
            calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hour));
            calendar.set(Calendar.MINUTE, Integer.parseInt(minute));
            calendar.set(Calendar.SECOND, 0);
            String s = dateFormatForTime.format(calendar.getTimeInMillis());
            try {
                Time time1 = new Time(dateFormatForTime.parse(s).getTime());
                modifyBook.setTime(time1);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            EditDBPost(context,modifyBook);
            modifyBookingDialog.dismiss();
            itemCallback.updateCompactCalendarView();
            itemCallback.updateList();
            mBook.clear();
            mBookString.clear();
            notifyItemChanged(position);
        });
    }
    public void setContactPhone()
    {
        TextView contactPhone = modifyBookingDialog.findViewById(R.id.phoneNumber);
        contactPhone.setText(user.getUserPhoneNumber());
        contactPhone.setOnClickListener(view1 -> {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + user.getUserPhoneNumber()));//change the number
            context.startActivity(callIntent);
        });
    }

}


