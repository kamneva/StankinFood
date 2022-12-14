package com.example.hackinhome2021_stankinfood.fragments;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hackinhome2021_stankinfood.R;
import com.example.hackinhome2021_stankinfood.adapters.ProductRecyclerViewAdapter;
import com.example.hackinhome2021_stankinfood.interfaces.OnBackPressedFragment;
import com.example.hackinhome2021_stankinfood.interfaces.OnRecyclerViewClickListener;
import com.example.hackinhome2021_stankinfood.models.Order;
import com.example.hackinhome2021_stankinfood.models.Product;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.text.SimpleDateFormat;
import java.util.Locale;


public class OrderFragment extends Fragment implements
        OnRecyclerViewClickListener,
        OnBackPressedFragment {
    private static final String IS_ORDER = "isOrder";
    private static final String ORDER = "order";

    private boolean isOrder; //true- кассир, false - клиент
    private Order order;
    private ImageView imageViewQR;
    private TextView textViewRealPickupTime;
    private Button buttonCloseOrder;

    public OrderFragment() {
    }

    public static OrderFragment newInstance(boolean isOrder, Order order) {
        OrderFragment fragment = new OrderFragment();
        Bundle args = new Bundle();
        args.putBoolean(IS_ORDER, isOrder);
        args.putParcelable(ORDER, order);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            isOrder = getArguments().getBoolean(IS_ORDER);
            order = getArguments().getParcelable(ORDER);
        }
        if (savedInstanceState != null) {
            isOrder = getArguments().getBoolean(IS_ORDER);
            order = savedInstanceState.getParcelable(ORDER);
        }
        for (Product product : order.getPositions()) {
            product.setCountForOrder(product.getProductsLeft());
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putBoolean(IS_ORDER, isOrder);
        outState.putParcelable(ORDER, order);
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order, container, false);

        initTextViewOrderId(view);
        initTextViewRealTotalPrice(view);
        initTextViewRealPickupTime(view);
        initRecyclerViewProducts(view);
        initImageViewOR(view);
        initButtonCloseOrder(view);

        return view;
    }


    private void initTextViewOrderId(View view) {
        TextView textViewOrderId = view.findViewById(R.id.textViewOrderId);
        String orderId = view.getResources().getString(R.string.order_id);
        String realId = order.getOrderId();
        String resultId = orderId + " " + realId;
        textViewOrderId.setText(resultId);
    }

    private void initTextViewRealTotalPrice(View view) {
        TextView textViewRealTotalPrice = view.findViewById(R.id.textViewRealTotalPrice);
        String currency = view.getResources().getString(R.string.currency);
        String result = getTotalPriceToString() + " " + currency;
        textViewRealTotalPrice.setText(result);
    }

    private String getTotalPriceToString() {
        int result = 0;
        for (Product product : order.getPositions()) {
            result += product.getCountForOrder() * product.getPrice();
        }
        return String.valueOf(result);
    }

    private void initRecyclerViewProducts(View view) {
        RecyclerView recyclerViewProducts = view.findViewById(R.id.recyclerViewProducts);
        recyclerViewProducts.getItemAnimator().setChangeDuration(0);
        ProductRecyclerViewAdapter productRecyclerViewAdapter = new ProductRecyclerViewAdapter(
                order.getPositions(), this);
        recyclerViewProducts.setAdapter(productRecyclerViewAdapter);
    }

    private void initImageViewOR(View view) {
        imageViewQR = view.findViewById(R.id.imageViewQR);
        if (isOrder) {
            imageViewQR.setVisibility(View.GONE);
        } else setImageViewQR();
    }

    private void initButtonCloseOrder(View view) {
        buttonCloseOrder = view.findViewById(R.id.buttonCloseOrder);
        if (isOrder) buttonCloseOrder.setVisibility(View.VISIBLE);
    }


    private void setImageViewQR() {
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(
                    order.getOrderId(), BarcodeFormat.QR_CODE,
                    250, 250);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            imageViewQR.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    private void initTextViewRealPickupTime(View view) {
        textViewRealPickupTime = view.findViewById(R.id.textViewRealPickupTime);
        textViewRealPickupTime.setText(new SimpleDateFormat("HH:mm", Locale.ENGLISH)
                .format(order.getPickupTime()));
    }


    @Override
    public void onItemClick(View view, int position) {

    }


    @Override
    public boolean onBackPressed() {
        return false;
    }
}
