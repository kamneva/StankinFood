package com.example.hackinhome2021_stankinfood.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.hackinhome2021_stankinfood.R;
import com.example.hackinhome2021_stankinfood.fragments.AuthRegChooseFragment;
import com.example.hackinhome2021_stankinfood.fragments.AuthRegFragment;
import com.example.hackinhome2021_stankinfood.fragments.CartFragment;
import com.example.hackinhome2021_stankinfood.fragments.MenuFragment;
import com.example.hackinhome2021_stankinfood.fragments.OrderFragment;
import com.example.hackinhome2021_stankinfood.fragments.OrdersFragment;
import com.example.hackinhome2021_stankinfood.fragments.ProductFragment;
import com.example.hackinhome2021_stankinfood.fragments.RestaurantsFragment;
import com.example.hackinhome2021_stankinfood.interfaces.OnBackPressedFragment;
import com.example.hackinhome2021_stankinfood.models.Order;
import com.example.hackinhome2021_stankinfood.models.Product;
import com.example.hackinhome2021_stankinfood.models.Restaurant;
import com.example.hackinhome2021_stankinfood.models.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;

import org.apache.commons.net.time.TimeTCPClient;

import java.io.IOException;
import java.security.SecureRandom;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class MainActivity extends AppCompatActivity
        implements BottomNavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "LOG_MESSAGE";

    private static final String AUTH_REG_CHOOSE_FRAGMENT = "AUTH_REG_CHOOSE_FRAGMENT";
    private static final String AUTH_REG_FRAGMENT = "AUTH_REG_FRAGMENT";

    public static final int MENU_HEADER = 0;
    public static final int MENU_PRODUCT_INACTIVE = 1;
    public static final int MENU_PRODUCT_ACTIVE = 2;
    public static final int ORDER_PRODUCT_INACTIVE = 3;
    public static final int ORDER_PRODUCT_ACTIVE = 4;

    private static final String USER_DATA_KEY = "userData";
    private static final String CURRENT_USER_KEY = "currentUser";

    private static final String COLLECTION_RESTAURANTS = "restaurants";
    private static final String COLLECTION_ORDERS = "orders";
    private static final String COLLECTION_PRODUCTS = "products";
    private static final String COLLECTION_FAVORITE_ORDERS = "favoriteOrders";
    private static final String COLLECTION_USERS = "users";

    private String currentWeekday;
    private Date currentDate = null;
    public static Order userOrder = new Order();

    private View parentLayout;
    private ProgressBar progressBar;
    private int currentDirection = 0;
    private int previousDirection = 0;
    private int previousBottomNavigationTabId;

    private BottomNavigationView bottomNavigationView;

    private CurrentTimeGetterThread currentTimeGetterThread = null;

    private User userData;
    private FirebaseUser currentUser = null;
    private static final int RC_SIGN_IN = 9001;
    private GoogleSignInClient googleSignInClient;

    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        parentLayout = findViewById(android.R.id.content);
        progressBar = findViewById(R.id.progressBar);
        initBottomNavigationView();
        previousBottomNavigationTabId = R.id.menuItemRestaurants;

        currentUser = firebaseAuth.getCurrentUser();
        if (savedInstanceState != null) {
            currentUser = savedInstanceState.getParcelable(CURRENT_USER_KEY);
            userData = savedInstanceState.getParcelable(USER_DATA_KEY);
        }

        if (currentUser == null) {
            hideBottomNavigationView(true);
            replaceFragmentToAuthRegChooseFragment();
        } else {
            findUserInDatabase();

//            currentTimeGetterThread = new CurrentTimeGetterThread();
//            currentTimeGetterThread.start();
        }

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
    }


    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelable(CURRENT_USER_KEY, currentUser);
        outState.putParcelable(USER_DATA_KEY, userData);
        super.onSaveInstanceState(outState);
    }

    private void initBottomNavigationView() {
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
    }

    private class CurrentTimeGetterThread extends Thread {
        @Override
        public void run() {
            TimeTCPClient client = new TimeTCPClient();

            while (true) {
                try {
                    client.connect("time.nist.gov");
                    client.setKeepAlive(false);

                    currentDate = client.getDate();
                    client.disconnect();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                    try {
                        Thread.sleep(250);
                    } catch (InterruptedException interruptedException) {
                        interruptedException.printStackTrace();
                    }
                }

                if (currentDate != null) {
                    DateFormat weekdayString = new SimpleDateFormat("EEEE", Locale.ENGLISH);
                    currentWeekday = weekdayString.format(currentDate);
//                    findUserInDatabase();
                    break;
                }
            }
        }
    }


    private void hideProgressBar(boolean hide) {
        if (hide) {
            progressBar.setVisibility(View.GONE);
        } else progressBar.setVisibility(View.VISIBLE);
    }

    public void createUserWithEmailAndPassword(String email, String password) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "createUserWithEmailAndPassword(): Successful!");
                        hideKeyboard(this);

                        currentUser = firebaseAuth.getCurrentUser();
                        currentUser.sendEmailVerification();

                        Fragment fragment = getSupportFragmentManager().findFragmentByTag(AUTH_REG_FRAGMENT);
                        ((AuthRegFragment) fragment).showAlertDialogVerificationMessage(email);
                    } else {
                        Log.d(TAG, "createUserWithEmailAndPassword(): Failed!");
                    }
                });
    }

    public void authUserWithEmailAndPassword(String email, String password) {
        hideKeyboard(this);

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "authUserWithEmailAndPassword(): Successful!");
                        currentUser = firebaseAuth.getCurrentUser();
                        if (!currentUser.isEmailVerified()) {
                            Fragment fragment = getSupportFragmentManager().findFragmentByTag(AUTH_REG_FRAGMENT);
                            ((AuthRegFragment) fragment).showSnackBarEmailNotVerified();
                        } else findUserInDatabase();
                    } else {
                        Log.d(TAG, "authUserWithEmailAndPassword(): Failed!");
                    }
                });
    }


    public void signInWithGoogle() {
        Intent signInWithGoogle = googleSignInClient.getSignInIntent();
        startActivityForResult(signInWithGoogle, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "onActivityResult: " + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException apiException) {
                apiException.printStackTrace();
            }
        }
    }

    public void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "signInWithCredential: Successful!");
                        currentUser = firebaseAuth.getCurrentUser();
                        findUserInDatabase();
                    } else {
                        Log.w(TAG, "signInWithCredential: Failed!", task.getException());
                    }
                });
    }


    private void findUserInDatabase() {
        User user = new User();
        user.setUserId(currentUser.getUid());
        user.setRestaurantId(null);

        firebaseFirestore.collection(COLLECTION_USERS).whereEqualTo(
                "userId", user.getUserId()).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "findUserInDatabase(): Task Successful!");
                        for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                            userData = queryDocumentSnapshot.toObject(User.class);
                        }
                        if (currentTimeGetterThread == null) {
                            currentTimeGetterThread = new CurrentTimeGetterThread();
                            currentTimeGetterThread.start();
                        }
                        if (task.getResult().isEmpty()) {
                            createUserInDatabase(user);
                        }
                        setBottomNavigationViewToZeroPosition();
                        getRestaurantsFromFireStore();
                        userOrder.setUserId(currentUser.getUid());
                    } else {
                        Log.d(TAG, "findUserInDatabase(): Task Failed!");
                    }
                });
    }

    private void createUserInDatabase(User user) {
        firebaseFirestore.collection(COLLECTION_USERS).document().set(user)
                .addOnCompleteListener(taskInner -> {
                    if (taskInner.isSuccessful()) {
                        Log.d(TAG, "createUserInDatabase(): Task Successful!");
                    } else {
                        Log.d(TAG, "createUserInDatabase(): Task Failed!");
                    }
                });
    }

    public void sendResetPasswordByEmail(String email) {
        firebaseAuth.sendPasswordResetEmail(email)
                .addOnSuccessListener(aVoid -> {
                    Fragment fragment = getSupportFragmentManager().findFragmentByTag(AUTH_REG_FRAGMENT);
                    ((AuthRegFragment) fragment).showSnackBarResetPassword(email);
                })
                .addOnFailureListener(e -> Log.d(TAG, "sendResetPasswordByEmail(): Failed!"));
    }


