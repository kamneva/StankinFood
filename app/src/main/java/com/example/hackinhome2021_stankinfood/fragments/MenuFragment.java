package com.example.hackinhome2021_stankinfood.fragments;

import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hackinhome2021_stankinfood.R;
import com.example.hackinhome2021_stankinfood.activities.MainActivity;
import com.example.hackinhome2021_stankinfood.adapters.ProductRecyclerViewAdapter;
import com.example.hackinhome2021_stankinfood.interfaces.OnBackPressedFragment;
import com.example.hackinhome2021_stankinfood.interfaces.OnRecyclerViewClickListener;
import com.example.hackinhome2021_stankinfood.models.Product;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class MenuFragment extends Fragment implements
        SearchView.OnQueryTextListener,
        TabLayout.OnTabSelectedListener,
        OnRecyclerViewClickListener,
        OnBackPressedFragment {

    private static final String IS_MENU = "isMenu";
    private static final String PRODUCT_LIST = "productList";

    private boolean isMenu;
    private List<Product> productList;
    private List<Integer> titleIndexesList;
    private List<Integer> savedProductsLeft;

    private boolean isScrolled = false;

    private SearchView searchView;
    private TabLayout tabLayout;
    private RecyclerView recyclerViewMenu;
    private GridLayoutManager gridLayoutManager;
    private RecyclerView.SmoothScroller smoothScroller;
    private ProductRecyclerViewAdapter productRecyclerViewAdapter;

    public MenuFragment() {
    }

    public static MenuFragment newInstance(boolean isMenu, List<Product> productList) {
        MenuFragment fragment = new MenuFragment();
        Bundle args = new Bundle();
        args.putBoolean(IS_MENU, isMenu);
        args.putParcelableArrayList(PRODUCT_LIST, (ArrayList<? extends Parcelable>) productList);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            isMenu = getArguments().getBoolean(IS_MENU);
            productList = getArguments().getParcelableArrayList(PRODUCT_LIST);
        }
        if (savedInstanceState != null) {
            isMenu = savedInstanceState.getBoolean(IS_MENU);
            productList = savedInstanceState.getParcelableArrayList(PRODUCT_LIST);
        }
        setTitleIndexList();
        setSavedProductsLeft();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putBoolean(IS_MENU, isMenu);
        outState.putParcelableArrayList(PRODUCT_LIST, (ArrayList<? extends Parcelable>) productList);
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu, container, false);

        initSearchView(view);
        initGridLayoutManager(view);
        initSmoothScroller();
        initRecyclerViewMenu(view);
        initTabLayout(view);

        return view;
    }


    private void setTitleIndexList() {
        titleIndexesList = new ArrayList<>();

        for (int i = 0; i < productList.size(); i++) {
            if (productList.get(i).getViewType() == MainActivity.MENU_HEADER) {
                titleIndexesList.add(i);
            }
        }
        titleIndexesList.add(productList.size());
    }

    private void setSavedProductsLeft() {
        savedProductsLeft = new ArrayList<>();

        for (Product product : productList) {
            savedProductsLeft.add(product.getProductsLeft());
        }
    }

    private void initSearchView(View view) {
        searchView = view.findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(this);
    }

    private void initGridLayoutManager(View view) {
        gridLayoutManager = new GridLayoutManager(view.getContext(), 2);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (productList.get(position).getViewType() == MainActivity.MENU_HEADER) {
                    return 2;
                } else return 1;
            }
        });
    }

    private void initSmoothScroller() {
        smoothScroller = new LinearSmoothScroller(getContext()) {
            @Override
            protected int getVerticalSnapPreference() {
                return LinearSmoothScroller.SNAP_TO_START;
            }
        };
    }

    private void initRecyclerViewMenu(View view) {
        MyOnScrollListener myOnScrollListener = new MyOnScrollListener();

        recyclerViewMenu = view.findViewById(R.id.recyclerViewMenu);
        recyclerViewMenu.getItemAnimator().setChangeDuration(0);
        productRecyclerViewAdapter = new ProductRecyclerViewAdapter(productList, this);
        recyclerViewMenu.setLayoutManager(gridLayoutManager);
        recyclerViewMenu.setAdapter(productRecyclerViewAdapter);
        recyclerViewMenu.addOnScrollListener(myOnScrollListener);
    }

    private void initTabLayout(View view) {
        tabLayout = view.findViewById(R.id.tabLayout);

        if (isMenu) {
            for (Product product : productList) {
                if (product.getViewType() == MainActivity.MENU_HEADER) {
                    tabLayout.addTab(tabLayout.newTab().setText(product.getCategoryName()));
                }
            }

            tabLayout.addOnTabSelectedListener(this);
        } else tabLayout.setVisibility(View.GONE);
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (newText.length() > 0) {
            tabLayout.setVisibility(View.GONE);
            ((MainActivity) getActivity()).hideBottomNavigationView(true);
        } else {
            tabLayout.setVisibility(View.VISIBLE);
            ((MainActivity) getActivity()).hideBottomNavigationView(false);
        }
        productRecyclerViewAdapter.getFilter().filter(newText);

        return false;
    }


    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        if (!isScrolled) {
            int selectedPosition = tabLayout.getSelectedTabPosition();
            smoothScroller.setTargetPosition(titleIndexesList.get(selectedPosition));
            gridLayoutManager.startSmoothScroll(smoothScroller);
        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
    }


    private class MyOnScrollListener extends RecyclerView.OnScrollListener {
        @Override
        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            isScrolled = newState != RecyclerView.SCROLL_STATE_IDLE;
        }

        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            if (isScrolled) {
                tabLayout.removeOnTabSelectedListener(MenuFragment.this);
                int firstVisiblePosition = gridLayoutManager.findFirstVisibleItemPosition();
                int lastVisiblePosition = gridLayoutManager.findLastVisibleItemPosition();

                int index = 0;
                for (int i = 0; i < titleIndexesList.size() - 1; i++) {
                    if (firstVisiblePosition < titleIndexesList.get(i + 1) &&
                            lastVisiblePosition - 3 < titleIndexesList.get(i + 1)) {
                        index = i;
                        break;
                    } else if (firstVisiblePosition < titleIndexesList.get(i) &&
                            lastVisiblePosition - 2 > titleIndexesList.get(i)) {
                        index = i;
                        break;
                    }
                }

                tabLayout.setScrollPosition(index, 0, true);
                tabLayout.selectTab(tabLayout.getTabAt(index));
                tabLayout.addOnTabSelectedListener(MenuFragment.this);
            }
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        int id = view.getId();
        Product currentProduct = productList.get(position);

        if (id == R.id.cardView) {
//            savedCardViewClick = id;
//            savedCardViewPosition = position;
            ((MainActivity) getActivity()).replaceFragmentToProductFragment(productList, position);
        } else if (id == R.id.imageButtonLiked) {
            ((MainActivity) getActivity()).markProductAsLiked(currentProduct, !currentProduct.isLiked());
            currentProduct.setLiked(!currentProduct.isLiked());
            currentProduct.setRating(((float) currentProduct.getLikesCount()) /
                    ((float) (productList.size() - titleIndexesList.size() + 1)));

            if (!isMenu) {
                productList.remove(currentProduct);
                productRecyclerViewAdapter.notifyItemRemoved(position);
                productRecyclerViewAdapter.notifyItemRangeChanged(position, productList.size());
            } else productRecyclerViewAdapter.notifyItemChanged(position);
        } else {
            int productsLeft = savedProductsLeft.get(position);

            if (id == R.id.buttonPrice) {
                currentProduct.setCountForOrder(1);
                currentProduct.setProductsLeft(productsLeft - 1);
                currentProduct.setViewType(MainActivity.MENU_PRODUCT_ACTIVE);
            } else if (id == R.id.imageButtonMinus) {
                if (currentProduct.getCountForOrder() - 1 == 0) {
                    currentProduct.setViewType(MainActivity.MENU_PRODUCT_INACTIVE);
                } else currentProduct.setViewType(MainActivity.MENU_PRODUCT_ACTIVE);

                currentProduct.setCountForOrder(currentProduct.getCountForOrder() - 1);
                currentProduct.setProductsLeft(productsLeft - currentProduct.getCountForOrder());
            } else if (id == R.id.imageButtonPlus) {
                if (currentProduct.getCountForOrder() + 1 <= productsLeft) {
                    currentProduct.setCountForOrder(currentProduct.getCountForOrder() + 1);
                    currentProduct.setProductsLeft(productsLeft - currentProduct.getCountForOrder());
                    currentProduct.setViewType(MainActivity.MENU_PRODUCT_ACTIVE);
                } else {
                    String noProductLeft = getResources().getString(R.string.no_product_left);
                    Snackbar.make(getView(), noProductLeft, BaseTransientBottomBar.LENGTH_SHORT).show();
                }
            }
            productRecyclerViewAdapter.notifyItemChanged(position);
            MainActivity.userOrder.addPosition(currentProduct);
        }
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