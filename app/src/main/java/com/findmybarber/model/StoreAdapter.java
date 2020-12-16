package com.findmybarber.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.recyclerview.widget.RecyclerView;
import com.findmybarber.R;
import java.util.List;

public class StoreAdapter extends RecyclerView.Adapter<StoreAdapter.ViewHolder> {

    private  List<Store> mStores;
    private  Context mContext;
    public StoreAdapter(List<Store> stores, Context mContext) {
        this.mStores = stores;
        this.mContext = mContext;
    }
    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private View itemView;
        public TextView storeName;
        public TextView description;
        public Button detail_button;
        public ViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            storeName = itemView.findViewById(R.id.storename);
            description = itemView.findViewById(R.id.description);
            detail_button = itemView.findViewById(R.id.detail_button);
            detail_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(view.getContext(),
                            storeName.getText() +" | "
                                    + " Demo function", Toast.LENGTH_SHORT)
                            .show();
                }
            });
        }
    }
    // Create new views (invoked by the layout manager)
    @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View storeView =
                inflater.inflate(R.layout.store_item, parent, false);
        return new ViewHolder(storeView);
    }
    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        Store store = mStores.get(position);
        viewHolder.storeName.setText(store.getName());
        viewHolder.description.setText(store.getDescription());
        // Get element from your dataset at this position and replace the
        // contents of the view with that element
    }
    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mStores.size();
    }
}
