package com.example.hackinhome2021_stankinfood.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hackinhome2021_stankinfood.R;
import com.example.hackinhome2021_stankinfood.activities.MainActivity;
import com.example.hackinhome2021_stankinfood.adapters.OrderRecyclerViewAdapter;
import com.example.hackinhome2021_stankinfood.interfaces.OnBackPressedFragment;
import com.example.hackinhome2021_stankinfood.interfaces.OnRecyclerViewClickListener;
import com.example.hackinhome2021_stankinfood.models.Order;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.List;

public class OrdersFragment extends Fragment implements
        SearchView.OnQueryTextListener,
        OnRecyclerViewClickListener,
        OnBackPressedFragment,
        View.OnClickListener {

    private static final String IS_MANAGER_VIEW = "isManagerView";
    private static final String ORDER_LIST = "orderList";

    private boolean isManagerView;
    private List<Order> orderList;

    private SearchView searchView;
    private RecyclerView recyclerViewMenu;
    private LinearLayoutManager linearLayoutManager;
    private OrderRecyclerViewAdapter orderRecyclerViewAdapter;

    public OrdersFragment() {
    }

    public static OrdersFragment newInstance(boolean isManagerView, List<Order> orderList) {
        OrdersFragment fragment = new OrdersFragment();
        Bundle args = new Bundle();
        args.putBoolean(IS_MANAGER_VIEW, isManagerView);
        args.putParcelableArrayList(ORDER_LIST, (ArrayList<? extends Parcelable>) orderList);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            isManagerView = getArguments().getBoolean(IS_MANAGER_VIEW);
            orderList = getArguments().getParcelableArrayList(ORDER_LIST);
        }
        if (savedInstanceState != null) {
            isManagerView = savedInstanceState.getBoolean(IS_MANAGER_VIEW);
            orderList = savedInstanceState.getParcelableArrayList(ORDER_LIST);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelableArrayList(ORDER_LIST, (ArrayList<? extends Parcelable>) orderList);
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu, container, false);

        initSearchView(view);
        linearLayoutManager = new LinearLayoutManager(view.getContext());
        initRecyclerViewMenu(view);
        initTabLayout(view);
        if (isManagerView) initButtonScanQR(view);

        return view;
    }


    private void initSearchView(View view) {
        searchView = view.findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(this);
    }

    private void initRecyclerViewMenu(View view) {
        recyclerViewMenu = view.findViewById(R.id.recyclerViewMenu);
        recyclerViewMenu.getItemAnimator().setChangeDuration(0);
        orderRecyclerViewAdapter = new OrderRecyclerViewAdapter(orderList, this);
        recyclerViewMenu.setLayoutManager(linearLayoutManager);
        recyclerViewMenu.setAdapter(orderRecyclerViewAdapter);
    }

    private void initTabLayout(View view) {
        TabLayout tabLayout = view.findViewById(R.id.tabLayout);
        tabLayout.setVisibility(View.GONE);
    }

    private void initButtonScanQR(View view) {
        Button buttonScanQR = view.findViewById(R.id.buttonScanQR);
        buttonScanQR.setOnClickListener(this);
        buttonScanQR.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        ((MainActivity) getActivity()).hideBottomNavigationView(newText.length() > 0);
        orderRecyclerViewAdapter.getFilter().filter(newText);

        return false;
    }


    @Override
    public void onItemClick(View view, int position) {
        if (view.getId() == R.id.cardView) {
            ((MainActivity) getActivity()).replaceFragmentToOrderFragment(
                    orderList.get(position));
        }
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.buttonScanQR) {
            IntentIntegrator intentIntegrator = new IntentIntegrator((MainActivity) getActivity());
            intentIntegrator.setBeepEnabled(true);
            intentIntegrator.setOrientationLocked(true);
            IntentIntegrator.forSupportFragment(OrdersFragment.this).initiateScan();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult intentResult = IntentIntegrator.parseActivityResult(
                requestCode, resultCode, data);

        if (intentResult != null) {
            if (intentResult.getContents() != null) {
                ((MainActivity) getActivity()).getOrderFromFireStore(intentResult.getContents());
            } else {
                Snackbar.make(getView(), getResources().getString(R.string.scan_error),
                        BaseTransientBottomBar.LENGTH_SHORT).show();
            }
        } else super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void onDestroyView() {
        searchView.setQuery(null, true);
        super.onDestroyView();
    }


    @Override
    public boolean onBackPressed() {
        if (searchView.getQuery().length() > 0) {
            searchView.setQuery(null, true);
            searchView.clearFocus();
            recyclerViewMenu.requestFocus();
            return true;
        } else return false;
    }
}