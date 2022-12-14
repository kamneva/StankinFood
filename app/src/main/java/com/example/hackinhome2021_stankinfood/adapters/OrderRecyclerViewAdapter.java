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
import com.example.hackinhome2021_stankinfood.models.Order;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class OrderRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements Filterable, OnRecyclerViewClickListener {

    private final List<Order> orderList;
    private final List<Order> orderListFull = new ArrayList<>();
    private final OnRecyclerViewClickListener onRecyclerViewClickListener;

    public OrderRecyclerViewAdapter(List<Order> orderList,
                                    OnRecyclerViewClickListener onRecyclerViewClickListener) {
        this.orderList = orderList;
        orderListFull.addAll(orderList);
        this.onRecyclerViewClickListener = onRecyclerViewClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_menu_order, parent, false);

        return new ViewHolderOrder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewHolderOrder viewHolderOrder = (ViewHolderOrder) holder;
        Order currentOrder = orderList.get(position);

        if (currentOrder.getOrderId() != null) {
            viewHolderOrder.textViewOrderId.setText(currentOrder.getOrderId());
            String pickupTimeString = new SimpleDateFormat("HH:mm", Locale.ENGLISH)
                    .format(currentOrder.getPickupTime());
            viewHolderOrder.textViewRealPickupTime.setText(pickupTimeString);
        } else {
            viewHolderOrder.textViewOrderId.setText("");
            viewHolderOrder.textViewName.setText(currentOrder.getName());
            viewHolderOrder.textViewRealPickupTime.setText("");
        }

        viewHolderOrder.cardView.setOnClickListener(new MyOnClickListener(position));
    }


    @Override
    public int getItemCount() {
        return orderList.size();
    }

    private class MyOnClickListener implements View.OnClickListener {
        private final int position;

        public MyOnClickListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            onRecyclerViewClickListener.onItemClick(v, position);
        }
    }


    @Override
    public Filter getFilter() {
        return orderFilter;
    }

    private final Filter orderFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Order> orderFiltered = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                orderFiltered.addAll(orderFiltered);
            } else {
                String searchQuery = constraint.toString().toLowerCase().trim();

                for (Order order : orderFiltered) {
                    if (order.getName().toLowerCase().contains(searchQuery) ||
                            order.getOrderId().toLowerCase().contains(searchQuery)) {
                        orderFiltered.add(order);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = orderFiltered;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            orderList.clear();
            orderList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };


    @Override
    public void onItemClick(View view, int position) {

    }


    private static class ViewHolderOrder extends RecyclerView.ViewHolder {
        private final CardView cardView;
        private final TextView textViewOrderId;
        private final TextView textViewName;
        private final TextView textViewRealPickupTime;

        public ViewHolderOrder(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.cardView);
            textViewOrderId = itemView.findViewById(R.id.textViewOrderId);
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewRealPickupTime = itemView.findViewById(R.id.textViewRealPickupTime);
        }
    }
}
