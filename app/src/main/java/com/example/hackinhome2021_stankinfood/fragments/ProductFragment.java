package com.example.hackinhome2021_stankinfood.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.hackinhome2021_stankinfood.R;
import com.example.hackinhome2021_stankinfood.activities.MainActivity;
import com.example.hackinhome2021_stankinfood.interfaces.OnBackPressedFragment;
import com.example.hackinhome2021_stankinfood.models.Product;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

public class ProductFragment extends Fragment implements
        View.OnClickListener,
        OnBackPressedFragment {
    private static final String PRODUCT = "product";

    private int savedProductsLeft;
    private Product product;

    private TextView textViewName;
    private ImageView imageViewProductImage;
    private ImageButton imageButtonLiked;
    private RatingBar ratingBar;
    private TextView textViewDescription;
    private Button buttonProductPrice;

    private TextView textViewSinglePrice;
    private TextView textViewTotalPrice;
    private TextView textViewRealSinglePrice;
    private TextView textViewRealTotalPrice;
    private TextView textViewProductsLeft;
    private TextView textViewRealProductsLeft;

    private ImageButton imageButtonMinus;
    private TextView textViewCount;
    private ImageButton imageButtonPlus;

    public ProductFragment() {
    }

    public static ProductFragment newInstance(Product product) {
        ProductFragment fragment = new ProductFragment();
        Bundle args = new Bundle();
        args.putParcelable(PRODUCT, product);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            product = getArguments().getParcelable(PRODUCT);
        }
        if (savedInstanceState != null) {
            product = savedInstanceState.getParcelable(PRODUCT);
        }
        savedProductsLeft = product.getProductsLeft() + product.getCountForOrder();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelable(PRODUCT, product);
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product, container, false);

        initImageView(view);
        initRatingBar(view);

        initTextViewName(view);
        initTextViewDescription(view);
        initTextViewSinglePrice(view);
        initTextViewTotalPrice(view);
        initTextViewProductsLeft(view);

        initButtonPrice(view);
        initTextViewCount(view);
        initImageButton(view);

        hideViewsByButtonClick(product.getCountForOrder() != 0);

        return view;
    }

    private void initButtonPrice(View view) {
        buttonProductPrice = view.findViewById(R.id.buttonProductPrice);
        String price = product.getPrice() + " " + view.getResources().getString(R.string.currency);
        buttonProductPrice.setText(price);

        buttonProductPrice.setOnClickListener(this);
    }

    private void initImageButton(View view) {
        imageButtonMinus = view.findViewById(R.id.imageButtonMinus);
        imageButtonPlus = view.findViewById(R.id.imageButtonPlus);
        imageButtonLiked = view.findViewById(R.id.imageButtonLiked);
        imageButtonLiked.setSelected(product.isLiked());

        imageButtonMinus.setOnClickListener(this);
        imageButtonPlus.setOnClickListener(this);
        imageButtonLiked.setOnClickListener(this);
    }

    private void initTextViewName(View view) {
        textViewName = view.findViewById(R.id.textViewName);
        textViewName.setText(product.getProductName());
    }

    private void initTextViewDescription(View view) {
        textViewDescription = view.findViewById(R.id.textViewDescription);
        textViewDescription.setText(product.getDescription());
    }

    private void initTextViewSinglePrice(View view) {
        String currency = getResources().getString(R.string.currency);
        String singlePriceString = product.getPrice() + " " + currency;

        textViewSinglePrice = view.findViewById(R.id.textViewSinglePrice);
        textViewRealSinglePrice = view.findViewById(R.id.textViewRealSinglePrice);
        textViewRealSinglePrice.setText(singlePriceString);
    }

    private void initTextViewProductsLeft(View view) {
        textViewProductsLeft = view.findViewById(R.id.textViewProductsLeft);
        textViewRealProductsLeft = view.findViewById(R.id.textViewRealProductsLeft);
        textViewRealProductsLeft.setText(String.valueOf(product.getProductsLeft()));
    }

    private void initTextViewCount(View view) {
        String countString = String.valueOf(product.getCountForOrder());

        textViewCount = view.findViewById(R.id.textViewCount);
        textViewCount.setText(countString);
    }

    private void initTextViewTotalPrice(View view) {
        String currency = getResources().getString(R.string.currency);
        String totalPriceString = product.getPrice() * product.getCountForOrder() + " " + currency;

        textViewTotalPrice = view.findViewById(R.id.textViewTotalPrice);
        textViewRealTotalPrice = view.findViewById(R.id.textViewRealTotalPrice);
        textViewRealTotalPrice.setText(totalPriceString);
    }

    private void initRatingBar(View view) {
        ratingBar = view.findViewById(R.id.ratingBar);
        ratingBar.setRating(product.getRating());
    }

    private void initImageView(View view) {
        imageViewProductImage = view.findViewById(R.id.imageViewPicture);
        if (product.getImageURL() != null) {
            Glide.with(getContext()).load(product.getImageURL()).into(imageViewProductImage);
        }
        if (product.getImageURL() != null) {
            Glide.with(getContext()).load(product.getImageURL()).into(imageViewProductImage);
        } else {
            imageViewProductImage.setImageDrawable(ContextCompat.getDrawable(getContext(),
                    R.drawable.ic_no_image_64dp));
        }
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.imageButtonLiked) {
            ((MainActivity) getActivity()).markProductAsLiked(product, !product.isLiked());
            product.setLiked(!product.isLiked());

            imageButtonLiked.setSelected(product.isLiked());
        } else {
            if (id == R.id.buttonProductPrice) {
                product.setCountForOrder(1);
                product.setViewType(MainActivity.MENU_PRODUCT_ACTIVE);
                textViewRealProductsLeft.setText(String.valueOf(savedProductsLeft - 1));
                hideViewsByButtonClick(true);
            } else if (id == R.id.imageButtonMinus) {
                if (product.getCountForOrder() - 1 == 0) {
                    product.setViewType(MainActivity.MENU_PRODUCT_INACTIVE);
                    hideViewsByButtonClick(false);
                } else product.setViewType(MainActivity.MENU_PRODUCT_ACTIVE);
                product.setCountForOrder(product.getCountForOrder() - 1);
                product.setProductsLeft(savedProductsLeft - product.getCountForOrder());
            } else if (id == R.id.imageButtonPlus) {
                if (product.getCountForOrder() + 1 <= savedProductsLeft) {
                    product.setCountForOrder(product.getCountForOrder() + 1);
                    product.setProductsLeft(savedProductsLeft - product.getCountForOrder());
                    product.setViewType(MainActivity.MENU_PRODUCT_ACTIVE);
                } else {
                    String noProductLeft = getResources().getString(R.string.no_product_left);
                    Snackbar.make(getView(), noProductLeft, BaseTransientBottomBar.LENGTH_SHORT).show();
                }
            }
            MainActivity.userOrder.addPosition(product);
        }
        textViewCount.setText(String.valueOf(product.getCountForOrder()));
        textViewRealTotalPrice.setText(getTotalPriceString());
        textViewRealProductsLeft.setText(String.valueOf(savedProductsLeft - product.getCountForOrder()));
    }

    private String getTotalPriceString() {
        String currency = getResources().getString(R.string.currency);
        return product.getCountForOrder() * product.getPrice() + " " + currency;
    }

    private void hideViewsByButtonClick(boolean hide) {
        if (hide) {
            buttonProductPrice.setVisibility(View.GONE);

            imageButtonMinus.setVisibility(View.VISIBLE);
            textViewCount.setVisibility(View.VISIBLE);
            imageButtonPlus.setVisibility(View.VISIBLE);

            textViewSinglePrice.setVisibility(View.VISIBLE);
            textViewRealSinglePrice.setVisibility(View.VISIBLE);

            textViewTotalPrice.setVisibility(View.VISIBLE);
            textViewRealTotalPrice.setVisibility(View.VISIBLE);

            textViewProductsLeft.setVisibility(View.VISIBLE);
            textViewRealProductsLeft.setVisibility(View.VISIBLE);
        } else {
            buttonProductPrice.setVisibility(View.VISIBLE);

            imageButtonMinus.setVisibility(View.GONE);
            textViewCount.setVisibility(View.GONE);
            imageButtonPlus.setVisibility(View.GONE);

            textViewTotalPrice.setVisibility(View.GONE);
            textViewRealTotalPrice.setVisibility(View.GONE);

            textViewSinglePrice.setVisibility(View.GONE);
            textViewRealSinglePrice.setVisibility(View.GONE);

            textViewProductsLeft.setVisibility(View.GONE);
            textViewRealProductsLeft.setVisibility(View.GONE);
        }
    }


    @Override
    public boolean onBackPressed() {
//        ((MainActivity) getActivity()).restoreCardViewClickListener();
        return false;
    }
}