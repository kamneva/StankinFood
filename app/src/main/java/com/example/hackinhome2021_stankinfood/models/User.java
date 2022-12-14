package com.example.hackinhome2021_stankinfood.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.Exclude;

import java.util.List;

public class User implements Parcelable {
    private String userId;
    private String restaurantId;
    private List<Order> orderList;

    public User() {
    }

    protected User(Parcel in) {
        userId = in.readString();
        restaurantId = in.readString();
        orderList = in.createTypedArrayList(Order.CREATOR);
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };


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


    @Exclude
    public List<Order> getOrderList() {
        return orderList;
    }

    public void setOrderList(List<Order> orderList) {
        this.orderList = orderList;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userId);
        dest.writeString(restaurantId);
        dest.writeTypedList(orderList);
    }


    @Exclude
    @Override
    public String toString() {
        return "\nUser {" + "\n" +
                "\tuserId = '" + userId + '\'' + ",\n" +
                "\trestaurantId = '" + restaurantId + '\'' + "\n" +
                '}' + "\n";
    }
}
