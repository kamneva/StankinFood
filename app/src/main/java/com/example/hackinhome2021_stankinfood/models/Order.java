package com.example.hackinhome2021_stankinfood.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.example.hackinhome2021_stankinfood.activities.MainActivity;
import com.google.firebase.firestore.Exclude;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Order implements Parcelable {
    private String orderId;
    private String userId;
    private String restaurantId;
    private String name;
    private Date pickupTime;
    private boolean isDone;
    private List<Product> positions = new ArrayList<>();

    public Order() {
    }

    protected Order(Parcel in) {
        orderId = in.readString();
        userId = in.readString();
        restaurantId = in.readString();
        name = in.readString();
        isDone = in.readByte() != 0;
        positions = in.createTypedArrayList(Product.CREATOR);
    }

    public static final Creator<Order> CREATOR = new Creator<Order>() {
        @Override
        public Order createFromParcel(Parcel in) {
            return new Order(in);
        }

        @Override
        public Order[] newArray(int size) {
            return new Order[size];
        }
    };


    @Exclude
    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }


    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public Date getPickupTime() {
        return pickupTime;
    }

    public void setPickupTime(Date pickupTime) {
        this.pickupTime = pickupTime;
    }


    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }


    public List<Product> getPositions() {
        return positions;
    }

    public void setPositions(List<Product> positions) {
        this.positions = positions;
    }


    public void clearPositions() {
        positions.clear();
        Log.d("LOG_MESSAGE", positions.toString());
    }

    public void addPosition(Product productInput) {
        Product productSaved = null;
        boolean isNewProduct = true;
        for (Product productInList : positions) {
            if (productInput.getProductId().equals(productInList.getProductId())) {
                productSaved = productInList;
                isNewProduct = false;
                break;
            }
        }

        if (isNewProduct) {
            Product productNew = (Product) productInput.clone();
            productNew.setLikesCount(0);
            productNew.setLikedUserIds(null);
            productNew.setProductsLeft(productNew.getCountForOrder());
            productNew.setViewType(MainActivity.ORDER_PRODUCT_INACTIVE);
            positions.add(productNew);
        } else {
            if (productInput.getCountForOrder() != 0) {
                productSaved.setCountForOrder(productInput.getCountForOrder());
            } else removePosition(productSaved.getProductId());
        }
    }

    public void setViewTypeForPositions(boolean isActive) {
        for (Product product : positions) {
            if (isActive) {
                product.setViewType(MainActivity.ORDER_PRODUCT_ACTIVE);
            } else {
                product.setViewType(MainActivity.ORDER_PRODUCT_INACTIVE);
            }
        }
    }

    public void removePosition(String productId) {
        for (Product productInList : positions) {
            if (productId.equals(productInList.getProductId())) {
                positions.remove(productInList);
                break;
            }
        }
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(orderId);
        dest.writeString(userId);
        dest.writeString(restaurantId);
        dest.writeString(name);
        dest.writeByte((byte) (isDone ? 1 : 0));
        dest.writeTypedList(positions);
    }


    @Override
    public String toString() {
        return "\nOrder {" + "\n" +
                "\torderId = '" + orderId + '\'' + ",\n" +
                "\tname = '" + name + '\'' + ",\n" +
                "\tpickupTime = " + pickupTime + ",\n" +
                "\tisDone = " + isDone + ",\n" +
                "\tpositions = " + positions + "\n" +
                '}' + "\n";
    }
}
