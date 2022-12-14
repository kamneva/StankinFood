package com.example.hackinhome2021_stankinfood.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hackinhome2021_stankinfood.R;
import com.example.hackinhome2021_stankinfood.interfaces.OnRecyclerViewClickListener;
import com.example.hackinhome2021_stankinfood.models.Restaurant;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RestaurantRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements Filterable, OnRecyclerViewClickListener {

    private final List<Restaurant> restaurantList;
    private final List<Restaurant> restaurantListFull = new ArrayList<>();
    private final OnRecyclerViewClickListener onRecyclerViewClickListener;

    public RestaurantRecyclerViewAdapter(List<Restaurant> restaurantList,
                                         OnRecyclerViewClickListener onRecyclerViewClickListener) {
        this.restaurantList = restaurantList;
        restaurantListFull.addAll(restaurantList);
        this.onRecyclerViewClickListener = onRecyclerViewClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_menu_restaurant, parent, false);

        return new ViewHolderRestaurant(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewHolderRestaurant viewHolderRestaurant = (ViewHolderRestaurant) holder;
        Restaurant currentRestaurant = restaurantList.get(position);

        String openingHoursString = new SimpleDateFormat("HH:mm", Locale.ENGLISH)
                .format(currentRestaurant.getOpeningHours());
        String closingHoursString = new SimpleDateFormat("HH:mm", Locale.ENGLISH)
                .format(currentRestaurant.getClosingHours());

        viewHolderRestaurant.textViewName.setText(currentRestaurant.getName());
        viewHolderRestaurant.textViewRealAddress.setText(currentRestaurant.getAddress());
        viewHolderRestaurant.textViewRealOpeningHours.setText(openingHoursString);
        viewHolderRestaurant.textViewRealClosingHours.setText(closingHoursString);

        viewHolderRestaurant.cardView.setOnClickListener(new MyOnClickListener(position));
    }

    @Override
    public int getItemCount() {
        return restaurantList.size();
    }


    @Override
    public Filter getFilter() {
        return restaurantFilter;
    }

    private final Filter restaurantFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Restaurant> restaurantsFiltered = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                restaurantsFiltered.addAll(restaurantListFull);
            } else {
                String searchQuery = constraint.toString().toLowerCase().trim();

                for (Restaurant restaurant : restaurantListFull) {
                    if (restaurant.getName().toLowerCase().contains(searchQuery)) {
                        restaurantsFiltered.add(restaurant);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = restaurantsFiltered;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            restaurantList.clear();
            restaurantList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };


    private class MyOnClickListener implements View.OnClickListener {
        int position;

        public MyOnClickListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            onRecyclerViewClickListener.onItemClick(v, position);
        }
    }


    @Override
    public void onItemClick(View view, int position) {

    }

    private static class ViewHolderRestaurant extends RecyclerView.ViewHolder {
        private final CardView cardView;
        private final TextView textViewName;
        private final TextView textViewRealAddress;
        private final TextView textViewRealOpeningHours;
        private final TextView textViewRealClosingHours;

        public ViewHolderRestaurant(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.cardView);
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewRealAddress = itemView.findViewById(R.id.textViewRealAddress);
            textViewRealOpeningHours = itemView.findViewById(R.id.textViewRealOpeningHours);
            textViewRealClosingHours = itemView.findViewById(R.id.textViewRealClosingHours);
        }
    }
}
