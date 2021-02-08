package com.findmybarber.model;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import com.findmybarber.R;
import com.findmybarber.view.activities.Login;
import com.findmybarber.view.activities.MainActivity;
import com.findmybarber.view.fragments.ActionMe;

import java.util.List;
import java.util.concurrent.ExecutionException;

import static android.content.Context.MODE_PRIVATE;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.ViewHolder> {

    private final List<Book> mBookings;
    private final FragmentActivity mFragmentActivity;

    public BookAdapter(List<Book> mBookings, FragmentActivity mFragmentActivity) {
        this.mBookings = mBookings;
        this.mFragmentActivity = mFragmentActivity;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final View itemView;
        public TextView storeName;
        public TextView description;
        private Context context;
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
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Book book = mBookings.get(position);
        Store store = getStoreByBookStoreID(book);

        holder.storeName.setText(store.getName());
        holder.description.setText("Meeting at " + book.getDate() + ", " + book.getTime());
        Button bookAgain = holder.itemView.findViewById(R.id.bookAgain);
        bookAgain.setOnClickListener(view -> {
            GetNextSlot getNextSlot= new GetNextSlot(holder.context, store.getID());
            try {
                String time = getNextSlot.execute().get();
                Dialog dialog = new Dialog(holder.context,R.style.PauseDialog);
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                dialog.setContentView(R.layout.book_again_dialog);
                TextView textView = holder.itemView.findViewById(R.id.suggestedTimeSlot);
                textView.setText("Would you like to book an appointment in "+ store.getName() + " at" + time);
                dialog.show();
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
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

}
