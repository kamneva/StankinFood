package com.example.hackinhome2021_stankinfood.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.Exclude;

import java.util.Date;
import java.util.List;

public class Restaurant implements Parcelable {
    private String restaurantId;
    private String name;
    private String address;
    private Date openingHours;
    private Date closingHours;

    public Restaurant() {
    }

    protected Restaurant(Parcel in) {
        restaurantId = in.readString();
        name = in.readString();
        address = in.readString();
    }

    public static final Creator<Restaurant> CREATOR = new Creator<Restaurant>() {
        @Override
        public Restaurant createFromParcel(Parcel in) {
            return new Restaurant(in);
        }

        @Override
        public Restaurant[] newArray(int size) {
            return new Restaurant[size];
        }
    };


    @Exclude
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


    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }


    public Date getOpeningHours() {
        return openingHours;
    }

    public void setOpeningHours(Date openingHours) {
        this.openingHours = openingHours;
    }


    public Date getClosingHours() {
        return closingHours;
    }

    public void setClosingHours(Date closingHours) {
        this.closingHours = closingHours;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(restaurantId);
        dest.writeString(name);
        dest.writeString(address);
    }


    @Exclude
    @Override
    public String toString() {
        return "\nRestaurant {" + ",\n" +
                "\trestaurantId = '" + restaurantId + '\'' + ",\n" +
                "\tname = '" + name + '\'' + ",\n" +
                "\taddress = '" + address + '\'' + ",\n" +
                "\topeningHours = " + openingHours + ",\n" +
                "\tclosingHours = " + closingHours + ",\n" +
                '}' + "\n";
    }
}
