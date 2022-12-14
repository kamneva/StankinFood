package com.example.hackinhome2021_stankinfood.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.hackinhome2021_stankinfood.R;
import com.example.hackinhome2021_stankinfood.activities.MainActivity;
import com.example.hackinhome2021_stankinfood.interfaces.OnRecyclerViewClickListener;
import com.example.hackinhome2021_stankinfood.models.Product;

import java.util.ArrayList;
import java.util.List;

public class ProductRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements Filterable {

    private final List<Product> productList;
    private final List<Product> productListFull = new ArrayList<>();
    private final OnRecyclerViewClickListener onRecyclerViewClickListener;

    public ProductRecyclerViewAdapter(List<Product> productList,
                                      OnRecyclerViewClickListener onRecyclerViewClickListener) {
        this.productList = productList;
        productListFull.addAll(productList);
        this.onRecyclerViewClickListener = onRecyclerViewClickListener;
    }


    @Override
    public int getItemViewType(int position) {
        return productList.get(position).getViewType();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case MainActivity.MENU_HEADER:
                return new ViewHolderMenuHeader(layoutInflater.inflate(
                        R.layout.item_menu_header, parent, false));

            case MainActivity.MENU_PRODUCT_ACTIVE:
            case MainActivity.MENU_PRODUCT_INACTIVE:
                return new ViewHolderMenuProduct(layoutInflater.inflate(
                        R.layout.item_menu_product, parent, false));

            case MainActivity.ORDER_PRODUCT_ACTIVE:
            case MainActivity.ORDER_PRODUCT_INACTIVE:
                return new ViewHolderOrderProduct(layoutInflater.inflate(
                        R.layout.item_order_product, parent, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Product currentProduct = productList.get(position);
        MyOnClickListener myOnClickListener = new MyOnClickListener(holder.getAdapterPosition());

        switch (getItemViewType(position)) {
            case MainActivity.MENU_HEADER:
                setDataForViewHolderMenuHeader(
                        (ViewHolderMenuHeader) holder, currentProduct);
                break;
            case MainActivity.MENU_PRODUCT_INACTIVE:
                setDataForViewHolderMenuProductInactive(
                        (ViewHolderMenuProduct) holder, currentProduct, myOnClickListener);
                break;
            case MainActivity.MENU_PRODUCT_ACTIVE:
                setDataForViewHolderMenuProductActive(
                        (ViewHolderMenuProduct) holder, currentProduct, myOnClickListener);
                break;
            case MainActivity.ORDER_PRODUCT_INACTIVE:
                setDataForViewHolderOrderProductInactive(
                        (ViewHolderOrderProduct) holder, currentProduct);
                break;
            case MainActivity.ORDER_PRODUCT_ACTIVE:
                setDataForViewHolderOrderProductActive(
                        (ViewHolderOrderProduct) holder, currentProduct, myOnClickListener);
                break;
        }
    }


    private String getStringResourceSinglePrice(RecyclerView.ViewHolder viewHolder) {
        return viewHolder.itemView.getResources().getString(R.string.single_price);
    }

    private String getStringResourceTotalPrice(RecyclerView.ViewHolder viewHolder) {
        return viewHolder.itemView.getResources().getString(R.string.total_price);
    }

    private String getStringResourceProductsLeft(RecyclerView.ViewHolder viewHolder) {
        return viewHolder.itemView.getResources().getString(R.string.products_left);
    }

    private String getStringResourceCurrency(RecyclerView.ViewHolder viewHolder) {
        return viewHolder.itemView.getResources().getString(R.string.currency);
    }

    private Drawable getNoImageDrawable(RecyclerView.ViewHolder holder) {
        return ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.ic_no_image_64dp);
    }


    private String getStringSinglePriceWithNumber(RecyclerView.ViewHolder viewHolder,
                                                  int singlePrice) {
        return getStringResourceSinglePrice(viewHolder) +
                " " + singlePrice + " " +
                getStringResourceCurrency(viewHolder);
    }

    private String getStringTotalPriceWithNumber(RecyclerView.ViewHolder viewHolder,
                                                 int singlePrice, int countForOrder) {
        return getStringResourceTotalPrice(viewHolder) +
                " " + singlePrice * countForOrder + " " +
                getStringResourceCurrency(viewHolder);
    }

    private String getStringProductsLeftWithNumber(RecyclerView.ViewHolder viewHolder,
                                                   int productsLeft) {
        return getStringResourceProductsLeft(viewHolder) + " " + productsLeft;
    }


    private String getStringForDescriptionAfterClick(RecyclerView.ViewHolder viewHolder,
                                                     int singlePrice, int countForOrder,
                                                     int productsLeft) {
        return getStringSinglePriceWithNumber(viewHolder, singlePrice) +
                "\n\n" +
                getStringTotalPriceWithNumber(viewHolder, singlePrice, countForOrder) +
                "\n\n" +
                getStringProductsLeftWithNumber(viewHolder, productsLeft);
    }


    private void setDataForViewHolderMenuHeader(ViewHolderMenuHeader viewHolderMenuHeader,
                                                Product product) {
        viewHolderMenuHeader.textViewName.setText(product.getCategoryName());
    }

    private void setDataForViewHolderMenuProductInactive(
            ViewHolderMenuProduct viewHolderMenuProduct,
            Product product, MyOnClickListener myOnClickListener) {
        viewHolderMenuProduct.imageButtonLiked.setSelected(product.isLiked());
        if (product.getImageURL() != null) {
            Glide.with(viewHolderMenuProduct.itemView.getContext()).load(product.getImageURL())
                    .into(viewHolderMenuProduct.imageViewPicture);
        } else {
            viewHolderMenuProduct.imageViewPicture.setImageDrawable(
                    getNoImageDrawable(viewHolderMenuProduct));
        }
        viewHolderMenuProduct.textViewName.setText(product.getProductName());
        viewHolderMenuProduct.ratingBar.setRating(product.getRating());

        String singlePrice = product.getPrice() + " " +
                getStringResourceCurrency(viewHolderMenuProduct);
        viewHolderMenuProduct.textViewDescription.setText(product.getDescription());
        viewHolderMenuProduct.textViewDescription.setGravity(Gravity.START);
        viewHolderMenuProduct.buttonPrice.setText(singlePrice);

        viewHolderMenuProduct.buttonPrice.setVisibility(View.VISIBLE);
        viewHolderMenuProduct.imageButtonMinus.setVisibility(View.GONE);
        viewHolderMenuProduct.textViewCount.setVisibility(View.GONE);
        viewHolderMenuProduct.imageButtonPlus.setVisibility(View.GONE);

        viewHolderMenuProduct.cardView.setOnClickListener(myOnClickListener);
        viewHolderMenuProduct.imageButtonLiked.setOnClickListener(myOnClickListener);
        viewHolderMenuProduct.buttonPrice.setOnClickListener(myOnClickListener);
    }

    private void setDataForViewHolderMenuProductActive(
            ViewHolderMenuProduct viewHolderMenuProduct,
            Product product, MyOnClickListener myOnClickListener) {
        viewHolderMenuProduct.imageButtonLiked.setSelected(product.isLiked());
        if (product.getImageURL() != null) {
            Glide.with(viewHolderMenuProduct.itemView.getContext()).load(product.getImageURL())
                    .into(viewHolderMenuProduct.imageViewPicture);
        } else {
            viewHolderMenuProduct.imageViewPicture.setImageDrawable(
                    getNoImageDrawable(viewHolderMenuProduct));
        }
        viewHolderMenuProduct.textViewName.setText(product.getProductName());
        viewHolderMenuProduct.ratingBar.setRating(product.getRating());

        viewHolderMenuProduct.textViewDescription.setText(getStringForDescriptionAfterClick(
                viewHolderMenuProduct, product.getPrice(), product.getCountForOrder(), product.getProductsLeft()));
        viewHolderMenuProduct.textViewDescription.setGravity(Gravity.CENTER_VERTICAL | Gravity.START);
        viewHolderMenuProduct.textViewCount.setText(String.valueOf(product.getCountForOrder()));

        viewHolderMenuProduct.buttonPrice.setVisibility(View.GONE);
        viewHolderMenuProduct.imageButtonMinus.setVisibility(View.VISIBLE);
        viewHolderMenuProduct.textViewCount.setVisibility(View.VISIBLE);
        viewHolderMenuProduct.imageButtonPlus.setVisibility(View.VISIBLE);

        viewHolderMenuProduct.cardView.setOnClickListener(myOnClickListener);
        viewHolderMenuProduct.imageButtonLiked.setOnClickListener(myOnClickListener);
        viewHolderMenuProduct.imageButtonMinus.setOnClickListener(myOnClickListener);
        viewHolderMenuProduct.imageButtonPlus.setOnClickListener(myOnClickListener);
    }

    private void setDataForViewHolderOrderProductInactive(
            ViewHolderOrderProduct viewHolderOrderProduct,
            Product product) {
        if (product.getImageURL() != null) {
            Glide.with(viewHolderOrderProduct.itemView.getContext()).load(product.getImageURL())
                    .into(viewHolderOrderProduct.imageViewPicture);
        } else {
            viewHolderOrderProduct.imageViewPicture.setImageDrawable(
                    getNoImageDrawable(viewHolderOrderProduct));
        }
        viewHolderOrderProduct.textViewName.setText(product.getProductName());
        String totalPrice = product.getPrice() * product.getCountForOrder() + " " +
                getStringResourceCurrency(viewHolderOrderProduct);
        viewHolderOrderProduct.textViewRealTotalPrice.setText(totalPrice);
        viewHolderOrderProduct.textViewCount.setText(String.valueOf(product.getCountForOrder()));

        viewHolderOrderProduct.imageButtonPlus.setVisibility(View.GONE);
        viewHolderOrderProduct.imageButtonMinus.setVisibility(View.GONE);
    }

    private void setDataForViewHolderOrderProductActive(
            ViewHolderOrderProduct viewHolderOrderProduct,
            Product product, MyOnClickListener myOnClickListener) {
        if (product.getImageURL() != null) {
            Glide.with(viewHolderOrderProduct.itemView.getContext()).load(product.getImageURL())
                    .into(viewHolderOrderProduct.imageViewPicture);
        } else {
            viewHolderOrderProduct.imageViewPicture.setImageDrawable(
                    getNoImageDrawable(viewHolderOrderProduct));
        }
        viewHolderOrderProduct.textViewName.setText(product.getProductName());
        String totalPrice = product.getPrice() * product.getCountForOrder() + " " +
                getStringResourceCurrency(viewHolderOrderProduct);
        viewHolderOrderProduct.textViewRealTotalPrice.setText(totalPrice);
        viewHolderOrderProduct.textViewCount.setText(String.valueOf(product.getCountForOrder()));

        viewHolderOrderProduct.imageButtonPlus.setVisibility(View.VISIBLE);
        viewHolderOrderProduct.imageButtonMinus.setVisibility(View.VISIBLE);

        viewHolderOrderProduct.imageButtonMinus.setOnClickListener(myOnClickListener);
        viewHolderOrderProduct.imageButtonPlus.setOnClickListener(myOnClickListener);
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }


    @Override
    public Filter getFilter() {
        return productFilter;
    }

    private final Filter productFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Product> filteredProductList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredProductList.addAll(productListFull);
            } else {
                String searchQuery = constraint.toString().toLowerCase().trim();

                for (Product product : productListFull) {
                    if (product.getViewType() != MainActivity.MENU_HEADER &&
                            product.getProductName().toLowerCase().contains(searchQuery)) {
                        filteredProductList.add(product);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredProductList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            productList.clear();
            productList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };


    private class MyOnClickListener implements View.OnClickListener {
        private final int position;

        MyOnClickListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            onRecyclerViewClickListener.onItemClick(v, position);
        }
    }


    private static class ViewHolderMenuHeader extends RecyclerView.ViewHolder {
        private final TextView textViewName;

        public ViewHolderMenuHeader(@NonNull View itemView) {
            super(itemView);

            textViewName = itemView.findViewById(R.id.textViewName);
        }
    }

    private static class ViewHolderMenuProduct extends RecyclerView.ViewHolder {
        private final CardView cardView;
        private final ImageButton imageButtonLiked;
        private final ImageView imageViewPicture;
        private final TextView textViewName;
        private final RatingBar ratingBar;
        private final TextView textViewDescription;
        private final Button buttonPrice;
        private final ImageButton imageButtonMinus;
        private final TextView textViewCount;
        private final ImageButton imageButtonPlus;

        public ViewHolderMenuProduct(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.cardView);
            imageButtonLiked = itemView.findViewById(R.id.imageButtonLiked);
            imageViewPicture = itemView.findViewById(R.id.imageViewPicture);
            textViewName = itemView.findViewById(R.id.textViewName);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            textViewDescription = itemView.findViewById(R.id.textViewDescription);
            buttonPrice = itemView.findViewById(R.id.buttonPrice);
            imageButtonMinus = itemView.findViewById(R.id.imageButtonMinus);
            textViewCount = itemView.findViewById(R.id.textViewCount);
            imageButtonPlus = itemView.findViewById(R.id.imageButtonPlus);
        }
    }

    private static class ViewHolderOrderProduct extends RecyclerView.ViewHolder {
        private final CardView cardView;
        private final ImageView imageViewPicture;
        private final TextView textViewName;
        private final TextView textViewTotalPrice;
        private final TextView textViewRealTotalPrice;
        private final ImageButton imageButtonMinus;
        private final TextView textViewCount;
        private final ImageButton imageButtonPlus;

        public ViewHolderOrderProduct(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.cardView);
            imageViewPicture = itemView.findViewById(R.id.imageViewPicture);
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewTotalPrice = itemView.findViewById(R.id.textViewTotalPrice);
            textViewRealTotalPrice = itemView.findViewById(R.id.textViewRealTotalPrice);
            imageButtonMinus = itemView.findViewById(R.id.imageButtonMinus);
            textViewCount = itemView.findViewById(R.id.textViewCount);
            imageButtonPlus = itemView.findViewById(R.id.imageButtonPlus);
        }
    }
}
