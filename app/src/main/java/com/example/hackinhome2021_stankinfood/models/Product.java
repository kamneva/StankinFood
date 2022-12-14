package com.example.hackinhome2021_stankinfood.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.Exclude;

import java.util.Comparator;
import java.util.List;

public class Product implements Parcelable, Cloneable {
    private String productId;
    private String restaurantId;
    private String imageURL;
    private String categoryName;
    private String productName;
    private String description;
    private int productsLeft;
    private int countForOrder;
    private float rating;
    private int price;
    private int likesCount;
    private boolean isLiked;
    private List<String> likedUserIds;
    private int viewType;


    public Product() {
    }

    protected Product(Parcel in) {
        productId = in.readString();
        restaurantId = in.readString();
        imageURL = in.readString();
        categoryName = in.readString();
        productName = in.readString();
        description = in.readString();
        productsLeft = in.readInt();
        countForOrder = in.readInt();
        rating = in.readFloat();
        price = in.readInt();
        likesCount = in.readInt();
        isLiked = in.readByte() != 0;
        likedUserIds = in.createStringArrayList();
        viewType = in.readInt();
    }

    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException cloneNotSupportedException) {
            cloneNotSupportedException.printStackTrace();
        }
        return null;
    }

    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };


    @Exclude
    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }


    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }


    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }


    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }


    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public int getProductsLeft() {
        return productsLeft;
    }

    public void setProductsLeft(int productsLeft) {
        this.productsLeft = productsLeft;
    }


    @Exclude
    public int getCountForOrder() {
        return countForOrder;
    }

    public void setCountForOrder(int countForOrder) {
        this.countForOrder = countForOrder;
    }


    @Exclude
    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }


    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }


    public int getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
    }


    public List<String> getLikedUserIds() {
        return likedUserIds;
    }

    public void setLikedUserIds(List<String> likedUserIds) {
        this.likedUserIds = likedUserIds;
    }


    @Exclude
    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }


    @Exclude
    public int getViewType() {
        return viewType;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(productId);
        dest.writeString(restaurantId);
        dest.writeString(imageURL);
        dest.writeString(categoryName);
        dest.writeString(productName);
        dest.writeString(description);
        dest.writeInt(productsLeft);
        dest.writeInt(countForOrder);
        dest.writeFloat(rating);
        dest.writeInt(price);
        dest.writeInt(likesCount);
        dest.writeByte((byte) (isLiked ? 1 : 0));
        dest.writeStringList(likedUserIds);
        dest.writeInt(viewType);
    }


    public static final Comparator<Product> PRODUCT_COMPARATOR_WITH_CATEGORIES =
            (product_1, product_2) -> {
                if (product_1.getCategoryName().compareTo(product_2.getCategoryName()) < 0) {
                    return -1;
                } else if (product_1.getCategoryName().compareTo(product_2.getCategoryName()) == 0) {
                    if (product_1.getRating() > product_2.getRating()) {
                        return -1;
                    } else if (product_1.getRating() == product_2.getRating()) {
                        return Integer.compare(product_1.getProductName().compareTo(product_2.getProductName()), 0);
                    } else return 1;
                } else return 1;
            };

    public static final Comparator<Product> PRODUCT_COMPARATOR_WITHOUT_CATEGORIES =
            (product_1, product_2) -> {
                if (product_1.getRating() > product_2.getRating()) {
                    return -1;
                } else if (product_1.getRating() == product_2.getRating()) {
                    return Integer.compare(product_1.getProductName().compareTo(product_2.getProductName()), 0);
                } else return 1;
            };


    @Override
    public String toString() {
        return "\nProduct {" + "\n" +
                "\tproductId = '" + productId + '\'' + ",\n" +
                "\timageURL = '" + imageURL + '\'' + ",\n" +
                "\tcategoryName = '" + categoryName + '\'' + ",\n" +
                "\tproductName = '" + productName + '\'' + ",\n" +
                "\tdescription = '" + description + '\'' + ",\n" +
                "\tproductsLeft = " + productsLeft + ",\n" +
                "\tcountForOrder = " + countForOrder + ",\n" +
                "\tprice = " + price + ",\n" +
                "\tlikesCount = " + likesCount + ",\n" +
                "\tisLiked = " + isLiked + ",\n" +
                "\tviewType = " + viewType + "\n" +
                '}' + "\n";
    }
}
