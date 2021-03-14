package com.findmybarber.model.adapter;

import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.findmybarber.R;
import com.findmybarber.controller.asynctask.GetNextSlot;
import com.findmybarber.model.Book;
import com.findmybarber.model.Store;
import com.findmybarber.view.activities.Login;
import com.findmybarber.view.activities.MainActivity;

import java.util.Calendar;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import static android.content.Context.MODE_PRIVATE;
import static com.findmybarber.view.activities.MainActivity.bookingsList;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.ViewHolder> {

    private final List<Book> mBookings;
    private final FragmentActivity mFragmentActivity;
    private String time;
    private BookAdapter adapter;
    private boolean flag = false;
    public BookAdapter(List<Book> mBookings, FragmentActivity mFragmentActivity, BookAdapter adapter) {
        this.mBookings = mBookings;
        this.mFragmentActivity = mFragmentActivity;
        this.adapter = adapter;
        if (adapter != null)
            flag = true;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final View itemView;
        public TextView storeName;
        public TextView description;
        private final Context context;

        public ViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            storeName = itemView.findViewById(R.id.storename);
            description = itemView.findViewById(R.id.description);
            this.context = itemView.getContext();
        }
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View bookingView = inflater.inflate(R.layout.book_item, parent, false);
        return new ViewHolder(bookingView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Book book = mBookings.get(position);
        Store store = getStoreByBookStoreID(book);

        holder.storeName.setText(store.getName());
        holder.description.setText("Meeting at " + book.getDate() + ", " + book.getTime());
        Button bookAgain = holder.itemView.findViewById(R.id.bookAgain);
        Dialog dialog = new Dialog(holder.context, R.style.PauseDialog);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setContentView(R.layout.book_again_dialog);
        TextView textView = dialog.findViewById(R.id.suggestedTimeSlot);
        TextView anotherDate = dialog.findViewById(R.id.another_date);
        Button bookNow = dialog.findViewById(R.id.bt_book);
        ImageView imageView = dialog.findViewById(R.id.img_close1);
        bookAgain.setOnClickListener(view -> {
            GetNextSlot getNextSlot = new GetNextSlot(holder.context, store.getID());
            try {
                time = getNextSlot.execute().get();
                textView.setText("Would you like to book an appointment in " + store.getName() + " at " + time + "?");
                dialog.show();
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        });

        anotherDate.setOnClickListener(view -> {
            MainActivity mainActivity = (MainActivity) mFragmentActivity;
            SharedPreferences sharedPreferences;
            sharedPreferences = mFragmentActivity.getSharedPreferences("store", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("storeName", store.getName());
            editor.putString("storeID", store.getID());
            editor.apply();
            dialog.dismiss();
            mainActivity.loadStoreDetails();
        });

        imageView.setOnClickListener(view -> {
            dialog.dismiss();
        });

        bookNow.setOnClickListener(view -> {
            String date = time.split(",", 2)[0];
            String time = this.time.split(",", 2)[1];
            String minutes = time.split(":", 2)[1];
            String hours = time.split(":", 2)[0];
            StringBuilder stringBuilder = new StringBuilder(hours);
            stringBuilder.deleteCharAt(0);
            String year = date.split("/", 3)[2];
            String dayOfMonth = date.split("/", 3)[1];
            String month = date.split("/", 3)[0];
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(stringBuilder.toString()));
            calendar.set(Calendar.MINUTE, Integer.parseInt(minutes));
            calendar.set(Calendar.SECOND, 0);
            SharedPreferences sharedPreferences;
            sharedPreferences = mFragmentActivity.getSharedPreferences("CurrentUserPref", MODE_PRIVATE);
            UUID id = UUID.randomUUID();
            calendar.set(Integer.parseInt(year), Integer.parseInt(month) - 1, Integer.parseInt(dayOfMonth));
            String userEmail = Login.adminsList.stream().filter(user -> user.getStoreID().equals(store.getID())).findFirst().get().getUserEmail();
            Book book1 = new Book(id.toString(), store.getID(), sharedPreferences.getString("KeyUser", null), userEmail, calendar);
            bookingsList.add(book1);
            MainActivity.postBookAppointment(holder.context, book1);
            MainActivity.appointmentsForUserList.add(book1);
            createNotificationChannel(holder.context);
            setNotification(time,holder.context);
//            Toast.makeText(holder.context, "New meeting has been created at: " + time, Toast.LENGTH_LONG).show();
            if(flag)
                adapter.notifyItemInserted(MainActivity.appointmentsForUserList.size());
            else
                notifyItemInserted(getItemCount());
            dialog.dismiss();

        });
    }

    @Override
    public int getItemCount() {
        return mBookings.size();
    }

    public Store getStoreByBookStoreID(Book book) {
        for (Store st: Login.dbStoresList) {
            if(book.getStoreID().equals(st.getID())) {
                return st;
            }
        }
        return null;
    }

    public static void setNotification(String time,Context context){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,"FindMyBarberA")
        .setSmallIcon(R.drawable.icon_add_store)
        .setContentTitle("FindMyBarber")
        .setContentText("New meeting has been created at: " + time)
        .setPriority(NotificationCompat.PRIORITY_HIGH);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(100,builder.build());
    }
    public static void createNotificationChannel(Context context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "FindMyBarber Channel";
            String description = "Channel for FindMyBarber notifications";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("FindMyBarberA", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
