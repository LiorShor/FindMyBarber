package com.findmybarber.model.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import com.findmybarber.R;
import com.findmybarber.controller.asynctask.GetStorePhone;
import com.findmybarber.model.Store;
import com.findmybarber.view.activities.Login;
import com.findmybarber.view.activities.MainActivity;

import java.util.List;
import java.util.concurrent.ExecutionException;

import static android.content.Context.MODE_PRIVATE;

public class StoreAdapter extends RecyclerView.Adapter<StoreAdapter.ViewHolder> {

    private final List<Store> mStores;
    private final FragmentActivity mFragmentActivity;
    private final Dialog callDialog;
    private final Context context;
    public StoreAdapter(FragmentActivity fragmentActivity, List<Store> stores,Context context) {
        this.mStores = stores;
        this.context = context;
        this.mFragmentActivity = fragmentActivity;
        callDialog = new Dialog(context,R.style.PauseDialog);
        callDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        callDialog.setContentView(R.layout.calldialog);

    }
    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final View itemView;
        public TextView storeName;
        public TextView description;
        public ViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            storeName = itemView.findViewById(R.id.storename);
            description = itemView.findViewById(R.id.description);
        }
    }
    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View storeView = inflater.inflate(R.layout.store_item, parent, false);
        return new ViewHolder(storeView);
    }
    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        Store store = mStores.get(position);
        viewHolder.storeName.setText(store.getName());
        viewHolder.description.setText(store.getDescription());
        Button bookNow = viewHolder.itemView.findViewById(R.id.bookNow);

        bookNow.setOnClickListener(view -> {

            if (Login.dbStoresList.contains(mStores.get(viewHolder.getAdapterPosition()))) {
                MainActivity mainActivity = (MainActivity) mFragmentActivity;
                mainActivity.loadStoreDetails();
                int pos = viewHolder.getAdapterPosition();
                mainActivity.getBookingList(mStores.get(pos).getID());
                SharedPreferences sharedPreferences;
                sharedPreferences = mFragmentActivity.getSharedPreferences("store", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("storeName", mStores.get(pos).getName());
                editor.putString("storeID", mStores.get(pos).getID());
                editor.apply();
            } else {

                GetStorePhone getStorePhone = new GetStorePhone(mStores.get(viewHolder.getAdapterPosition()).getName(),mFragmentActivity.getApplicationContext());
                try {
                    String phoneNumber = getStorePhone.execute().get();

                    callDialog.show();
                    Button callNow = callDialog.findViewById(R.id.call);
                    callNow.setOnClickListener(view1 -> {
                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        callIntent.setData(Uri.parse("tel:" + phoneNumber));//change the number
                        mFragmentActivity.getApplicationContext().startActivity(callIntent);
                    });
                    Button cancel = callDialog.findViewById(R.id.cancel);
                    cancel.setOnClickListener(view1 -> callDialog.dismiss());

                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mStores.size();
    }
}