//    private void animationFragmentManager(FragmentTransaction fragmentTransaction) {
//        Log.d(TAG, "currentDirection: " + currentDirection + "; previousDirection: " + previousDirection);
//        if (currentDirection < previousDirection) {
//            fragmentTransaction.setCustomAnimations(
//                    R.anim.enter_from_left, R.anim.exit_to_right,
//                    R.anim.enter_from_right, R.anim.exit_to_left);
//        } else {
//            fragmentTransaction.setCustomAnimations(
//                    R.anim.enter_from_right, R.anim.exit_to_left,
//                    R.anim.enter_from_left, R.anim.exit_to_right);
//        }
//    }


    public void replaceFragmentToAuthRegChooseFragment() {
        hideProgressBar(true);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        animationFragmentManager(fragmentTransaction);
        fragmentTransaction.replace(R.id.mainContainer,
                new AuthRegChooseFragment(), AUTH_REG_CHOOSE_FRAGMENT);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public void replaceFragmentToAuthRegFragment(boolean isRegistration) {
        hideProgressBar(true);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        animationFragmentManager(fragmentTransaction);
        fragmentTransaction.replace(R.id.mainContainer,
                AuthRegFragment.newInstance(isRegistration), AUTH_REG_FRAGMENT);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void replaceRestaurantsToFragment(List<Restaurant> restaurantList) {
        hideProgressBar(true);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.popBackStack();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        animationFragmentManager(fragmentTransaction);
        fragmentTransaction.replace(R.id.mainContainer,
                RestaurantsFragment.newInstance(restaurantList));
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public void replaceFragmentToProductFragment(List<Product> productList, int position) {
        hideProgressBar(true);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        animationFragmentManager(fragmentTransaction);
        fragmentTransaction.replace(R.id.mainContainer,
                ProductFragment.newInstance(productList.get(position)));
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void replaceFragmentToMenuFragment(boolean isMenu,
                                               boolean withCategoriesSort,
                                               List<Product> productList) {
        hideProgressBar(true);
        List<Product> result = getConvertedProductListForRecyclerView(
                productList, isMenu, withCategoriesSort);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        animationFragmentManager(fragmentTransaction);
        fragmentTransaction.replace(R.id.mainContainer,
                MenuFragment.newInstance(isMenu, result));
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void replaceFragmentToCardFragment() {
        hideProgressBar(true);
        userOrder.setViewTypeForPositions(true);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        animationFragmentManager(fragmentTransaction);
        fragmentTransaction.replace(R.id.mainContainer, CartFragment.newInstance(userOrder));
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public void replaceFragmentToOrderFragment(Order order) {
        hideProgressBar(true);
        boolean isManagerView = userData.getRestaurantId() != null;
        order.setViewTypeForPositions(isManagerView);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        animationFragmentManager(fragmentTransaction);
        fragmentTransaction.replace(R.id.mainContainer, OrderFragment.newInstance(
                isManagerView, order));
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void replaceFragmentToOrdersFragment(List<Order> orderList) {
        hideProgressBar(true);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        animationFragmentManager(fragmentTransaction);
        fragmentTransaction.replace(R.id.mainContainer, OrdersFragment.newInstance(
                userData.getRestaurantId() != null, orderList));
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager)
                activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) view = new View(activity);

        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void hideBottomNavigationView(boolean hide) {
        if (hide) {
            bottomNavigationView.setVisibility(View.GONE);
        } else bottomNavigationView.setVisibility(View.VISIBLE);
    }


    private void setBottomNavigationViewToZeroPosition() {
        currentDirection = 0;
        previousBottomNavigationTabId = R.id.menuItemRestaurants;
        bottomNavigationView.setOnNavigationItemSelectedListener(null);
        bottomNavigationView.setSelectedItemId(R.id.menuItemRestaurants);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
    }


    public void getRestaurantsFromFireStore() {
        hideProgressBar(false);
        firebaseFirestore.collection(COLLECTION_RESTAURANTS).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "getRestraintsFromFireStore(): Successful!");
                        List<Restaurant> restaurantList = new ArrayList<>();
                        for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                            Restaurant restaurant = queryDocumentSnapshot.toObject(Restaurant.class);
                            restaurant.setRestaurantId(queryDocumentSnapshot.getId());
                            restaurantList.add(restaurant);
                        }
                        setBottomNavigationViewToZeroPosition();
                        replaceRestaurantsToFragment(restaurantList);
                    } else {
                        Log.d(TAG, "getRestraintsFromFireStore(): Failed!");
                    }
                });
    }

    private List<Product> getConvertedProductListForRecyclerView(List<Product> productList,
                                                                 boolean isMenu,
                                                                 boolean withCategoriesSort) {
        for (Product product : productList) {
            product.setRating(((float) product.getLikesCount()) / ((float) productList.size()));
            product.setViewType(MainActivity.MENU_PRODUCT_INACTIVE);
        }

        if (withCategoriesSort) {
            Collections.sort(productList, Product.PRODUCT_COMPARATOR_WITH_CATEGORIES);
        } else Collections.sort(productList, Product.PRODUCT_COMPARATOR_WITHOUT_CATEGORIES);
        if (isMenu) convertForRecyclerView(productList);

        return productList;
    }

    private void convertForRecyclerView(List<Product> productList) {
        List<String> categoryNamesList = new ArrayList<>();

        for (Product product : productList) {
            String savedCategoryName = product.getCategoryName();
            if (!categoryNamesList.contains(savedCategoryName)) {
                categoryNamesList.add(savedCategoryName);
            }
        }

        if (categoryNamesList.size() > 0) {
            int index = 0;
            String savedCategoryName = categoryNamesList.get(0);
            Product firstHeader = new Product();
            firstHeader.setCategoryName(savedCategoryName);
            firstHeader.setViewType(MainActivity.MENU_HEADER);
            productList.add(0, firstHeader);
            index++;

            for (int i = 1; i < productList.size(); i++) {
                if (!productList.get(i).getCategoryName().equals(savedCategoryName)) {
                    savedCategoryName = categoryNamesList.get(index);
                    Product categoryName = new Product();
                    categoryName.setCategoryName(savedCategoryName);
                    categoryName.setViewType(MainActivity.MENU_HEADER);
                    productList.add(i, categoryName);
                    index++;
                }
            }
        }
    }

    public void getFavoriteProductsFromFireStore() {
        hideProgressBar(false);
        firebaseFirestore.collection(COLLECTION_PRODUCTS)
                .whereArrayContains("likedUserIds", currentUser.getUid())
                .whereGreaterThanOrEqualTo("productsLeft", 1).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "getFavoriteProductsFromFireStore(): Success!");
                        List<Product> productList = new ArrayList<>();
                        for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                            Product product = queryDocumentSnapshot.toObject(Product.class);
                            product.setProductId(queryDocumentSnapshot.getId());
                            product.setLiked(true);
                            productList.add(product);
                        }
                        replaceFragmentToMenuFragment(false, false, productList);
                    } else {
                        Log.d(TAG, "getFavoriteProductsFromFireStore(): Failed!");
                    }
                });
    }

    public void getMenuFromFireStore(Restaurant restaurant) {
        hideProgressBar(false);
        userOrder.clearPositions();

        firebaseFirestore.collection(COLLECTION_PRODUCTS)
                .whereEqualTo("restaurantId", restaurant.getRestaurantId())
                .whereGreaterThanOrEqualTo("productsLeft", 1).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "getProductsFromFireStore(): Success!");
                        List<Product> productList = new ArrayList<>();
                        for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                            Product product = queryDocumentSnapshot.toObject(Product.class);
                            product.setProductId(queryDocumentSnapshot.getId());
                            if (product.getLikedUserIds() != null && product.getLikedUserIds().size() != 0) {
                                if (product.getLikedUserIds().contains(currentUser.getUid())) {
                                    product.setLikedUserIds(Collections.singletonList(currentUser.getUid()));
                                    product.setLiked(true);
                                } else {
                                    product.setLikedUserIds(null);
                                    product.setLiked(false);
                                }
                            }
                            productList.add(product);
                        }
                        replaceFragmentToMenuFragment(true, true, productList);
                    } else {
                        Log.d(TAG, "getProductsFromFireStore(): Failed!");
                    }
                });
    }

    public void addOrderToFireStore(Order order) {
        for (Product product : order.getPositions()) {
            product.setProductsLeft(product.getCountForOrder());
        }

        firebaseFirestore.collection(COLLECTION_ORDERS).add(order)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "addOrderToFireStore(): Successful!");
                        order.clearPositions();
                        setBottomNavigationViewToZeroPosition();
                        getRestaurantsFromFireStore();
                    } else Log.d(TAG, "addOrderToFireStore(): Failure!");
                });
    }

    public void getRestaurantOrdersFromFireStore() {
        hideProgressBar(false);
        firebaseFirestore.collection(COLLECTION_ORDERS)
                .whereEqualTo("restaurantId", userData.getRestaurantId()).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "getUserOrdersFromFireStore(): Successful!");
                        List<Order> restaurantOrders = new ArrayList<>();
                        for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                            Order order = queryDocumentSnapshot.toObject(Order.class);
                            order.setOrderId(queryDocumentSnapshot.getId());
                            restaurantOrders.add(order);
                        }
                        replaceFragmentToOrdersFragment(restaurantOrders);
                    } else {
                        Log.d(TAG, "getUserOrdersFromFireStore(): Failure!");
                    }
                });
    }

    public void getUserOrdersFromFireStore() {
        hideProgressBar(false);
        firebaseFirestore.collection(COLLECTION_ORDERS)
                .whereEqualTo("userId", currentUser.getUid()).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "getUserOrdersFromFireStore(): Successful!");
                        List<Order> userOrders = new ArrayList<>();
                        for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                            Order order = queryDocumentSnapshot.toObject(Order.class);
                            order.setOrderId(queryDocumentSnapshot.getId());
                            userOrders.add(order);
                        }
                        replaceFragmentToOrdersFragment(userOrders);
                    } else {
                        Log.d(TAG, "getUserOrdersFromFireStore(): Failure!");
                    }
                });
    }

    public void getOrderFromFireStore(String orderId) {
        hideProgressBar(false);
        firebaseFirestore.collection(COLLECTION_ORDERS).document(orderId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "getOrderFromFireStore(): Successful!");
                        Order order = task.getResult().toObject(Order.class);
                        order.setOrderId(task.getResult().getId());
                        replaceFragmentToOrderFragment(order);
                    } else {
                        Log.d(TAG, "getOrderFromFireStore(): Failed!");
                    }
                });
    }


    public void markProductAsLiked(Product product, boolean isLiked) {
        firebaseFirestore.collection(COLLECTION_PRODUCTS).document(product.getProductId())
                .update("likedUserIds", isLiked ?
                        FieldValue.arrayUnion(currentUser.getUid()) :
                        FieldValue.arrayRemove(currentUser.getUid()))
                .addOnCompleteListener(task -> {
                    String message = isLiked ? getResources().getString(R.string.like_add) :
                            getResources().getString(R.string.like_remove);
                    Snackbar.make(parentLayout, message,
                            BaseTransientBottomBar.LENGTH_SHORT).show();
                });
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id != previousBottomNavigationTabId) {
            previousDirection = currentDirection;
            if (id == R.id.menuItemRestaurants) {
                currentDirection = 0;
                getRestaurantsFromFireStore();
            } else if (id == R.id.menuItemOrders) {
                currentDirection = 1;
                if (userData.getRestaurantId() == null) {
                    getUserOrdersFromFireStore();
                } else getRestaurantOrdersFromFireStore();
            } else if (id == R.id.menuItemFavoriteProducts) {
                currentDirection = 2;
                getFavoriteProductsFromFireStore();
            } else if (id == R.id.menuItemCart) {
                currentDirection = 3;
                replaceFragmentToCardFragment();
            }
            previousBottomNavigationTabId = id;
        }

        return true;
    }


    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.mainContainer);
        if (!(fragment instanceof OnBackPressedFragment) ||
                !((OnBackPressedFragment) fragment).onBackPressed()) {
            if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
                finish();
            } else super.onBackPressed();
        }
    }
}